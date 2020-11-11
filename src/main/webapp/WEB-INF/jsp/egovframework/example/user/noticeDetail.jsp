<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include>
</head>
<body>
<div class="frame">
		<div class="bg">
			<jsp:include page="../frame/userMenu.jsp"></jsp:include>
			<div class="body">
				<div class="notititlebox">
					<img src="/holdem/webflow/images/WINGAME.png" loading="lazy" alt="" class="notilogo">
					<div class="notititle">
						윈게임<span class="text-span-10">공지사항</span>센터입니다.
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
						<a href="/holdem/user/notice.do" class="listhomelink w-inline-block">
							<div class="listlink click">공지사항</div>
						</a>
					</div>
				</div>
				<div class="noticedetail">
					<div class="table-19">
						<div class="ttop-9">
							<div class="noticetilte">[공지] ${noticeDatail.title}</div>
							<div class="div-block-9">
								<div class="tdate-7-copy">
									등록일<span class="text-span-11"><fmt:formatDate value="${noticeDatail.regDate}" pattern="yyyy-MM-dd"/></span>
								</div>
								<div class="tdate-7-copy">
									조회수<span class="text-span-11">${noticeDatail.viewCnt}</span>
								</div>
							</div>
						</div>
						<a href="#" class="rowbtn w-inline-block"></a>
						<div class="notidetailbox">
							<div class="notidetext">
								${noticeDatailText}
							</div>
						</div>
					</div>
					<a href="/holdem/user/notice.do" class="deletebtn-7 a w-inline-block">
						<div class="deletebtntext">목록으로</div>
					</a>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
</body>
</html>