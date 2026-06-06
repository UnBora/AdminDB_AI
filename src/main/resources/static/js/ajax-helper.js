/**
 * AJAX Helper Module
 * Provides a wrapper around the Fetch API for common HTTP patterns
 * Includes CSRF token handling, Bearer token support, and error handling
 */

export const AjaxHelper = {
  /**
   * Get CSRF token from meta tag
   * @returns {string} CSRF token value or empty string
   */
  getCsrfToken() {
    const meta = document.querySelector('meta[name="csrf-token"]');
    return meta ? meta.getAttribute('content') : '';
  },

  /**
   * Get CSRF header name from meta tag
   * @returns {string} CSRF header name or default
   */
  getCsrfHeaderName() {
    const meta = document.querySelector('meta[name="csrf-header"]');
    return meta ? meta.getAttribute('content') : 'X-CSRF-TOKEN';
  },

  /**
   * Get Authorization token from localStorage
   * @returns {string} Bearer token or empty string
   */
  getAuthToken() {
    return localStorage.getItem('auth_token') || '';
  },

  /**
   * Build common request headers
   * @param {Object} options - Additional header options
   * @returns {Object} Headers object
   */
  buildHeaders(options = {}) {
    const headers = {
      'Content-Type': 'application/json',
      ...options
    };

    const token = this.getAuthToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const csrfToken = this.getCsrfToken();
    if (csrfToken) {
      headers[this.getCsrfHeaderName()] = csrfToken;
    }

    return headers;
  },

  /**
   * Handle fetch errors and display notifications
   * @param {Response|Error} response - Fetch response or error
   * @param {string} defaultMessage - Message if response body is empty
   */
  handleError(response, defaultMessage = 'An error occurred') {
    const NotificationHandler = window.NotificationHandler || {};
    
    if (response instanceof Error) {
      const error = `Network error: ${response.message}`;
      console.error(error);
      NotificationHandler.showError?.(error);
      throw response;
    }

    if (!response.ok) {
      const status = response.status;
      const statusText = response.statusText;
      
      if (status === 401) {
        console.warn('Unauthorized: Clearing auth token');
        localStorage.removeItem('auth_token');
        window.location.href = '/login';
        return;
      }

      if (status === 403) {
        NotificationHandler.showError?.('Access denied: You do not have permission');
      } else if (status === 404) {
        NotificationHandler.showError?.('Resource not found');
      } else if (status === 500) {
        NotificationHandler.showError?.('Server error: Please try again later');
      } else {
        NotificationHandler.showError?.(defaultMessage);
      }

      const error = new Error(`${status} ${statusText}`);
      error.status = status;
      throw error;
    }

    return response;
  },

  /**
   * Perform GET request
   * @param {string} url - Request URL
   * @param {Object} options - Request options (headers, params, etc.)
   * @returns {Promise<any>} Parsed response
   */
  async GET(url, options = {}) {
    try {
      const headers = this.buildHeaders(options.headers);
      
      let fullUrl = url;
      if (options.params) {
        const params = new URLSearchParams(options.params);
        fullUrl = `${url}?${params.toString()}`;
      }

      const response = await fetch(fullUrl, {
        method: 'GET',
        headers,
        credentials: 'include'
      });

      this.handleError(response, 'Failed to fetch data');
      return await response.json();
    } catch (error) {
      console.error('GET error:', error);
      throw error;
    }
  },

  /**
   * Perform POST request
   * @param {string} url - Request URL
   * @param {Object} data - Data to send
   * @param {Object} options - Request options
   * @returns {Promise<any>} Parsed response
   */
  async POST(url, data, options = {}) {
    try {
      const headers = this.buildHeaders(options.headers);

      const response = await fetch(url, {
        method: 'POST',
        headers,
        body: JSON.stringify(data),
        credentials: 'include'
      });

      this.handleError(response, 'Failed to create resource');
      return await response.json();
    } catch (error) {
      console.error('POST error:', error);
      throw error;
    }
  },

  /**
   * Perform PUT request
   * @param {string} url - Request URL
   * @param {Object} data - Data to send
   * @param {Object} options - Request options
   * @returns {Promise<any>} Parsed response
   */
  async PUT(url, data, options = {}) {
    try {
      const headers = this.buildHeaders(options.headers);

      const response = await fetch(url, {
        method: 'PUT',
        headers,
        body: JSON.stringify(data),
        credentials: 'include'
      });

      this.handleError(response, 'Failed to update resource');
      return await response.json();
    } catch (error) {
      console.error('PUT error:', error);
      throw error;
    }
  },

  /**
   * Perform DELETE request
   * @param {string} url - Request URL
   * @param {Object} options - Request options
   * @returns {Promise<any>} Parsed response or null
   */
  async DELETE(url, options = {}) {
    try {
      const headers = this.buildHeaders(options.headers);

      const response = await fetch(url, {
        method: 'DELETE',
        headers,
        credentials: 'include'
      });

      this.handleError(response, 'Failed to delete resource');
      
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await response.json();
      }
      return null;
    } catch (error) {
      console.error('DELETE error:', error);
      throw error;
    }
  },

  /**
   * Download file export
   * @param {string} url - Export endpoint URL
   * @param {string} format - File format (csv, excel, pdf)
   * @param {string} fileName - Optional file name
   */
  async EXPORT(url, format = 'csv', fileName = null) {
    try {
      const headers = this.buildHeaders();

      const fullUrl = `${url}?format=${format}`;
      const response = await fetch(fullUrl, {
        method: 'GET',
        headers,
        credentials: 'include'
      });

      this.handleError(response, 'Failed to download file');

      const blob = await response.blob();
      const link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      
      if (!fileName) {
        const contentDisposition = response.headers.get('content-disposition');
        fileName = contentDisposition
          ? contentDisposition.split('filename=')[1]?.replace(/"/g, '')
          : `export_${Date.now()}.${format}`;
      }

      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(link.href);

      console.log(`File downloaded: ${fileName}`);
    } catch (error) {
      console.error('EXPORT error:', error);
      throw error;
    }
  },

  /**
   * Set authentication token
   * @param {string} token - Bearer token
   */
  setAuthToken(token) {
    if (token) {
      localStorage.setItem('auth_token', token);
    } else {
      localStorage.removeItem('auth_token');
    }
  }
};

export default AjaxHelper;
