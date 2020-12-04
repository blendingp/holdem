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
import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
@RequestMapping("/admin")
public class AiController {

	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;
	
	@RequestMapping("/aiSetting.do")
	public String aiSetting(HttpServletRequest request , ModelMap model) {
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			 paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		EgovMap in = new EgovMap();
		in.put("first", paginationInfo.getFirstRecordIndex());
		in.put("record", paginationInfo.getRecordCountPerPage());
		model.addAttribute("list", sampleDAO.list("selectMemberInfoList" ,in));
		paginationInfo.setTotalRecordCount((int)sampleDAO.select("selectMemberInfoListCnt"));
		model.addAttribute("pi", paginationInfo);
		return "admin/aiSetting";
	}

	@ResponseBody
	@RequestMapping(value = "/changeUserSet.do", produces="application/json; charset=utf8;")
	public String changeUserSet(HttpServletRequest request)throws Exception {
		JSONObject obj = new JSONObject();
		EgovMap in = new EgovMap();
		String result = "success";
		String arrList = ""+request.getParameter("arrIdx");
		int kind = Integer.parseInt(""+request.getParameter("kind"));
		String[] arrIdx = arrList.split("-");
		if(arrIdx != null && arrIdx.length > 0){
			for(int i=0; i<arrIdx.length; i++){
				in.put("idx", arrIdx[i]);
				in.put("kind", kind);
				sampleDAO.update("updateMemberInfoAiSet" , in);
			}
			result = "success";
		}else{
			result = "fail";
		}
		obj.put("result", result);
		
		return obj.toJSONString();
	}

}

