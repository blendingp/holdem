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
<meta charset="UTF-8">
<title>WinGame</title>
<script>
function fn_egov_link_page(pageIndex){
	location.href="/holdem/user/notice.do?pageIndex="+pageIndex;
}
</script>
</head>
<body>
<a href="/holdem/user/main.do">메인</a>

<table>
	<tr>
		<th>제목</th>
		<th>조회수</th>
		<th>등록일</th>
	</tr>
 	<c:forEach var="item" items="${noticeListTop}">
		<tr>
			<td><a href="/holdem/user/noticeDetail.do?idx=${item.idx}">★${item.title}</a></td>
			<td>${item.viewCnt}</td>
			<td><fmt:formatDate value="${item.regDate}" pattern="yyyy.MM.dd HH:mm"/></td>
		</tr>
	</c:forEach>
	<c:forEach var="item" items="${noticeList}">
		<tr>
			<td><a href="/holdem/user/noticeDetail.do?idx=${item.idx}">${item.title}</a></td>
			<td>${item.viewCnt}</td>
			<td><fmt:formatDate value="${item.regDate}" pattern="yyyy.MM.dd HH:mm"/></td>
		</tr>
	</c:forEach>
</table>
<ui:pagination paginationInfo="${paginationInfo}" type="image" jsFunction="fn_egov_link_page"/>
</body>
</html>