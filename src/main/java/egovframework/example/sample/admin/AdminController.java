package egovframework.example.sample.admin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;
	
    private static byte[] Sha256(String password) throws NoSuchAlgorithmException {
        MessageDigest messagediegest = MessageDigest.getInstance("SHA-256");
        messagediegest.update(password.getBytes());
        return messagediegest.digest();
    }
    
    private static String BytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b: bytes) {
          builder.append(String.format("%02x", b));
        }
        
        return builder.toString();
    }
    
	@RequestMapping(value = "/leftmenu.do")
	public String leftmenu(HttpServletRequest request, ModelMap model) throws Exception {
		return "frame/leftmenu";
	}
	
	@RequestMapping(value="/login.do")
	public String login() {
		return "admin/login";
	}
	
	@ResponseBody
	@RequestMapping(value="/loginChk.do" , produces = "application/json; charset=utf8")
	public String loginChk(HttpServletRequest request) {
		String id = ""+request.getParameter("id");
		String pw = ""+request.getParameter("pw");
		String savePw = "";
		try {
			savePw = BytesToHex(Sha256(pw));
		} catch (Exception e) {
			e.printStackTrace();
		}
		EgovMap in = new EgovMap();
		in.put("id", id);
		in.put("pw", savePw);
		JSONObject lobj = new JSONObject();
		HttpSession session = request.getSession();
		if(sampleDAO.select("selectAdminLoginChk" , in)== null)
		{
			lobj.put("result", "fail");
			lobj.put("msg", "아이디 혹은 비밀번호를 확인해주세요");
		}
		else
		{
			lobj.put("result", "success");
			session.setAttribute("adminId", id);
		}
		return lobj.toJSONString();
	}
	
	@RequestMapping(value="/logout.do")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("adminId", null);
		return "redirect:login.do";
	}
	
	@RequestMapping(value="/main.do")
	public String main() {
		return "admin/main";
	}
	
	@RequestMapping(value = "/gamelogp.do")
	public String gamelog(HttpServletRequest request, ModelMap model) throws Exception {
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(50);
		paginationInfo.setPageSize(7);

		//인자생성
		EgovMap in = new EgovMap();
		in.put("firstindex", "" + paginationInfo.getFirstRecordIndex());
		in.put("recordperpage", "" + paginationInfo.getRecordCountPerPage());
		List<?> list = (List<?>) sampleDAO.list("GameNumberPaging", in);
		model.addAttribute("resultList", list);
		EgovMap count = (EgovMap) sampleDAO.select("GameNumberTotal");
		String total = "" + count.get("num");
		paginationInfo.setTotalRecordCount(Integer.parseInt(total));
		model.addAttribute("paginationInfo", paginationInfo);		
		return "admin/gamelog";
	}

	@RequestMapping(value = "/gameDetailLogp.do")
	public String gameDetailLogp(HttpServletRequest request, ModelMap model) throws Exception {
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(100);
		paginationInfo.setPageSize(7);
		
		String gameid = ""+request.getParameter("gameid");
		model.addAttribute("gameid", gameid);
		//인자생성
		EgovMap in = new EgovMap();
		in.put("gameid", gameid);
		in.put("firstindex", "" + paginationInfo.getFirstRecordIndex());
		in.put("recordperpage", "" + paginationInfo.getRecordCountPerPage());
		List<?> list = (List<?>) sampleDAO.list("GameNumberDetailPaging", in);
		model.addAttribute("resultList", list);
		EgovMap count = (EgovMap) sampleDAO.select("GameNumberDetailTotal", in);
		String total = "" + count.get("num");
		paginationInfo.setTotalRecordCount(Integer.parseInt(total));
		model.addAttribute("paginationInfo", paginationInfo);		
		return "admin/gamedetaillog";
	}
	
	//구정연_상용문구페이지
	@RequestMapping(value = "/chat.do")
	public String chat(HttpServletRequest request, ModelMap model) throws Exception {
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(10);
		paginationInfo.setPageSize(7);
		
		//인자생성
		EgovMap in = new EgovMap();
		in.put("firstindex", "" + paginationInfo.getFirstRecordIndex());
		in.put("recordperpage", "" + paginationInfo.getRecordCountPerPage());
		List<?> list = (List<?>) sampleDAO.list("ChatPaging", in);
		model.addAttribute("resultList", list);
		EgovMap count = (EgovMap) sampleDAO.select("ChatTotal");
		String total = "" + count.get("num");
		paginationInfo.setTotalRecordCount(Integer.parseInt(total));
		model.addAttribute("paginationInfo", paginationInfo);		
		
		return "admin/chat";
	}
	
	//구정연_상용문구 등록
	@RequestMapping(value = "/chatinsert.do", method=RequestMethod.GET)
	public String chatinsert(HttpServletRequest request, ModelMap model) throws Exception {
		
		String mention = request.getParameter("mention");
		EgovMap in = new EgovMap();
		in.put("cmention", mention);
		sampleDAO.insert("ChatInsert",in);
		
	
		return "redirect:/chat.do";
	}

}
