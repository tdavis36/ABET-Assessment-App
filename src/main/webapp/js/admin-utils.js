/**
 * Enhanced Course Selection for FCAR Creation
 *
 * This script implements dynamic course loading based on professor selection
 * and improves the overall FCAR creation experience in the admin interface.
 */

document.addEventListener('DOMContentLoaded', function() {
    // Get the professor select dropdown
    const professorSelect = document.getElementById('professorId');

    // Get the course select dropdown
    const courseSelect = document.getElementById('courseId');

    // Get the dynamic course container
    const dynamicCourseContainer = document.getElementById('dynamicCourseContainer');

    // If the elements don't exist, exit early
    if (!professorSelect || !courseSelect) return;

    // Initialize the course selection to be disabled by default
    courseSelect.disabled = true;

    // Add a placeholder option to the course dropdown if it doesn't exist
    if (!courseSelect.querySelector('option[value=""]')) {
        const placeholderOption = document.createElement('option');
        placeholderOption.value = "";
        placeholderOption.textContent = "Select an Instructor First";
        courseSelect.prepend(placeholderOption);
        courseSelect.selectedIndex = 0;
    }

    // Add event listener to the professor select
    professorSelect.addEventListener('change', function() {
        const professorId = this.value;

        // If no instructor is selected, disable the course dropdown
        if (!professorId) {
            courseSelect.disabled = true;
            courseSelect.querySelector('option[value=""]').textContent = "Select an Instructor First";
            courseSelect.selectedIndex = 0;

            // Clear the outcomes section
            if (dynamicCourseContainer) {
                dynamicCourseContainer.innerHTML = '<p>Please select an instructor and course to see associated outcomes.</p>';
            }

            return;
        }

        // Show loading state
        courseSelect.disabled = true;
        courseSelect.querySelector('option[value=""]').textContent = "Loading courses...";
        courseSelect.selectedIndex = 0;

        // Fetch the professor's courses
        fetch(`${getContextPath()}/admin?action=getProfessorCourses&userId=${professorId}`, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(courses => {
                // Populate the course dropdown with the professor's courses
                populateCourseDropdown(courses);
            })
            .catch(error => {
                console.error('Error fetching professor courses:', error);
                courseSelect.disabled = false;
                courseSelect.querySelector('option[value=""]').textContent = "Error loading courses";
            });
    });

    // Add event listener to the course select
    courseSelect.addEventListener('change', function() {
        const courseId = this.value;

        // If no course is selected, clear the outcomes section
        if (!courseId && dynamicCourseContainer) {
            dynamicCourseContainer.innerHTML = '<p>Please select a course to see associated outcomes.</p>';
            return;
        }

        // If a course is selected, update the outcomes section
        if (courseId && dynamicCourseContainer) {
            updateOutcomesForCourse(courseId);
        }
    });

    /**
     * Populates the course dropdown with the professor's courses
     * @param {Array} courses - Array of course codes
     */
    function populateCourseDropdown(courses) {
        // Clear all options except the first (placeholder)
        while (courseSelect.options.length > 1) {
            courseSelect.remove(1);
        }

        // If no courses available
        if (!courses || courses.length === 0) {
            courseSelect.querySelector('option[value=""]').textContent = "No courses assigned to this instructor";
            courseSelect.disabled = true;

            // Clear the outcomes section
            if (dynamicCourseContainer) {
                dynamicCourseContainer.innerHTML = '<p>This instructor has no assigned courses. Please assign courses to this instructor first.</p>';
            }

            return;
        }

        // Update placeholder text
        courseSelect.querySelector('option[value=""]').textContent = "Select a Course";

        // Sort courses alphabetically
        courses.sort();

        // Get all available courses with details
        const allCourses = getAllCoursesFromPage();

        // Add each course as an option
        courses.forEach(courseCode => {
            const option = document.createElement('option');
            option.value = courseCode;

            // Find the course name if available
            const courseDetails = allCourses.find(c => c.courseCode === courseCode);
            if (courseDetails && courseDetails.courseName) {
                option.textContent = `${courseCode}: ${courseDetails.courseName}`;
            } else {
                option.textContent = courseCode;
            }

            courseSelect.appendChild(option);
        });

        // Enable the course dropdown
        courseSelect.disabled = false;

        // Clear the outcomes section
        if (dynamicCourseContainer) {
            dynamicCourseContainer.innerHTML = '<p>Please select a course to see associated outcomes.</p>';
        }
    }

    /**
     * Updates the outcomes section for the selected course
     * @param {string} courseId - The course code
     */
    function updateOutcomesForCourse(courseId) {
        if (!dynamicCourseContainer) return;

        // Show loading state
        dynamicCourseContainer.innerHTML = '<p>Loading course outcomes...</p>';

        // If we have course outcomes data directly available on the page
        if (typeof courseOutcomes !== 'undefined' && courseOutcomes[courseId]) {
            renderOutcomesForCourse(courseId);
            return;
        }

        // Otherwise, fetch the outcomes for this course
        fetch(`${getContextPath()}/admin?action=getCourseOutcomes&courseId=${courseId}`, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                // Store the outcomes in the global courseOutcomes object if it exists
                if (typeof window.courseOutcomes === 'undefined') {
                    window.courseOutcomes = {};
                }
                window.courseOutcomes[courseId] = data.outcomeIds || [];

                // Store outcome descriptions if available
                if (data.outcomeDescriptions && typeof window.dynamicOutcomeDescriptions === 'undefined') {
                    window.dynamicOutcomeDescriptions = {};
                }

                if (data.outcomeDescriptions) {
                    Object.assign(window.dynamicOutcomeDescriptions, data.outcomeDescriptions);
                }

                renderOutcomesForCourse(courseId);
            })
            .catch(error => {
                console.error('Error fetching course outcomes:', error);
                dynamicCourseContainer.innerHTML = '<p class="error-message">Error loading course outcomes. Please try again.</p>';
            });
    }

    /**
     * Renders the outcomes for the selected course
     * @param {string} courseId - The course code
     */
    function renderOutcomesForCourse(courseId) {
        if (!dynamicCourseContainer) return;

        // If we don't have courseOutcomes defined or for this course
        if (typeof courseOutcomes === 'undefined' || !courseOutcomes[courseId] || courseOutcomes[courseId].length === 0) {
            dynamicCourseContainer.innerHTML = '<p>No outcomes are assigned to this course.</p>';
            return;
        }

        // Generate HTML for outcomes
        let html = `
            <h3>Outcomes for ${courseId}</h3>
            <p>The following outcomes are automatically assigned based on the course. You can select which indicators to include for each outcome.</p>
        `;

        // For each outcome assigned to the course
        courseOutcomes[courseId].forEach(outcomeId => {
            html += createOutcomeHTML(outcomeId, courseId);
        });

        // Update the container
        dynamicCourseContainer.innerHTML = html;

        // Add event listeners to outcome checkboxes
        const outcomeCheckboxes = dynamicCourseContainer.querySelectorAll('input[type="checkbox"][id^="outcome_"]');
        outcomeCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                const outcomeId = this.value;
                toggleIndicators(outcomeId, this.checked);
                updateSelectedOutcomes();
            });

            // Trigger the change event to initialize indicator visibility
            checkbox.dispatchEvent(new Event('change'));
        });
    }

    /**
     * Creates HTML for an outcome and its indicators
     * @param {number} outcomeId - The outcome ID
     * @param {string} courseId - The course code
     * @returns {string} - HTML string
     */
    function createOutcomeHTML(outcomeId, courseId) {
        // Get the outcome description
        let outcomeDescription = "Unknown Outcome";
        if (typeof dynamicOutcomeDescriptions !== 'undefined' && dynamicOutcomeDescriptions[outcomeId]) {
            outcomeDescription = dynamicOutcomeDescriptions[outcomeId];
        } else if (typeof outcomeDescriptions !== 'undefined' && outcomeDescriptions[outcomeId]) {
            outcomeDescription = outcomeDescriptions[outcomeId];
        }

        // Start building the HTML
        let html = `
            <div class="outcome-container">
                <div class="outcome-checkbox-container">
                    <input type="checkbox" id="${courseId}_outcome_${outcomeId}" 
                           name="selectedOutcome_${outcomeId}" value="${outcomeId}" 
                           class="outcome-checkbox" checked />
                    <label for="${courseId}_outcome_${outcomeId}" class="outcome-label">
                        Outcome ${outcomeId}: ${outcomeDescription}
                    </label>
                </div>
        `;

        // Add indicators section
        html += `<div id="${courseId}_indicators_${outcomeId}" class="indicators-container">`;

        // If we have indicator data
        if (typeof indicators !== 'undefined' && indicators[outcomeId]) {
            indicators[outcomeId].forEach((indicator, index) => {
                // Extract indicator number and description
                const indicatorParts = indicator.split(' ');
                const indicatorNumber = indicatorParts[0];
                const indicatorDescription = indicatorParts.slice(1).join(' ');

                html += `
                    <div class="indicator-container">
                        <input type="checkbox" id="${courseId}_indicator_${indicatorNumber.replace('.', '_')}"
                               name="indicator_${indicatorNumber.replace('.', '_')}"
                               value="${indicatorNumber}" checked />
                        <label for="${courseId}_indicator_${indicatorNumber.replace('.', '_')}">
                            ${indicator}
                        </label>
                    </div>
                `;
            });
        } else {
            html += `<p>No indicators available for this outcome.</p>`;
        }

        html += `</div></div>`;
        return html;
    }

    /**
     * Toggles visibility of indicators for an outcome
     * @param {number} outcomeId - The outcome ID
     * @param {boolean} checked - Whether the outcome is checked
     */
    function toggleIndicators(outcomeId, checked) {
        // Get the active courseId
        const courseId = courseSelect.value;
        if (!courseId) return;

        const indicatorsDiv = document.getElementById(`${courseId}_indicators_${outcomeId}`);
        if (indicatorsDiv) {
            indicatorsDiv.style.display = checked ? 'block' : 'none';

            // Toggle indicator checkboxes
            const indicators = indicatorsDiv.querySelectorAll('input[type="checkbox"]');
            indicators.forEach(indicator => {
                indicator.disabled = !checked;
                if (!checked) {
                    indicator.checked = false;
                }
            });
        }
    }

    /**
     * Updates the hidden input with selected outcomes
     */
    function updateSelectedOutcomes() {
        const courseId = courseSelect.value;
        if (!courseId) return;

        const selectedOutcomes = [];
        const outcomeCheckboxes = document.querySelectorAll(`input[type="checkbox"][id^="${courseId}_outcome_"]`);

        outcomeCheckboxes.forEach(checkbox => {
            if (checkbox.checked) {
                selectedOutcomes.push(checkbox.value);
            }
        });

        const selectedOutcomesInput = document.getElementById('selectedOutcomesInput');
        if (selectedOutcomesInput) {
            selectedOutcomesInput.value = selectedOutcomes.join(',');
        }
    }

    /**
     * Gets all courses available on the page
     * @returns {Array} - Array of course objects with courseCode and courseName
     */
    function getAllCoursesFromPage() {
        const courses = [];

        // Try to get courses from the global courses array
        if (typeof window.allCourses !== 'undefined') {
            return window.allCourses;
        }

        // Alternative: Extract from DOM elements on the page
        try {
            // Method 1: Try to get courses from any existing course dropdowns
            const courseOptions = document.querySelectorAll('select[id="courseId"] option, select[name="courseId"] option');

            courseOptions.forEach(option => {
                if (option.value && option.value !== "") {
                    const text = option.textContent;
                    let courseName = "";

                    // Extract course name if format is "CODE: NAME"
                    if (text.includes(':')) {
                        courseName = text.split(':')[1].trim();
                    }

                    courses.push({
                        courseCode: option.value,
                        courseName: courseName
                    });
                }
            });

            // Method 2: Try to find courses in a data element
            if (courses.length === 0) {
                const courseDataElement = document.getElementById('courseData');
                if (courseDataElement && courseDataElement.textContent) {
                    try {
                        const courseData = JSON.parse(courseDataElement.textContent);
                        return courseData;
                    } catch (e) {
                        console.warn('Failed to parse course data from element:', e);
                    }
                }
            }

            // Method 3: Make an AJAX request to get course list
            if (courses.length === 0) {
                // This is async so won't have immediate results
                fetch(`${getContextPath()}/admin?action=getCourseList`, {
                    method: 'GET',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                })
                    .then(response => response.json())
                    .then(data => {
                        window.allCourses = data;
                    })
                    .catch(error => console.error('Failed to fetch course list:', error));
            }
        } catch (e) {
            console.warn('Error extracting courses from page:', e);
        }

        return courses;
    }

    /**
     * Gets the context path for the application
     * @returns {string} - The context path
     */
    function getContextPath() {
        return window.contextPath || document.body.getAttribute('data-context-path') || '';
    }
});
