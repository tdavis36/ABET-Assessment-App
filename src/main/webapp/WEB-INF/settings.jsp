<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%-- Convert potentially problematic values to explicit booleans --%>
<c:set var="hasSuccessMessage" value="${not empty successMessage}" />
<c:set var="hasErrorMessage" value="${not empty errorMessage}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>ABET App Settings</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />
<div class="dashboard" id="settingsDashboard">
    <div class="header-container">
        <h1>Application Settings</h1>
    </div>

    <!-- Message Display Section -->
    <c:if test="${hasSuccessMessage || hasErrorMessage}">
        <div class="message-container">
            <c:if test="${hasSuccessMessage}">
                <div class="success-message">
                    <p>${successMessage}</p>
                </div>
            </c:if>
            <c:if test="${hasErrorMessage}">
                <div class="error-message">
                    <p>${errorMessage}</p>
                </div>
            </c:if>
        </div>
    </c:if>

    <!-- Database Connection Settings Section -->
    <div class="section">
        <h2>Database Connection Settings</h2>
        <p>Configure the database connection parameters. Changes will take effect after restarting the connection pool.</p>

        <div class="form-section">
            <form action="${pageContext.request.contextPath}/settings" method="post">
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

                <div class="action-buttons">
                    <button type="submit" class="btn btn-submit">Save Settings</button>
                    <button type="button" class="btn" onclick="testConnection()">Test Connection</button>
                    <button type="button" class="btn" onclick="restartConnectionPool()">Restart Connection Pool</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Import FCAR Section -->
    <div class="settings-section">
        <h2>Import FCARs</h2>
        <p>Upload a CSV file to import multiple FCARs at once. This is useful for bulk creating course assessments for instructors.</p>

        <div class="import-help">
            <h4>CSV Format Guidelines</h4>
            <ul>
                <li><strong>Required columns:</strong> <code>courseCode</code>, <code>instructorId</code>, <code>semester</code>, <code>year</code></li>
                <li><strong>Optional columns:</strong> <code>outcomeId</code>, <code>indicatorId</code>, <code>workUsed</code>, <code>assessmentDescription</code>, <code>level1</code>, <code>level2</code>, <code>level3</code>, <code>targetGoal</code>, <code>summary</code>, <code>improvementActions</code></li>
            </ul>
        </div>

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

        <div class="import-help">
            <h4>Example CSV format</h4>
            <pre><code>courseCode,instructorId,semester,year,outcomeId,indicatorId,workUsed,assessmentDescription,level1,level2,level3,targetGoal,summary,improvementActions
CS101,12,Fall,2025,1,2,"Final Exam","Questions 3-5 on the final exam",2,15,8,70,"Most students met expectations","Will provide more practice problems"
CS220,14,Spring,2025,2,4,"Project 2","Database implementation project",3,12,5,75,"Good results but room for improvement","Add more examples of normalization"</code></pre>
        </div>
    </div>

    <%-- Enhanced User Management Section --%>
    <div class="settings-section">
        <h2>Manage Users</h2>
        <div class="form-section">
            <h3>Create New Professor Account</h3>
            <form action="${pageContext.request.contextPath}/settings" method="post" id="createUserForm">
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
                        <div class="course-search">
                            <input type="text" id="courseSearchInputCreate" placeholder="Search courses..." />
                            <div class="course-actions">
                                <button type="button" class="btn" id="selectAllCoursesCreate">Select All</button>
                                <button type="button" class="btn" id="clearAllCoursesCreate">Clear All</button>
                            </div>
                        </div>
                        <div class="course-list">
                            <c:forEach items="${courses}" var="course">
                                <div class="course-item">
                                    <input type="checkbox" id="course_${course.courseCode}" name="assignedCourses" value="${course.courseCode}" />
                                    <label for="course_${course.courseCode}">${course.courseCode}: ${course.courseName}</label>
                                </div>
                            </c:forEach>
                        </div>
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
                            <%-- Convert professor.active to an explicit boolean --%>
                            <c:set var="isProfessorActive" value="false" />
                            <c:if test="${professor.active == true}">
                                <c:set var="isProfessorActive" value="true" />
                            </c:if>

                            <tr>
                                <td>${professor.userId}</td>
                                <td>${professor.firstName} ${professor.lastName}</td>
                                <td>${professor.email}</td>
                                <td>${professor.deptName}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${isProfessorActive}">Active</c:when>
                                        <c:otherwise>Inactive</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <button type="button" class="btn" onclick="openEditUserModal(${professor.userId}, '${professor.firstName}', '${professor.lastName}', '${professor.email}', ${professor.deptId}, ${professor.roleId})">Edit</button>
                                    <button type="button" class="btn
                                    <c:choose>
                                        <c:when test="${isProfessorActive}">btn-danger</c:when>
                                        <c:otherwise>btn-success</c:otherwise>
                                    </c:choose>"
                                            onclick="confirmToggleStatus(${professor.userId}, '${professor.firstName} ${professor.lastName}',
                                            <c:choose>
                                            <c:when test="${isProfessorActive}">true</c:when>
                                            <c:otherwise>false</c:otherwise>
                                            </c:choose>)">
                                        <c:choose>
                                            <c:when test="${isProfessorActive}">Deactivate</c:when>
                                            <c:otherwise>Activate</c:otherwise>
                                        </c:choose>
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

    <!-- Enhanced Edit User Modal -->
    <div id="editUserModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditUserModal()">&times;</span>
            <h2>Edit Professor</h2>
            <form id="editUserForm">
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
</div>

