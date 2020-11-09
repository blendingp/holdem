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
<style>
#chatbody {
	display: none;
}
</style>
<body>
	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">채팅관리</h1>
				</div>
			</div>
			<div class="panel-body">
				<div class="table-responsive">
					<button type="button" id="toggleButton" class="btn btn-primary btn-sm">상용문구 등록</button>
					<form id="frm" action="/holdem/admin/chatinsert.do">
						<div id="chatbody">
							<input type="text" name="mention" />
							<button type="button" onclick="document.getElementById('frm').submit();" class="btn btn-primary btn-sm">등록하기</button>
						</div>
					</form>
					<table class="table">
						<thead>
							<tr>
								<th>#</th>
								<th>상용문구</th>
								<th>수정</th>
								<th>삭제</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="result" items="${resultList}">
								<tr>
									<td>${result.cidx}</td>
									<td>${result.cmention}</td>
									<td>
										<button type="button"
											onClick="location.href='/holdem/admin/chatupdate.do?cidx=${result.cidx}'"
											class="btn btn-primary btn-sm">수정</button>
									</td>
									<td>
										<button type="button"
											onClick="location.href='/holdem/admin/chatdelete.do?cidx=${result.cidx}'"
											class="btn btn-primary btn-sm">삭제</button>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div style="display: flex; justify-content: center">
						<ui:pagination paginationInfo="${paginationInfo }" type="image" jsFunction="fn_egov_link_page" />
					</div>
				</div>
			</div>
		</div>
	</div>

	<form action="/holdem/admin/chat.do" name="listForm" id="listForm">
		<input type="hidden" name="pageIndex" value="1" />
	</form>

	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
		function fn_egov_link_page(pageNo) {
			document.listForm.pageIndex.value = pageNo;
			document.listForm.action = "<c:url value='/admin/chat.do'/>";
			document.listForm.submit();
		}
		$(document).ready(function() {
			$('#toggleButton').click(function() {
				var state = $('#chatbody').css('display');
				if (state == 'none') 
				{
					$('#chatbody').show();
				} 
				else 
				{
					$('#chatbody').hide();
				}

			})
		})
	</script>
</body>
</html>