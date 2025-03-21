<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>FCAR Assessment Form</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        .form-container {
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
        }
        .form-section {
            margin-bottom: 30px;
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
        textarea {
            height: 100px;
            resize: vertical;
        }
        .achievement-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        .achievement-table th, .achievement-table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }
        .achievement-table th {
            background-color: #f2f2f2;
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
        .major-breakdown {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px dashed #ccc;
        }
        .add-major-btn {
            background-color: #2196F3;
            color: white;
            padding: 5px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 10px;
        }
        .add-major-btn:hover {
            background-color: #0b7dda;
        }
        .results-section {
            margin-top: 20px;
            padding: 15px;
            background-color: #e8f5e9;
            border-radius: 5px;
            border: 1px solid #c8e6c9;
        }
    </style>
</head>
<body>
<div class="form-container">
    <h1>Faculty Course Assessment Report (FCAR)</h1>
    <p>Complete the assessment form below for the assigned outcome/indicator.</p>
    
    <form action="${pageContext.request.contextPath}/ProfessorServlet" method="post">
        <input type="hidden" name="action" value="submitFCAR"/>
        
        <!-- Basic Information Section -->
        <div class="form-section">
            <h2>Basic Information</h2>
            <div class="form-group">
                <label for="courseId">Course:</label>
                <input type="text" id="courseId" name="courseId" required />
            </div>
            
            <div class="form-group">
                <label for="professorId">Professor:</label>
                <input type="text" id="professorId" name="professorId" required />
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
        </div>
        
        <!-- Outcome and Indicator Section -->
        <div class="form-section">
            <h2>Outcome and Indicator</h2>
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
        </div>
        
        <!-- Assessment Method Section -->
        <div class="form-section">
            <h2>Assessment Method</h2>
            <div class="form-group">
                <label for="workUsed">Work Used for Assessment:</label>
                <input type="text" id="workUsed" name="workUsed" placeholder="e.g., Final Project, Exam Question 3, Assignment 2" required />
            </div>
            
            <div class="form-group">
                <label for="assessmentDescription">Description of Assessment Method:</label>
                <textarea id="assessmentDescription" name="assessmentDescription" placeholder="Describe how the work was used to assess the outcome/indicator..." required></textarea>
            </div>
        </div>
        
        <!-- Achievement Levels Section -->
        <div class="form-section">
            <h2>Achievement Levels</h2>
            <p>Enter the number of students who achieved each level:</p>
            
            <table class="achievement-table">
                <thead>
                    <tr>
                        <th>Exemplary (4)</th>
                        <th>Satisfactory (3)</th>
                        <th>Developing (2)</th>
                        <th>Unsatisfactory (1)</th>
                        <th>Not Applicable (0)</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><input type="number" name="level4" min="0" value="0" required /></td>
                        <td><input type="number" name="level3" min="0" value="0" required /></td>
                        <td><input type="number" name="level2" min="0" value="0" required /></td>
                        <td><input type="number" name="level1" min="0" value="0" required /></td>
                        <td><input type="number" name="level0" min="0" value="0" required /></td>
                    </tr>
                </tbody>
            </table>
            
            <div class="form-group" style="margin-top: 15px;">
                <label>
                    <input type="checkbox" id="breakdownByMajor" name="breakdownByMajor" onchange="toggleMajorBreakdown()" />
                    Break down by majors
                </label>
            </div>
            
            <div id="majorBreakdownContainer" class="major-breakdown" style="display: none;">
                <h3>Achievement Levels by Major</h3>
                <div id="majorBreakdowns">
                    <div class="major-section">
                        <div class="form-group">
                            <label>Major:</label>
                            <input type="text" name="major[]" placeholder="e.g., Computer Science" />
                        </div>
                        <table class="achievement-table">
                            <thead>
                                <tr>
                                    <th>Exemplary (4)</th>
                                    <th>Satisfactory (3)</th>
                                    <th>Developing (2)</th>
                                    <th>Unsatisfactory (1)</th>
                                    <th>Not Applicable (0)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><input type="number" name="majorLevel4[]" min="0" value="0" /></td>
                                    <td><input type="number" name="majorLevel3[]" min="0" value="0" /></td>
                                    <td><input type="number" name="majorLevel2[]" min="0" value="0" /></td>
                                    <td><input type="number" name="majorLevel1[]" min="0" value="0" /></td>
                                    <td><input type="number" name="majorLevel0[]" min="0" value="0" /></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <button type="button" class="add-major-btn" onclick="addMajorBreakdown()">Add Another Major</button>
            </div>
        </div>
        
        <!-- Results Section -->
        <div class="form-section">
            <h2>Results</h2>
            <div class="form-group">
                <label for="summary">Summary/Observations of Results:</label>
                <textarea id="summary" name="summary" placeholder="Provide a summary and observations of the assessment results..." required></textarea>
            </div>
            
            <div class="results-section">
                <h3>Calculated Results</h3>
                <p>These results will be calculated automatically based on the achievement levels entered above.</p>
                <div id="calculatedResults">
                    <p>Total Students: <span id="totalStudents">0</span></p>
                    <p>Students Meeting Target (Level 3 or 4): <span id="studentsMetTarget">0</span></p>
                    <p>Percentage Meeting Target: <span id="percentageMetTarget">0%</span></p>
                    <p>Target Goal Met: <span id="targetMet">No</span></p>
                </div>
            </div>
        </div>
        
        <!-- Improvement Actions Section -->
        <div class="form-section">
            <h2>Improvement Actions</h2>
            <div class="form-group">
                <label for="improvementActions">Proposed Actions for Improvement:</label>
                <textarea id="improvementActions" name="improvementActions" placeholder="Describe any actions that will be taken to improve student performance..."></textarea>
            </div>
        </div>
        
        <button type="submit" class="btn-submit">Submit FCAR</button>
    </form>
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
    
    // Function to toggle major breakdown section
    function toggleMajorBreakdown() {
        const checkbox = document.getElementById('breakdownByMajor');
        const container = document.getElementById('majorBreakdownContainer');
        
        if (checkbox.checked) {
            container.style.display = 'block';
        } else {
            container.style.display = 'none';
        }
    }
    
    // Function to add another major breakdown section
    function addMajorBreakdown() {
        const container = document.getElementById('majorBreakdowns');
        const newSection = document.createElement('div');
        newSection.className = 'major-section';
        newSection.innerHTML = `
            <div class="form-group" style="margin-top: 20px;">
                <label>Major:</label>
                <input type="text" name="major[]" placeholder="e.g., Computer Science" />
            </div>
            <table class="achievement-table">
                <thead>
                    <tr>
                        <th>Exemplary (4)</th>
                        <th>Satisfactory (3)</th>
                        <th>Developing (2)</th>
                        <th>Unsatisfactory (1)</th>
                        <th>Not Applicable (0)</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><input type="number" name="majorLevel4[]" min="0" value="0" /></td>
                        <td><input type="number" name="majorLevel3[]" min="0" value="0" /></td>
                        <td><input type="number" name="majorLevel2[]" min="0" value="0" /></td>
                        <td><input type="number" name="majorLevel1[]" min="0" value="0" /></td>
                        <td><input type="number" name="majorLevel0[]" min="0" value="0" /></td>
                    </tr>
                </tbody>
            </table>
        `;
        container.appendChild(newSection);
    }
    
    // Function to calculate and update results
    function calculateResults() {
        const level4 = parseInt(document.getElementsByName('level4')[0].value) || 0;
        const level3 = parseInt(document.getElementsByName('level3')[0].value) || 0;
        const level2 = parseInt(document.getElementsByName('level2')[0].value) || 0;
        const level1 = parseInt(document.getElementsByName('level1')[0].value) || 0;
        const level0 = parseInt(document.getElementsByName('level0')[0].value) || 0;
        
        const totalStudents = level4 + level3 + level2 + level1 + level0;
        const studentsMetTarget = level4 + level3;
        const percentageMetTarget = totalStudents > 0 ? Math.round((studentsMetTarget / totalStudents) * 100) : 0;
        const targetGoal = parseInt(document.getElementById('targetGoal').value) || 70;
        const targetMet = percentageMetTarget >= targetGoal ? 'Yes' : 'No';
        
        document.getElementById('totalStudents').textContent = totalStudents;
        document.getElementById('studentsMetTarget').textContent = studentsMetTarget;
        document.getElementById('percentageMetTarget').textContent = percentageMetTarget + '%';
        document.getElementById('targetMet').textContent = targetMet;
        
        // Update target met styling
        const targetMetElement = document.getElementById('targetMet');
        if (targetMet === 'Yes') {
            targetMetElement.style.color = 'green';
            targetMetElement.style.fontWeight = 'bold';
        } else {
            targetMetElement.style.color = 'red';
            targetMetElement.style.fontWeight = 'bold';
        }
    }
    
    // Add event listeners to achievement level inputs
    document.getElementsByName('level4')[0].addEventListener('input', calculateResults);
    document.getElementsByName('level3')[0].addEventListener('input', calculateResults);
    document.getElementsByName('level2')[0].addEventListener('input', calculateResults);
    document.getElementsByName('level1')[0].addEventListener('input', calculateResults);
    document.getElementsByName('level0')[0].addEventListener('input', calculateResults);
    document.getElementById('targetGoal').addEventListener('input', calculateResults);
    
    // Initialize the form
    document.addEventListener('DOMContentLoaded', function() {
        updateIndicators();
        calculateResults();
    });
</script>
</body>
</html>
