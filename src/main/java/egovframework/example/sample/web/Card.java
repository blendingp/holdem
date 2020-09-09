package egovframework.example.sample.web;

import java.util.ArrayList;

import org.springframework.web.socket.WebSocketSession;


public class Card {	
	
	public int cardcode = 0;
	
	public  Card(int cardcode){
		this.cardcode = cardcode;
	}
	
	public int CardShape(){
		return (int)(cardcode/13);
	}
	
	public int CardNum(){
		return cardcode%13;
	}
	
	public void clear()
	{
		this.cardcode = -1;
	}
}
