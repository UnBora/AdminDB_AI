/**
 * App Initialization Script
 * Exposes all utility modules to global window namespace for easy access
 */

import AjaxHelper from './ajax-helper.js';
import NotificationHandler from './notification-handler.js';
import ModalHelper from './modal-helper.js';
import CommonUtils from './common-utils.js';
import FormHandler from './form-handler.js';
import DataTableInit from './datatable-init.js';
import ChartInit from './chart-init.js';
import ThemeToggle from './theme-toggle.js';

// Expose modules to global namespace
window.AjaxHelper = AjaxHelper;
window.NotificationHandler = NotificationHandler;
window.ModalHelper = ModalHelper;
window.CommonUtils = CommonUtils;
window.FormHandler = FormHandler;
window.DataTableInit = DataTableInit;
window.ChartInit = ChartInit;
window.ThemeToggle = ThemeToggle;

// Log initialization
console.log('AdminDB_AI JavaScript utilities loaded');
console.log({
  AjaxHelper,
  NotificationHandler,
  ModalHelper,
  CommonUtils,
  FormHandler,
  DataTableInit,
  ChartInit,
  ThemeToggle
});

export default {
  AjaxHelper,
  NotificationHandler,
  ModalHelper,
  CommonUtils,
  FormHandler,
  DataTableInit,
  ChartInit,
  ThemeToggle
};
