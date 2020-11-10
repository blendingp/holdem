package egovframework.example.sample.user;

import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import egovframework.example.sample.service.impl.SampleDAO;
import egovframework.rte.psl.dataaccess.util.EgovMap;

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
		return "user/main";
	}
	
	@RequestMapping(value="/inquiry.do")
	public String inquiry(ModelMap model) {
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
}
