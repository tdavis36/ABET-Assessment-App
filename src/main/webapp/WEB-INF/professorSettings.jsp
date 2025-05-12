<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Settings - ABET Assessment</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .message-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: var(--card-bg);
            border-radius: 5px;
            box-shadow: 0 1px 3px var(--shadow);
            text-align: center;
        }
        
        .message-container h1 {
            color: var(--primary);
            margin-bottom: 20px;
        }
        
        .message-container p {
            font-size: 1.1em;
            line-height: 1.5;
            margin-bottom: 20px;
        }
        
        .btn {
            display: inline-block;
            padding: 10px 20px;
            background-color: var(--primary);
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
            margin-top: 20px;
        }
        
        .btn:hover {
            background-color: var(--primary-dark);
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/navbar.jsp" />
    
    <div class="message-container">
        <h1>Settings</h1>
        <p>Settings are only available for administrators.</p>
        <p>Please contact your system administrator if you need to change any settings.</p>
        <a href="${pageContext.request.contextPath}/ProfessorServlet" class="btn">Back to Dashboard</a>
    </div>
</body>
</html>