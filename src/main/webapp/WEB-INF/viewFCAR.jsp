<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Set" %>
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
        /* Additional styles for filter controls */
        .filter-container {
            background-color: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 20px;
        }

        .filter-form {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            align-items: flex-end;
        }

        .filter-group {
            flex: 1;
            min-width: 150px;
        }

        .filter-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            font-size: 0.9rem;
        }

        .filter-actions {
            display: flex;
            gap: 10px;
            align-items: flex-end;
            margin-top: 10px;
        }

        .filter-toggle {
            background-color: var(--primary);
            color: white;
            border: none;
            border-radius: 4px;
            padding: 8px 12px;
            cursor: pointer;
            margin-bottom: 15px;
        }

        .filter-toggle:hover {
            background-color: var(--primary-dark);
        }

        /* Explicitly define filter-count styles to ensure they are applied */
        .filter-count {
            background-color: #181313 !important;
            border: 1px solid #1e201f !important;
            padding: 10px !important;
            margin-bottom: 15px !important;
            border-radius: 4px !important;
            font-weight: bold !important;
            color: #e0e0e0 !important;
        }

        @media (max-width: 768px) {
            .filter-form {
                flex-direction: column;
            }

            .filter-group {
                width: 100%;
            }
        }
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

        <%
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

        // Initialize FCARRepository
        FCARRepository repo = new FCARRepository();

        // Check if we're viewing a specific FCAR
        FCAR selectedFCAR = (FCAR) request.getAttribute("selectedFCAR");

        // Determine user role
        User user = (User) session.getAttribute("user");
        String dashboardUrl;
        if (user instanceof Admin) {
            dashboardUrl = request.getContextPath() + "/admin";
        } else if (user instanceof Professor) {
            dashboardUrl = request.getContextPath() + "/professor";
        } else {
            dashboardUrl = request.getContextPath() + "/";
        }

        // Always get all FCARs for filtering options, even when viewing a specific FCAR
        List<FCAR> allFCARs;
        if (request.getAttribute("allFCARs") != null) {
            // Use the allFCARs that was already set
            allFCARs = (List<FCAR>) request.getAttribute("allFCARs");
        } else {
            // Fetch all FCARs based on user role
            allFCARs = (user instanceof Admin)
                    ? repo.findAll()
                    : repo.findByInstructorId(user.getUserId());

            // Set the attribute if we're not viewing a specific FCAR
            if (selectedFCAR == null) {
                request.setAttribute("allFCARs", allFCARs);
            }
        }

        // Always collect unique values for filters
        Set<String> courseCodesSet = new HashSet<>();
        Set<String> semestersSet = new HashSet<>();
        Set<Integer> yearsSet = new HashSet<>();
        Set<String> statusesSet = new HashSet<>();

        for (FCAR fcar : allFCARs) {
            courseCodesSet.add(fcar.getCourseCode());
            semestersSet.add(fcar.getSemester());
            yearsSet.add(fcar.getYear());
            statusesSet.add(fcar.getStatus());
        }

        // Convert sets to lists for easier iteration in JSP
        List<String> courseCodes = new ArrayList<>(courseCodesSet);
        List<String> semesters = new ArrayList<>(semestersSet);
        List<Integer> years = new ArrayList<>(yearsSet);
        List<String> statuses = new ArrayList<>(statusesSet);

        // Sort the lists for better display
        java.util.Collections.sort(courseCodes);
        java.util.Collections.sort(semesters);
        java.util.Collections.sort(years);
        java.util.Collections.sort(statuses);

        // Set attributes for filter controls
        request.setAttribute("courseCodes", courseCodes);
        request.setAttribute("semesters", semesters);
        request.setAttribute("years", years);
        request.setAttribute("statuses", statuses);

        // If we're viewing a specific FCAR, set the fcarId parameter for the Edit button
        if (selectedFCAR != null) {
            request.setAttribute("fcarId", selectedFCAR.getFcarId());
        }
    %>

    <div class="section">
        <button type="button" class="filter-toggle" id="toggleFilterBtn">Show Filter Options</button>

        <div class="filter-container" id="filterContainer" style="display: none;">
            <h3>Filter FCARs</h3>

            <c:choose>
                <c:when test="${not empty selectedFCAR}">
                    <div class="filter-message">
                        <p>You are currently viewing a specific FCAR. To apply filters:</p>
                        <a href="${pageContext.request.contextPath}/view?action=viewAll" class="btn">View All FCARs</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <%-- Normal filter form when viewing all FCARs --%>
                    <form id="filterForm" class="filter-form">
                        <div class="filter-group">
                            <label for="filterCourse">Course:</label>
                            <select id="filterCourse" name="course">
                                <option value="">All Courses</option>
                                <c:forEach var="course" items="${courseCodes}">
                                    <option value="${course}">${course}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="filter-group">
                            <label for="filterSemester">Semester:</label>
                            <select id="filterSemester" name="semester">
                                <option value="">All Semesters</option>
                                <c:forEach var="semester" items="${semesters}">
                                    <option value="${semester}">${semester}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="filter-group">
                            <label for="filterYear">Year:</label>
                            <select id="filterYear" name="year">
                                <option value="">All Years</option>
                                <c:forEach var="year" items="${years}">
                                    <option value="${year}">${year}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="filter-group">
                            <label for="filterStatus">Status:</label>
                            <select id="filterStatus" name="status">
                                <option value="">All Statuses</option>
                                <c:forEach var="status" items="${statuses}">
                                    <option value="${status}">${status}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="filter-group">
                            <label for="searchText">Search:</label>
                            <input type="text" id="searchText" name="search" placeholder="Search in FCARs...">
                        </div>

                        <div class="filter-actions">
                            <button type="button" class="btn" onclick="applyFilters()">Apply Filters</button>
                            <button type="button" class="btn" onclick="resetFilters()">Reset</button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- FIXED: Added direct styling to the filter count -->
        <c:if test="${empty selectedFCAR && not empty allFCARs}">
            <div id="filterCount" class="filter-count" style="background-color: #181313; border: 1px solid #aad9c5; padding: 10px; margin-bottom: 15px; border-radius: 4px; font-weight: bold; color: #333;">
                Showing all ${fn:length(allFCARs)} FCARs
            </div>
        </c:if>

        <h2>
            <c:choose>
                <c:when test="${not empty selectedFCAR}">
                    FCAR Details
                </c:when>
                <c:otherwise>
                    Existing FCARs
                </c:otherwise>
            </c:choose>
        </h2>

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
                                <!-- FIXED: Added this parameter and return false to toggleDetails -->
                                <button class="btn toggle-details" onclick="toggleDetails('fcar-${selectedFCAR.fcarId}', this); return false;">Show Details</button>
                                <c:if test="${user.userId == selectedFCAR.instructorId || user.roleId == 1}">
                                    <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${selectedFCAR.fcarId}" class="btn">Edit FCAR</a>
                                </c:if>
                            </div>
                        </div>

                        <div id="fcar-${selectedFCAR.fcarId}" class="fcar-details" style="display:none;">
                            <div class="fcar-section">
                                <h4>FCAR ID: ${selectedFCAR.fcarId}</h4>
                                <p>Course: ${selectedFCAR.courseCode}</p>
                                <p>Instructor ID: ${selectedFCAR.instructorId}</p>
                                <p>Semester: ${selectedFCAR.semester} ${selectedFCAR.year}</p>
                                <p>Status: ${selectedFCAR.status}</p>
                            </div>

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

                            <c:if test="${(assessmentMethods != null && not empty assessmentMethods) || (selectedFCAR != null && selectedFCAR.assessmentMethods != null && not empty selectedFCAR.assessmentMethods)}">
                                <div class="fcar-section">
                                    <h4>Assessment Methods</h4>

                                        <%-- Use appropriate map based on what's available --%>
                                    <c:set var="methodsMap" value="${assessmentMethods != null && not empty assessmentMethods ? assessmentMethods : selectedFCAR.assessmentMethods}" />

                                        <%-- Display Assessment Description and Work Used in a structured way --%>
                                    <div class="assessment-details">
                                        <c:if test="${methodsMap['assessmentDescription'] != null}">
                                            <div class="form-group">
                                                <strong>Assessment Description:</strong>
                                                <div class="content-box">${methodsMap['assessmentDescription']}</div>
                                            </div>
                                        </c:if>

                                        <c:if test="${methodsMap['workUsed'] != null}">
                                            <div class="form-group">
                                                <strong>Work Used:</strong>
                                                <div class="content-box">${methodsMap['workUsed']}</div>
                                            </div>
                                        </c:if>

                                        <c:if test="${methodsMap['targetGoal'] != null}">
                                            <div class="form-group">
                                                <strong>Target Goal:</strong>
                                                <div class="content-box">${methodsMap['targetGoal']}%</div>
                                            </div>
                                        </c:if>
                                    </div>

                                        <%-- Rest of the assessment methods content - this is unchanged --%>
                                        <%-- ... existing content ... --%>
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
                <ul class="fcar-list" id="fcarList">
                    <c:forEach var="fcar" items="${allFCARs}" varStatus="status">
                        <li class="fcar-item"
                            data-course="${fcar.courseCode}"
                            data-semester="${fcar.semester}"
                            data-year="${fcar.year}"
                            data-status="${fcar.status}">
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
                                    <!-- FIXED: Added this parameter and return false to toggleDetails -->
                                    <button class="btn toggle-details" onclick="toggleDetails('fcar-${fcar.fcarId}', this); return false;">Show Details</button>
                                    <c:if test="${user.userId == fcar.instructorId || user.roleId == 1}">
                                        <a href="${pageContext.request.contextPath}/view?action=edit&fcarId=${fcar.fcarId}" class="btn">Edit FCAR</a>
                                    </c:if>
                                </div>
                            </div>

                            <!-- FIXED: Added verification content at the beginning of the details section -->
                            <div id="fcar-${fcar.fcarId}" class="fcar-details" style="display:none;">
                                <div class="fcar-section">
                                    <h4>FCAR ID: ${fcar.fcarId}</h4>
                                    <p>Course: ${fcar.courseCode}</p>
                                    <p>Instructor ID: ${fcar.instructorId}</p>
                                    <p>Semester: ${fcar.semester} ${fcar.year}</p>
                                    <p>Status: ${fcar.status}</p>
                                </div>

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
                                    </p>
                                    <p><strong>Semester/Year:</strong> ${fcar.semester} ${fcar.year}</p>
                                    <p><strong>Status:</strong> <span class="status-badge status-${fn:toLowerCase(fcar.status)}">${fcar.status}</span></p>
                                    <p><strong>Created:</strong> ${fcar.createdAt}</p>
                                    <c:if test="${fcar.updatedAt != null && not empty fcar.updatedAt}"><p><strong>Updated:</strong> ${fcar.updatedAt}</p></c:if>
                                </div>

                                <c:if test="${fcar != null && fcar.assessmentMethods != null && not empty fcar.assessmentMethods}">
                                    <div class="fcar-section">
                                        <h4>Assessment Methods</h4>

                                            <%-- Display Assessment Description and Work Used in a structured way --%>
                                        <div class="assessment-details">
                                            <c:if test="${fcar.assessmentMethods['assessmentDescription'] != null}">
                                                <div class="form-group">
                                                    <strong>Assessment Description:</strong>
                                                    <div class="content-box">${fcar.assessmentMethods['assessmentDescription']}</div>
                                                </div>
                                            </c:if>

                                            <c:if test="${fcar.assessmentMethods['workUsed'] != null}">
                                                <div class="form-group">
                                                    <strong>Work Used:</strong>
                                                    <div class="content-box">${fcar.assessmentMethods['workUsed']}</div>
                                                </div>
                                            </c:if>

                                            <c:if test="${fcar.assessmentMethods['targetGoal'] != null}">
                                                <div class="form-group">
                                                    <strong>Target Goal:</strong>
                                                    <div class="content-box">${fcar.assessmentMethods['targetGoal']}%</div>
                                                </div>
                                            </c:if>
                                        </div>

                                            <%-- Rest of the assessment methods content - this is unchanged --%>
                                            <%-- ... existing content ... --%>
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

    <!-- JavaScript for expanding/collapsing FCAR details and filtering -->
    <script>
        // FIXED: Improved toggleDetails function with better debugging
        function toggleDetails(detailsId, buttonElement) {
            console.log("Toggle details called for:", detailsId);
            const detailsElement = document.getElementById(detailsId);

            if (!detailsElement) {
                console.error("Could not find details element with ID:", detailsId);
                return;
            }

            console.log("Details element found:", detailsElement);
            console.log("Details content length:", detailsElement.innerHTML.trim().length);
            console.log("Details display style:", detailsElement.style.display);

            // Determine the button that was clicked
            let button = buttonElement;
            if (!button && typeof event !== 'undefined' && event.currentTarget) {
                button = event.currentTarget;
            }
            if (!button) {
                button = document.querySelector(`button[onclick*="'${detailsId}'"]`);
            }

            console.log("Button element:", button);

            // Explicitly set display instead of toggling
            if (detailsElement.style.display === "none" || detailsElement.style.display === "") {
                // Show details
                detailsElement.style.display = "block";
                // Update button text if button exists
                if (button) {
                    button.textContent = "Hide Details";
                }
                console.log("Details explicitly shown");
            } else {
                // Hide details
                detailsElement.style.display = "none";
                // Update button text if button exists
                if (button) {
                    button.textContent = "Show Details";
                }
                console.log("Details explicitly hidden");
            }
        }

        document.addEventListener('DOMContentLoaded', function() {
            // FIXED: Log all details elements to check their content
            document.querySelectorAll('.fcar-details').forEach(function(details) {
                console.log("Details element ID:", details.id);
                console.log("Details content length:", details.innerHTML.trim().length);
                console.log("First 100 chars of content:", details.innerHTML.trim().substring(0, 100));
            });

            const toggleFilterBtn = document.getElementById('toggleFilterBtn');
            const filterContainer = document.getElementById('filterContainer');

            // Retrieve saved filter state from localStorage if available
            const savedFilterState = localStorage.getItem('fcarFilterVisible');

            if (toggleFilterBtn && filterContainer) {
                // Initialize based on saved state if available
                if (savedFilterState === 'true') {
                    filterContainer.style.display = "block";
                    toggleFilterBtn.textContent = "Hide Filter Options";
                }

                toggleFilterBtn.addEventListener('click', function() {
                    if (filterContainer.style.display === "none") {
                        filterContainer.style.display = "block";
                        toggleFilterBtn.textContent = "Hide Filter Options";
                        localStorage.setItem('fcarFilterVisible', 'true');
                    } else {
                        filterContainer.style.display = "none";
                        toggleFilterBtn.textContent = "Show Filter Options";
                        localStorage.setItem('fcarFilterVisible', 'false');
                    }
                });
            }

            // Keep filter settings when navigating between pages
            if (window.location.href.includes('action=viewAll')) {
                // If navigating from a specific FCAR to the list view,
                // try to restore any previously applied filters
                restoreFilterState();
            }

            // Debug and enhance edit buttons
            const editButtons = document.querySelectorAll('a[href*="action=edit"]');
            console.log('Found ' + editButtons.length + ' edit buttons');

            editButtons.forEach(function(button, index) {
                // Ensure the href doesn't have any malformed parts
                const href = button.getAttribute('href');
                console.log('Edit button ' + index + ' href: ' + href);

                // Fix any potential issues with the edit URLs
                if (href && href.includes('fcarId=null')) {
                    // If we detect a null fcarId, try to fix it
                    console.warn('Found edit button with null fcarId, attempting to fix');

                    // Try to find the correct fcarId from the closest parent FCAR item
                    const fcarItem = button.closest('.fcar-item');
                    if (fcarItem) {
                        const detailsId = fcarItem.querySelector('.fcar-details').id;
                        if (detailsId) {
                            // Extract fcarId from the details element ID (format: fcar-{id})
                            const fcarId = detailsId.split('-')[1];
                            if (fcarId) {
                                // Fix the href
                                button.href = href.replace('fcarId=null', 'fcarId=' + fcarId);
                                console.log('Fixed edit button href: ' + button.href);
                            }
                        }
                    }
                }

                // Add click event listener for debugging
                button.addEventListener('click', function(event) {
                    // Don't prevent default behavior, just log the click
                    console.log('Edit button clicked: ' + button.getAttribute('href'));
                });
            });

            // FIXED: Also check styling of the filter count element
            const filterCount = document.getElementById('filterCount');
            if (filterCount) {
                console.log("Filter count element found:", filterCount);
                console.log("Filter count class:", filterCount.className);
                console.log("Filter count style:", filterCount.getAttribute('style'));

                // Ensure it has the correct class and style
                if (!filterCount.classList.contains('filter-count')) {
                    filterCount.classList.add('filter-count');
                    console.log("Added filter-count class to the element");
                }

                // Force apply styles directly if needed
                filterCount.style.backgroundColor = '#e6f4ee';
                filterCount.style.border = '1px solid #aad9c5';
                filterCount.style.padding = '10px';
                filterCount.style.marginBottom = '15px';
                filterCount.style.borderRadius = '4px';
                filterCount.style.fontWeight = 'bold';
                filterCount.style.color = '#333';
            }

            // Add hook to save filter state when clicking on FCAR links
            const fcarLinks = document.querySelectorAll('a[href*="fcarId"]');
            fcarLinks.forEach(link => {
                link.addEventListener('click', saveFilterState);
            });
        });

        // Save filter state before navigating away
        function saveFilterState() {
            if (document.getElementById('filterForm')) {
                const filterState = {
                    course: document.getElementById('filterCourse').value,
                    semester: document.getElementById('filterSemester').value,
                    year: document.getElementById('filterYear').value,
                    status: document.getElementById('filterStatus').value,
                    search: document.getElementById('searchText').value
                };
                localStorage.setItem('fcarFilterValues', JSON.stringify(filterState));
            }
        }

        // Restore filter state when returning to the list view
        function restoreFilterState() {
            const savedFilters = localStorage.getItem('fcarFilterValues');
            if (savedFilters) {
                try {
                    const filterValues = JSON.parse(savedFilters);

                    // Set form values
                    if (document.getElementById('filterCourse')) {
                        document.getElementById('filterCourse').value = filterValues.course || '';
                    }
                    if (document.getElementById('filterSemester')) {
                        document.getElementById('filterSemester').value = filterValues.semester || '';
                    }
                    if (document.getElementById('filterYear')) {
                        document.getElementById('filterYear').value = filterValues.year || '';
                    }
                    if (document.getElementById('filterStatus')) {
                        document.getElementById('filterStatus').value = filterValues.status || '';
                    }
                    if (document.getElementById('searchText')) {
                        document.getElementById('searchText').value = filterValues.search || '';
                    }

                    // Apply the filters immediately if any are set
                    if (filterValues.course || filterValues.semester ||
                        filterValues.year || filterValues.status || filterValues.search) {
                        applyFilters();
                    }
                } catch (e) {
                    console.error("Error restoring filter state:", e);
                    localStorage.removeItem('fcarFilterValues');
                }
            }
        }

        // Apply filters to FCAR list
        function applyFilters() {
            const course = document.getElementById('filterCourse').value;
            const semester = document.getElementById('filterSemester').value;
            const year = document.getElementById('filterYear').value;
            const status = document.getElementById('filterStatus').value;
            const searchText = document.getElementById('searchText').value.toLowerCase();

            const fcarItems = document.querySelectorAll('#fcarList .fcar-item');
            let visibleCount = 0;

            fcarItems.forEach(function(item) {
                const itemCourse = item.getAttribute('data-course');
                const itemSemester = item.getAttribute('data-semester');
                const itemYear = item.getAttribute('data-year');
                const itemStatus = item.getAttribute('data-status');
                const itemText = item.innerText.toLowerCase();

                // Check if item matches all selected filters
                const matchesCourse = !course || itemCourse === course;
                const matchesSemester = !semester || itemSemester === semester;
                const matchesYear = !year || itemYear === year;
                const matchesStatus = !status || itemStatus === status;
                const matchesSearch = !searchText || itemText.includes(searchText);

                // Show or hide based on filter matches
                if (matchesCourse && matchesSemester && matchesYear && matchesStatus && matchesSearch) {
                    item.style.display = '';
                    visibleCount++;
                } else {
                    item.style.display = 'none';
                }
            });

            // Update the filter count display
            const filterCount = document.getElementById('filterCount');
            if (filterCount) {
                if (visibleCount === fcarItems.length) {
                    filterCount.textContent = `Showing all ${visibleCount} FCARs`;
                } else {
                    filterCount.textContent = `Showing ${visibleCount} of ${fcarItems.length} FCARs`;
                }

                // FIXED: Force apply styles to ensure proper display
                filterCount.style.backgroundColor = '#e6f4ee';
                filterCount.style.border = '1px solid #aad9c5';
                filterCount.style.padding = '10px';
                filterCount.style.marginBottom = '15px';
                filterCount.style.borderRadius = '4px';
                filterCount.style.fontWeight = 'bold';
                filterCount.style.color = '#333';
            }

            // Save filter state
            saveFilterState();
        }

        // Reset all filters
        function resetFilters() {
            // Reset all filter inputs
            document.getElementById('filterCourse').value = '';
            document.getElementById('filterSemester').value = '';
            document.getElementById('filterYear').value = '';
            document.getElementById('filterStatus').value = '';
            document.getElementById('searchText').value = '';

            // Show all FCAR items
            const fcarItems = document.querySelectorAll('#fcarList .fcar-item');
            fcarItems.forEach(function(item) {
                item.style.display = '';
            });

            // Update the filter count display
            const filterCount = document.getElementById('filterCount');
            if (filterCount) {
                filterCount.textContent = `Showing all ${fcarItems.length} FCARs`;

                // FIXED: Force apply styles to ensure proper display
                filterCount.style.backgroundColor = '#e6f4ee';
                filterCount.style.border = '1px solid #aad9c5';
                filterCount.style.padding = '10px';
                filterCount.style.marginBottom = '15px';
                filterCount.style.borderRadius = '4px';
                filterCount.style.fontWeight = 'bold';
                filterCount.style.color = '#333';
            }

            // Clear saved filter state
            localStorage.removeItem('fcarFilterValues');
        }
    </script>
</div>
</body>
</html>