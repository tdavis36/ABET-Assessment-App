<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ABETAppTeam.model.FCAR" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Professor Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<!-- same navbar as admin -->
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="dashboard" id="profDashboard">
    <div class="header-container">
        <h1>Professor Dashboard</h1>
    </div>

    <!-- Status Key (identical to admin) -->
    <div class="status-key">
        <div><span class="status draft"></span> Draft</div>
        <div><span class="status submitted"></span> Submitted</div>
        <div><span class="status approved"></span> Approved</div>
        <div><span class="status rejected"></span> Rejected</div>
    </div>

    <!-- Assigned FCARs -->
    <div class="section">
        <h2>Your Assigned FCARs</h2>

        <c:choose>
            <c:when test="${not empty assignedFCARs}">
                <div class="fcar-box">
                    <c:forEach var="fcar" items="${assignedFCARs}">
                        <div class="fcar-item">
                            <div>
                                <span class="status ${fn:toLowerCase(fcar.status)}"></span>
                                <strong>Course:</strong> <c:out value="${fcar.courseId}"/> –
                                <strong>Semester:</strong> <c:out value="${fcar.semester}"/> <c:out value="${fcar.year}"/> –
                                <strong>Status:</strong> <c:out value="${fcar.status}"/>
                            </div>
                            <div class="fcar-actions">
                                <!-- Open/Edit -->
                                <form method="get"
                                      action="${pageContext.request.contextPath}/ViewFCARServlet"
                                      style="display:inline;">
                                    <input type="hidden" name="action" value="editFCAR"/>
                                    <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                    <button class="btn" type="submit">Open / Edit</button>
                                </form>
                                <!-- Submit if not yet submitted/approved -->
                                <c:if test="${fcar.status != 'Submitted' && fcar.status != 'Approved'}">
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/ProfessorServlet"
                                          style="display:inline;">
                                        <input type="hidden" name="action" value="submitFCARStatus"/>
                                        <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                        <button class="btn" type="submit">Submit</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p>No FCARs assigned to you.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
