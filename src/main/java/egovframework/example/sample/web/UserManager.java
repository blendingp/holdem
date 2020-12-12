package egovframework.example.sample.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import javax.json.JsonObject;
import javax.mail.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.fabric.xmlrpc.base.Array;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.mapper.Mapper;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class UserManager {
	ArrayList<User> userlist = new ArrayList<User>();

	public User findFromUseridx(int useridx) {
		for (User a : userlist) {
			if (a.uidx == useridx) {
				return a;
			}
		}

		return null;
	}

	public User find(WebSocketSession session) {
		// 세션에서 기록해둔 uidx를 가져옴.
		// 없다면 얜 아직 로긴한 애가 아니니까 스킵

		Map<String, Object> m = session.getAttributes();
		int uidx = -1;
		try {
			uidx = Integer.parseInt("" + m.get("useridx"));
		}catch(Exception e)
		{
			System.out.println("User find error:"+e.getMessage() );
			return null;
		}

		for (User a : userlist) {
			if (a.uidx == uidx) {
				return a;
			}
		}

		return null;
	}

	public void connect(WebSocketSession session, User user) {

		// 닉네임 설정이 안되어 있으면 닉네임 설정 될떄까지 다음으로 안넘김		
		if( user.GetNickNameEmpty() == true )  		
		{
			JSONObject cobj = new JSONObject();
			cobj.put("cmd", "nickname");
			cobj.put("empty", true);

			if( session == null )
			{
				return ;
			}

			if( session.isOpen() == false )
			{
				return ;
			}

			ObjectMapper mapper = new ObjectMapper();

			try {
				session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
			} catch (IOException e) {
				e.printStackTrace();
			}

			return;
		}

		userlist.add(user);

		JSONObject cobj = new JSONObject();
		System.out.println("useridx: " + find(session).uidx);		
		cobj.put("cmd", "connectResult");
		cobj.put("useridx", find(session).uidx);
		cobj.put("balance", find(session).balance);
		cobj.put("cash", find(session).cash);
		cobj.put("budget", find(session).budget);
		cobj.put("point", find(session).point);
		cobj.put("safe_balance", find(session).safe_balance);
		cobj.put("safe_point", find(session).safe_point);
		cobj.put("bank", find(session).bank);
		cobj.put("nickname", ""+ find(session).nickname);
		
		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void buy(WebSocketSession session, String product, String receipt) {

		int rt = find(session).buyItem(product, receipt);

		ObjectMapper mapper = new ObjectMapper();

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "buyresult");
		cobj.put("result", rt);
		cobj.put("balance", find(session).balance);
		cobj.put("cash", find(session).cash);
		cobj.put("budget", find(session).budget);
		cobj.put("members", find(session).memberInfo);
		cobj.put("totalpayment", find(session).totalpayment);
		cobj.put("consumableItem", find(session).consumableItem);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Deal(WebSocketSession session, int item, int action, long amount) {

		int rt = find(session).Deal(item, action, amount);
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "dealresult");
		cobj.put("result", rt);
		cobj.put("balance", find(session).balance);
		cobj.put("safe_balance", find(session).safe_balance);
		cobj.put("point", find(session).point);
		cobj.put("safe_point", find(session).safe_point);
		cobj.put("bank", find(session).bank);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Beg(WebSocketSession session) {

		int rt = find(session).Beg();
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "begresult");
		cobj.put("result", rt);
		cobj.put("balance", find(session).balance);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SearchUserByID(WebSocketSession session, String id) {
		JSONObject cobj = Friend.SearchUserByID(id);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void RequestFriend(WebSocketSession session, String id) {
		JSONObject cobj = Friend.RequestFriend(find(session), id);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void AcceptFriend(WebSocketSession session, String id) {
		JSONObject cobj = Friend.AcceptFriend(id);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		//System.out.println(cobj.toJSONString());

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void RequestFriendList(WebSocketSession session) throws JsonProcessingException
	{
		ArrayList<FriendModel> requestlist = Friend.GetRequestFriendList(find(session), 0);
		
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "requestfriendlist");
		cobj.put("result", true);
		cobj.put("servertime", System.currentTimeMillis());		
		
		JSONArray list = new JSONArray();
		for(int nCount = 0; nCount < requestlist.size(); ++nCount)
		{
			JSONObject item = new JSONObject();
			item.put("uid", requestlist.get(nCount).UID);			
			item.put("name", requestlist.get(nCount).GetUserID(requestlist.get(nCount).Midx));			
			item.put("midx", requestlist.get(nCount).Midx);			
			item.put("balance", requestlist.get(nCount).GetUserBalance(requestlist.get(nCount).Midx));
			item.put("friendidx", requestlist.get(nCount).Friendidx);			
			item.put("createdtime", requestlist.get(nCount).Createtime);			
			item.put("status", requestlist.get(nCount).Status);			
			item.put("nextsendtime", requestlist.get(nCount).Nextsendtime);						
			
			list.add(item);
		}

		cobj.put("list", list);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void GetFriendList(WebSocketSession session) throws JsonProcessingException
	{
		ArrayList<FriendModel> requestlist = Friend.GetRequestFriendList(find(session), 1);
		ArrayList<FriendModel> friendlist = Friend.GetFriendList(find(session));		
		
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "friendlist");
		cobj.put("result", true);
		cobj.put("servertime", System.currentTimeMillis());
		
		JSONArray list = new JSONArray();
		for(int nCount = 0; nCount < requestlist.size(); ++nCount)
		{			
			requestlist.get(nCount).IsConnected = findFromUseridx(requestlist.get(nCount).Midx) != null;

			JSONObject item = new JSONObject();
			item.put("uid", requestlist.get(nCount).UID);			
			item.put("name", requestlist.get(nCount).GetUserID(requestlist.get(nCount).Midx));							
			item.put("balance", requestlist.get(nCount).GetUserBalance(requestlist.get(nCount).Midx));			
			item.put("createdtime", requestlist.get(nCount).Createtime);			
			item.put("status", requestlist.get(nCount).Status);			
			item.put("nextsendtime", requestlist.get(nCount).Nextsendtimefriend);		
			item.put("isconnected", requestlist.get(nCount).IsConnected);				
			
			list.add(item);
		}

		for(int nCount = 0; nCount < friendlist.size(); ++nCount)
		{						
			friendlist.get(nCount).IsConnected = findFromUseridx(friendlist.get(nCount).Friendidx) != null;

			JSONObject item = new JSONObject();
			item.put("uid", friendlist.get(nCount).UID);			
			item.put("name", friendlist.get(nCount).GetUserID(friendlist.get(nCount).Friendidx));								
			item.put("balance", friendlist.get(nCount).GetUserBalance(friendlist.get(nCount).Friendidx));				
			item.put("createdtime", friendlist.get(nCount).Createtime);			
			item.put("status", friendlist.get(nCount).Status);			
			item.put("nextsendtime", friendlist.get(nCount).Nextsendtime);						
			item.put("isconnected", friendlist.get(nCount).IsConnected);	
			
			list.add(item);
		}

		cobj.put("list", list);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SendFriendGold(WebSocketSession session, String uid) throws JsonProcessingException
	{		
		ArrayList<FriendModel> requestlist = Friend.GetRequestFriendList(find(session), 1);
		ArrayList<FriendModel> friendlist = Friend.GetFriendList(find(session));		

		for( FriendModel friend : requestlist )
		{
			if( friend.SendGold(find(session).uidx, uid) == true )
			{
				Task.IncreaseTask(find(session), 3, 1);
				Task.UpdateDB(find(session));			

				JSONObject cobj = new JSONObject();
				cobj.put("cmd", "sendfriendgold");
				cobj.put("result", true);
				cobj.put("uid", uid);

				if( session == null )
				{
					return ;
				}

				if( session.isOpen() == false )
				{
					return ;
				}

				try {
					session.sendMessage(new TextMessage(cobj.toJSONString()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return ;
			}
		}
		for( FriendModel friend : friendlist )
		{
			if( friend.SendGold(find(session).uidx, uid) == true )
			{
				Task.IncreaseTask(find(session), 3, 1);
				Task.UpdateDB(find(session));			

				JSONObject cobj = new JSONObject();
				cobj.put("cmd", "sendfriendgold");
				cobj.put("result", true);
				cobj.put("uid", uid);

				if( session == null )
				{
					return ;
				}

				if( session.isOpen() == false )
				{
					return ;
				}

				try {
					session.sendMessage(new TextMessage(cobj.toJSONString()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return ;
			}
		}		

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "sendfriendgold");
		cobj.put("result", false);
		cobj.put("uid", uid);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void DeleteFriend(WebSocketSession session, String uid)
	{
		ArrayList<FriendModel> requestlist = Friend.GetRequestFriendList(find(session), 1);
		ArrayList<FriendModel> friendlist = Friend.GetFriendList(find(session));		
		
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "deletefriend");		
		boolean result = false;				
				
		for(int nCount = 0; nCount < requestlist.size(); ++nCount)
		{			
			if(requestlist.get(nCount).UID.equals(uid) == true)
			{
				requestlist.get(nCount).Delete(uid);
				result = true;
			}
		}

		for(int nCount = 0; nCount < friendlist.size(); ++nCount)
		{			
			if(friendlist.get(nCount).UID.equals(uid) == true)
			{
				friendlist.get(nCount).Delete(uid);
				result = true;
			}
		}		

		cobj.put("result", result);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void CheckAttendance(WebSocketSession session) throws JsonProcessingException
	{	
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "checkattandance");		
		cobj.put("prev", find(session).attendance.Count);
		cobj.put("count", find(session).attendance.CheckAttendance());
			
		ClassPathResource resource = new ClassPathResource("json/attendance.json");

		try {
			Path path = Paths.get(resource.getURI());
			String content = Files.readString(path);	

			ObjectMapper mapper = new ObjectMapper();
			ArrayList<AttendanceItem> itemlist = mapper.readValue(content, new ArrayList<AttendanceItem>().getClass());

			cobj.put("attandance", itemlist);	
			
			if( session == null )
			{
				return ;
			}

			if( session.isOpen() == false )
			{
				return ;
			}

			session.sendMessage(new TextMessage(cobj.toJSONString()));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void GetInbox(WebSocketSession session, int type) throws JsonProcessingException
	{		
		ArrayList<InBox> list = InBox.GetUserInbox(find(session).uidx, type);		
		ObjectMapper mapper = new ObjectMapper();

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "getinbox");
		cobj.put("list", list);

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void GetInBoxReward(WebSocketSession session, String uid) throws JsonProcessingException
	{				
		InBox inbox = InBox.GetInbox(find(session).uidx, uid);		
		
		for( Item item : inbox.ItemList )
		{
			if(find(session).InsertItem(item) == false)
			{
				uid = "";
				inbox = null;
				break;
			}
		}		

		if( inbox != null )
		{
			inbox.DeleteInBox();
		}

		ObjectMapper mapper = new ObjectMapper();

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "getinboxreward");
		cobj.put("uid", uid);
		if( inbox != null)
		{
			cobj.put("reward", inbox.ItemList);			
			cobj.put("avatalist", find(session).avatalist);
		}					

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void GetUserInfo(WebSocketSession session) {
		
		find(session).CheckExpireTodayRecord();

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "userinfo");							
		cobj.put("info", find(session).MakeUserInfo());
		
		System.out.println("GetUserInfo:"+find(session).point );

		JSONObject jackpot = new JSONObject();
		jackpot.put("cmd", "jackpot");							
		jackpot.put("amount", JackpotManager.GetJackpotAmount());

		ObjectMapper mapper = new ObjectMapper();

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
			session.sendMessage(new TextMessage(mapper.writeValueAsString(jackpot)));
			if( find(session).MakeUserInfo().memberinfo.ban.isEmpty() == false )
			{
				session.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SetUserAvata(WebSocketSession session, String avata) {

		find(session).SetUserAvata(avata);
	}

	public void GetUserAuth(WebSocketSession session)
	{
		boolean result = find(session).GetAuth();
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "authresult");							
		cobj.put("result", result);		

		ObjectMapper mapper = new ObjectMapper();

		if( session == null )
		{
			return ;
		}

		if( session.isOpen() == false )
		{
			return ;
		}

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public void SignUp(WebSocketSession session, String id, String pass)
	{
		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "singup");							

		if( ExsitID(id) == true)
		{
			cobj.put("result", false);		

			ObjectMapper mapper = new ObjectMapper();

			if( session == null )
			{
				return ;
			}

			if( session.isOpen() == false )
			{
				return ;
			}

			try {
				session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
			} catch (IOException e) {			
				e.printStackTrace();
			}

			return ;
		}		

		EgovMap insert = new EgovMap();
		insert.put("muserid", id);
		insert.put("muserpw", pass);    			
		insert.put("socail", "");
		SocketHandler.sk.sampleDAO.insert("InsertUser", insert);		

		EgovMap in = new EgovMap();
        in.put("muserid", id);
        in.put("muserpw", pass);    			
        EgovMap ed = (EgovMap)SocketHandler.sk.sampleDAO.select("Login", in);
		session.getAttributes().put("useridx", ed.get("midx"));
		if( ed == null )
		{
			cobj.put("result", false);		

			ObjectMapper mapper = new ObjectMapper();

			if( session == null )
			{
				return ;
			}

			if( session.isOpen() == false )
			{
				return ;
			}

			try {
				session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
			} catch (IOException e) {			
				e.printStackTrace();
			}

			return ;
		}
		
		EgovMap itemin = new EgovMap();
		itemin.put("midx", ed.get("midx"));		
		itemin.put("point", 5000);		
		SocketHandler.sk.sampleDAO.insert("InsertItem", itemin);		
		
        User user = new User(Integer.parseInt(""+ed.get("midx")), session);        		
        		
        connect(session, user);
	}

	public void SignUp(WebSocketSession session, String social)
	{
		EgovMap in = new EgovMap();
        in.put("socail", social);
		EgovMap ed = (EgovMap)SocketHandler.sk.sampleDAO.select("ExsitSocial", in);
		
		if( ed == null )
		{
			EgovMap insert = new EgovMap();
			insert.put("muserid", "");
			insert.put("muserpw", "");    			
			insert.put("socail", social);
			SocketHandler.sk.sampleDAO.insert("InsertUser", insert);		

			ed = (EgovMap)SocketHandler.sk.sampleDAO.select("ExsitSocial", in);

			EgovMap itemin = new EgovMap();
			itemin.put("midx", ed.get("midx"));		
			itemin.put("point", 5000);		
			SocketHandler.sk.sampleDAO.insert("InsertItem", itemin);			        
		}				
		
		session.getAttributes().put("useridx", ed.get("midx"));			
		
		User user = new User(Integer.parseInt(""+ed.get("midx")), session);      			
					
		connect(session, user);			
	}

	public void GoogleLogin(WebSocketSession session, String social)
	{
		EgovMap in = new EgovMap();
        in.put("socail", social);
		EgovMap ed = (EgovMap)SocketHandler.sk.sampleDAO.select("ExsitSocial", in);
		
		if( ed == null )
		{
			JSONObject cobj = new JSONObject();
			cobj.put("cmd", "gsingin");							
			cobj.put("result", false);

			ObjectMapper mapper = new ObjectMapper();

			try {
				session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
			} catch (IOException e) {			
				e.printStackTrace();
			}

			return ;
		}
		else
		{
			session.getAttributes().put("useridx", ed.get("midx"));			
		
			User user = new User(Integer.parseInt(""+ed.get("midx")), session);      			
					
			connect(session, user);
		}		
	}

	public User SetNickName(WebSocketSession session, String nickname)
	{
		if(session.getAttributes().containsKey("useridx") == false)
		{
			return null;
		}

		EgovMap in = new EgovMap();
		in.put("nickname", nickname);

		EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("ExsitNickName", in);		

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "exsitnickname");
		cobj.put("exsit", ed != null);

		ObjectMapper mapper = new ObjectMapper();

		if( session == null )
		{
			return null;
		}

		if( session.isOpen() == false )
		{
			return null;
		}

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if( ed != null )
		{			
			return null;			
		}
		
		User user = new User( (int)session.getAttributes().get("useridx"), session);
		user.SetNickName(nickname);

		return user;
	}

	public void ReSetNickName(WebSocketSession session, String nickname)
	{
		EgovMap in = new EgovMap();
		in.put("nickname", nickname);

		EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("ExsitNickName", in);		

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "exsitnickname");
		cobj.put("exsit", ed != null);

		ObjectMapper mapper = new ObjectMapper();
		User user = find(session);

		if(User.CheckSendPacket(user) == false)
		{
			return ;
		}

		try {
			user.session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if( ed != null )
		{			
			return ;			
		}
	
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("cmd", "changednickname");
		jsonobj.put("name", user.ReNickName(nickname));		
		jsonobj.put("consumableItem", user.consumableItem);		

		if(User.CheckSendPacket(user) == false)
		{
			return ;
		}

		try {
			user.session.sendMessage(new TextMessage(mapper.writeValueAsString(jsonobj)));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void UseItem(WebSocketSession session, String type)
	{
		User user = find(session);
		user.UseItem(type);			

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "itemused");
		cobj.put("point", user.point);
		cobj.put("consumableItem", user.consumableItem);		

		if( User.CheckSendPacket(user) == false)
		{
			return ;
		}

		ObjectMapper mapper = new ObjectMapper();

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean ExsitID(String id)
	{
		EgovMap in = new EgovMap();
		in.put("muserid", id);

		EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("ExsitID", in);		

		return ed != null;
	}

	public boolean ExsitSocail(String socail)
	{
		EgovMap in = new EgovMap();
		in.put("socail", socail);

		EgovMap ed = (EgovMap) SocketHandler.sk.sampleDAO.select("ExsitSocial", in);		

		return ed != null;
	}
}
