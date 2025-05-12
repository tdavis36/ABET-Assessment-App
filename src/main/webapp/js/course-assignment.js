/**
 * Course Assignment Functionality for Edit User Form
 *
 * This script improves the existing course assignment functionality in the Edit User modal:
 * 1. Better search and filtering for courses
 * 2. Improved UI with select all/none options
 * 3. More reliable course checkbox population
 * 4. Progress indicators during loading
 */

// Function to open the edit user modal with course loading
function openEditUserModal(userId, firstName, lastName, email, deptId, roleId) {
    console.log(`Opening edit modal for user ID: ${userId}`);

    // Set form values
    document.getElementById('editUserId').value = userId;
    document.getElementById('editFirstName').value = firstName;
    document.getElementById('editLastName').value = lastName;
    document.getElementById('editEmail').value = email;
    document.getElementById('editDepartment').value = deptId;
    document.getElementById('editRole').value = roleId;

    // Clear the password field
    document.getElementById('editPassword').value = '';

    // Show the modal with loading state
    const modal = document.getElementById('editUserModal');
    modal.style.display = 'block';

    // Show loading indicator in course area
    const courseContainer = document.getElementById('editCourseCheckboxes');
    courseContainer.innerHTML = '<div class="loading-indicator">Loading courses...</div>';

    // Populate course checkboxes with enhanced UI
    populateEditCourseCheckboxes(userId);
}

// Function to close the edit user modal
function closeEditUserModal() {
    document.getElementById('editUserModal').style.display = 'none';
}

// Function to populate course checkboxes in the edit modal
function populateEditCourseCheckboxes(userId) {
    console.log(`Starting to populate course checkboxes for user ID: ${userId}`);

    const container = document.getElementById('editCourseCheckboxes');
    if (!container) {
        console.error('Course container element not found');
        return;
    }

    // Ensure userId is a value, not an element
    if (typeof userId === 'object' && userId.value) {
        userId = userId.value;
    }

    // Get proper context path from the global variable or data attribute
    const contextPath = window.contextPath || document.body.getAttribute('data-context-path') || '';
    console.log(`Using context path: ${contextPath}`);

    // First fetch the user's assigned courses
    const assignedCoursesUrl = `${contextPath}/settings?action=getProfessorCourses&userId=${userId}`;
    console.log(`Fetching assigned courses from: ${assignedCoursesUrl}`);

    fetch(assignedCoursesUrl)
        .then(response => {
            console.log(`Professor courses response status: ${response.status}`);
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.text().then(text => {
                console.log(`Raw professor courses response: ${text}`);
                try {
                    return JSON.parse(text);
                } catch (e) {
                    console.error('Error parsing JSON response:', e);
                    return [];
                }
            });
        })
        .then(assignedCourses => {
            console.log('Assigned courses received: ', assignedCourses);
            if (!Array.isArray(assignedCourses)) {
                console.warn('Assigned courses is not an array, converting to empty array');
                assignedCourses = [];
            }

            // Now fetch all available courses
            const allCoursesUrl = `${contextPath}/settings?action=getCourseList`;
            console.log(`Fetching all courses from: ${allCoursesUrl}`);

            return fetch(allCoursesUrl)
                .then(response => {
                    console.log(`All courses response status: ${response.status}`);
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.text().then(text => {
                        console.log(`Raw all courses response (first 100 chars): ${text.substring(0, 100)}...`);
                        try {
                            return { assignedCourses, allCourses: JSON.parse(text) };
                        } catch (e) {
                            console.error('Error parsing JSON response:', e);
                            return { assignedCourses, allCourses: [] };
                        }
                    });
                });
        })
        .then(data => {
            const { assignedCourses, allCourses } = data;
            console.log(`All courses received, count: ${allCourses.length}`);

            // Build the enhanced UI
            buildCourseSelectionUI(container, allCourses, assignedCourses);
            return true;
        })
        .catch(error => {
            console.error('Error fetching course data:', error);
            container.innerHTML = `
                <div class="error-message">
                    Error loading courses: ${error.message}<br>
                    <button class="btn" onclick="populateEditCourseCheckboxes(${userId})">Retry</button>
                </div>
            `;
        });
}

