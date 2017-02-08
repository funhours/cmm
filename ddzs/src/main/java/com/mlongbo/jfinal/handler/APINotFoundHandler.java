package com.mlongbo.jfinal.handler;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.mlongbo.jfinal.common.bean.BaseResponse;
import com.mlongbo.jfinal.common.bean.Code;
import com.mlongbo.jfinal.render.MyRenderFactory;

/**
 * 处理404接口*
 * @author malongbo
 * @date 15-1-18
 * @package com.pet.project
 */
public class APINotFoundHandler extends Handler {
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (!target.startsWith("/api")) {
        	this.next.handle(target, request, response, isHandled);
            return;
        }
        
        if (JFinal.me().getAction(target, new String[1]) == null) {
            isHandled[0] = true;
            try {
                request.setCharacterEncoding("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            
            new MyRenderFactory().getJsonRender(new BaseResponse(Code.NOT_FOUND, "resource is not found")).setContext(request, response).render();
            
            
        } else {
            this.next.handle(target, request, response, isHandled);
        }
    }
}
