package egovframework.example.sample.web;

import java.util.ArrayList;

import org.springframework.web.socket.WebSocketSession;


public class Card {	
	
	int cardcode = 0;
	
	public  Card(int cardcode){
		this.cardcode = cardcode;
	}
	
	public String CardShape(int cardcode){
		return "";
	}
	
	public int CardNum(int cardcode){
		return 0;
	}
}
