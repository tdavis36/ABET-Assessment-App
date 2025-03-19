<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="dashboard" id="adminDashboard">
        <h1>Welcome, Dr. [Admin Name]</h1>
        <a href="${pageContext.request.contextPath}/" class="btn">Logout</a>

        <div class="section">
            <h2>Professor Task Overview</h2>
            <select id="professorSort" onchange="sortProfessors()">
                <option value="mostOutstanding">Most Outstanding Tasks</option>
                <option value="mostCompleted">Most Completed Tasks</option>
            </select>
            <div id="professorList">
                <details>
                    <summary>Dr. Smith - 3 Outstanding, 5 Completed</summary>
                    <div class="task-box">
                        <div class="task-item">Task 1 - Not Started</div>
                        <div class="task-item">Task 2 - In Progress</div>
                    </div>
                </details>
                <details>
                    <summary>Dr. Johnson - 5 Outstanding, 2 Completed</summary>
                    <div class="task-box">
                        <div class="task-item">Task 3 - Not Started</div>
                        <div class="task-item">Task 4 - Submitted</div>
                    </div>
                </details>
            </div>
        </div>
        
        <div class="section">
            <h2>All Tasks Overview</h2>
            <select id="taskSort" onchange="sortTasks()">
                <option value="percentComplete">Sort by % Complete</option>
                <option value="urgency">Sort by Urgency</option>
            </select>
            <div id="taskList">
                <div class="task-item">Task 1 - Dr. Smith (20% completed, High Urgency)</div>
                <div class="task-item">Task 2 - Dr. Johnson (50% completed, Medium Urgency)</div>
            </div>
        </div>

        <!-- Create New Task (FORM Version) -->
        <div class="section">
            <h2>Create New Task</h2>
            <form action="${pageContext.request.contextPath}/AdminServlet" method="post">
                <!-- Hidden action parameter, so the servlet knows what to do -->
                <input type="hidden" name="action" value="assignTask" />

                <label>Task Name:</label>
                <input type="text" name="taskName" required />

                <label>Form Template:</label>
                <input type="text" name="formTemplate" />

                <label>Urgency:</label>
                <select name="urgency">
                    <option value="high">High</option>
                    <option value="medium">Medium</option>
                    <option value="low">Low</option>
                </select>

                <label>Assign to Professor:</label>
                <select name="professorId">
                    <option value="Dr. Smith">Dr. Smith</option>
                    <option value="Dr. Johnson">Dr. Johnson</option>
                    <!-- Possibly use real professor IDs here -->
                </select>

                <button class="btn" type="submit">Submit</button>
            </form>
        </div>

</body>
</html>
