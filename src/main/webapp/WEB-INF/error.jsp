<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Error - ABET Assessment System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="header.jsp" />

<div class="container">
  <div class="error-container">
    <div class="card">
      <div class="card-header bg-danger text-white">
        <h3><i class="fas fa-exclamation-triangle"></i> Error</h3>
      </div>
      <div class="card-body">
        <h4>An error occurred:</h4>
        <p class="error-message">${error}</p>

        <div class="mt-4">
          <a href="${pageContext.request.contextPath}/ReportServlet" class="btn btn-primary">
            <i class="fas fa-arrow-left"></i> Back to Reports
          </a>
          <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
            <i class="fas fa-home"></i> Home
          </a>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>