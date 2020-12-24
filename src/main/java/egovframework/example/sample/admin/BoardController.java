package egovframework.example.sample.admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import egovframework.example.sample.web.PaymentLog;
import egovframework.example.sample.web.model.ProductModel;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
@RequestMapping("/admin")
public class BoardController {

	@Resource(name = "sampleDAO")
	private SampleDAO sampleDAO;

	@Resource(name = "fileProperties")
	private Properties fileProperties;

	static List<ProductModel> pmList = null;
	
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

	@RequestMapping("/imageEdit.do")
	public String imageEdit(ModelMap model) {
		model.addAttribute("imageList", sampleDAO.list("selectMainFileImage"));
		return "admin/imageEdit";
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
		if (imageCnt > 7) {
			int limit = imageCnt - 7;
			EgovMap in = new EgovMap();
			in.put("limit", limit);
			sampleDAO.delete("deleteMainFileImageLimit", in);
		}

		return obj.toJSONString();
	}

	@ResponseBody
	@RequestMapping(value = "/imageUpdate.do", produces = "application/json; charset=utf8;")
	public String imageUpdate(MultipartHttpServletRequest mtfRequest) {
		List<MultipartFile> mf = mtfRequest.getFiles("file[]");
		String[] idx = mtfRequest.getParameterValues("idx[]");
		String[] orderNum = mtfRequest.getParameterValues("orderNum[]");
		String path = fileProperties.getProperty("file.photo");
		JSONObject obj = new JSONObject();
		obj.put("success", "success");
		if (!new File(path).exists()) {
			new File(path).mkdirs();
		}
		for (int idxCnt = 0; idxCnt < idx.length; idxCnt++) {
			EgovMap info = (EgovMap) sampleDAO.select("selectMainFileDetail", idx[idxCnt]);
			if (!mf.get(idxCnt).isEmpty()) { // 파일이 존재함
				String originFileName = mf.get(idxCnt).getOriginalFilename();
				String safeFile = System.currentTimeMillis() + originFileName;
				try {
					File file = new File(path + info.get("saveNm"));
					if (file.exists()) {
						file.delete(); // 기존에 있던 파일 삭제
					}
					mf.get(idxCnt).transferTo(new File(path + safeFile));
					EgovMap in = new EgovMap();
					in.put("originNm", originFileName);
					in.put("saveNm", safeFile);
					in.put("orderNum", orderNum[idxCnt]);
					in.put("kind", "I");
					in.put("idx", idx[idxCnt]);
					sampleDAO.update("updateMainFileImage", in);
				} catch (Exception e) {
					e.printStackTrace();
					obj.put("success", "fail");
				}
			} else { // 파일 변경 X
				try {
					EgovMap in = new EgovMap();
					in.put("originNm", info.get("originNm"));
					in.put("saveNm", info.get("saveNm"));
					in.put("orderNum", orderNum[idxCnt]);
					in.put("kind", "I");
					in.put("idx", idx[idxCnt]);
					sampleDAO.update("updateMainFileImage", in);
				} catch (Exception e) {
					e.printStackTrace();
					obj.put("success", "fail");
				}
			}
		}
		return obj.toJSONString();
	}

	@ResponseBody
	@RequestMapping(value = "/imageDel.do", produces = "application/json; charset=utf8;")
	public String imageDel(HttpServletRequest request) throws Exception {
		JSONObject obj = new JSONObject();
		EgovMap in = new EgovMap();
		String result = "success";
		String path = fileProperties.getProperty("file.photo");
		String delList = "" + request.getParameter("delArray");
		String[] delArray = delList.split("-");
		if (delArray != null && delArray.length > 0) {
			for (int i = 0; i < delArray.length; i++) {
				in.put("idx", delArray[i]);
				EgovMap fileInfo = (EgovMap) sampleDAO.select("selectMainFileDetail", in);
				File file = new File(path + fileInfo.get("saveNm"));
				if (file.exists()) {
					file.delete();
				}
				sampleDAO.delete("deleteMainFileImage", in);
			}
			result = "success";
		} else {
			result = "nothing";
		}
		obj.put("result", result);

		return obj.toJSONString();
	}

