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
			document.listForm.action = "<c:url value='/admin/userDWlog.do'/>";
			document.listForm.submit();
		}
	</script>

	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">유저 입출금 내역</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-heading">관리자가 유저에게 골드/칩을 입금/출금한 내역입니다</div>
						<div class="panel-body">
							<div>
								<form action="/holdem/admin/userDWlog.do" name="listForm" id="listForm">
									<input type="hidden" name="pageIndex" value="1" />
									<div class="row">
										<div class="col-lg-2">
											<div class="form-group">
												<label>골드/칩</label>
												<select class="form-control" name="mKind" onchange="fn_egov_link_page(1)">
													<option value="all"<c:if test="${mKind == 'all'}">selected="selected"</c:if>>전체</option>
													<option value="balance"<c:if test="${mKind == 'balance'}">selected="selected"</c:if>>골드</option>
													<option value="point"<c:if test="${mKind == 'point'}">selected="selected"</c:if>>칩</option>
												</select>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>입금/출금</label>
												<select class="form-control" name="dwKind" onchange="fn_egov_link_page(1)">
													<option value="all"<c:if test="${dwKind == 'all'}">selected="selected"</c:if>>전체</option>
													<option value="deposit"<c:if test="${dwKind == 'deposit'}">selected="selected"</c:if>>입금</option>
													<option value="withdrawal"<c:if test="${dwKind == 'withdrawal'}">selected="selected"</c:if>>출금</option>
												</select>
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
											<th>골드/칩</th>
											<th>입금/출금</th>
											<th>입출금액</th>
											<th>날짜</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${list}">
											<tr>
												<td>
													<c:if test="${empty item.userId}">${item.socail }</c:if>
													<c:if test="${!empty item.userId}">${item.userId }</c:if>
												</td>
												<td>${item.userNick}</td>
												<td>
													<c:if test="${item.product.split('_')[1] == 'balance'}">
														골드		
													</c:if>
													<c:if test="${item.product.split('_')[1] == 'point'}">
														칩
													</c:if>
												</td>
												<td>
													<c:if test="${item.product.split('_')[2] == 'deposit'}">
														입금
													</c:if>
													<c:if test="${item.product.split('_')[2] == 'withdrawal'}">
														출금
													</c:if>
												</td>
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