package egovframework.example.sample.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.example.sample.web.PaymentLog;
import egovframework.example.sample.web.model.ProductModel;
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
		model.addAttribute("prdtNm", request.getParameter("prdtNm"));
		model.addAttribute("code", request.getParameter("code"));
		model.addAttribute("money", request.getParameter("money"));
		EgovMap info = (EgovMap)sampleDAO.select("selectinfo" , session.getAttribute("midx"));
		model.addAttribute("muserid", info.get("muserid"));
		model.addAttribute("nick", info.get("nickname"));
		session.setAttribute("money", request.getParameter("money"));
		session.setAttribute("code", request.getParameter("code"));
		return "/danal/Order";
	}
	@RequestMapping(value = "/cpcgi.do")
	public String cpcgi(HttpServletRequest request, ModelMap model) throws Exception
	{
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
		HttpSession session = request.getSession();
		session.setAttribute("midx", request.getParameter("MIDX"));
		EgovMap in = new EgovMap();
		// 세션 다시 등록 (안끊기게 ) 
		in.put("midx", request.getParameter("MIDX"));
		EgovMap mInfo = (EgovMap)sampleDAO.select("selectMemberInfo" , in);
		session.setAttribute("muserid", mInfo.get("muserid"));
		session.setAttribute("muserpw", mInfo.get("muserpw"));
		ClassPathResource resource = new ClassPathResource("json/product.json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			Path path = Paths.get(resource.getURI());
			String content = Files.readString(path);
			List<ProductModel> pmList = mapper.readValue(content , new TypeReference<ArrayList<ProductModel>>() {});
			for(int pmCnt=0; pmCnt < pmList.size(); pmCnt++)
			{
				if(pmList.get(pmCnt).getProduct().equals(request.getParameter("CODE")))
				{
					// payment_log기록
					PaymentLog.Insert(Integer.parseInt(""+request.getParameter("MIDX")), 1, Long.parseLong(""+request.getParameter("MONEY")), request.getParameter("CODE"), "");
					// item cash 수량 +
					int amount = Integer.parseInt(request.getParameter("MONEY").substring(0, request.getParameter("MONEY").length()-1));
					in.put("type", "cash");
					in.put("amount", amount);
					sampleDAO.update("updatePlusCashAmount",in);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
			
		return "/danal/Success";
	}


}
