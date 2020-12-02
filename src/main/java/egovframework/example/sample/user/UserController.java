package egovframework.example.sample.user;

import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
@RequestMapping("/user")
public class UserController {
	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;

	@Resource(name = "fileProperties")
	private Properties fileProperties;

	@RequestMapping(value="/main.do")
	public String main(ModelMap model) {
		model.addAttribute("youtube", sampleDAO.select("selectMainFileYoutube"));
		model.addAttribute("imageList", sampleDAO.list("selectMainFileImageUser"));
		return "user/main";
	}
	
	@RequestMapping(value="/inquiry.do")
	public String inquiry(ModelMap model, HttpServletRequest request) {
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			 paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		EgovMap in = new EgovMap();
		paginationInfo.setRecordCountPerPage(10);
		paginationInfo.setPageSize(10);
		in.put("firstindex", paginationInfo.getFirstRecordIndex());
		in.put("recordperpage", paginationInfo.getRecordCountPerPage());
		List<EgovMap> inquiryList = (List<EgovMap>)sampleDAO.list("selectInquiry" , in);
		int totCnt = (int)sampleDAO.select("selectInquiryCnt" , in);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("inquiryList", inquiryList);
		model.addAttribute("paginationInfo", paginationInfo);
		return "user/inquiry";
	}
	
	@RequestMapping(value="/inquiryAdd.do")
	public String inquiryAdd(ModelMap model) {
		return "user/inquiryAdd";
	}
	
