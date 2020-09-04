package egovframework.example.sample.web;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Resource;

import org.json.simple.JSONObject;
import org.springframework.web.socket.WebSocketSession;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class User {
	public int uidx;
	public boolean use=false;
	public String nickname;
	public int seat = -1;
	public int roomnum= -1;
	public int betmoney = 0;
	public int balance = 0;
	public int point = 0;
	public int safe_point = 0;
	public int safe_balance = 0;
	public int cash = 0;
	public int budget = 0;
	public boolean die = false;//true일떄 다이인 상태
	public int currentGuBetMoney=0;//현재 구 에 베팅한 머니 / 모든 유저가  이 머니가 같아야 다음  단계로 넘어감.
	public String img;
	public WebSocketSession session;
	String gamestat="";
	int level = 1000;
	int topcard = -1;
	Card card1=new Card(-1);//아직카드없음
	Card card2=new Card(-1);
	
	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;
	
	public void init(){
		betmoney = 0;
		currentGuBetMoney=0;//현재 구 에 베팅한 머니 / 모든 유저가  이 머니가 같아야 다음  단계로 넘어감.
		die = false;
	}
	public User(int uidx, WebSocketSession session, String userid){
		System.out.println("user객체생성 dbg1");
		this.uidx = uidx;
		this.nickname = userid;
		this.balance = 1100;		
		this.session = session;
		System.out.println("user객체생성 dbg2");
					      
		EgovMap in = new EgovMap();
		in.put("midx", uidx);				
		ArrayList<EgovMap> ed = (ArrayList<EgovMap>)SocketHandler.sk.sampleDAO.list("SelectUserItem", in);		
		
		if(ed==null){
			System.out.println("실패");			
		}
		else			
		{						
			for( int nCount =0; nCount < ed.size(); ++nCount )
			{												
				if( ed.get(nCount).get("type").toString().equals("balance") == true )
				{					
					this.balance = (int)ed.get(nCount).get("amount");					
				}
				else if( ed.get(nCount).get("type").toString().equals("cash") == true )
				{
					this.cash = (int)ed.get(nCount).get("amount");				
				}
				else if( ed.get(nCount).get("type").toString().equals("budget") == true )
				{
					this.budget = (int)ed.get(nCount).get("amount");				
				}
				else if( ed.get(nCount).get("type").toString().equals("point") == true )
				{
					this.point = (int)ed.get(nCount).get("amount");				
				}
				else if( ed.get(nCount).get("type").toString().equals("safe_balance") == true )
				{
					this.safe_balance = (int)ed.get(nCount).get("amount");				
				}
				else if( ed.get(nCount).get("type").toString().equals("safe_point") == true )
				{
					this.safe_point = (int)ed.get(nCount).get("amount");				
				}
			}
		}
		
		Random random = new Random();		
		this.img = "Character"+(random.nextInt(4)+1);
	}
	
	private Object find(WebSocketSession session2) {
		// TODO Auto-generated method stub
		return null;
	}
	public void setGameStat(String gamestat){
		this.gamestat=gamestat;
	}
	
	public String getGameStat(){
		return gamestat;
	}
	
	public int buyItem(String product, String receipt)
	{		
		/*
		 * 품목을 관리할수 있는게 없는 관계로 일단 하드 코딩으로 대체함... 
		 * 
		 * */
		
		System.out.println(product);
		System.out.println(receipt);
		
		EgovMap in = new EgovMap();
		in.put("midx", uidx);		
		
		switch(product)
		{
		case "Gem0": 
			this.cash += 110;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;		
		case "Gem1": 
			this.cash += 220;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;
		case "Gem2": 
			this.cash += 330;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;
		case "Gem3": 
			this.cash += 440;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;
		case "Gem4": 
			this.cash += 550;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;
		case "Gem5": 
			this.cash += 660;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;
		case "Gem6": 
			this.cash += 770;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;
		case "Gem7": 
			this.cash += 880;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;
		default: 
			this.cash += 0;
			in.put("amount", this.cash);
			in.put("type", "cash");
			break;
		}				
		int rt = SocketHandler.sk.sampleDAO.update("updateItemAmont", in);		
					
		return rt;
	}
	
	public int Deal(int item, int action, int amount)
	{
		EgovMap[] in = new EgovMap[2];
		in[0] = new EgovMap();
		in[1] = new EgovMap();
		in[0].put("midx", uidx);
		in[1].put("midx", uidx);
		
		int[] rt = new int[2];
		
		if( item == 0 )
		{
			if( action == 0 )
			{
				if( this.point < amount )
				{
					return 0;					
				}
				
				this.point -= amount;
				this.safe_point += amount;				
			}
			else				
			{
				if( this.safe_point < amount )
				{
					return 0;					
				}
				
				this.safe_point -= amount;
				this.point += amount;
			}				
			
			in[0].put("amount", this.point);
			in[0].put("type", "point");
			in[1].put("amount", this.safe_point);
			in[1].put("type", "safe_point");
			
			rt[0] = SocketHandler.sk.sampleDAO.update("updateItemAmont", in[0]);				
			rt[1] = SocketHandler.sk.sampleDAO.update("updateItemAmont", in[1]);
		}
		else
		{			
			if( action == 0 )
			{
				if( this.balance < amount )
				{
					return 0;					
				}
				
				this.balance -= amount;
				this.safe_balance += amount;
			}
			else				
			{
				if( this.safe_balance < amount )
				{
					return 0;					
				}
				
				this.safe_balance -= amount;
				this.balance += amount;
			}
			
			in[0].put("amount", this.balance);
			in[0].put("type", "balance");
			in[1].put("amount", this.safe_balance);
			in[1].put("type", "safe_balance");
			
			rt[0] = SocketHandler.sk.sampleDAO.update("updateItemAmont", in[0]);				
			rt[1] = SocketHandler.sk.sampleDAO.update("updateItemAmont", in[1]);
		}
				
		return rt[0]&rt[1];		
	}
	
	public int Beg()
	{
		this.balance = 500;
		return 1;
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
		roomnum = -1;
		betmoney = 0;		
		gamestat="";
		
		card1.clear();
		card2.clear();
	}
}
