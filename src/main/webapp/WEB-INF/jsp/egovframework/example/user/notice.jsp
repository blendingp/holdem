<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include>
</head>
<script>
function fn_egov_link_page(pageIndex){
	location.href="/holdem/user/notice.do?pageIndex="+pageIndex;
}
</script>
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
				<div class="noticebox">
					<div class="table-19">
						<div class="ttop-9">
							<div class="tnum-7">순번</div>
							<div class="ttitle-8">제목</div>
							<div class="tnum-7">조회수</div>
							<div class="tdate-7">등록일</div>
						</div>
						<c:forEach var="item" items="${noticeListTop}">
							<a href="/holdem/user/noticeDetail.do?idx=${item.idx}" class="rowbtn w-inline-block">
								<div class="row-11">
									<div class="tnum2-4">[고정]</div>
									<div class="ttitle2-2">${item.title}</div>
									<div class="tnum2-4">${item.viewCnt }</div>
									<div class="tdate2-4"><fmt:formatDate value="${item.regDate}" pattern="yyyy-MM-dd"/></div>
								</div>
							</a>
						</c:forEach>
						<c:forEach var="item" items="${noticeList}">
							<a href="/holdem/user/noticeDetail.do?idx=${item.idx}" class="rowbtn w-inline-block">
								<div class="row-11">
									<div class="tnum2-4">${item.no}</div>
									<div class="ttitle2-2">${item.title}</div>
									<div class="tnum2-4">${item.viewCnt }</div>
									<div class="tdate2-4"><fmt:formatDate value="${item.regDate}" pattern="yyyy-MM-dd"/></div>
								</div>
							</a>
						</c:forEach>
					</div>
					<div class="bottombtnbox-2" style="width:auto;">
						<ui:pagination paginationInfo="${paginationInfo}" type="user" jsFunction="fn_egov_link_page"/>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
</body>
</html>