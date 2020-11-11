<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html  data-wf-page="5fab6843c0cca0c084955994" data-wf-site="5faa9fb759c008dd7db7c885">
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include></head>
<script type="text/javascript" src="${pageContext.request.contextPath}/se2/js/HuskyEZCreator.js" charset="utf-8"></script>
<body>
	<div class="frame">
		<div class="form-block w-form">
			<form id="inquiryForm">
				<jsp:include page="../frame/userMenu.jsp"></jsp:include>
				<div class="body">
					<div class="notititlebox">
						<img src="/holdem/webflow/images/WINGAME.png" loading="lazy" alt="" class="notilogo">
						<div class="notititle">
							윈게임<span class="text-span-10">1:1 문의</span>센터입니다.
						</div>
						<div class="notibottitle">
							윈게임을 이용해 주셔서 감사합니다.<br>궁금하신 사항이 있으시다면 언제든지 문의해 주시기 바랍니다.
						</div>
					</div>
					<div class="togbox">
						<div class="listclick">
							<a href="/holdem/user/main.do" class="listhomelink w-inline-block">
								<div class="listlink">HOME</div>
							</a>
							<div class="listhome">&gt;</div>
							<a href="/holdem/user/inquiry.do" class="listhomelink w-inline-block">
								<div class="listlink click">1:1 문의</div>
							</a>
						</div>
					</div>
					<div class="write">
						<div class="inquiry-write">
							<div class="table-20">
								<div class="trow-2">
									<div class="twrap-2">
										<div class="ttxt-3">제목</div>
									</div>
									<input type="text" class="tinput-2 w-input" maxlength="100" name="title" id="title">
								</div>
								<div class="trow2-2-copy">
									<div class="twrap-2">
										<div class="ttxt-3">문의내용</div>
									</div>
									<!-- <textarea name="text" class="textarea-2 w-input"></textarea> -->
									<textarea id="smartEditor" style="max-height:358px;" name="text"></textarea>
								</div>
								<div class="trow-2-copy">
									<div class="twrap-2">
										<div class="ttxt-3">비밀번호</div>
									</div>
									<input type="password" class="text-field w-input" maxlength="15" name="pw" id="pw">
								</div>
							</div>
							<div class="pbox-7">
								<a href="#" class="pbtn-4 w-inline-block"></a>
								<div class="listbtnwrap-copy-2">
									<a href="javascript:inquiryInsert()" class="registerbtn w-inline-block">
										<div class="deletebtntext">등록</div>
									</a> 
									<a href="/holdem/user/inquiry.do" class="listbtn-2 w-inline-block">
										<div class="deletebtntext">목록으로</div>
									</a>
								</div>
							</div>
						</div>
					</div>
				</div>
				<jsp:include page="../frame/userBottom.jsp"></jsp:include>
			</form>
		</div>
	</div>
	<script>
		function inquiryInsert(){
			oEditors.getById["smartEditor"].exec("UPDATE_CONTENTS_FIELD",[]);
			var title = $("#title").val();
			var text = $("#smartEditor").val();
			var pw = $("#pw").val();
			if(title == "")
			{
				alert("제목을 입력해주세요.");
				$("#title").focus();
				return;
			}
			if(text == "")
			{
				alert("내용을 입력해주세요.");
				$("#smartEditor").focus();
				return;
			}
			if(pw == "")
			{
				alert("비밀번호를 입력해주세요.");
				$("#pw").focus();
				return;
			}
			$.ajax({
				type : 'post',
				data : $("#inquiryForm").serialize(),
				url : '/holdem/user/inquiryInsert.do',
				success:function(data){
					if(data.result == 'success')
					{
						alert('등록이 완료되었습니다.');	
						location.href="/holdem/user/inquiry.do";
					}
					else
					{
						alert('등록에 실패하였습니다. \n다시 시도해주세요.');
					}
				},
				error:function(e){
					console.log('ajax error' + e);
				}
			})
		}
		var oEditors = []; 
		nhn.husky.EZCreator.createInIFrame({ 
			oAppRef : oEditors, 
			elPlaceHolder : "smartEditor",  
			sSkinURI : "${pageContext.request.contextPath}/se2/SmartEditor2Skin.html",  
			fCreator : "createSEditor2", 
			htParams : {  
				bUseToolbar : true,   // 툴사용여부 
				bUseVerticalResizer : false, // 입력창 크기 조절 바 
				//bSkipXssFilter : true, // xss 필터 
				bUseModeChanger : false  // 텍스트 모드 변경 
				},
			fOnAppLoad : function(){
			   oEditors.getById["smartEditor"].exec("PASTE_HTML", ['${text}']); // 미리 적용할 텍스트 내용이 있는경우
			   document.getElementsByTagName("iframe")[0].style.width = "89%";
			   document.getElementsByTagName("iframe")[0].style.height = "100%";
			   document.getElementsByTagName("iframe")[0].style.padding = "1%";

			  },		
		});
	</script>
</body>
</html>