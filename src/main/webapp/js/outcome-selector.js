/**
 * Outcome Selector JavaScript
 * This script handles the automatic selection of outcomes based on the selected course.
 * It uses the outcome data provided by the OutcomeController.
 */

// Global variables to store outcome data
let outcomeDescriptions = {};
let outcomeNumbers = {};
let indicators = {};
let courseOutcomes = {};

/**
 * Initialize the outcome selector with data from the server
 * @param {Object} data - The outcome data from the server
 */
function initializeOutcomeSelector(data) {
    // Store the data in global variables
    outcomeDescriptions = data.outcomeDescriptions || {};
    outcomeNumbers = data.outcomeNumbers || {};
    indicators = data.indicators || {};
    courseOutcomes = data.courseOutcomes || {};
    
    // Set up event listener for course selection
    const courseSelect = document.getElementById('courseId');
    if (courseSelect) {
        courseSelect.addEventListener('change', handleCourseChange);
        
        // If a course is already selected, trigger the change event
        if (courseSelect.value) {
            handleCourseChange();
        }
    }
}

/**
 * Handle course selection change
 */
function handleCourseChange() {
    const courseSelect = document.getElementById('courseId');
    const courseCode = courseSelect.value;
    
    // Clear all outcome selections
    clearOutcomeSelections();
    
    // If the course has associated outcomes, select them
    if (courseCode && courseOutcomes[courseCode]) {
        selectOutcomesForCourse(courseCode);
    }
}

/**
 * Clear all outcome selections
 */
function clearOutcomeSelections() {
    // Find all outcome checkboxes and uncheck them
    const outcomeCheckboxes = document.querySelectorAll('input[type="checkbox"][id^="outcome_"]');
    outcomeCheckboxes.forEach(checkbox => {
        checkbox.checked = false;
        
        // Hide the indicators for this outcome
        const outcomeId = checkbox.value;
        const indicatorsDiv = document.getElementById(`indicators_${outcomeId}`);
        if (indicatorsDiv) {
            indicatorsDiv.style.display = 'none';
        }
    });
    
    // Update the hidden input with selected outcomes
    updateSelectedOutcomes();
}

/**
 * Select outcomes for a specific course
 * @param {string} courseCode - The course code
 */
function selectOutcomesForCourse(courseCode) {
    const outcomeIds = courseOutcomes[courseCode];
    if (!outcomeIds || !outcomeIds.length) {
        return;
    }
    
    // Select each outcome associated with the course
    outcomeIds.forEach(outcomeId => {
        const outcomeCheckbox = document.getElementById(`outcome_${outcomeId}`);
        if (outcomeCheckbox) {
            outcomeCheckbox.checked = true;
            
            // Show the indicators for this outcome
            const indicatorsDiv = document.getElementById(`indicators_${outcomeId}`);
            if (indicatorsDiv) {
                indicatorsDiv.style.display = 'block';
            }
        }
    });
    
    // Update the hidden input with selected outcomes
    updateSelectedOutcomes();
}

/**
 * Toggle indicators visibility based on outcome selection
 * @param {number} outcomeId - The outcome ID
 */
function toggleIndicators(outcomeId) {
    const outcomeCheckbox = document.getElementById(`outcome_${outcomeId}`);
    const indicatorsDiv = document.getElementById(`indicators_${outcomeId}`);
    
    if (indicatorsDiv) {
        indicatorsDiv.style.display = outcomeCheckbox.checked ? 'block' : 'none';
    }
}

/**
 * Update the hidden input with selected outcomes
 */
function updateSelectedOutcomes() {
    const selectedOutcomes = [];
    const outcomeCheckboxes = document.querySelectorAll('input[type="checkbox"][id^="outcome_"]:checked');
    
    outcomeCheckboxes.forEach(checkbox => {
        selectedOutcomes.push(checkbox.value);
    });
    
    // Update the hidden input
    const selectedOutcomesInput = document.getElementById('selectedOutcomesInput');
    if (selectedOutcomesInput) {
        selectedOutcomesInput.value = selectedOutcomes.join(',');
    }
}

/**
 * Set the action for form submission
 * @param {string} action - The action to set
 */
function setAction(action) {
    document.getElementById('saveActionInput').value = action;
}

/**
 * Save the FCAR as a draft and exit
 */
function saveAndExit() {
    setAction('draft');
    document.getElementById('fcarForm').submit();
}

// Initialize when the DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // The outcome data will be injected by the JSP
    if (typeof outcomeData !== 'undefined') {
        initializeOutcomeSelector(outcomeData);
    }
    
    // Set up event listeners for outcome checkboxes
    const outcomeCheckboxes = document.querySelectorAll('input[type="checkbox"][id^="outcome_"]');
    outcomeCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            toggleIndicators(this.value);
            updateSelectedOutcomes();
        });
    });
});
