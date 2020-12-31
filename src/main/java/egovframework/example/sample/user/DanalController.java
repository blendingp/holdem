package egovframework.example.sample.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;

@Controller
@RequestMapping("/danal")
public class DanalController {
	@Resource(name="sampleDAO")
	SampleDAO sampleDAO;
	
	@RequestMapping(value = "/order.do")
	public String order(HttpServletRequest request, ModelMap model) throws Exception
	{
		HttpSession session = request.getSession();
		model.addAttribute("cash", request.getParameter("cash"));
		model.addAttribute("money", request.getParameter("money"));
		session.setAttribute("money", request.getParameter("money"));
		EgovMap info = (EgovMap)sampleDAO.select("selectinfo" , session.getAttribute("midx"));
		model.addAttribute("muserid", info.get("muserid"));
		model.addAttribute("nick", info.get("nickname"));
		return "/danal/Order";
	}
	@RequestMapping(value = "/cpcgi.do")
	public String cpcgi(HttpServletRequest request, ModelMap model) throws Exception
	{
		HttpSession session = request.getSession();
		System.out.println("sessin get");
		System.out.println(session.getAttribute("money"));
		return "/danal/CPCGI";
	}
	@RequestMapping(value = "/cancel.do")
	public String cancel(HttpServletRequest request, ModelMap model) throws Exception
	{
		return "/danal/Cancel";
	}
	@RequestMapping(value = "/ready.do")
	public String Ready(HttpServletRequest request, ModelMap model) throws Exception
	{
		return "/danal/Ready";
	}
	@RequestMapping(value = "/success.do")
	public String success(HttpServletRequest request, ModelMap model) throws Exception
	{
		return "/danal/Success";
	}


}
