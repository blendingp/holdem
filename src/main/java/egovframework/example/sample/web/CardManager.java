package egovframework.example.sample.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.web.socket.WebSocketSession;

public class CardManager {
	ArrayList<Card> cardlist = new ArrayList<Card>();
	int popcard = 0;
	public CardManager(){				
			
		for(int i = 0;i < 52; ++i)
			cardlist.add(new Card(i));
		
		Collections.shuffle(cardlist);
		for(int i = 0;i < 52; ++i)
		System.out.println("card:");
	}
	
	public Card shuffleCard(){
		Collections.shuffle(cardlist);
		
		Card c = new Card(0);
		return c;
	}	
	
	public Card popCard(){
		return cardlist.get(popcard++);
	}
	
	
}
