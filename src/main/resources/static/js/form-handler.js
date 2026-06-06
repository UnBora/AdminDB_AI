/**
 * Form Handler Module
 * Handles form submission with validation, AJAX, file uploads, and notifications
 */

export const FormHandler = {
  /**
   * Validate form using HTML5 validation + custom validators
   * @param {HTMLElement} formElement - Form element
   * @returns {boolean} True if valid
   */
  validateForm(formElement) {
    const isValid = formElement.checkValidity() === false;
    formElement.classList.toggle('was-validated', true);

    if (isValid) {
      formElement.classList.add('was-validated');
      return false;
    }

    return true;
  },

  /**
   * Get form data as object
   * @param {HTMLElement} formElement - Form element
   * @returns {Object} Form data
   */
  getFormData(formElement) {
    const formData = new FormData(formElement);
    const data = {};

    for (const [key, value] of formData.entries()) {
      if (data[key]) {
        if (Array.isArray(data[key])) {
          data[key].push(value);
        } else {
          data[key] = [data[key], value];
        }
      } else {
        data[key] = value;
      }
    }

    return data;
  },

  /**
   * Handle form submission
   * @param {HTMLElement} formElement - Form element
   * @param {Function} onSuccess - Success callback(response)
   * @param {Function} onError - Error callback(error)
   * @param {Object} options - Additional options
   */
  handleFormSubmit(formElement, onSuccess, onError, options = {}) {
    const {
      method = 'POST',
      url = formElement.action,
      includeFiles = true,
      showLoadingSpinner = true,
      resetOnSuccess = true,
      successMessage = null,
      errorMessage = 'An error occurred'
    } = options;

    if (!formElement) {
      console.error('Form element not provided');
      return;
    }

    if (!url) {
      console.error('Form URL not provided');
      return;
    }

    formElement.addEventListener('submit', async (e) => {
      e.preventDefault();

      if (!this.validateForm(formElement)) {
        return;
      }

      const CommonUtils = window.CommonUtils || {};
      const NotificationHandler = window.NotificationHandler || {};
      const AjaxHelper = window.AjaxHelper || {};

      try {
        // Show loading state
        const submitBtn = formElement.querySelector('button[type="submit"]');
        if (submitBtn && showLoadingSpinner) {
          CommonUtils.setLoadingState?.(submitBtn, true);
        }

        // Prepare request body
        let body;
        if (includeFiles && formElement.enctype === 'multipart/form-data') {
          body = new FormData(formElement);
        } else {
          body = JSON.stringify(this.getFormData(formElement));
        }

        // Build headers
        const headers = {};
        if (typeof body === 'string') {
          headers['Content-Type'] = 'application/json';
        }

        // Make request
        const response = await fetch(url, {
          method: method,
          body: body,
          headers: AjaxHelper.buildHeaders?.(headers) || headers,
          credentials: 'include'
        });

        if (!response.ok) {
          const status = response.status;
          const contentType = response.headers.get('content-type');

          let errorData = {};
          if (contentType && contentType.includes('application/json')) {
            errorData = await response.json();
          }

          throw {
            status,
            message: errorData.message || errorMessage,
            errors: errorData.errors || {}
          };
        }

        const result = await response.json();

        // Reset form
        if (resetOnSuccess) {
          formElement.reset();
          formElement.classList.remove('was-validated');
          CommonUtils.clearErrors?.(formElement);
        }

        // Show success message
        if (successMessage) {
          NotificationHandler.showSuccess?.(successMessage);
        } else if (result.message) {
          NotificationHandler.showSuccess?.(result.message);
        }

        // Call success callback
        if (onSuccess) {
          onSuccess(result);
        }
      } catch (error) {
        console.error('Form submission error:', error);

        // Show field errors if available
        if (error.errors && typeof error.errors === 'object') {
          Object.keys(error.errors).forEach(fieldName => {
            const field = formElement.querySelector(`[name="${fieldName}"]`);
            if (field) {
              CommonUtils.showError?.(field, error.errors[fieldName]);
            }
          });
        }

        // Show error message
        const message = error.message || errorMessage;
        NotificationHandler.showError?.(message);

        // Call error callback
        if (onError) {
          onError(error);
        }
      } finally {
        // Hide loading state
        const submitBtn = formElement.querySelector('button[type="submit"]');
        if (submitBtn && showLoadingSpinner) {
          CommonUtils.setLoadingState?.(submitBtn, false);
        }
      }
    });
  },

  /**
   * Setup file input with preview and size validation
   * @param {string} inputId - File input ID
   * @param {string} previewId - Preview element ID
   * @param {Object} options - Options (maxSize, allowedTypes)
   */
  setupFileInput(inputId, previewId, options = {}) {
    const {
      maxSize = 5 * 1024 * 1024, // 5MB default
      allowedTypes = [],
      onPreview = null
    } = options;

    const fileInput = document.getElementById(inputId);
    const previewElement = previewId ? document.getElementById(previewId) : null;

    if (!fileInput) {
      console.error(`File input with ID "${inputId}" not found`);
      return;
    }

    fileInput.addEventListener('change', (e) => {
      const files = e.target.files;
      if (!files || files.length === 0) return;

      const file = files[0];
      const CommonUtils = window.CommonUtils || {};

      // Check file size
      if (file.size > maxSize) {
        const sizeMB = (maxSize / (1024 * 1024)).toFixed(2);
        CommonUtils.showError?.(fileInput, `File size must be less than ${sizeMB}MB`);
        return;
      }

      // Check file type
      if (allowedTypes.length > 0 && !allowedTypes.includes(file.type)) {
        CommonUtils.showError?.(fileInput, `File type not allowed. Allowed types: ${allowedTypes.join(', ')}`);
        return;
      }

      // Show preview
      if (previewElement) {
        if (file.type.startsWith('image/')) {
          const reader = new FileReader();
          reader.onload = (event) => {
            previewElement.innerHTML = `<img src="${event.target.result}" alt="Preview" class="img-fluid" style="max-width: 200px;">`;
          };
          reader.readAsDataURL(file);
        } else {
          previewElement.innerHTML = `<p class="text-muted">📄 ${file.name} (${(file.size / 1024).toFixed(2)}KB)</p>`;
        }
      }

      if (onPreview) {
        onPreview(file);
      }
    });
  },

  /**
   * Auto-save form with debounce
   * @param {HTMLElement} formElement - Form element
   * @param {string} saveUrl - API endpoint to save to
   * @param {number} delay - Debounce delay in milliseconds
   */
  setupAutoSave(formElement, saveUrl, delay = 1000) {
    if (!formElement) return;

    const CommonUtils = window.CommonUtils || {};
    const AjaxHelper = window.AjaxHelper || {};
    const NotificationHandler = window.NotificationHandler || {};

    const debouncedSave = CommonUtils.debounce?.(async () => {
      try {
        const data = this.getFormData(formElement);
        await AjaxHelper.PUT?.(saveUrl, data);
        NotificationHandler.showSuccess?.('Saved automatically');
      } catch (error) {
        console.error('Auto-save error:', error);
      }
    }, delay);

    formElement.addEventListener('change', debouncedSave);
  }
};

export default FormHandler;
