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
        
        <div class="section">
            <h2>Create New Task</h2>
            <label>Task Name:</label>
            <input type="text" id="taskName" />
            <label>Form Template:</label>
            <input type="text" id="formTemplate" />
            <label>Urgency:</label>
            <select id="taskUrgency">
                <option value="high">High</option>
                <option value="medium">Medium</option>
                <option value="low">Low</option>
            </select>
            <label>Assign to Professor:</label>
            <select id="assignProfessor">
                <option value="Dr. Smith">Dr. Smith</option>
                <option value="Dr. Johnson">Dr. Johnson</option>
            </select>
            <button class="btn" onclick="createTask()">Submit</button>
        </div>
    </div>

    <script>
        function createTask() {
            let taskName = document.getElementById("taskName").value;
            let professor = document.getElementById("assignProfessor").value;
            let urgency = document.getElementById("taskUrgency").value;
            let newTask = document.createElement("div");
            newTask.classList.add("task-item");
            newTask.innerHTML = `${taskName} - ${professor} (${urgency} Urgency)`;
            document.getElementById("taskList").appendChild(newTask);
        }
    </script>
</body>
</html>
