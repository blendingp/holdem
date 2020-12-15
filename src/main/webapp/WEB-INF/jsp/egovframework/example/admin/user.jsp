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
			document.listForm.action = "<c:url value='/admin/user.do'/>";
			document.listForm.submit();
		}
	</script>

	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">유저 목록</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div>
								<form action="/holdem/admin/user.do" name="listForm" id="listForm">
									<input type="hidden" name="pageIndex" value="1" />
									<div class="row">
										<div class="col-lg-2">
											<div class="form-group">
												<select class="form-control" name="uKind" onchange="fn_egov_link_page(1)">
													<option value="all"<c:if test="${uKind == 'all' }">selected="selected"</c:if>>전체</option>
													<option value="normal"<c:if test="${uKind == 'normal' }">selected="selected"</c:if>>일반유저</option>
													<option value="ai"<c:if test="${uKind == 'ai' }">selected="selected"</c:if>>AI유저</option>
												</select>
											</div>
										</div>
									</div>
								</form>
								<table class="table table-hover">
									<thead>
										<tr>
											<th>아이디</th>
											<th>닉네임</th>
											<th>소셜가입</th>
											<th>AI구분</th>
											<th>골드</th>
											<th>칩</th>
											<th>보석</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${list}">
											<tr style="cursor:pointer" onclick="location.href='/holdem/admin/userInfo.do?idx=${item.midx}'">
												<td>${item.muserid}</td>
												<td>${item.nickname}</td>
												<td>${item.socail}</td>
												<td>
													<c:if test="${item.ai == 1}">
														AI 유저		
													</c:if>
													<c:if test="${item.ai != 1}">
														일반 유저 
													</c:if>
												</td>
												<td><fmt:formatNumber value="${item.balance}" pattern="#,###"/></td>
												<td><fmt:formatNumber value="${item.point}" pattern="#,###"/></td>
												<td><fmt:formatNumber value="${item.cash}" pattern="#,###"/></td>
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
		</div>
	</div>

	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
</body>
</html>