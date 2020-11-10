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
					<h1 class="page-header">메인 이미지 설정</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="table-responsive">
								<table class="table">
									<thead>
										<tr>
											<th><input id="allChk" type="checkbox" onclick="allChk(this)"/></th>
											<th>이미지</th>
											<th>노출 순번</th>
											<th>등록일</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="result" items="${imageList}">
											<tr>
												<td><input type="checkbox" name="arrayIdx" value="${result.idx}"/></td>
												<td>${result.saveNm}</td>
												<td>${result.orderNum}</td>
												<td><fmt:formatDate value="${result.regDate}" pattern="yyyy-MM-dd HH:mm"/></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
			<button type="button" onclick="listDel()" class="btn btn-outline btn-danger">선택삭제</button>
			<button type="button" onclick="location.href='/holdem/admin/imageAdd.do'" class="btn btn-outline btn-default">이미지 추가</button>
			<button type="button" onclick="location.href='/holdem/admin/imageEdit.do'" class="btn btn-outline btn-default">이미지 수정</button>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
		function allChk(obj) {
			var chkObj = document.getElementsByName("arrayIdx");
			var rowCnt = chkObj.length - 1;
			var check = obj.checked;
			if (check) 
			{
				for (var i = 0; i <= rowCnt; i++) {
					if (chkObj[i].type == "checkbox")
					{
						chkObj[i].checked = true;						
					}
				}
			} 
			else 
			{
				for (var i = 0; i <= rowCnt; i++) {
					if (chkObj[i].type == "checkbox") 
					{
						chkObj[i].checked = false;
					}
				}
			}
		}
		function listDel() {
			var idx = "";
			var idxChk = document.getElementsByName("arrayIdx");
			var chked = false;
			var indexid = false;
			for (i = 0; i < idxChk.length; i++) {
				if (idxChk[i].checked) 
				{
					if (indexid) 
					{
						idx = idx + '-';
					}
					idx = idx + idxChk[i].value;
					indexid = true;
				}
			}
			if (!indexid) 
			{
				alert("삭제할 이미지를 선택해주세요.");
				return false;
			}
			var param = {"delArray" : idx};
			if (confirm("선택한 이미지들을 삭제하시겠습니까?")) {
				jQuery.ajax({
					type : 'post',
					data : param,
					url : "/holdem/admin/imageDel.do",
					success : function(data) {
						if (data.result == 'success') {
							alert("삭제되었습니다.");
							location.reload();
						} else {
							alert("에러가 발생했습니다. 다시 시도해주세요");
							location.reload();
						}
					},
					errer : function(e) {
						console.log('error' + e);
					}
				});
			} 
			else 
			{
				return false;
			}
		}
	</script>
</body>
</html>