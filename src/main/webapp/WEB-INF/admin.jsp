<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .form-section {
            margin-bottom: 20px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], input[type="number"], select, textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .btn-submit {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .btn-submit:hover {
            background-color: #45a049;
        }
    </style>
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
                <form action="${pageContext.request.contextPath}/AdminServlet" method="post">
                    <input type="hidden" name="action" value="createFCAR" />
                    
                    <div class="form-group">
                        <label for="courseId">Course ID:</label>
                        <input type="text" id="courseId" name="courseId" required />
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
                    
                    <!-- Outcome and Indicator Section -->
                    <div class="form-group">
                        <label for="outcome">Student Learning Outcome:</label>
                        <select id="outcome" name="outcome" required onchange="updateIndicators()">
                            <option value="">Select an outcome</option>
                            <option value="outcome1">Analyze a complex computing problem and to apply principles of computing and other relevant disciplines to identify solutions.</option>
                            <option value="outcome2">Design, implement, and evaluate a computing-based solution to meet a given set of computing requirements in the context of the program's discipline.</option>
                            <option value="outcome3">Communicate effectively in a variety of professional contexts.</option>
                            <option value="outcome4">Recognize professional responsibilities and make informed judgments in computing practice based on legal and ethical principles.</option>
                            <option value="outcome5">Function effectively as a member or leader of a team engaged in activities appropriate to the program's discipline.</option>
                            <option value="outcome6">Apply computer science theory and software development fundamentals to produce computing-based solutions.</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="indicator">Performance Indicator:</label>
                        <select id="indicator" name="indicator" required>
                            <option value="">Select an outcome first</option>
                        </select>
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
    // Define the indicators for each outcome based on the CSV file
    const indicators = {
        outcome1: [
            "Student can correctly interpret a computational problem and define its parameters",
            "Student can analyze a computational problem in order to choose mathematical and algorithmic principles that can be applied to solve the problem",
            "Student can define a solution to a computational problem",
            "Student can effectively collect and document system requirements",
            "Student can effectively analyze and model a problem domain",
            "Student can identify the relative efficiency of different algorithms using asymptotic notation"
        ],
        outcome2: [
            "Student can identify and evaluate appropriate technologies to be used in a system",
            "Student can effectively construct a design model of a system",
            "Student can effectively incorporate requirements outside the problem domain (e.g., a user interface) into the design model",
            "Student can plan and implement a testing strategy to ensure that system meets its quality goal",
            "Student can collect and analyze runtime benchmark data to characterize the efficiency of an algorithm or data structure",
            "Student can specify appropriate security concerns and requirements for a component or system",
            "Student can evaluate a component or system to identify security characteristics and identify vulnerabilities"
        ],
        outcome3: [
            "Student can write a clear and well-organized technical report",
            "Student can create and present a clear and well-organized technical presentation using appropriate visual, textual, and spoken content",
            "Student can communicate technical content to peers",
            "Student can communicate technical content to general audiences"
        ],
        outcome4: [
            "Student can analyze and explain the ethical issues surrounding a particular computing topic (for example, peer-to-peer file sharing)",
            "Student demonstrates recognition of his or her professional responsibilities as a member of the computing profession"
        ],
        outcome5: [
            "Student demonstrates an ability to participate in and implement processes for team communication and coordination",
            "Student demonstrates an ability to work closely with other students to solve technical problems"
        ],
        outcome6: [
            "Student is proficient in a current programming language",
            "Student can create user interfaces using current platforms",
            "Student can write programs that use concurrency",
            "Student can implement automated tests to satisfy the goals of a testing strategy",
            "Student can use appropriate implementation techniques and practices to meet security requirements and/or mitigate discovered vulnerabilities"
        ]
    };

    // Function to update indicators dropdown based on selected outcome
    function updateIndicators() {
        const outcomeSelect = document.getElementById('outcome');
        const indicatorSelect = document.getElementById('indicator');
        
        // Clear existing options
        indicatorSelect.innerHTML = '';
        
        // Get selected outcome
        const selectedOutcome = outcomeSelect.value;
        
        if (selectedOutcome) {
            // Add default option
            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.textContent = 'Select an indicator';
            indicatorSelect.appendChild(defaultOption);
            
            // Add indicators for the selected outcome
            const outcomeIndicators = indicators[selectedOutcome];
            outcomeIndicators.forEach((indicator, index) => {
                const option = document.createElement('option');
                option.value = `${selectedOutcome}_indicator${index + 1}`;
                option.textContent = indicator;
                indicatorSelect.appendChild(option);
            });
        } else {
            // If no outcome selected, show default message
            const option = document.createElement('option');
            option.value = '';
            option.textContent = 'Select an outcome first';
            indicatorSelect.appendChild(option);
        }
    }
</script>
</body>
</html>
