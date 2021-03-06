package egovframework.example.sample.user;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.example.sample.web.model.ProductModel;
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
	public String main(HttpServletRequest request, ModelMap model) {
		String re = request.getParameter("re");  
		model.addAttribute("re", re);
		model.addAttribute("youtube", sampleDAO.select("selectMainFileYoutube"));
		model.addAttribute("imageList", sampleDAO.list("selectMainFileImageUser"));
		return "user/main";
	}
	
	@RequestMapping(value = "/loginprocess.do")
	public String loginprocess(HttpServletRequest request, ModelMap model) throws Exception {
		
		String muserid = request.getParameter("muserid");
		String muserpw = request.getParameter("muserpw");
		
		EgovMap in = new EgovMap();
		in.put("muserid", muserid);
		in.put("muserpw", muserpw);
		EgovMap ed = (EgovMap) sampleDAO.select("login", in);
		
		if(ed!=null){
			//로그인성공
			HttpSession session = request.getSession();
			session.setAttribute("midx", ed.get("midx"));
			session.setAttribute("muserid", ed.get("muserid"));
			session.setAttribute("muserpw", ed.get("muserpw"));
		}
		else{
			//로그인실패
			return "redirect:/user/main.do?re=1";
		}

		return "redirect:/user/main.do";		
	}
	
	@RequestMapping(value = "/logoutprocess.do")
	public String logoutprocess(HttpServletRequest request, ModelMap model) throws Exception {
		HttpSession session = request.getSession();
		session.setAttribute("midx", null);
		session.setAttribute("muserid", null);
		session.setAttribute("muserpw", null);
		return "redirect:/user/main.do";	
	}
	
	@RequestMapping(value = "/join.do")
	public String join(HttpServletRequest request, ModelMap model) throws Exception {
		model.addAttribute("birthDate", request.getParameter("birthDate"));
		model.addAttribute("uid", request.getParameter("uid"));
		model.addAttribute("autchTick", request.getParameter("autchTick"));
		return "user/join";
	}
	
	@RequestMapping(value = "/verification.do")
	public String verification(HttpServletRequest request, ModelMap model) throws Exception {
		return "user/verification";
	}
	
	@RequestMapping(value = "/joinNice.do")
	public String joinNice(HttpServletRequest request, ModelMap model) throws Exception {
		return "user/joinNice";
	}
	@RequestMapping(value = "/identifyresult.do")
	public String identifyresult(HttpServletRequest request, ModelMap model) throws Exception {
		return "user/joinbyphone";
	}
	
	@ResponseBody
	@RequestMapping(value = "/joinInsert.do")
	public String joinInsert(HttpServletRequest request, ModelMap model) throws Exception {
		String muserid = request.getParameter("muserid");
		String muserpw = request.getParameter("muserpw");
		String nickname = request.getParameter("nickname");
		String birthDate = request.getParameter("birthDate");
		String uid = request.getParameter("uid");
		String autchTick = request.getParameter("autchTick");
//		String birthY = request.getParameter("birthY");
//		String birthM = request.getParameter("birthM");
//		String birthD = request.getParameter("birthD");
		
		EgovMap in = new EgovMap();
		in.put("muserid", muserid);
		in.put("muserpw", muserpw);
		in.put("nickname", nickname);
		//in.put("birthdate", birthY + birthM + birthD);
		EgovMap id = (EgovMap) sampleDAO.select("selectId", muserid);
		if(id != null) 
		{
			return "id";
		}
		EgovMap nick = (EgovMap) sampleDAO.select("selectNick", nickname);
		if(nick != null) 
		{
			return "nick";
		}
		sampleDAO.insert("insertJoin",in);
		EgovMap info = (EgovMap)sampleDAO.select("Login", in);
		in.put("midx", ""+info.get("midx"));
		in.put("ai", "0");
		in.put("balance", 0);
		in.put("point", 1000000000);
		in.put("birthdate", birthDate);
		in.put("uid", uid);
		in.put("autchTick", autchTick);
		sampleDAO.insert("insertMemberItem" , in); // AI item 추가 
		sampleDAO.insert("insertJoinAuth" , in); // 휴대폰인증 
		sampleDAO.insert("insertMemberInfo" , in); // ai설정 추가 
		return "success";
		//return "redirect:/user/main.do";
	}

	@ResponseBody
	@RequestMapping(value="/checkId.do")
	public String checkId(HttpServletRequest request)
	{
		String muserid = request.getParameter("muserid");
		EgovMap ed = (EgovMap) sampleDAO.select("selectId", muserid);
		if(ed != null) 
		{
			return "fail";
		}
		else
		{
			return "success";
		}
	}
	@ResponseBody
	@RequestMapping(value="/checkNick.do")
	public String checkNick(HttpServletRequest request)
	{
		String nickname = request.getParameter("nickname");
		EgovMap ed = (EgovMap) sampleDAO.select("selectNick", nickname);
		if(ed != null) 
		{
			return "fail";
		}
		else
		{
			return "success";
		}
	}
	
	@RequestMapping(value = "/shop.do")
	public String shop(HttpServletRequest request, ModelMap model) throws Exception {
		ClassPathResource resource = new ClassPathResource("json/product.json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			Path path = Paths.get(resource.getURI());
			String content = Files.readString(path);
			List<ProductModel> pmList = mapper.readValue(content , new TypeReference<ArrayList<ProductModel>>() {});
			model.addAttribute("pmList", pmList);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return "user/shop";
	}
	
	@ResponseBody
	@RequestMapping(value="/checkCashLimit.do")
	public String checkCashLimit(HttpServletRequest request){
		HttpSession session = request.getSession();
		int money = Integer.parseInt(""+request.getParameter("m"));
		System.out.println( "money : " + money);
		int midx = Integer.parseInt(""+session.getAttribute("midx"));
		Calendar cal = Calendar.getInstance();
		EgovMap in = new EgovMap();
		in.put("midx", midx);
		in.put("year", cal.get(Calendar.YEAR));
		in.put("month", cal.get(Calendar.MONTH) + 1);
		in.put("day", cal.get(Calendar.DAY_OF_MONTH));
		int totMoney = (int)sampleDAO.select("selectTodayPaymentMoney" , in);
		if(totMoney+money > 500000)
		{
			return "fail";
		}
		return "success";
	}
	
	@RequestMapping(value = "/myinfo.do")
	public String myinfo(HttpServletRequest request, ModelMap model) throws Exception {
		HttpSession session = request.getSession();
		String midx = ""+session.getAttribute("midx");
		String result = request.getParameter("result");
		model.addAttribute("result", result);
		
		EgovMap in = new EgovMap();
		in.put("midx", midx);
		EgovMap ed = (EgovMap) sampleDAO.select("selectinfo", in);
		EgovMap ed2 = (EgovMap) sampleDAO.select("selectAuth", in);
		model.addAttribute("item", ed);
		model.addAttribute("item2", ed2);
		
		return "user/myinfo";
	}
	
	@RequestMapping(value = "/myinfoupdate.do")
	public String myinfoupdate(HttpServletRequest request, ModelMap model) throws Exception {
		String midx = request.getParameter("midx");
		String muserpw = request.getParameter("muserpw");
		
		EgovMap in = new EgovMap();
		in.put("midx", midx);
		in.put("muserpw", muserpw);
		EgovMap ed = (EgovMap) sampleDAO.select("selectPw", in);
		if(ed == null) return "redirect:/user/myinfo.do?result=1"; 
		
		String muserpw2 = request.getParameter("muserpw2");				
		in.put("muserpw", muserpw2);
		sampleDAO.update("updatemyinfo",in);
		
		return "redirect:/user/myinfo.do?result=0"; 
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
