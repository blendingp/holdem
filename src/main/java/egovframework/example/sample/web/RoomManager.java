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

	void joinRoom(User user, String roomkey){
		Room room = findbykey(roomkey);
		if( room == null )
		{
			++roomcount;
			room = new Room(roomcount, roomkey);			
			roomList.add(room);
		}		

		room.join(user, roomcount);		
	}

	void GoldRoomQuickJoin(User user)
	{
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
		Room room = find(number);
		if( room == null )
		{
			return false;	
		}	
		if( room.fullRoom() == true )	
		{
			return false;
		}

		room.join(user, roomcount);

		return true;
	}

	boolean CreateRoom(User user, CreateRoom roominfo)
	{
		if( user.balance < roominfo.setting.ante * 3 )
		{
			return false;
		}

		++roomcount;
		Room room = new Room(roomcount, roominfo.roomkey);			
		room.SetGoldRoom(roominfo.setting);
		
		room.join(user, roomcount);
		roomList.add(room);
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

		try {
			session.sendMessage(new TextMessage(mapper.writeValueAsString(cobj)));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	void leaveRoom(int roomNum , User user){			
		Room room = find(user.roomnum);	
	
		room.notifyLeaveUser(user.seat);
		room.leave(user);		
		if( room.gameManager.userlist.size() <= 0 )
		{
			roomList.remove(room);
		}

		System.out.println("<< leave user >>");
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

	void checkTimerGame(){
		for( Room r : roomList){
			if( r == null )
			{
				continue;
			}
			
			r.checkTimerGame();
		}
	}
	
	public void onMessage( JSONObject obj){
		
		/*if( (""+obj.get("cmd")).compareTo("joinroom") ==0){
			joinRoom( ""+obj.get("roomNum") ,""+ obj.get("uidx")  );
		}*/
	}
	
}
