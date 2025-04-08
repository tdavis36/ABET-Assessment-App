<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ABET Assessment Application</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div class="dashboard">
    <div class="header-container">
        <h1>ABET Assessment Application</h1>
    </div>

    <div class="section">
        <h2>Sign In</h2>

        <% if (request.getAttribute("error") != null) { %>
        <div style="color: var(--danger); margin-bottom: 20px; padding: 10px; background-color: #ffebee; border-radius: 5px; border: 1px solid #ffcdd2;">
            <%= request.getAttribute("error") %>s
        </div>
        <% } %>

        <div class="form-container">
            <form action="${pageContext.request.contextPath}/index" method="post">
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" required>
                </div>

                <div class="form-group">
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <div style="display: flex; justify-content: space-between; margin-top: 20px;">
                    <button type="submit" class="btn-submit">Login</button>
                </div>
            </form>
        </div>
    </div>

    <div class="section">
        <h2>About ABET Assessment App</h2>
        <p>This application streamlines the ABET assessment process by automating data collection and reporting.</p>

        <h3>Key Features:</h3>
        <ul>
            <li>Faculty Course Assessment Reports (FCARs)</li>
            <li>Course-to-Professor Mapping</li>
            <li>Student Learning Outcomes Assessment</li>
            <li>Data Export and Reporting</li>
        </ul>
    </div>
</div>

<footer style="background-color: var(--primary-dark); color: white; text-align: center; padding: 1rem; margin-top: 2rem;">
    <p>&copy; 2025 ABET Assessment App Team. All rights reserved.</p>
</footer>
</body>
</html>