package egovframework.example.sample.admin;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
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
@RequestMapping("/admin")
public class BoardController {

	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;

	@Resource(name = "fileProperties")
	private Properties fileProperties;

	/*
	 * 유튜브 등록 설정
	 */
	@RequestMapping("/youtube.do")
	public String youtube(ModelMap model) {
		model.addAttribute("youtube", sampleDAO.select("selectMainFileYoutube"));
		return "admin/youtube";
	}

	@ResponseBody
	@RequestMapping(value = "/youtubeSet.do", produces = "appliction/json; charset=utf8")
	public String youtubeSet(HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		String youtubeLink = "" + request.getParameter("youtube");
		String link[] = youtubeLink.split("=");
		EgovMap in = new EgovMap();
		if (link[1].contains("&") == true) {
			String linkYoutube[] = link[1].split("&");
			String linkId = linkYoutube[0];
			in.put("saveNm", linkId);
		} else {
			String linkId = link[1];
			in.put("saveNm", linkId);
		}
		in.put("originNm", youtubeLink);
		in.put("kind", "Y");
		in.put("orderNum", 1);
		EgovMap youtube = (EgovMap) sampleDAO.select("selectMainFileYoutube");
		if (youtube == null) {
			sampleDAO.insert("insertMainFile", in);
		} else {
			in.put("idx", youtube.get("idx"));
			sampleDAO.update("updateMainFile", in);
		}
		obj.put("result", "success");
		return obj.toJSONString();
	}

	/*
	 * 이미지 등록 설정
	 */
	@RequestMapping("/image.do")
	public String image(ModelMap model) {
		model.addAttribute("imageList", sampleDAO.list("selectMainFileImage"));
		return "admin/image";
	}

	@RequestMapping("/imageAdd.do")
	public String imageAdd(ModelMap model) {
		return "admin/imageAdd";
	}

