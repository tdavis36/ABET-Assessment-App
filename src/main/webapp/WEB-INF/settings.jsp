<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>ABET App Settings</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .settings-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        .settings-section {
            background-color: var(--card-bg);
            border-radius: 5px;
            margin-bottom: 20px;
            padding: 20px;
            box-shadow: 0 1px 3px var(--shadow);
        }

        .settings-section h2 {
            margin-top: 0;
            color: var(--primary);
            border-bottom: 1px solid var(--border);
            padding-bottom: 10px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }

        .form-group input[type="text"],
        .form-group input[type="password"],
        .form-group input[type="number"] {
            width: 100%;
            padding: 8px;
            border: 1px solid var(--border);
            border-radius: 4px;
            box-sizing: border-box;
        }

        .button-container {
            margin-top: 20px;
            display: flex;
            gap: 10px;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
        }

        .btn-primary {
            background-color: var(--primary);
            color: white;
        }

        .btn-secondary {
            background-color: var(--secondary);
            color: white;
        }

        .message-container {
            margin-bottom: 20px;
        }

        .success-message {
            background-color: #dff0d8;
            border: 1px solid #d6e9c6;
            color: #3c763d;
            padding: 15px;
            border-radius: 4px;
        }

        .error-message {
            background-color: #f2dede;
            border: 1px solid #ebccd1;
            color: #a94442;
            padding: 15px;
            border-radius: 4px;
        }

        /* Import section styling */
        .import-preview {
            margin-top: 15px;
            border: 1px solid var(--border);
            border-radius: 4px;
            padding: 15px;
            background-color: var(--form-bg);
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .data-table th, .data-table td {
            border: 1px solid var(--border);
            padding: 8px;
            text-align: left;
        }

        .data-table th {
            background-color: #f5f5f5; /* Light gray background for table headers */
        }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="settings-container">
    <h1>Application Settings</h1>

    <!-- Message Display Section -->
    <c:if test="${not empty successMessage || not empty errorMessage}">
        <div class="message-container">
            <c:if test="${not empty successMessage}">
                <div class="success-message">
                    <p>${successMessage}</p>
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <p>${errorMessage}</p>
                </div>
            </c:if>
        </div>
    </c:if>

    <!-- Database Connection Settings Section -->
    <div class="settings-section">
        <h2>Database Connection Settings</h2>
        <p>Configure the database connection parameters. Changes will take effect after restarting the connection pool.</p>

        <form action="${pageContext.request.contextPath}/SettingsServlet" method="post">
            <input type="hidden" name="action" value="saveDbSettings">

            <div class="form-group">
                <label for="dbHost">Database Host:</label>
                <input type="text" id="dbHost" name="dbHost" value="${dbConfig.host}" required>
            </div>

            <div class="form-group">
                <label for="dbPort">Database Port:</label>
                <input type="number" id="dbPort" name="dbPort" value="${dbConfig.port}" required>
            </div>

            <div class="form-group">
                <label for="dbName">Database Name:</label>
                <input type="text" id="dbName" name="dbName" value="${dbConfig.name}" required>
            </div>

            <div class="form-group">
                <label for="dbUsername">Database Username:</label>
                <input type="text" id="dbUsername" name="dbUsername" value="${dbConfig.username}" required>
            </div>

            <div class="form-group">
                <label for="dbPassword">Database Password:</label>
                <input type="password" id="dbPassword" name="dbPassword" value="${dbConfig.password}" required>
            </div>

            <div class="button-container">
                <button type="submit" class="btn btn-primary">Save Settings</button>
                <button type="button" class="btn btn-secondary" onclick="testConnection()">Test Connection</button>
                <button type="button" class="btn btn-secondary" onclick="restartConnectionPool()">Restart Connection Pool</button>
            </div>
        </form>
    </div>

    <!-- Import FCAR Section -->
    <div class="settings-section">
        <h2>Import FCARs</h2>
        <p>Upload a CSV file to import multiple FCARs at once. The CSV file should have the following columns:</p>
        <ul>
            <li>courseCode - Course code (e.g., CS101)</li>
            <li>instructorId - Professor ID</li>
            <li>semester - Semester (Spring, Summer, Fall)</li>
            <li>year - Year (e.g., 2025)</li>
            <li>outcomeId - Outcome ID (optional)</li>
            <li>indicatorId - Indicator ID (optional)</li>
            <li>workUsed - Work used for assessment (optional)</li>
            <li>assessmentDescription - Description of assessment method (optional)</li>
            <li>level1 - Number of students below expectations (optional)</li>
            <li>level2 - Number of students meeting expectations (optional)</li>
            <li>level3 - Number of students exceeding expectations (optional)</li>
            <li>targetGoal - Target goal percentage (optional)</li>
            <li>summary - Summary of results (optional)</li>
            <li>improvementActions - Proposed actions for improvement (optional)</li>
        </ul>

        <form action="${pageContext.request.contextPath}/ImportFCARServlet" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="fcarFile">Select CSV File:</label>
                <input type="file" id="fcarFile" name="fcarFile" accept=".csv" required>
            </div>
            <div class="form-group">
                <label>
                    <input type="checkbox" id="headerRow" name="headerRow" checked>
                    File contains header row
                </label>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary">Import FCARs</button>
            </div>
        </form>

        <div id="importPreview" class="import-preview" style="display: none;">
            <h3>Import Preview</h3>
            <div id="previewContent"></div>
        </div>
    </div>

    <!-- User Management Section -->
    <div class="settings-section">
        <h2>Manage Users</h2>
        <div class="form-section">
            <h3>Create New Professor Account</h3>
            <form action="${pageContext.request.contextPath}/SettingsServlet" method="post" id="createUserForm">
                <input type="hidden" name="action" value="createUser" />

                <div class="form-group">
                    <label for="firstName">First Name:</label>
                    <input type="text" id="firstName" name="firstName" required />
                </div>

                <div class="form-group">
                    <label for="lastName">Last Name:</label>
                    <input type="text" id="lastName" name="lastName" required />
                </div>

                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" required />
                </div>

                <div class="form-group">
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required />
                </div>

                <div class="form-group">
                    <label for="department">Department:</label>
                    <select id="department" name="deptId" required>
                        <option value="">Select a Department</option>
                        <c:forEach items="${departments}" var="dept">
                            <option value="${dept.id}">${dept.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Assign Courses:</label>
                    <div class="course-checkboxes">
                        <c:forEach items="${courses}" var="course">
                            <div>
                                <input type="checkbox" id="course_${course.courseCode}" name="assignedCourses" value="${course.courseCode}" />
                                <label for="course_${course.courseCode}">${course.courseCode}: ${course.courseName}</label>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <button type="submit" class="btn-submit">Create Professor Account</button>
            </form>
        </div>

        <!-- Existing Professors List -->
        <div class="form-section">
            <h3>Existing Professors</h3>
            <div class="user-list">
                <c:if test="${professors != null && not empty professors}">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Department</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${professors}" var="professor">
                            <tr>
                                <td>${professor.userId}</td>
                                <td>${professor.firstName} ${professor.lastName}</td>
                                <td>${professor.email}</td>
                                <td>${professor.deptName}</td>
                                <td>${professor.active ? "Active" : "Inactive"}</td>
                                <td>
                                    <button type="button" class="btn" onclick="openEditUserModal(${professor.userId}, '${professor.firstName}', '${professor.lastName}', '${professor.email}', ${professor.deptId}, ${professor.roleId})">Edit</button>
                                    <button type="button" class="btn ${professor.active ? 'btn-danger' : 'btn-success'}" onclick="confirmToggleStatus(${professor.userId}, '${professor.firstName} ${professor.lastName}', ${professor.active})">
                                        ${professor.active ? "Deactivate" : "Activate"}
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <c:if test="${professors == null || empty professors}">
                    <p>No professors found.</p>
                </c:if>
            </div>
        </div>
    </div>
</div>

<!-- Edit User Modal -->
<div id="editUserModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeEditUserModal()">&times;</span>
        <h2>Edit Professor</h2>
        <form action="${pageContext.request.contextPath}/SettingsServlet" method="post" id="editUserForm">
            <input type="hidden" name="action" value="editUser" />
            <input type="hidden" name="userId" id="editUserId" />

            <div class="form-group">
                <label for="editFirstName">First Name:</label>
                <input type="text" id="editFirstName" name="firstName" required />
            </div>

            <div class="form-group">
                <label for="editLastName">Last Name:</label>
                <input type="text" id="editLastName" name="lastName" required />
            </div>

            <div class="form-group">
                <label for="editEmail">Email:</label>
                <input type="email" id="editEmail" name="email" required />
            </div>

            <div class="form-group">
                <label for="editPassword">New Password (leave blank to keep current):</label>
                <input type="password" id="editPassword" name="password" />
            </div>

            <div class="form-group">
                <label for="editDepartment">Department:</label>
                <select id="editDepartment" name="deptId" required>
                    <option value="">Select a Department</option>
                    <c:forEach items="${departments}" var="dept">
                        <option value="${dept.id}">${dept.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="editRole">Role:</label>
                <select id="editRole" name="roleId" required>
                    <option value="1">Administrator</option>
                    <option value="2">Professor</option>
                </select>
            </div>

            <div class="form-group">
                <label>Assign Courses:</label>
                <div class="course-checkboxes" id="editCourseCheckboxes">
                    <!-- Will be populated dynamically -->
                </div>
            </div>

            <div class="button-container">
                <button type="button" class="btn-cancel" onclick="closeEditUserModal()">Cancel</button>
                <button type="submit" class="btn-submit">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<!-- Hidden form for toggling user status -->
<form id="toggleStatusForm" method="post" action="${pageContext.request.contextPath}/SettingsServlet" style="display:none;">
    <input type="hidden" name="action" value="toggleUserStatus" />
    <input type="hidden" name="userId" id="toggleUserId" />
</form>

<!-- Confirmation Modal -->
<div id="confirmationModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeConfirmationModal()">&times;</span>
        <h2>Confirm Action</h2>
        <p id="confirmationMessage"></p>
        <div class="button-container">
            <button type="button" class="btn-cancel" onclick="closeConfirmationModal()">Cancel</button>
            <button type="button" class="btn-submit" id="confirmButton">Confirm</button>
        </div>
    </div>
</div>

<script>
    // Function to open the edit user modal
    function openEditUserModal(userId, firstName, lastName, email, deptId, roleId) {
        // Set form values
        document.getElementById('editUserId').value = userId;
        document.getElementById('editFirstName').value = firstName;
        document.getElementById('editLastName').value = lastName;
        document.getElementById('editEmail').value = email;
        document.getElementById('editDepartment').value = deptId;
        document.getElementById('editRole').value = roleId;

        // Populate course checkboxes
        populateEditCourseCheckboxes(userId);

        // Show the modal
        document.getElementById('editUserModal').style.display = 'block';
    }

    // Function to confirm toggling user status
    function confirmToggleStatus(userId, userName, isActive) {
        // Set the user ID in the hidden form
        document.getElementById('toggleUserId').value = userId;

        // Set the confirmation message
        const action = isActive ? "deactivate" : "activate";
        document.getElementById('confirmationMessage').textContent = 
            `Are you sure you want to ${action} the user "${userName}"?`;

        // Set the confirm button action
        const confirmButton = document.getElementById('confirmButton');
        confirmButton.onclick = function() {
            document.getElementById('toggleStatusForm').submit();
        };

        // Show the confirmation modal
        document.getElementById('confirmationModal').style.display = 'block';
    }

    // Function to close the confirmation modal
    function closeConfirmationModal() {
        document.getElementById('confirmationModal').style.display = 'none';
    }

    // Function to close the edit user modal
    function closeEditUserModal() {
        document.getElementById('editUserModal').style.display = 'none';
    }

    // Function to populate course checkboxes in the edit modal
    function populateEditCourseCheckboxes(userId) {
        const container = document.getElementById('editCourseCheckboxes');
        container.innerHTML = '';

        // Make an AJAX call to get assigned courses for this professor
        fetch(`${pageContext.request.contextPath}/SettingsServlet?action=getProfessorCourses&userId=${userId}`)
            .then(response => response.json())
            .then(assignedCourses => {
                // Add all available courses with checkboxes
                addCoursesToModal(container, assignedCourses);
            })
            .catch(error => {
                console.error('Error fetching assigned courses:', error);
                // Fallback: just show all courses unchecked
                addCoursesToModal(container, []);
            });
    }

    // Helper function to add courses to the modal
    function addCoursesToModal(container, assignedCourses) {
        // Get all available courses from the server
        fetch(`${pageContext.request.contextPath}/SettingsServlet?action=getCourseList`)
            .then(response => response.json())
            .then(courses => {
                courses.forEach(course => {
                    const courseDiv = document.createElement('div');
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.id = `edit_course_${course.courseCode}`;
                    checkbox.name = 'assignedCourses';
                    checkbox.value = course.courseCode;
                    checkbox.checked = assignedCourses.includes(course.courseCode);

                    const label = document.createElement('label');
                    label.htmlFor = `edit_course_${course.courseCode}`;
                    label.textContent = `${course.courseCode}: ${course.courseName}`;

                    courseDiv.appendChild(checkbox);
                    courseDiv.appendChild(document.createTextNode(' '));
                    courseDiv.appendChild(label);

                    container.appendChild(courseDiv);
                });
            })
            .catch(error => {
                console.error('Error fetching courses:', error);
            });
    }

    // Function to test database connection
    function testConnection() {
        const host = document.getElementById('dbHost').value;
        const port = document.getElementById('dbPort').value;
        const name = document.getElementById('dbName').value;
        const username = document.getElementById('dbUsername').value;
        const password = document.getElementById('dbPassword').value;

        // Create form data
        const formData = new FormData();
        formData.append('action', 'testConnection');
        formData.append('dbHost', host);
        formData.append('dbPort', port);
        formData.append('dbName', name);
        formData.append('dbUsername', username);
        formData.append('dbPassword', password);

        // Send AJAX request
        fetch('${pageContext.request.contextPath}/SettingsServlet', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Connection successful!');
            } else {
                alert('Connection failed: ' + data.message);
            }
        })
        .catch(error => {
            alert('Error testing connection: ' + error);
        });
    }

    // Function to restart connection pool
    function restartConnectionPool() {
        if (confirm('Are you sure you want to restart the database connection pool? This may temporarily interrupt database operations.')) {
            // Send AJAX request
            fetch('${pageContext.request.contextPath}/SettingsServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'action=restartConnectionPool'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Connection pool restarted successfully!');
                } else {
                    alert('Failed to restart connection pool: ' + data.message);
                }
            })
            .catch(error => {
                alert('Error restarting connection pool: ' + error);
            });
        }
    }

    // File import preview functionality
    document.addEventListener('DOMContentLoaded', function() {
        const fileInput = document.getElementById('fcarFile');
        if (fileInput) {
            fileInput.addEventListener('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    // Show preview section
                    document.getElementById('importPreview').style.display = 'block';

                    // Read file contents
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        const contents = e.target.result;
                        const lines = contents.split('\n');

                        // Display preview of first 5 lines
                        let previewHTML = '<table class="data-table"><thead><tr>';

                        // Parse header row
                        const headerRow = document.getElementById('headerRow').checked;
                        let headers = [];

                        if (headerRow && lines.length > 0) {
                            headers = lines[0].split(',');
                            for (let header of headers) {
                                previewHTML += `<th>${header ? header.trim() : ''}</th>`;
                            }
                            previewHTML += '</tr></thead><tbody>';

                            // Show up to 5 data rows
                            const rowsToShow = Math.min(lines.length - 1, 5);
                            for (let i = 1; i <= rowsToShow; i++) {
                                if (lines[i] && lines[i].trim()) {
                                    previewHTML += '<tr>';
                                    const cells = lines[i].split(',');
                                    for (let cell of cells) {
                                        previewHTML += `<td>${cell ? cell.trim() : ''}</td>`;
                                    }
                                    previewHTML += '</tr>';
                                }
                            }
                        } else {
                            // No header row, just show data
                            const firstRow = lines[0].split(',');
                            for (let i = 0; i < firstRow.length; i++) {
                                previewHTML += `<th>Column ${i+1}</th>`;
                            }
                            previewHTML += '</tr></thead><tbody>';

                            // Show up to 5 data rows
                            const rowsToShow = Math.min(lines.length, 5);
                            for (let i = 0; i < rowsToShow; i++) {
                                if (lines[i] && lines[i].trim()) {
                                    previewHTML += '<tr>';
                                    const cells = lines[i].split(',');
                                    for (let cell of cells) {
                                        previewHTML += `<td>${cell ? cell.trim() : ''}</td>`;
                                    }
                                    previewHTML += '</tr>';
                                }
                            }
                        }

                        previewHTML += '</tbody></table>';

                        if (lines.length > 6) {
                            previewHTML += `<p>...and ${lines.length - 6} more rows</p>`;
                        }

                        document.getElementById('previewContent').innerHTML = previewHTML;
                    };
                    reader.readAsText(file);
                }
            });

            // Update preview when header checkbox changes
            const headerCheckbox = document.getElementById('headerRow');
            if (headerCheckbox) {
                headerCheckbox.addEventListener('change', function() {
                    if (fileInput.files.length > 0) {
                        // Trigger the file input change event to refresh preview
                        const event = new Event('change');
                        fileInput.dispatchEvent(event);
                    }
                });
            }
        }
    });
</script>
</body>
</html>
