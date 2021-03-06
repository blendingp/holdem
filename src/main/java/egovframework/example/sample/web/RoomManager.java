package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class RoomManager {
	ArrayList<Room> roomList = new ArrayList<Room>();
	int roomcount = 0;
	public  RoomManager(){
		/*for(int k=0; k<roomcount; k++){
			Room room = new Room(k, 10);
			roomList.add(room);
		}*/
	}
		
	public Room findbykey(String roomkey){		
		for( Room r : roomList){
			if( r.fullRoom() == false && r.GetRoomByKey(roomkey) != null)
			{
				return r;
			}				
		}
		return null;
	}	

	public Room find(int roomnumber){		
		for( Room r : roomList){
			if( r.GetRoomByNumber(roomnumber) != null)
			{
				return r;
			}				
		}
		return null;
	}	

	void joinRoom(User user, String roomkey)
	{
		boolean isPass = true;
		if( user.IsAuth() == false )
		{
			ObjectMapper mapper = new ObjectMapper();

			JSONObject cobj = new JSONObject();
			cobj.put("cmd", "auth");
			cobj.put("midx", user.uidx);
			cobj.put("result", false);

			try {
				if(User.CheckSendPacket(user) == true)
				{
					user.session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
				}				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

			isPass = false;
			return ;
		}
		if( Room.GetRoomInfo(roomkey).useitem.equals("balance") == true){
			if( user.balance < Room.GetRoomInfo(roomkey).ante * 100 ){
				isPass = false;				
			}
		}
		else if( Room.GetRoomInfo(roomkey).useitem.equals("point") == true){
			if( user.point < Room.GetRoomInfo(roomkey).ante * 100 ){
				isPass = false;
			}
		}			

		Room room = findbykey(roomkey);
		if( room == null && isPass == true)
		{
			++roomcount;
			room = new Room(roomcount, roomkey);			
			roomList.add(room);
		}		
		else if( room != null )
		{
			if( room.isPrivate() == true)
			{
				if( Room.GetRoomInfo(roomkey).useitem.equals("balance") == true){
					if( user.balance > 0 ){
						isPass = true;
					}
				}
				else if( Room.GetRoomInfo(roomkey).useitem.equals("point") == true){
					if( user.point > 0 ){
						isPass = true;
					}
				}	
			}
		}

		if( isPass == false )
		{
			return ;
		}
		room.join(user, room.ridx);		
	}

	void GoldRoomQuickJoin(User user)
	{
		if( user.IsAuth() == false )
		{
			ObjectMapper mapper = new ObjectMapper();

			JSONObject cobj = new JSONObject();
			cobj.put("cmd", "auth");
			cobj.put("midx", user.uidx);
			cobj.put("result", false);

			try {
				if(User.CheckSendPacket(user) == true)
				{
					user.session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
				}				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

			return ;
		}

		if( Room.GetRoomInfo("goldroom10").useitem.equals("balance") == true){
			if( user.balance < Room.GetRoomInfo("goldroom10").ante * 100 ){
				return ;
			}
		}
		else if( Room.GetRoomInfo("goldroom10").useitem.equals("point") == true){
			if( user.point < Room.GetRoomInfo("goldroom10").ante * 100 ){
				return ;
			}
		}
		
		ArrayList<Room> list = new ArrayList<>();
		for( Room room : roomList )
		{
			if(room.GetRoomInfoByKey("goldroom") != null && room.fullRoom() == false)
			{
				list.add(room);
			}
		}

		Collections.sort(roomList, new Comparator<Room>() {
			@Override public int compare(Room s1, Room s2) {
				return s1.gameManager.userlist.size() - s2.gameManager.userlist.size();
			}
		});

		if( list.size() > 0 )
		{
			for( Room room : list )
			{
				if(room.join(user, room.ridx) == true)
				{
					break;
				}
			}
		}
		else
		{
			CreateRoom roominfo = new CreateRoom();
			roominfo.roomkey = "goldroom10";
			roominfo.setting = new GoldRoom();
			roominfo.setting.ante = 10;
			roominfo.setting.maxbetvalue = 1000;			
			roominfo.setting.isprivate = false;
			roominfo.setting.maxplayer = 9;
			CreateRoom(user, roominfo);
		}

	}

	boolean JoinRoomByNumber(User user, int number){

		if( user.IsAuth() == false )
		{
			ObjectMapper mapper = new ObjectMapper();

			JSONObject cobj = new JSONObject();
			cobj.put("cmd", "auth");
			cobj.put("midx", user.uidx);
			cobj.put("result", false);

			try {
				if(User.CheckSendPacket(user) == true)
				{
					user.session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
				}				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		Room room = find(number);
		if( room == null )
		{
			return false;	
		}	
		if( room.fullRoom() == true )	
		{
			return false;
		}

		room.join(user, number);

		return true;
	}

	boolean CreateRoom(User user, CreateRoom roominfo)
	{
		if( user.IsAuth() == false )
		{
			ObjectMapper mapper = new ObjectMapper();

			JSONObject cobj = new JSONObject();
			cobj.put("cmd", "auth");
			cobj.put("midx", user.uidx);
			cobj.put("result", false);

			try {
				if(User.CheckSendPacket(user) == true)
				{
					user.session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
				}				
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		if( roominfo.setting.isprivate == false && user.balance < roominfo.setting.ante * 100)
		{			
			return false;
		}		

		if( roominfo.setting.isprivate == true && user.balance <= 0)
		{			
			return false;
		}		

		++roomcount;
		Room room = new Room(roomcount, roominfo.roomkey);			
		room.SetGoldRoom(roominfo.setting);
		
		room.join(user, roomcount);
		roomList.add(room);
		if( room.isPrivate() == true) 
		{
			user.NotifyFriend(room.ridx);			
		}

		return true;
	}

	void GetRoomList(WebSocketSession session)
	{
		ArrayList<RoomInfo> list = new ArrayList<>();		
		for( Room room : roomList )
		{
			if(room.GetRoomInfoByKey("goldroom") != null)
			{
				list.add(room.GetRoomInfoByKey("goldroom"));
			}
		}

		ObjectMapper mapper = new ObjectMapper();

		JSONObject cobj = new JSONObject();
		cobj.put("cmd", "roomlist");
		cobj.put("list", list);

		if( session == null )
		{
			return;
		}

		if( session.isOpen()==false )
		{
			return;			
		}

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	void leaveRoom(int roomNum , User user){		
		Room room = find(user.roomnum);		
		if( room == null )
			return;
		room.leave(user);	
	}
	
	void checkStartGame(){
		for( Room r : roomList){
			if( r == null )
			{
				continue;
			}
			r.checkStartGame();
		}
	}

	User findFromUseridx(int midx) {
		for( Room r : roomList){
			for(User u:r.gameManager.userlist)
				if(u.uidx == midx)
					return u;
			for(User u:r.gameManager.watchinguserlist)
				if(u.uidx == midx)
					return u;
		}
		return null;
	}
	
	void checkTimerGame(){
		ArrayList<Room> removeList=new ArrayList<Room>();
		for( Room r : roomList){
			if( r == null )
			{
				continue;
			}
			
			r.checkTimerGame();
			if( r.gameManager.userlist.size() + r.gameManager.watchinguserlist.size() <= 0 )
			{
				removeList.add(r);
			}
		}
		roomList.removeAll(removeList);
	}
	
	void checkErrorGamingRoom() {
		for( Room r : roomList){
			if( r == null )
			{
				continue;
			}
			
			r.checkErrorGamingRoom();
		}
	}
	
	public void onMessage( JSONObject obj){
		
		/*if( (""+obj.get("cmd")).compareTo("joinroom") ==0){
			joinRoom( ""+obj.get("roomNum") ,""+ obj.get("uidx")  );
		}*/
	}
	
}
