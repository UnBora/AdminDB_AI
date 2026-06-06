/**
 * Common Utilities Module
 * General utility functions for date formatting, currency, debouncing, etc.
 */

export const CommonUtils = {
  /**
   * Format date with various formats
   * @param {Date|string|number} date - Date to format
   * @param {string} format - Format string (yyyy-MM-dd, MM/dd/yyyy, etc.)
   * @returns {string} Formatted date
   */
  formatDate(date, format = 'yyyy-MM-dd') {
    if (!date) return '';
    const d = new Date(date);
    if (isNaN(d)) return '';

    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');

    const tokens = {
      'yyyy': year,
      'yy': String(year).slice(-2),
      'MM': month,
      'M': d.getMonth() + 1,
      'dd': day,
      'd': d.getDate(),
      'HH': hours,
      'H': d.getHours(),
      'mm': minutes,
      'm': d.getMinutes(),
      'ss': seconds,
      's': d.getSeconds()
    };

    let result = format;
    Object.keys(tokens).forEach(token => {
      result = result.replace(new RegExp(token, 'g'), tokens[token]);
    });

    return result;
  },

  /**
   * Format currency value
   * @param {number} amount - Amount to format
   * @param {string} currency - Currency code (USD, EUR, GBP, etc.)
   * @param {string} locale - Locale string (default: en-US)
   * @returns {string} Formatted currency
   */
  formatCurrency(amount, currency = 'USD', locale = 'en-US') {
    if (amount === null || amount === undefined) return '';
    
    try {
      return new Intl.NumberFormat(locale, {
        style: 'currency',
        currency: currency
      }).format(amount);
    } catch (error) {
      console.error('Currency formatting error:', error);
      return amount.toString();
    }
  },

  /**
   * Debounce function - delays execution until after wait time
   * @param {Function} func - Function to debounce
   * @param {number} wait - Wait time in milliseconds
   * @returns {Function} Debounced function
   */
  debounce(func, wait = 300) {
    let timeout;
    return function debounced(...args) {
      clearTimeout(timeout);
      timeout = setTimeout(() => func.apply(this, args), wait);
    };
  },

  /**
   * Throttle function - limits execution to once per time period
   * @param {Function} func - Function to throttle
   * @param {number} limit - Time limit in milliseconds
   * @returns {Function} Throttled function
   */
  throttle(func, limit = 300) {
    let lastRun = 0;
    return function throttled(...args) {
      const now = Date.now();
      if (now - lastRun >= limit) {
        lastRun = now;
        func.apply(this, args);
      }
    };
  },

  /**
   * Get query parameter from URL
   * @param {string} param - Parameter name
   * @param {string} url - Optional URL (defaults to current URL)
   * @returns {string|null} Parameter value or null
   */
  getQueryParam(param, url = null) {
    const searchParams = new URLSearchParams(
      url ? new URL(url).search : window.location.search
    );
    return searchParams.get(param);
  },

  /**
   * Get all query parameters as object
   * @param {string} url - Optional URL (defaults to current URL)
   * @returns {Object} Query parameters as key-value pairs
   */
  getQueryParams(url = null) {
    const searchParams = new URLSearchParams(
      url ? new URL(url).search : window.location.search
    );
    const params = {};
    for (const [key, value] of searchParams.entries()) {
      params[key] = value;
    }
    return params;
  },

  /**
   * Scroll to top of page smoothly
   * @param {number} duration - Animation duration in milliseconds
   */
  scrollToTop(duration = 300) {
    const start = window.scrollY;
    const startTime = performance.now();

    const scroll = (timestamp) => {
      const elapsed = timestamp - startTime;
      const progress = Math.min(elapsed / duration, 1);
      window.scrollY = start * (1 - progress);
      
      if (progress < 1) {
        requestAnimationFrame(scroll);
      }
    };

    requestAnimationFrame(scroll);
  },

  /**
   * Set loading state on element
   * @param {HTMLElement|string} element - Element or element ID
   * @param {boolean} isLoading - Loading state
   * @param {string} loadingText - Loading text (optional)
   */
  setLoadingState(element, isLoading, loadingText = 'Loading...') {
    if (typeof element === 'string') {
      element = document.getElementById(element);
    }
    if (!element) return;

    if (isLoading) {
      element.disabled = true;
      element.classList.add('disabled');
      if (element.tagName === 'BUTTON' || element.tagName === 'A') {
        const originalText = element.getAttribute('data-original-text') || element.textContent;
        element.setAttribute('data-original-text', originalText);
        element.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>${loadingText}`;
      }
    } else {
      element.disabled = false;
      element.classList.remove('disabled');
      if (element.tagName === 'BUTTON' || element.tagName === 'A') {
        const originalText = element.getAttribute('data-original-text');
        if (originalText) {
          element.textContent = originalText;
        }
      }
    }
  },

  /**
   * Clear validation error messages from form
   * @param {HTMLElement|string} formElement - Form element or form ID
   */
  clearErrors(formElement) {
    if (typeof formElement === 'string') {
      formElement = document.getElementById(formElement);
    }
    if (!formElement) return;

    const inputs = formElement.querySelectorAll('.is-invalid');
    inputs.forEach(input => {
      input.classList.remove('is-invalid');
      const feedback = input.nextElementSibling;
      if (feedback && feedback.classList.contains('invalid-feedback')) {
        feedback.textContent = '';
      }
    });
  },

  /**
   * Show validation error on form input
   * @param {HTMLElement|string} inputElement - Input element or input ID
   * @param {string} message - Error message
   */
  showError(inputElement, message) {
    if (typeof inputElement === 'string') {
      inputElement = document.getElementById(inputElement);
    }
    if (!inputElement) return;

    inputElement.classList.add('is-invalid');
    let feedback = inputElement.nextElementSibling;
    if (!feedback || !feedback.classList.contains('invalid-feedback')) {
      feedback = document.createElement('div');
      feedback.className = 'invalid-feedback d-block';
      inputElement.parentNode.insertBefore(feedback, inputElement.nextSibling);
    }
    feedback.textContent = message;
  },

  /**
   * Encode HTML special characters
   * @param {string} text - Text to encode
   * @returns {string} Encoded text
   */
  encodeHTML(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  },

  /**
   * Decode HTML entities
   * @param {string} html - HTML to decode
   * @returns {string} Decoded text
   */
  decodeHTML(html) {
    const txt = document.createElement('textarea');
    txt.innerHTML = html;
    return txt.value;
  },

  /**
   * Deep clone object
   * @param {Object} obj - Object to clone
   * @returns {Object} Cloned object
   */
  deepClone(obj) {
    if (obj === null || typeof obj !== 'object') return obj;
    if (obj instanceof Date) return new Date(obj);
    if (obj instanceof Array) return obj.map(item => this.deepClone(item));
    if (obj instanceof Object) {
      const cloned = {};
      for (const key in obj) {
        if (obj.hasOwnProperty(key)) {
          cloned[key] = this.deepClone(obj[key]);
        }
      }
      return cloned;
    }
  }
};

export default CommonUtils;