// Function to build the enhanced course selection UI
function buildCourseSelectionUI(container, allCourses, assignedCourses) {
    console.log('Building course selection UI');

    // Convert assignedCourses to a Set for faster lookups
    const assignedCoursesSet = new Set(assignedCourses || []);
    console.log(`Assigned courses set created with size: ${assignedCoursesSet.size}`);

    // Build the UI structure
    let html = `
        <div class="course-selection-ui">
            <div class="course-filter">
                <input type="text" id="courseSearchInput" placeholder="Search courses..." class="form-control">
                <div class="course-actions">
                    <button type="button" class="btn btn-sm" id="selectAllCourses">Select All</button>
                    <button type="button" class="btn btn-sm" id="clearAllCourses">Clear All</button>
                </div>
            </div>
            <div class="course-list">
    `;

    // Sort courses by course code
    allCourses.sort((a, b) => {
        const codeA = a.courseCode || a.course_code || '';
        const codeB = b.courseCode || b.course_code || '';
        return codeA.localeCompare(codeB);
    });
    console.log('Courses sorted successfully');

    // Group courses by department
    const coursesByDept = {};
    allCourses.forEach(course => {
        const deptId = (course.deptId || course.dept_id || 'Other').toString();
        if (!coursesByDept[deptId]) {
            coursesByDept[deptId] = [];
        }
        coursesByDept[deptId].push(course);
    });
    console.log('Courses grouped by department');

    // Add courses grouped by department
    Object.entries(coursesByDept).forEach(([deptId, deptCourses]) => {
        // Get department name - using a function to avoid undefined issues
        const deptName = getDepartmentName(deptId) || `Department ${deptId}`;
        console.log(`Adding department ${deptName} with ${deptCourses.length} courses`);

        html += `<div class="department-group" data-dept-id="${deptId}">`;
        html += `<div class="department-header">${deptName}</div>`;

        deptCourses.forEach(course => {
            // Handle either format of course data (courseCode or course_code)
            const courseCode = course.courseCode || course.course_code;
            const courseName = course.courseName || course.course_name || '';

            // Check if course is assigned
            const isAssigned = assignedCoursesSet.has(courseCode);

            html += `
                <div class="course-item">
                    <input type="checkbox" id="edit_course_${courseCode}" 
                           name="assignedCourses" value="${courseCode}"
                           ${isAssigned ? 'checked' : ''}>
                    <label for="edit_course_${courseCode}">
                        ${courseCode}: ${courseName}
                    </label>
                </div>
            `;
        });

        html += `</div>`;
    });

    html += `
            </div>
        </div>
    `;

    // Set the HTML
    container.innerHTML = html;
    console.log('Course HTML injected into container');

    // Set up event handlers
    setupCourseSearchBehavior();
    console.log('Setting up course search behavior');
    setupCourseSelectionButtons();
    console.log('Setting up course selection buttons');
    updateSelectedCoursesCounter();
    console.log('Counting selected courses');

    console.log('UI setup complete');
}

// Function to set up course search behavior
function setupCourseSearchBehavior() {
    const searchInput = document.getElementById('courseSearchInput');
    if (!searchInput) {
        console.warn('Course search input not found');
        return;
    }

    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        const courseItems = document.querySelectorAll('.course-item');
        const departmentGroups = document.querySelectorAll('.department-group');

        // Hide/show courses based on search
        let visibleCourses = 0;
        courseItems.forEach(item => {
            const courseText = item.textContent.toLowerCase();
            const shouldShow = courseText.includes(searchTerm);
            item.style.display = shouldShow ? '' : 'none';
            if (shouldShow) visibleCourses++;
        });

        // Hide/show department headers if all courses in that department are hidden
        departmentGroups.forEach(dept => {
            const visibleCoursesInDept = Array.from(dept.querySelectorAll('.course-item')).filter(
                item => item.style.display !== 'none'
            );

            dept.style.display = visibleCoursesInDept.length === 0 ? 'none' : '';
        });

        // Show a message if no courses match search
        let noResultsMsg = document.querySelector('.no-courses-message');
        if (visibleCourses === 0) {
            if (!noResultsMsg) {
                noResultsMsg = document.createElement('div');
                noResultsMsg.className = 'no-courses-message';
                const courseList = document.querySelector('.course-list');
                if (courseList) {
                    courseList.appendChild(noResultsMsg);
                }
            }

            if (noResultsMsg) {
                noResultsMsg.textContent = `No courses match "${searchTerm}"`;
                noResultsMsg.style.display = 'block';
            }
        } else if (noResultsMsg) {
            noResultsMsg.style.display = 'none';
        }
    });
}

