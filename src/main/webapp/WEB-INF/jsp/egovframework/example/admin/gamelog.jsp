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
			document.listForm.action = "<c:url value='/admin/gamelogp.do'/>";
			document.listForm.submit();
		}
	</script>

	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">Game Log</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<form action="/holdem/admin/gamelogp.do" name="listForm" id="listForm">
								<input type="hidden" name="pageIndex" value="1" />
								<div class="row">
									<div class="col-lg-1">
										<div class="form-group">
											<label>게임한 유저, AI</label>
											<select class="form-control" name="uKind">
												<option value="all"<c:if test="${uKind == 'all'}">selected="selected"</c:if>>전체</option>
												<option value="user"<c:if test="${uKind == 'user'}">selected="selected"</c:if>>유저만</option>
											</select>
										</div>
									</div>
									<div class="col-lg-1">
										<div class="form-group">
											<label>게임 인원 검색</label>
											<input placeholder="숫자만 입력" onkeyup="SetNum(this)" class="form-control" name="num" id="num" value="${num}">
										</div>
									</div>
									<div class="col-lg-2">
										<div class="form-group">
											<label>특정 유저 검색(닉네임)</label>
											<input placeholder="닉네임" class="form-control" name="nick" id="nick" value="${nick}">
										</div>
									</div>
									<div class="col-lg-2">
										<div class="form-group">
											<label>삥머니</label>
											<input placeholder="숫자만 입력" onkeyup="SetNum(this)" class="form-control" name="pping" id="pping" value="${pping}">
										</div>
									</div>
									<div class="col-lg-5">
										<div class="form-group">
											<label>기간검색</label>
											<div style="display:flex">
												<input type="datetime-local" class="form-control" name="startD" value="${startD}" id="startD"/>
												&nbsp;~&nbsp;
												<input type="datetime-local" class="form-control" name="endD" value="${endD}" id="endD"/>
												&nbsp;&nbsp;&nbsp;
												<button type="button" onclick="search()" class="btn btn-outline btn-default">검색</button>
											</div> 
										</div>
									</div>
								</div>
							</form>
							<div class="table-responsive">
								<table class="table">
									<thead>
										<tr>
											<th>고유번호</th>											
											<th>삥머니</th>											
											<th>참여자 닉네임</th>											
											<th>참여자 수</th>											
											<th>보기</th>
											<th>날짜</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="result" items="${resultList }">
											<tr>
												<td>${result.gameIdentifier}</td>
												<td>${result.gvalue4}</td>
												<td>${result.gvalue3}</td>
												<td>${result.gvalue2}</td>
												<td>
													<button type="button"
														onClick="location.href='/holdem/admin/gameDetailLogp.do?gameIdentifier=${result.gameIdentifier}'"
														class="btn btn-primary btn-sm">보기</button>
												</td>
												<td>${result.gdate}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-12" style="text-align: center;">
								<ul class="pagination">
									<ui:pagination paginationInfo="${paginationInfo }" type="admin" jsFunction="fn_egov_link_page" />
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
	function SetNum(obj){
		val=obj.value;
		re=/[^0-9]/gi;
		obj.value=val.replace(re,"");
	}		
	function search(){
		var startD = $("#startD").val();
		var endD = $("#endD").val();
		if(startD != '' && endD == '' ){
			alert("조회종료기간을 선택해주세요");
			return;
		}
		if(endD != '' && startD == '' ){
			alert("조회시작기간을 선택해주세요");
			return;
		}
		if(endD < startD){
			alert("조회종료기간이 종료시작기간보다 작을 수 없습니다.");
			return;
		}
		$("#listForm").submit();
	}
	</script>
</body>
</html>