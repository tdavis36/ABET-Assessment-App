<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="navbar-container">
  <nav class="navbar">
    <div class="navbar-brand">
      <a href="${pageContext.request.contextPath}/">
        ABET Assessment
      </a>
    </div>

    <div class="navbar-menu">
      <ul class="navbar-items">
        <c:if test="${sessionScope.userRole == 'admin'}">
          <li class="navbar-item">
            <a class="navbar-link ${activePage == 'admin' ? 'active' : ''}" href="${pageContext.request.contextPath}/AdminServlet">Admin Dashboard</a>
          </li>
        </c:if>
        <c:if test="${sessionScope.userRole == 'professor'}">
          <li class="navbar-item">
            <a class="navbar-link ${activePage == 'professor' ? 'active' : ''}" href="${pageContext.request.contextPath}/ProfessorServlet">Professor Dashboard</a>
          </li>
        </c:if>
        <li class="navbar-item">
          <a class="navbar-link ${activePage == 'reports' ? 'active' : ''}" href="${pageContext.request.contextPath}/ReportServlet">Reports</a>
        </li>
        <li class="navbar-item">
          <a class="navbar-link ${activePage == 'viewFCAR' ? 'active' : ''}" href="${pageContext.request.contextPath}/ViewFCARServlet?action=viewAll">View FCARs</a>
        </li>
      </ul>
    </div>

    <div class="navbar-right">
      <c:if test="${not empty sessionScope.user}">
        <span class="user-greeting">Welcome, ${sessionScope.user.firstName} ${sessionScope.user.lastName}</span>
        <a class="navbar-link logout-link" href="${pageContext.request.contextPath}/${sessionScope.userRole == 'admin' ? 'AdminServlet?action=logout' : (sessionScope.userRole == 'professor' ? 'ProfessorServlet?action=logout' : '?action=logout')}">
          Logout
        </a>
      </c:if>
    </div>
  </nav>
</div>