	@ResponseBody
	@RequestMapping(value = "/imageOrderUpdate.do", produces = "application/json; charset=utf8;")
	public String imageOrderUpdate(HttpServletRequest request) throws Exception {
		String[] numArr = request.getParameterValues("numArr");
		String[] idxArr = request.getParameterValues("idxArr");
		JSONObject obj = new JSONObject();
		for (int numCnt = 0; numCnt < numArr.length; numCnt++) {
			EgovMap in = new EgovMap();
			in.put("idx", idxArr[numCnt]);
			in.put("orderNum", numArr[numCnt]);
			try {
				sampleDAO.update("updateMainFileImageOrderNum", in);
			} catch (Exception e) {
				obj.put("result", "fail");
				return obj.toJSONString();
			}
		}
		obj.put("result", "success");
		return obj.toJSONString();
	}

	@RequestMapping(value = "/notice.do")
	public String notice(HttpServletRequest request, ModelMap model) {
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		EgovMap in = new EgovMap();
		in.put("firstindex", paginationInfo.getFirstRecordIndex());
		in.put("recordperpage", paginationInfo.getRecordCountPerPage());
		List<EgovMap> noticeList = (List<EgovMap>) sampleDAO.list("selectNoticeAdmin", in);
		int totCnt = (int) sampleDAO.select("selectNoticeAdminCnt");
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("paginationInfo", paginationInfo);
		return "admin/notice";
	}

	@RequestMapping(value = "/noticeAdd.do")
	public String noticeAdd() {
		return "admin/noticeAdd";
	}

	@ResponseBody
	@RequestMapping(value = "/noticeInsert.do", produces = "application/json; charset=utf8")
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
	@RequestMapping(value = "/noticeDel.do", produces = "application/json; charset=utf8;")
	public String noticeDel(HttpServletRequest request) throws Exception {
		JSONObject obj = new JSONObject();
		EgovMap in = new EgovMap();
		String result = "success";
		String delList = "" + request.getParameter("delArray");
		String[] delArray = delList.split("-");
		if (delArray != null && delArray.length > 0) {
			for (int i = 0; i < delArray.length; i++) {
				in.put("idx", delArray[i]);
				sampleDAO.update("deleteNotice", in);
			}
			result = "success";
		} else {
			result = "nothing";
		}
		obj.put("result", result);

		return obj.toJSONString();
	}

	@RequestMapping(value = "/noticeInfo.do")
	public String noticeInfo(HttpServletRequest request, ModelMap model) {
		int idx = Integer.parseInt("" + request.getParameter("idx"));
		model.addAttribute("info", sampleDAO.select("selectNoticeDetail", idx));
		return "admin/noticeInfo";
	}

