<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
					<h1 class="page-header">
					<c:if test="${type == 'G' }">게임 설명</c:if>
					<c:if test="${type == 'P' }">이용약관</c:if>
					<c:if test="${type == 'C' }">회사소개</c:if>
					<c:if test="${type == 'V' }">개인정보처리방침</c:if>
					 등록</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="row">
								<div class="col-lg-12">
									<form role="form" id="etcBoardForm">
										<input type="hidden" id="type" name="type" value="${type}"/>
										<input type="hidden" id="path" name="path" value="${path}"/>
										<div class="form-group">
											<label>내용</label> 
											<textarea class="form-control" rows="30" name="text" id="smartEditor">${info.text}</textarea>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
					<button type="button" onclick="etcBoardInsert()" class="btn btn-outline btn-default">등록</button>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
	function etcBoardInsert(){
		oEditors.getById["smartEditor"].exec("UPDATE_CONTENTS_FIELD",[]);
		var text = $("#smartEditor").val();
		var type =$("#type").val();
		var path = $("#path").val();
		if(text == "")
		{
			alert("내용을 입력해주세요.");
			$("#smartEditor").focus();
			return;
		}
		$.ajax({
			type:'post',
			data:$("#etcBoardForm").serialize(),
			url:'/holdem/admin/etcB/'+path+'Insert.do',
			success:function(data){
				if(data.result == 'success')
				{
					alert('등록이 완료되었습니다.');
					location.href="/holdem/admin/etcB/"+path+".do?type="+type;
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
		fOnAppLoad : function(){
		   oEditors.getById["smartEditor"].exec("PASTE_HTML", ['${text}']); // 미리 적용할 텍스트 내용이 있는경우 
		  },		
	});
	</script>
</body>
</html>