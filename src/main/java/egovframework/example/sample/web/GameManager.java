package egovframework.example.sample.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.socket.TextMessage;

public class GameManager {		
	public ArrayList<User> userlist = new ArrayList<User>();
	public CardManager cardManager = new CardManager();

	String Game="대기";
	//Calendar startTime;
	long timerStartTime = 0;//객체로 만들면 좋다
	int whosturn = 0;
	int whosturnUseridx;
	int totalmoney = 0;
	int callmoney = 0;
	int startTime;
	
	Card card1;
	Card card2;
	Card card3;
	Card card4;
	Card card5;
	
	Room room;
	public GameManager(Room room){
		this.room = room;
	}
	

	void startCheck(User user, ArrayList<User> userlist){
		user.setGameStat("OK");
		int cnt = 0;
		for(User u : userlist){
			if(u.getGameStat()=="OK"){
				cnt++;
			}
		}
		if(cnt>=2) {			
			startTime = SocketHandler.second;
			Game="checkstart";
			//쓰레드에서 삼초뒤에 시작
			//notifyGameStart();
		}
		
	}
	
	void checkStartGame(){
		if(Game.compareTo("checkstart")==0){
			notifyGameStart();
			
			if(SocketHandler.second-startTime>3){
				Game="twoCard";
				startTime = SocketHandler.second; 
				drawCard();
				whosturnUseridx = userlist.get(whosturn).uidx;
			}
 		}
		
		if(Game.compareTo("twoCard")==0){			
			if(SocketHandler.second-startTime>1){
				Game="showBetPan";
				showBetPan();
			}
		}
	}


	
	public void notifyGameStart() {
		
		for(User u : userlist){
			JSONObject obj = new JSONObject();						
			//방접속자에게 보냄
			obj.put("cmd","startGame");
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	
	public void drawCard(){
			cardManager.shuffleCard();
			
			JSONObject obj = new JSONObject();
			//JSONArray j = new JSONArray();
			//카드 두장씩 세팅
			for(int k =0; k<userlist.size(); k++){
				userlist.get(k).setCard(cardManager.popCard(), cardManager.popCard());
				//JSONObject item = new JSONObject();
				obj.put("cmd", "giveTwoCard");
				obj.put("useridx", userlist.get(k).uidx);
				obj.put("card1", userlist.get(k).card1.cardcode);
				obj.put("card2", userlist.get(k).card2.cardcode);
				//j.add(item);
				
				try {
					userlist.get(k).session.sendMessage(new TextMessage(obj.toJSONString()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			//obj.put("twocardlist", j);
			
	}
	
	public void giveTwoCard() {
		for(User u : userlist){
			JSONObject obj = new JSONObject();						
			//방참여자에게 보냄
			obj.put("useridx","참여자인덱스");
			obj.put("protocol","RoomJoinOk");
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	/*	//함수 분할
	int ct=0;
	public void GiveTowCard(User u){
		u.setGameStat("twoCard");
		
		if(ct==0){
			for(User s:userlist)ct+=s.getGameStat("twoCard");			
		}
		
		//6대신 함수명을써라
		if(ct>=6){//여기서 6은 게임참여자수를 의미함
			if(whosturn<6){
				whosturn++;
				shareCard();
			}
			else{
				//배팅시작
				PostbettingStart();
			}
		}
		
	}
		
	public void shareCard(){
		for(User u : userlist){
			Card card1 = cardManager.popCard();
			Card card2 = cardManager.popCard();
			u.setCard(card1, card2);
		}

		JSONObject obj = new JSONObject();						
		//방접속자에게 보냄
		obj.put("protocol","giveTowCardOK");
		//0번쨰 카드 두장 담아서
		try {
			userlist.get(0).session.sendMessage(new TextMessage(obj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	*/
	public void showBetPan(){
		JSONObject obj = new JSONObject();
		obj.put("cmd","bet");
		obj.put("whosturn",userlist.get(whosturn).uidx);
		try {
			userlist.get(whosturn).session.sendMessage(new TextMessage(obj.toJSONString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public int thisTurnMoneyCompute(int kind){
		
		if(kind==0)
			return totalmoney;
		else if(kind==1)
			return room.defaultmoney;
		else if(kind==2)
			return room.defaultmoney;
		else
			return 1;
		
	}
	public void bet(int roomidx, User u, int betkind){	
		System.out.println("whosturnUseridx:"+whosturnUseridx +" uidx:"+u.uidx);
		if(whosturnUseridx != u.uidx)
			return;
		System.out.println("dbg BET1");
		whosturn++;//다음사람배팅		
		System.out.println("dbg BET2 whosturn: "+whosturn+"Game:"+Game);
		if(whosturn == userlist.size()){
			if(Game.compareTo("showBetPan")==0)			
				showThreeCard();
			else if(Game.compareTo("THEFLOP")==0)			
				TheTurn();
			else if(Game.compareTo("THETURN")==0)			
				TheRiver();
			else if(Game.compareTo("THERIVER")==0)			
				TheEnd();
			return;
		}

		System.out.println("dbg BET3");
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;
		u.betmoney +=  thisTurnMoneyCompute(betkind);//나의 배팅금액 현재돈+배팅금액
		callmoney = u.betmoney; //이전 사람의 배팅금액 
		totalmoney += thisTurnMoneyCompute(betkind);
		//배팅한 사람 돈 차감 시키기!!!
		u.balance -= thisTurnMoneyCompute(betkind);
		JSONObject obj = new JSONObject();
		obj.put("cmd", "betsuc");
		obj.put("totalmoney", totalmoney);
		obj.put("callmoney", callmoney);
		obj.put("myBetMoney", thisTurnMoneyCompute(betkind));
		obj.put("balance", u.balance);
		obj.put("seat", u.seat);//금방배팅한 사람
		obj.put("whosturnUseridx", whosturnUseridx);//이제 배팅할 사람의 인덱스
		
		for(User uu : userlist){
			try {
				uu.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void showThreeCard(){		
		whosturn=0;
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;
		System.out.println("showThreeCard whosturn:"+whosturn);
		JSONObject obj = new JSONObject();		
		Game = "THEFLOP";		
		card1=cardManager.popCard();
		card2=cardManager.popCard();
		card3=cardManager.popCard();
		
		obj.put("cmd","THEFLOP");
		obj.put("card1", card1.cardcode);
		obj.put("card2", card2.cardcode);		
		obj.put("card3", card3.cardcode);
		obj.put("whosturn",userlist.get(whosturn).uidx);
		
		for(User u : userlist){		
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void TheTurn(){
		whosturn=0;
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;
		
		JSONObject obj = new JSONObject();
		Game = "THETURN";
		
		card4=cardManager.popCard();
		
		obj.put("cmd","THETURN");
		obj.put("card4", card4.cardcode);
		obj.put("whosturn",userlist.get(whosturn).uidx);
		
		for(User u : userlist){		
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void TheRiver(){
		System.out.println("theriver================ 마지막 카드 공개");
		whosturn=0;
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;
		
		JSONObject obj = new JSONObject();
		Game = "THERIVER";
		
		card5=cardManager.popCard();		
		obj.put("cmd","THERIVER");
		obj.put("card5", card5.cardcode);
		obj.put("whosturn",userlist.get(whosturn).uidx);

		for(User u : userlist){		
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void TheEnd(){
		System.out.println("TheEnd================ ");
		whosturn=0;
		whosturnUseridx = userlist.get(whosturn%userlist.size()).uidx;
		
		JSONObject obj = new JSONObject();
		Game = "THEEND";				
		obj.put("cmd","THEEND");
		
		for(User u : userlist){		
			try {
				u.session.sendMessage(new TextMessage(obj.toJSONString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}	
	public void showResult(){
		whosturn=0;
		JSONObject obj = new JSONObject();
		obj.put("protocol","showResult");
		Game = "THEEND";
	}
}
