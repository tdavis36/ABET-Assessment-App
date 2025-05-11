<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.ABETAppTeam.model.FCAR" %>
<%@ page import="com.ABETAppTeam.repository.FCARRepository" %>
<%@ page import="com.ABETAppTeam.repository.OutcomeRepository" %>
<%@ page import="com.ABETAppTeam.repository.IndicatorRepository" %>
<%@ page import="com.ABETAppTeam.model.Outcome" %>
<%@ page import="com.ABETAppTeam.model.Indicator" %>
<%@ page import="com.ABETAppTeam.model.User" %>
<%@ page import="com.ABETAppTeam.model.Admin" %>
<%@ page import="com.ABETAppTeam.model.Professor" %>
<%@ page import="com.ABETAppTeam.controller.DisplaySystemController" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>FCAR View</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <style>
        /* Message styling */
        .message-container {
            margin-bottom: 20px;
        }

        .success-message {
            background-color: #dff0d8;
            border: 1px solid #d6e9c6;
            color: #3c763d;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 10px;
        }

        .error-message {
            background-color: #f2dede;
            border: 1px solid #ebccd1;
            color: #a94442;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 10px;
        }

        /* Override button width to prevent full-width on all screens */
        .btn {
            width: auto !important;
            max-width: none !important;
            margin-right: 5px;
        }

        /* Specific styling for the toggle-details button */
        .toggle-details {
            width: auto !important;
            display: inline-block !important;
            margin-left: auto !important;
            flex-grow: 0 !important;
        }

        /* Maintain spacing between buttons */
        .fcar-actions {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            justify-content: flex-end;
            margin-top: 10px;
        }

        /* FCAR item styling */
        .fcar-item {
            background-color: var(--card-bg);
            border-radius: 5px;
            margin-bottom: 15px;
            box-shadow: 0 1px 3px var(--shadow);
            overflow: hidden;
        }

        /* FCAR header styling */
        .fcar-header {
            padding: 15px;
            background-color: var(--form-bg);
            border-bottom: 1px solid var(--border);
        }

        /* FCAR details styling */
        .fcar-details {
            padding: 15px;
            background-color: var(--card-bg);
        }

        .fcar-section {
            margin-bottom: 15px;
            padding: 15px;
            background-color: var(--form-bg);
            border-radius: 5px;
            border: 1px solid var(--border);
        }

        .fcar-section h4 {
            margin-top: 0;
            margin-bottom: 10px;
            color: var(--primary);
        }

        /* Ensure buttons remain a reasonable size on mobile */
        @media (max-width: 768px) {
            .btn {
                padding: 8px 12px;
                font-size: 14px;
                margin-bottom: 5px;
            }

            /* Improve responsive layout for action buttons */
            .fcar-actions {
                justify-content: flex-start;
            }

            /* Ensure the fcar-header stays as a row even on mobile */
            .fcar-header {
                flex-wrap: nowrap !important;
                gap: 10px;
            }

            /* Keep toggle button from growing */
            .toggle-details {
                flex-shrink: 0;
                flex-basis: auto;
            }
        }
        /* Ensure buttons auto-size */
        .btn { width: auto !important; max-width: none !important; }
        .fcar-actions { display: flex; flex-wrap: wrap; gap: 8px; justify-content: flex-end; }
    </style>

