<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:include page="../frame/admintop.jsp" flush="true" />
</head>
<body>
	<script>
		        function fn_egov_link_page(pageNo){
		        	document.listForm.pageIndex.value = pageNo;
		        	document.listForm.action = "<c:url value='/gamelogp.do'/>";
		           	document.listForm.submit();
		        }
	</script>
	
    <div id="wrapper">
         <c:import url="/leftmenu.do" />
        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Game Index</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->

			<div class="panel-body">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>번호</th>
                                <th>보기</th>                                 
                            </tr>
                        </thead>
                        <tbody>
                        	<c:forEach var="result" items="${resultList }">
                            <tr>
                                <td>${result.gidx}</td>
                                <td>${result.gameid}</td>
                                <td>
                                <button type="button" onClick="location.href='/holdem/gameDetailLogp.do?gameid=${result.gameid}'" class="btn btn-primary btn-sm">보기</button>
                                </td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div style="display:flex;justify-content:center">
                    <ui:pagination paginationInfo="${paginationInfo }" type="image" jsFunction="fn_egov_link_page"/>
                    </div>
                </div>
                <!-- /.table-responsive -->
            </div>
        </div>
        <!-- /#page-wrapper -->
    </div>
    <!-- /#wrapper -->	
	
	<form action="/holdem/gamelogp.do" name="listForm" id="listForm">
		<input type="hidden" name="pageIndex" value="1"/>
	</form>	

    <!-- jQuery -->
    <script src="/holdem/vendor/jquery/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="/holdem/vendor/bootstrap/js/bootstrap.min.js"></script>

    <!-- Metis Menu Plugin JavaScript -->
    <script src="/holdem/vendor/metisMenu/metisMenu.min.js"></script>

    <!-- Custom Theme JavaScript -->
    <script src="/holdem/dist/js/sb-admin-2.js"></script>
	
</body>
</html>