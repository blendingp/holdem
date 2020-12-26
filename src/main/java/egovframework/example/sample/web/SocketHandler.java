package egovframework.example.sample.web;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.example.sample.web.model.UserMsg;
import egovframework.rte.psl.dataaccess.util.EgovMap;
 
public class SocketHandler extends TextWebSocketHandler implements InitializingBean {
	
	@Resource(name = "sampleDAO") SampleDAO sampleDAO;
	
	public static int gameidIdx = -1;
	public static String gameIdentifier = "";
    private final Logger logger = LogManager.getLogger(getClass());
    private Set<WebSocketSession> sessionSet = new HashSet<WebSocketSession>();
	public LinkedList<UserMsg> msglist = new LinkedList<UserMsg>();	
	public LinkedList<WebSocketSession> disconnectlist = new LinkedList<WebSocketSession>();	


    UserManager usermanager = new UserManager();
    RoomManager roommanager = new RoomManager();
    ChatMention chatmention = new ChatMention();
    
    static public int jokbotest = -1;
    
	public static SocketHandler sk=null; 
	public static Object insertLog(int gameid, String gameIdentifier, String gkind,int useridx, long value1, long value2, String value3, long value4,long value5){
		Object rt=null;
		EgovMap in=new EgovMap();
		in.put("gameid", gameid);
		in.put("gameIdentifier", gameIdentifier);
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
	
	public static String getGameIdentifier() throws NoSuchAlgorithmException{				
		//return BytesToHex( Sha256(""+gameidIdx) );
		return (""+(new Date()).getTime())+gameidIdx;
	}
    
    public SocketHandler GetHandl(){return sk;}
    public SocketHandler() {
        super();
        this.logger.info("create SocketHandler instance!");
        sk =this;
    }
    
    void disconnect()
    {
        WebSocketSession session=null;
        synchronized(disconnectlist) 
        {
        	if( disconnectlist.size() > 0 )
        	{
            	session = disconnectlist.pop();
        	}
        }
        
        if(session == null)
        {
        	return;
        }
        
    	User u = usermanager.find(session);
    	if( u == null)
    		return;
		
		int idx = u.uidx;		
		System.out.println("접속끊김 발생  uidx:"+u.uidx);    	    		

		for( int nCount = 0; nCount < usermanager.userlist.size(); ++nCount)
		{			
			if( usermanager.userlist.get(nCount).uidx == idx )
			{
				usermanager.userlist.remove(usermanager.userlist.get(nCount));
				break;
			}			
		}    	
    	
    	if( u.roomnum != -1){
			Room room = roommanager.find(u.roomnum);					
			System.out.println("LeaveReserve 호출전");
			room.LeaveReserve(u);

			if( room.gameManager.userlist.size() > 0 )
			{
				Task.IncreaseTask(u, 2, 1);
				Task.UpdateDB(u);			
			}
		}		
    	try {
    		session.close();
    	}catch(Exception e) {
    		System.out.println("접속끊김 에러처리");
    	}
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	super.afterConnectionClosed(session, status);
    	synchronized(disconnectlist) {
    		if( disconnectlist.contains(session) != true) {
    			disconnectlist.add(session);
    		}
    	}
        sessionSet.remove(session);
        
        this.logger.info("remove session!");
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    	//System.out.println("접속에러\n" + exception.getMessage());
    	synchronized(disconnectlist) {
    		if( disconnectlist.contains(session) != true) {
    			disconnectlist.add(session);
    		}
    	}
        this.logger.error("web socket error!", exception);
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
        synchronized(msglist) {
            msglist.add(new UserMsg(session,msg));
        }
    }
 
    public void cmdProcess() throws Exception {
    
    	//================================================={ msg
    	UserMsg um = null;
        synchronized(msglist) {
        	if( msglist.size()>0)
        	{
        		um = msglist.pop();
        	}
        }
        if( um == null)
        	return;
        WebSocketSession session=um.session;
        String msg=um.msg;
        //=================================================} msg
        
        JSONParser p = new JSONParser();
        JSONObject obj = (JSONObject)p.parse(msg);
        System.out.println("cmd:"+msg);
        User ltu = usermanager.find(session);  
        if(ltu != null) 
        {
        	ltu.lastcmdtime = (new Date()).getTime();
        }
        
        switch(""+ obj.get("protocol"))
		{
        	case "requestprofile"://클라이언트가 자신의 프로필 정보를 요청한 것에 대한 응답.
        	{
        		
        	}break;
			case "leave":
    		{
    			try {
	    			User u = usermanager.find(session);  
	    			if(u == null) break;
					int roomidx = Integer.parseInt(""+obj.get("roomidx"));
					if( roomidx < 0) break;
	    			roommanager.leaveRoom(u.roomnum, u);    	    				    			
    			}catch(Exception e) {
    				System.out.println("나가는중 에러 감지 :"+e.getMessage() );
    			}
    		}break;
        	case "connect":
        	{        	
        		EgovMap in = new EgovMap();
        		in.put("muserid", obj.get("userid"));
        		in.put("muserpw", obj.get("userpw"));
				//System.out.println("login id:"+obj.get("userid")+" pw:"+obj.get("userpw"));
				
				JSONObject cobj = new JSONObject();
				cobj.put("cmd", "loginfail");				

				if( session == null )
				{
					return ;
				}

				if( session.isOpen() == false )
				{
					return ;
				}				

        		EgovMap ed = (EgovMap)sampleDAO.select("Login", in);
        		if(ed==null){					
					//System.out.println("로그인실패");
					cobj.put("code", 1);

					ObjectMapper mapper = new ObjectMapper();

					try {
						session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
					} catch (IOException e) {
						e.printStackTrace();
					}
        			break;
				}
				if( usermanager.findFromUseridx((int)ed.get("midx")) != null)				
				{
					//System.out.println("중복 로그인");
					
					/*
					User tu =usermanager.findFromUseridx((int)ed.get("midx"));
			    	synchronized(disconnectlist) {
			    		if( disconnectlist.contains(tu.session) != true) {
			    			disconnectlist.add(tu.session);
			    		}
			    	}
			    	this.disconnect();*/
					
					try {
						User tu =usermanager.findFromUseridx((int)ed.get("midx"));
				  		long ctime = (new Date()).getTime(); 
				  		if( tu.lastcmdtime != -1 && tu.lastcmdtime + 60*1000 < ctime ) {
					    	synchronized(disconnectlist) {
					    		if( disconnectlist.contains( tu.session) != true) {
					    			disconnectlist.add( tu.session );
					    		}
					    	}
				  		}
				  		disconnect();
				  		tu.session.close();
					}catch(Exception e) {
						System.out.println("중복접속 강제 종료 처리중 에러");
					}


					cobj.put("code", 2);

					ObjectMapper mapper = new ObjectMapper();

					try {
						session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
					} catch (IOException e) {
						e.printStackTrace();
					}

					break;
				}

        		session.getAttributes().put("useridx", ed.get("midx"));
        		User user = new User(Integer.parseInt(""+ed.get("midx")), session);
        		
        		usermanager.connect(session, user);
        		break;
        	}
        	case "getAutoCharge":
        	{
    			try {
	    			User u = usermanager.find(session);
	    			if( u == null )return;
	    			//AI일경우 자동 재충전 처리
	    			if(u.isAI == true )
	    			{
	    				if( u.point < 1000000000)
	    					u.point += 1000000000;
	    				if( u.balance < 1000000 )
	    					u.balance += 1000000;
	    				
	    				// insertLog 머니로그 남겨야 함 !!!
	    				JSONObject chgobj=new JSONObject();
	    				chgobj.put("cmd", "aiautocharge");
	    				chgobj.put("currentpoint",""+u.point);
	    				chgobj.put("currentbalance",""+u.balance);
	    				u.sendMe(chgobj);
	    			}
	    			System.out.println("AI user 자동 충전 point:"+u.point+" gold:"+u.balance );
    			}catch(Exception e) {
    				System.out.println("getAutoCharge 에러 감지 :"+e.getMessage() );
    			}
        	}break;
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
				u1.timeoutstack = 0;
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
				roommanager.find(roomidx).gameManager.bet(u1, betkind);				 
        		break;
			}
			case "timeout":
        	{             	
        		User u1 = usermanager.find(session);             	
             	roommanager.find(u1.roomnum).gameManager.timeout(u1);
        		break;
        	}
        	case "sbBet":
        	{             	
        		User u2 = usermanager.find(session);
             	int betkind = Integer.parseInt(""+obj.get("betkind"));
             	int roomidx = Integer.parseInt(""+obj.get("roomidx"));             
             	roommanager.find(roomidx).gameManager.bet(u2, betkind);
        		break;
        	}
        	case "bbBet":
        	{             	
        		User u3 = usermanager.find(session);
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
        		usermanager.Beg(session);
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
			case "checkloginauth":
			{
				usermanager.GetUserAuth(session, Integer.parseInt(""+obj.get("midx")));
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
			case "renickname":
			{
				usermanager.ReSetNickName(session, ""+obj.get("nickname"));
		
			}break;
			case "reserveleave":
			{
				User user = usermanager.find(session);        		        		
				int roomidx = user.roomnum;
				roommanager.find(roomidx).BraodCastLeaveReserve(user, Boolean.parseBoolean(""+obj.get("state")));
			}break;
			case "getroominfo":
			{
				User user = usermanager.find(session);        		        		
				int roomidx = user.roomnum;
				roommanager.find(roomidx).GetRoomInfo(session);
			}break;
			case "useitem":
			{
				usermanager.UseItem(session, ""+obj.get("type"));
			}break;
			case "changelostlimit":
			{				
				usermanager.find(session).ChangeLostLimit(Long.parseLong(""+obj.get("amount")));
			}break;
		}
    }
 
    @Override
    public boolean supportsPartialMessages() {
        this.logger.info("call method!");
        return super.supportsPartialMessages();
    }
 
    public static int debugi = 0;
    public static int second = 0,sect=0;   
    @Override
    public void afterPropertiesSet() throws Exception {
    	gameidIdx = 0;
    	EgovMap gameId = (EgovMap) sampleDAO.select("selectLastGameId");    	
    	if(gameId!=null)
    		gameidIdx = Integer.parseInt(""+gameId.get("gameid"));
    	
    	gameIdentifier = BytesToHex( Sha256(""+gameidIdx) );
    	
    	chatmention.setDAO(sampleDAO);
		chatmention.uploding();
		JackpotManager.Init();
    	
        Thread thread = new Thread() {
 
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(50);
                        sect++;
                        if(sect>=10) 
                        {
                        	second++;
                        	sect=0;
                        }
                   		disconnect();			// 강종 유저 처리
                   		debugi=1;
                        cmdProcess(); 			// 클라로 부터 받은 메세지 처리
                        debugi=2;
                        roommanager.checkStartGame();
                        debugi=3;
						roommanager.checkTimerGame();
						debugi=4;
						roommanager.checkErrorGamingRoom();
						debugi=5;
						JackpotManager.Update();
                    } catch (InterruptedException e) {
                        System.out.println( "socketThread \n"+e.getMessage() +" i:"+debugi);
                    }catch(Exception e)
                    {
                    	System.out.println( "other errors lg \n"+e.getMessage() +" i:"+debugi);
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
        if (session != null && session.isOpen()) {
       	 synchronized( session)
       	 {
             try {
                 session.sendMessage(new TextMessage(message));
              } catch (Exception ignored) {
                 this.logger.error("fail to send message!", ignored);
              }
       	 }
         }
   }    
    static public void sendMessages(WebSocketSession session,String message) {
        if (session != null && session.isOpen()) {
       	 synchronized( session)
       	 {
             try {
                 session.sendMessage(new TextMessage(message));
              } catch (Exception ignored) {
                 System.out.println("static fail to send message!" );
              }
       	 }
         }
   }    

    private static byte[] Sha256(String password) throws NoSuchAlgorithmException {
        MessageDigest messagediegest = MessageDigest.getInstance("SHA-256");
        messagediegest.update(password.getBytes());
        messagediegest.update((""+System.currentTimeMillis()).getBytes());
        return messagediegest.digest();
    }
    
    private static String BytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b: bytes) {
          builder.append(String.format("%02x", b));
        }
        
        return builder.toString();
    }	    
}
