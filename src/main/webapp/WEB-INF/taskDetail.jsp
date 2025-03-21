<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test Page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="container">
        <h1>Test Page</h1>
        <p>This is the task details jsp file.</p>
        <p>Current time: <%= new java.util.Date() %></p>
    </div>
</body>
</html>
