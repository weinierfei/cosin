/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chatopera.cc.webim.util.server.handler;

import com.chatopera.cc.core.UKDataContext;
import com.chatopera.cc.util.UKTools;
import com.chatopera.cc.util.client.NettyClients;
import com.chatopera.cc.webim.service.acd.ServiceQuene;
import com.chatopera.cc.webim.service.cache.CacheHelper;
import com.chatopera.cc.webim.service.impl.AgentUserService;
import com.chatopera.cc.webim.service.repository.AgentServiceRepository;
import com.chatopera.cc.webim.service.repository.ConsultInviteRepository;
import com.chatopera.cc.webim.util.MessageUtils;
import com.chatopera.cc.webim.util.OnlineUserUtils;
import com.chatopera.cc.webim.util.server.message.AgentStatusMessage;
import com.chatopera.cc.webim.util.server.message.ChatMessage;
import com.chatopera.cc.webim.util.server.message.NewRequestMessage;
import com.chatopera.cc.webim.web.model.*;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

public class CalloutEventHandler
{
	protected SocketIOServer server;
	private final Logger logger = LoggerFactory.getLogger(CalloutEventHandler.class);


    @Autowired
    public CalloutEventHandler(SocketIOServer server)
    {  
        this.server = server ;
    }  


    @OnConnect
    public void onConnect(SocketIOClient client)  
    {
        String user = client.getHandshakeData().getSingleUrlParam("userid") ;
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
        String session = client.getHandshakeData().getSingleUrlParam("session") ;
        String admin = client.getHandshakeData().getSingleUrlParam("admin") ;
        logger.info("onConnect userid {}, orgi {}.", user, orgi);

        if(!StringUtils.isBlank(user) && !StringUtils.isBlank(user)){
            client.set("agentno", user);
            InetSocketAddress address = (InetSocketAddress) client.getRemoteAddress()  ;
            String ip = UKTools.getIpAddr(client.getHandshakeData().getHttpHeaders(), address.getHostString()) ;

            NettyClients.getInstance().putCalloutEventClient(user, client);
        }
    }  
      
    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息  
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client)  
    {
        String user = client.getHandshakeData().getSingleUrlParam("userid") ;
        String orgi = client.getHandshakeData().getSingleUrlParam("orgi") ;
        String session = client.getHandshakeData().getSingleUrlParam("session") ;
        String admin = client.getHandshakeData().getSingleUrlParam("admin") ;
		logger.info("onDisconnect userid {}, orgi {}", user, orgi);
        NettyClients.getInstance().removeCalloutEventClient(user, UKTools.getContextID(client.getSessionId().toString()));

    }
}