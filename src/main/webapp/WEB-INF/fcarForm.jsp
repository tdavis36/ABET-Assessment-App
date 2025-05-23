<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Faculty Course Assessment Report (FCAR)</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="${pageContext.request.contextPath}/js/ajax-utils.js"></script>
    <script src="${pageContext.request.contextPath}/js/outcome-selector.js"></script>
</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />
<div class="form-container">
    <h1>Faculty Course Assessment Report (FCAR)</h1>
    <p>Complete the assessment form below for the course.</p>

    <form action="${pageContext.request.contextPath}/${sessionScope.userRole == 'admin' ? 'admin' : 'professor'}" method="post" id="fcarForm">
        <input type="hidden" name="action" value="saveFCAR"/>
        <input type="hidden" name="saveAction" id="saveActionInput" value="save"/>

        <!-- Basic Information Section -->
        <div class="form-section">
            <h2>Basic Information</h2>
            <!-- If editing an existing FCAR, include its ID as a hidden field -->
            <c:if test="${not empty fcar}">
                <input type="hidden" name="fcarId" value="${fcar.fcarId}" />
            </c:if>

            <div class="form-group">
                <label for="courseId">Course:</label>
                <input type="text" id="courseId" name="courseCode" value="${not empty fcar ? fcar.courseCode : ''}" required />
            </div>

            <div class="form-group">
                <label for="professorId">Professor:</label>
                <c:choose>
                    <c:when test="${sessionScope.userRole == 'admin'}">
                        <input type="text" id="professorId" name="professorId" value="${not empty fcar ? fcar.instructorId : ''}" required />
                    </c:when>
                    <c:otherwise>
                        <input type="text" id="professorId" value="${sessionScope.user.userId}" readonly />
                        <input type="hidden" name="professorId" value="${sessionScope.user.userId}" />
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="form-group">
                <label for="semester">Semester:</label>
                <select id="semester" name="semester" required>
                    <option value="Spring" ${not empty fcar && fcar.semester == 'Spring' ? 'selected' : ''}>Spring</option>
                    <option value="Summer" ${not empty fcar && fcar.semester == 'Summer' ? 'selected' : ''}>Summer</option>
                    <option value="Fall" ${not empty fcar && fcar.semester == 'Fall' ? 'selected' : ''}>Fall</option>
                </select>
            </div>

            <div class="form-group">
                <label for="year">Year:</label>
                <input type="number" id="year" name="year" min="2020" max="2030" value="${not empty fcar && not empty fcar.year ? fcar.year : '2025'}" required />
            </div>
        </div>

        <!-- Outcomes and Indicators Section -->
        <div class="form-section">
            <h2>Outcomes and Indicators</h2>
            <p>The following outcomes and indicators are assigned to this FCAR based on the course. These cannot be changed.</p>

            <div id="assignedOutcomesContainer" style="margin-top: 15px;">
                <!-- Hidden input to store the selected outcomes -->
                <input type="hidden" name="selectedOutcomes" id="selectedOutcomesInput"
                       value="${not empty fcar.assessmentMethods['selectedOutcomes'] ? fcar.assessmentMethods['selectedOutcomes'] : ''}" />

                <!-- Check if user is admin or professor -->
                <c:set var="isAdmin" value="${sessionScope.userRole == 'admin'}" />

                <c:choose>
                    <%-- Check if outcomes attribute exists --%>
                    <c:when test="${not empty outcomes}">
                        <%-- Get the course code from the FCAR or from the form input --%>
                        <c:set var="currentCourseCode" value="${not empty fcar.courseCode ? fcar.courseCode : param.courseId}" />

                        <%-- Get the course outcomes from the OutcomeController --%>
                        <c:set var="courseOutcomeIds" value="${outcomeController.findByCourseId(currentCourseCode)}" />

                        <%-- Only show outcomes associated with this course --%>
                        <c:forEach var="outcome" items="${outcomes}" varStatus="status">
                            <c:set var="outcomeId" value="${outcome.id}" />
                            <c:set var="outcomeSelected" value="${fn:contains(fcar.assessmentMethods['selectedOutcomes'], outcomeId)}" />

                            <%-- Check if this outcome is associated with the course --%>
                        <c:set var="isAssociatedWithCourse" value="true" />
                        <c:if test="${not empty courseOutcomeIds}">
                            <c:set var="isAssociatedWithCourse" value="false" />
                            <c:forEach var="courseOutcomeId" items="${courseOutcomeIds}">
                                <c:if test="${courseOutcomeId == outcomeId}">
                                    <c:set var="isAssociatedWithCourse" value="true" />
                                </c:if>
                            </c:forEach>
                        </c:if>

                        <%-- Display outcomes associated with this course or if no course is selected yet --%>
                        <c:if test="${isAssociatedWithCourse || isAdmin}">
                                <div class="outcome-container">
                                    <div class="outcome-label">
                                        <c:if test="${isAdmin}">
                                            <input type="checkbox" id="outcome_${outcomeId}" name="outcome_${outcomeId}"
                                                   value="${outcomeId}" ${outcomeSelected ? 'checked' : ''}
                                                   onchange="toggleIndicators(${outcomeId}); updateSelectedOutcomes();" />
                                        </c:if>
                                        <label for="outcome_${outcomeId}">Outcome ${outcomeId}: ${outcome.description}</label>
                                    </div>

                                    <div id="indicators_${outcomeId}" class="indicators-container" style="${outcomeSelected ? 'display: block;' : 'display: none;'}">
                                        <c:set var="hasIndicators" value="false" />

                                        <c:if test="${not empty indicatorsByOutcome[outcomeId]}">
                                            <c:forEach var="indicator" items="${indicatorsByOutcome[outcomeId]}">
                                                <c:set var="hasIndicators" value="true" />
                                                <c:set var="indicatorKey" value="indicator_${outcomeId}.${indicator.number}" />
                                                <c:set var="isSelected" value="${not empty fcar.assessmentMethods[indicatorKey]}" />

                                                <div class="indicator-container">
                                                    <c:if test="${isAdmin}">
                                                        <input type="checkbox" id="indicator_${outcomeId}_${indicator.number}"
                                                               name="indicator_${outcomeId}_${indicator.number}"
                                                               value="${outcomeId}.${indicator.number}" ${isSelected ? 'checked' : ''} />
                                                    </c:if>
                                                    <c:if test="${isSelected || (empty fcar.assessmentMethods['selectedOutcomes'])}">
                                                        <input type="hidden" name="indicator_${outcomeId}.${indicator.number}" value="selected" />
                                                    </c:if>
                                                    <label for="indicator_${outcomeId}_${indicator.number}">${outcomeId}.${indicator.number} ${indicator.description}</label>
                                                </div>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test="${!hasIndicators}">
                                            <p>No indicators available for this outcome.</p>
                                        </c:if>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${not empty fcar.assessmentMethods['selectedOutcomes']}">
                            <c:forEach var="outcomeId" items="${fcar.assessmentMethods['selectedOutcomes'].split(',')}" varStatus="status">
                                <div class="outcome-container">
                                    <div class="outcome-label">
                                        <c:choose>
                                            <c:when test="${outcomeId == '1'}">
                                                <c:if test="${isAdmin}">
                                                    <input type="checkbox" id="outcome_1" name="outcome_1" value="1" checked
                                                           onchange="toggleIndicators(1); updateSelectedOutcomes();" />
                                                </c:if>
                                                <label for="outcome_1">Outcome 1: Analyze a complex computing problem and to apply principles of computing and other relevant disciplines to identify solutions.</label>
                                            </c:when>
                                            <c:when test="${outcomeId == '2'}">
                                                <c:if test="${isAdmin}">
                                                    <input type="checkbox" id="outcome_2" name="outcome_2" value="2" checked
                                                           onchange="toggleIndicators(2); updateSelectedOutcomes();" />
                                                </c:if>
                                                <label for="outcome_2">Outcome 2: Design, implement, and evaluate a computing-based solution to meet a given set of computing requirements in the context of the program's discipline.</label>
                                            </c:when>
                                            <c:when test="${outcomeId == '3'}">
                                                <c:if test="${isAdmin}">
                                                    <input type="checkbox" id="outcome_3" name="outcome_3" value="3" checked
                                                           onchange="toggleIndicators(3); updateSelectedOutcomes();" />
                                                </c:if>
                                                <label for="outcome_3">Outcome 3: Communicate effectively in a variety of professional contexts.</label>
                                            </c:when>
                                            <c:when test="${outcomeId == '4'}">
                                                <c:if test="${isAdmin}">
                                                    <input type="checkbox" id="outcome_4" name="outcome_4" value="4" checked
                                                           onchange="toggleIndicators(4); updateSelectedOutcomes();" />
                                                </c:if>
                                                <label for="outcome_4">Outcome 4: Recognize professional responsibilities and make informed judgments in computing practice based on legal and ethical principles.</label>
                                            </c:when>
                                            <c:when test="${outcomeId == '5'}">
                                                <c:if test="${isAdmin}">
                                                    <input type="checkbox" id="outcome_5" name="outcome_5" value="5" checked
                                                           onchange="toggleIndicators(5); updateSelectedOutcomes();" />
                                                </c:if>
                                                <label for="outcome_5">Outcome 5: Function effectively as a member or leader of a team engaged in activities appropriate to the program's discipline.</label>
                                            </c:when>
                                            <c:when test="${outcomeId == '6'}">
                                                <c:if test="${isAdmin}">
                                                    <input type="checkbox" id="outcome_6" name="outcome_6" value="6" checked
                                                           onchange="toggleIndicators(6); updateSelectedOutcomes();" />
                                                </c:if>
                                                <label for="outcome_6">Outcome 6: Apply computer science theory and software development fundamentals to produce computing-based solutions.</label>
                                            </c:when>
                                            <c:otherwise>
                                                <label>Unknown Outcome: ${outcomeId}</label>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div id="indicators_${outcomeId}" class="indicators-container">
                                        <c:set var="hasIndicators" value="false" />

                                        <c:forEach var="i" begin="1" end="7">
                                            <c:set var="indicatorKey" value="indicator_${outcomeId}.${i}" />
                                            <c:set var="isSelected" value="${not empty fcar.assessmentMethods[indicatorKey]}" />

                                            <c:if test="${isSelected || isAdmin}">
                                                <div class="indicator-container">
                                                    <c:set var="hasIndicators" value="true" />
                                                    <c:set var="indicatorText" value="" />

                                                    <c:choose>
                                                        <c:when test="${outcomeId == '1' && i == 1}">
                                                            <c:set var="indicatorText" value="1.1 Student can correctly interpret a computational problem and define its parameters" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '1' && i == 2}">
                                                            <c:set var="indicatorText" value="1.2 Student can analyze a computational problem in order to choose mathematical and algorithmic principles that can be applied to solve the problem" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '1' && i == 3}">
                                                            <c:set var="indicatorText" value="1.3 Student can define a solution to a computational problem" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '1' && i == 4}">
                                                            <c:set var="indicatorText" value="1.4 Student can effectively collect and document system requirements" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '1' && i == 5}">
                                                            <c:set var="indicatorText" value="1.5 Student can effectively analyze and model a problem domain" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '1' && i == 6}">
                                                            <c:set var="indicatorText" value="1.6 Student can identify the relative efficiency of different algorithms using asymptotic notation" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '2' && i == 1}">
                                                            <c:set var="indicatorText" value="2.1 Student can identify and evaluate appropriate technologies to be used in a system" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '2' && i == 2}">
                                                            <c:set var="indicatorText" value="2.2 Student can effectively construct a design model of a system" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '2' && i == 3}">
                                                            <c:set var="indicatorText" value="2.3 Student can effectively incorporate requirements outside the problem domain (e.g., a user interface) into the design model" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '2' && i == 4}">
                                                            <c:set var="indicatorText" value="2.4 Student can plan and implement a testing strategy to ensure that system meets its quality goal" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '2' && i == 5}">
                                                            <c:set var="indicatorText" value="2.5 Student can collect and analyze runtime benchmark data to characterize the efficiency of an algorithm or data structure" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '2' && i == 6}">
                                                            <c:set var="indicatorText" value="2.6 Student can specify appropriate security concerns and requirements for a component or system" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '2' && i == 7}">
                                                            <c:set var="indicatorText" value="2.7 Student can evaluate a component or system to identify security characteristics and identify vulnerabilities" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '3' && i == 1}">
                                                            <c:set var="indicatorText" value="3.1 Student can write a clear and well-organized technical report" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '3' && i == 2}">
                                                            <c:set var="indicatorText" value="3.2 Student can create and present a clear and well-organized technical presentation using appropriate visual, textual, and spoken content" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '3' && i == 3}">
                                                            <c:set var="indicatorText" value="3.3 Student can communicate technical content to peers" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '3' && i == 4}">
                                                            <c:set var="indicatorText" value="3.4 Student can communicate technical content to general audiences" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '4' && i == 1}">
                                                            <c:set var="indicatorText" value="4.1 Student can analyze and explain the ethical issues surrounding a particular computing topic (for example, peer-to-peer file sharing)" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '4' && i == 2}">
                                                            <c:set var="indicatorText" value="4.2 Student demonstrates recognition of his or her professional responsibilities as a member of the computing profession" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '5' && i == 1}">
                                                            <c:set var="indicatorText" value="5.1 Student demonstrates an ability to participate in and implement processes for team communication and coordination" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '5' && i == 2}">
                                                            <c:set var="indicatorText" value="5.2 Student demonstrates an ability to work closely with other students to solve technical problems" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '6' && i == 1}">
                                                            <c:set var="indicatorText" value="6.1 Student is proficient in a current programming language" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '6' && i == 2}">
                                                            <c:set var="indicatorText" value="6.2 Student can create user interfaces using current platforms" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '6' && i == 3}">
                                                            <c:set var="indicatorText" value="6.3 Student can write programs that use concurrency" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '6' && i == 4}">
                                                            <c:set var="indicatorText" value="6.4 Student can implement automated tests to satisfy the goals of a testing strategy" />
                                                        </c:when>
                                                        <c:when test="${outcomeId == '6' && i == 5}">
                                                            <c:set var="indicatorText" value="6.5 Student can use appropriate implementation techniques and practices to meet security requirements and/or mitigate discovered vulnerabilities" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="indicatorText" value="" />
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:if test="${not empty indicatorText}">
                                                        <c:if test="${isAdmin}">
                                                            <input type="checkbox" id="indicator_${outcomeId}_${i}" name="indicator_${outcomeId}_${i}"
                                                                   value="${outcomeId}.${i}" ${isSelected ? 'checked' : ''} />
                                                        </c:if>
                                                        <c:if test="${isSelected}">
                                                            <input type="hidden" name="indicator_${outcomeId}.${i}" value="selected" />
                                                        </c:if>
                                                        <label for="indicator_${outcomeId}_${i}">${indicatorText}</label>
                                                    </c:if>
                                                </div>
                                            </c:if>
                                        </c:forEach>

                                        <c:if test="${!hasIndicators}">
                                            <p>No indicators are selected for this outcome.</p>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                        <c:if test="${empty fcar.assessmentMethods['selectedOutcomes']}">
                            <p>No outcomes have been assigned to this FCAR. Please contact an administrator.</p>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="form-group">
                <label for="targetGoal">Target Goal (%):</label>
                <input type="number" id="targetGoal" name="targetGoal" min="0" max="100" value="${not empty fcar.assessmentMethods['targetGoal'] ? fcar.assessmentMethods['targetGoal'] : '70'}" required />
            </div>
        </div>

        <!-- Assessment Method Section -->
        <div class="form-section">
            <h2>Assessment Method</h2>
            <div class="form-group">
                <label for="workUsed">Work Used for Assessment:</label>
                <input type="text" id="workUsed" name="workUsed" value="${fcar.assessmentMethods['workUsed']}" placeholder="e.g., Final Project, Exam Question 3, Assignment 2" required />
            </div>

            <div class="form-group">
                <label for="assessmentDescription">Description of Assessment Method:</label>
                <textarea id="assessmentDescription" name="assessmentDescription" placeholder="Describe how the work was used to assess the outcome/indicator..." required>${fcar.assessmentMethods['assessmentDescription']}</textarea>
            </div>
        </div>

        <!-- Achievement Levels Section -->
        <div class="form-section">
            <h2>Achievement Levels</h2>
            <p>Enter the number of students who achieved each level:</p>

            <table class="achievement-table">
                <thead>
                <tr>
                    <th>Exceeds Expectations</th>
                    <th>Meets Expectations</th>
                    <th>Below Expectations</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td><label>
                        <input type="number" name="level3" min="0" value="${not empty fcar.assessmentMethods['level3'] ? fcar.assessmentMethods['level3'] : '0'}" required />
                    </label></td>
                    <td><label>
                        <input type="number" name="level2" min="0" value="${not empty fcar.assessmentMethods['level2'] ? fcar.assessmentMethods['level2'] : '0'}" required />
                    </label></td>
                    <td><label>
                        <input type="number" name="level1" min="0" value="${not empty fcar.assessmentMethods['level1'] ? fcar.assessmentMethods['level1'] : '0'}" required />
                    </label></td>
                </tr>
                </tbody>
            </table>

            <!-- Hidden fields to maintain backward compatibility -->
            <input type="hidden" name="level4" value="0" />
            <input type="hidden" name="level0" value="0" />

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
                            <label>
                                <input type="text" name="major[]" placeholder="e.g., Computer Science" />
                            </label>
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
                                <td><label>
                                    <input type="number" name="majorLevel4[]" min="0" value="0" />
                                </label></td>
                                <td><label>
                                    <input type="number" name="majorLevel3[]" min="0" value="0" />
                                </label></td>
                                <td><label>
                                    <input type="number" name="majorLevel2[]" min="0" value="0" />
                                </label></td>
                                <td><label>
                                    <input type="number" name="majorLevel1[]" min="0" value="0" />
                                </label></td>
                                <td><label>
                                    <input type="number" name="majorLevel0[]" min="0" value="0" />
                                </label></td>
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
                <textarea id="summary" name="summary" placeholder="Provide a summary and observations of the assessment results..." required>${fcar.improvementActions['summary']}</textarea>
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
                <textarea id="improvementActions" name="improvementActions" placeholder="Describe any actions that will be taken to improve student performance...">${fcar.improvementActions['actions']}</textarea>
            </div>
        </div>

        <div class="button-container">
            <c:choose>
                <c:when test="${sessionScope.userRole == 'admin'}">
                    <!-- Admin buttons -->
                    <button type="button" class="btn-submit" onclick="submitFCAR()">Submit FCAR</button>
                    <button type="button" class="btn-submit" onclick="saveAndExit()">Save Changes</button>
                </c:when>
                <c:otherwise>
                    <!-- Professor buttons -->
                    <button type="button" class="btn-submit" onclick="submitFCAR()">Submit FCAR</button>
                    <button type="button" class="btn-submit btn-save" onclick="saveAndExit()">Save as Draft</button>
                </c:otherwise>
            </c:choose>
        </div>
    </form>
