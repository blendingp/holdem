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
					<h1 class="page-header">AI 유저 생성</h1>
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
											<label>초기문자와 개수를 입력해주세요. - 개수 최대 100개</label><br/>
											<label>※ 생성 시 유저 아이디 및 닉네임은 초기문자1부터 시작합니다.</label><br/>
											<label>※ AI 생성 시 기존 아이디가 존재하는 경우 숫자가 증가되어 생성됩니다.</label>
										</div>
										<div class="form-group">
											<label>초기문자</label> 
											<input id="firstNm" name="firstNm" class="form-control" >
										</div>
										<div class="form-group">
											<label>생성 개수</label> 
											<input id="num" name="num" class="form-control"  placeholder="숫자만 입력" onkeyup="SetNum(this);">
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
					<button type="button" onclick="aiCreate()" class="btn btn-outline btn-default">등록</button>
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
		function aiCreate(){
			var firstNm = $("#firstNm").val();
			var num = $("#num").val();
			var balance = $("#balance").val();
			var point = $("#point").val();
			if(firstNm == ""){
				alert("초기문자를 입력해주세요.");
				return;
			}
			if(num == "" || num <= 0 ){
				alert("생성 개수를 입력해주세요.");
				return;
			}
			if(num > 1000){
				alert("AI 생성 최대 개수는 1000개입니다.");
				return;
			}
			if(balance == "" || balance < 0 ){
				alert("골드를 입력해주세요.");
				return;
			}
			if(point == "" || point < 0 ){
				alert("칩을 입력해주세요.");
				return;
			}
			var param = {"firstNm" : firstNm , "num" : num , "balance" : balance , "point" : point};
			$.ajax({
				type:'post',
				data:param,
				url:'/holdem/admin/aiCreateProcess.do',
				success:function(data){
					if(data.result == 'success')
					{
						alert("생성 완료하였습니다.");
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