	@ResponseBody
	@RequestMapping(value = "/noticeUpdate.do", produces = "application/json; charset=utf8")
	public String noticeUpdate(HttpServletRequest request) {
		int idx = Integer.parseInt("" + request.getParameter("idx"));
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

	@RequestMapping(value = "/inquiry.do")
	public String inquiry(HttpServletRequest request, ModelMap model) {
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		EgovMap in = new EgovMap();
		in.put("firstindex", paginationInfo.getFirstRecordIndex());
		in.put("recordperpage", paginationInfo.getRecordCountPerPage());
		List<EgovMap> inquiryList = (List<EgovMap>) sampleDAO.list("selectInquiry", in);
		int totCnt = (int) sampleDAO.select("selectInquiryCnt");
		paginationInfo.setTotalRecordCount(totCnt);
		model.addAttribute("inquiryList", inquiryList);
		model.addAttribute("paginationInfo", paginationInfo);
		return "admin/inquiry";
	}

	@RequestMapping(value = "/inquiryInfo.do")
	public String inquiryInfo(HttpServletRequest request, ModelMap model) {
		int idx = Integer.parseInt("" + request.getParameter("idx"));
		EgovMap detail = (EgovMap) sampleDAO.select("selectInquiryDetail", idx);
		model.addAttribute("info", detail);
		model.addAttribute("text", StringEscapeUtils.unescapeHtml3("" + detail.get("text")));
		return "admin/inquiryInfo";
	}

	@ResponseBody
	@RequestMapping(value = "/inquiryDel.do", produces = "application/json; charset=utf8;")
	public String inquiryDel(HttpServletRequest request) throws Exception {
		JSONObject obj = new JSONObject();
		EgovMap in = new EgovMap();
		String result = "success";
		String delList = "" + request.getParameter("delArray");
		String[] delArray = delList.split("-");
		if (delArray != null && delArray.length > 0) {
			for (int i = 0; i < delArray.length; i++) {
				in.put("idx", delArray[i]);
				sampleDAO.update("deleteInquiry", in);
			}
			result = "success";
		} else {
			result = "nothing";
		}
		obj.put("result", result);

		return obj.toJSONString();
	}

	@ResponseBody
	@RequestMapping(value = "/answerInsert.do", produces = "application/json; charset=utf8")
	public String answerInsert(HttpServletRequest request) {
		int idx = Integer.parseInt("" + request.getParameter("idx"));
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

	@RequestMapping(value = "/etcB/{path}.do")
	public String path(@PathVariable("path") String path, HttpServletRequest request, ModelMap model) {
		String type = "" + request.getParameter("type");
		model.addAttribute("info", sampleDAO.select("selectEtcBoardByKind", type));
		model.addAttribute("type", type);
		model.addAttribute("path", path);
		return "admin/etcBoard";
	}

	@ResponseBody
	@RequestMapping(value = "/etcB/{path}Insert.do", produces = "application/json; charset=utf8")
	public String pathInsert(@PathVariable("path") String path, HttpServletRequest request) {
		String type = "" + request.getParameter("type");
		String text = request.getParameter("text");
		EgovMap in = new EgovMap();
		in.put("text", text);
		in.put("kind", type);
		JSONObject obj = new JSONObject();
		try {
			EgovMap info = (EgovMap) sampleDAO.select("selectEtcBoardByKind", in);
			if (info == null) {
				sampleDAO.insert("insertEtcBoard", in);
			} else {
				in.put("idx", info.get("idx"));
				sampleDAO.update("updateEtcBoard", in);
			}
			obj.put("result", "success");
			return obj.toJSONString();
		} catch (Exception e) {
			obj.put("result", "fail");
			return obj.toJSONString();
		}
	}

	@RequestMapping(value = "/user.do")
	public String user(HttpServletRequest request, ModelMap model) {
		String uKind = "all";
		String search = request.getParameter("search");
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null || request.getParameter("pageIndex").equals("")) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		if (request.getParameter("uKind") != null) {
			uKind = "" + request.getParameter("uKind");
		}
		EgovMap in = new EgovMap();
		in.put("first", paginationInfo.getFirstRecordIndex());
		in.put("record", paginationInfo.getRecordCountPerPage());
		in.put("uKind", uKind);
		in.put("search", search);
		List<?> list = (List<?>) sampleDAO.list("selectUserListByAdmin", in);
		paginationInfo.setTotalRecordCount((int) sampleDAO.select("selectUserListByAdminTot", in));
		model.addAttribute("list", list);
		model.addAttribute("pi", paginationInfo);
		model.addAttribute("uKind", uKind);
		model.addAttribute("search", search);
		return "admin/user";
	}

	@RequestMapping(value = "/userInfo.do")
	public String userInfo(HttpServletRequest request, ModelMap model) {
		int idx = Integer.parseInt("" + request.getParameter("idx"));
		model.addAttribute("info", sampleDAO.select("selectUserInfoByAdmin", idx));
		return "admin/userInfo";
	}

	@ResponseBody
	@RequestMapping(value = "/userMoneyUpdate.do", produces = "application/json; charset=utf8")
	public String userMoneyUpdate(HttpServletRequest request) {
		int idx = Integer.parseInt("" + request.getParameter("idx"));
		int money = Integer.parseInt("" + request.getParameter("money"));
		String kind = "" + request.getParameter("kind");
		String type = "" + request.getParameter("type");
		EgovMap in = new EgovMap();
		in.put("idx", idx);
		in.put("kind", kind);
		in.put("type", type);
		in.put("money", money);
		JSONObject obj = new JSONObject();
		try {
			// 기존 정보 가져옴
			EgovMap info = (EgovMap) sampleDAO.select("selectUserInfoByAdmin", idx);
			// 기존 머니
			long beforeM = Long.parseLong(""+info.get(type));
			//int beforeM = (int)info.get(type);
			// 입금인지 출금인지 확인하여 업데이트
			if (kind.equals("deposit")) { // 입금
				sampleDAO.update("updateUserMoneyDeposit", in);
			} else { // 출금
				if (beforeM - money < 0) {
					obj.put("result", "noMoney");
					return obj.toJSONString();
				}
				sampleDAO.update("updateUserMoneyWithdrawal", in);
			}
			// 로그추가
			PaymentLog.Insert(idx, 0, money, "ad_" + type + "_" + kind, null);
			obj.put("result", "success");
			return obj.toJSONString();
		} catch (Exception e) {
			obj.put("result", "fail");
			return obj.toJSONString();
		}
	}

	@RequestMapping("/userDWlog.do")
	public String userDWlog(HttpServletRequest request, ModelMap model) {
		String search = request.getParameter("search");
		String mKind = request.getParameter("mKind");
		String dwKind = request.getParameter("dwKind");
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null || request.getParameter("pageIndex").equals("")) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		EgovMap in = new EgovMap();
		in.put("first", paginationInfo.getFirstRecordIndex());
		in.put("record", paginationInfo.getRecordCountPerPage());
		in.put("search", search);
		if (mKind == null || mKind.equals("") || mKind.equals("all")) {
			if (dwKind == null || dwKind.equals("") || dwKind.equals("all")) {
				in.put("kind1", "ad_point_deposit");
				in.put("kind2", "ad_point_withdrawal");
				in.put("kind3", "ad_balance_deposit");
				in.put("kind4", "ad_balance_withdrawal");
				mKind = "all";
				dwKind = "all";
			} else {
				mKind = "all";
				in.put("kind1", "ad_point_" + dwKind);
				in.put("kind2", "ad_balance_" + dwKind);
			}
		} else {
			if (dwKind == null || dwKind.equals("") || dwKind.equals("all")) {
				dwKind = "all";
				in.put("kind1", "ad_" + mKind + "_withdrawal");
				in.put("kind2", "ad_" + mKind + "_deposit");
			} else {
				in.put("kind1", "ad_" + mKind + "_" + dwKind);
				in.put("kind2", "ad_" + mKind + "_" + dwKind);
			}
		}
		List<?> list = (List<?>) sampleDAO.list("selectUserDwListByAdmin", in);
		paginationInfo.setTotalRecordCount((int) sampleDAO.select("selectUserDwListByAdminTot", in));
		model.addAttribute("list", list);
		model.addAttribute("pi", paginationInfo);
		model.addAttribute("mKind", mKind);
		model.addAttribute("dwKind", dwKind);
		model.addAttribute("search", search);
		return "admin/userDWlog";
	}

