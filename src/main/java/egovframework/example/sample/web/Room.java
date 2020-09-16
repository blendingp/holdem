package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;

public class Room {
	GameManager gameManager;

	int ridx;
	int defaultmoney;//삥머니
	int maxmoney;//배팅 맥스 머니

	public Room(int ridx, int defaultmoney){
		this.ridx = ridx;
		this.defaultmoney = defaultmoney;
		this.maxmoney = defaultmoney * 100;
		gameManager = new GameManager(this);
	}
	public void init(){
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
	public void notifyRoomUsers() {//notifyJoinUser아래와 거의 같으나 내가 안만들어서 어디서 쓰이는지 불안해서 새로 만듬, 룸안의 유저가 변경되었을때 갱신하기 위해 쓰임.

		JSONObject myobj = new JSONObject();						
		myobj.put("cmd","RoomUsers");
		//방에 참여중인 모든 사람 불러오기
		JSONArray j = new JSONArray();
		System.out.println("notifyRoomUsers");
		for(int i=0; i<gameManager.userlist.size(); i++)
		{
			JSONObject item = new JSONObject();			
			item.put("useridx",""+ gameManager.userlist.get(i).uidx);
			item.put("seat",""+ gameManager.userlist.get(i).seat);
			item.put("img",""+ gameManager.userlist.get(i).img);
			item.put("balance",""+ gameManager.userlist.get(i).balance);
			item.put("nickname",""+ gameManager.userlist.get(i).nickname);
			j.add(item);
			System.out.println("uidx:"+gameManager.userlist.get(i).uidx +"  seat:"+gameManager.userlist.get(i).seat );
		}
		myobj.put("userlist", j);

		gameManager.sendRoom(myobj);
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
				item.put("nickname",""+ gameManager.userlist.get(i).nickname);
				j.add(item);
			}
			myobj.put("userlist", j);

			try {
				u.session.sendMessage(new TextMessage(myobj.toJSONString()));
			} catch (IOException e) {
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
		
		if( u.balance < 10 )
		{
			return ;
		}
		
		if( gameManager.GameMode == "대기" )
		{
			u.seat = gameManager.GetEmptySeat();		
			gameManager.SetSeat(u.seat);
			
			gameManager.userlist.add( u );
			u.roomnum = ridx;
			gameManager.startCheck(u, gameManager.userlist);
			notifyJoinUser();
			
			gameManager.setWorkTime( );//새로 한명 들어올때마다 대기 시간을 증가시켜서 여러명이 들어올 여지를 둔다.
		}
				
	}

	public void leave(User u) {		
		System.out.println(u.seat);
		gameManager.EmptySeat(u.seat);
		gameManager.userlist.remove(u);
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
}
