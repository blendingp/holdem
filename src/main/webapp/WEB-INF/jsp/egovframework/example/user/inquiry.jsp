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
</head>
<body>
<script>
function fn_egov_link_page(pageIndex){
	location.href="/holdem/user/inquiry.do?pageIndex="+pageIndex;
}
</script>

<a href="/holdem/user/main.do">메인</a>
<a href="/holdem/user/inquiryAdd.do">문의하기</a>

<table>
	<tr>
		<th>제목</th>
		<th>등록일</th>
		<th>답변 여부</th>
	</tr>
	<c:forEach var="item" items="${inquiryList}">
		<tr>
			<td><a href="javascript:void(window.open('/holdem/user/inquiryChk.do?idx=${item.idx}','inquiry','width=400px, height=200px'))">${item.title}</a></td>
			<td><fmt:formatDate value="${item.regDate}" pattern="yyyy.MM.dd HH:mm"/></td>
			<td>&ensp;&ensp;&ensp;
				<c:if test="${item.answer == null }">N</c:if>
				<c:if test="${item.answer != null }">Y</c:if>
			</td>
		</tr>
	</c:forEach>
</table>
<ui:pagination paginationInfo="${paginationInfo}" type="image" jsFunction="fn_egov_link_page"/>
</body>
</html>