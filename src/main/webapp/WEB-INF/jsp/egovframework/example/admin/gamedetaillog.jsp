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
		        	document.listForm.action = "<c:url value='/gameDetailLogp.do'/>";
		           	document.listForm.submit();
		        }
	</script>
	
    <div id="wrapper">
         <c:import url="/leftmenu.do" />
        <div id="page-wrapper">
            <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">${gameid } - Game Detail Log</h1>
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
                                <th>kind</th>
                                <th>useridx</th>
                                <th>value1</th>
                                <th>value2</th>
                                <th>value3</th>
                                <th>value4</th>
                                <th>value5</th>
                                <th>date</th>                                                                
                            </tr>
                        </thead>
                        <tbody>
                        	<c:forEach var="result" items="${resultList }">
                            <tr>
                                <td>${result.gidx}</td>                                
                                <td>${result.gkind}</td>
                                <td>
                                	<c:if test="${result.guseridx ne -1}">
                                	${result.guseridx}
                                	</c:if>                                
                                </td>
                                <td>
                                	<c:if test="${result.gkind eq 'join'}">
                                	소유 잔액:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'twoCard'}">
                                	카드1:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'bet'}">
                                	배팅금액:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'THEFLOP'}">
                                	공개카드1:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'THETURN'}">
                                	공개카드4:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'THERIVER'}">
                                	공개카드5:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'gameEnd'}">
                                	totalmoney:
                                	</c:if>
                                	<c:if test="${result.gvalue1 ne -1}">
                                	${result.gvalue1}                     
                                	</c:if>           	
                                </td>
                                <td>
									<c:if test="${result.gkind eq 'join'}">
                                	좌석번호:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'twoCard'}">
                                	카드2:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'bet'}">
                                	배팅후 잔액:
                                	</c:if>
                                	<c:if test="${result.gkind eq 'THEFLOP'}">
                                	공개카드2:
                                	</c:if>
                                	<c:if test="${result.gvalue2 ne -1}">
                                	${result.gvalue2}
                                	</c:if>
                                </td>
                                <td>
                                	${result.gvalue3}
                                </td>
                                <td>
                                	<c:if test="${result.gkind eq 'THEFLOP'}">
                                	공개카드3:
                                	</c:if>
                                	<c:if test="${result.gvalue4 ne -1}">
                                	<c:if test="${result.gkind ne 'bet'}">
                                		${result.gvalue4}
                                	</c:if>
                                	</c:if>
                                	
                                	<c:if test="${result.gkind eq 'bet' }">
                                		<c:if test="${result.gvalue4 eq 0 }">
                                			다이
                                		</c:if>
                                		<c:if test="${result.gvalue4 eq 1 }">
                                			삥
                                		</c:if>
                                		<c:if test="${result.gvalue4 eq 2 }">
                                			콜
                                		</c:if>
                                		<c:if test="${result.gvalue4 eq 3 }">
                                			따당
                                		</c:if>
                                		<c:if test="${result.gvalue4 eq 4 }">
                                			하프
                                		</c:if>
                                		<c:if test="${result.gvalue4 eq 5 }">
                                			풀
                                		</c:if>
                                		<c:if test="${result.gvalue4 eq 6 }">
                                			맥스
                                		</c:if>	
                                	</c:if>
                                </td>
                                <td>
                                	<c:if test="${result.gkind eq 'bet' }">
                                		다음사람 번호 :
                                	</c:if>
                                	<c:if test="${result.gvalue5 ne -1}">                                	
                                		${result.gvalue5}                                	
                                	</c:if>
                                	

                                	
                                </td>
                                <td>${result.gdate}</td>
                                <td>                                
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
	
	<form action="/holdem/gameDetailLogp.do" name="listForm" id="listForm">
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