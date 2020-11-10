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
					<h1 class="page-header">공지사항 등록</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="row">
								<div class="col-lg-12">
									<form role="form" id="noticeForm">
										<input type="hidden" name="idx" value="${info.idx}"/>
										<div class="form-group">
											<label>상단 노출은 3건까지 가능합니다.</label><br/>
											<label>※ 상단 노출이 3건을 초과하면, 등록일 기준 최신 3건으로 노출됩니다.</label><br/>
										</div>
										<div class="form-group">
											<label>제목</label> 
											<input type="text" id="title" name="title" class="form-control" value="${info.title}">
										</div>
										<div class="form-group">
											<label>내용</label> 
											<textarea class="form-control" rows="20" name="text" id="smartEditor">${info.text}</textarea>
										</div>
										<div class="row">
											<div class="col-lg-6">
												<div class="form-group">
													<label>상단 고정</label>
													<div class="radio">
														<label class="radio-inline">
															<input type="radio" name="topYn" value="Y" <c:if test="${info.topYn == 'Y'}">checked</c:if>>Y
														</label>
														<label class="radio-inline">
															<input type="radio" name="topYn" value="N" <c:if test="${info.topYn == 'N'}">checked</c:if>>N
														</label>
													</div>
												</div>
											</div>
											<div class="col-lg-6">
												<div class="form-group">
													<label>노출 여부</label>
													<div class="radio">
														<label class="radio-inline">
															<input type="radio" name="expsYn" value="Y" <c:if test="${info.expsYn == 'Y'}">checked</c:if>>Y
														</label>
														<label class="radio-inline">
															<input type="radio" name="expsYn" value="N" <c:if test="${info.expsYn == 'N'}">checked</c:if>>N
														</label>
													</div>
												</div>
											</div>
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
					<button type="button" onclick="noticeUpdate()" class="btn btn-outline btn-default">수정</button>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
	function noticeUpdate(){
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
			type:'post',
			data:$("#noticeForm").serialize(),
			url:'/holdem/admin/noticeUpdate.do',
			success:function(data){
				if(data.result == 'success')
				{
					alert('수정이 완료되었습니다.');
					location.href="/holdem/admin/notice.do";
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