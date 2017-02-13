package com.mlongbo.jfinal.person;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class PersonController extends Controller {
	
	public void index() {
		//分页参数
		int pageNumber = getParaToInt("pageNum", 1);
		int pageSize = getParaToInt("pageSize", 10);
		setAttr("pageNumber", pageNumber);
		setAttr("pageSize", pageSize);
	
		Page<Record> page = Db.paginate(pageNumber, pageSize, "select * ","from t_user where 1=1 order by creationDate");
		setAttr("page", page);
		render("index.html");
	}
	
	public void toEdit(){
		int editType = getParaToInt("editType");
		if(editType == 1){//1：编辑 0：新增
			int pId = getParaToInt("pId");
			Record votePerson = Db.findFirst("select a.*,b.ImgName from voteperson a,image b where a.personId = " + pId + " and a.headImgId = b.id" );
			setAttr("vPerson", votePerson);
		}
		
		render("personEdit.html");
	}
}
