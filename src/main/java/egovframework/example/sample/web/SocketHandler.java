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
import egovframework.rte.psl.dataaccess.util.EgovMap;
 
public class SocketHandler extends TextWebSocketHandler implements InitializingBean {
	
	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;
	
	public static int gameidIdx = -1;
	
	public static SocketHandler sk=null; 
	public static Object insertLog(int gameid,String gkind,int useridx, int value1, int value2, String value3, int value4,int value5){
		Object rt=null;
		EgovMap in=new EgovMap();
		in.put("gameid", gameid);
		in.put("gkind", gkind);
		in.put("useridx", useridx);
		in.put("value1", value1);
		in.put("value2", value2);
		in.put("value3", value3);
		in.put("value4", value4);
		in.put("value5", value5);
		rt = SocketHandler.sk.sampleDAO.insert("insertLog",in);
		return rt;
		
	}
	
	public static int GameIdxAdder(){
		gameidIdx++;
		return gameidIdx;
	}
	
    private final Logger logger = LogManager.getLogger(getClass());
    private Set<WebSocketSession> sessionSet = new HashSet<WebSocketSession>();

    UserManager usermanager = new UserManager();
    RoomManager roommanager = new RoomManager();
    
    public SocketHandler GetHandl(){return sk;}
    public SocketHandler() {
        super();
        this.logger.info("create SocketHandler instance!");
        sk =this;
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
//        this.logger.info("receive message:" + message.toString());
        
//        System.out.println("handleMessage test");
        String msg = ""+message.getPayload();
        JSONParser p = new JSONParser();
        JSONObject obj = (JSONObject)p.parse(msg);
        
        //System.out.println(obj.toJSONString());
        switch(""+ obj.get("protocol"))
		{
			case "leave":
    		{
    			User u = usermanager.find(session);
    			int ss = u.seat;
    			int roomidx = Integer.parseInt(""+obj.get("roomidx"));
    			roommanager.leaveRoom(roomidx, u);
    			roommanager.find(roomidx).notifyLeaveUser(ss);
    		}
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
             	Room r = roommanager.find(roomidx);
             	r.gameManager.setRoomEndTime( SocketHandler.second );//새로 한명 들어올때마다 대기 시간을 증가시켜서 여러명이 들어올 여지를 둔다.
        		break;
        	}
        	case "bet":
        	{             	
        		User u1 = usermanager.find(session);
        		System.out.println("nm bet "+u1.seat);
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
             	roommanager.find(roomidx).gameManager.bet(roomidx , u1, betkind);
        		break;
        	}
        	case "sbBet":
        	{             	
        		User u2 = usermanager.find(session);
        		System.out.println("sb bet "+u2.seat);
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
             	roommanager.find(roomidx).gameManager.bet(roomidx , u2, betkind);
        		break;
        	}
        	case "bbBet":
        	{             	
        		User u3 = usermanager.find(session);
        		System.out.println("bb bet "+u3.seat);
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
             	roommanager.find(roomidx).gameManager.bet(roomidx , u3, betkind);
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
    	gameidIdx = 0;
    	EgovMap gameId = (EgovMap) sampleDAO.select("selectLastGameId");
    	if(gameId!=null)
    		gameidIdx = Integer.parseInt(""+gameId.get("gameid"));
    	
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
