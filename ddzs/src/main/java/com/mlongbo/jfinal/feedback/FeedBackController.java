package com.mlongbo.jfinal.feedback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mlongbo.jfinal.model.FeedBack;
import com.mlongbo.jfinal.model.FeedBackReply;
import com.mlongbo.jfinal.model.Product;
import com.mlongbo.jfinal.vo.AjaxResult;

public class FeedBackController extends Controller {
	static FeedBack dao = new FeedBack().dao();
	static FeedBackReply fbrDao = new FeedBackReply().dao();
	private final AjaxResult result = new AjaxResult();
	
	public void index() {
	    try {
	        //分页参数
	        Page<FeedBack> page = dao.paginate(getParaToInt("p", 1), 10,"select f.*,(select content from feed_back_reply where feedbackId = f.id) as reply ","from feed_back f where 1=1 order by f.creatDate");
	        setAttr("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

		render("index.html");
	}
	
	/**
	 * 
	 * @Description (回复)
	 */
	public void reply(){
	    boolean isReply = false;
	    String committer = getPara("userId");
	    String feedbackId = getPara("feedbackId");
	    String replyContent = getPara("replyContent");
	    String sql = "select * from feed_back_reply where 1=1 and feedbackId = ?";
	    FeedBackReply feedBackReply = fbrDao.findFirst(sql,feedbackId);
	    if(feedBackReply != null){
	        isReply = feedBackReply.set("content", replyContent).update();
	    }else{
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	        isReply = new FeedBackReply()
	                .set("content", replyContent)
	                .set("feedbackId", feedbackId)
	                .set("createDate",df.format(new Date()))
	                .set("committer", committer)
	                .save();
	    }
	    result.setData(isReply);
	    renderJson(result);
	}
	
	 // 单个删除
	@Before(Tx.class)
    public void delById(){
        String feedbackId = getPara("feedbackId");
        String sql = "select * from feed_back_reply where 1=1 and feedbackId = ?";
        fbrDao.findFirst(sql,feedbackId).delete();
        boolean isDelete = dao.deleteById(feedbackId);
        
        renderJson(isDelete);
    }
    
    /**
     * 多个删除
     */
	@Before(Tx.class)
    public void delByIds(){
        try {
            
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        List<FeedBack> feedBackList = new ArrayList<FeedBack>();
        List<FeedBackReply> feedBackReplyList = new ArrayList<FeedBackReply>();
        String fbrFindsql = "select * from feed_back_reply where 1=1 and feedbackId = ?";
        for (int i = 0; i < delsList.length; i++) {
            FeedBack feedBack = dao.findById(delsList[i]); 
            feedBackList.add(feedBack);
            FeedBackReply feedBackReply = fbrDao.findFirst(fbrFindsql,delsList[i]);
            feedBackReplyList.add(feedBackReply);
        }
        
        String sql = "delete from feed_back where 1=1 and id = ?";
        String fbrDelsql = "delete from feed_back_reply where 1=1 and id = ?";
        int[] resultCount = Db.batch(sql, "id", feedBackList, feedBackList.size());
        Db.batch(fbrDelsql, "id", feedBackReplyList, feedBackReplyList.size());
        
        result.setData(resultCount);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        renderJson(result);
    }
	
	
}
