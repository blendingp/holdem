package egovframework.example.sample.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.RandomUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.socket.TextMessage;

public class Room {
	GameManager gameManager;

	int ridx;
	int defaultmoney;//삥머니
	int maxmoney;//배팅 맥스 머니
	int maxusersize = 9;
	int title = 0;

	public String UsedItem = "balance";
	private String _roomKey = "";
	private boolean _isPrivate = false;

	public Room(int ridx, String roomkey){
		this.ridx = ridx;		
		_roomKey = roomkey;	
		
		SetRoomInfo(roomkey);
		gameManager = new GameManager(this);
		
		Random random = new Random();		
		title = random.nextInt(4);		
	}
	public void init(){
		gameManager.init();
	}

	public boolean fullRoom(){
		
		if( gameManager.userlist.size() >= maxusersize)
		{
			return true;
		}

		return false;

	}
	public boolean emptyRoom(){
		if( gameManager.userlist.size() == 0)
			return true;
		return false;
	}
	public void notifyRoomUsers() {//notifyJoinUser아래와 거의 같으나 내가 안만들어서 어디서 쓰이는지 불안해서 새로 만듬, 룸안의 유저가 변경되었을때 갱신하기 위해 쓰임.

		JSONObject myobj = new JSONObject();						
		myobj.put("cmd","RoomUsers");
		//방에 참여중인 모든 사람 불러오기
		JSONArray j = new JSONArray();
		System.out.println("notifyRoomUsers");
		for(int i=0; i<gameManager.userlist.size(); i++)
		{
			JSONObject item = new JSONObject();			
			item.put("useridx",gameManager.userlist.get(i).uidx);
			item.put("seat",gameManager.userlist.get(i).seat);
			item.put("img",""+ gameManager.userlist.get(i).img);
			if( this.UsedItem.equals("balance") == true){
				item.put("balance",gameManager.userlist.get(i).balance);
			}			
			else if( this.UsedItem.equals("point") == true){
				item.put("balance",gameManager.userlist.get(i).point);
			}			
			item.put("nickname",""+ gameManager.userlist.get(i).nickname);
			item.put("profile", gameManager.userlist.get(i).todayprofile);
			j.add(item);
			System.out.println("uidx:"+gameManager.userlist.get(i).uidx +"  seat:"+gameManager.userlist.get(i).seat );
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

		ArrayList<User> joinuserlist = new ArrayList<>();
		for( User user : gameManager.userlist )
		{
			joinuserlist.add(user);
		}

		for( User user : gameManager.watchinguserlist )
		{
			joinuserlist.add(user);
		}
		
		// 방에 참여중인 모든 사람 불러오기
		JSONArray j = new JSONArray();
		for (int i = 0; i < joinuserlist.size(); i++) {
			JSONObject item = new JSONObject();
			item.put("useridx", joinuserlist.get(i).uidx);
			item.put("seat", joinuserlist.get(i).seat);
			item.put("img", "" + joinuserlist.get(i).img);
			if (this.UsedItem.equals("balance") == true) {
				item.put("balance", joinuserlist.get(i).balance);
			} else if (this.UsedItem.equals("point") == true) {
				item.put("balance", joinuserlist.get(i).point);
			}
			item.put("isdie", joinuserlist.get(i).die);
			item.put("nickname", "" + joinuserlist.get(i).nickname);
			item.put("profile", joinuserlist.get(i).todayprofile);
			j.add(item);
		}
		
		myobj.put("userlist", j);

		gameManager.sendRoom(myobj);
	}

	public void notifyLeaveUser(int seat) {

		JSONObject myobj = new JSONObject();						
		myobj.put("cmd","RoomLeaveOk");
		myobj.put("seat",seat);
		//방에 참여중인 모든 사람 불러오기
		gameManager.sendRoom(myobj);

	}

	public boolean join(User u, int ridx ) {
		
		if( this.UsedItem.equals("balance") == true){
			if( u.balance < defaultmoney * 3 ){
				return false;
			}
		}
		else if( this.UsedItem.equals("point") == true){
			if( u.point < defaultmoney * 3 ){
				return false;
			}
		}		

		if( gameManager.GetEmptySeat() < 0 )
		{
			return false;
		}

		u.seat = gameManager.GetEmptySeat();		
		gameManager.SetSeat(u.seat);
		u.roomnum = ridx;		
		
		if( gameManager.GameMode == "대기" )
		{						
			gameManager.userlist.add( u );		
			gameManager.startCheck(u, gameManager.userlist);			
			
			gameManager.setWorkTime( );//새로 한명 들어올때마다 대기 시간을 증가시켜서 여러명이 들어올 여지를 둔다.			
		}
		else
		{
			gameManager.InsertWatchingUser(u);
		}

		notifyJoinUser(u);

		return true;
				
	}

	public void leave(User u) {		
		System.out.println(u.seat);
		gameManager.EmptySeat(u.seat);
		u.die = false;
		gameManager.userlist.remove(u);
		gameManager.leaveuserlist.remove(u);
		gameManager.watchinguserlist.remove(u);
		/*
		for(int k=0; k<gameManager.userlist.size(); k++)
			gameManager.userlist.get(k).seat = k;*/		
		
		u.clear();
		System.out.println("<< Room . leave >>");
	}


	public void postCheckStartRoom(User u) {
		//gameManager.startCheck(u);
	}	

	public void postGiveTowCard(User u) {
		//gameManager.GiveTowCard(u);
	}	

	public void checkStartGame(){
		gameManager.checkStartGame();
	}	

	public void checkTimerGame(){
		gameManager.checkTimerGame();
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
			info.currentcount = gameManager.userlist.size();
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
		this.maxmoney = (int)roominfo.maxbetvalue;
		this.maxusersize = roominfo.maxplayer;		
		_isPrivate = roominfo.isprivate;
	}

	public void LeaveReserve(User user)
	{		
		for( int nCount = 0; nCount < gameManager.userlist.size(); ++nCount )
		{						
			if(gameManager.userlist.get(nCount).uidx == user.uidx)
			{											
				//gameManager.userlist.get(nCount).die = true;				
				gameManager.leaveuserlist.add(gameManager.userlist.get(nCount));				
				gameManager.userlist.remove(gameManager.userlist.get(nCount));				

				JSONObject obj = new JSONObject();

				obj.put("cmd","reserve");				
				obj.put("seat", user.seat);
				gameManager.sendRoom(obj);				
			}
		}		
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
			System.out.println(e.getMessage());
			e.printStackTrace();
		}        
	}	 
}
