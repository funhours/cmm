package com.mlongbo.jfinal.person;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.model.UserRole;
import com.mlongbo.jfinal.vo.AjaxResult;

public class ApiPersonController extends BaseController {
	private final AjaxResult result = new AjaxResult();
	static User dao = new User().dao();
	static UserRole userRoleDao = new UserRole().dao();
	public void index() {
	    String userId = getPara("userId");
		//分页参数
		Page<User> page = dao.paginate(getParaToInt("p", 1), 10,"select * ","from t_user where 1=1 and userType <> 1 and parentUserId = '"+userId+"' and userId <> '"+userId+"' order by creationDate");
		renderJson(page);
	}
	
	/**
	 * 
	 * @Description (供应商管理)
	 */
	public void supplier(){
        //分页参数
        Page<User> page = dao.paginate(getParaToInt("p", 1), 10,"select * ","from t_user where 1=1 and userType = 2 order by creationDate");
        setAttr("page", page);
        render("supplier.html");
	}
	
	/**
	 * 方法描述：跳转编辑页面
	 * @author: FunHours
	 */
	public void toEdit(){
		int editType = getParaToInt("editType");
		if(editType == 1){//1：编辑 0：新增
			String userId = getLoginUserId();
			Record user = Db.findFirst("select * from t_user where userId = '" + userId+"'"  );
			setAttr("user", user);
			render("personEdit.html");
			return;
		}
		
		render("personAdd.html");
	}
	
	/**
	 * 方法描述：保存修改
	 */
	public void savePersonEdit(){
		boolean saveOk = false;
		String loginUserId = getPara("userId");
		User user = getModel(User.class);
		//修改
		String userId = user.get("userId");
		user.set("parentUserId", loginUserId);
		user.set(User.CREATION_DATE, DateUtils.getNowTimeStamp());
		if(!StringUtils.isEmpty(userId)){
			UserRole userRole = userRoleDao.findFirst("select * from t_userroles where userID='"+userId+"'");
            userRole.set(UserRole.ROLE_ID, user.get(User.USERTYPE));
            userRole.update();
			if(user.update()){
				saveOk = true;
				result.success("信息修改成功");
			}else{
				result.addError("信息修改失败，请联系管理员");
			};
			
			result.setData(saveOk);
		}else{
			// 新增
			userId = RandomUtils.randomCustomUUID();
			user.set(User.USER_ID, userId);
			String password =  user.get(User.PASSWORD);
			String hashedPass = HashKit.sha256("kyddzs" + password);
			user.setPassword(hashedPass);
			user.set(User.LOGIN_NAME, user.get("telephone"));
			user.set("parentUserId", loginUserId);
			user.save();
			
			
			saveOk = new UserRole()
            .set(UserRole.USER_ID, userId)
            .set(UserRole.ROLE_ID, user.get(User.USERTYPE))
            .save();
			
			if(saveOk){
				result.success("信息保存成功");
			}else{
				result.addError("信息保存失败，请联系管理员");
			};
			result.setData(saveOk);
		}
		renderJson(result);
	}
	
	//启用
	public void review(){
		String _reviewList = getPara("reviewList");
		String reviewList[] = _reviewList.split(",");
		List<User> recordList = new ArrayList<User>();
		for (int i = 0; i < reviewList.length; i++) {
			User user = dao.findById(reviewList[i]); 
			user.set(User.ACTIVITY, 1);
			recordList.add(user);
		}
		int[] resultCount = Db.batchUpdate(recordList, reviewList.length);
		result.setData(resultCount);
		renderJson(result);
	}
	//禁用
	public void disable(){
		String _reviewList = getPara("reviewList");
		String reviewList[] = _reviewList.split(",");
		List<User> recordList = new ArrayList<User>();
		for (int i = 0; i < reviewList.length; i++) {
			User user = dao.findById(reviewList[i]); 
			user.set(User.ACTIVITY, 0);
			recordList.add(user);
		}
		int[] resultCount = Db.batchUpdate(recordList, reviewList.length);
		result.setData(resultCount);
		renderJson(result);
	}
	
	// 单个删除
	public void delById(){
		String userId = getLoginUserId();
		boolean isDelete = dao.deleteById(userId);
		
		renderJson(isDelete);
	}
	
	/**
	 * 多个删除
	 */
	public void delByIds(){
		
		String _delsList = getPara("delsList");
		String delsList[] = _delsList.split(",");
		List<User> recordList = new ArrayList<User>();
		for (int i = 0; i < delsList.length; i++) {
			User user = dao.findById(delsList[i]); 
			user.set(User.ACTIVITY, 0);
			recordList.add(user);
		}
		String sql = "delete from t_user where 1=1 and userId = ?";
		int[] resultCount = Db.batch(sql, "userId", recordList, recordList.size());
		result.setData(resultCount);
		renderJson(result);
	}
	
	/**
	 * 搜索
	 */
	public void search(){
		
		String searchRole = getPara("searchRole");
		String searchName = getPara("searchName");
		if(searchRole.equals("0")&&StringUtils.isNotEmpty(searchName)){//用户名不为空的所有
			Page<User> page = dao.paginate(getParaToInt("p", 1), 10,"select * ","from t_user where 1=1 and nickName like '%"+searchName+"%' order by creationDate");
			setAttr("page", page);
		}else if(StringUtils.isEmpty(searchName)){//用户名为空的所有
			Page<User> page = dao.paginate(getParaToInt("p", 1), 10,"select * ","from t_user where 1=1 order by creationDate");
			setAttr("page", page);
		}else if(StringUtils.isEmpty(searchName)){//用户名为空的指定身份
			Page<User> page = dao.paginate(getParaToInt("p", 1), 10,"select * ","from t_user where 1=1 and userType = '"+searchRole+"' order by creationDate");
			setAttr("page", page);
		}else{//用户名不为空的指定身份
			Page<User> page = dao.paginate(getParaToInt("p", 1), 10,"select * ","from t_user where 1=1 and userType = '"+searchRole+"' and nickName like '%"+searchName+"%' order by creationDate");
			setAttr("page", page);
		}
		render("index.html");
		
	}
	
    /**
     * 
     * @Description (员工数量)
     */
    public void employeeCount (){
        String userId = getPara("userId");
        String sql = "select count(*) from t_user where 1=1 and userType = '3' and parentUserId = '"+userId+"'";
        String listSql = "select * from t_user where 1=1 and userType = '3' and parentUserId = '"+userId+"'";
        List<User> users = dao.find(listSql);
        Long employeeCount = Db.queryLong(sql);
        
        JSONObject employee = new JSONObject();
        employee.put("users", users);
        employee.put("employeeCount", employeeCount);
        
        result.setData(employee);
        
        renderJson(result);
    }
    
    /**
     * 
     * @Description (代理商数量)
     */
    public void agentCount(){
        String userId = getPara("userId");
        String sql = "select count(*) from t_user where 1=1 and userType = '4' and parentUserId = '"+userId+"'";
        String listSql = "select * from t_user where 1=1 and userType = '4' and parentUserId = '"+userId+"'";
        List<User> users = dao.find(listSql);
        Long agentCount = Db.queryLong(sql);
        
        JSONObject agent = new JSONObject();
        agent.put("users", users);
        agent.put("agentCount", agentCount);
        
        result.setData(agent);
        
        renderJson(result);
    }
	
	
}
