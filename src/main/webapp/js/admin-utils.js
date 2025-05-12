/**
 * Enhanced Course Outcomes Display for FCAR Creation
 *
 * This script fixes the issue where all outcomes are shown regardless of the selected course.
 * It ensures that only outcomes linked to the selected course are displayed.
 */

document.addEventListener('DOMContentLoaded', function() {
    // Get the professor select dropdown
    const professorSelect = document.getElementById('professorId');

    // Get the course select dropdown
    const courseSelect = document.getElementById('courseId');

    // Get the dynamic course container where outcomes will be displayed
    const dynamicCourseContainer = document.getElementById('dynamicCourseContainer');

    // If the elements don't exist, exit early
    if (!professorSelect || !courseSelect || !dynamicCourseContainer) {
        console.warn('Required elements for course outcomes not found');
        return;
    }

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
            dynamicCourseContainer.innerHTML = '<p>Please select an instructor and course to see associated outcomes.</p>';
            return;
        }

        // Show loading state
        courseSelect.disabled = true;
        courseSelect.querySelector('option[value=""]').textContent = "Loading courses...";
        courseSelect.selectedIndex = 0;

        // Get the context path
        const contextPath = window.contextPath || '';

        // Fetch the professor's courses
        fetch(`${contextPath}/admin?action=getProfessorCourses&userId=${professorId}`, {
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
                // Clear all options except the first (placeholder)
                while (courseSelect.options.length > 1) {
                    courseSelect.remove(1);
                }

                // If no courses available
                if (!courses || courses.length === 0) {
                    courseSelect.querySelector('option[value=""]').textContent = "No courses assigned to this instructor";
                    courseSelect.disabled = true;

                    // Clear the outcomes section
                    dynamicCourseContainer.innerHTML = '<p>This instructor has no assigned courses. Please assign courses to this instructor first.</p>';
                    return;
                }

                // Update placeholder text
                courseSelect.querySelector('option[value=""]').textContent = "Select a Course";

                // Sort courses alphabetically
                courses.sort();

                // Add each course as an option
                courses.forEach(courseCode => {
                    const option = document.createElement('option');
                    option.value = courseCode;

                    // Find course name if available
                    const courseDetails = findCourseDetails(courseCode);
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
                dynamicCourseContainer.innerHTML = '<p>Please select a course to see associated outcomes.</p>';
            })
            .catch(error => {
                console.error('Error fetching professor courses:', error);
                courseSelect.disabled = false;
                courseSelect.querySelector('option[value=""]').textContent = "Error loading courses";
            });
    });

    // Add event listener to the course select
    courseSelect.addEventListener('change', function() {
        const courseCode = this.value;

        // If no course is selected, clear the outcomes section
        if (!courseCode) {
            dynamicCourseContainer.innerHTML = '<p>Please select a course to see associated outcomes.</p>';
            // Clear the hidden input for selected outcomes
            document.getElementById('selectedOutcomesInput').value = '';
            return;
        }

        // Show loading message
        dynamicCourseContainer.innerHTML = '<p>Loading outcomes for course ' + courseCode + '...</p>';

        // Check if we have course outcomes data in the global variable
        if (typeof courseOutcomes !== 'undefined' && courseOutcomes[courseCode]) {
            renderCourseOutcomes(courseCode);
        } else {
            // If not, fetch them from the server
            const contextPath = window.contextPath || '';

            fetch(`${contextPath}/admin?action=getCourseOutcomes&courseId=${courseCode}`, {
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
                    // Initialize courseOutcomes if not already defined
                    if (typeof window.courseOutcomes === 'undefined') {
                        window.courseOutcomes = {};
                    }

                    // Store the outcomes
                    window.courseOutcomes[courseCode] = data.outcomeIds || [];

                    // Update any outcome descriptions
                    if (data.outcomeDescriptions && typeof window.dynamicOutcomeDescriptions === 'undefined') {
                        window.dynamicOutcomeDescriptions = {};
                    }

                    if (data.outcomeDescriptions) {
                        Object.assign(window.dynamicOutcomeDescriptions, data.outcomeDescriptions);
                    }

                    renderCourseOutcomes(courseCode);
                })
                .catch(error => {
                    console.error('Error fetching course outcomes:', error);
                    dynamicCourseContainer.innerHTML =
                        '<p class="error-message">Error loading outcomes for course ' + courseCode + '. ' + error.message + '</p>';
                });
        }
    });

    /**
     * Renders the outcomes for the selected course
     * @param {string} courseCode - The course code
     */
    function renderCourseOutcomes(courseCode) {
        // If we don't have courseOutcomes defined or none for this course
        if (typeof courseOutcomes === 'undefined' || !courseOutcomes[courseCode] || courseOutcomes[courseCode].length === 0) {
            dynamicCourseContainer.innerHTML = '<p>No outcomes are assigned to this course.</p>';
            // Clear the hidden input for selected outcomes
            document.getElementById('selectedOutcomesInput').value = '';
            return;
        }

        // Generate HTML for outcomes
        let html = `
            <h3>Outcomes for ${courseCode}</h3>
            <p>The following outcomes are automatically assigned based on the course. 
            You can select which indicators to include for each outcome.</p>
            <div id="dynamicCourseOutcomes">
        `;

        // For each outcome assigned to the course
        const selectedOutcomes = [];
        courseOutcomes[courseCode].forEach(outcomeId => {
            selectedOutcomes.push(outcomeId);

            // Get outcome description
            let outcomeDescription = "Unknown Outcome";
            if (typeof dynamicOutcomeDescriptions !== 'undefined' && dynamicOutcomeDescriptions[outcomeId]) {
                outcomeDescription = dynamicOutcomeDescriptions[outcomeId];
            } else if (typeof outcomeDescriptions !== 'undefined' && outcomeDescriptions[outcomeId]) {
                outcomeDescription = outcomeDescriptions[outcomeId];
            }

            html += `
                <div class="outcome-container">
                    <div class="outcome-checkbox-container">
                        <input type="checkbox" id="${courseCode}_outcome_${outcomeId}" 
                               name="selectedOutcome_${outcomeId}" value="${outcomeId}" 
                               class="outcome-checkbox" checked />
                        <label for="${courseCode}_outcome_${outcomeId}" class="outcome-label">
                            Outcome ${outcomeId}: ${outcomeDescription}
                        </label>
                    </div>
                    
                    <div class="indicators-label">Select Indicators:</div>
                    
                    <div id="${courseCode}_indicators_${outcomeId}" class="indicators-container">
            `;

            // Add indicators for this outcome
            if (typeof indicators !== 'undefined' && indicators[outcomeId]) {
                indicators[outcomeId].forEach((indicator, index) => {
                    // Extract indicator number (e.g., "1.1" from "1.1 Student can...")
                    const indicatorParts = indicator.split(' ');
                    const indicatorNumber = indicatorParts[0];
                    const indicatorDescription = indicatorParts.slice(1).join(' ');

                    html += `
                        <div class="indicator-container">
                            <input type="checkbox" id="${courseCode}_indicator_${indicatorNumber.replace('.', '_')}"
                                   name="indicator_${indicatorNumber.replace('.', '_')}"
                                   value="${indicatorNumber}" checked />
                            <label for="${courseCode}_indicator_${indicatorNumber.replace('.', '_')}">
                                ${indicator}
                            </label>
                        </div>
                    `;
                });
            } else {
                html += `<p>No indicators available for this outcome.</p>`;
            }

            html += `</div></div>`;
        });

        html += `</div>`;

        // Update the container
        dynamicCourseContainer.innerHTML = html;

        // Update the hidden input with selected outcomes
        document.getElementById('selectedOutcomesInput').value = selectedOutcomes.join(',');

        // Add event listeners to outcome checkboxes
        const outcomeCheckboxes = dynamicCourseContainer.querySelectorAll('input[type="checkbox"][id^="' + courseCode + '_outcome_"]');
        outcomeCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                const outcomeId = this.value;
                toggleIndicatorsForCourse(courseCode, outcomeId, this.checked);
                updateSelectedOutcomesForCourse(courseCode);
            });
        });
    }

    /**
     * Toggles indicators visibility for a course-specific outcome
     * @param {string} courseCode - The course code
     * @param {string} outcomeId - The outcome ID
     * @param {boolean} checked - Whether the outcome is checked
     */
    function toggleIndicatorsForCourse(courseCode, outcomeId, checked) {
        const indicatorsDiv = document.getElementById(`${courseCode}_indicators_${outcomeId}`);
        if (indicatorsDiv) {
            indicatorsDiv.style.display = checked ? 'block' : 'none';

            // Enable/disable the indicator checkboxes
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
     * Updates the selected outcomes input for a specific course
     * @param {string} courseCode - The course code
     */
    function updateSelectedOutcomesForCourse(courseCode) {
        const selectedOutcomes = [];
        const outcomeCheckboxes = document.querySelectorAll(`input[type="checkbox"][id^="${courseCode}_outcome_"]:checked`);

        outcomeCheckboxes.forEach(checkbox => {
            selectedOutcomes.push(checkbox.value);
        });

        // Update the hidden input
        document.getElementById('selectedOutcomesInput').value = selectedOutcomes.join(',');
    }

    /**
     * Finds course details from various possible sources
     * @param {string} courseCode - The course code to look up
     * @return {Object|null} The course details or null if not found
     */
    function findCourseDetails(courseCode) {
        // Try from global variable if it exists
        if (typeof window.allCourses !== 'undefined') {
            const course = window.allCourses.find(c => c.courseCode === courseCode);
            if (course) return course;
        }

        // Try to extract from course select options
        const options = Array.from(document.querySelectorAll('select option'))
            .filter(option => option.value === courseCode);

        if (options.length > 0) {
            const text = options[0].textContent;
            if (text.includes(':')) {
                const parts = text.split(':');
                return {
                    courseCode: courseCode,
                    courseName: parts[1].trim()
                };
            }
        }

        // If no details found
        return null;
    }
});