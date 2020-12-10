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
			document.listForm.action = "<c:url value='/admin/goldFeeMonthLog.do'/>";
			document.listForm.submit();
		}
	</script>

	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">골드수수료 월별 내역</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div>
								<form action="/holdem/admin/goldFeeMonthLog.do" name="listForm" id="listForm">
									<input type="hidden" name="pageIndex" value="1" />
									<div class="row">
										<div class="col-lg-2">
											<div class="form-group">
												<label>년도 선택</label>
												<select class="form-control" name="year" onchange="fn_egov_link_page(1)">
													<jsp:useBean id="now" class="java.util.Date" />
													<fmt:formatDate value="${now}" pattern="yyyy" var="yearStart"/>
								                    <c:forEach begin="0" end="${yearStart - 2019}" var="result" step="1">
								                        <option value="${yearStart-result}" <c:if test="${yearStart-result == year}">selected</c:if> >${yearStart-result}년</option>
								                    </c:forEach>
												</select>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>월 선택</label>
												<select class="form-control" name="month" onchange="fn_egov_link_page(1)">
													<c:forEach var="monthStart" begin="1" end="12">
														<option value="${monthStart}"<c:if test="${monthStart == month}">selected</c:if>>${monthStart}월</option>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>${year}년 ${month}월 총 수수료</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${sumFee}" pattern="#,###"/></pre>
											</div>
										</div>
									</div>
								</form>
								<table class="table table-hover">
									<thead>
										<tr>
											<th>아이디</th>
											<th>게임회차</th>
											<th>상금</th>
											<th>수수료</th>
											<th>날짜</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${list}">
											<tr>
												<td>
													<c:if test="${empty item.muserid}">${item.socail }</c:if>
													<c:if test="${!empty item.muserid}">${item.muserid }</c:if>
												</td>
												<td>
													${item.gameid} &nbsp;&nbsp;
													<button type="button"
														onClick="location.href='/holdem/admin/gameDetailLogp.do?gameid=${item.gameid}'"
														class="btn btn-primary btn-sm">보기</button>
												</td>
												<td><fmt:formatNumber value="${item.winmoney}" pattern="#,###"/></td>
												<td><fmt:formatNumber value="${item.fee}" pattern="#,###"/></td>
												<td><fmt:formatDate value="${item.gdate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
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