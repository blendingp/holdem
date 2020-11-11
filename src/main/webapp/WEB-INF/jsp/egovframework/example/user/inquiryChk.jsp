<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>WinGame</title>
</head>
<body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<input type="password" placeholder="비밀번호를 입력하세요." name="pw" id="pw" onkeypress="if(event.keyCode==13) {javascript:inquiryChk(); return false;}">
<button onclick="inquiryChk()">확인</button>
<script>
function inquiryChk(){
	var pw = document.getElementById("pw").value;
	var idx= "${idx}";
	$.ajax({
		type:'post',
		url:'/holdem/user/inquiryPwChk.do?idx='+idx,
		data: {"pw" :pw},
		success:function(data){
			if(data.result == 'success'){
				window.close(); // 자신 창 닫음 
				window.opener.location.href="/holdem/user/inquiryDetail.do?idx="+idx; // 부모창에서 페이지 이동 
			}else{
				alert("비밀번호가 일치하지 않습니다.");
				location.reload();
			}
		},
		error:function(e){
			console.log('ajaxError'+e);
		}
	})
}
</script>
</body>
</html>