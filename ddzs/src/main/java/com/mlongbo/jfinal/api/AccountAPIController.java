package com.mlongbo.jfinal.api;

import static com.mlongbo.jfinal.model.User.AVATAR;
import static com.mlongbo.jfinal.model.User.EMAIL;
import static com.mlongbo.jfinal.model.User.NICK_NAME;
import static com.mlongbo.jfinal.model.User.PASSWORD;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;
import com.mlongbo.jfinal.common.Require;
import com.mlongbo.jfinal.common.bean.BaseResponse;
import com.mlongbo.jfinal.common.bean.Code;
import com.mlongbo.jfinal.common.bean.Constant;
import com.mlongbo.jfinal.common.bean.DatumResponse;
import com.mlongbo.jfinal.common.bean.LoginResponse;
import com.mlongbo.jfinal.common.token.TokenManager;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.utils.SMSUtils;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.config.AppProperty;
import com.mlongbo.jfinal.interceptor.TokenInterceptor;
import com.mlongbo.jfinal.model.InitUseCount;
import com.mlongbo.jfinal.model.OrderStore;
import com.mlongbo.jfinal.model.RegisterCode;
import com.mlongbo.jfinal.model.SmsStore;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.vo.AjaxResult;

/**
 * 用户账号相关的接口*
 *
 * 检查账号是否被注册: GET /api/account/checkUser
 * 发送注册验证码: POST /api/account/sendCode
 * 注册: POST /api/account/register
 * 登录： POST /api/account/login
 * 查询用户资料: GET /api/account/profile
 * 修改用户资料: PUT /api/account/profile
 * 修改密码: PUT /api/account/password
 * 修改头像: PUT /api/account/avatar
 *
 * @author malongbo
 */
@Before(TokenInterceptor.class)
public class AccountAPIController extends BaseAPIController {
	private static Logger log = Logger.getLogger(AccountAPIController.class);
	private final AjaxResult result = new AjaxResult();
    /**
     * 检查用户账号是否被注册*
     */
    @Clear
    public void checkUser() {
        String loginName = getPara("loginName");
        if (StringUtils.isEmpty(loginName)) {
            renderArgumentError("loginName can not be null");
            return;
        }
        //检查手机号码是否被注册
        boolean exists = Db.findFirst("SELECT * FROM t_user WHERE loginName=?", loginName) != null;
        renderJson(new BaseResponse(exists ? Code.SUCCESS:Code.FAIL, exists ? "registered" : "unregistered"));
    }
    
    /**
     * 1. 检查是否被注册*
     * 2. 发送短信验证码*
     */
    @Clear
    public void sendCode() {
        String loginName = getPara("loginName");
        if (StringUtils.isEmpty(loginName)) {
            renderArgumentError("loginName can not be null");
            return;
        }

        //检查手机号码有效性
        if (!SMSUtils.isMobileNo(loginName)) {
            renderArgumentError("mobile number is invalid");
            return;
        }

        //检查手机号码是否被注册
        if (Db.findFirst("SELECT * FROM t_user WHERE loginName=?", loginName) != null) {
            renderJson(new BaseResponse(Code.ACCOUNT_EXISTS,"mobile already registered"));
            return;
        }

        String smsCode = SMSUtils.randomSMSCode(6);
        //发送短信验证码
        if (!SMSUtils.sendCode(new String[] {loginName}, smsCode)) {
            renderFailed("sms send failed");
            return;
        }
        
        //保存验证码数据
        RegisterCode registerCode = new RegisterCode()
                .set(RegisterCode.MOBILE, loginName)
                .set(RegisterCode.CODE, smsCode);

        //保存数据
        if (Db.findFirst("SELECT * FROM t_register_code WHERE mobile=?", loginName) == null) {
            registerCode.save();
        } else {
            registerCode.update();
        }
        
        renderJson(new BaseResponse("sms sended"));
        
    }
    
	/**
	 * 用户注册
	 */
    @Clear
	public void register(){
		//必填信息
		String loginName = getPara("loginName").trim();//登录帐号
        int code = getParaToInt("code", 0);//手机验证码
        int userType = getParaToInt("userType", 0);//用户类型
        String password = getPara("password");//密码
		String nickName = getPara("nickName");//昵称
		int userStatus = 0; //帐号状态. 1表示开启 ，0表示禁用
    	//头像信息，为空则使用默认头像地址
    	String avatar = getPara("avatar", AppProperty.me().defaultUserAvatar());

        //校验必填项参数
		if(!notNull(Require.me()
                .put(loginName, "loginName can not be null")
                .put(code, "code can not be null")//根据业务需求决定是否使用此字段
                .put(password, "password can not be null")
                .put(nickName, "nickName can not be null"))){
			return;
		}

        //检查账户是否已被注册
        if (Db.findFirst("SELECT * FROM t_user WHERE loginName=?", loginName) != null) {
            renderJson(new BaseResponse(Code.ACCOUNT_EXISTS, "mobile already registered"));
            return;
        }
        
        //检查验证码是否有效
        if (Db.findFirst("SELECT * FROM t_register_code WHERE mobile=? AND code = ?", loginName, code) == null) {
            renderJson(new BaseResponse(Code.CODE_ERROR,"code is invalid"));
            return;
        }
        
		//保存用户数据
		String userId = RandomUtils.randomCustomUUID();
		String saltPassword = HashKit.sha256("kyddzs" + password);
		
		
		InitUseCount initUseCount = InitUseCount.dao.findById(1);
		
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, + initUseCount.getInt("useDay"));//今天的时间加初始使用时间
        date = calendar.getTime();

