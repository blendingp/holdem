package egovframework.example.sample.admin;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.example.sample.web.SocketHandler;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
@RequestMapping("/cardtest")
public class CardTestController {

	public static int jokbotest = 0;
	public static int []cards=new int[52];
	
	@RequestMapping("/main.do")
	public String aiSetting(HttpServletRequest request , ModelMap model) {
		
		return "admin/cardtestmain";
	}

	@ResponseBody
	@RequestMapping(value = "/change.do", produces="application/json; charset=utf8;")
	public String changeUserSet(HttpServletRequest request)throws Exception {
		String mode = ""+request.getParameter("mode");
		try {
			CardTestController.jokbotest = Integer.parseInt(""+mode);
			String []cs = (""+request.getParameter("cards")).split(",");
			
			for(int i=0 ; i < cards.length ; i++)
			{
				cards[i]=Integer.parseInt(cs[i]);
			}
		}catch(Exception e) 
		{
			System.out.println("cardtest err");
		}

		JSONObject obj = new JSONObject();
		obj.put("result", "ok");
		return obj.toJSONString();
	}

	
	
}

