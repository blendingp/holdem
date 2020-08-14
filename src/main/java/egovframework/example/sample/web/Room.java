package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;

public class Room {
	//ArrayList<User> userlist = new ArrayList<User>();
	//0~8
	public int[] seats = new int[9];


	GameManager gameManager;

	int ridx;
	int defaultmoney;//삥머니
	int maxmoney;//배팅 맥스 머니

	public Room(int ridx, int defaultmoney){
		this.ridx = ridx;
		this.defaultmoney = defaultmoney;
		this.maxmoney = defaultmoney * 10000;
		Arrays.fill(seats, -1);		
		gameManager = new GameManager(this);
	}
	public void init(){
		Arrays.fill(seats, -1);		
		gameManager.init();
	}

	public boolean fullRoom(int ridx){
		this.ridx = ridx;
		return true;
	}
	public boolean emptyRoom(){
		if( gameManager.userlist.size() == 0)
			return true;
		return false;
	}

	public void notifyJoinUser() {

		for(User u : gameManager.userlist){
			JSONObject myobj = new JSONObject();						
			myobj.put("cmd","RoomJoinOk");
			myobj.put("roomidx",ridx);
			myobj.put("useridx",u.uidx);//참여자인덱스
			myobj.put("seat",u.seat);
			
			//방에 참여중인 모든 사람 불러오기
			JSONArray j = new JSONArray();
			for(int i=0; i<gameManager.userlist.size(); i++)
			{
				JSONObject item = new JSONObject();
				item.put("useridx",""+ gameManager.userlist.get(i).uidx);
				item.put("seat",""+ gameManager.userlist.get(i).seat);
				item.put("img",""+ gameManager.userlist.get(i).img);
				item.put("balance",""+ gameManager.userlist.get(i).balance);
				j.add(item);
			}
			myobj.put("userlist", j);

			try {
				u.session.sendMessage(new TextMessage(myobj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();
			}

		}
	}

	public void notifyLeaveUser(int seat) {


		JSONObject myobj = new JSONObject();						
		myobj.put("cmd","RoomLeaveOk");
		myobj.put("seat",seat);
		//방에 참여중인 모든 사람 불러오기
		for(int i = 0; i<gameManager.userlist.size(); i++){


			try {
				gameManager.userlist.get(i).session.sendMessage(new TextMessage(myobj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();
			}
		}

	}




	public void join(User u, int ridx ) {
		gameManager.userlist.add( u );
		u.roomnum = ridx;
		for(int k=0; k<seats.length; k++){
			if(seats[k] == -1){
				seats[k] = u.uidx;
				u.seat = k;
				System.out.println(u.uidx+" 유저의 자리번호:"+u.seat);
				break;
			}
		}
		gameManager.startCheck(u, gameManager.userlist);
		notifyJoinUser();
	}

	public void leave(User u) {
		gameManager.userlist.remove(u);
		for(int k=0; k<seats.length; k++){
			if(seats[k] == u.uidx){

				seats[k] = -1;
				u.seat = -1;
				u.roomnum = -1;
				break;
			}
		}
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
}