</div>

<script>
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

    // Function to set the action and submit the form
    function setAction(action) {
        document.getElementById('saveActionInput').value = action;
    }

    // Function to save as draft and exit
    function saveAndExit() {
        // Validate form before saving
        if (!validateForm()) {
            return false;
        }

        // Set the action to 'save'
        document.getElementById('saveActionInput').value = 'save';

        // Add a hidden field to indicate we want to redirect to ViewFCARServlet
        let redirectField = document.getElementById('redirectToViewField');
        if (!redirectField) {
            redirectField = document.createElement('input');
            redirectField.type = 'hidden';
            redirectField.id = 'redirectToViewField';
            redirectField.name = 'redirectToView';
            document.getElementById('fcarForm').appendChild(redirectField);
        }
        redirectField.value = 'true';

        // Show saving indicator
        const saveButton = event.target;
        const originalText = saveButton.textContent;
        saveButton.textContent = 'Saving...';
        saveButton.disabled = true;

        // Get form data
        const form = document.getElementById('fcarForm');
        const formData = new FormData(form);

        // Get form action URL
        const actionUrl = form.getAttribute('action');

        // Submit the form with fetch API
        fetch(actionUrl, {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                // Success callback
                saveButton.textContent = 'Saved!';

                // Show success message if provided in the response
                if (data.message) {
                    // You can display a toast notification or alert here
                    console.log('Success: ' + data.message);
                }

                // Redirect after a short delay
                setTimeout(function() {
                    // If redirectUrl is provided in the response, use it
                    if (data.redirectUrl) {
                        window.location.href = data.redirectUrl;
                    } else {
                        // Otherwise, redirect to the view page
                        window.location.href = '${pageContext.request.contextPath}/ViewFCARServlet?action=viewAll';
                    }
                }, 1000);
            })
            .catch(error => {
                // Error callback
                console.error('Error:', error);
                saveButton.textContent = 'Error Saving';
                alert('Error saving FCAR: ' + error.message);

                // Reset button after a delay
                setTimeout(function() {
                    saveButton.textContent = originalText;
                    saveButton.disabled = false;
                }, 2000);
            });

        // Prevent default form submission
        return false;
    }

    // Validate the form before submission
    function validateForm() {
        const requiredFields = document.querySelectorAll('[required]');
        let isValid = true;

        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                field.classList.add('error');
                isValid = false;
            } else {
                field.classList.remove('error');
            }
        });

        if (!isValid) {
            alert('Please fill in all required fields before saving.');
        }

        return isValid;
    }

    // Function to submit the FCAR
    function submitFCAR() {
        // Validate form before submitting
        if (!validateForm()) {
            return false;
        }

        let confirmMessage = 'Are you sure you want to submit this FCAR?';

        // Different message for admin vs professor
        if (${sessionScope.userRole == 'admin'}) {
            confirmMessage += ' Once submitted, it can only be edited by administrators.';
        } else {
            confirmMessage += ' Once submitted, it may not be editable by you, but administrators can still make changes.';
        }

        if (confirm(confirmMessage)) {
            document.getElementById('saveActionInput').value = 'submit';

            // Show submitting indicator
            const submitButton = event.target;
            const originalText = submitButton.textContent;
            submitButton.textContent = 'Submitting...';
            submitButton.disabled = true;

            // Get form data
            const form = document.getElementById('fcarForm');
            const formData = new FormData(form);

            // Get form action URL
            const actionUrl = form.getAttribute('action');

            // Submit the form with fetch API
            fetch(actionUrl, {
                method: 'POST',
                body: formData,
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    // Success callback
                    submitButton.textContent = 'Submitted!';

                    // Show success message if provided
                    if (data.message) {
                        console.log('Success: ' + data.message);
                    }

                    // Redirect after a short delay
                    setTimeout(function() {
                        if (data.redirectUrl) {
                            window.location.href = data.redirectUrl;
                        } else {
                            window.location.href = '${pageContext.request.contextPath}/ViewFCARServlet?action=viewAll';
                        }
                    }, 1000);
                })
                .catch(error => {
                    // Error callback
                    console.error('Error:', error);
                    submitButton.textContent = 'Error Submitting';
                    alert('Error submitting FCAR: ' + error.message);

                    // Reset button after a delay
                    setTimeout(function() {
                        submitButton.textContent = originalText;
                        submitButton.disabled = false;
                    }, 2000);
                });
        }

        // Prevent default form submission
        return false;
    }

    // Function to save changes (for admin)
    function saveChanges() {
        document.getElementById('saveActionInput').value = 'save';
        // Add a hidden field to indicate we want to redirect to ViewFCARServlet
        var redirectField = document.createElement('input');
        redirectField.type = 'hidden';
        redirectField.name = 'redirectToView';
        redirectField.value = 'true';
        document.getElementById('fcarForm').appendChild(redirectField);
        // Submit the form with saveFCAR action
        document.getElementById('fcarForm').submit();
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

    // Function to toggle indicators for an outcome
    function toggleIndicators(outcomeId) {
        const checkbox = document.getElementById(`outcome_${outcomeId}`);
        const indicatorsDiv = document.getElementById(`indicators_${outcomeId}`);

        if (checkbox.checked) {
            indicatorsDiv.style.display = 'block';
        } else {
            indicatorsDiv.style.display = 'none';

            // Uncheck all indicators for this outcome
            const indicators = document.querySelectorAll(`input[id^="indicator_${outcomeId}_"]`);
            indicators.forEach(indicator => {
                indicator.checked = false;
            });
        }
    }

    // Function to update the selected outcomes hidden input
    function updateSelectedOutcomes() {
        const outcomeCheckboxes = document.querySelectorAll('input[id^="outcome_"][type="checkbox"]');
        const selectedOutcomes = [];

        outcomeCheckboxes.forEach(checkbox => {
            if (checkbox.checked) {
                selectedOutcomes.push(checkbox.value);
            }
        });

        document.getElementById('selectedOutcomesInput').value = selectedOutcomes.join(',');
    }

    // Initialize outcome data from the server
    const outcomeData = {
        outcomeDescriptions: ${requestScope.outcomeDescriptions != null ? requestScope.outcomeDescriptions : "{}"},
        outcomeNumbers: ${requestScope.outcomeNumbers != null ? requestScope.outcomeNumbers : "{}"},
        indicators: ${requestScope.indicators != null ? requestScope.indicators : "{}"},
        courseOutcomes: ${requestScope.courseOutcomes != null ? requestScope.courseOutcomes : "{}"}
    };

    // Complete the DOMContentLoaded function at the bottom of your file
    document.addEventListener('DOMContentLoaded', function() {
        // Initialize outcome selector
        if (typeof initializeOutcomeSelector === 'function') {
            initializeOutcomeSelector(outcomeData);
        }

        // Calculate initial results
        calculateResults();

        // Add event listeners to achievement level inputs
        document.getElementsByName('level4')[0].addEventListener('input', calculateResults);
        document.getElementsByName('level3')[0].addEventListener('input', calculateResults);
        document.getElementsByName('level2')[0].addEventListener('input', calculateResults);
        document.getElementsByName('level1')[0].addEventListener('input', calculateResults);
        document.getElementsByName('level0')[0].addEventListener('input', calculateResults);
        document.getElementById('targetGoal').addEventListener('input', calculateResults);

        // Add event listeners to form buttons
        document.getElementById('fcarForm').addEventListener('submit', function(event) {
            // Prevent form submission if the action is 'submit' and confirmation is needed
            if (document.getElementById('saveActionInput').value === 'submit') {
                let confirmMessage = 'Are you sure you want to submit this FCAR?';

                // Different message for admin vs professor
                if (${sessionScope.userRole == 'admin'}) {
                    confirmMessage += ' Once submitted, it can only be edited by administrators.';
                } else {
                    confirmMessage += ' Once submitted, it may not be editable by you, but administrators can still make changes.';
                }

                if (!confirm(confirmMessage)) {
                    event.preventDefault();
                }
            }
        });
    });
</script>
