package egovframework.example.sample.web;

import org.json.simple.JSONObject;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class ProfileManager {
	static JSONObject getInfo(User u)
	{
		JSONObject obj=new JSONObject();
		EgovMap rt = (EgovMap)SocketHandler.sk.sampleDAO.select( "profileGet" , "" + u.uidx );
		
		String mtype = ""+rt.get("members");
		String mchiprefillcount = ""+rt.get("chiprefillcount");
		
		int imchiprefillcount = 3;
		int imtype =0;
		
		try
		{
			imtype=Integer.parseInt(mtype);
			imchiprefillcount = Integer.parseInt(mchiprefillcount);
		}catch(Exception e) 
		{
			System.out.println("ProfileManager getInfo error - type");
		}
		switch(imtype) {
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
		
		obj.put("cmd", "profile" );
		obj.put("point", ""+rt.get("point")  );
		obj.put("balance", ""+rt.get("balance")  );
		obj.put("members", ""+rt.get("members")  );
		obj.put("expire", ""+rt.get("expire")  );
		obj.put("totalgame", ""+rt.get("totalgame")  );
		obj.put("totalwin", ""+rt.get("totalwin")  );
		obj.put("totallose", ""+rt.get("totallose")  );
		obj.put("putallin", ""+rt.get("allin")  );
		obj.put("todaygame", ""+rt.get("todaygame")  );
		obj.put("todaywin", ""+rt.get("todaywin")  );
		obj.put("todaylose", ""+rt.get("todaylose")  );
		obj.put("chiprefillcount", ""+rt.get("chiprefillcount")  );
		obj.put("goldrefillcount", ""+rt.get("goldrefillcount")  );
		
		return obj;
	}
}
