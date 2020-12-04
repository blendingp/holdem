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
	<script>
		function fn_egov_link_page(pageNo) {
			document.listForm.pageIndex.value = pageNo;
			document.listForm.action = "<c:url value='/admin/aiSetting.do'/>";
			document.listForm.submit();
		}
	</script>

	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">유저 AI 설정</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="table-responsive">
								<table class="table table-hover">
									<thead>
										<tr>
											<th><input id="allChk" type="checkbox" onclick="allChk(this)"/></th>
											<th>닉네임</th>
											<th>AI구분</th>
											<th>AI설정</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${list}">
											<tr>
												<td><input type="checkbox" name="arrayIdx" value="${item.midx}"/></td>
												<td>${item.nickname}</td>
												<td>${item.ai ? 'ai유저' : '일반유저'}</td>
												<td>
													<c:if test="${item.ai}">
														<button type="button" onclick="ChangeSetOne('0' , '${item.midx}')" class="btn btn-outline btn-warning">일반유저로 변경</button>		
													</c:if>
													<c:if test="${!item.ai}">
														<button type="button" onclick="ChangeSetOne('1' , '${item.midx}')" class="btn btn-outline btn-primary">ai유저로 변경</button>
													</c:if>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-12" style="text-align: center;">
								<ul class="pagination">
									<ui:pagination paginationInfo="${pi}" type="admin" jsFunction="fn_egov_link_page" />
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
			<button type="button" onclick="ChangeSet('1')" class="btn btn-outline btn-primary">선택 AI로 변경</button>
			<button type="button" onclick="ChangeSet('0')" class="btn btn-outline btn-warning">선택 일반유저 변경</button>
		</div>
	</div>

	<form action="/holdem/admin/aiSetting.do" name="listForm" id="listForm">
		<input type="hidden" name="pageIndex" value="1" />
	</form>
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
	function ChangeSet(kind) {
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
			alert("변경할 유저를 선택해주세요.");
			return false;
		}
		var param = {"arrIdx" : idx , "kind" : kind};
		if (confirm("선택한 유저를 변경하시겠습니까?")) {
			jQuery.ajax({
				type : 'post',
				data : param,
				url : "/holdem/admin/changeUserSet.do",
				success : function(data) {
					if (data.result == 'success') {
						alert("변경되었습니다.");
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
	}	
	function ChangeSetOne(kind , idx){
		var param = {"arrIdx" : idx , "kind" : kind};
		if (confirm("유저를 변경하시겠습니까?")) {
			jQuery.ajax({
				type : 'post',
				data : param,
				url : "/holdem/admin/changeUserSet.do",
				success : function(data) {
					if (data.result == 'success') {
						alert("변경되었습니다.");
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
	}
	</script>
</body>
</html>