<!-- Hidden form for toggling user status -->
<form id="toggleStatusForm" method="post" action="${pageContext.request.contextPath}/settings" style="display:none;">
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
            <button type="button" class="btn btn-cancel" onclick="closeConfirmationModal()">Cancel</button>
            <button type="button" class="btn btn-submit" id="confirmButton">Confirm</button>
        </div>
    </div>
</div>

<script>
    // Store context path for AJAX calls
    document.addEventListener('DOMContentLoaded', function() {
        // Set global context path from the data attribute
        window.contextPath = document.body.getAttribute('data-context-path') || '';

        // Initialize the edit form submission
        const editUserForm = document.getElementById('editUserForm');
        if (editUserForm) {
            editUserForm.addEventListener('submit', function(event) {
                event.preventDefault();
                submitEditUserForm();
            });
        }

        // Initialize course search on the create user form too
        initializeCreateFormCourseSearch();
    });

    // Function to initialize course search on create form
    function initializeCreateFormCourseSearch() {
        const searchInput = document.getElementById('courseSearchInputCreate');
        if (!searchInput) return;

        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const courseItems = document.querySelectorAll('input[id^="course_"]').forEach(item => {
                const label = document.querySelector(`label[for="${item.id}"]`);
                if (!label) return;

                const text = label.textContent.toLowerCase();
                const container = item.closest('.course-item');
                if (container) {
                    container.style.display = text.includes(searchTerm) ? '' : 'none';
                }
            });
        });

        // Initialize select all/none buttons
        const selectAllBtn = document.getElementById('selectAllCoursesCreate');
        const clearAllBtn = document.getElementById('clearAllCoursesCreate');

        if (selectAllBtn) {
            selectAllBtn.addEventListener('click', function() {
                document.querySelectorAll('input[name="assignedCourses"]').forEach(cb => {
                    if (cb.closest('.course-item').style.display !== 'none') {
                        cb.checked = true;
                    }
                });
            });
        }

        if (clearAllBtn) {
            clearAllBtn.addEventListener('click', function() {
                document.querySelectorAll('input[name="assignedCourses"]').forEach(cb => {
                    if (cb.closest('.course-item').style.display !== 'none') {
                        cb.checked = false;
                    }
                });
            });
        }
    }

    // Fix testConnection function to use context path
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

        // Send AJAX request with correct context path
        fetch(window.contextPath + '/settings', {
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

    // Fix restartConnectionPool function to use context path
    function restartConnectionPool() {
        if (confirm('Are you sure you want to restart the database connection pool? This may temporarily interrupt database operations.')) {
            // Send AJAX request with correct context path
            fetch(window.contextPath + '/settings', {
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

    // Updated submitEditUserForm to use the correct context path
    function submitEditUserForm() {
        const form = document.getElementById('editUserForm');
        const submitButton = form.querySelector('button[type="submit"]');
        const originalText = submitButton.textContent;

        // Show progress
        submitButton.textContent = 'Saving...';
        submitButton.disabled = true;

        // Collect form data
        const formData = new FormData(form);

        // Count selected courses for feedback
        const selectedCourses = document.querySelectorAll('input[name="assignedCourses"]:checked');
        formData.append('courseCount', selectedCourses.length);

        // Get the full URL with context path
        const actionUrl = window.contextPath + '/settings';

        // Send AJAX request
        fetch(actionUrl, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server returned an error: ${response.status}`);
                }
                // Try as JSON first, but fall back to text if needed
                return response.json().catch(() => response.text());
            })
            .then(data => {
                submitButton.textContent = 'Saved!';

                // Handle success response - might be text or JSON
                let message = 'User data saved successfully';
                if (typeof data === 'object' && data.message) {
                    message = data.message;
                }

                // Show success message or redirect
                setTimeout(() => {
                    alert(message);
                    closeEditUserModal();
                    // Reload the page to show updated data
                    window.location.reload();
                }, 1000);
            })
            .catch(error => {
                console.error('Error saving user data:', error);
                submitButton.textContent = 'Error!';
                alert('Failed to save changes. Please try again.');

                setTimeout(() => {
                    submitButton.textContent = originalText;
                    submitButton.disabled = false;
                }, 2000);
            });

        return false; // Prevent form submission
    }

    // Fix populateEditCourseCheckboxes to use the correct context path
    function populateEditCourseCheckboxes(userId) {
        const container = document.getElementById('editCourseCheckboxes');
        container.innerHTML = '<p>Loading courses...</p>';

        // Ensure userId is a value, not an element
        if(typeof userId === 'object' && userId.value) {
            userId = userId.value;
        }

        // Update the URL path to match the servlet mapping with correct context path
        fetch(`${window.contextPath}/settings?action=getProfessorCourses&userId=${userId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(assignedCourses => {
                // Add all available courses with checkboxes
                return addCoursesToModal(container, assignedCourses);
            })
            .catch(error => {
                console.error('Error fetching assigned courses:', error);
                // Fallback: just show all courses unchecked
                container.innerHTML = '<p class="error-message">Error loading assigned courses. Please try again.</p>';
                return addCoursesToModal(container, []);
            });
    }

    // Updated addCoursesToModal to use correct context path
    function addCoursesToModal(container, assignedCourses) {
        // Convert assignedCourses to a Set for faster lookups
        const assignedCoursesSet = new Set(assignedCourses);

        // Get all available courses from the server with correct context path
        return fetch(`${window.contextPath}/settings?action=getCourseList`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(courses => {
                if (!courses || courses.length === 0) {
                    container.innerHTML = '<p>No courses available in the system.</p>';
                    return;
                }

                // Add search box for courses
                const searchBox = document.createElement('div');
                searchBox.className = 'course-search';
                searchBox.innerHTML = `
                <input type="text" id="courseSearchInput" placeholder="Search courses..." />
                <div class="course-actions">
                    <button type="button" class="btn" id="selectAllCourses">Select All</button>
                    <button type="button" class="btn" id="clearAllCourses">Clear All</button>
                </div>
            `;
                container.innerHTML = '';
                container.appendChild(searchBox);

                // Create course list container with scrollable area
                const courseList = document.createElement('div');
                courseList.className = 'course-list';
                container.appendChild(courseList);

                // Sort courses by code for better organization
                courses.sort((a, b) => a.courseCode.localeCompare(b.courseCode));

                // Add each course as a checkbox
                courses.forEach(course => {
                    const courseDiv = document.createElement('div');
                    courseDiv.className = 'course-item';

                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.id = `edit_course_${course.courseCode}`;
                    checkbox.name = 'assignedCourses';
                    checkbox.value = course.courseCode;

                    // Check if this course is assigned to the professor
                    if (course.assigned === true || assignedCoursesSet.has(course.courseCode)) {
                        checkbox.checked = true;
                    }

                    const label = document.createElement('label');
                    label.htmlFor = `edit_course_${course.courseCode}`;
                    label.textContent = `${course.courseCode}: ${course.courseName}`;

                    courseDiv.appendChild(checkbox);
                    courseDiv.appendChild(document.createTextNode(' '));
                    courseDiv.appendChild(label);

                    courseList.appendChild(courseDiv);
                });

                // Initialize search functionality
                initializeCourseSearch();

                // Initialize select/clear all buttons
                initializeCourseButtons();
            })
            .catch(error => {
                console.error('Error fetching courses:', error);
                container.innerHTML = '<p class="error-message">Error loading course list. Please try again.</p>';
            });
    }
</script>
<script src="${pageContext.request.contextPath}/js/course-assignment.js"></script>
</body>
</html>