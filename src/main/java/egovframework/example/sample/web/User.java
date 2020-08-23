package egovframework.example.sample.web;

import java.util.Random;

import org.springframework.web.socket.WebSocketSession;

public class User {
	public int uidx;
	public boolean use=false;
	public String nickname;
	public int seat = -1;
	public int roomnum= -1;
	public int betmoney = 0;
	public int balance = 0;
	public boolean die = false;//true일떄 다이인 상태
	public int currentGuBetMoney=0;//현재 구 에 베팅한 머니 / 모든 유저가  이 머니가 같아야 다음  단계로 넘어감.
	public String img;
	public WebSocketSession session;
	String gamestat="";
	int level = 1000;
	int topcard = -1;
	Card card1=new Card(-1);//아직카드없음
	Card card2=new Card(-1);
	public void init(){
		betmoney = 0;
		currentGuBetMoney=0;//현재 구 에 베팅한 머니 / 모든 유저가  이 머니가 같아야 다음  단계로 넘어감.
		die = false;
	}
	public User(int uidx, WebSocketSession session, String userid){
		System.out.println("user객체생성 dbg1");
		this.uidx = uidx;
		this.nickname = userid;
		this.balance = 1000;
		this.session = session;
		System.out.println("user객체생성 dbg2");
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
