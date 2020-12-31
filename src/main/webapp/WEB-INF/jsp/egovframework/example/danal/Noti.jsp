<%@ page contentType="text/html; charset=euc-kr"%>
<%@ page import="java.util.*,java.io.*,java.text.*, java.net.*"%>
<%@ page import="kr.co.danal.jsinbi.HttpClient"%>
<%@ include file="/inc/function.jsp"%>
<%
	request.setCharacterEncoding("euc-kr");

	/****************************************************************************
	 * 신용카드 결과 통보. 
	 *  - Noti.jsp
	 *     
	 * 결제시스템 연동에 대한 문의사항 있으시면 기술지원팀으로 연락 주십시오.
	 * DANAL Commerce Division Technique supporting Team 
	 * EMAIL : tech@danal.co.kr
	 *****************************************************************************/
%>
<%
	String Path = new File(application.getRealPath(request.getServletPath())).getParentFile().getAbsolutePath();
	String LogDir = Path + "/log";
	System.out.println("##############" + LogDir);
	try {
		Calendar cal = Calendar.getInstance();
		String ndt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
		String dt = new SimpleDateFormat("yyMMdd").format(cal.getTime());

		String Logs = LogDir + "/noti_" + dt + ".log";

		String QueryString = data2str(getReqMap(request));

		String Out = "[" + ndt + "]" + QueryString + "\n";

		File logDirectory = new File(LogDir);

		if (!logDirectory.exists()) {
			out.print("Fail-Cannot open log file");
			return;
		}

		PrintWriter stream = new PrintWriter(new BufferedWriter(new FileWriter(Logs, true)));

		stream.write(Out);
		stream.close();

		out.print("OK");

		/***************************************************
		 * Noti 성공 시 결제 완료에 대한 작업
		 * - Noti의 결과에 따라 DB작업등의 코딩을 삽입하여 주십시오.
		 * - ORDERID, AMOUNT 등 결제 거래내용에 대한 검증을 반드시 하시기 바랍니다.
		 ****************************************************/
	} catch (Exception e) {
		e.printStackTrace();
		out.print("Fail-" + e.getMessage());
	}
%>
