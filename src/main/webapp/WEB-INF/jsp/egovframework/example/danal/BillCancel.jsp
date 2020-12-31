<%@ page import="java.util.*,java.io.*,java.net.*,java.text.*,kr.co.danal.jsinbi.*"%>
<%@ page contentType="text/html; charset=euc-kr" pageEncoding="euc-kr"%>
<%@ include file="/inc/function.jsp" %>
<%
	Map REQ_DATA = new HashMap();
	Map RES_DATA = new HashMap();
	
	/**************************************************
	 * 결제 정보
	 **************************************************/
	REQ_DATA.put("TID", "xxxxxxxxxxxxx");
	
	/**************************************************
	 * 기본 정보
	 **************************************************/
	REQ_DATA.put("CANCELTYPE", "C");
  REQ_DATA.put("AMOUNT", (String) request.getParameter("amount"));

	/**************************************************
	 * 취소 정보
	 **************************************************/
	REQ_DATA.put("CANCELREQUESTER", "CP_CS_PERSON");
	REQ_DATA.put("CANCELDESC", "Item not delivered");


	REQ_DATA.put("TXTYPE", "CANCEL");
	REQ_DATA.put("SERVICETYPE", "DANALCARD");

	
	RES_DATA = CallCredit(REQ_DATA, false);
	
	if( RES_DATA.get("RETURNCODE").equals("0000")){
		// 결제 성공 시 작업 진행
		out.print( data2str(RES_DATA) );
	} else {
		// 결제 실패 시 작업 진행
		out.print( data2str(RES_DATA) );
	}
%>