/**
 * Modal Helper Module
 * Wrapper around Bootstrap 5 modals with event handling and data binding
 */

export const ModalHelper = {
  /**
   * Show a modal by ID
   * @param {string} modalId - Bootstrap modal element ID
   * @param {Object} data - Optional data to pre-fill form fields
   */
  showModal(modalId, data = null) {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) {
      console.error(`Modal with ID "${modalId}" not found`);
      return;
    }

    if (data) {
      this.prefillFormData(modalElement, data);
    }

    const modal = new bootstrap.Modal(modalElement);
    modal.show();

    const firstInput = modalElement.querySelector('input, textarea, select');
    if (firstInput) {
      firstInput.focus();
    }
  },

  /**
   * Close a modal by ID
   * @param {string} modalId - Bootstrap modal element ID
   */
  closeModal(modalId) {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) {
      console.error(`Modal with ID "${modalId}" not found`);
      return;
    }

    const modal = bootstrap.Modal.getInstance(modalElement);
    if (modal) {
      modal.hide();
    }
  },

  /**
   * Pre-fill form fields with data
   * @param {HTMLElement} modalElement - Modal element
   * @param {Object} data - Key-value pairs to fill
   */
  prefillFormData(modalElement, data) {
    Object.keys(data).forEach(key => {
      const input = modalElement.querySelector(`[name="${key}"]`);
      if (input) {
        if (input.type === 'checkbox' || input.type === 'radio') {
          input.checked = data[key];
        } else {
          input.value = data[key];
        }
        input.dispatchEvent(new Event('change', { bubbles: true }));
      }
    });
  },

  /**
   * Get form data from modal
   * @param {string} modalId - Modal element ID
   * @returns {Object} Form data as key-value pairs
   */
  getFormData(modalId) {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) {
      console.error(`Modal with ID "${modalId}" not found`);
      return {};
    }

    const form = modalElement.querySelector('form');
    if (!form) {
      console.warn(`No form found in modal "${modalId}"`);
      return {};
    }

    const formData = new FormData(form);
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
   * Clear form fields in modal
   * @param {string} modalId - Modal element ID
   */
  clearFormData(modalId) {
    const modalElement = document.getElementById(modalId);
    if (!modalElement) {
      console.error(`Modal with ID "${modalId}" not found`);
      return;
    }

    const form = modalElement.querySelector('form');
    if (form) {
      form.reset();
    }

    const inputs = modalElement.querySelectorAll('input, textarea, select');
    inputs.forEach(input => {
      input.classList.remove('is-invalid');
      const feedback = input.nextElementSibling;
      if (feedback && feedback.classList.contains('invalid-feedback')) {
        feedback.textContent = '';
      }
    });
  },

  /**
   * Show confirmation dialog
   * @param {string} title - Dialog title
   * @param {string} message - Dialog message
   * @param {Function} onConfirm - Callback on confirmation
   * @param {Function} onCancel - Optional callback on cancel
   */
  showConfirm(title, message, onConfirm, onCancel = null) {
    const confirmId = `confirm-modal-${Date.now()}`;
    const confirmHTML = `
      <div class="modal fade" id="${confirmId}" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${title}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              ${message}
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button type="button" class="btn btn-primary" id="confirm-btn">Confirm</button>
            </div>
          </div>
        </div>
      </div>
    `;

    document.body.insertAdjacentHTML('beforeend', confirmHTML);
    const confirmElement = document.getElementById(confirmId);
    const modal = new bootstrap.Modal(confirmElement);

    confirmElement.querySelector('#confirm-btn').addEventListener('click', () => {
      onConfirm();
      modal.hide();
    });

    confirmElement.addEventListener('hidden.bs.modal', () => {
      confirmElement.remove();
      onCancel?.();
    });

    modal.show();
  },

  /**
   * Show alert dialog
   * @param {string} title - Alert title
   * @param {string} message - Alert message
   */
  showAlert(title, message) {
    const alertId = `alert-modal-${Date.now()}`;
    const alertHTML = `
      <div class="modal fade" id="${alertId}" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">${title}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              ${message}
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>
            </div>
          </div>
        </div>
      </div>
    `;

    document.body.insertAdjacentHTML('beforeend', alertHTML);
    const alertElement = document.getElementById(alertId);
    const modal = new bootstrap.Modal(alertElement);

    alertElement.addEventListener('hidden.bs.modal', () => {
      alertElement.remove();
    });

    modal.show();
  },

  /**
   * On modal shown event
   * @param {string} modalId - Modal element ID
   * @param {Function} callback - Callback function
   */
  onShown(modalId, callback) {
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
      modalElement.addEventListener('shown.bs.modal', callback);
    }
  },

  /**
   * On modal hidden event
   * @param {string} modalId - Modal element ID
   * @param {Function} callback - Callback function
   */
  onHidden(modalId, callback) {
    const modalElement = document.getElementById(modalId);
    if (modalElement) {
      modalElement.addEventListener('hidden.bs.modal', callback);
    }
  }
};

export default ModalHelper;
