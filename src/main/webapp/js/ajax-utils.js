/**
 * AJAX Utilities for ABETApp
 * This file provides common functions for making AJAX requests and handling responses.
 */

// Global AJAX settings
const AjaxUtils = {
    // Base URL for AJAX requests (automatically determined from current page)
    baseUrl: window.location.origin + window.location.pathname.substring(0, window.location.pathname.indexOf('/WEB-INF')),
    
    /**
     * Make a GET request
     * @param {string} url - The URL to send the request to
     * @param {Object} params - URL parameters as an object
     * @param {Function} successCallback - Function to call on success
     * @param {Function} errorCallback - Function to call on error
     */
    get: function(url, params, successCallback, errorCallback) {
        // Build query string from params object
        const queryString = params ? '?' + Object.keys(params)
            .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(params[key]))
            .join('&') : '';
            
        // Make the request
        fetch(this.baseUrl + url + queryString, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            if (successCallback) successCallback(data);
        })
        .catch(error => {
            console.error('Error:', error);
            if (errorCallback) errorCallback(error);
        });
    },
    
    /**
     * Make a POST request
     * @param {string} url - The URL to send the request to
     * @param {Object} data - The data to send in the request body
     * @param {Function} successCallback - Function to call on success
     * @param {Function} errorCallback - Function to call on error
     */
    post: function(url, data, successCallback, errorCallback) {
        fetch(this.baseUrl + url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            if (successCallback) successCallback(data);
        })
        .catch(error => {
            console.error('Error:', error);
            if (errorCallback) errorCallback(error);
        });
    },
    
    /**
     * Submit a form via AJAX
     * @param {HTMLFormElement} form - The form element to submit
     * @param {Function} successCallback - Function to call on success
     * @param {Function} errorCallback - Function to call on error
     */
    submitForm: function(form, successCallback, errorCallback) {
        // Get form data
        const formData = new FormData(form);
        const data = {};
        
        // Convert FormData to JSON object
        formData.forEach((value, key) => {
            // Handle multiple values for the same key (like checkboxes)
            if (data[key]) {
                if (!Array.isArray(data[key])) {
                    data[key] = [data[key]];
                }
                data[key].push(value);
            } else {
                data[key] = value;
            }
        });
        
        // Make the request
        fetch(form.action, {
            method: form.method.toUpperCase(),
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: JSON.stringify(data)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            if (successCallback) successCallback(data);
        })
        .catch(error => {
            console.error('Error:', error);
            if (errorCallback) errorCallback(error);
        });
    },
    
    /**
     * Load content into an element via AJAX
     * @param {string} url - The URL to load content from
     * @param {string} targetElementId - The ID of the element to load content into
     * @param {Object} params - URL parameters as an object
     * @param {Function} callback - Function to call after content is loaded
     */
    loadContent: function(url, targetElementId, params, callback) {
        const targetElement = document.getElementById(targetElementId);
        if (!targetElement) {
            console.error('Target element not found:', targetElementId);
            return;
        }
        
        // Show loading indicator
        targetElement.innerHTML = '<div class="loading">Loading...</div>';
        
        // Build query string from params object
        const queryString = params ? '?' + Object.keys(params)
            .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(params[key]))
            .join('&') : '';
            
        // Make the request
        fetch(this.baseUrl + url + queryString, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.text();
        })
        .then(html => {
            targetElement.innerHTML = html;
            if (callback) callback();
        })
        .catch(error => {
            console.error('Error:', error);
            targetElement.innerHTML = '<div class="error">Error loading content: ' + error.message + '</div>';
        });
    }
};