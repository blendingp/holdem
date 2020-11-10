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
					<h1 class="page-header">메인 이미지 추가</h1>
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
											<label>이미지는 총 7장까지 등록가능합니다.</label><br/>
											<label>※ 7장 초과 등록 경우 7장까지만 등록됩니다.</label><br/>
											<label>※ 기존 등록된 사진과 추가된 사진이 7장이 넘을 경우 등록일 기준 오래된 순으로 삭제됩니다.</label>
										</div>
										<div class="form-group">
											<label>사진등록</label> 
											<input type="file" id="file" name="file" multiple accept="image/*" maxlength="7">
										</div>
									</form>
								</div>
							</div>
						</div>
					</div>
					<button type="button" onclick="imageInsert()" class="btn btn-outline btn-default">등록</button>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	<script>
		function imageInsert(){
			var form = $("#imageForm")[0];
			var data = new FormData(form);
			var myFile = $("#file").prop('files');
			if(myFile.length < 1)
			{
				alert("적어도 하나 이상의 사진을 올려야합니다.");
				return;
			}
			console.log('ajax start');
			console.log(data);
			$.ajax({
				type:'post',
				url : '/holdem/admin/imageInsert.do',
				processData:false,
				contentType:false,
				data: data,
				success:function(data){
					if(data.success == 'success')
					{
						alert("이미지가 등록되었습니다. ");
						location.href="/holdem/admin/image.do";
					}
					else
					{
						alert("등록 중 오류가 발생했습니다 \n다시 시도해주세요.");
					}
				},
				error:function(e){
					console.log('ajaxError' + e);
				}
			});
		}
	</script>
</body>
</html>