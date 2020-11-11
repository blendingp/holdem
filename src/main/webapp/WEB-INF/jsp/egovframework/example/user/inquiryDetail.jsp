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
<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
<script>
function inquiryDel(){
	if(confirm("글을 삭제하시겠습니까?")){
		jQuery.ajax({
			type:'post',
			url:'/holdem/user/inquiryDel.do?idx='+${inquiryDetail.idx},
			success:function(data){
				if(data.result=='success'){
					alert("삭제되었습니다. ");
					location.href="/holdem/user/inquiry.do";
				}else{
					alert("에러가 발생했습니다. 다시 시도해주세요.");
				}
			},
			error:function(e){
				console.log('error'+e);
			}
		})	
	}
}
</script>
</head>
<body>
<a href="/holdem/user/inquiry.do">목록으로</a><br/>
제목 : ${inquiryDetail.title}<br/>
내용 : ${text}<br/>
<c:if test="${inquiryDetail.answer != null}">
	답변 : ${inquiryDetail.answer}<br/>
</c:if>
<c:if test="${inquiryDetail.answer == null}">
	<a href="/holdem/user/inquiryEdit.do?idx=${inquiryDetail.idx}">수정하기</a> 
	<a href="javascript:inquiryDel()">삭제하기</a><br/>
</c:if> 
※관리자가 답변을 한 후에는 수정, 삭제가 불가능 합니다. 
</body>
</html>