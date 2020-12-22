<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
					<h1 class="page-header">유저 정보</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="row">
								<div class="col-lg-12">
									<form role="form" id="inquiryForm">
										<input type="hidden" name="idx" id="idx" value="${info.midx}"/>
										<div class="row">
											<div class="col-lg-6">
												<div class="form-group">
													<label>아이디</label> 
													<pre>${info.muserid}&nbsp;</pre>
												</div>
											</div>
											<div class="col-lg-6">
												<div class="form-group">
													<label>닉네임</label> 
													<pre>${info.nickname}&nbsp;</pre>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-lg-6">
												<div class="form-group">
													<label>소셜가입</label> 
													<pre>${info.socail}&nbsp;</pre>
												</div>
											</div>
											<div class="col-lg-6">
												<div class="form-group">
													<label>AI구분</label> 
													<c:if test="${item.ai == 1}">
														<pre>AI 유저</pre>		
													</c:if>
													<c:if test="${item.ai != 1}">
														<pre>일반 유저</pre>
													</c:if>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-lg-6">
												<div class="form-group">
													<label>골드</label> 
													<pre><fmt:formatNumber value="${info.balance}" pattern="#,###"/></pre>
												</div>
											</div>
											<div class="col-lg-6">
												<label>골드 입금 / 출금</label> 
												<div class="form-group input-group">
													<span class="input-group-btn">
														<button type="button" onclick="javascript:updateMoney('balance','deposit')" class="btn btn-primary">입금</button>
													</span>
													<input name="balance" class="form-control" placeholder="숫자만 입력" id="balance" onkeyup="SetNum(this);"/>
													<span class="input-group-btn">
														<button type="button" onclick="javascript:updateMoney('balance','withdrawal')" class="btn btn-danger">출금</button>
													</span>
												</div>											
											</div>
										</div>
										<div class="row">
											<div class="col-lg-6">
												<div class="form-group">
													<label>칩</label> 
													<pre><fmt:formatNumber value="${info.point}" pattern="#,###"/></pre>
												</div>
											</div>
											<div class="col-lg-6">
												<label>칩 입금 / 출금</label> 
												<div class="form-group input-group">
													<span class="input-group-btn">
														<button type="button" onclick="javascript:updateMoney('point','deposit')" class="btn btn-primary">입금</button>
													</span>
													<input name="point" class="form-control" placeholder="숫자만 입력" id="point" onkeyup="SetNum(this);"/>
													<span class="input-group-btn">
														<button type="button" onclick="javascript:updateMoney('point','withdrawal')" class="btn btn-danger">출금</button>
													</span>
												</div>											
											</div>
										</div>
										<div class="row">
											<div class="col-lg-6">
												<div class="form-group">
													<label>보석</label> 
													<pre><fmt:formatNumber value="${info.cash}" pattern="#,###"/></pre>
												</div>
											</div>
											<div class="col-lg-6">
												<label>보석 추가 / 삭제</label> 
												<div class="form-group input-group">
													<span class="input-group-btn">
														<button type="button" onclick="javascript:updateMoney('cash','deposit')" class="btn btn-primary">추가</button>
													</span>
													<input name="cash" class="form-control" placeholder="숫자만 입력" id="cash" onkeyup="SetNum(this);"/>
													<span class="input-group-btn">
														<button type="button" onclick="javascript:updateMoney('cash','withdrawal')" class="btn btn-danger">삭제</button>
													</span>
												</div>											
											</div>
										</div>
									</form>
								</div>
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
		function updateMoney(type , kind){
			var idx = $("#idx").val();
			var money = $("#"+type).val();
			if(money == '')
			{
				alert("숫자를 입력해주세요.")
				return;
			}
			var param = {"idx" : idx , "type" : type , "kind" : kind , "money" : money};
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
					else if (data.result == 'noMoney')
					{
						if(kind == 'balance' || kind == 'point')
						{
							alert("금액이 부족합니다.");
						}
						else
						{
							alert("개수가 부족합니다.");
						}
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