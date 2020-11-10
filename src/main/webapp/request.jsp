<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.net.SocketException" %>
<%@ page import="javax.net.ssl.SSLSocketFactory" %>
<%@ page import="javax.net.ssl.SSLSocket" %>
<%@ page import="java.security.Security" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
request.setCharacterEncoding("utf-8");
%>
<%!
	//#######################################################################################
	//#####
	//#####	개인실명확인 샘플 소스 (실명확인요청)						나이스평가정보
	//#####
	//#####
	//#######################################################################################
	public String start(String niceUid, String svcPwd, String service, String strGbn, String strResId, String strNm, String strBankCode, String strAccountNo, String svcGbn, String strOrderNo, String svc_cls, String inq_rsn){
        
    	String result = "";
        
    	BufferedReader in = null;
      	PrintWriter out = null;
        
        try{
        	Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        	SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
          	SSLSocket soc = (SSLSocket)factory.createSocket("secure.nuguya.com", 443);
            
            // 타임아웃  +++++++++++++++++++++++++++++++++++++++++++++++++++++ 
            soc.setSoTimeout(10*1000);	// 타임아웃 10초 
        	soc.setSoLinger(true, 10);	
            soc.setKeepAlive(true);		
            // 타임아웃  +++++++++++++++++++++++++++++++++++++++++++++++++++++ 
            
            out = new PrintWriter(soc.getOutputStream());
            in  = new BufferedReader(new InputStreamReader(soc.getInputStream()), 8 * 1024);
			
			result = rlnmCheck(out, in, niceUid, svcPwd, service, strGbn, strResId, strNm, strBankCode, strAccountNo, svcGbn, strOrderNo,  svc_cls, inq_rsn);
                       
        }catch(SocketException e){
        	System.out.println(e.getMessage());
        }catch (Exception e){
        	e.printStackTrace();
        }finally{
            if (out != null){
                try{
                    out.close();
                }catch (Exception e){
                }
            }
            if (in != null){
                try{
                    in.close();
                }catch (Exception e){
                }
            }
        }
			
			return result;
		
    }


    public String rlnmCheck(PrintWriter out, BufferedReader in, String niceUid, String svcPwd, String service, String strGbn, String strResId, String strNm, String strBankCode, String strAccountNo, String svcGbn, String strOrderNo, String svc_cls, String inq_rsn) throws IOException
    {
        StringBuffer sbResult = new StringBuffer();
        
        String contents = "niceUid=" + niceUid + "&svcPwd=" + svcPwd + "&service=" + service + "&strGbn=" + strGbn + "&strResId=" + strResId + "&strNm=" + strNm + "&strBankCode=" + strBankCode + "&strAccountNo=" + strAccountNo + "&svcGbn=" + svcGbn + "&strOrderNo=" + strOrderNo + "&svc_cls=" + svc_cls + "&inq_rsn=" + inq_rsn + "&seq=0000001";

        out.println("POST https://secure.nuguya.com/nuguya2/service/realname/sprealnameactconfirm.do HTTP/1.1"); //UTF-8 URL
		//out.println("POST https://secure.nuguya.com/nuguya/service/realname/sprealnameactconfirm.do HTTP/1.1");
        out.println("Host: secure.nuguya.com");
        out.println("Connection: Keep-Alive");
        out.println("Content-Type: application/x-www-form-urlencoded");
        out.println("Content-Length: " + contents.length());
        out.println();
        out.println(contents);
        out.flush();
        
        String line = null;
        int i=0;
        boolean notYet = true;
        while((line = in.readLine())!= null){
            i++;
            if (notYet && line.indexOf("HTTP/1.") == -1 ){
            	continue;
            }
            if (notYet && line.indexOf("HTTP/1.") > -1 ){
            	notYet = false;
            }
            
            if (line.indexOf("HTTP/1.") > -1 ){
            	notYet = false;
            }
            if (line.startsWith("0") ){
            	break;
            }
            if (line == null){
                break;
            }
        	
            if(i==9) sbResult.append(line);
        }
		
				System.out.println(sbResult.toString());
        return sbResult.toString();
    }
%>
**<%=request.getParameter("service"     )%>**
<%

	request.setCharacterEncoding("utf-8");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	//##################################################
	//###### ▣ 회원사 ID 설정   - 계약시에 발급된 회원사 ID를 설정하십시오. ▣
	//###### ▣ 회원사 PW 설정   - 계약시에 발급된 회원사 PASSWORD를 설정하십시오. ▣
	//###### ▣ 조회사유  설정   - 10:회원가입 20:기존회원가입 30:성인인증 40:비회원확인 90:기타사유 ▣
	//###### ▣ 개인/사업자 설정 - 1:개인 2:사업자 ▣
	//##################################################
  	String niceUid = "NID202903";	// 나이스평가정보에서 고객사에 부여한 구분 id
	String svcPwd  = "202903";			// 나이스평가정보에서 고객사에 부여한 서비스 이용 패스워드
	String inq_rsn = "10";				// 조회사유 - 10:회원가입 20:기존회원가입 30:성인인증 40:비회원확인 90:기타사유
	String strGbn  = "2";					// 1 : 개인, 2: 사업자
	//#################################################
	
	String service      = request.getParameter("service"     )==null?"":request.getParameter("service"     ); //서비스구분 1=계좌소유주확인 2=계좌성명확인 3=계좌유효성확인
	String strResId     = request.getParameter("JUMINNO"     )==null?"":request.getParameter("JUMINNO"    );	//생년월일(사업자 번호,법인번호)
	String strNm        = request.getParameter("USERNM"      )==null?"":request.getParameter("USERNM"       );	//계좌소유주명
	String strBankCode  = request.getParameter("strBankCode" )==null?"":request.getParameter("strBankCode" );	//은행코드(전문참조)
	String strAccountNo = request.getParameter("strAccountNo")==null?"":request.getParameter("strAccountNo");	//계좌번호
	String svcGbn       = request.getParameter("svcGbn"      )==null?"":request.getParameter("svcGbn"      );	//업무구분(전문참조)
	String svc_cls      = request.getParameter("svc_cls"     )==null?"":request.getParameter("svc_cls"     ); //내-외국인구분
	String strOrderNo   = sdf.format(new Date()) + (Math.round(Math.random() * 10000000000L) + "");           //주문번호 : 매 요청마다 중복되지 않도록 유의

	out.println("strNm"+strNm); 
  	String result = start(niceUid, svcPwd, service, strGbn, strResId, strNm, strBankCode, strAccountNo, svcGbn, strOrderNo, svc_cls, inq_rsn);
	String[] results = result.split("\\|");
	String resultOrderNo = results[0];
	String resultCode    = results[1];
	String resultMsg     = results[2];
		
	// P000: 정상응답일때 송신되는 코드
	// E999: 시스템이상

	out.println("<br><font color=blue>주문번호 : "   + resultOrderNo + "</font><br>");
	out.println("<br><font color=blue>응답코드 : "   + resultCode + "</font><br>");
	out.println("<br><font color=blue>응답메시지 : " + resultMsg + "</font><br>");
	out.println("<script>parent.onrequest('"+resultMsg+"' );</script>");
%>