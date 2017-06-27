package com.mlongbo.jfinal.accountBook;

import com.jfinal.plugin.activerecord.Page;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.AccountBook;
import com.mlongbo.jfinal.vo.AjaxResult;


public class ApiAccountBookController extends BaseController {
    private final AjaxResult result = new AjaxResult();
    static AccountBook dao = new AccountBook().dao();
    public void index(){
        String userId = getPara("userId");
      //分页参数
        Page<AccountBook> abPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from account_book where 1=1 and userId = '"+userId+"' order by creationDate");
        renderJson(abPage);
    }
    
    public void toEdit(){
        int editType = getParaToInt("editType");
        if(editType == 1){//1：编辑 0：新增
            String abId = getPara("abId");
            AccountBook aBook = dao.findFirst("select * from account_book where id = '" + abId+"'"  );
            setAttr("aBook", aBook);
            render("edit.html");
            return;
        }
        
        render("add.html");
    }
    
    /**
     * 方法描述：保存修改
     */
    public void saveABookEdit(){
        try {
            boolean saveOk = false;
            int editType = getParaToInt("editType");
            AccountBook aBook = getModel(AccountBook.class);
            String userId = getPara("userId");
            //获取要保存的账本日期
            String inputDate = getPara("inputDate");
            //修改
            aBook.set("creationDate", DateUtils.getNowTimeStamp());
            if(editType == 1){
                String[] _date = inputDate.split("-");
                saveOk = aBook.set("userId", userId)
                .set("year", _date[0])
                .set("month", _date[1])
                .set("day", _date[2])
                .set("creationDate", DateUtils.getNowTimeStamp())
                .update();
            }else{
               // 新增
                String[] _date = inputDate.split("-");
                saveOk = aBook.set("userId", userId)
                .set("year", _date[0])
                .set("month", _date[1])
                .set("day", _date[2])
                .set("creationDate", DateUtils.getNowTimeStamp())
                .save();
                
                if(saveOk){
                    result.success("信息保存成功");
                }else{
                    result.addError("信息保存失败，请联系管理员");
                };
                result.setData(saveOk);
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
        renderJson(result);
    }
    
 // 单个删除
    public void delById(){
        String abId = getPara("abId");
        boolean isDelete = dao.deleteById(abId);
        
        renderJson(isDelete);
    }
    
    /**
     * 多个删除
     */
    public void delByIds(){
        
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        int resultCount = 0;
        int failCount = 0;
        for (int i = 0; i < delsList.length; i++) {
            try {
                dao.deleteById(delsList[i]);
                resultCount++;
            } catch (Exception e) {
                e.printStackTrace();
                failCount++;
            }
        }
        result.setData(resultCount);
        result.setData(failCount);
        renderJson(result);
    }
}
