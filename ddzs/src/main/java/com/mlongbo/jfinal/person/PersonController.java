package com.mlongbo.jfinal.person;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.vo.AjaxResult;

public class PersonController extends Controller {
	private final AjaxResult result = new AjaxResult();
	static User dao = new User().dao();
	public void index() {
		//分页参数
		/*int pageNumber = getParaToInt("pageNum", 1);
		int pageSize = getParaToInt("pageSize", 10);
		setAttr("pageNumber", pageNumber);
		setAttr("pageSize", pageSize);*/
		
	
//		Page<Record> page = Db.paginate(pageNumber, pageSize, "select * ","from t_user where 1=1 order by creationDate");
		Page<User> page = dao.paginate(getParaToInt("p", 1), 10,"select * ","from t_user where 1=1 order by creationDate");
		setAttr("page", page);
		render("index.html");
	}
	
	/**
	 * 方法描述：跳转编辑页面
	 * @author: FunHours
	 */
	public void toEdit(){
		int editType = getParaToInt("editType");
		if(editType == 1){//1：编辑 0：新增
			int uId = getParaToInt("uId");
			Record person = Db.findFirst("select * from t_user where userId = " + uId  );
			setAttr("person", person);
		}
		
		render("personEdit.html");
	}
	
	/**
	 * 
	 */
	public void savePersonEdit(){
		boolean saveOk = false;
		User user = getModel(User.class);
		
		//修改
		String userId = user.get("userId");
		user.set(User.CREATION_DATE, DateUtils.getNowTimeStamp());
		if(!StringUtils.isEmpty(userId)){
			if(user.update()){
				saveOk = true;
				result.success("信息修改成功");
			}else{
				result.addError("信息修改失败，请联系管理员");
			};
			
			result.setData(saveOk);
		}else{
			// 新增
			user.set(User.LOGIN_NAME, user.get("telephone"));
			if(user.save()){
				saveOk = true;
				result.success("信息保存成功");
			}else{
				result.addError("信息保存失败，请联系管理员");
			};
			
			result.setData(saveOk);
		}
			
		renderJson(result);
	}
	
	
}
