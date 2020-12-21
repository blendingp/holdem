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
			document.listForm.action = "<c:url value='/admin/purchaseLog.do'/>";
			document.listForm.submit();
		}
	</script>

	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">아이템 일별 구매내역</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div>
								<form action="/holdem/admin/purchaseLog.do" name="listForm" id="listForm">
									<input type="hidden" name="pageIndex" value="1" />
									<div class="row">
										<div class="col-lg-2">
											<div class="form-group">
												<label>정렬</label>
												<select class="form-control" name="orderKind" onchange="fn_egov_link_page(1)">
													<option value="recent"<c:if test="${orderKind == 'recent'}">selected</c:if>>최신순</option>
													<option value="high"<c:if test="${orderKind == 'high'}">selected</c:if>>가격높은순</option>
													<option value="low"<c:if test="${orderKind == 'low'}">selected</c:if>>가격낮은순</option>
												</select>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>총 구매가격</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${sumCharge}" pattern="#,###"/></pre>
											</div>
										</div>										
										<div class="col-lg-2">
											<div class="form-group input-group">
												<label>검색어 입력</label>
												<div style="display:flex">
													<input placeholder="아이디 혹은 닉네임" style="display:block;" class="form-control idinput" name="search" id="search" value="${search}">
													<span class="input-group-btn">
														<button type="submit" class="btn btn-default">검색</button>
													</span>
												</div>
											</div>
										</div>
									</div>
								</form>
								<table class="table table-hover">
									<thead>
										<tr>
											<th>아이디</th>
											<th>닉네임</th>
											<th>아이템</th>
											<th>가격</th>
											<th>구매날짜</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${list}">
											<tr>
												<td>
													<c:if test="${empty item.muserid}">${item.socail }</c:if>
													<c:if test="${!empty item.muserid}">${item.muserid }</c:if>
												</td>
												<td>${item.nickname}</td>
												<td>${item.productNm}</td>
												<td><fmt:formatNumber value="${item.price}" pattern="#,###"/></td>
												<td><fmt:formatDate value="${item.date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
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