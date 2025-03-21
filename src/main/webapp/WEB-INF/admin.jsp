<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .form-section {
            margin-bottom: 20px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], input[type="number"], select, textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .btn-submit {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .btn-submit:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="dashboard" id="adminDashboard">
        <h1>Welcome, Dr. [Admin Name]</h1>
        <a href="${pageContext.request.contextPath}/" class="btn">Logout</a>

        <!-- Action Buttons -->
        <div style="display: flex; gap: 10px; margin-bottom: 20px;">
            <form action="${pageContext.request.contextPath}/ViewFCARServlet" method="get">
                <input type="hidden" name="action" value="viewAll"/>
                <button type="submit" class="btn">View FCARs</button>
            </form>
        </div>

        <!-- View FCARs Section -->
        <div class="section">
            <h2>FCARs</h2>

            <!-- Dynamically display all created FCARs -->
            <div id="fcarList">
                <h3>Existing FCARs</h3>
                <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
                <c:choose>
                    <c:when test="${not empty allFCARs}">
                        <ul>
                            <c:forEach var="fcar" items="${allFCARs}">
                                <li>
                                    <strong>Professor:</strong> ${fcar.professorId} -
                                    <strong>Course:</strong> ${fcar.courseId} -
                                    <strong>Status:</strong> ${fcar.status}
                                    <form method="get" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                                        <input type="hidden" name="action" value="editFCAR"/>
                                        <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                        <button type="submit" class="btn">Edit</button>
                                    </form>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p>No FCARs available.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Create New FCAR Section -->
        <div class="section">
            <h2>Create New FCAR</h2>
            <div class="form-section">
                <form action="${pageContext.request.contextPath}/AdminServlet" method="post">
                    <input type="hidden" name="action" value="createFCAR" />
                    
                    <div class="form-group">
                        <label for="courseId">Course ID:</label>
                        <input type="text" id="courseId" name="courseId" required />
                    </div>
                    
                    <div class="form-group">
                        <label for="professorId">Assign to Professor:</label>
                        <select id="professorId" name="professorId" required>
                            <option value="">Select a Professor</option>
                            <c:forEach var="professor" items="${professors}">
                                <option value="${professor.id}">${professor.name}</option>
                            </c:forEach>
                            <!-- Real professor names instead of placeholders -->
                            <option value="Smith">Dr. Smith</option>
                            <option value="Johnson">Dr. Johnson</option>
                            <option value="Williams">Dr. Williams</option>
                            <option value="Davis">Dr. Davis</option>
                            <option value="Miller">Dr. Miller</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="semester">Semester:</label>
                        <select id="semester" name="semester" required>
                            <option value="Spring">Spring</option>
                            <option value="Summer">Summer</option>
                            <option value="Fall">Fall</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="year">Year:</label>
                        <input type="number" id="year" name="year" min="2020" max="2030" value="2025" required />
                    </div>
                    
                    <button type="submit" class="btn-submit">Create FCAR</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
