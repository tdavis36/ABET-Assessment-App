<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header>
  <!-- Add the link to your stylesheet -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">

  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
      <a class="navbar-brand" href="${pageContext.request.contextPath}/">ABET Assessment System</a>

      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarMain">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarMain">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/">Home</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/AdminServlet">Admin</a>
          </li>
          <li class="nav-item active">
            <a class="nav-link" href="${pageContext.request.contextPath}/ReportServlet">Reports</a>
          </li>
        </ul>

        <ul class="navbar-nav ml-auto">
          <c:if test="${not empty sessionScope.user}">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                 data-toggle="dropdown">
                  ${sessionScope.user.firstName} ${sessionScope.user.lastName}
              </a>
              <div class="dropdown-menu dropdown-menu-right">
                <a class="dropdown-item" href="${pageContext.request.contextPath}/UserServlet?action=profile">
                  My Profile
                </a>
                <div class="dropdown-divider"></div>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/LogoutServlet">
                  Logout
                </a>
              </div>
            </li>
          </c:if>
          <c:if test="${empty sessionScope.user}">
            <li class="nav-item">
              <a class="nav-link" href="${pageContext.request.contextPath}/LoginServlet">Login</a>
            </li>
          </c:if>
        </ul>
      </div>
    </div>
  </nav>
</header>