// Function to set up select all/none buttons
function setupCourseSelectionButtons() {
    const selectAllBtn = document.getElementById('selectAllCourses');
    const clearAllBtn = document.getElementById('clearAllCourses');

    if (selectAllBtn) {
        selectAllBtn.addEventListener('click', function() {
            // Only select visible courses (respecting the search filter)
            const visibleCourses = document.querySelectorAll('.course-item:not([style*="display: none"]) input[type="checkbox"]');
            visibleCourses.forEach(checkbox => {
                checkbox.checked = true;
            });
            updateSelectedCoursesCounter();
        });
    }

    if (clearAllBtn) {
        clearAllBtn.addEventListener('click', function() {
            // Only clear visible courses (respecting the search filter)
            const visibleCourses = document.querySelectorAll('.course-item:not([style*="display: none"]) input[type="checkbox"]');
            visibleCourses.forEach(checkbox => {
                checkbox.checked = false;
            });
            updateSelectedCoursesCounter();
        });
    }

    // Add event listeners to all checkboxes to update counter
    const checkboxes = document.querySelectorAll('.course-item input[type="checkbox"]');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateSelectedCoursesCounter);
    });
}

// Function to count and display the number of selected courses
function updateSelectedCoursesCounter() {
    const totalCheckboxes = document.querySelectorAll('.course-item input[type="checkbox"]');
    const checkedBoxes = document.querySelectorAll('.course-item input[type="checkbox"]:checked');

    const checkedCount = checkedBoxes.length;
    const totalCount = totalCheckboxes.length;

    // Update counter display if it exists
    let counterElem = document.getElementById('selectedCoursesCounter');
    if (!counterElem) {
        // Create counter if it doesn't exist
        const counterContainer = document.querySelector('.course-filter');
        if (counterContainer) {
            counterElem = document.createElement('div');
            counterElem.id = 'selectedCoursesCounter';
            counterElem.className = 'selected-count';
            counterContainer.appendChild(counterElem);
        }
    }

    if (counterElem) {
        counterElem.textContent = `${checkedCount} of ${totalCount} courses selected`;
    }
}

// Helper function to get a department name by ID
function getDepartmentName(deptId) {
    // First try to get from global departments data
    if (window.departmentsData) {
        const dept = window.departmentsData.find(d => d.id.toString() === deptId.toString());
        if (dept) {
            return dept.name;
        }
    }

    // Try to get from the department select element
    const deptSelect = document.getElementById('editDepartment');
    if (deptSelect) {
        const option = deptSelect.querySelector(`option[value="${deptId}"]`);
        if (option) {
            return option.textContent;
        }
    }

    // If deptId is a known department code, return a human-readable name
    switch (deptId) {
        case '1': return 'Computer Science';
        case '2': return 'Mathematics';
        case '3': return 'Engineering';
        case '4': return 'Physics';
        case '5': return 'Business';
        default: return null;
    }
}

