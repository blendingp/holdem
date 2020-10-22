package egovframework.example.sample.web;

import java.io.IOException;

import javax.json.JsonObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.json.simple.JSONObject;

import egovframework.example.sample.web.model.ProfileModel;
import egovframework.rte.psl.dataaccess.util.EgovMap;

public class ProfileManager {

	public static ProfileModel GetTotalInfo(int midx) {
		EgovMap in = new EgovMap();
		in.put("midx", midx);
		EgovMap result = (EgovMap) SocketHandler.sk.sampleDAO.select("GetProfileData", in);

		ProfileModel profile = null;

		if (result != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				profile = mapper.readValue(mapper.writeValueAsBytes(result), ProfileModel.class);
			} catch (JsonParseException e) {	
				System.out.println(e.getMessage());			
				e.printStackTrace();
			} catch (JsonMappingException e) {				
				System.out.println(e.getMessage());			
				e.printStackTrace();
			} catch (JsonProcessingException e) {				
				System.out.println(e.getMessage());			
				e.printStackTrace();
			} catch (IOException e) {				
				System.out.println(e.getMessage());			
				e.printStackTrace();
			}
		}
		else
		{
			profile = new ProfileModel();
			profile.midx = midx;
			UpdateProfile(profile);
		}		

		return profile;
	}

	public static ProfileModel GetTodayInfo(int midx) {
		EgovMap in = new EgovMap();
		in.put("midx", midx);
		EgovMap result = (EgovMap) SocketHandler.sk.sampleDAO.select("GetTodayProfileData", in);

		ProfileModel profile = null;

		if (result != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				profile = mapper.readValue(mapper.writeValueAsBytes(result), ProfileModel.class);
			} catch (JsonParseException e) {	
				System.out.println(e.getMessage());			
				e.printStackTrace();
			} catch (JsonMappingException e) {				
				System.out.println(e.getMessage());			
				e.printStackTrace();
			} catch (JsonProcessingException e) {				
				System.out.println(e.getMessage());			
				e.printStackTrace();
			} catch (IOException e) {				
				System.out.println(e.getMessage());			
				e.printStackTrace();
			}
		}
		else
		{
			profile = new ProfileModel();
			profile.midx = midx;
			UpdateTodayProfile(profile);
		}		

		return profile;
	}	

	public static Boolean UpdateProfile(ProfileModel model)
	{				
		try {
			ObjectMapper mapper = new ObjectMapper();
			String value = mapper.writeValueAsString(model);
			System.out.println(value);
			EgovMap jsonobject = mapper.readValue(value, EgovMap.class);
			/*jsonobject.remove("goldrefillcount");
			jsonobject.remove("chiprefillcount");
			jsonobject.remove("expire");*/
			System.out.println(jsonobject.toString());

			SocketHandler.sk.sampleDAO.update("UpdateProfileData", jsonobject);
		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());			
			e.printStackTrace();
			return false;
		}		

		return true;
	}

	public static Boolean UpdateTodayProfile(ProfileModel model)
	{
		try {
			ObjectMapper mapper = new ObjectMapper();
			String value = mapper.writeValueAsString(model);
			System.out.println(value);
			EgovMap jsonobject = mapper.readValue(value, EgovMap.class);
			System.out.println(jsonobject.toString());

			SocketHandler.sk.sampleDAO.update("UpdateTodayProfileData", jsonobject);
		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());			
			e.printStackTrace();
			return false;
		}			

		return true;
	}
}
