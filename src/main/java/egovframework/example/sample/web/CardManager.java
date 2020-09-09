package egovframework.example.sample.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.web.socket.WebSocketSession;

public class CardManager {
	ArrayList<Card> cardlist = new ArrayList<Card>();
	int popcard = 0;
	public void init(){
		popcard= 0;
	}
	public CardManager(){				
			
		for(int i = 0;i < 52; ++i)
			cardlist.add(new Card(i));
	}
	int tmidx=0;
	public void cv(int k,int n){
		if( k>3)k=3;
		if( n>12)n=12;
		cardlist.get(tmidx++).cardcode=k*13+n;
	}
	public void shuffleCard(){
		tmidx = 0;
		switch(SocketHandler.jokbotest){
		case 0:
		case -1:Collections.shuffle(cardlist);
			break;
		case 1://트리플 커뮤니티에 두장 깔고, 한장은 개인 카드로 주기 (첫번째 사람)
			cv(0,5);cv(1,8);
			cv(3,5);cv(2,2);
			cv(3,3);cv(0,7);
			cv(2,5);cv(1,5);cv(3,7);cv(3,10);cv(3,11);break;
		case 2://원페어   커뮤니티에 한장 깔고, 한장은 개인 카드로 주기 (첫번째 사람)
			cv(0,5);cv(0,7);
			cv(1,2);cv(1,3);
			cv(2,4);cv(2,5);
			cv(3,6);cv(3,7);cv(3,9);cv(3,11);cv(3,12);break;
		case 3://플러시   커뮤니티에 무늬 세장 깔고, 두장은 개인 카드로 주기 (첫번째 사람) // 현재 플러시 색안칠해짐.
			cv(0,5);cv(0,2);
			cv(1,2);cv(1,3);
			cv(2,4);cv(2,5);
			cv(0,6);cv(0,7);cv(0,9);cv(3,11);cv(3,12);break;
		case 4://스트레이트    커뮤니티에 2 3 4 ,  5 6 두장은 개인 카드로 주기 (첫번째 사람) // 안된다고함.
			cv(0,4);cv(0,5);
			cv(1,2);cv(1,3);
			cv(2,3);cv(2,5);
			cv(0,1);cv(1,2);cv(2,3);cv(3,11);cv(3,12);break;
		}
		
	}	
	
	public Card popCard(){
		return cardlist.get(popcard++);
	}
	
/*
	   ArrayList<Card> cardlist = new ArrayList<Card>();
	   int popcard = 0;
	   public void init(){
	      popcard= 0;
	   }
	   public CardManager(){            
	         
	      for(int i = 0;i < 52; ++i)
	         cardlist.add(new Card(i));
	   }
	   
	   public void shuffleCard(){
	      //Collections.shuffle(cardlist);
	   }   
	   
	   public Card popCard(){
	      return cardlist.get(popcard++);
	   }
	   public Card popCard(int cardcode){
	      return cardlist.get(cardcode);
	   }   
	   public String getCardShape(int cardcode){
	        String shape = "";
	        if (cardcode / 13 == 0)
	            shape = "clov-";
	        else if (cardcode / 13 == 1)
	            shape = "dia-";
	        else if (cardcode / 13 == 2)
	            shape = "heart-";
	        else if (cardcode / 13 == 3)
	            shape = "space-";
	        return shape;
	   }
	   
	    public int getCardNum(int cardcode)
	    {
	        int c = (cardcode % 13) + 1;
	        return c;
	    }   */
	
}
