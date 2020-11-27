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
		<div class="bg-copy form-block w-form">
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
					<div class="noticebox">
						<div class="table-19">
							<div class="ttop10">
								<div class="tnum-7">순번</div>
								<div class="ttitle-8">제목</div>
								<div class="tdate-7">등록일</div>
								<div class="tdate-copy-2">답변상태</div>
							</div>
							<c:forEach var="item" items="${inquiryList}">
								<a href="javascript:pwChk(${item.idx})" class="rowbtn w-inline-block">
									<div class="row-11">
										<div class="tnum2-4">${item.no }</div>
										<div class="ttitle2-2">${item.title}</div>
										<div class="tdate2-4"><fmt:formatDate value="${item.regDate}" pattern="yyyy-MM-dd HH:mm"/></div>
										<div class="wstate">
											<c:if test="${item.answerYn == 'Y' }">
												<div class="wstatext-3 done">답변완료</div>
											</c:if>
											<c:if test="${item.answerYn != 'Y' }">
												<div class="wstatext-3">답변대기</div>
											</c:if>
										</div>
									</div>
								</a> 
							</c:forEach>
						</div>
						<div class="pbox-7">
							<a href="#" class="pbtn-4 w-inline-block"></a>
							<div class="listbtnwrap-copy-2">
								<a href="/holdem/user/inquiryAdd.do" class="listbtn-2 w-inline-block">
									<div class="deletebtntext">문의하기</div>
								</a>
							</div>
						</div>
						<div class="bottombtnbox-2" style="width: auto;">
							<ui:pagination paginationInfo="${paginationInfo}" type="user" jsFunction="fn_egov_link_page" />
						</div>
					</div>
				</div>
				<div class="pframe" id="pwChk">
					<div class="pwpopup">
						<div class="pwtitle">비밀번호 입력</div>
						<input type="password" class="pwinput w-input" name="pw" id="pw" required>
						<input type="hidden" name="idx" id="idx" required>
						<div class="ntxt" style="display:none;"></div>
						<div style="width:100%; display:flex;">
							<a href="javascript:inquiryChk()" class="checkbtn w-button" style="width:48%;">확인</a>
							<a href="javascript:pwChkClose()" class="checkbtn w-button" style="width:48%; margin-left:4%;">취소</a> 
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
	<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	<script>
		function fn_egov_link_page(pageIndex){
			location.href="/holdem/user/inquiry.do?pageIndex="+pageIndex;
		}
		function pwChk(idx){
			$("#idx").val(idx);
			$("#pwChk").css('display' , 'flex');
		}
		function pwChkClose(){
			$("#pwChk").css('display' , 'none');
		}
		
		function inquiryChk(){
			var pw = document.getElementById("pw").value;
			var idx= document.getElementById("idx").value;
			console.log(pw == "");
			if(pw == "")
			{
				$(".ntxt").text("비밀번호를 입력해주세요.");
				$(".ntxt").css('display' ,'block');
				
			}
			$.ajax({
				type:'post',
				url:'/holdem/user/inquiryPwChk.do?idx='+idx,
				data: {"pw" :pw},
				success:function(data){
					if(data.result == 'success'){
						$("#pwChk").css('display' , 'none');
						location.href="/holdem/user/inquiryDetail.do?idx="+idx;
					}else{
						$(".ntxt").text("비밀번호 확인 후 다시 입력해주세요.");
						$(".ntxt").css('display' ,'block');
					}
				},
				error:function(e){
					console.log('ajaxError'+e);
				}
			})
		}		
	</script>
</body>
</html>