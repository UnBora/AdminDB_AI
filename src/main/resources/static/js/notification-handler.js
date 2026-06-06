/**
 * Notification Handler Module
 * Provides toast and alert notifications with auto-dismiss functionality
 */

export const NotificationHandler = {
  /**
   * Toast container ID
   */
  CONTAINER_ID: 'toast-container',

  /**
   * Ensure toast container exists
   */
  ensureContainer() {
    let container = document.getElementById(this.CONTAINER_ID);
    if (!container) {
      container = document.createElement('div');
      container.id = this.CONTAINER_ID;
      container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
      container.setAttribute('role', 'region');
      container.setAttribute('aria-live', 'assertive');
      container.setAttribute('aria-atomic', 'true');
      document.body.appendChild(container);
    }
    return container;
  },

  /**
   * Create and show a toast notification
   * @param {string} type - Toast type (success, error, warning, info)
   * @param {string} message - Toast message
   * @param {number} duration - Duration in milliseconds (0 = no auto-dismiss)
   */
  showToast(type, message, duration = 5000) {
    const container = this.ensureContainer();
    const toastId = `toast_${Date.now()}_${Math.random()}`;

    const typeConfig = {
      success: {
        bg: 'bg-success',
        icon: '✓',
        title: 'Success'
      },
      error: {
        bg: 'bg-danger',
        icon: '✕',
        title: 'Error'
      },
      warning: {
        bg: 'bg-warning',
        icon: '!',
        title: 'Warning'
      },
      info: {
        bg: 'bg-info',
        icon: 'ℹ',
        title: 'Info'
      }
    };

    const config = typeConfig[type] || typeConfig.info;

    const toastHTML = `
      <div id="${toastId}" class="toast ${config.bg} text-white border-0" role="status" aria-live="polite">
        <div class="toast-header ${config.bg} text-white border-0">
          <span class="me-2">${config.icon}</span>
          <strong class="me-auto">${config.title}</strong>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
        <div class="toast-body">
          ${message}
        </div>
      </div>
    `;

    container.insertAdjacentHTML('beforeend', toastHTML);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
      autohide: duration > 0,
      delay: duration
    });

    toast.show();

    if (duration > 0) {
      toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
      });
    }
  },

  /**
   * Show success notification
   * @param {string} message - Message text
   * @param {number} duration - Duration in milliseconds
   */
  showSuccess(message, duration = 5000) {
    this.showToast('success', message, duration);
  },

  /**
   * Show error notification
   * @param {string} message - Message text
   * @param {number} duration - Duration in milliseconds
   */
  showError(message, duration = 5000) {
    this.showToast('error', message, duration);
  },

  /**
   * Show warning notification
   * @param {string} message - Message text
   * @param {number} duration - Duration in milliseconds
   */
  showWarning(message, duration = 5000) {
    this.showToast('warning', message, duration);
  },

  /**
   * Show info notification
   * @param {string} message - Message text
   * @param {number} duration - Duration in milliseconds
   */
  showInfo(message, duration = 5000) {
    this.showToast('info', message, duration);
  }
};

export default NotificationHandler;
