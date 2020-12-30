package egovframework.example.sample.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.example.sample.web.model.RoomInfoModel;

public class Room {
	GameManager gameManager;

	int ridx;
	long defaultmoney;// 삥머니
	long maxmoney;// 배팅 맥스 머니
	int maxusersize = 9;
	int title = 0;
	RoomInfoModel roominfo = new RoomInfoModel();

	public String UsedItem = "balance";
	private String _roomKey = "";
	private boolean _isPrivate = false;

	public static RoomSetting GetRoomInfo(String key) {
		ClassPathResource resource = new ClassPathResource("json/roomsetting/" + key + ".json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			Path path = Paths.get(resource.getURI());
			String content = Files.readString(path);
			RoomSetting setting = mapper.readValue(content, RoomSetting.class);
			return setting;
		} catch (IOException e) {
			System.out.println("RoomSetting getRoominfo error:"+e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public Room(int ridx, String roomkey) {
		this.ridx = ridx;
		_roomKey = roomkey;

		SetRoomInfo(roomkey);
		gameManager = new GameManager(this);

		Random random = new Random();
		title = random.nextInt(4);
	}

	public boolean fullRoom() {

		if (gameManager.userlist.size() + gameManager.watchinguserlist.size() + gameManager.spareuserlist.size() >= maxusersize*2 ) {
			return true;
		}
		return false;
	}

	public boolean emptyRoom() {
		if (gameManager.userlist.size() + gameManager.watchinguserlist.size() + gameManager.spareuserlist.size() == 0)
			return true;
		return false;
	}

	public void notifyRoomUsers() {// notifyJoinUser아래와 거의 같으나 내가 안만들어서 어디서 쓰이는지 불안해서 새로 만듬, 룸안의 유저가 변경되었을때 갱신하기 위해
									// 쓰임.

		JSONObject myobj = new JSONObject();
		myobj.put("cmd", "RoomUsers");
		// 방에 참여중인 모든 사람 불러오기
		JSONArray j = new JSONArray();
		for (int i = 0; i < gameManager.userlist.size(); i++) {
			JSONObject item = new JSONObject();
			item.put("useridx", gameManager.userlist.get(i).uidx);
			item.put("seat", gameManager.userlist.get(i).seat);
			item.put("img", "" + gameManager.userlist.get(i).img);
			if (this.UsedItem.equals("balance") == true) {
				item.put("balance", gameManager.userlist.get(i).balance);
			} else if (this.UsedItem.equals("point") == true) {
				item.put("balance", gameManager.userlist.get(i).point);
			}
			item.put("nickname", "" + gameManager.userlist.get(i).nickname);
			item.put("profile", gameManager.userlist.get(i).todayprofile);
			j.add(item);			
		}
		myobj.put("userlist", j);

		gameManager.sendRoom(myobj);
	}

	public void notifyJoinUser(User u) {

		JSONObject myobj = new JSONObject();
		myobj.put("cmd", "RoomJoinOk");
		myobj.put("roomidx", ridx);
		myobj.put("useridx", u.uidx);// 참여자인덱스
		myobj.put("seat", u.seat);
		myobj.put("ante", defaultmoney);
		myobj.put("max", maxmoney);
		myobj.put("maxuser", maxusersize);
		myobj.put("isprivate", _isPrivate);
		myobj.put("roomkey", _roomKey);
		myobj.put("dealer", gameManager.getDealerSeat() );
		myobj.put("smallblind", gameManager.getDealerSeatOffset(1) );
		myobj.put("bigblind", gameManager.getDealerSeatOffset(2));
		myobj.put("spareCount", gameManager.spareuserlist.size() );
		
		if( isPrivate() == true )
		{
			myobj.put("roominfo", roominfo);
		}

		JSONArray j = new JSONArray();

		for (User user : gameManager.userlist) {
			JSONObject item = new JSONObject();
			item.put("useridx", user.uidx);
			item.put("seat", user.seat);
			item.put("img", "" + user.img);
			if (this.UsedItem.equals("balance") == true) {
				item.put("balance", user.balance);
			} else if (this.UsedItem.equals("point") == true) {
				item.put("balance", user.point);
			}
			item.put("members", user.memberInfo.grade);
			item.put("isdie", user.die);
			item.put("nickname", "" + user.nickname);
			item.put("profile", user.todayprofile);
			item.put("iswatching", false);
			j.add(item);
		}

		for (User user : gameManager.watchinguserlist) {
			JSONObject item = new JSONObject();
			item.put("useridx", user.uidx);
			item.put("seat", user.seat);
			item.put("img", "" + user.img);
			if (this.UsedItem.equals("balance") == true) {
				item.put("balance", user.balance);
			} else if (this.UsedItem.equals("point") == true) {
				item.put("balance", user.point);
			}
			item.put("members", user.memberInfo.grade);
			item.put("isdie", user.die);
			item.put("nickname", "" + user.nickname);
			item.put("profile", user.todayprofile);
			item.put("iswatching", true);
			j.add(item);
		}

		myobj.put("userlist", j);

		gameManager.sendRoom(myobj);
	}

	public void BroadCasetUser() {

		JSONObject myobj = new JSONObject();
		myobj.put("cmd", "RoomJoinOk");
		myobj.put("roomidx", ridx);		
		myobj.put("ante", defaultmoney);
		myobj.put("max", maxmoney);
		myobj.put("maxuser", maxusersize);
		myobj.put("isprivate", _isPrivate);
		myobj.put("roomkey", _roomKey);

		myobj.put("dealer", gameManager.getDealerSeat() );
		myobj.put("smallblind", gameManager.getDealerSeatOffset(1) );
		myobj.put("bigblind", gameManager.getDealerSeatOffset(2));
		myobj.put("spareCount", gameManager.spareuserlist.size() );
		
		JSONArray j = new JSONArray();

		for (User user : gameManager.userlist) {
			JSONObject item = new JSONObject();
			item.put("useridx", user.uidx);
			item.put("seat", user.seat);
			item.put("img", "" + user.img);
			if (this.UsedItem.equals("balance") == true) {
				item.put("balance", user.balance);
			} else if (this.UsedItem.equals("point") == true) {
				item.put("balance", user.point);
			}
			item.put("members", user.memberInfo.grade);
			item.put("isdie", user.die);
			item.put("nickname", "" + user.nickname);
			item.put("profile", user.todayprofile);
			item.put("iswatching", false);
			j.add(item);
		}

		for (User user : gameManager.watchinguserlist) {
			JSONObject item = new JSONObject();
			item.put("useridx", user.uidx);
			item.put("seat", user.seat);
			item.put("img", "" + user.img);
			if (this.UsedItem.equals("balance") == true) {
				item.put("balance", user.balance);
			} else if (this.UsedItem.equals("point") == true) {
				item.put("balance", user.point);
			}
			item.put("members", user.memberInfo.grade);
			item.put("isdie", user.die);
			item.put("nickname", "" + user.nickname);
			item.put("profile", user.todayprofile);
			item.put("iswatching", true);
			j.add(item);
		}

		myobj.put("userlist", j);
		gameManager.sendRoom(myobj);
	}
	
	public void spareCount() {
		JSONObject myobj = new JSONObject();
		myobj.put("count", gameManager.spareuserlist.size());
		myobj.put("cmd", "spareCount");
		gameManager.sendRoom(myobj);
	}
	
	public void notifyLeaveUser(int seat) {
		JSONObject myobj = new JSONObject();
		myobj.put("cmd", "RoomLeaveOk");
		myobj.put("seat", seat);
		myobj.put("neterror", false);
		myobj.put("iswatching", (!gameManager.IsJoinGame(seat)));
		// 방에 참여중인 모든 사람 불러오기
		gameManager.sendRoom(myobj);
	}

	public void notifyLeaveUserNetError(int seat) {
		JSONObject myobj = new JSONObject();
		myobj.put("cmd", "RoomLeaveOk");
		myobj.put("seat", seat);
		myobj.put("neterror", true);
		myobj.put("iswatching", (!gameManager.IsJoinGame(seat)));
		// 방에 참여중인 모든 사람 불러오기
		gameManager.sendRoom(myobj);
	}
	
	public void GetRoomInfo(WebSocketSession session) {
		if( session == null )
		{
			return ;
		}

		roominfo.roomidx = ridx;
		roominfo.ante = defaultmoney;
		roominfo.lowerlimit = defaultmoney * 100;
		roominfo.max = maxmoney;
		roominfo.maxuser = maxusersize;

		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JSONObject myobj = new JSONObject();
			myobj.put("cmd", "roominfo");
			myobj.put("info", roominfo);
			
			if( session.isOpen() == false )
			{
				return ;
			}

			session.sendMessage(new TextMessage(mapper.writeValueAsString(myobj)));
		} 
		catch (JsonProcessingException e) {			
			e.printStackTrace();
		}
		catch ( IOException ioe )
		{

		}
	}

	public boolean isPrivate()
	{
		return _isPrivate;
	}

	public boolean join(User u, int ridx ) {
		
		if( _isPrivate == false )
		{
			if( this.UsedItem.equals("balance") == true){
				if( u.balance < defaultmoney * 100 ){
					return false;
				}
			}
			else if( this.UsedItem.equals("point") == true){
				if( u.point < defaultmoney * 100 ){
					return false;
				}
			}		
		}		
		else
		{
			if( this.UsedItem.equals("balance") == true){
				if( u.balance <= 0 ){
					return false;
				}
			}
			else if( this.UsedItem.equals("point") == true){
				return false;
			}		
		}

		if( gameManager.GetEmptySeat() < 0 )
		{
			if( gameManager.spareuserlist.contains(u) == false )
			{
				gameManager.InsertSpareUser(u);
			}			
			//spare패킷 보내기 
			spareCount();
//			return false;
		}
		if(gameManager.userlist.contains(u) == false 
			&& gameManager.watchinguserlist.contains(u) == false
			&& gameManager.spareuserlist.contains(u) == false
			&& gameManager.leaveuserlist.contains(u) == false)
		{
			u.seat = gameManager.GetEmptySeat();		
			gameManager.SetSeat(u.seat);
		}
		
		u.roomnum = ridx;	
		u.live = true;
		if( u.seat >= 0 )
		{
			if( gameManager.GameMode.compareTo("대기") == 0 )
			{			
				if(gameManager.userlist.contains(u) == false)
				{
					gameManager.userlist.add( u );		
				}						
				gameManager.setWorkTime( );//새로 한명 들어올때마다 대기 시간을 증가시켜서 여러명이 들어올 여지를 둔다.			
			}
			else
			{
				if( gameManager.watchinguserlist.contains(u) == false )
				{
					gameManager.InsertWatchingUser(u);
				}			
			}
		}

		notifyJoinUser(u);

		return true;
				
	} 

	
	public void leave(User u) {		
		
		for( int nCount = 0; nCount < gameManager.spareuserlist.size(); nCount++ )
		{
			if( gameManager.spareuserlist.get(nCount).uidx == u.uidx)
			{					
				notifyLeaveUser(u.seat);
				gameManager.spareuserlist.remove(nCount);
				spareCount();
				System.out.println("<<spare Room . leave >> :"+ u.nickname+" "+(new Date()).toLocaleString() );
				return;
			}		
		}
		
		for( int nCount = 0; nCount < gameManager.watchinguserlist.size(); nCount++ )
		{			
			if( gameManager.watchinguserlist.get(nCount).uidx == u.uidx)
			{
				notifyLeaveUser(u.seat);
				gameManager.EmptySeat(u.seat);
				gameManager.watchinguserlist.remove(nCount);
				u.clear();
				System.out.println("<<관전자 Room . leave >> :"+ u.nickname+" "+(new Date()).toLocaleString() );
				return;
			}			
		}
		
		if( gameManager.GameMode.compareTo("대기") != 0 )
		{
			System.out.println("대기중에 나가기 시도함. "+u.uidx );
			return;
		}

		notifyLeaveUser(u.seat);
		gameManager.EmptySeat(u.seat);
		for( int nCount = 0; nCount < gameManager.userlist.size(); nCount++ )
		{
			if( gameManager.userlist.get(nCount).uidx == u.uidx)
			{
				gameManager.userlist.remove(nCount);
				break;
			}			
		}

		for( int nCount = 0; nCount < gameManager.leaveuserlist.size(); nCount++ )
		{
			if( gameManager.leaveuserlist.get(nCount).uidx == u.uidx)
			{
				gameManager.leaveuserlist.remove(nCount);
				break;
			}			
		}

		u.clear();
		System.out.println("<< Room . leave >> :"+ u.nickname);
	}

	public void unliveLeave() {		
		
		ArrayList<User> rmwatchlist = new ArrayList<User>();
		for( int nCount = 0; nCount < gameManager.watchinguserlist.size(); nCount++ )
		{
			User u = gameManager.watchinguserlist.get(nCount);
			System.out.println("u:"+ u.live);
			if( u.live == false )
			{
				notifyLeaveUserNetError(u.seat);
				gameManager.EmptySeat(u.seat);
				rmwatchlist.add(u);
				u.clear();
				System.out.println("<<관전자 Room . leave >> :"+ u.nickname+" "+(new Date()).toLocaleString() );
			}			
		}
		gameManager.watchinguserlist.removeAll(rmwatchlist);

		ArrayList<User> rmuserlist = new ArrayList<User>();
		for( int nCount = 0; nCount < gameManager.userlist.size(); nCount++ )
		{
			User u = gameManager.userlist.get(nCount);
			System.out.println("u:"+ u.live);
			if( u.live == false )
			{
				System.out.println("=================NetError====================");
				notifyLeaveUserNetError(u.seat);
				gameManager.EmptySeat(u.seat);
				rmuserlist.add(u);
				u.clear();
			}			
		}
		gameManager.userlist.removeAll(rmuserlist);
	}
	
	public void postCheckStartRoom(User u) {
		//gameManager.startCheck(u);
	}	

	public void postGiveTowCard(User u) {
		//gameManager.GiveTowCard(u);
	}	

	public void checkStartGame(){
		try {
			gameManager.checkStartGame();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	public void checkTimerGame(){
		gameManager.checkTimerGame();
	}	
	public void checkErrorGamingRoom() {
		if( gameManager.GameMode.compareTo("대기") !=0  &&  gameManager.lastcmdtime +30000 < (new Date()).getTime() ) 
		{//
			gameManager.lastcmdtime = (new Date()).getTime();
			//30초이상 명령 진행이 안되고 있으면 에러난 방이므로 에러 로그를 기록하고 결과 처리한다. 해당 방 번호를 기록.,추가로 에러로그 기록
			gameManager.setWorkTime();
			gameManager.changeGameMode("showResult");
			System.out.println("===================게임 멈춤 문제발생====");
			SocketHandler.insertLog(gameManager.getGameId(), gameManager.getGameIdentifier(), "stopERROR", -1, -1, -1, "", -1, -1);
		}
	}

	public Room GetRoomByKey(String key)
	{
		if( fullRoom() == true )
		{
			return null;
		}

		if( _roomKey.equals(key) == true && _isPrivate == false)
		{
			return this;
		}

		return null;
	}

	public RoomInfo GetRoomInfoByKey(String key)
	{
		if( _roomKey.contains(key) == true && _isPrivate == false)
		{
			RoomInfo info = new RoomInfo();
			info.roomkey = _roomKey;
			info.roomnumber = ridx;
			info.ante = defaultmoney;
			info.maxbet = maxmoney;
			info.maxusersize = maxusersize;
			info.currentcount = gameManager.userlist.size() + gameManager.watchinguserlist.size() + gameManager.spareuserlist.size();
			info.title = title;
			return info;
		}

		return null;
	}

	public Room GetRoomByNumber(int number)
	{
		if( ridx == number)
		{			
			return this;
		}

		return null;
	}	

	public void SetGoldRoom(GoldRoom roominfo)
	{
		this.defaultmoney = roominfo.ante;
		this.maxmoney = roominfo.maxbetvalue;
		this.maxusersize = roominfo.maxplayer;		
		_isPrivate = roominfo.isprivate;
		this.roominfo.ante = roominfo.ante;
		this.roominfo.max = roominfo.maxbetvalue;
		this.roominfo.maxuser = roominfo.maxplayer;		
	}

	public void LeaveReserve(User user)
	{		
		if( gameManager.IsJoinGame(user.seat) == false)
		{
			System.out.println("LeaveReserve first====================================");
			//notifyLeaveUser(user.seat);
			leave(user);
			return ;
		}

		for( int nCount = 0; nCount < gameManager.userlist.size(); ++nCount )
		{						
			if(gameManager.userlist.get(nCount).uidx == user.uidx)
			{											
				gameManager.leaveuserlist.add(gameManager.userlist.get(nCount));								
				BraodCastLeaveReserve(user, true);				
			}
		}		
	}

	public void BraodCastLeaveReserve(User user, Boolean isreserve)
	{
		JSONObject obj = new JSONObject();
		obj.put("cmd","reserve");		
		obj.put("state", isreserve);
		obj.put("seat", user.seat);
		gameManager.sendRoom(obj);		
	}

	private void SetRoomInfo(String key)
	{
		ClassPathResource resource = new ClassPathResource("json/roomsetting/" + key + ".json");
		ObjectMapper mapper = new ObjectMapper();
		try {
            Path path = Paths.get(resource.getURI());
			String content = Files.readString(path);	 
			RoomSetting setting = mapper.readValue(content, RoomSetting.class);
			this.defaultmoney = setting.ante;
			this.maxmoney = setting.max;
			this.maxusersize = setting.maxplayer;
			this.UsedItem = setting.useitem;			
		}
		catch (IOException e) {
			System.out.println("SetRoomInfo err:"+e.getMessage());
			e.printStackTrace();
		}        
	}
	public void toReserveOnly(User u)
	{
		//게임중이라면 예약 가능
		//게임중이 아니거나 , 게임중에 대기하는중이라면 바로 관전으로 이동
		u.sparefix = true;
		JSONObject obj = new JSONObject();
		obj.put("cmd","reserveOnlyOk");
		u.sendMe(obj);
		System.out.println("게임중 관전 예약");
	}
	public void toReserveJoin(User u)
	{
		u.sparefix = false;
		JSONObject obj = new JSONObject();
		obj.put("cmd","reserveJoinOk");
		obj.put("count", reserveCount(u) );
		u.sendMe(obj);
	}
	int reserveCount(User tu)
	{
		int c=0;
		for(User u : gameManager.spareuserlist)
		{
			c++;
			if(u.uidx == tu.uidx)
				break;
		}
		return c;
	}
}
