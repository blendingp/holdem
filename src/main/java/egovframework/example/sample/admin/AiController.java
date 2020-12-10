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

	@RequestMapping(value="/aiUserCreate.do")
	public String aiUserCreate() {
		return "admin/aiUserCreate";
	}
	
	@ResponseBody
	@RequestMapping(value = "/aiCreateProcess.do", produces="application/json; charset=utf8;")
	public String aiCreateProcess(HttpServletRequest request)throws Exception {
		JSONObject obj = new JSONObject();
		EgovMap in = new EgovMap();
		String result = "success";
		int num = Integer.parseInt(""+request.getParameter("num"));
		int balance = Integer.parseInt(""+request.getParameter("balance"));
		int point = Integer.parseInt(""+request.getParameter("point"));
		int cnt = 0;
		String firstNm = ""+request.getParameter("firstNm");
		for(int numCnt=0; numCnt < num; numCnt ++) {
			cnt++;
			String id = firstNm+cnt;
			if(sampleDAO.select("selectMemberById",id)== null)
			{
				try {
					in.put("muserid", id);
					in.put("muserpw", "1234");
					in.put("socail", "");
					sampleDAO.insert("InsertUser" , in); // 회원 추가 
					EgovMap ed = (EgovMap)sampleDAO.select("Login", in);
					in.put("midx", ""+ed.get("midx"));
					in.put("nickname", id);
					in.put("ai", "1");
					in.put("balance", balance);
					in.put("point", point);
					sampleDAO.insert("insertMemberInfo" , in); // ai설정 추가 
					sampleDAO.insert("insertMemberItem" , in); // AI item 추가 
				} catch (Exception e) {
					result = "fail";
					obj.put("result", result);
					return obj.toJSONString();
				}
			}
			else
			{
				numCnt --;
			}
		}
		obj.put("result", result);
		return obj.toJSONString();
	}
	
	
}

