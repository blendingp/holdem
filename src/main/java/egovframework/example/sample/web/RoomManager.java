package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.web.socket.WebSocketSession;

public class RoomManager {
	ArrayList<Room> roomList = new ArrayList<Room>();
	int roomcount = 20;
	public  RoomManager(){
		for(int k=0; k<roomcount; k++){
			Room room = new Room(k, 1000);
			roomList.add(room);
		}
	}
	
	
	public Room find(int ridx){		
		for( Room r : roomList){
			if( r.ridx ==ridx)
				return r;
		}
		return null;
	}	


	void joinRoom(int roomNum , User user){
		Room room = find(roomNum);
		room.join(user, roomNum);
	}
	
	void leaveRoom(int roomNum , User user){
		Room room = find(roomNum);
		
		room.leave(user);
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
