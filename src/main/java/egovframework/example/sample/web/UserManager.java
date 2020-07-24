package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class UserManager {
	ArrayList<User> userlist = new ArrayList<User>();
	
	public User findFromUseridx(int useridx){			
		for( User a : userlist){
			if( a.uidx == useridx){				
				return a;				
			}
		}
		
		return null;
	}
		
	public User find(WebSocketSession session){
		//세션에서 기록해둔 uidx를 가져옴.
		//없다면 얜 아직 로긴한 애가 아니니까 스킵
		
		Map<String, Object> m = session.getAttributes();    	
    	int uidx =  Integer.parseInt(""+m.get("useridx"));
    	
		for( User a : userlist){
			if( a.uidx == uidx){
				return a;				
			}
		}
		
		return null;
	}
	
	public void connect(WebSocketSession session, User user){
		userlist.add(user);
		
		JSONObject cobj = new JSONObject();
		System.out.println("useridx: "+find(session).uidx);
		cobj.put("cmd", "connectResult");
		cobj.put("useridx", find(session).uidx);
		
		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
