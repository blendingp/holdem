package egovframework.example.sample.web.model;

import org.springframework.web.socket.WebSocketSession;

public class UserMsg {
	public UserMsg(WebSocketSession tsession,String tmsg) 
	{
		session = tsession;
		msg=tmsg;
	}
	public WebSocketSession session;
	public String msg;
}
