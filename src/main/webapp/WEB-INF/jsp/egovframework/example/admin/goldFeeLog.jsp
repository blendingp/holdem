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
			document.listForm.action = "<c:url value='/admin/goldFeeLog.do'/>";
			document.listForm.submit();
		}
	</script>

	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">골드수수료 일별 내역</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div>
								<form action="/holdem/admin/goldFeeLog.do" name="listForm" id="listForm">
									<input type="hidden" name="pageIndex" value="1" />
									<div class="row">
										<div class="col-lg-2">
											<div class="form-group">
												<label>승리 시 발생 수수료</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${sumFee}" pattern="#,###"/></pre>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>승리 시 발생 마일리지</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${sumFee-sumGold}" pattern="#,###"/></pre>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>AI가 유저에게 딴 돈</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${AiGetMoeny}" pattern="#,###"/></pre>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>AI가 획득한 적립금(마일리지)</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${aiFeeMile.aiMile}" pattern="#,###"/></pre>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>AI가 승리 시 낸 수수료</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${aiFeeMile.aiFee}" pattern="#,###"/></pre>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>유저가 AI한테 딴 돈</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${-UserGetMoeny}" pattern="#,###"/></pre>
											</div>
										</div>
										<%-- <div class="col-lg-2">
											<div class="form-group">
												<label>일별 총 수수료(적립금제외)</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${sumFee}" pattern="#,###"/>(<fmt:formatNumber value="${sumFee-sumGold}" pattern="#,###"/>)</pre>
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group">
												<label>전체 총 수수료</label>
												<pre style="padding:7.5px"><fmt:formatNumber value="${sumFeeAll}" pattern="#,###"/></pre>
											</div>
										</div> --%>										
									</div>
									<div class="row">
										<div class="col-lg-8">
											<div class="form-group">
												<label>순수익 계산식</label>
												<pre>승리 시 발생 수수료 - 승리 시 발생 마일리지 + AI가 유저에게 딴 돈 + AI가 획득한 적립금(마일리지) - AI가 승리 시 낸 수수료 - 유저가 AI한테 딴 돈</pre> 
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group input-group">
												<label>순수익금</label>
												<pre><fmt:formatNumber value="${sumFee - (sumFee-sumGold) + AiGetMoney + aiFeeMile.aiMile - aiFeeMile.aiFee -(-UserGetMoney)}" pattern="#,###"/></pre>
											</div>
										</div>
									</div>
									<div class="row">
										<div class="col-lg-4">
											<div class="form-group">
												<label>기간검색</label>
												<div style="display:flex">
													<input type="date" class="form-control" name="startD" value="${startD}" id="startD"/>
													&nbsp;~&nbsp;
													<input type="date" class="form-control" name="endD" value="${endD}" id="endD"/>
												</div> 
											</div>
										</div>
										<div class="col-lg-2">
											<div class="form-group input-group">
												<label>검색어 입력</label>
												<div style="display:flex">
													<input placeholder="아이디 혹은 닉네임" style="display:block;" class="form-control idinput" name="search" id="search" value="${search}">
													<span class="input-group-btn">
														<button type="button" onclick="btnSearch()" class="btn btn-default">검색</button>
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
												<td>${item.nickname}</td>
												<td>
													${item.gameid} &nbsp;&nbsp;
													<button type="button"
														onClick="location.href='/holdem/admin/gameDetailLogp.do?gameIdentifier=${item.gameid}'"
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
<script>
	function btnSearch(){
		var startD = $("#startD").val();
		var endD = $("#endD").val();
		if(endD < startD)
		{
			alert("조회종료기간이 조회시작기간보다 작을 수 없습니다.");
			return;
		}
		document.listForm.submit();
	}
</script>
</html>