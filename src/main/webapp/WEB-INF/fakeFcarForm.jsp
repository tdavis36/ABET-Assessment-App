<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Fake FCAR</title>
</head>
<body>
<h2>Create a New FCAR</h2>
<form action="${pageContext.request.contextPath}/ProfessorServlet" method="post">
    <!-- Tells the servlet what to do -->
    <input type="hidden" name="action" value="submitFakeFCAR"/>

    <label>Course ID:</label>
    <input type="text" name="courseId" required /><br/><br/>

    <label>Professor ID:</label>
    <input type="text" name="professorId" required /><br/><br/>

    <label>Semester:</label>
    <input type="text" name="semester" value="Spring" /><br/><br/>

    <label>Year:</label>
    <input type="number" name="year" value="2025" /><br/><br/>

    <button type="submit">Submit FCAR</button>
</form>
</body>
</html>
