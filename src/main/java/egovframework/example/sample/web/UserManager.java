package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;

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
	
	public void connect(WebSocketSession session, User user, String chatmention){
		userlist.add(user);		
		
		JSONObject cobj = new JSONObject();
		System.out.println("useridx: "+find(session).uidx);
		cobj.put("cmd", "connectResult");
		cobj.put("useridx", find(session).uidx);
		cobj.put("balance", find(session).balance);
		cobj.put("cash", find(session).cash);
		cobj.put("budget", find(session).budget);
		cobj.put("point", find(session).point);
		cobj.put("safe_balance", find(session).safe_balance);
		cobj.put("safe_point", find(session).safe_point);
		//구정연_멘션 스트링 리스트 전달
		cobj.put("chatmention", chatmention);
		System.out.println("chatmention :" + chatmention);
		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void buy(WebSocketSession session, String product, String receipt){
		
		int rt = find(session).buyItem(product, receipt);
		
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "buyresult");
		cobj.put("result", rt);
		cobj.put("balance", find(session).balance);
		cobj.put("cash", find(session).cash);
		cobj.put("budget", find(session).budget);
		
		System.out.println(rt);
		
		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Deal(WebSocketSession session, int item, int action, int amount){
		
		int rt = find(session).Deal(item, action, amount);
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "dealresult");
		cobj.put("result", rt);
		cobj.put("balance", find(session).balance);
		cobj.put("safe_balance", find(session).safe_balance);
		cobj.put("point", find(session).point);
		cobj.put("safe_point", find(session).safe_point);
		
		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Beg(WebSocketSession session){
		
		int rt = find(session).Beg();
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "begresult");
		cobj.put("result", rt);
		cobj.put("balance", find(session).balance);
				
		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
