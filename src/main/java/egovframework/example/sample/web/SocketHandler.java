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
	
	@Resource(name = "sampleDAO") SampleDAO sampleDAO;
	
	public static int gameidIdx = -1;
    private final Logger logger = LogManager.getLogger(getClass());
    private Set<WebSocketSession> sessionSet = new HashSet<WebSocketSession>();

    UserManager usermanager = new UserManager();
    RoomManager roommanager = new RoomManager();

    
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
	
    
    public SocketHandler GetHandl(){return sk;}
    public SocketHandler() {
        super();
        this.logger.info("create SocketHandler instance!");
        sk =this;
    }
    
    void disconnect(WebSocketSession session){
    	User u = usermanager.find(session);
    	if( u == null)
    		return;
    	
    	System.out.println("접속끊김 :"+u.seat );    	    
    	
    	if( u.roomnum != -1){
    		Room r = roommanager.find(u.roomnum);
    		//roommanager.find(u.roomnum).notifyLeaveUser(u.seat);
    		r.leave(u);    		
    		if( r.emptyRoom() ){
    			r.init();
    		}
    	}
    	usermanager.userlist.remove(u);
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	super.afterConnectionClosed(session, status);
    	disconnect(session);
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
        		EgovMap in = new EgovMap();
        		in.put("muserid", obj.get("userid"));
        		in.put("muserpw", obj.get("userpw"));
    			System.out.println("login id:"+obj.get("userid")+" pw:"+obj.get("userpw"));
        		EgovMap ed = (EgovMap)sampleDAO.select("Login", in);
        		if(ed==null){
        			System.out.println("로그인실패");
        			break;
        		}
        		session.getAttributes().put("useridx", ed.get("midx"));
        		User user = new User(Integer.parseInt(""+ed.get("midx")), session, ""+ed.get("muserid"));
        		usermanager.connect(session, user);
        		break;
        	}
        	case "joinRoom":
        	{
             	User u = usermanager.find(session);
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));
             	roommanager.joinRoom(roomidx , u);
             	Room r = roommanager.find(roomidx);
             	r.gameManager.setWorkTime( );//새로 한명 들어올때마다 대기 시간을 증가시켜서 여러명이 들어올 여지를 둔다.
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
        	case "buy":{        		
        		//User user = usermanager.find(session);
        		usermanager.buy(session, ""+obj.get("product"), ""+obj.get("recipt"));
	        }break;
        	case "deal":
        	{        		
        		usermanager.Deal(session, Integer.parseInt(""+obj.get("itemtype")), Integer.parseInt(""+obj.get("actiontype")), Integer.parseInt(""+obj.get("amount")));
        	}        	
	        break;
        	case "beg":
        	{        		
        		System.out.println(obj.toJSONString());
        		usermanager.Beg(session);
        	}        	
	        break;
		}
    }
 
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    	System.out.println("접속에러" );
    	disconnect(session);
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
                        Thread.sleep(500);
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
    //=======================
}
