<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include></head>
<body>
	<div class="frame">
		<div class="bg-copy">
			<jsp:include page="../frame/userMenu.jsp"></jsp:include>
			<div class="body">
				<div class="notititlebox">
					<img src="/holdem/webflow/images/WINGAME.png" loading="lazy" alt="" class="notilogo">
					<div class="notititle">윈게임<span class="text-span-10">LD 플레이어 설명 가이드</span>센터입니다.</div>
					<div class="notibottitle">윈게임을 이용해 주셔서 감사합니다.<br>궁금하신 사항이 있으시다면 언제든지 문의해 주시기 바랍니다.</div>
				</div>
				<div class="togbox">
					<div class="listclick">
						<a href="/holdem/user/main.do" class="listhomelink w-inline-block">
							<div class="listlink">HOME</div>
						</a>
						<div class="listhome">&gt;</div>
						<a href="/holdem/user/ldPlayerGuide.do" class="listhomelink w-inline-block">
						<div class="listlink click">LD 플레이어 설명 가이드</div>
						</a>
					</div>
				</div>
				<div class="quedetail">
					<div class="table-19">
						<div class="guidebox">
							<div class="extext">
								<span>1.</span> Google, naver, daum 등 시작페이지에서 LD플레이어를 검색하여, 홈페이지로 접속한다.
							</div>
							<div class="div-block-12">
								<img src="/holdem/webflow/images/ex1.png" loading="lazy"
									sizes="(max-width: 767px) 80vw, (max-width: 880px) 100vw, 880px"
									srcset="/holdem/webflow/images/ex1-p-500.png 500w, /holdem/webflow/images/ex1.png 880w" alt=""
									class="exlmage">
							</div>
							<div class="extext">2. download LDPlayer를 클릭한다.</div>
							<div class="div-block-12">
								<img src="/holdem/webflow/images/ex2.png" loading="lazy"
									sizes="(max-width: 767px) 80vw, (max-width: 991px) 100vw, 1038px"
									srcset="/holdem/webflow/images/ex2-p-500.png 500w, /holdem/webflow/images/ex2-p-800.png 800w, /holdem/webflow/images/ex2-p-1080.png 1080w, /holdem/webflow/images/ex2-p-1600.png 1600w, /holdem/webflow/images/ex2.png 1823w"
									alt="" class="exlmage">
							</div>
							<div class="extext-copy">3. 다운로드 받은 파일을 실행시킨다.</div>
							<div class="extext">4. 즉시 설치를 눌러 LD플레이어 설치를 완료한다.</div>
							<div class="div-block-12">
								<img src="/holdem/webflow/images/ex3.png" loading="lazy"
									sizes="(max-width: 767px) 80vw, 621px"
									srcset="/holdem/webflow/images/ex3-p-500.png 500w, /holdem/webflow/images/ex3.png 621w" alt=""
									class="exlmage">
							</div>
							<div class="extext">5. 설치가 완료되면 시작하기를 클릭한다.</div>
							<div class="div-block-12">
								<img src="/holdem/webflow/images/ex4.png" loading="lazy"
									sizes="(max-width: 767px) 80vw, 621px"
									srcset="/holdem/webflow/images/ex4.png 500w, /holdem/webflow/images/ex4.png 621w" alt=""
									class="exlmage">
							</div>
							<div class="extext">6. vvingame.com 홈페이지 하단에서 Google Play
								다운로드 버튼을 클릭한다.</div>
							<div class="div-block-12">
								<img src="/holdem/webflow/images/ex5.png" loading="lazy"
									sizes="(max-width: 767px) 80vw, (max-width: 991px) 100vw, 1038px"
									srcset="/holdem/webflow/images/ex5.png 500w, /holdem/webflow/images/ex5.png 800w, /holdem/webflow/images/ex5.png 1091w"
									alt="" class="exlmage">
							</div>
							<div class="extext">7. 설치된 파일을 우클릭하여 압축 풀기를 실행해준다.</div>
							<div class="div-block-12">
								<img src="/holdem/webflow/images/ex6.png" loading="lazy" alt="" class="exlmage">
							</div>
							<div class="extext">8. 압축풀기를 클릭하여 진행한다.</div>
							<div class="div-block-12">
								<img src="/holdem/webflow/images/ex7.png" loading="lazy" alt="" class="exlmage">
							</div>
							<div class="extext-copy">9. 압축을 해제한 폴더로 들어가서 apk 파일을
								더블클릭해준다.</div>
							<div class="extext">
								10. 자동으로 LD플레이어와 연결되어 LD플레이어 창이 열리고, <br>잠시 후에 Holdem 파일이
								화면에 생성된다. 이 파일을 실행하여 게임을 시작하면 된다.
							</div>
							<div class="div-block-12">
								<img src="/holdem/webflow/images/ex8.png" loading="lazy"
									sizes="(max-width: 767px) 80vw, (max-width: 991px) 100vw, 1038px"
									srcset="/holdem/webflow/images/ex8.png 500w, /holdem/webflow/images/ex8.png 800w, /holdem/webflow/images/ex8.png 1080w, /holdem/webflow/images/ex8.png 1541w"
									alt="" class="exlmage">
							</div>
						</div>
						<a href="/holdem/user/main.do" class="deletebtn-7 a w-inline-block">
							<div class="deletebtntext">홈으로</div>
						</a>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
</body>
</html>