	@ResponseBody
	@RequestMapping(value = "/imageInsert.do", produces = "appliction/json; charset=utf8")
	public String imageInsert(MultipartHttpServletRequest mtfRequest) {
		List<MultipartFile> mf = mtfRequest.getFiles("file");
		String path = fileProperties.getProperty("file.photo");
		JSONObject obj = new JSONObject();
		obj.put("success", "success");
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		if (mf.size() <= 7) {
			for (int fileCnt = 0; fileCnt < mf.size(); fileCnt++) {
				if (!mf.get(fileCnt).isEmpty()) { // 파일 null check
					String originFileName = mf.get(fileCnt).getOriginalFilename();
					// 저장될 파일이름
					String safeFile = System.currentTimeMillis() + originFileName;
					try {
						mf.get(fileCnt).transferTo(new File(path + safeFile));
						EgovMap in = new EgovMap();
						in.put("originNm", originFileName);
						in.put("saveNm", safeFile);
						in.put("orderNum", fileCnt + 1);
						in.put("kind", "I");
						sampleDAO.insert("insertMainFile", in);
					} catch (Exception e) {
						e.printStackTrace();
						obj.put("success", "fail");
					}
				}

			}
		} else {
			for (int fileCnt = 0; fileCnt < 7; fileCnt++) {
				if (!mf.get(fileCnt).isEmpty()) { // 파일 null check
					String originFileName = mf.get(fileCnt).getOriginalFilename();
					// 저장될 파일이름
					String safeFile = System.currentTimeMillis() + originFileName;
					try {
						mf.get(fileCnt).transferTo(new File(path + safeFile));
						EgovMap in = new EgovMap();
						in.put("originNm", originFileName);
						in.put("saveNm", safeFile);
						in.put("orderNum", fileCnt + 1);
						in.put("kind", "I");
						sampleDAO.insert("insertMainFile", in);
					} catch (Exception e) {
						e.printStackTrace();
						obj.put("success", "fail");
					}
				}
			}
		}
		int imageCnt = (int) sampleDAO.select("selectMainFileImageCnt");
		if(imageCnt > 7) { 
			int limit = imageCnt -7;
			EgovMap in = new EgovMap();
			in.put("limit",limit); 
			sampleDAO.delete("deleteMainFileImageLimit",in); 
		}

		return obj.toJSONString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/imageDel.do", produces="application/json; charset=utf8;")
	public String imageDel(HttpServletRequest request)throws Exception {
		JSONObject obj = new JSONObject();
		EgovMap in = new EgovMap();
		String result = "success";
		String path = fileProperties.getProperty("file.photo");
		String delList = ""+request.getParameter("delArray");
		String[] delArray = delList.split("-");
		if(delArray != null && delArray.length > 0){
			for(int i=0; i<delArray.length; i++){
				in.put("idx", delArray[i]);
				EgovMap fileInfo = (EgovMap)sampleDAO.select("selectMainFileDetail" , in);
				File file = new File(path+fileInfo.get("saveNm"));
				if(file.exists())
				{
					file.delete();
				}
				sampleDAO.delete("deleteMainFileImage" , in);
			}
			result = "success";
		}else{
			result = "nothing";
		}
		obj.put("result", result);
		
		return obj.toJSONString();
	}

	@ResponseBody
	@RequestMapping(value = "/imageOrderUpdate.do", produces="application/json; charset=utf8;")
	public String imageOrderUpdate(HttpServletRequest request)throws Exception {
		String[] numArr = request.getParameterValues("numArr");
		String[] idxArr = request.getParameterValues("idxArr");
		JSONObject obj = new JSONObject();
		for(int numCnt = 0; numCnt < numArr.length; numCnt++) {
			EgovMap in = new EgovMap();
			in.put("idx", idxArr[numCnt]);
			in.put("orderNum", numArr[numCnt]);
			try {
				sampleDAO.update("updateMainFileImageOrderNum",in);
			} catch (Exception e) {
				obj.put("result", "fail");
				return obj.toJSONString();
			}
		}
		obj.put("result", "success");
		return obj.toJSONString();
	}
	
	@RequestMapping(value="/notice.do")
	public String notice(ModelMap model) {
		model.addAttribute("noticeList", sampleDAO.list("selectNoticeAdmin"));
		return "admin/notice";
	}
	
	@RequestMapping(value="/noticeAdd.do")
	public String noticeAdd() {
		return "admin/noticeAdd";
	}
	
	@ResponseBody
	@RequestMapping(value="/noticeInsert.do" , produces = "application/json; charset=utf8")
	public String noticeInsert(HttpServletRequest request) {
		String title = request.getParameter("title");
		String text = request.getParameter("text");
		String topYn = request.getParameter("topYn");
		String expsYn = request.getParameter("expsYn");
		EgovMap in = new EgovMap();
		in.put("title", title);
		in.put("text", text);
		in.put("topYn", topYn);
		in.put("expsYn", expsYn);
		JSONObject obj = new JSONObject();
		try {
			sampleDAO.insert("insertNotice", in);
			obj.put("result", "success");
			return obj.toJSONString();
		} catch (Exception e) {
			obj.put("result", "fail");
			return obj.toJSONString();
		}
				
	}
	@ResponseBody
	@RequestMapping(value = "/noticeDel.do", produces="application/json; charset=utf8;")
	public String noticeDel(HttpServletRequest request)throws Exception {
		JSONObject obj = new JSONObject();
		EgovMap in = new EgovMap();
		String result = "success";
		String delList = ""+request.getParameter("delArray");
		String[] delArray = delList.split("-");
		if(delArray != null && delArray.length > 0){
			for(int i=0; i<delArray.length; i++){
				in.put("idx", delArray[i]);
				sampleDAO.update("deleteNotice" , in);
			}
			result = "success";
		}else{
			result = "nothing";
		}
		obj.put("result", result);
		
		return obj.toJSONString();
	}
	
	@RequestMapping(value="/noticeInfo.do")
	public String noticeInfo(HttpServletRequest request , ModelMap model) {
		int idx = Integer.parseInt(""+request.getParameter("idx"));
		model.addAttribute("info", sampleDAO.select("selectNoticeDetail" , idx));
		return "admin/noticeInfo";
	}
	
	@ResponseBody
	@RequestMapping(value="/noticeUpdate.do" , produces = "application/json; charset=utf8")
	public String noticeUpdate(HttpServletRequest request) {
		int idx = Integer.parseInt(""+request.getParameter("idx"));
		String title = request.getParameter("title");
		String text = request.getParameter("text");
		String topYn = request.getParameter("topYn");
		String expsYn = request.getParameter("expsYn");
		EgovMap in = new EgovMap();
		in.put("idx", idx);
		in.put("title", title);
		in.put("text", text);
		in.put("topYn", topYn);
		in.put("expsYn", expsYn);
		JSONObject obj = new JSONObject();
		try {
			sampleDAO.update("updateNotice", in);
			obj.put("result", "success");
			return obj.toJSONString();
		} catch (Exception e) {
			obj.put("result", "fail");
			return obj.toJSONString();
		}
	}
	
	@RequestMapping(value="/inquiry.do")
	public String inquiry(ModelMap model) {
		model.addAttribute("inquiryList", sampleDAO.list("selectInquiry"));
		return "admin/inquiry";
	}
	
	@RequestMapping(value="/inquiryInfo.do")
	public String inquiryInfo(HttpServletRequest request ,ModelMap model) {
		int idx = Integer.parseInt(""+request.getParameter("idx"));
		EgovMap detail = (EgovMap)sampleDAO.select("selectInquiryDetail" , idx);
		model.addAttribute("info", detail);
		model.addAttribute("text", StringEscapeUtils.unescapeHtml3(""+detail.get("text")));
		return "admin/inquiryInfo";
	}
	
	@ResponseBody
	@RequestMapping(value="/answerInsert.do" , produces = "application/json; charset=utf8")
	public String answerInsert(HttpServletRequest request) {
		int idx = Integer.parseInt(""+request.getParameter("idx"));
		String answer = request.getParameter("answer");
		EgovMap in = new EgovMap();
		in.put("idx", idx);
		in.put("answer", answer);
		JSONObject obj = new JSONObject();
		try {
			sampleDAO.update("insertAnswer", in);
			obj.put("result", "success");
			return obj.toJSONString();
		} catch (Exception e) {
			obj.put("result", "fail");
			return obj.toJSONString();
		}
	}
	
}
