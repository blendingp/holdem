package egovframework.example.sample.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.web.socket.WebSocketSession;

import egovframework.example.sample.admin.CardTestController;

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
		switch( CardTestController.jokbotest ){
		case 0:
		case -1:Collections.shuffle(cardlist);
			break;
		case 1:{
			for(int i=0;i<CardTestController.cards.length;i++)
			{
				cv(CardTestController.cards[i]/13,CardTestController.cards[i]%13);
			}
			}break;
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