</head>
<body>
<jsp:include page="/WEB-INF/navbar.jsp" />
<div class="dashboard" id="fcarViewDashboard">
    <div class="header-container">
        <h1>FCAR Details</h1>
    </div>

    <!-- Message Display Section -->
    <c:if test="${(successMessage != null && not empty successMessage) || (errorMessage != null && not empty errorMessage)}">
        <div class="message-container">
            <c:if test="${successMessage != null && not empty successMessage}">
                <div class="success-message">
                    <p>${successMessage}</p>
                </div>
            </c:if>
            <c:if test="${errorMessage != null && not empty errorMessage}">
                <div class="error-message">
                    <p>${errorMessage}</p>
                </div>
            </c:if>
        </div>
    </c:if>

    <!-- Status Key -->
    <div class="status-key">
        <div><span class="status draft"></span> Draft</div>
        <div><span class="status submitted"></span> Submitted</div>
        <div><span class="status approved"></span> Approved</div>
        <div><span class="status rejected"></span> Rejected</div>
    </div>

    <div class="section">
        <h2>Existing FCARs</h2>
        <%
            User user = (User) session.getAttribute("user");
            String dashboardUrl;
            if (user instanceof Admin) dashboardUrl = request.getContextPath() + "/admin";
            else if (user instanceof Professor) dashboardUrl = request.getContextPath() + "/professor";
            else dashboardUrl = request.getContextPath() + "/index";

            // Initialize repositories for outcomes and indicators
            OutcomeRepository outcomeRepository = new OutcomeRepository();
            IndicatorRepository indicatorRepository = new IndicatorRepository();

            // Create a map to store outcome information
            Map<Integer, Outcome> outcomeMap = new HashMap<>();
            for (Outcome outcome : outcomeRepository.findAll()) {
                outcomeMap.put(outcome.getId(), outcome);
            }

            // Create a map to store indicator information
            Map<Integer, List<Indicator>> indicatorMap = new HashMap<>();
            for (Indicator indicator : indicatorRepository.findAll()) {
                int outcomeId = indicator.getOutcomeId();
                if (!indicatorMap.containsKey(outcomeId)) {
                    indicatorMap.put(outcomeId, new ArrayList<>());
                }
                indicatorMap.get(outcomeId).add(indicator);
            }

            // Set attributes for JSTL access
            request.setAttribute("outcomeMap", outcomeMap);
            request.setAttribute("indicatorMap", indicatorMap);

            // Check if we're viewing a specific FCAR
            FCAR selectedFCAR = (FCAR) request.getAttribute("selectedFCAR");

            // If we're not viewing a specific FCAR and allFCARs is not set, fetch all FCARs
            if (selectedFCAR == null && request.getAttribute("allFCARs") == null) {
                FCARRepository repo = new FCARRepository();
                List<FCAR> allFCARs = (user instanceof Admin)
                        ? repo.findAll()
                        : repo.findByInstructorId(user.getUserId());
                request.setAttribute("allFCARs", allFCARs);
            }

            // If we're viewing a specific FCAR, set the fcarId parameter for the Edit button
            if (selectedFCAR != null) {
                request.setAttribute("fcarId", selectedFCAR.getFcarId());
            }
        %>
        <c:choose>
            <c:when test="${not empty selectedFCAR}">
                <!-- Display a single FCAR when selectedFCAR is set -->
                <ul class="fcar-list">
                    <li class="fcar-item">
                        <div class="fcar-header">
                            <div>
                                <h3>FCAR: ${selectedFCAR.courseCode} <c:if test="${course != null && not empty course}">- ${course.courseName}</c:if></h3>
                                <p>
                                    <strong>Professor:</strong>
                                    <c:choose>
                                        <c:when test="${professor != null && not empty professor}">
                                            ${professor.firstName} ${professor.lastName}
                                        </c:when>
                                        <c:otherwise>
                                            ID: ${selectedFCAR.instructorId}
                                        </c:otherwise>
                                    </c:choose> |
                                    <strong>Semester:</strong> ${selectedFCAR.semester} ${selectedFCAR.year} |
                                    <strong>Status:</strong>
                                    <span class="status-badge status-${fn:toLowerCase(selectedFCAR.status)}">${selectedFCAR.status}</span>
                                </p>
                            </div>
                            <div>
                                <button class="btn toggle-details" onclick="toggleDetails('fcar-${selectedFCAR.fcarId}')">Show Details</button>
                                <c:if test="${user.userId == selectedFCAR.instructorId || user.roleId == 1}">
                                    <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${selectedFCAR.fcarId}" class="btn">Edit FCAR</a>
                                </c:if>
                            </div>
                        </div>
                        <div id="fcar-${selectedFCAR.fcarId}" class="fcar-details" style="display:none;">
                            <div class="fcar-section">
                                <h4>Actions</h4>
                                <c:if test="${user.userId == selectedFCAR.instructorId || user.roleId == 1}">
                                    <p><strong>Edit Status:</strong>
                                        <c:choose>
                                            <c:when test="${selectedFCAR.status == 'Draft' || user.roleId == 1}">Available</c:when>
                                            <c:otherwise>Not available for ${selectedFCAR.status} FCARs</c:otherwise>
                                        </c:choose>
                                    </p>
                                </c:if>
                                <c:if test="${user.roleId == 1 && selectedFCAR.status == 'Submitted'}">
                                    <p><strong>Admin Actions:</strong>
                                        <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${selectedFCAR.fcarId}" class="btn">Edit FCAR</a>
                                        Approval pending
                                    </p>
                                </c:if>
                            </div>
                            <c:if test="${selectedFCAR != null && selectedFCAR.outcomeId > 0}">
                                <div class="fcar-section">
                                    <h4>Student Learning Outcomes</h4>
                                    <c:set var="outcome" value="${outcomeMap[selectedFCAR.outcomeId]}" />
                                    <p><strong>Outcome:</strong>
                                        <c:choose>
                                            <c:when test="${outcome != null}">
                                                ${outcome.outcomeNum}: ${outcome.description}
                                            </c:when>
                                            <c:otherwise>
                                                Outcome ID: ${selectedFCAR.outcomeId}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>

                                    <c:set var="indicators" value="${indicatorMap[selectedFCAR.outcomeId]}" />
                                    <c:set var="indicatorFound" value="false" />
                                    <c:if test="${indicators != null}">
                                        <c:forEach var="indicator" items="${indicators}">
                                            <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                                <c:set var="indicatorFound" value="true" />
                                            </c:if>
                                        </c:forEach>
                                    </c:if>

                                    <c:if test="${indicators == null || empty indicators || indicatorFound == false}">
                                        <p><strong>Indicator ID:</strong> ${selectedFCAR.indicatorId}</p>
                                    </c:if>
                                    <c:if test="${indicators != null}">
                                        <c:forEach var="indicator" items="${indicators}">
                                            <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                                <p><strong>Indicator:</strong> ${selectedFCAR.outcomeId}.${indicator.number}: ${indicator.description}</p>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </div>
                            </c:if>
                            <c:if test="${selectedFCAR.outcomeId != null && not empty selectedFCAR.outcomeId}">
                                <div class="fcar-section">
                                    <h4>Student Learning Outcomes</h4>
                                    <c:set var="outcome" value="${outcomeMap[selectedFCAR.outcomeId]}" />
                                    <p><strong>Outcome:</strong>
                                        <c:choose>
                                            <c:when test="${outcome != null && not empty outcome}">
                                                ${outcome.outcomeNum}: ${outcome.description}
                                            </c:when>
                                            <c:otherwise>
                                                Outcome ID: ${selectedFCAR.outcomeId}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>

                                    <c:set var="indicators" value="${indicatorMap[selectedFCAR.outcomeId]}" />
                                    <c:set var="indicatorFound" value="false" />
                                    <c:forEach var="indicator" items="${indicators}">
                                        <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                           <c:set var="indicatorFound" value="true" />
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${indicators == null || empty indicators || not indicatorFound}">
                                        <p><strong>Indicator ID:</strong> ${selectedFCAR.indicatorId}</p>
                                    </c:if>
                                    <c:forEach var="indicator" items="${indicators}">
                                        <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                            <p><strong>Indicator:</strong> ${selectedFCAR.outcomeId}.${indicator.number}: ${indicator.description}</p>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </c:if>
                            <c:if test="${(assessmentMethods != null && not empty assessmentMethods) || (selectedFCAR != null && selectedFCAR.assessmentMethods != null && not empty selectedFCAR.assessmentMethods)}">
                                <div class="fcar-section">
                                    <h4>Assessment Methods</h4>
                                    <c:choose>
                                        <c:when test="${assessmentMethods != null && not empty assessmentMethods}">
                                            <c:forEach var="method" items="${assessmentMethods}">
                                                <p><strong>${fn:replace(method.key, '_', ' ')}:</strong> ${method.value}</p>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="method" items="${selectedFCAR.assessmentMethods}">
                                                <p><strong>${fn:replace(method.key, '_', ' ')}:</strong> ${method.value}</p>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                            <c:if test="${(studentOutcomes != null && not empty studentOutcomes) || (selectedFCAR != null && selectedFCAR.studentOutcomes != null && not empty selectedFCAR.studentOutcomes)}">
                                <div class="fcar-section">
                                    <h4>Student Outcomes</h4>
                                    <c:choose>
                                        <c:when test="${studentOutcomes != null && not empty studentOutcomes}">
                                            <c:forEach var="outcome" items="${studentOutcomes}">
                                                <p><strong>${fn:replace(outcome.key, '_', ' ')}:</strong> ${outcome.value}</p>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="outcome" items="${selectedFCAR.studentOutcomes}">
                                                <p><strong>${fn:replace(outcome.key, '_', ' ')}:</strong> ${outcome.value}</p>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                            <c:if test="${(improvementActions != null && not empty improvementActions) || (selectedFCAR != null && selectedFCAR.improvementActions != null && not empty selectedFCAR.improvementActions)}">
                                <div class="fcar-section">
                                    <h4>Improvement Actions</h4>
                                    <c:choose>
                                        <c:when test="${improvementActions != null && not empty improvementActions}">
                                            <c:forEach var="act" items="${improvementActions}">
                                                <p><strong>${fn:replace(act.key, '_', ' ')}:</strong> ${act.value}</p>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="act" items="${selectedFCAR.improvementActions}">
                                                <p><strong>${fn:replace(act.key, '_', ' ')}:</strong> ${act.value}</p>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                        </div>
                    </li>
                </ul>
            </c:when>
            <c:when test="${not empty allFCARs}">
                <!-- Display all FCARs when allFCARs is set -->
                <ul class="fcar-list">
                    <c:forEach var="fcar" items="${allFCARs}" varStatus="status">
                        <li class="fcar-item">
                            <div class="fcar-header">
                                <div>
                                    <h3>FCAR #${status.index + 1}: ${fcar.courseCode}</h3>
                                    <p>
                                        <strong>Instructor:</strong>
                                        <%
                                            // Get the instructor for this FCAR
                                            FCAR currentFcar = (FCAR)pageContext.getAttribute("fcar");
                                            User instructor = DisplaySystemController.getInstance().getUser(currentFcar.getInstructorId());
                                            if (instructor != null) {
                                                out.print(instructor.getLastName());
                                            } else {
                                                out.print(currentFcar.getInstructorId());
                                            }
                                        %> |
                                        <strong>Semester:</strong> ${fcar.semester} ${fcar.year} |
                                        <strong>Status:</strong>
                                        <span class="status-badge status-${fn:toLowerCase(fcar.status)}">${fcar.status}</span>
                                    </p>
                                </div>
                                <div>
                                    <button class="btn toggle-details" onclick="toggleDetails('fcar-${fcar.fcarId}')">Show Details</button>
                                    <c:if test="${user.userId == fcar.instructorId || user.roleId == 1}">
                                        <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${fcar.fcarId}" class="btn">Edit FCAR</a>
                                    </c:if>
                                </div>
                            </div>
                            <div id="fcar-${fcar.fcarId}" class="fcar-details" style="display:none;">
                                <div class="fcar-section">
                                    <h4>Actions</h4>
                                    <c:if test="${user.userId == fcar.instructorId || user.roleId == 1}">
                                        <p><strong>Edit Status:</strong>
                                            <c:choose>
                                                <c:when test="${fcar.status == 'Draft' || user.roleId == 1}">Available</c:when>
                                                <c:otherwise>Not available for ${fcar.status} FCARs</c:otherwise>
                                            </c:choose>
                                        </p>
                                    </c:if>
                                    <c:if test="${user.roleId == 1 && fcar.status == 'Submitted'}">
                                        <p><strong>Admin Actions:</strong>
                                            <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${fcar.fcarId}" class="btn">Edit FCAR</a>
                                            Approval pending
                                        </p>
                                    </c:if>
                                </div>
                                <div class="fcar-section">
                                    <h4>FCAR Information</h4>
                                    <p><strong>Course:</strong> ${fcar.courseCode}</p>
                                    <p><strong>Instructor:</strong>
                                                <%
                                            // Get the instructor for this FCAR in the details section
                                            FCAR detailsFcar = (FCAR)pageContext.getAttribute("fcar");
                                            User detailsInstructor = DisplaySystemController.getInstance().getUser(detailsFcar.getInstructorId());
                                            if (detailsInstructor != null) {
                                                out.print(detailsInstructor.getLastName());
                                            } else {
                                                out.print("ID: " + detailsFcar.getInstructorId());
                                            }
                                        %>
                                    <p><strong>Semester/Year:</strong> ${fcar.semester} ${fcar.year}</p>
                                    <p><strong>Status:</strong> <span class="status-badge status-${fn:toLowerCase(fcar.status)}">${fcar.status}</span></p>
                                    <p><strong>Created:</strong> ${fcar.createdAt}</p>
                                    <c:if test="${fcar.updatedAt != null && not empty fcar.updatedAt}"><p><strong>Updated:</strong> ${fcar.updatedAt}</p></c:if>
                                </div>
                                <c:if test="${selectedFCAR != null && selectedFCAR.outcomeId > 0}">
                                    <div class="fcar-section">
                                        <h4>Student Learning Outcomes</h4>

                                        <c:set var="outcome" value="${outcomeMap[selectedFCAR.outcomeId]}" />
                                        <p><strong>Outcome:</strong>
                                            <c:choose>
                                                <c:when test="${outcome != null && not empty outcome}">
                                                    ${outcome.outcomeNum}: ${outcome.description}
                                                </c:when>
                                                <c:otherwise>
                                                    Outcome ID: ${selectedFCAR.outcomeId}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>

                                        <c:set var="indicators" value="${indicatorMap[selectedFCAR.outcomeId]}" />
                                        <c:set var="indicatorFound" value="false" />

                                        <c:if test="${not empty indicators}">
                                            <c:forEach var="indicator" items="${indicators}">
                                                <c:if test="${indicator.indicatorId == selectedFCAR.indicatorId}">
                                                    <c:set var="indicatorFound" value="true" />
                                                    <p><strong>Indicator:</strong> ${selectedFCAR.outcomeId}.${indicator.number}: ${indicator.description}</p>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test="${not indicatorFound}">
                                            <p><strong>Indicator ID:</strong> ${selectedFCAR.indicatorId}</p>
                                        </c:if>
                                    </div>
                                </c:if>

                                <c:if test="${fcar != null && fcar.outcomeId > 0}">
                                    <div class="fcar-section">
                                        <h4>Student Learning Outcomes</h4>

                                        <c:set var="outcome" value="${outcomeMap[fcar.outcomeId]}" />
                                        <p><strong>Outcome:</strong>
                                            <c:choose>
                                                <c:when test="${outcome != null && not empty outcome}">
                                                    ${outcome.outcomeNum}: ${outcome.description}
                                                </c:when>
                                                <c:otherwise>
                                                    Outcome ID: ${fcar.outcomeId}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>

                                        <c:set var="indicators" value="${indicatorMap[fcar.outcomeId]}" />
                                        <c:set var="indicatorFound" value="false" />

                                        <c:if test="${not empty indicators}">
                                            <c:forEach var="indicator" items="${indicators}">
                                                <c:if test="${indicator.indicatorId == fcar.indicatorId}">
                                                    <c:set var="indicatorFound" value="true" />
                                                    <p><strong>Indicator:</strong> ${fcar.outcomeId}.${indicator.number}: ${indicator.description}</p>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test="${not indicatorFound}">
                                            <p><strong>Indicator ID:</strong> ${fcar.indicatorId}</p>
                                        </c:if>
                                    </div>
                                </c:if>
                                <c:if test="${fcar != null && fcar.assessmentMethods != null && not empty fcar.assessmentMethods}">
                                    <div class="fcar-section">
                                        <h4>Assessment Methods</h4>
                                        <c:forEach var="method" items="${fcar.assessmentMethods}">
                                            <p><strong>${fn:replace(method.key, '_', ' ')}:</strong> ${method.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${fcar != null && fcar.studentOutcomes != null && not empty fcar.studentOutcomes}">
                                    <div class="fcar-section">
                                        <h4>Student Outcomes</h4>
                                        <c:forEach var="outcome" items="${fcar.studentOutcomes}">
                                            <p><strong>${fn:replace(outcome.key, '_', ' ')}:</strong> ${outcome.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${fcar != null && fcar.improvementActions != null && not empty fcar.improvementActions}">
                                    <div class="fcar-section">
                                        <h4>Improvement Actions</h4>
                                        <c:forEach var="act" items="${fcar.improvementActions}">
                                            <p><strong>${fn:replace(act.key, '_', ' ')}:</strong> ${act.value}</p>
                                        </c:forEach>
                                    </div>
                                </c:if>
                            </div>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                <p class="no-data">No FCARs found. Please create a new FCAR.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="action-buttons">
        <a href="${dashboardUrl}" class="btn" style="margin-right: 10px;">Back to Dashboard</a>
        <c:set var="paramFcarId" value="${param.fcarId}" />
        <c:if test="${(paramFcarId != null && paramFcarId != '') || (fcarId != null && fcarId != '')}">
            <c:set var="editFcarId" value="${(paramFcarId != null && paramFcarId != '') ? paramFcarId : fcarId}" />
            <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${editFcarId}" class="btn">Edit FCAR</a>
        </c:if>
    </div>

    <!-- Import functionality has been moved to the Settings page -->

    <!-- JavaScript for expanding/collapsing FCAR details -->
    <script>
        function toggleDetails(id) {
            const details = document.getElementById(id);
            const button = event.currentTarget;

            if (details.style.display === "none") {
                details.style.display = "block";
                button.textContent = "Hide Details";
            } else {
                details.style.display = "none";
                button.textContent = "Show Details";
            }
        }

        // Function to toggle indicators when an outcome is deselected
        function toggleIndicators(outcomeId, checked) {
            const indicatorsDiv = document.getElementById(`indicators_${outcomeId}`);
            if (indicatorsDiv) {
                indicatorsDiv.style.display = checked ? 'block' : 'none';

                // If outcome is unchecked, uncheck all its indicators
                if (!checked) {
                    const indicators = document.querySelectorAll(`input[id^="indicator_${outcomeId}_"]`);
                    indicators.forEach(indicator => {
                        indicator.checked = false;
                    });
                }
            }
        }

        // Add event listeners to outcome checkboxes
        document.addEventListener('DOMContentLoaded', function() {
            const outcomeCheckboxes = document.querySelectorAll('input[type="checkbox"][id^="outcome_"]');
            outcomeCheckboxes.forEach(checkbox => {
                checkbox.addEventListener('change', function() {
                    toggleIndicators(this.value, this.checked);
                });
            });
        });
    </script>
</div>

    <script>
        function toggleDetails(id) {
            const el = document.getElementById(id);
            const btn = event.currentTarget;
            if (el.style.display === 'none') {
                el.style.display = 'block'; btn.textContent = 'Hide Details';
            } else {
                el.style.display = 'none'; btn.textContent = 'Show Details';
            }
        }

        // Import functionality has been moved to the Settings page
    </script>
</body>
</html>
