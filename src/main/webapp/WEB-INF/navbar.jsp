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
        <c:if test="${sessionScope.user != null && sessionScope.user['class'].simpleName == 'Admin'}">
          <li class="navbar-item">
            <a class="navbar-link ${activePage == 'admin' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin">Admin Dashboard</a>
          </li>
          <li class="navbar-item">
            <a class="navbar-link ${activePage == 'settings' ? 'active' : ''}" href="${pageContext.request.contextPath}/settings">Settings</a>
          </li>
        </c:if>
        <c:if test="${sessionScope.user != null && sessionScope.user['class'].simpleName == 'Professor'}">
          <li class="navbar-item">
            <a class="navbar-link ${activePage == 'professor' ? 'active' : ''}" href="${pageContext.request.contextPath}/professor">Professor Dashboard</a>
          </li>
          <li class="navbar-item">
            <a class="navbar-link ${activePage == 'settings' ? 'active' : ''}" href="${pageContext.request.contextPath}/professorsettings">Settings</a>
          </li>
        </c:if>
        <li class="navbar-item">
          <a class="navbar-link ${activePage == 'reports' ? 'active' : ''}" href="${pageContext.request.contextPath}/reports">Reports</a>
        </li>
        <li class="navbar-item">
          <a class="navbar-link ${activePage == 'viewFCAR' ? 'active' : ''}" href="${pageContext.request.contextPath}/view?action=viewAll">View FCARs</a>
        </li>
      </ul>
    </div>

    <div class="navbar-right">
      <c:if test="${sessionScope.user != null && not empty sessionScope.user}">
        <span class="user-greeting">Welcome, ${sessionScope.user.firstName}</span>
        <a class="navbar-link logout-link" href="${pageContext.request.contextPath}/logout">
          Logout
        </a>
      </c:if>
    </div>
  </nav>
</div>