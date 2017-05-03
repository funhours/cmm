package com.mlongbo.jfinal.controller;


import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocketController {

    
    @OnOpen
    public void onOpen(Session session) {
        
    }

    @OnClose
    public void onClose(Session session) {
        
    }

    @OnMessage
    public void onMessage(String requestJson, Session session) throws IOException {
        session.getBasicRemote().sendText(requestJson);
    }
}