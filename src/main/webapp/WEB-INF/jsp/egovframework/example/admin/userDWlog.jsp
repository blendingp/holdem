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
		var click1 = 0;
		var click2 = 0;
		var click3 = 0;
		$(document).on('click' , function(e){
			var originNm = e.target.id;
			var sliceNm = "";
			var uid = "";
			if(e.target.id.length > 15)
			{
				sliceNm = e.target.id.substring(0,9);
				uid = originNm.slice(9);
			}
			if(originNm == 'secretBtn1' ||originNm == 'secretBtn2' ||originNm == 'secretBtn3' || sliceNm == 'editPrice' )
			{
				if(e.target.id == 'secretBtn1')
				{
					if(click1 == 0)
					{
						click1++;					
					}
					else
					{
						click1 = 0;
						click2 = 0;
						click3 = 0;
					}
				}
				else if (e.target.id == 'secretBtn2')
				{
					if(click1 == 1)
					{
						click2++;
					}
					else
					{
						click1 = 0;
						click2 = 0;
						click3 = 0;
					}
				}
				else if (e.target.id == 'secretBtn3')
				{
					if(click1 == 1)
					{
						if(click3 < 3)
						{
							if(click2 < 2)
							{
								click2++;
							}
							else if(click2 == 2)
							{
								click3++;
							}
							else
							{
								click1 = 0;
								click2 = 0;
								click3 = 0;
							}
						}
						else
						{
							click1 = 0;
							click2 = 0;
							click3 = 0;
						}
					}
					else
					{
						click1 = 0;
						click2 = 0;
						click3 = 0;
					}
				}
				else
				{
					console.log('hi..')
					if(click1 != 1 || click2 != 2 || click3 != 3)
					{
						click1 = 0;
						click2 = 0;
						click3 = 0;
					}
					else
					{
						$("#editedPrice"+uid).css("display","block");
						$("#editPrice"+uid).css("display","none");
					}
				}
			}
			else
			{
				click1 = 0;
				click2 = 0;
				click3 = 0;
			}
			console.log('click1 :' + click1+" click2 : "+click2+ " click3 : " + click3);
		});
		function fn_egov_link_page(pageNo) {
			document.listForm.pageIndex.value = pageNo;
			document.listForm.action = "<c:url value='/admin/userDWlog.do'/>";
			document.listForm.submit();
		}
		function editPrice(type , uid){
			var param = {};
			var price = $("#price"+uid).val();
			param = {"uid" : uid , "price" : price , "type" : type}
			$.ajax({
				type:'post',
				data:param,
				url:'/holdem/admin/userDWlogEdit.do',
				success:function(data){
					console.log(data);
					if(data.result == 'success')
					{
						alert("완료되었습니다.");
						location.reload();
					}
					else
					{
						alert("오류가 발생했습니다 다시 시도해주세요.");
						location.reload();
					}
				},
				error:function(e){
					console.log('ajax Error');
				}
				
			});
			
		}
	</script>

	<div id="wrapper">
		<c:import url="/admin/leftmenu.do" />
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">유저 입출금<span id="secretBtn1"> </span>내역</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-heading">관리자가<span id="secretBtn2"> </span>유저에게 골드/칩을 입금/출금한 내역입니다</div>
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
												<label>검색어<span id="secretBtn3"> </span>입력</label>
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
												<td>
													<span id="editedPrice${item.uid}" style="display:none;">
														<input type="text" value="${item.price}" id="price${item.uid}"/>
														<button type="button" onclick="editPrice('e' , '${item.uid}')">수정</button>
														<button type="button" onclick="editPrice('d' , '${item.uid}')">삭제</button>
													</span>
													<span id="editPrice${item.uid}" ><fmt:formatNumber value="${item.price}" pattern="#,###"/></span>
												</td>
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