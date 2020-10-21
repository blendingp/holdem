package egovframework.example.sample.web;

import org.json.simple.JSONObject;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class ProfileManager {
	static JSONObject getInfo(User u)
	{
		try {
			EgovMap in=new EgovMap();
			in.put("idx", ""+u.uidx );
			EgovMap rt = (EgovMap)SocketHandler.sk.sampleDAO.select( "profileGet" , in );
			
			u.profile.members = Integer.parseInt(""+ rt.get("members") );
			u.profile.expire.setTime( Integer.parseInt(""+rt.get("expire"))  );
			u.profile.totalgame= Integer.parseInt(""+rt.get("totalgame")  );
			u.profile.totalwin= Integer.parseInt(""+rt.get("totalwin")  );
			u.profile.totallose= Integer.parseInt(""+rt.get("totallose")  );
			u.profile.putallin= Integer.parseInt(""+rt.get("allin")  );
			u.profile.todaygame=Integer.parseInt( ""+rt.get("todaygame")  );
			u.profile.todaywin=Integer.parseInt( ""+rt.get("todaywin")  );
			u.profile.todaylose= Integer.parseInt(""+rt.get("todaylose")  );
			u.profile.chiprefillcount= Integer.parseInt(""+rt.get("chiprefillcount")  );
			u.profile.goldrefillcount= Integer.parseInt(""+rt.get("goldrefillcount")  );
		}catch(Exception e)
		{
			System.out.println("ProfileManager getInfo error"+e.toString() );
		}
		
		JSONObject obj=new JSONObject();
		obj.put("cmd", "profile" );
		obj.put("point", ""+u.point); // ""+rt.get("point")  );
		obj.put("balance", ""+u.balance); // +rt.get("balance")  );
		obj.put("members", ""+u.profile.members  );
		obj.put("expire", ""+u.profile.expire.getTime() );
		obj.put("totalgame", ""+u.profile.totalwin  );
		obj.put("totalwin", ""+u.profile.totallose  );
		obj.put("totallose", ""+u.profile.putallin  );
		obj.put("putallin", ""+u.profile.putallin  );
		obj.put("todaygame", ""+u.profile.todaygame  );
		obj.put("todaywin", ""+u.profile.todaylose );
		obj.put("todaylose", ""+u.profile.todaylose  );
		obj.put("chiprefillcount", ""+u.profile.chiprefillcount );
		obj.put("goldrefillcount", ""+u.profile.goldrefillcount );		
		switch(u.profile.members) {
		case 0:		
			obj.put("limitbalance","300조");
			obj.put("limitpoint", "500만");
			break;
		case 1:		
			obj.put("limitbalance","500조");
			obj.put("limitpoint", "1000만");
			break;
		case 2:		
			obj.put("limitbalance","1000조");
			obj.put("limitpoint", "2000만");
			break;
		case 3:		
			obj.put("limitbalance","1500조");
			obj.put("limitpoint", "6000만");
			break;
		default :
			obj.put("limitbalance","300조");
			obj.put("limitpoint", "500만");
		}
		return obj;
	}
}
