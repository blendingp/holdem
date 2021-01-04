<%@ page import="java.util.*,java.io.*,java.net.*,java.text.*,kr.co.danal.jsinbi.*"%>
<%@ page contentType="text/html; charset=euc-kr"%>
<%
	response.setHeader("Pragma", "No-cache");
	request.setCharacterEncoding("euc-kr");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<link href="/holdem/css/danal/style.css" type="text/css" rel="stylesheet"
	media="all" />
<title>*** 신용카드결제 승인요청 페이지 ***</title>
</head>
<%@ include file="/inc/function.jsp"%>
<body>
<%
	String RES_STR = toDecrypt((String) request.getParameter("RETURNPARAMS"));
	Map retMap = str2data(RES_STR);
	String returnCode = (String) retMap.get("RETURNCODE");
	String returnMsg = (String) retMap.get("RETURNMSG");
	System.out.println("m : " + (String)request.getParameter("m"));
	System.out.println("c : " + (String)request.getParameter("c"));
	System.out.println("idx : " + (String)request.getParameter("midx"));
	//*****  신용카드 인증결과 확인 *****************
	if (returnCode == null || !"0000".equals(returnCode)) {
		// returnCode가 없거나 또는 그 결과가 성공이 아니라면 실패 처리
		System.out.println("Authentication failed. " + returnMsg + "[" + returnCode + "]");
		return;
	}

	/*[ 필수 데이터 ]***************************************/
	Map REQ_DATA = new HashMap();
	Map RES_DATA = new HashMap();

	/**************************************************
	 * 결제 정보
	 **************************************************/
	REQ_DATA.put("TID", (String) retMap.get("TID"));
	REQ_DATA.put("AMOUNT", (String)request.getParameter("m")); // 최초 결제요청(AUTH)시에 보냈던 금액과 동일한 금액을 전송

	/**************************************************
	 * 기본 정보
	 **************************************************/
	REQ_DATA.put("TXTYPE", "BILL");
	REQ_DATA.put("SERVICETYPE", "DANALCARD");

	RES_DATA = CallCredit(REQ_DATA, false);

	System.out.println(RES_DATA.get("RETURNCODE"));
	System.out.println("0000".equals(RES_DATA.get("RETURNCODE")));
	if ("0000".equals(RES_DATA.get("RETURNCODE"))) {
		System.out.println("formsubmit..");
%>
<form name="form" ACTION="/holdem/danal/success.do" METHOD="POST">
	<input TYPE="HIDDEN" NAME="RETURNCODE" VALUE="<%=RES_DATA.get("RETURNCODE")%>">
	<input TYPE="HIDDEN" NAME="RETURNMSG" VALUE="<%=RES_DATA.get("RETURNMSG")%>">
	<input TYPE="HIDDEN" NAME="CODE" VALUE="<%=(String)request.getParameter("c")%>">
	<input TYPE="HIDDEN" NAME="MONEY" VALUE="<%=(String)request.getParameter("m")%>">
	<input TYPE="HIDDEN" NAME="MIDX" VALUE="<%=(String)request.getParameter("midx")%>">
</form>
<script Language="JavaScript">
	document.form.submit();
</script>
<%
	} else {
		String RETURNCODE = (String) RES_DATA.get("RETURNCODE");
		String RETURNMSG = (String) RES_DATA.get("RETURNMSG");
%>
<%@ include file="../danal/Error.jsp"%>
<%-- <jsp:include page="../danal/Error.jsp" /></jsp:include> --%>
<%
	}
%>
</form>
</body>
</html>