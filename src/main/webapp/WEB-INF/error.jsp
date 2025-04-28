<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Error - ABET Assessment System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="dashboard" id="errorDashboard">
  <div class="header-container">
    <h1>Error</h1>
  </div>

  <div class="section">
    <div class="card error-card">
      <div class="card-header bg-danger text-white">
        <h3><i class="fas fa-exclamation-triangle"></i> An Unexpected Error Occurred</h3>
      </div>
      <div class="card-body">
        <p class="error-message">${error}</p>
        <div class="action-buttons">
          <form action="${pageContext.request.contextPath}/ReportServlet" method="get" style="display:inline;">
            <button type="submit" class="btn">Back to Reports</button>
          </form>
          <form action="${pageContext.request.contextPath}/" method="get" style="display:inline;">
            <button type="submit" class="btn">Home</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>
