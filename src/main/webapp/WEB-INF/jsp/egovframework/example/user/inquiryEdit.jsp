<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>WinGame</title>
</head>
<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/se2/js/HuskyEZCreator.js" charset="utf-8"></script>
<body>
<form id="inquiryForm">
	문의제목 : <input type="text" name="title" id="title" value="${inquiryDetail.title}"/> <br/>
	문의 내용 <textarea class="form-control"rows="30" name="text" id="smartEditor">${inquiryDetail.text}</textarea><br/>
	※ 처음 등록한 비밀번호는 변경할 수 없습니다.  
	<br/>
	<button type="button" onclick="inquiryEdit()">수정</button>
</form>
<script>
function inquiryEdit(){
	oEditors.getById["smartEditor"].exec("UPDATE_CONTENTS_FIELD",[]);
	var title = $("#title").val();
	var text = $("#smartEditor").val();
	if(title == "")
	{
		alert("제목을 입력해주세요.");
		$("#title").focus();
		return;
	}
	if(text == "")
	{
		alert("내용을 입력해주세요.");
		$("#smartEditor").focus();
		return;
	}
	$.ajax({
		type : 'post',
		data : $("#inquiryForm").serialize(),
		url : '/holdem/user/inquiryEditProcess.do?idx='+${inquiryDetail.idx},
		success:function(data){
			if(data.result == 'success')
			{
				alert('수정이 완료되었습니다.');	
				location.href="/holdem/user/inquiry.do";
			}
			else
			{
				alert('수정에 실패하였습니다. \n다시 시도해주세요.');
			}
		},
		error:function(e){
			console.log('ajax error' + e);
		}
	})
}
var oEditors = []; 
nhn.husky.EZCreator.createInIFrame({ 
	oAppRef : oEditors, 
	elPlaceHolder : "smartEditor",  
	sSkinURI : "${pageContext.request.contextPath}/se2/SmartEditor2Skin.html",  
	fCreator : "createSEditor2", 
	htParams : {  
		bUseToolbar : true,   // 툴사용여부 
		bUseVerticalResizer : false, // 입력창 크기 조절 바 
		//bSkipXssFilter : true, // xss 필터 
		bUseModeChanger : false  // 텍스트 모드 변경 
		},
	fOnAppLoad : function(){
	   oEditors.getById["smartEditor"].exec("PASTE_HTML", ['${text}']); // 미리 적용할 텍스트 내용이 있는경우 
	  },		
});
</script>
</body>
</html>