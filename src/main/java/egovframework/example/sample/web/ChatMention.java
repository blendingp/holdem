package egovframework.example.sample.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class ChatMention {
	
	private SampleDAO sampleDAO;
	
	private final Logger logger = LogManager.getLogger(ChatMention.class);
	
	ArrayList<String> mention = new ArrayList<>();

	
	public void setDAO(SampleDAO dao) {
		sampleDAO = dao;
	}
	//구정연 _멘션 업로드
	public void uploding() {
		
		List<EgovMap> chatlist = (List<EgovMap>) sampleDAO.list("ChatList");
	
		for (int i = 0; i < chatlist.size(); i++) {
			
			mention.add(chatlist.get(i).get("cmention")+"");
			
			System.out.println("cmention :" + mention);
		}
	}
	//구정연_멘션 스트링으로 반환
	public String getChatMentionString() {
		
		String chatMention = "";
		
		for (int i = 0; i < mention.size(); i++) {
			
			String me = mention.get(i);
			
			if(chatMention.equals("")){
				chatMention += me;
			}else{
				chatMention += "," +  me;
			}
		}
		System.out.println("chatMention :" + chatMention);
		return chatMention;
	}

}
