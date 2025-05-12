/**
 * AJAX Utilities for ABET Assessment Application
 */
const AjaxUtils = {
    /**
     * Submit a form using AJAX
     * @param {HTMLFormElement} form - The form element to submit
     * @param {Function} successCallback - Function to call on successful submission
     * @param {Function} errorCallback - Function to call on submission error
     */
    submitForm: function(form, successCallback, errorCallback) {
        if (!form) {
            if (errorCallback) errorCallback(new Error('Form element not found'));
            return;
        }

        // Get the form's action URL
        const actionUrl = form.getAttribute('action');
        if (!actionUrl) {
            if (errorCallback) errorCallback(new Error('Form action URL not specified'));
            return;
        }

        // Create a FormData object to gather the form fields
        const formData = new FormData(form);

        // Send the data using the Fetch API
        fetch(actionUrl, {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Server responded with status: ' + response.status);
                }

                // Try to parse the response as JSON
                return response.json().catch(() => {
                    // If JSON parsing fails, return a simple success object
                    return { success: true };
                });
            })
            .then(data => {
                if (data.success === false) {
                    // Server returned an error
                    throw new Error(data.error || 'Unknown server error');
                }

                // Call the success callback
                if (successCallback) successCallback(data);
            })
            .catch(error => {
                console.error('Error submitting form:', error);

                // Call the error callback
                if (errorCallback) errorCallback(error);
            });
    },

    /**
     * Load content from a URL using AJAX
     * @param {string} url - The URL to load content from
     * @param {Function} successCallback - Function to call on successful load
     * @param {Function} errorCallback - Function to call on load error
     */
    loadContent: function(url, successCallback, errorCallback) {
        if (!url) {
            if (errorCallback) errorCallback(new Error('URL not specified'));
            return;
        }

        fetch(url, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Server responded with status: ' + response.status);
                }

                // Try to parse as JSON first
                return response.json()
                    .catch(() => {
                        // If not JSON, get as text
                        return response.text();
                    });
            })
            .then(data => {
                // Call the success callback
                if (successCallback) successCallback(data);
            })
            .catch(error => {
                console.error('Error loading content:', error);

                // Call the error callback
                if (errorCallback) errorCallback(error);
            });
    },

    /**
     * Send a simple AJAX request
     * @param {string} url - The URL to send the request to
     * @param {string} method - HTTP method (GET, POST, etc.)
     * @param {Object} data - Data to send (will be converted to JSON for POST)
     * @param {Function} successCallback - Function to call on success
     * @param {Function} errorCallback - Function to call on error
     */
    sendRequest: function(url, method, data, successCallback, errorCallback) {
        if (!url) {
            if (errorCallback) errorCallback(new Error('URL not specified'));
            return;
        }

        const options = {
            method: method || 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        };

        // Add JSON body for POST requests with data
        if ((method === 'POST' || method === 'PUT') && data) {
            options.headers['Content-Type'] = 'application/json';
            options.body = JSON.stringify(data);
        }

        fetch(url, options)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Server responded with status: ' + response.status);
                }

                // Try to parse as JSON
                return response.json()
                    .catch(() => {
                        // If not JSON, return text
                        return response.text();
                    });
            })
            .then(data => {
                // Check for error response
                if (data && data.success === false) {
                    throw new Error(data.error || 'Unknown server error');
                }

                // Call the success callback
                if (successCallback) successCallback(data);
            })
            .catch(error => {
                console.error('Error in AJAX request:', error);

                // Call the error callback
                if (errorCallback) errorCallback(error);
            });
    }
};