	@ResponseBody
	@RequestMapping(value="/inquiryInsert.do" , produces = "application/json; charset=utf8")
	public String inquiryInsert(HttpServletRequest request) {
		String title = ""+request.getParameter("title");
		String text = ""+request.getParameter("text");
		String pw = ""+request.getParameter("pw");
		JSONObject obj = new JSONObject();
		try {
			pw = BytesToHex(Sha256(pw));
			EgovMap in = new EgovMap();
			in.put("title", title);
			in.put("text", text);
			in.put("pw", pw);
			sampleDAO.insert("insertInquiry" , in);
			obj.put("result", "success");
		} catch (NoSuchAlgorithmException e) {
			obj.put("result", "fail");
			e.printStackTrace();
		}
		return obj.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping(value="/inquiryPwChk.do", produces = "application/json; charset=utf8")
	public String inquiryPwChk(HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		
		String pw = ""+request.getParameter("pw");
		String idx= ""+request.getParameter("idx");
		EgovMap in = new EgovMap();
		in.put("idx",idx);
		EgovMap pwChk = (EgovMap)sampleDAO.select("pwChk",in);
		try {
			pw = BytesToHex(Sha256(pw));
			if(pw.equals(pwChk.get("pw"))) {
				obj.put("result", "success");
			}else {
				obj.put("result","fail");
			}
			
		} catch (NoSuchAlgorithmException e) {
			obj.put("result", "fail");
			e.printStackTrace();
		}
		
		return obj.toJSONString();
	}
	
	
	@RequestMapping(value="/inquiryDetail.do")
	public String inquiryDetail(HttpServletRequest request, ModelMap model) {
		String idx=""+request.getParameter("idx");
		EgovMap in = new EgovMap();
		in.put("idx", idx);
		EgovMap inquiryDetail = (EgovMap)sampleDAO.select("selectInquiryDetail",in);
		model.addAttribute("inquiryDetail",inquiryDetail);
		model.addAttribute("text",StringEscapeUtils.unescapeHtml3(""+inquiryDetail.get("text")));
		if(inquiryDetail.get("answerYn") != null && inquiryDetail.get("answerYn").equals("Y")) 
		{
			model.addAttribute("answer",StringEscapeUtils.unescapeHtml3(""+inquiryDetail.get("answer")));
		}
		return "user/inquiryDetail";
	}
	
	@RequestMapping(value="/inquiryEdit.do")
	public String inquiryEdit(HttpServletRequest request,ModelMap model) {
		String idx= ""+request.getParameter("idx");
		EgovMap in = new EgovMap();
		in.put("idx", idx);
		model.addAttribute("inquiryDetail",sampleDAO.select("selectInquiryDetail",in));
		
		return "user/inquiryEdit";
	}
	
	@ResponseBody
	@RequestMapping(value="/inquiryEditProcess.do", produces = "application/json; charset=utf8")
	public String inquiryEditProcess(HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		String title=""+request.getParameter("title");
		String text=""+request.getParameter("text");
		String idx=""+request.getParameter("idx");
		
		EgovMap in = new EgovMap();
		in.put("title",title);
		in.put("text", text);
		in.put("idx",idx);
		sampleDAO.update("inquiryUpdate",in);
		obj.put("result", "success");
		
		return obj.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping(value="/inquiryDel.do",produces = "application/json; charset=utf8")
	public String inquiryDel(HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		String idx= ""+request.getParameter("idx");
		sampleDAO.update("inquiryDel",idx);
		obj.put("result", "success");
		
		return obj.toJSONString(); 
	}
	
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
	// 에디터 파일 업로드
	@RequestMapping(value = "/editorFileUpload.do")
	public void editorFileUpload(MultipartHttpServletRequest mre,HttpServletRequest request, ModelMap model , HttpServletResponse response) throws Exception {
		System.out.println("editorFileUpload");
	    try {
	         String sFileInfo = "";
	         String filename = mre.getFile("file").getOriginalFilename();
	         String filename_ext = filename.substring(filename.lastIndexOf(".")+1);
	         filename_ext = filename_ext.toLowerCase();
	         String dftFilePath = fileProperties.getProperty("file.editor");
	         SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd"); 
	         String today= formatter.format(new java.util.Date()); 
	         String filePath = dftFilePath+today+File.separator;
	         File file = new File(filePath);
	         if(!file.exists()) {
	            file.mkdirs();
	         }
	         String realFileNm = "";
	         realFileNm = UUID.randomUUID().toString().replaceAll("-", "") + filename.substring(filename.lastIndexOf("."));
	         String rlFileNm = filePath + realFileNm;
	         
	         MultipartFile mf = mre.getFile("file");
	         
	         mf.transferTo(new File(rlFileNm));
	         sFileInfo += "&bNewLine=true";
	         sFileInfo += "&sFileName="+ filename;;
	         sFileInfo += "&sFileURL="+"/filePath/holdem/editor/"+today+"/"+realFileNm; 
	         PrintWriter print = response.getWriter();
	         print.print(sFileInfo);
	         print.flush();
	         print.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@RequestMapping(value="/notice.do")
	public String notice(ModelMap model, HttpServletRequest request) {
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			 paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		EgovMap in = new EgovMap();
		List<EgovMap> noticeListTop = (List<EgovMap>)sampleDAO.list("selectNoticeUserTop");
		// 상단 고정된 값 빼고 select 되게 
		for(int topCnt = 0; topCnt < noticeListTop.size(); topCnt ++)
		{
			in.put("excludeIdx"+topCnt, noticeListTop.get(topCnt).get("idx"));
		}
		paginationInfo.setRecordCountPerPage(10 - noticeListTop.size());
		paginationInfo.setPageSize(5);
		in.put("firstindex", paginationInfo.getFirstRecordIndex());
		in.put("recordperpage", paginationInfo.getRecordCountPerPage());
		List<EgovMap> noticeList = (List<EgovMap>)sampleDAO.list("selectNoticeUser" , in);
		int totCnt = (int)sampleDAO.select("selectNoticeUserCnt" , in);
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("noticeListTop", noticeListTop);
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("paginationInfo", paginationInfo);
		
		return "user/notice";
	}
	
	
	
	@RequestMapping(value="/noticeDetail.do")
	public String noticeDetail(HttpServletRequest request, ModelMap model, HttpServletResponse response) {
		String idx=""+request.getParameter("idx");
		EgovMap in = new EgovMap();
		in.put("idx", idx);
		EgovMap noticeDetail = (EgovMap)sampleDAO.select("selectNoticeDetail",in);
		model.addAttribute("noticeDatail",noticeDetail);
		model.addAttribute("noticeDatailText",StringEscapeUtils.unescapeHtml3(""+noticeDetail.get("text")));

		//조회수 처리 
		Cookie[] cookies = request.getCookies();
		Cookie viewCookie = null;
        if (cookies != null && cookies.length > 0){
            for (int i = 0; i < cookies.length; i++){
                // Cookie의 name이 cookie + reviewNo와 일치하는 쿠키를 viewCookie에 넣어줌 
                if (cookies[i].getName().equals("cookie"+idx)){ 
                    viewCookie = cookies[i];
                }
            }
        }		
        if(viewCookie == null){
        	Cookie newCookie = new Cookie("cookie"+idx, "|" + idx + "|");
        	response.addCookie(newCookie);
        	sampleDAO.update("noticeViewCnt",idx);  //조회수
        }
        
		return "user/noticeDetail";
	}
	
	
	@RequestMapping(value="/etcB/{path}.do")
	public String path(@PathVariable("path") String path , HttpServletRequest request , ModelMap model) {
		String type = ""+request.getParameter("type");
		EgovMap info = (EgovMap) sampleDAO.select("selectEtcBoardByKind" , type);
		if(info != null)
		{
			model.addAttribute("info",StringEscapeUtils.unescapeHtml3(""+info.get("text")));
		}
		else
		{
			model.addAttribute("info","등록된 글이 없습니다.");
		}
		model.addAttribute("path" , path);
		model.addAttribute("type" , type);
		return "user/etcBoard";
	}
	
	@RequestMapping(value="/ldPlayerGuide.do")
	public String ldPlayerGuide() {
		return "user/ldPlayerGuide";
	}
	
}
