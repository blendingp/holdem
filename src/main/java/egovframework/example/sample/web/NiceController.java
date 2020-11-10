package egovframework.example.sample.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;

@Controller
public class NiceController {
	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;
	
	@RequestMapping(value = "/join.do")
	public String join(HttpServletRequest request, ModelMap model) throws Exception {
		String midx = request.getParameter("midx");
		model.addAttribute("midx", midx);
		return "join";
	}
	
	//회원가입 핸드폰인증
	@RequestMapping(value="/identifyresult.do")
	public String identifyresult(HttpServletRequest request, ModelMap model) throws Exception
	{
		String midx = ""+request.getParameter("midx");
		model.addAttribute("midx", midx);
		return "joinbyphone";
	}
	
	@RequestMapping(value="/failresult.do")
	public String failresult(HttpServletRequest request, ModelMap model) throws Exception{
		return "fail";
	}	
	
	@ResponseBody
	@RequestMapping(value = "/insertAuth.do")
	public String insertAuth(HttpServletRequest request, ModelMap model) throws Exception {
		String midx = ""+1;//request.getParameter("midx");
		String birthDate = request.getParameter("birthDate");
		String uid = request.getParameter("uid");
		String autchTick = request.getParameter("autchTick");		
		System.out.println("birthDate:"+birthDate);
		
		EgovMap in = new EgovMap();
		in.put("midx", midx);
		in.put("birthDate", birthDate);
		in.put("uid", uid);
		in.put("autchTick", autchTick);
		sampleDAO.insert("insertAuthSelf",in);
		
		return "ok";
	}
}
