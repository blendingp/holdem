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
					<h1 class="page-header">특정 AI 골드 설정</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div>
								<table class="table table-hover">
									<thead>
										<tr>
											<th>아이디</th>
											<th>닉네임</th>
											<th>골드</th>
											<th>골드백만추가</th>
											<th>칩</th>
											<th>보석</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="item" items="${list}">
											<tr>
												<td>${item.muserid}</td>
												<td>${item.nickname}</td>
												<td><fmt:formatNumber value="${item.balance}" pattern="#,###"/></td>
												<td>
													<button type="button"
														onClick="plusGold(${item.midx});"
														class="btn btn-info">골드 백만 추가</button>
												</td>
												<td><fmt:formatNumber value="${item.point}" pattern="#,###"/></td>
												<td><fmt:formatNumber value="${item.cash}" pattern="#,###"/></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
	function plusGold(idx){
		var param = {"idx" : idx , "type" : "balance" , "kind" : "deposit" , "money" : "1000000"};
		$.ajax({
			type:'post',
			url:'/holdem/admin/userMoneyUpdate.do' ,
			data:param,
			success:function(data){
				console.log(data);
				if(data.result == 'success')
				{
					alert("완료되었습니다. ");
					location.reload();
				}
				else
				{
					alert("오류가 발생했습니다. 다시 시도해주세요.");
					location.reload();
				}
			},
			error:function(e){
				console.log('ajaxError');
			}
		});
	}
	
	</script>
</body>
</html>