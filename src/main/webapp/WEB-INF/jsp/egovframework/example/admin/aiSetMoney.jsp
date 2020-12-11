<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
					<h1 class="page-header">AI 골드/칩 설정</h1>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="row">
								<div class="col-lg-6">
									<form role="form" id="imageForm">
										<div class="form-group">
											<label>각 골드와 칩을 입력해주세요.</label><br/>
											<label>※ 등록 시 모든 ai의 골드/칩이 변경됩니다.</label><br/>
										</div>
										<div class="form-group">
											<label>골드</label> 
											<input id="balance" name="balance" class="form-control"  placeholder="숫자만 입력" onkeyup="SetNum(this);">
										</div>
										<div class="form-group">
											<label>칩</label> 
											<input id="point" name="point" class="form-control"  placeholder="숫자만 입력" onkeyup="SetNum(this);">
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
					<button type="button" onclick="aiSetMoney()" class="btn btn-outline btn-default">등록</button>
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
		function aiSetMoney(){
			var balance = $("#balance").val();
			var point = $("#point").val();
			if(balance == "" || balance < 0 ){
				alert("골드를 입력해주세요.");
				return;
			}
			if(point == "" || point < 0 ){
				alert("칩을 입력해주세요.");
				return;
			}
			var param = {"balance" : balance , "point" : point};
			$.ajax({
				type:'post',
				data:param,
				url:'/holdem/admin/aiSetMoneyProcess.do',
				success:function(data){
					if(data.result == 'success')
					{
						alert("변경 완료하였습니다.");
						location.reload();
					}
					else
					{
						alert("오류가 발생하였습니다 다시 시도해주세요.");
						location.reload();
					}
				},
				error:function(e){
					console.log('ajax Error');
				}
			})
		}
	</script>
</body>
</html>