		new User().set("userId", userId)
                .set(User.LOGIN_NAME, loginName)
		        .set(User.PASSWORD, saltPassword)
                .set(User.NICK_NAME, nickName)
		        .set(User.CREATION_DATE, DateUtils.getNowTimeStamp())
		        .set(User.USERTYPE, userType)
                .set(User.AVATAR, avatar)
                .set(User.STATUS, userStatus)
                .set(User.ACTIVITY, userStatus)
                .set("parentUserId", userId)
                .set("expiryDate", date)
                .save();
		
		//分配免费订单量,短信量
		if(userType == 2){
		    int smsCount = initUseCount.getInt("smsCount");
		    int orderCount = initUseCount.getInt("orderCount");
		    
		    new OrderStore()
		            .set("id", RandomUtils.randomCustomUUID())
		            .set("userId", userId)
		            .set("remaining", orderCount)
		            .set("warningValue",10)
		            .save();
		    
		    new SmsStore()
		    .set("id", RandomUtils.randomCustomUUID())
		    .set("userId", userId)
		    .set("remaining", smsCount)
		    .set("warningValue",10)
		    .save();
		}
		
        //删除验证码记录
        Db.update("DELETE FROM t_register_code WHERE mobile=? AND code = ?", loginName, code);
        
		//返回数据
		renderJson(new BaseResponse("success"));
	}
	
	
    /**
     * 登录接口
     */
    @Clear
    public void login() {
        String loginName = getPara("loginName");
        String password = getPara("password");
        //校验参数, 确保不能为空
        if (!notNull(Require.me()
                .put(loginName, "loginName can not be null")
                .put(password, "password can not be null")
        )) {
            return;
        }
        
        String sql = "SELECT * FROM t_user WHERE loginName=? AND password=?";
        User nowUser = User.dao.findFirst(sql, loginName, StringUtils.encodePassword(password, "md5"));
        LoginResponse response = new LoginResponse();
        if (nowUser == null) {
            response.setCode(Code.FAIL).setMessage("userName or password is error");
            renderJson(response);
            return;
        }
        Map<String, Object> userInfo = new HashMap<String, Object>(nowUser.getAttrs());
        userInfo.remove(PASSWORD);
        response.setInfo(userInfo);
        response.setMessage("login success");
        response.setToken(TokenManager.getMe().generateToken(nowUser));
        response.setConstant(Constant.me());
        renderJson(response);
    }

    /**
     * 资料相关的接口
     */
    public void profile() {
        String method = getRequest().getMethod();
        if ("get".equalsIgnoreCase(method)) { //查询资料
            getProfile();
        } else if ("put".equalsIgnoreCase(method)) { //修改资料
            updateProfile();
        } else {
            render404();
        }
    }


    /**
     * 查询用户资料
     */
    private void getProfile() {
        String userId = getPara("userId");
        User resultUser = null;
        if (StringUtils.isNotEmpty(userId)) {
            resultUser = User.dao.findById(userId);
        } else {
            resultUser = getUser();
        }

        DatumResponse response = new DatumResponse();
        
        if (resultUser == null) {
            response.setCode(Code.FAIL).setMessage("user is not found");
        } else {
            HashMap<String, Object> map = new HashMap<String, Object>(resultUser.getAttrs());
            map.remove(PASSWORD);
            response.setDatum(map);
        }

        renderJson(response);
    }

    /**
     * 修改用户资料
     */
    private void updateProfile() {
        boolean flag = false;
        BaseResponse response = new BaseResponse();
        User user = getUser();
        String nickName = getPara("nickName");
        if (StringUtils.isNotEmpty(nickName)) {
            user.set(NICK_NAME, nickName);
            flag = true;
        }

        String email = getPara("email");
        if (StringUtils.isNotEmpty(email)) {
            user.set(EMAIL, email);
            flag = true;
        }
        
        String avatar = getPara("avatar");
        if (StringUtils.isNotEmpty(avatar)) {
            user.set(AVATAR, avatar);
            flag = true;
        }

        //修改用户类型
        if (flag) {
            boolean update = user.update();
            renderJson(response.setCode(update ? Code.SUCCESS : Code.FAIL).setMessage(update ? "update success" : "update failed"));
        } else {
            renderArgumentError("must set profile");
        }
    }

    /**
     * 修改密码
     */
    public void password(){
        if (!"put".equalsIgnoreCase(getRequest().getMethod())) {
            render404();
            return;
        }
    	//根据用户id，查出这个用户的密码，再跟传递的旧密码对比，一样就更新，否则提示旧密码错误
    	String oldPwd = getPara("oldPwd");
    	String newPwd = getPara("newPwd");
    	if(!notNull(Require.me()
    			.put(oldPwd, "old password can not be null")
    			.put(newPwd, "new password can not be null"))){
    		return;
    	}
    	//用户真实的密码
        User nowUser = getUser();
    	if(StringUtils.encodePassword(oldPwd, "md5").equalsIgnoreCase(nowUser.getStr(PASSWORD))){
    		boolean flag = nowUser.set(User.PASSWORD, StringUtils.encodePassword(newPwd, "md5")).update();
            renderJson(new BaseResponse(flag?Code.SUCCESS:Code.FAIL, flag?"success":"failed"));
    	}else{
            renderJson(new BaseResponse(Code.FAIL, "oldPwd is invalid"));
    	}
    }
    
    /**
     * 修改头像接口
     * /api/account/avatar
     */
    public void avatar() {
        if (!"put".equalsIgnoreCase(getRequest().getMethod())) {
            renderJson(new BaseResponse(Code.NOT_FOUND));
            return;
        }
    	String avatar=getPara("avatar");
    	if(!notNull(Require.me()
    			.put(avatar, "avatar url can not be null"))){
    		return;
    	}
    	getUser().set(User.AVATAR, avatar).update();
    	renderSuccess("success");
    }
    

    
    
}

