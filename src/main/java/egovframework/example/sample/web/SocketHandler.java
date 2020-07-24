package egovframework.example.sample.web;
 
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import egovframework.example.sample.service.impl.SampleDAO;
 
public class SocketHandler extends TextWebSocketHandler implements InitializingBean {
	
	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;
	
    private final Logger logger = LogManager.getLogger(getClass());
    private Set<WebSocketSession> sessionSet = new HashSet<WebSocketSession>();

    UserManager usermanager = new UserManager();
    RoomManager roommanager = new RoomManager();
    
    public SocketHandler() {
        super();
        this.logger.info("create SocketHandler instance!");
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	super.afterConnectionClosed(session, status);
        sessionSet.remove(session);
        this.logger.info("remove session!");
    }
 
    @Override
    public void afterConnectionEstablished(WebSocketSession session)throws Exception {
    	super.afterConnectionEstablished(session);
        sessionSet.add(session);
        
        this.logger.info("add session! id: " + session.getId());
    }
 
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    
    	super.handleMessage(session, message);
        this.logger.info("receive message:" + message.toString());
        
        System.out.println("handleMessage test");
        String msg = ""+message.getPayload();
        JSONParser p = new JSONParser();
        JSONObject obj = (JSONObject)p.parse(msg);
        
        System.out.println(obj.toJSONString());
        switch(""+ obj.get("protocol"))
		{
        	case "connect":
        	{
        		session.getAttributes().put("useridx", obj.get("useridx"));
        		User user = new User(Integer.parseInt(""+obj.get("useridx")), session);
        		usermanager.connect(session, user);
        		break;
        	}
        	case "joinRoom":
        	{
             	User u = usermanager.find(session);
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));
             	roommanager.joinRoom(roomidx , u);
        		break;
        	}
        	case "bet":
        	{             	
        		User u1 = usermanager.find(session);
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
             	roommanager.find(roomidx).gameManager.bet(roomidx , u1, betkind);
        		break;
        	}
		}
    }
 
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        this.logger.error("web socket error!", exception);
    }
 
    @Override
    public boolean supportsPartialMessages() {
        this.logger.info("call method!");
        return super.supportsPartialMessages();
    }
 
    public static int second = 0;   
    @Override
    public void afterPropertiesSet() throws Exception {
    	
    	
        Thread thread = new Thread() {
            int i = 0;
 
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        second++;                        
                        roommanager.checkStartGame();
                        roommanager.checkTimerGame();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };
        thread.start();
    }
    
    public synchronized void sendMessage(String message) {
    	 
        for (WebSocketSession session : this.sessionSet) {
 
            if (session.isOpen()) {
 
                try {
 
                    session.sendMessage(new TextMessage(message));
 
                } catch (Exception ignored) {
 
                    this.logger.error("fail to send message!", ignored);
 
                }
 
            }
 
        }
 
    }
    public synchronized void sendMessage(WebSocketSession session,String message) {
         if (session.isOpen()) {
              try {
                  session.sendMessage(new TextMessage(message));
               } catch (Exception ignored) {
                  this.logger.error("fail to send message!", ignored);
               }
          }
    }
}
