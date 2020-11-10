<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../frame/admintop.jsp" flush="true" />
</head>
<body>
	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">메인 유튜브 설정</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="row">
								<div class="col-lg-6">
									<form role="form">
										<div class="form-group">
											<label>현재 등록되어있는 유튜브</label>
											<c:if test="${youtube != null }">
												<iframe style="width:100%; height:500px;" src="https://www.youtube.com/embed/${youtube.saveNm}" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" ></iframe>
											</c:if>
											<c:if test="${youtube == null }">
												<p class="help-block">현재 등록되어있는 유튜브가 없습니다.</p>
											</c:if>
										</div>
										<label>유튜브 링크 주소</label> 
										<div class="form-group input-group">
											<input class="form-control" id="youtube" name="youtube">
											<span class="input-group-btn">
												<button type="button" onclick="javascript:setYoutube()" class="btn btn-info">등록</button>
											</span>
										</div>
										<p class="help-block">주소창에있는 주소 복사 (하나만 입력 가능)<br/>※ 기존에 등록되어있는 유튜브는 삭제됩니다.</p>
									</form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
		function setYoutube(){
			var youtube = $("#youtube").val();
			if(youtube == "" || youtube == null)
			{
				alert("유튜브 주소를 입력해주세요.");
				$("#youtube").focus();
				return;
			}
			if(youtube.match(/https/g).length > 1)
			{
				alert("유튜브 주소는 하나만 입력해주세요.");
				$("#youtube").focus();
				return;
			}
			$.ajax({
				type:'post',
				data : {'youtube' : youtube},
				url : '/holdem/admin/youtubeSet.do',
				success:function(data){
					alert("유튜브 등록이 완료되었습니다.");
					location.reload();
				},
				error:function(){
					console.log('ajax Error');
				}
			})
		}
	</script>
</body>
</html>