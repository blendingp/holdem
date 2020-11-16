<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.*" %>  

<%
    NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();
    
    String sSiteCode = "BS429";            // NICE로부터 부여받은 사이트 코드
    String sSitePassword = "QyPjnDjRKQle";        // NICE로부터 부여받은 사이트 패스워드
    
    String sRequestNumber = "REQ0000000001";            // 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로 
                                                        // 업체에서 적절하게 변경하여 쓰거나, 아래와 같이 생성한다.
    sRequestNumber = niceCheck.getRequestNO(sSiteCode);
      session.setAttribute("REQ_SEQ" , sRequestNumber);    // 해킹등의 방지를 위하여 세션을 쓴다면, 세션에 요청번호를 넣는다.
      
       String sAuthType = "";          // 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서
       
       String popgubun     = "N";        //Y : 취소버튼 있음 / N : 취소버튼 없음
    String customize     = "";        //없으면 기본 웹페이지 / Mobile : 모바일페이지
    
    String sGender = "";             //없으면 기본 선택 값, 0 : 여자, 1 : 남자 
    String midx =""+ request.getAttribute("midx");
    
    
    // CheckPlus(본인인증) 처리 후, 결과 데이타를 리턴 받기위해 다음예제와 같이 http부터 입력합니다.
    //리턴url은 인증 전 인증페이지를 호출하기 전 url과 동일해야 합니다. ex) 인증 전 url : http://www.~ 리턴 url : http://www.~
    String sReturnUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/holdem/identifyresult.do?midx="+midx;      // 성공시 이동될 URL
    String sErrorUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/holdem/failresult.do";          // 실패시 이동될 URL
    // 입력될 plain 데이타를 만든다.
    String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
                        "8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
                        "9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
                        "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
                        "7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl +
                        "11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
                        "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize + 
                        "6:GENDER" + sGender.getBytes().length + ":" + sGender;
    
    String sMessage = "";
    String sEncData = "";
    
    int iReturn = niceCheck.fnEncode(sSiteCode, sSitePassword, sPlainData);
    if( iReturn == 0 )
    {
        sEncData = niceCheck.getCipherData();
    }
    else if( iReturn == -1)
    {
        sMessage = "암호화 시스템 에러입니다.";
    }    
    else if( iReturn == -2)
    {
        sMessage = "암호화 처리오류입니다.";
    }    
    else if( iReturn == -3)
    {
        sMessage = "암호화 데이터 오류입니다.";
    }    
    else if( iReturn == -9)
    {
        sMessage = "입력 데이터 오류입니다.";
    }    
    else
    {
        sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
    }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here<%=midx+"**"%></title>

  <script src="https://d3e54v103j8qbb.cloudfront.net/js/jquery-3.4.1.min.220afd743d.js?site=5e9e98d3908fa91a88dbb4ca" type="text/javascript" integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo=" crossorigin="anonymous"></script>
  <script language='javascript'>  
    var midx = "${midx}";    
    window.name ="Parent_window";
    
    $(document).ready(function(){    	
        var windowRef = window.open('', 'popupChk', 'width=500, height=550, top=0, left=0, fullscreen=yes, menubar=no, status=no, toolbar=no, titlebar=no, location=no, scrollbar=no');                
        $(windowRef.document).ready(function()
        {
            console.log("windowRef is readyed");
            fnPopup();
        });        
    });    
    
    function fnPopup(){
        
        document.form_chk.action = "https://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb";
        document.form_chk.target = "popupChk";
        document.form_chk.submit();
    }
    
    function getphone(hpn, sBirthDate, idnum, sName, di, suc, aa){
    	console.log("getphone ========");
        if(suc==0){
			alert("성공");
        }
        /* else{
            alert("이미 가입된 상태입니다.");
            $("#phone").val("");
            $(".jrow3").html("<a href='javascript:fnPopup()' class='phonebtn w-inline-block'><img src='images/휴대폰인증버튼.png' alt='' class='phoneimg'></a>");                                        
        } */		        
    }
    
    function authFail(){
    	alert("본인인증 실패");
    }
    </script>
</head>
<body>
   <form name="form_chk" method="post">
        <input type="hidden" name="m" value="checkplusSerivce">                        <!-- 필수 데이타로, 누락하시면 안됩니다. -->
        <input type="hidden" name="EncodeData" value="<%= sEncData %>">        <!-- 위에서 업체정보를 암호화 한 데이타입니다. -->
    </form>
    
    <!-- 
    <a href="javascript:fnPopup()" class="certificationbtn w-button">본인인증</a>
    <input type="hidden" name="phone" id="phone"/> -->
</body>
</html>