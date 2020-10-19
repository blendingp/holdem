package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.json.simple.JSONObject;
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
			if( r.GetRoomByKey(roomkey) != null)
			{
				return r;
			}				
		}
		return null;
	}	

	public Room find(int roomnumber){		
		System.out.println( "size : " + roomList.size());

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
		}
		System.out.println(roomcount);

		room.join(user, roomcount);
		roomList.add(room);
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
			r.checkStartGame();
		}
	}

	void checkTimerGame(){
		for( Room r : roomList){
			r.checkTimerGame();
		}
	}
	
	public void onMessage( JSONObject obj){
		
		/*if( (""+obj.get("cmd")).compareTo("joinroom") ==0){
			joinRoom( ""+obj.get("roomNum") ,""+ obj.get("uidx")  );
		}*/
	}
	
}
