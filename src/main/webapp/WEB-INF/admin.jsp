<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.ABETAppTeam.model.FCAR" %>
<%@ page import="com.ABETAppTeam.model.Course" %>
<%@ page import="com.ABETAppTeam.model.User" %>
<%@ page import="com.ABETAppTeam.model.Professor" %>
<%@ page import="com.ABETAppTeam.model.Department" %>
<%@ page import="com.ABETAppTeam.model.Outcome" %>
<%@ page import="com.ABETAppTeam.model.Indicator" %>
<%@ page import="java.util.HashMap" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />

<div class="dashboard" id="adminDashboard">
    <div class="header-container">
        <h1>Admin Dashboard</h1>
    </div>

    <!-- Status Key -->
    <div class="status-key">
        <div><span class="status draft"></span> Draft</div>
        <div><span class="status submitted"></span> Submitted</div>
        <div><span class="status approved"></span> Approved</div>
        <div><span class="status rejected"></span> Rejected</div>
    </div>

    <!-- Statistics Section -->
    <div class="section">
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
        <form action="${pageContext.request.contextPath}/view" method="get">
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
                        <strong>Instructor:</strong> <% 
                            // Look up the professor's last name using their ID
                            int professorId = fcar.getProfessorId();
                            String professorLastName = "";
                            List<User> professors = (List<User>) request.getAttribute("professors");
                            if (professors != null) {
                                for (User prof : professors) {
                                    if (prof.getUserId() == professorId) {
                                        professorLastName = prof.getLastName();
                                        break;
                                    }
                                }
                            }
                            // Display the professor's last name if found, otherwise display the ID
                            out.print(professorLastName.isEmpty() ? professorId : professorLastName);
                        %> -
                        <strong>Semester:</strong> <%= fcar.getSemester() %> <%= fcar.getYear() %> -
                        <strong>Status:</strong> <%= fcar.getStatus() %>
                    </div>
                    <div class="fcar-actions">
                        <form method="get" action="${pageContext.request.contextPath}/view" style="display:inline;">
                            <input type="hidden" name="fcarId" value="<%= fcar.getFcarId() %>"/>
                            <button type="submit" class="btn">View</button>
                        </form>
                        <form method="get" action="${pageContext.request.contextPath}/view" style="display:inline;">
                            <input type="hidden" name="action" value="editFCAR"/>
                            <input type="hidden" name="fcarId" value="<%= fcar.getFcarId() %>"/>
                            <button type="submit" class="btn">Edit</button>
                        </form>
                        <% if ("submitted".equals(status)) { %>
                        <form method="post" action="${pageContext.request.contextPath}/admin" style="display:inline;">
                            <input type="hidden" name="action" value="approveFCAR"/>
                            <input type="hidden" name="fcarId" value="<%= fcar.getFcarId() %>"/>
                            <button type="submit" class="btn" style="background-color: var(--success);">Approve</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/admin" style="display:inline;">
                            <input type="hidden" name="action" value="rejectFCAR"/>
                            <input type="hidden" name="fcarId" value="<%= fcar.getFcarId() %>"/>
                            <button type="submit" class="btn" style="background-color: var(--danger);">Reject</button>
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
            <form action="${pageContext.request.contextPath}/admin" method="post" id="createFcarForm">
                <input type="hidden" name="action" value="createFCAR" />
                <input type="hidden" name="selectedOutcomes" id="selectedOutcomesInput" value="" />

                <div class="form-group">
                    <label for="professorId">Select Instructor:</label>
                    <select id="professorId" name="professorId" required>
                        <option value="">Select an Instructor</option>
                        <c:forEach items="${professors}" var="instructor">
                            <option value="${instructor.userId}">${instructor.firstName} ${instructor.lastName}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="courseId">Course:</label>
                    <select id="courseId" name="courseId" required disabled>
                        <option value="">Select an Instructor First</option>
                    </select>
                    <small class="form-text text-muted">Only courses assigned to the selected instructor will be shown</small>
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
                        <div id="dynamicCourseContainer">
                            <p>Please select an instructor and course to see the associated outcomes and indicators.</p>
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

    <!-- User management has been moved to the Settings page -->
</div>

<!-- User management modals have been moved to the Settings page -->

<script>
    // Get outcomeData from the controller
    <% 
    String outcomeData = (String) request.getAttribute("outcomeData");
    if (outcomeData != null && !outcomeData.isEmpty()) {
        out.println(outcomeData);
    } 

    // Get outcomes from request attributes for dynamic access
    List<Outcome> outcomes = (List<Outcome>) request.getAttribute("outcomes");
    Map<Integer, String> dynamicOutcomeDescriptions = new HashMap<>();
    if (outcomes != null) {
        for (Outcome outcome : outcomes) {
            dynamicOutcomeDescriptions.put(outcome.getId(), outcome.getDescription());
        }
    }

    // Convert dynamicOutcomeDescriptions to JSON for JavaScript
    StringBuilder dynamicOutcomeDescriptionsJson = new StringBuilder();
    dynamicOutcomeDescriptionsJson.append("const dynamicOutcomeDescriptions = {");
    for (Map.Entry<Integer, String> entry : dynamicOutcomeDescriptions.entrySet()) {
        dynamicOutcomeDescriptionsJson.append(entry.getKey())
                .append(": \"")
                .append(entry.getValue().replace("\"", "\\\""))
                .append("\", ");
    }
    if (!dynamicOutcomeDescriptions.isEmpty()) {
        dynamicOutcomeDescriptionsJson.delete(dynamicOutcomeDescriptionsJson.length() - 2, dynamicOutcomeDescriptionsJson.length());
    }
    dynamicOutcomeDescriptionsJson.append("};");

    // Output the dynamic outcome descriptions
    out.println(dynamicOutcomeDescriptionsJson.toString());
    %>

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

            // Create an outcome checkbox container
            const outcomeCheckboxContainer = document.createElement('div');
            outcomeCheckboxContainer.className = 'outcome-checkbox-container';

            // Create an outcome checkbox
            const outcomeCheckbox = document.createElement('input');
            outcomeCheckbox.type = 'checkbox';
            outcomeCheckbox.id = `${selectedCourse}_outcome_${outcomeId}`;
            outcomeCheckbox.name = `selectedOutcome_${outcomeId}`;
            outcomeCheckbox.value = outcomeId;
            outcomeCheckbox.checked = true;
            outcomeCheckbox.className = 'outcome-checkbox';

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
            // Use dynamic outcome descriptions if available, fall back to static if not
            outcomeLabel.textContent = `Outcome ${outcomeId}: ${dynamicOutcomeDescriptions[outcomeId] || outcomeDescriptions[outcomeId]}`;

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

    // User management functions have been moved to the Settings page

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

        // User management modal handling has been moved to the Settings page
        (function() {
            // Wait for DOM to be fully loaded
            document.addEventListener('DOMContentLoaded', function() {
                console.log("Course outcomes fix script loaded");

                // Get key elements
                const courseSelect = document.getElementById('courseId');
                const dynamicCourseContainer = document.getElementById('dynamicCourseContainer');
                const outcomesContainer = document.getElementById('outcomesContainer');

                if (!courseSelect || !dynamicCourseContainer) {
                    console.warn("Required elements not found for course outcomes fix");
                    return;
                }

                // Override the existing updateOutcomes function with a fixed version
                window.updateOutcomes = function() {
                    console.log("Enhanced updateOutcomes called");

                    // Clear the current outcome display
                    dynamicCourseContainer.innerHTML = '';

                    // Get the selected course
                    const selectedCourse = courseSelect.value;
                    console.log("Selected course:", selectedCourse);

                    // If no course is selected, show a placeholder message
                    if (!selectedCourse) {
                        dynamicCourseContainer.innerHTML = '<p>Please select a course to see its associated outcomes.</p>';
                        return;
                    }

                    // Check if we have outcome data for this course
                    if (!window.courseOutcomes || !window.courseOutcomes[selectedCourse]) {
                        dynamicCourseContainer.innerHTML = '<p>No outcome data found for this course. Please contact an administrator.</p>';
                        console.warn(`No course outcomes found for ${selectedCourse}`);
                        return;
                    }

                    // Get the outcomes for this course
                    const courseSpecificOutcomes = window.courseOutcomes[selectedCourse];
                    console.log(`Outcomes for ${selectedCourse}:`, courseSpecificOutcomes);

                    if (!courseSpecificOutcomes || courseSpecificOutcomes.length === 0) {
                        dynamicCourseContainer.innerHTML = '<p>No outcomes are assigned to this course.</p>';
                        return;
                    }

                    // Create a dynamic outcomes section
                    const dynamicOutcomesDiv = document.createElement('div');
                    dynamicOutcomesDiv.id = 'dynamicCourseOutcomes';

                    // Add header
                    const header = document.createElement('h3');
                    header.textContent = `Outcomes for ${selectedCourse}`;
                    dynamicOutcomesDiv.appendChild(header);

                    // Add description
                    const description = document.createElement('p');
                    description.textContent = 'The following outcomes are automatically assigned based on the course. You can select which indicators to include for each outcome.';
                    dynamicOutcomesDiv.appendChild(description);

                    // Array to store selected outcomes for the hidden input
                    const selectedOutcomes = [];

                    // For each outcome assigned to this course
                    courseSpecificOutcomes.forEach(outcomeId => {
                        selectedOutcomes.push(outcomeId);

                        // Create outcome container
                        const outcomeContainer = document.createElement('div');
                        outcomeContainer.className = 'outcome-container';

                        // Create checkbox container
                        const checkboxContainer = document.createElement('div');
                        checkboxContainer.className = 'outcome-checkbox-container';

                        // Create checkbox
                        const checkbox = document.createElement('input');
                        checkbox.type = 'checkbox';
                        checkbox.id = `${selectedCourse}_outcome_${outcomeId}`;
                        checkbox.name = `selectedOutcome_${outcomeId}`;
                        checkbox.value = outcomeId;
                        checkbox.className = 'outcome-checkbox';
                        checkbox.checked = true; // Default to checked

                        // Event listener for checkbox
                        checkbox.addEventListener('change', function() {
                            // Toggle indicators visibility
                            const indicatorsDiv = document.getElementById(`${selectedCourse}_indicators_${outcomeId}`);
                            if (indicatorsDiv) {
                                indicatorsDiv.style.display = this.checked ? 'block' : 'none';
                            }

                            // Update selected outcomes
                            updateSelectedOutcomesInput(selectedCourse);
                        });

                        // Create label with outcome description
                        const label = document.createElement('label');
                        label.htmlFor = checkbox.id;
                        label.className = 'outcome-label';

                        // Get outcome description from available sources
                        let outcomeDescription = "Unknown Outcome";
                        if (window.dynamicOutcomeDescriptions && window.dynamicOutcomeDescriptions[outcomeId]) {
                            outcomeDescription = window.dynamicOutcomeDescriptions[outcomeId];
                        } else if (window.outcomeDescriptions && window.outcomeDescriptions[outcomeId]) {
                            outcomeDescription = window.outcomeDescriptions[outcomeId];
                        }

                        label.textContent = `Outcome ${outcomeId}: ${outcomeDescription}`;

                        // Add checkbox and label to container
                        checkboxContainer.appendChild(checkbox);
                        checkboxContainer.appendChild(label);
                        outcomeContainer.appendChild(checkboxContainer);

                        // Add indicators label
                        const indicatorsLabel = document.createElement('div');
                        indicatorsLabel.className = 'indicators-label';
                        indicatorsLabel.textContent = 'Select Indicators:';
                        outcomeContainer.appendChild(indicatorsLabel);

                        // Create indicators container
                        const indicatorsDiv = document.createElement('div');
                        indicatorsDiv.id = `${selectedCourse}_indicators_${outcomeId}`;
                        indicatorsDiv.className = 'indicators-container';
                        indicatorsDiv.style.display = 'block'; // Initially visible

                        // Add indicators for this outcome
                        if (window.indicators && window.indicators[outcomeId]) {
                            window.indicators[outcomeId].forEach((indicator, index) => {
                                // Parse indicator number and description
                                const indicatorParts = indicator.split(' ');
                                const indicatorNumber = indicatorParts[0];
                                const indicatorDescription = indicatorParts.slice(1).join(' ');

                                // Create indicator container
                                const indicatorContainer = document.createElement('div');
                                indicatorContainer.className = 'indicator-container';

                                // Create checkbox
                                const indicatorCheckbox = document.createElement('input');
                                indicatorCheckbox.type = 'checkbox';
                                indicatorCheckbox.id = `${selectedCourse}_indicator_${indicatorNumber.replace('.', '_')}`;
                                indicatorCheckbox.name = `indicator_${indicatorNumber.replace('.', '_')}`;
                                indicatorCheckbox.value = indicatorNumber;
                                indicatorCheckbox.checked = true; // Default to checked

                                // Create label
                                const indicatorLabel = document.createElement('label');
                                indicatorLabel.htmlFor = indicatorCheckbox.id;
                                indicatorLabel.textContent = indicator;

                                // Add checkbox and label to container
                                indicatorContainer.appendChild(indicatorCheckbox);
                                indicatorContainer.appendChild(document.createTextNode(' '));
                                indicatorContainer.appendChild(indicatorLabel);

                                // Add to indicators container
                                indicatorsDiv.appendChild(indicatorContainer);
                            });
                        } else {
                            // No indicators available
                            const noIndicatorsMsg = document.createElement('p');
                            noIndicatorsMsg.textContent = 'No indicators available for this outcome.';
                            indicatorsDiv.appendChild(noIndicatorsMsg);
                        }

                        // Add indicators container to outcome container
                        outcomeContainer.appendChild(indicatorsDiv);

                        // Add outcome container to dynamic outcomes div
                        dynamicOutcomesDiv.appendChild(outcomeContainer);
                    });

                    // Add the dynamic outcomes to the container
                    dynamicCourseContainer.appendChild(dynamicOutcomesDiv);

                    // Update the hidden input with selected outcomes
                    document.getElementById('selectedOutcomesInput').value = selectedOutcomes.join(',');
                };

                // Helper function to update selected outcomes input
                function updateSelectedOutcomesInput(courseCode) {
                    const selectedOutcomes = [];
                    const checkboxes = document.querySelectorAll(`input[type="checkbox"][id^="${courseCode}_outcome_"]:checked`);

                    checkboxes.forEach(checkbox => {
                        selectedOutcomes.push(checkbox.value);
                    });

                    document.getElementById('selectedOutcomesInput').value = selectedOutcomes.join(',');
                }

                // Re-attach the course select change event handler
                courseSelect.addEventListener('change', updateOutcomes);

                console.log("Course outcomes fix applied");
            });
        })();
    })
</script>
<script src="${pageContext.request.contextPath}/js/admin-utils.js"></script>