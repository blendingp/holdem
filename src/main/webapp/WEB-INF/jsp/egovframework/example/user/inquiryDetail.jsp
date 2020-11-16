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
<body>
	<div class="frame">
		<div class="bg form-block w-form">
			<form id="email-form-2" name="email-form-2" data-name="Email Form 2">
				<jsp:include page="../frame/userMenu.jsp"></jsp:include>
				<div class="body">
					<div class="notititlebox">
						<img src="/holdem/webflow/images/WINGAME.png" loading="lazy" alt=""
							class="notilogo">
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
					<div class="quedetail">
						<div class="table-19">
							<div class="ttop-9">
								<div class="inquirytitle">${inquiryDetail.title}</div>
								<div class="twrap">
									<div class="tdate-7-copy">
										등록일<span class="text-span-11"><fmt:formatDate value="${inquiryDetail.regDate}" pattern="yyyy.MM.dd"/></span>
									</div>
									<div class="div-block-11">
										<c:if test="${inquiryDetail.answerYn == 'Y' }">
											<div class="wstatext-3 done">답변완료</div>
										</c:if>
										<c:if test="${inquiryDetail.answerYn != 'Y' }">
											<div class="wstatext-3">답변대기</div>
										</c:if>
									</div>
								</div>
							</div>
							<a href="#" class="rowbtn w-inline-block"></a>
							<div class="notidetailbox">
								<div>${text}</div>
							</div>
						</div>
						<div class="pbox-7">
							<!-- <a href="#" class="pbtn-4 w-inline-block"></a> -->
							<div class="listbtnwrap-copy-2" style="width:auto;">
								<c:if test="${inquiryDetail.answerYn != 'Y' }">
									<a href="/holdem/user/inquiryEdit.do?idx=${inquiryDetail.idx}" class="registerbtn w-inline-block">
										<div class="deletebtntext">수정</div>
									</a> 
									<a href="javascript:inquiryDel()" class="listbtn-2 w-inline-block">
										<div class="deletebtntext">삭제</div>
									</a> 
									<a href="/holdem/user/inquiry.do" class="listbtn-2 w-inline-block">
										<div class="deletebtntext">목록으로</div>
									</a>
								</c:if>
							</div>
						</div>
					</div>
					<c:if test="${inquiryDetail.answerYn == 'Y' }">
						<div class="quedetail">
							<div class="table-19">
								<div class="ttop-9">
									<div class="inquirytitle">[답변]</div>
									<div class="twrap">
										<div class="tdate-7-copy">
											답변일<span class="text-span-11"><fmt:formatDate value="${inquiryDetail.answerDate}" pattern="yyyy.MM.dd"/></span>
										</div>
										<div class="div-block-11">
											<div class="wstatext-3 done">답변완료</div>
										</div>
									</div>
								</div>
								<a href="#" class="rowbtn w-inline-block"></a>
								<div class="notidetailbox">
									<div>${answer}</div>
								</div>
							</div>
							<div class="pbox-7">
								<div class="listbtnwrap-copy-2" style="width:auto;">
									<c:if test="${inquiryDetail.answerYn == 'Y' }">
										<div>※ 관리자가 답변을 한 후에는 수정, 삭제가 불가능 합니다.</div> 
									</c:if>
									<a href="/holdem/user/inquiry.do" class="listbtn-2 w-inline-block">
										<div class="deletebtntext">목록으로</div>
									</a>
								</div>
							</div>
						</div>	
					</c:if>				
				</div>
			</form>
		</div>
	</div>
	<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	<script>
		function inquiryDel(){
			if(confirm("글을 삭제하시겠습니까?")){
				jQuery.ajax({
					type:'post',
					url:'/holdem/user/inquiryDel.do?idx='+${inquiryDetail.idx},
					success:function(data){
						if(data.result=='success'){
							alert("삭제되었습니다. ");
							location.href="/holdem/user/inquiry.do";
						}else{
							alert("에러가 발생했습니다. 다시 시도해주세요.");
						}
					},
					error:function(e){
						console.log('error'+e);
					}
				})	
			}
		}	
	</script>
</body>
</html>