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
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h1>Welcome, Dr. [Admin Name]</h1>
            <a href="${pageContext.request.contextPath}/" class="btn" style="margin-left: 20px;">Logout</a>
        </div>

        <!-- Action Buttons -->
        <div style="display: flex; gap: 20px; margin-bottom: 20px;">
            <form action="${pageContext.request.contextPath}/ViewFCARServlet" method="get">
                <input type="hidden" name="action" value="viewAll"/>
                <button type="submit" class="btn">View FCARs</button>
            </form>
            <!-- New View Reports button -->
            <form action="${pageContext.request.contextPath}/ReportsServlet" method="get">
                <input type="hidden" name="action" value="viewReports"/>
                <button type="submit" class="btn">View Reports</button>
            </form>
        </div>

        <!-- View FCARs Section -->
        <div class="section">
            <h2>FCARs</h2>

            <!-- Dynamically display all created FCARs -->
            <div id="fcarList">
                <h3>Existing FCARs</h3>
                <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
                <c:choose>
                    <c:when test="${not empty allFCARs}">
                        <ul>
                            <c:forEach var="fcar" items="${allFCARs}">
                                <li>
                                    <strong>Professor:</strong> ${fcar.professorId} -
                                    <strong>Course:</strong> ${fcar.courseId} -
                                    <strong>Status:</strong> ${fcar.status}
                                    <form method="get" action="${pageContext.request.contextPath}/AdminServlet" style="display:inline;">
                                        <input type="hidden" name="action" value="editFCAR"/>
                                        <input type="hidden" name="fcarId" value="${fcar.fcarId}"/>
                                        <button type="submit" class="btn">Edit</button>
                                    </form>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p>No FCARs available.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Create New FCAR Section -->
        <div class="section">
            <h2>Create New FCAR</h2>
            <div class="form-section">
                <form action="${pageContext.request.contextPath}/AdminServlet" method="post" id="createFcarForm">
                    <input type="hidden" name="action" value="createFCAR" />
                    <input type="hidden" name="selectedOutcomes" id="selectedOutcomesInput" value="" />
                    
                    <div class="form-group">
                        <label for="courseId">Course ID:</label>
                        <select id="courseId" name="courseId" required>
                            <option value="">Select a Course</option>
                            <option value="CS101">CS101 - Fundamentals of Computer Science I</option>
                            <option value="CS201">CS201 - Fundamentals of Computer Science II</option>
                            <option value="CS320">CS320 - Software Engineering Design</option>
                            <option value="CS330">CS330 - Network/App Protocols</option>
                            <option value="CS335">CS335 - Cybersecurity Analysis & Application</option>
                            <option value="CS340">CS340 - Programming Language Design</option>
                            <option value="CS350">CS350 - Data Structures</option>
                            <option value="CS360">CS360 - Database Systems</option>
                            <option value="CS420">CS420 - Operating Systems</option>
                            <option value="CS456">CS456 - Social/Prof Issues</option>
                            <option value="CS481">CS481 - Senior Design Project</option>
                            <option value="CS400">CS400 - Senior Design Project</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="professorId">Assign to Professor:</label>
                        <select id="professorId" name="professorId" required>
                            <option value="">Select a Professor</option>
                            <c:forEach var="professor" items="${professors}">
                                <option value="${professor.id}">${professor.name}</option>
                            </c:forEach>
                            <!-- Real professor names instead of placeholders -->
                            <option value="Smith">Dr. Smith</option>
                            <option value="Johnson">Dr. Johnson</option>
                            <option value="Williams">Dr. Williams</option>
                            <option value="Davis">Dr. Davis</option>
                            <option value="Miller">Dr. Miller</option>
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
                        <div id="outcomesContainer" style="margin-top: 10px; border: 1px solid #ddd; padding: 15px; border-radius: 5px; max-height: 400px; overflow-y: auto;">
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
    // Define the outcomes
    const outcomeDescriptions = {
        1: "Analyze a complex computing problem and to apply principles of computing and other relevant disciplines to identify solutions.",
        2: "Design, implement, and evaluate a computing-based solution to meet a given set of computing requirements in the context of the program's discipline.",
        3: "Communicate effectively in a variety of professional contexts.",
        4: "Recognize professional responsibilities and make informed judgments in computing practice based on legal and ethical principles.",
        5: "Function effectively as a member or leader of a team engaged in activities appropriate to the program's discipline.",
        6: "Apply computer science theory and software development fundamentals to produce computing-based solutions."
    };

    // Define the indicators for each outcome
    const indicators = {
        1: [
            "1.1 Student can correctly interpret a computational problem and define its parameters",
            "1.2 Student can analyze a computational problem in order to choose mathematical and algorithmic principles that can be applied to solve the problem",
            "1.3 Student can define a solution to a computational problem",
            "1.4 Student can effectively collect and document system requirements",
            "1.5 Student can effectively analyze and model a problem domain",
            "1.6 Student can identify the relative efficiency of different algorithms using asymptotic notation"
        ],
        2: [
            "2.1 Student can identify and evaluate appropriate technologies to be used in a system",
            "2.2 Student can effectively construct a design model of a system",
            "2.3 Student can effectively incorporate requirements outside the problem domain (e.g., a user interface) into the design model",
            "2.4 Student can plan and implement a testing strategy to ensure that system meets its quality goal",
            "2.5 Student can collect and analyze runtime benchmark data to characterize the efficiency of an algorithm or data structure",
            "2.6 Student can specify appropriate security concerns and requirements for a component or system",
            "2.7 Student can evaluate a component or system to identify security characteristics and identify vulnerabilities"
        ],
        3: [
            "3.1 Student can write a clear and well-organized technical report",
            "3.2 Student can create and present a clear and well-organized technical presentation using appropriate visual, textual, and spoken content",
            "3.3 Student can communicate technical content to peers",
            "3.4 Student can communicate technical content to general audiences"
        ],
        4: [
            "4.1 Student can analyze and explain the ethical issues surrounding a particular computing topic (for example, peer-to-peer file sharing)",
            "4.2 Student demonstrates recognition of his or her professional responsibilities as a member of the computing profession"
        ],
        5: [
            "5.1 Student demonstrates an ability to participate in and implement processes for team communication and coordination",
            "5.2 Student demonstrates an ability to work closely with other students to solve technical problems"
        ],
        6: [
            "6.1 Student is proficient in a current programming language",
            "6.2 Student can create user interfaces using current platforms",
            "6.3 Student can write programs that use concurrency",
            "6.4 Student can implement automated tests to satisfy the goals of a testing strategy",
            "6.5 Student can use appropriate implementation techniques and practices to meet security requirements and/or mitigate discovered vulnerabilities"
        ]
    };

    // Define the mapping of course IDs to outcomes
    const courseOutcomes = {
        "CS101": [1, 6],
        "CS201": [1, 2, 6],
        "CS320": [1, 2, 3, 5, 6],
        "CS330": [2, 4],
        "CS335": [2, 4, 6],
        "CS340": [6],
        "CS350": [1],
        "CS360": [1, 2],
        "CS420": [6],
        "CS456": [4],
        "CS481": [1, 2, 3, 5, 6],
        "CS400": [1, 2, 3, 5, 6]
    };

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
            outcomeContainer.style.marginBottom = '20px';
            outcomeContainer.style.padding = '10px';
            outcomeContainer.style.backgroundColor = '#f5f5f5';
            outcomeContainer.style.borderRadius = '5px';
            
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
            outcomeLabel.style.fontWeight = 'bold';
            outcomeLabel.textContent = `Outcome ${outcomeId}: ${outcomeDescriptions[outcomeId]}`;
            
            // Add checkbox and label to container
            outcomeCheckboxContainer.appendChild(outcomeCheckbox);
            outcomeCheckboxContainer.appendChild(outcomeLabel);
            
            // Add outcome checkbox container to outcome container
            outcomeContainer.appendChild(outcomeCheckboxContainer);
            
            // Add indicators label
            const indicatorsLabel = document.createElement('div');
            indicatorsLabel.style.fontWeight = 'bold';
            indicatorsLabel.style.marginBottom = '5px';
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
                    indicatorContainer.style.marginLeft = '20px';
                    indicatorContainer.style.marginBottom = '5px';
                    
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
