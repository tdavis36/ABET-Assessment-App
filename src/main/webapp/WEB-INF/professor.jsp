<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Professor Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="dashboard">
        <h1>Welcome, Dr. [Professor Name]</h1>
        <a href="${pageContext.request.contextPath}/" class="btn">Logout</a>
        
        <div class="status-key">
            <div><span class="status not-started"></span> Not Started</div>
            <div><span class="status in-progress"></span> In Progress</div>
            <div><span class="status submitted"></span> Submitted</div>
            <div><span class="status completed"></span> Completed</div>
        </div>
        
        <div class="section">
            <h2>Outstanding Tasks</h2>
            <div class="task-box">
                <div class="task-item">
                    <div><span class="status not-started"></span> Task 1</div>
                    <div class="task-actions">
                        <span class="uploaded-docs">report.pdf (0% completed)</span>
                        <button class="btn">Open</button>
                        <button class="btn">Submit</button>
                    </div>
                </div>
                <div class="task-item">
                    <div><span class="status in-progress"></span> Task 2</div>
                    <div class="task-actions">
                        <span class="uploaded-docs">assessment.docx (50% completed)</span>
                        <button class="btn">Open</button>
                        <button class="btn">Submit</button>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="section">
            <h2>Completed Tasks</h2>
            <div class="task-box">
                <div class="task-item">
                    <div><span class="status submitted"></span> Task A</div>
                    <span class="uploaded-docs">summary.docx (100% completed)</span>
                </div>
                <div class="task-item">
                    <div><span class="status completed"></span> Task B</div>
                    <span class="uploaded-docs">final_report.pdf (100% completed)</span>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
