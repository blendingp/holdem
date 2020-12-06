package egovframework.example.sample.web;
 
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    ChatMention chatmention = new ChatMention();
    
    static public int jokbotest = -1;
    
	public static SocketHandler sk=null; 
	public static Object insertLog(int gameid,String gkind,int useridx, long value1, long value2, String value3, long value4,long value5){
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
		
		int seat = u.seat;		
		System.out.println("접속끊김 :"+seat );    	    		
    	
    	if( u.roomnum != -1){			
			Room room = roommanager.find(u.roomnum);					
			//room.leave(u);			
			//room.notifyLeaveUser(seat);
			//System.out.println(seat);
			
			room.checkSBorBBfirstOut(u);

			room.LeaveReserve(u);

			if( room.gameManager.userlist.size() <= 0 )
			{
				roommanager.roomList.remove(room);
			}
			else
			{				
				Task.IncreaseTask(u, 2, 1);
				Task.UpdateDB(u);				
			}			
		}
		
		for( int nCount = 0; nCount < usermanager.userlist.size(); ++nCount)
		{
			if( usermanager.userlist.get(nCount).uidx == u.uidx )
			{
				usermanager.userlist.remove(usermanager.userlist.get(nCount));
				break;
			}			
		}    	
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
        
        System.out.println(obj.toJSONString());
        switch(""+ obj.get("protocol"))
		{
        	case "requestprofile"://클라이언트가 자신의 프로필 정보를 요청한 것에 대한 응답.
        	{
        		
        	}break;
			case "leave":
    		{    			    			
    			User u = usermanager.find(session);    			
				int roomidx = Integer.parseInt(""+obj.get("roomidx"));
				System.out.println("leave roomnum :"+u.roomnum);
    			roommanager.leaveRoom(u.roomnum, u);    	    			
    			//AI일경우 자동 재충전 처리
    			if(u.isAI == true)
    			{
    				u.point += 20000000;
    				// insertLog 머니로그 남겨야 함 !!!
    				JSONObject chgobj=new JSONObject();
    				chgobj.put("cmd", "aiautocharge");
    				chgobj.put("currentpoint",""+u.point);
    				chgobj.put("currentbalance",""+u.balance);
    				u.sendMe(chgobj);
    			}
    			System.out.println("leave dbg 5");
    		}break;
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
				if( usermanager.findFromUseridx((int)ed.get("midx")) != null)				
				{
					System.out.println("중복 로그인");
					break;
				}

        		session.getAttributes().put("useridx", ed.get("midx"));
        		User user = new User(Integer.parseInt(""+ed.get("midx")), session);
        		
        		usermanager.connect(session, user);
        		break;
        	}
        	case "joinRoom":
        	{
             	User u = usermanager.find(session);
             	//int roomidx = Integer.parseInt(""+obj.get("roomidx"));
             	roommanager.joinRoom(u, ""+obj.get("roomkey"));             	
        		break;
			}
			case "createroom":
			{				
				User u = usermanager.find(session);
				ObjectMapper mapper = new ObjectMapper();								
				CreateRoom roominfo = mapper.readValue(obj.toJSONString(), CreateRoom.class);				
				roommanager.CreateRoom(u, roominfo);
			}break;
			case "joingoldroom":
			{
				User u = usermanager.find(session);
				roommanager.JoinRoomByNumber(u, Integer.parseInt(""+obj.get("number")));
			}break;
			case "goldquickjoin":
			{
				User u = usermanager.find(session);
				roommanager.GoldRoomQuickJoin(u);
			}break;
			case "getgoldroomlist":
			{
				roommanager.GetRoomList(session);
			}break;
        	case "bet":
        	{             	
        		User u1 = usermanager.find(session);
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
             	roommanager.find(roomidx).gameManager.bet(u1, betkind);
        		break;
        	}
        	case "sbBet":
        	{             	
        		User u2 = usermanager.find(session);
        		System.out.println("sb bet "+u2.seat);
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
             	roommanager.find(roomidx).gameManager.bet(u2, betkind);
        		break;
        	}
        	case "bbBet":
        	{             	
        		User u3 = usermanager.find(session);
        		System.out.println("bb bet "+u3.seat);
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
             	roommanager.find(roomidx).gameManager.bet(u3, betkind);
        		break;
        	}
        	case "buy":{        		
        		//User user = usermanager.find(session);
        		usermanager.buy(session, ""+obj.get("product"), ""+obj.get("recipt"));
	        }break;
        	case "deal":
        	{        		
        		usermanager.Deal(session, Integer.parseInt(""+obj.get("itemtype")), Integer.parseInt(""+obj.get("actiontype")), Long.parseLong(""+obj.get("amount")));
        	}        	
	        break;
        	case "beg":
        	{        		
        		System.out.println(obj.toJSONString());
        		usermanager.Beg(session);
        	}        	
	        break;	        
        	case "JokboTestPacket":
        	{        		
        		System.out.println("JokboTestPacket:"+ obj.toJSONString());
        		jokbotest = Integer.parseInt(""+obj.get("jokbokind"));
        		System.out.println("jokbotest:"+jokbotest);
        	}        	
	        break;	        	        
        	case "emoticon":
        	{
        		User user = usermanager.find(session);        		        		
				int roomidx = user.roomnum;
				int type = Integer.parseInt(""+obj.get("type"));
        		roommanager.find(roomidx).gameManager.EmoticonBroadCast(user, type);
			}break;			
			case "searchuser":
			{
				usermanager.SearchUserByID(session, ""+obj.get("userid"));
			}break;
	        case "requestfriend":
			{
				usermanager.RequestFriend(session, ""+obj.get("userid"));
			}break;
			case "requestfriendlist":
			{
				usermanager.RequestFriendList(session);
			}break;
			case "acceptfriend":
			{
				usermanager.AcceptFriend(session, ""+obj.get("uid"));
			}break;
			case "friendlist":
			{
				usermanager.GetFriendList(session);
			}break;
			case "sendfriendgold":
			{
				usermanager.SendFriendGold(session, ""+obj.get("uid"));
			}break;
			case "deletefriend":
			{
				usermanager.DeleteFriend(session, ""+obj.get("uid"));
			}break;
			case "checkattandance":
			{
				usermanager.CheckAttendance(session);
			}break;
			case "getinbox":
			{				
				usermanager.GetInbox(session, Integer.parseInt(""+obj.get("type")));
			}break;
			case "getinboxreward":
			{				
				usermanager.GetInBoxReward(session, ""+obj.get("uid"));
			}break;
			case "userinfo":
			{
				usermanager.GetUserInfo(session);
			}break;
			case "gettaskreward":
			{
				User user = usermanager.find(session);
				Task.PayReward(""+obj.get("uid"), user);
			}break;
			case "setavata":
			{
				usermanager.SetUserAvata(session, ""+obj.get("avata"));				
			}break;
			case "checkauth":
			{
				usermanager.GetUserAuth(session);
			}break;
			case "signup":
			{
				if( (""+obj.get("social")).isEmpty() == true )
				{
					usermanager.SignUp(session, ""+obj.get("id"), ""+obj.get("pass"));
				}				

			}break;		
			case "existgoogle":
			{
				if( (""+obj.get("social")).isEmpty() == false )
				{
					usermanager.GoogleLogin(session, ""+obj.get("social"));
				}
			}break;
			case "googlesign":
			{
				usermanager.SignUp(session, ""+obj.get("social"));
			}break;
			case "setnickname":
			{
				User user = usermanager.SetNickName(session, ""+obj.get("nickname"));
				if( user != null )
				{
					usermanager.connect(session, user);
				}				
			}break;
			case "getroominfo":
			{
				User user = usermanager.find(session);        		        		
				int roomidx = user.roomnum;
				roommanager.find(roomidx).GetRoomInfo(session);
			}break;
		}
    }
 
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    	//System.out.println("접속에러\n" + exception.getMessage());
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
    	
    	chatmention.setDAO(sampleDAO);
		chatmention.uploding();
		JackpotManager.Init();
    	
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
						JackpotManager.Update();
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