	@ResponseBody
	@RequestMapping(value="/userDWlogEdit.do" , produces = "application/json; charset=utf8")
	public String userDWlogEdit(HttpServletRequest request) {
		String type = request.getParameter("type");
		String uid = request.getParameter("uid");
		JSONObject obj = new JSONObject();
		EgovMap in = new EgovMap();
		in.put("uid", uid);
		if(type.equals("e"))
		{ // 편집
			int price = Integer.parseInt(""+request.getParameter("price"));
			in.put("price", price);
			try {
				sampleDAO.update("updateDwPrice" ,in);
				obj.put("result" , "success");
				return obj.toJSONString();
			} catch (Exception e) {
				obj.put("result" , "fail");
				return obj.toJSONString();
			}
		}
		else 
		{// 삭제
			try {
				sampleDAO.delete("deleteDwLog",in);
				obj.put("result" , "success");
				return obj.toJSONString();
			} catch (Exception e) {
				obj.put("result" , "fail");
				return obj.toJSONString();
			}
			
		}
	}
	
	
	@RequestMapping(value = "/purchaseLog.do")
	public String purchaseLog(HttpServletRequest request, ModelMap model) {
		productJson(); // product JSON 정보 
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startD = request.getParameter("startD");
		String endD = request.getParameter("endD");
		if(startD == null)
		{
			startD = sdf.format(today); 
		}
		if(endD == null)
		{
			endD = sdf.format(today);
		}
		String search = request.getParameter("search");
		String orderKind = request.getParameter("orderKind");
		if(orderKind == null || orderKind.equals(""))
		{
			orderKind = "recent";
		}
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null || request.getParameter("pageIndex").equals("")) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		EgovMap in = new EgovMap();
		in.put("first", paginationInfo.getFirstRecordIndex());
		in.put("record", paginationInfo.getRecordCountPerPage());
		in.put("orderKind", orderKind);
		in.put("search", search);
		in.put("startD", startD+" 00:00:00");
		in.put("endD", endD+" 23:59:59");
		List<EgovMap> list = (List<EgovMap>) sampleDAO.list("selectUserPurchaseLog", in);
		for(int listCnt=0; listCnt < list.size(); listCnt++) 
		{
			for(int pmListCnt=0; pmListCnt < pmList.size(); pmListCnt++)
			{
				if(list.get(listCnt).get("product").equals(pmList.get(pmListCnt).Product))
				{
					list.get(listCnt).put("productNm", pmList.get(pmListCnt).Title);
				}
			}
		}
		paginationInfo.setTotalRecordCount((int) sampleDAO.select("selectUserPurchaseLogTot", in));
		model.addAttribute("list", list);
		model.addAttribute("sumCharge", (long)sampleDAO.select("selectUserPurchaseSum" , in));
		model.addAttribute("sumChargeAll", (long)sampleDAO.select("selectUserPurchaseSumAll" , in));
		model.addAttribute("pi", paginationInfo);		 
		model.addAttribute("orderKind", orderKind);		 
		model.addAttribute("search", search);
		model.addAttribute("startD", startD);
		model.addAttribute("endD", endD);
		return "admin/purchaseLog";
	}
	
	@RequestMapping(value = "/purchaseMonthLog.do")
	public String purchaseMonthLog(HttpServletRequest request, ModelMap model) {
		productJson(); // product JSON 정보 
		String search = request.getParameter("search");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		Calendar cal = Calendar.getInstance();
		if(year == null || year.equals(""))
		{
			year = ""+cal.get(Calendar.YEAR);
		}
		if(month == null || month.equals(""))
		{
			month = ""+(cal.get(Calendar.MONTH)+1);
		}
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null || request.getParameter("pageIndex").equals("")) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		EgovMap in = new EgovMap();
		in.put("first", paginationInfo.getFirstRecordIndex());
		in.put("record", paginationInfo.getRecordCountPerPage());
		in.put("year", year);
		in.put("month", month);
		in.put("search", search);
		List<EgovMap> list = (List<EgovMap>) sampleDAO.list("selectUserPurchaseLog", in);
		for(int listCnt=0; listCnt < list.size(); listCnt++) 
		{
			for(int pmListCnt=0; pmListCnt < pmList.size(); pmListCnt++)
			{
				if(list.get(listCnt).get("product").equals(pmList.get(pmListCnt).Product))
				{
					list.get(listCnt).put("productNm", pmList.get(pmListCnt).Title);
				}
			}
		}
		paginationInfo.setTotalRecordCount((int) sampleDAO.select("selectUserPurchaseLogTot", in));
		model.addAttribute("list", list);
		model.addAttribute("sumCharge", (long)sampleDAO.select("selectUserPurchaseSum" , in));
		model.addAttribute("pi", paginationInfo);	
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("search", search);		
		return "admin/purchaseMonthLog";
	}

	@RequestMapping(value = "/goldFeeLog.do")
	public String goldFeeLog(HttpServletRequest request, ModelMap model) {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startD = request.getParameter("startD");
		String endD = request.getParameter("endD");
		if(startD == null)
		{
			startD = sdf.format(today); 
		}
		if(endD == null)
		{
			endD = sdf.format(today);
		}
		String search = request.getParameter("search");
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null || request.getParameter("pageIndex").equals("")) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		EgovMap in = new EgovMap();
		in.put("first", paginationInfo.getFirstRecordIndex());
		in.put("record", paginationInfo.getRecordCountPerPage());
		in.put("search", search);
		in.put("startD", startD+" 00:00:00");
		in.put("endD", endD+" 23:59:59");
		List<EgovMap> list = (List<EgovMap>) sampleDAO.list("selectGoldFeeLog", in);
		paginationInfo.setTotalRecordCount((int) sampleDAO.select("selectGoldFeeLogTot", in));
		model.addAttribute("list", list);
		model.addAttribute("sumFee", (long)sampleDAO.select("selectGoldFeeSum" , in));
		model.addAttribute("sumFeeAll", (long)sampleDAO.select("selectGoldFeeSumAll" , in));
		model.addAttribute("sumGold", (long)sampleDAO.select("selectGoldBackSum" , in));
		model.addAttribute("pi", paginationInfo);
		model.addAttribute("search", search);
		model.addAttribute("startD", startD);
		model.addAttribute("endD", endD);
		return "admin/goldFeeLog";
	}
	
	@RequestMapping(value = "/goldFeeMonthLog.do")
	public String goldFeeMonthLog(HttpServletRequest request, ModelMap model) {
		String search = request.getParameter("search");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		Calendar cal = Calendar.getInstance();
		if(year == null || year.equals(""))
		{
			year = ""+cal.get(Calendar.YEAR);
		}
		if(month == null || month.equals(""))
		{
			month = ""+(cal.get(Calendar.MONTH)+1);
		}
		PaginationInfo paginationInfo = new PaginationInfo();
		if (request.getParameter("pageIndex") == null || request.getParameter("pageIndex").equals("")) {
			paginationInfo.setCurrentPageNo(1);
		} else {
			paginationInfo.setCurrentPageNo(Integer.parseInt("" + request.getParameter("pageIndex")));
		}
		paginationInfo.setRecordCountPerPage(15);
		paginationInfo.setPageSize(10);
		EgovMap in = new EgovMap();
		in.put("first", paginationInfo.getFirstRecordIndex());
		in.put("record", paginationInfo.getRecordCountPerPage());
		in.put("year", year);
		in.put("month", month);
		in.put("search", search);
		List<EgovMap> list = (List<EgovMap>) sampleDAO.list("selectGoldFeeLog", in);
		paginationInfo.setTotalRecordCount((int) sampleDAO.select("selectGoldFeeLogTot", in));
		model.addAttribute("list", list);
		model.addAttribute("sumFee", (long)sampleDAO.select("selectGoldFeeSum" , in));
		model.addAttribute("pi", paginationInfo);	
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("search", search);
		return "admin/goldFeeMonthLog";
	}

	public static void productJson() {
		ClassPathResource resource = new ClassPathResource("json/product.json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			Path path = Paths.get(resource.getURI());
			String content = Files.readString(path);
			pmList = mapper.readValue(content , new TypeReference<ArrayList<ProductModel>>() {});
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
}
