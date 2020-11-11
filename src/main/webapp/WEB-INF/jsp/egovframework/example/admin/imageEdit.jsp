<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
<body>
	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">메인 이미지 수정</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-heading">순번이 같은경우 최신순부터 노출</div>
						<div class="panel-body">
							<div class="table-responsive">
								<div class="form-group">
									<label>변경 이미지를 등록하지않으면, 기존 이미지가 유지됩니다.</label><br/>
								</div>
								<form id="imageForm">
									<table class="table">
										<thead>
											<tr>
												<th>이미지</th>
												<th>변경이미지 등록</th>
												<th>노출 순번</th>
												<th>등록일</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="result" items="${imageList}">
												<input type="hidden" name="idx[]" value="${result.idx}"/>
												<tr>
													<td><img style="max-width:100%; height:150px;" src="/filePath/holdem/photo/${result.saveNm}"/></td>
													<td><input type="file" name="file[]"/></td>
													<td><input type="number" name="orderNum[]" value="${result.orderNum}"/></td>
													<td><fmt:formatDate value="${result.regDate}" pattern="yyyy-MM-dd HH:mm"/></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
			<button type="button" onclick="imageEdit()" class="btn btn-outline btn-default">수정</button>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
		function imageEdit(){
			var form = $("#imageForm")[0];
			var data = new FormData(form);
			$.ajax({
				type:'post',
				url : '/holdem/admin/imageUpdate.do',
				processData:false,
				contentType:false,
				data: data,
				success:function(data){
					if(data.success == 'success')
					{
						alert("이미지가 수정되었습니다. ");
						location.href="/holdem/admin/image.do";
					}
					else
					{
						alert("수정 중 오류가 발생했습니다 \n다시 시도해주세요.");
					}
				},
				error:function(e){
					console.log('ajaxError' + e);
				}
			});			
		}
	</script>
</body>
</html>