// Function to submit the edit user form with better feedback
function submitEditUserForm(event) {
    if (event) event.preventDefault();

    const form = document.getElementById('editUserForm');
    if (!form) {
        console.error('Edit user form not found');
        return false;
    }

    const submitButton = form.querySelector('button[type="submit"]');
    const originalText = submitButton ? submitButton.textContent : 'Save Changes';

    // Show progress
    if (submitButton) {
        submitButton.textContent = 'Saving...';
        submitButton.disabled = true;
    }

    // Collect form data
    const formData = new FormData(form);

    // Count selected courses for feedback
    const selectedCourses = document.querySelectorAll('input[name="assignedCourses"]:checked');
    formData.append('courseCount', selectedCourses.length);

    // Get the full URL with context path
    const contextPath = window.contextPath || document.body.getAttribute('data-context-path') || '';
    const actionUrl = contextPath + '/settings';

    console.log('Submitting form to: ' + actionUrl);
    console.log('Selected courses count: ' + selectedCourses.length);

    // Send AJAX request
    fetch(actionUrl, {
        method: 'POST',
        body: formData
    })
        .then(response => {
            console.log('Form submission response status: ' + response.status);
            if (!response.ok) {
                throw new Error(`Server returned an error: ${response.status}`);
            }

            // Try to parse as JSON, but fall back to text if needed
            return response.json().catch(() => {
                console.log('Response not JSON, treating as success text');
                return { success: true, message: 'Changes saved successfully' };
            });
        })
        .then(data => {
            console.log('Submission successful, server response:', data);

            if (submitButton) {
                submitButton.textContent = 'Saved!';
            }

            // Handle success response
            let message = 'User data saved successfully';
            if (typeof data === 'object' && data.message) {
                message = data.message;
            }

            // Show success message or redirect
            setTimeout(() => {
                alert(message);
                closeEditUserModal();
                // Reload the page to show updated data
                window.location.reload();
            }, 1000);
        })
        .catch(error => {
            console.error('Error saving user data:', error);

            if (submitButton) {
                submitButton.textContent = 'Error!';
            }

            alert('Failed to save changes: ' + error.message);

            setTimeout(() => {
                if (submitButton) {
                    submitButton.textContent = originalText;
                    submitButton.disabled = false;
                }
            }, 2000);
        });

    return false; // Prevent form submission
}

// Function to confirm and toggle user status
function confirmToggleStatus(userId, userName, isCurrentlyActive) {
    const action = isCurrentlyActive ? 'deactivate' : 'activate';
    if (confirm(`Are you sure you want to ${action} the account for ${userName}?`)) {
        const form = document.getElementById('toggleStatusForm');
        if (form) {
            document.getElementById('toggleUserId').value = userId;
            form.submit();
        }
    }
}

// Add the CSS styles for enhanced course assignment UI
function addEnhancedCourseAssignmentStyles() {
    if (!document.getElementById('enhanced-course-assignment-styles')) {
        const style = document.createElement('style');
        style.id = 'enhanced-course-assignment-styles';
        style.textContent = `
/* Course selection enhanced UI */
.course-selection-ui {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.course-filter {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 10px;
}

.course-filter input {
    flex-grow: 1;
    min-width: 200px;
}

.course-actions {
    display: flex;
    gap: 5px;
}

.course-list {
    max-height: 300px;
    overflow-y: auto;
    border: 1px solid var(--border, #ddd);
    border-radius: 4px;
    padding: 10px;
}

.department-group {
    margin-bottom: 15px;
}

.department-header {
    font-weight: bold;
    margin-bottom: 5px;
    padding-bottom: 5px;
    border-bottom: 1px solid var(--border, #ddd);
}

.course-item {
    display: flex;
    align-items: flex-start;
    padding: 3px 0;
}

.course-item input[type="checkbox"] {
    margin-top: 4px;
    margin-right: 6px;
}

.course-item label {
    cursor: pointer;
    font-weight: normal;
    margin-bottom: 0;
}

.selected-count {
    font-size: 0.9em;
    color: var(--text-muted, #999);
    margin-top: 5px;
}

.loading-indicator {
    text-align: center;
    padding: 20px;
    color: var(--text-muted, #999);
}

.no-courses-message {
    text-align: center;
    padding: 10px;
    font-style: italic;
    color: var(--text-muted, #999);
}

.error-message {
    padding: 10px;
    margin: 10px 0;
    background-color: #f8d7da;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
    color: #721c24;
}
`;
        document.head.appendChild(style);
        console.log('Enhanced course assignment styles added');
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded - initializing course assignment functionality');

    // Set up the context path from data attribute
    window.contextPath = document.body.getAttribute('data-context-path') || '';
    console.log('Context path set to: ' + window.contextPath);

    // Add enhanced styles
    addEnhancedCourseAssignmentStyles();

    // Store departments data if available
    const deptSelect = document.getElementById('editDepartment');
    if (deptSelect) {
        window.departmentsData = Array.from(deptSelect.options)
            .filter(option => option.value)
            .map(option => ({
                id: option.value,
                name: option.textContent
            }));
        console.log('Department data stored, count: ' + window.departmentsData.length);
    }

    // Add event listener to edit user form
    const editForm = document.getElementById('editUserForm');
    if (editForm) {
        editForm.addEventListener('submit', submitEditUserForm);
        console.log('Edit user form submit handler attached');
    } else {
        console.log('Edit user form not found');
    }
});