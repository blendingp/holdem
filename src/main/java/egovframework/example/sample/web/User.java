package egovframework.example.sample.web;

import java.util.Random;

import org.springframework.web.socket.WebSocketSession;

public class User {
	public int uidx;
	public int seat = -1;
	public int betmoney = 0;
	public int balance = 0;
	
	public String img;
	public WebSocketSession session;
	String gamestat="";
	
	Card card1=new Card(-1);//아직카드없음
	Card card2=new Card(-1);
	
	public User(int uidx, WebSocketSession session){
		this.uidx = uidx;
		this.balance = 1000000;
		this.session = session;
		
		Random random = new Random();		
		this.img = "Character"+(random.nextInt(4)+1);
	}
	
	public void setGameStat(String gamestat){
		this.gamestat=gamestat;
	}
	
	public String getGameStat(){
		return gamestat;
	}

	public int getGameStat(String stat){
		if(getGameStat().compareTo(stat)==0)
			return 1;
		else return 0;
	}
	
	public void setCard(Card card1, Card card2){
		this.card1.cardcode = card1.cardcode;
		this.card2.cardcode =card2.cardcode;
	}
	
	public void clear()
	{
		seat = -1;
		betmoney = 0;
		balance = 0;
		gamestat="";
		
		card1.clear();
		card2.clear();
	}
}
