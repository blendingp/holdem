<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<!--  This site was created in Webflow. http://www.webflow.com  -->
<!--  Last Published: Tue Dec 29 2020 07:26:58 GMT+0000 (Coordinated Universal Time)  -->
<html data-wf-page="5fead5b30531f8fb18593f53" data-wf-site="5faa9fb759c008dd7db7c885">
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include>
</head>
<body>
	<div class="frame">
		<div class="bg-copy">
			<jsp:include page="../frame/userMenu.jsp"></jsp:include>
			<div class="body">
				<div class="w-form">
					<form id="joinform" name="joinform">
						<div class="notititlebox">
							<img src="images/WINGAME.png" loading="lazy" alt="" class="notilogo">
							<div class="notititle">
								윈게임<span class="text-span-10">회원가입</span>센터입니다.
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
								<a href="#" class="listhomelink w-inline-block">
									<div class="listlink click">회원가입</div>
								</a>
							</div>
						</div>
						<div class="myinfobox">
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">아이디</div>
								</div>
								<div class="jnwrap">
									<input type="text" class="jinput2 w-input" maxlength="256" name="muserid" placeholder="아이디를 입력해주세요" id="muserid" >
								</div>
								<a href="javascript:checkId()" class="overlapbtn w-button">중복확인</a>
								<div class="jnotice" id="museridch" style="display:none;">아이디를 입력해주세요.</div>
								<div class="jnotice" id="museridch2" style="display:none;">이미 존재하는 아이디입니다.</div>
								<div class="jnotice2" id="museridch3" style="display:none;">사용가능한 아이디입니다.</div> 
							</div>
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">비밀번호</div>
								</div>
								<div class="jnwrap">
									<input type="password" class="jinput w-input" maxlength="256" name="muserpw" placeholder="비밀번호(8~15자리 영문,숫자,특수문자(~!@#$%^&*) 조합)" id="muserpw" >
								</div>
								<div class="jnotice" id="muserpwch" style="display:none;">비밀번호를 입력해주세요.</div>
							</div>
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">닉네임</div>
								</div>
								<div class="jnwrap">
									<input type="text" class="jinput2 w-input" maxlength="256" name="nickname" placeholder="닉네임을 입력해주세요" id="nickname" >
								</div>
								<a href="javascript:checkNick()" class="overlapbtn w-button">중복확인</a>
								<div class="jnotice" id="nicknamech" style="display:none;">닉네임을 입력해주세요.</div>
								<div class="jnotice" id="nicknamech2" style="display:none;">이미 존재하는 닉네임입니다.</div>
								<div class="jnotice2" id="nicknamech3" style="display:none;">사용 가능한 닉네임입니다.</div>
							</div>
							<input type="hidden" name="birthDate" value="${birthDate}"/>
							<input type="hidden" name="uid" value="${birthDate}"/>
							<input type="hidden" name="autchTick" value="${autchTick}"/>
							<%-- <div class="jrow">
								<div class="jbox">
									<div class="jtxt">생년월일</div>
								</div>
								<select id="birthY" name="birthY" class="select-field-3 w-select">
									<jsp:useBean id="now" class="java.util.Date" />
									<fmt:formatDate value="${now}" pattern="yyyy" var="yearStart"/>
									<c:forEach begin="0" end="${yearStart - 1930}" var="result" step="1">
									    <option value="${yearStart-result}" >${yearStart-result}년</option>
									</c:forEach>								
								</select>
								<select id="birthM" name="birthM" class="select-field-3 w-select">
									<c:forEach begin="1" end ="12" var="month">
										<option value="${month}">${month}월</option>
									</c:forEach>
								</select>
								<select id="birthD" name="birthD" class="select-field-3 w-select">
									<c:forEach begin="1" end ="31" var="day">
										<option value="${day}">${day}일</option>
									</c:forEach>
								</select>
							</div> --%>
							<div class="notice">※ 회원님의 소중한 개인정보는 WINGAME 개인정보처리방침에 따라 안전하게 관리되고 있습니다.</div>
						</div>
						<a href="#" onclick="javascript:checkContents()" class="deletebtn-7 a w-inline-block">
							<div class="deletebtntext">가입하기</div>
						</a>
					</form>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
	<script>
		var idChk = false;
		var nickChk = false;
		function checkId(){
			$("[id*=museridch]").css("display","none");
			var muserid = document.getElementById("muserid").value;
			if(muserid.length == 0)
			{
				$("#museridch").css("display","flex");
				return;
			}
			$.ajax({
				type:'post',
				url:'/holdem/user/checkId.do?muserid='+muserid,
				success:function(data){
					if(data == 'success')
					{
						$("#museridch3").css("display","flex");
						$("#muserid").attr("readonly" ,true);
						idChk = true;
					}
					else
					{
						$("#museridch2").css("display","flex");
					}
				}
			})
		}
		function checkNick(){
			$("[id*=nicknamech]").css("display","none");
			var nickname = document.getElementById("nickname").value;
			
			if(nickname.length == 0)
			{
				$("#nicknamech").css("display","flex");
				return;
			}
			$.ajax({
				type:'post',
				url:'/holdem/user/checkNick.do?nickname='+nickname,
				success:function(data){
					if(data == 'success')
					{
						$("#nicknamech3").css("display","flex");
						$("#nickname").attr("readonly" ,true);
						nickChk= true;
					}
					else
					{
						$("#nicknamech2").css("display","flex");
					}
				}
			})
		}
		function checkContents(){
			var muserid = document.getElementById("muserid").value;
			var muserpw = document.getElementById("muserpw").value;
			var nickname = document.getElementById("nickname").value;
			$("#museridch , #muserpwch , #nicknamech").css('display' , 'none');
			var pattern1 = /[0-9]/; // 숫자 
			var pattern2 = /[a-zA-Z]/; // 문자 
			var pattern3 = /[~!@#$%^&*]/;
			if(muserid.length == 0)
			{
				$("#museridch").css("display","flex");
			}
			if(muserpw.length == 0)
			{
				$("#muserpwch").css("display","flex");
			}
			if(nickname.length == 0)
			{
				$("#nicknamech").css("display","flex");
				return;
			}
			if(!idChk)
			{
				alert("아이디 중복확인이 필요합니다.");
				return;
			}
			if(muserpw.length < 8 || muserpw.length > 15)
			{
				alert("비밀번호는 8~15자리로 입력해주세요.");
				return;
			}
 			if(!pattern1.test(muserpw) || !pattern2.test(muserpw) || !pattern3.test(muserpw))
			{
				alert("비밀번호는 영문 , 숫자 , 특수문자(~!@#$%^&*)를 포함해서 작성해야합니다.");
				return;
			} 
			if(!nickChk)
			{
				alert("닉네임 중복확인이 필요합니다.");
				return;
			}
			var param = $("#joinform").serialize();
			$.ajax({
				type:'post',
				data:param,
				url:'/holdem/user/joinInsert.do',
				success:function(data){
					console.log(data);
					if(data == 'id')
					{
						checkId()
						idChk=false;
					}
					else if (data == 'nick')
					{
						checkNick()
						nickChk = false;
					}
					else
					{
						alert('회원가입 완료되었습니다. 메인으로 이동합니다.');
						location.href="/holdem/user/main.do";
					}
				}
			})
			/* if(muserid.length != 0 && muserpw.length != 0 && nickname.length != 0)
			{
				var fm = document.getElementById('joinform');
				fm.submit();
			} */
		}
	</script>
</body>
</html>