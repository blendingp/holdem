package egovframework.example.sample.Admin;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
public class AdminController {
	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;
	
	@RequestMapping(value = "/leftmenu.do")
	public String leftmenu(HttpServletRequest request, ModelMap model) throws Exception {
		return "frame/leftmenu";
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
