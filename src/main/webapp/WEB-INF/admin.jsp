<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.ABETAppTeam.FCAR" %>
<%@ page import="com.ABETAppTeam.Course" %>
<%@ page import="com.ABETAppTeam.User" %>
<%@ page import="com.ABETAppTeam.Professor" %>
<%@ page import="java.util.HashMap" %>

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
    <div class="header-container">
        <h1>Admin Dashboard</h1>
        <a href="${pageContext.request.contextPath}/?action=logout" class="btn">Logout</a>
    </div>
`
    <!-- Status Key -->
    <div class="status-key">
        <div><span class="status draft"></span> Draft</div>
        <div><span class="status submitted"></span> Submitted</div>
        <div><span class="status approved"></span> Approved</div>
        <div><span class="status rejected"></span> Rejected</div>
    </div>

    <!-- Statistics Section -->
    <div class="section">
        <h2>Dashboard Statistics</h2>
        <div class="stat-container">
            <%
                List<FCAR> allFCARs = (List<FCAR>) request.getAttribute("allFCARs");
                Map<String, Integer> fcarStatusCounts = (Map<String, Integer>) request.getAttribute("fcarStatusCounts");
                if (fcarStatusCounts == null) {
                    fcarStatusCounts = new HashMap<>();
                }

                int totalFCARs = allFCARs != null ? allFCARs.size() : 0;
                int draftCount = fcarStatusCounts.getOrDefault("Draft", 0);
                int submittedCount = fcarStatusCounts.getOrDefault("Submitted", 0);
                int approvedCount = fcarStatusCounts.getOrDefault("Approved", 0);
                int rejectedCount = fcarStatusCounts.getOrDefault("Rejected", 0);
            %>
            <div class="stat-box">
                <h3>Total FCARs</h3>
                <div class="stat-number"><%= totalFCARs %></div>
            </div>
            <div class="stat-box">
                <h3>Draft</h3>
                <div class="stat-number"><%= draftCount %></div>
            </div>
            <div class="stat-box">
                <h3>Submitted</h3>
                <div class="stat-number"><%= submittedCount %></div>
            </div>
            <div class="stat-box">
                <h3>Approved</h3>
                <div class="stat-number"><%= approvedCount %></div>
            </div>
            <div class="stat-box">
                <h3>Rejected</h3>
                <div class="stat-number"><%= rejectedCount %></div>
            </div>
        </div>
    </div>

    <!-- Action Buttons -->
    <div class="action-buttons">
        <form action="${pageContext.request.contextPath}/ViewFCARServlet" method="get">
            <input type="hidden" name="action" value="viewAll"/>
            <button type="submit" class="btn">View All FCARs</button>
        </form>
    </div>

    <!-- View FCARs Section -->
    <div class="section">
        <h2>Manage FCARs</h2>

        <!-- Dynamically display all FCARs -->
        <div id="fcarList">
            <h3>Existing FCARs</h3>
            <%
                if (allFCARs != null && !allFCARs.isEmpty()) {
            %>
            <div class="fcar-box">
                <%
                    for (FCAR fcar : allFCARs) {
                        String status = fcar.getStatus().toLowerCase();
                %>
                <div class="fcar-item">
                    <div>
                        <span class="status <%= status %>"></span>
                        <strong>Course:</strong> <%= fcar.getCourseId() %> -
                        <strong>Professor:</strong> <%= fcar.getProfessorId() %> -
                        <strong>Semester:</strong> <%= fcar.getSemester() %> <%= fcar.getYear() %> -
                        <strong>Status:</strong> <%= fcar.getStatus() %>
                    </div>
                    <div class="fcar-actions">
                        <form method="get" action="${pageContext.request.contextPath}/ViewFCARServlet" style="display:inline;">
                            <input type="hidden" name="fcarId" value="<%= fcar.getFcarId() %>"/>
                            <button type="submit" class="btn">View</button>
                        </form>
                        <form method="get" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                            <input type="hidden" name="action" value="editFCAR"/>
                            <input type="hidden" name="fcarId" value="<%= fcar.getFcarId() %>"/>
                            <button type="submit" class="btn">Edit</button>
                        </form>
                        <% if ("submitted".equals(status)) { %>
                        <form method="post" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                            <input type="hidden" name="action" value="approveFCAR"/>
                            <input type="hidden" name="fcarId" value="<%= fcar.getFcarId() %>"/>
                            <button type="submit" class="btn" style="background-color: #28a745;">Approve</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                            <input type="hidden" name="action" value="rejectFCAR"/>
                            <input type="hidden" name="fcarId" value="<%= fcar.getFcarId() %>"/>
                            <button type="submit" class="btn" style="background-color: #dc3545;">Reject</button>
                        </form>
                        <% } %>
                    </div>
                </div>
                <% } %>
            </div>
            <% } else { %>
            <p>No FCARs available.</p>
            <% } %>
        </div>
    </div>

    <!-- 'Create New FCAR' section -->
    <div class="section">
        <h2>Create New FCAR</h2>
        <div class="form-section">
            <form action="${pageContext.request.contextPath}/AdminServlet" method="post" id="createFcarForm">
                <input type="hidden" name="action" value="createFCAR" />
                <input type="hidden" name="selectedOutcomes" id="selectedOutcomesInput" value="" />

                <div class="form-group">
                    <label for="courseId">Course:</label>
                    <select id="courseId" name="courseId" required>
                        <option value="">Select a Course</option>
                        <%
                            List<Course> courses = (List<Course>) request.getAttribute("courses");
                            if (courses != null && !courses.isEmpty()) {
                                for (Course course : courses) {
                        %>
                        <option value="<%= course.getCourseId() %>"><%= course.getCourseCode() %>: <%= course.getCourseName() %></option>
                        <%
                            }
                        } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="professorId">Assign to Professor:</label>
                    <select id="professorId" name="professorId" required>
                        <option value="">Select a Professor</option>
                        <%
                            List<User> users = (List<User>) request.getAttribute("users");
                            if (users != null && !users.isEmpty()) {
                                for (User user : users) {
                                    if (user instanceof Professor) {
                        %>
                        <option value="<%= user.getUserId() %>"><%= user.getFirstName() %> <%= user.getLastName() %></option>
                        <%
                                }
                            }
                        } else {
                        %>
                        <option value="Smith">Dr. Smith</option>
                        <option value="Johnson">Dr. Johnson</option>
                        <% } %>
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

                <!-- Outcomes and Indicators Section -->
                <div class="form-group">
                    <label>Student Learning Outcomes and Indicators:</label>
                    <div id="outcomesContainer" class="outcomes-container">
                        <div id="courseOutcomesPlaceholder">
                            <p>Please select a course to see the associated outcomes and indicators.</p>
                        </div>

                        <!-- Dynamic Course Outcomes Container -->
                        <div id="dynamicCourseOutcomes" style="display: none;">
                            <!-- This will be populated dynamically by JavaScript -->
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="targetGoal">Target Goal (%):</label>
                    <input type="number" id="targetGoal" name="targetGoal" min="0" max="100" value="70" required />
                </div>

                <button type="submit" class="btn-submit">Create FCAR</button>
            </form>
        </div>
    </div>
</div>

<script>
    <% 
    // Check if outcomeData is available from the controller
    String outcomeData = (String) request.getAttribute("outcomeData");
    if (outcomeData != null && !outcomeData.isEmpty()) {
        // Output the data from the controller
        out.println(outcomeData);
    } else {
        // Fallback to an empty data structure if controller data is not available
    %>
    // Empty data structures for fallback
    const outcomeDescriptions = {};
    const outcomeNumbers = {};
    const indicators = {};
    const courseOutcomes = {};
    <% } %>

    // Function to show the outcomes for the selected course
    function updateOutcomes() {
        const dynamicOutcomesDiv = document.getElementById('dynamicCourseOutcomes');
        const placeholderDiv = document.getElementById('courseOutcomesPlaceholder');

        // Get the selected course
        const selectedCourse = document.getElementById('courseId').value;

        // If no course is selected, show placeholder and hide outcomes
        if (!selectedCourse || !courseOutcomes[selectedCourse]) {
            placeholderDiv.style.display = 'block';
            dynamicOutcomesDiv.style.display = 'none';
            return;
        }

        // Hide placeholder and show dynamic outcomes
        placeholderDiv.style.display = 'none';
        dynamicOutcomesDiv.style.display = 'block';

        // Clear previous content
        dynamicOutcomesDiv.innerHTML = '';

        // Add header
        const header = document.createElement('h3');
        header.textContent = `Outcomes for ${selectedCourse}`;
        dynamicOutcomesDiv.appendChild(header);

        // Add description
        const description = document.createElement('p');
        description.textContent = 'The following outcomes are automatically assigned based on the course. You can select which indicators to include for each outcome.';
        dynamicOutcomesDiv.appendChild(description);

        // Generate outcome sections
        courseOutcomes[selectedCourse].forEach(outcomeId => {
            // Create outcome container
            const outcomeContainer = document.createElement('div');
            outcomeContainer.className = 'outcome-container';

            // Create outcome checkbox container
            const outcomeCheckboxContainer = document.createElement('div');
            outcomeCheckboxContainer.style.display = 'flex';
            outcomeCheckboxContainer.style.alignItems = 'center';
            outcomeCheckboxContainer.style.marginBottom = '10px';

            // Create outcome checkbox
            const outcomeCheckbox = document.createElement('input');
            outcomeCheckbox.type = 'checkbox';
            outcomeCheckbox.id = `${selectedCourse}_outcome_${outcomeId}`;
            outcomeCheckbox.name = `selectedOutcome_${outcomeId}`;
            outcomeCheckbox.value = outcomeId;
            outcomeCheckbox.checked = true;
            outcomeCheckbox.style.marginRight = '10px';

            // Add event listener to toggle indicators when outcome is checked/unchecked
            outcomeCheckbox.addEventListener('change', function() {
                const indicatorsDiv = document.getElementById(`${selectedCourse}_indicators_${outcomeId}`);
                if (indicatorsDiv) {
                    indicatorsDiv.style.display = this.checked ? 'block' : 'none';

                    // Enable/disable indicator checkboxes
                    const indicatorCheckboxes = indicatorsDiv.querySelectorAll('input[type="checkbox"]');
                    indicatorCheckboxes.forEach(checkbox => {
                        checkbox.disabled = !this.checked;
                    });
                }
            });

            // Create outcome label
            const outcomeLabel = document.createElement('label');
            outcomeLabel.htmlFor = outcomeCheckbox.id;
            outcomeLabel.className = 'outcome-label';
            outcomeLabel.textContent = `Outcome ${outcomeId}: ${outcomeDescriptions[outcomeId]}`;

            // Add checkbox and label to container
            outcomeCheckboxContainer.appendChild(outcomeCheckbox);
            outcomeCheckboxContainer.appendChild(outcomeLabel);

            // Add outcome checkbox container to outcome container
            outcomeContainer.appendChild(outcomeCheckboxContainer);

            // Add indicators label
            const indicatorsLabel = document.createElement('div');
            indicatorsLabel.className = 'indicators-label';
            indicatorsLabel.textContent = 'Select Indicators:';
            outcomeContainer.appendChild(indicatorsLabel);

            // Create indicators container with ID for toggling
            const indicatorsDiv = document.createElement('div');
            indicatorsDiv.id = `${selectedCourse}_indicators_${outcomeId}`;
            indicatorsDiv.style.display = 'block'; // Initially visible since outcome is checked by default

            // Add indicators
            if (indicators[outcomeId]) {
                indicators[outcomeId].forEach((indicator, index) => {
                    // Extract indicator number (e.g., "1.1" from "1.1 Student can...")
                    const indicatorNumber = indicator.split(' ')[0];

                    // Create indicator container
                    const indicatorContainer = document.createElement('div');
                    indicatorContainer.className = 'indicator-container';

                    // Create checkbox
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.id = `${selectedCourse}_indicator_${indicatorNumber.replace('.', '_')}`;
                    checkbox.name = `indicator_${indicatorNumber.replace('.', '_')}`;
                    checkbox.value = indicatorNumber;
                    checkbox.checked = true;

                    // Create label
                    const label = document.createElement('label');
                    label.htmlFor = checkbox.id;
                    label.textContent = indicator;

                    // Add checkbox and label to container
                    indicatorContainer.appendChild(checkbox);
                    indicatorContainer.appendChild(document.createTextNode(' '));
                    indicatorContainer.appendChild(label);

                    // Add indicator to indicators container
                    indicatorsDiv.appendChild(indicatorContainer);
                });
            }

            // Add indicators container to outcome container
            outcomeContainer.appendChild(indicatorsDiv);

            // Add outcome container to dynamic outcomes div
            dynamicOutcomesDiv.appendChild(outcomeContainer);
        });
    }

    // Function to collect selected outcomes and update the hidden input
    function updateSelectedOutcomesInput() {
        const selectedCourse = document.getElementById('courseId').value;
        if (!selectedCourse || !courseOutcomes[selectedCourse]) {
            return;
        }

        const selectedOutcomes = [];
        courseOutcomes[selectedCourse].forEach(outcomeId => {
            const checkbox = document.getElementById(`${selectedCourse}_outcome_${outcomeId}`);
            if (checkbox && checkbox.checked) {
                selectedOutcomes.push(outcomeId);
            }
        });

        document.getElementById('selectedOutcomesInput').value = selectedOutcomes.join(',');
    }

    // Add event listeners
    document.addEventListener('DOMContentLoaded', function() {
        // Course select dropdown
        const courseSelect = document.getElementById('courseId');
        if (courseSelect) {
            courseSelect.addEventListener('change', updateOutcomes);
        }

        // Form submission
        const form = document.getElementById('createFcarForm');
        if (form) {
            form.addEventListener('submit', function(event) {
                // Update the selected outcomes input before submitting
                updateSelectedOutcomesInput();
            });
        }
    });
</script>
</body>
</html>