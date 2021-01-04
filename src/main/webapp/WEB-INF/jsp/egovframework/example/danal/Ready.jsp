<%@ page import="java.util.*,java.io.*,java.net.*,java.text.*,kr.co.danal.jsinbi.*"%>
<%@ page contentType="text/html; charset=euc-kr" pageEncoding="euc-kr"%>
<%
	response.setHeader("Pragma", "No-cache");
	request.setCharacterEncoding("euc-kr");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<link href="/holdem/css/danal/style.css" type="text/css" rel="stylesheet" media="all" />
<title>*** �ſ�ī�� ������û ***</title>
</head>
<%@ include file="/inc/function.jsp"%>
<body>
<%
	/*[ �ʼ� ������ ]***************************************/
	Map REQ_DATA = new HashMap();
	Map RES_DATA = null;

	/******************************************************
	 *  RETURNURL 	: CPCGI�������� Full URL�� �־��ּ���
	 *  CANCELURL 	: BackURL�������� Full URL�� �־��ּ���
	 ******************************************************/
	 System.out.println("M ::: " + session.getAttribute("money"));
	 System.out.println("C ::: " + session.getAttribute("code"));
	 System.out.println("midx ::: " + session.getAttribute("midx"));
	String RETURNURL = request.getScheme() + "://" + request.getServerName() +":" + request.getServerPort()+"/holdem/danal/cpcgi.do?m="+session.getAttribute("money")+"&c="+session.getAttribute("code")+"&midx="+session.getAttribute("midx");
	String CANCELURL = request.getScheme() + "://" + request.getServerName() +":" + request.getServerPort()+"/holdem/danal/cancel.do";

	/**************************************************
	 * SubCP ����
	 **************************************************/
	REQ_DATA.put("SUBCPID", "");

	/**************************************************
	 * ���� ����
	 **************************************************/
	REQ_DATA.put("AMOUNT", (String) request.getParameter("amount"));
	REQ_DATA.put("CURRENCY", "410");
	REQ_DATA.put("ITEMNAME", URLDecoder.decode((String) request.getParameter("itemname") , "utf8"));
	REQ_DATA.put("USERAGENT", "WP");
	REQ_DATA.put("ORDERID", (String) request.getParameter("orderid"));
	REQ_DATA.put("OFFERPERIOD", "���������Ⱓ����");

	/**************************************************
	 * �� ����
	 **************************************************/
	REQ_DATA.put("USERNAME", (String) request.getParameter("username")); // ������ �̸�
	REQ_DATA.put("USERID", (String) request.getParameter("userid")); // ����� ID
	REQ_DATA.put("USEREMAIL", (String) request.getParameter("useremail")); // �Һ��� email����ó

	/**************************************************
	 * URL ����
	 **************************************************/
	REQ_DATA.put("CANCELURL", CANCELURL);
	REQ_DATA.put("RETURNURL", RETURNURL);

	/**************************************************
	 * �⺻ ����
	 **************************************************/
	REQ_DATA.put("TXTYPE", "AUTH");
	REQ_DATA.put("SERVICETYPE", "DANALCARD");
	REQ_DATA.put("ISNOTI", "N");
	REQ_DATA.put("BYPASSVALUE", "this=is;a=test;bypass=value"); // BILL���� �Ǵ� Noti���� �������� ��. '&'�� ����� ��� ���� �߸��ԵǹǷ� ����.

	RES_DATA = CallCredit(REQ_DATA, true);

	if ("0000".equals(RES_DATA.get("RETURNCODE"))) {
%>
<form name="form" ACTION="<%=RES_DATA.get("STARTURL")%>" METHOD="POST">
	<input TYPE="HIDDEN" NAME="STARTPARAMS" VALUE="<%=RES_DATA.get("STARTPARAMS")%>"> 
	<input TYPE="HIDDEN" NAME="AMOUNT" VALUE="<%=RES_DATA.get("AMOUNT")%>"> 
</form>
<script>
	document.form.submit();
</script>
<%
	} else {
		String RETURNCODE = (String) RES_DATA.get("RETURNCODE");
		System.out.println("RETURNCODE  :" + RETURNCODE);
		String RETURNMSG = (String) RES_DATA.get("RETURNMSG");
		String BackURL = "Javascript:self.close()";
%>
<%@ include file="../danal/Error.jsp"%>
<%
	}
%>
</form> 
</body>
</html>


