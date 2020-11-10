<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../frame/admintop.jsp" flush="true" />
</head>
<script type="text/javascript" src="${pageContext.request.contextPath}/se2/js/HuskyEZCreator.js" charset="utf-8"></script>
<body>
	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">1:1문의 답변</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="row">
								<div class="col-lg-12">
									<form role="form" id="inquiryForm">
										<input type="hidden" name="idx" value="${info.idx}"/>
										<div class="form-group">
											<label>제목</label> 
											<pre>${info.title}</pre>
										</div>
										<div class="form-group">
											<label>문의 내용</label>
											<pre style="max-height:450px;">${text}</pre>
										</div>
										<div class="form-group">
											<label>답변 내용</label> 
											<textarea class="form-control" rows="20" name="answer" id="smartEditor">${info.answer}</textarea>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
					<button type="button" onclick="answerInsert()" class="btn btn-outline btn-default">답변등록</button>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
	function answerInsert(){
		oEditors.getById["smartEditor"].exec("UPDATE_CONTENTS_FIELD",[]);
		var answer = $("#smartEditor").val();
		if(answer == "")
		{
			alert("답변을 입력해주세요.");
			$("#smartEditor").focus();
			return;
		}
		$.ajax({
			type:'post',
			data:$("#inquiryForm").serialize(),
			url:'/holdem/admin/answerInsert.do',
			success:function(data){
				if(data.result == 'success')
				{
					alert('답변 등록이 완료되었습니다.');
					location.href="/holdem/admin/inquiry.do";
				}
				else 
				{
					alert('오류가 발생했습니다. 다시 시도해주세요.')
				}
			},
			error:function(e){
				console.log('ajax Error' + e);
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
	});
	</script>
</body>
</html>