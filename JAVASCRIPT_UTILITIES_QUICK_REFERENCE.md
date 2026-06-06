# JavaScript Utilities Quick Reference

## 8 Utility Modules - 1,991 Lines of Code

### Summary

| Module | Lines | Purpose |
|--------|-------|---------|
| ajax-helper.js | 269 | HTTP requests with CSRF/Bearer token support |
| notification-handler.js | 130 | Toast notifications (success, error, warning, info) |
| modal-helper.js | 238 | Bootstrap modal wrapper with data binding |
| common-utils.js | 266 | General utilities (date, currency, debounce, etc.) |
| form-handler.js | 270 | Form submission with validation and AJAX |
| datatable-init.js | 276 | DataTables.js initialization and helpers |
| chart-init.js | 301 | Chart.js initialization (line, bar, pie, doughnut) |
| theme-toggle.js | 194 | Dark/light mode with localStorage persistence |
| app-init.js | 47 | Module initialization and global exposure |
| **Total** | **1,991** | **Production-ready frontend utilities** |

---

## Quick Start

### 1. Include in HTML Template

```html
<!-- CSRF meta tags (required) -->
<meta name="csrf-token" content="${_csrf.token}" />
<meta name="csrf-header" content="X-CSRF-TOKEN" />

<!-- Dependencies -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
<script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<!-- Utilities initialization -->
<script type="module" src="/js/app-init.js"></script>
```

### 2. Use in JavaScript

```javascript
// All modules available globally
AjaxHelper.GET('/api/users');
NotificationHandler.showSuccess('Done!');
ModalHelper.showModal('myModal');
CommonUtils.formatDate(new Date());
FormHandler.handleFormSubmit(form, onSuccess);
DataTableInit.initDataTable('myTable');
ChartInit.createLineChart('myChart', data);
ThemeToggle.toggleTheme();
```

---

## Module Functions

### AjaxHelper
- `GET(url, options)` - Fetch with CSRF/Bearer token
- `POST(url, data, options)` - Create resource
- `PUT(url, data, options)` - Update resource
- `DELETE(url, options)` - Delete resource
- `EXPORT(url, format, fileName)` - Download file
- `setAuthToken(token)` - Store Bearer token
- `getCsrfToken()` - Get CSRF from meta tag
- `getAuthToken()` - Get Bearer token

### NotificationHandler
- `showSuccess(message, duration)` - Green toast
- `showError(message, duration)` - Red toast
- `showWarning(message, duration)` - Yellow toast
- `showInfo(message, duration)` - Blue toast
- `showToast(type, message, duration)` - Custom toast

### ModalHelper
- `showModal(modalId, data)` - Show with prefill
- `closeModal(modalId)` - Hide modal
- `getFormData(modalId)` - Get form values
- `clearFormData(modalId)` - Reset form
- `showConfirm(title, message, onConfirm, onCancel)` - Confirmation dialog
- `showAlert(title, message)` - Alert dialog
- `onShown(modalId, callback)` - Listen for show
- `onHidden(modalId, callback)` - Listen for hide

### CommonUtils
- `formatDate(date, format)` - Format date/time
- `formatCurrency(amount, currency, locale)` - Format money
- `debounce(func, wait)` - Delay execution
- `throttle(func, limit)` - Limit execution
- `getQueryParam(param, url)` - Get URL param
- `getQueryParams(url)` - Get all params
- `scrollToTop(duration)` - Smooth scroll
- `setLoadingState(element, isLoading, text)` - Loading spinner
- `clearErrors(formElement)` - Remove validation errors
- `showError(inputElement, message)` - Show field error
- `encodeHTML(text)` - Escape HTML
- `decodeHTML(html)` - Unescape HTML
- `deepClone(obj)` - Clone object

### FormHandler
- `handleFormSubmit(form, onSuccess, onError, options)` - AJAX form submission
- `setupFileInput(inputId, previewId, options)` - File upload with preview
- `setupAutoSave(form, saveUrl, delay)` - Auto-save on change
- `validateForm(formElement)` - HTML5 validation
- `getFormData(formElement)` - Get form as object

### DataTableInit
- `initDataTable(elementId, options)` - Initialize table
- `reloadTable(table, resetPaging)` - Reload data
- `addRow(table, rowData, redraw)` - Add row
- `updateRow(table, rowIndex, rowData)` - Update row
- `deleteRow(table, rowIndex)` - Delete row
- `getSelectedRows(table)` - Get selected rows
- `clearSelection(table)` - Deselect all
- `setupFilters(table, configs)` - Add filters
- `exportToCSV(table, filename)` - Export to CSV

### ChartInit
- `createLineChart(elementId, data, options)` - Line chart
- `createBarChart(elementId, data, options)` - Bar chart
- `createPieChart(elementId, data, options)` - Pie chart
- `createDoughnutChart(elementId, data, options)` - Doughnut chart
- `createAreaChart(elementId, data, options)` - Area chart
- `updateChart(chart, newData)` - Update chart
- `destroyChart(chart)` - Destroy chart
- `getColorPalette()` - Get theme colors
- `getDefaultOptions()` - Default chart options

### ThemeToggle
- `getCurrentTheme()` - Get current theme
- `setTheme(theme)` - Set light/dark
- `toggleTheme()` - Toggle theme
- `initThemeToggle(toggleElement)` - Setup toggle button
- `onThemeChange(callback)` - Listen for changes
- `autoInit()` - Auto-initialize

---

## Common Patterns

### AJAX with Error Handling
```javascript
try {
  const data = await AjaxHelper.GET('/api/users');
  NotificationHandler.showSuccess('Loaded');
} catch (error) {
  // Already handled with notification
}
```

### Form Submission
```javascript
FormHandler.handleFormSubmit(form, (response) => {
  NotificationHandler.showSuccess('Saved!');
  // Reload page or update UI
}, (error) => {
  console.error(error);
});
```

### DataTable with Server-Side Processing
```javascript
const table = DataTableInit.initDataTable('myTable', {
  columns: [...],
  serverSide: true,
  ajax: '/api/users'
});
```

### Chart Display
```javascript
const chart = ChartInit.createLineChart('canvas', {
  labels: ['Jan', 'Feb', 'Mar'],
  datasets: [{
    label: 'Sales',
    data: [100, 200, 150]
  }]
});
```

### Modal with Data Binding
```javascript
const data = { id: 1, name: 'John', email: 'john@example.com' };
ModalHelper.showModal('editModal', data);

// Get updated data
const updated = ModalHelper.getFormData('editModal');
```

### Debounced Search
```javascript
const search = CommonUtils.debounce(async (e) => {
  const results = await AjaxHelper.GET('/api/search', {
    params: { q: e.target.value }
  });
}, 300);

searchInput.addEventListener('input', search);
```

### Theme Toggle
```javascript
ThemeToggle.initThemeToggle('themeToggleBtn');

ThemeToggle.onThemeChange((detail) => {
  console.log('Theme:', detail.theme);
});
```

---

## Features

✅ **ES6 Modules** - Import/export structure
✅ **No jQuery Required** - Vanilla JavaScript + Bootstrap 5
✅ **CSRF Protection** - Auto-add from meta tag
✅ **Bearer Tokens** - localStorage-based auth
✅ **Error Handling** - Automatic notifications
✅ **Form Validation** - HTML5 + custom
✅ **File Upload** - With preview support
✅ **Data Tables** - Search, sort, pagination
✅ **Charts** - Multiple chart types
✅ **Modals** - Data binding & events
✅ **Notifications** - Toast alerts
✅ **Dark Mode** - Theme toggle
✅ **Utilities** - Date, currency, debounce
✅ **Progressive Enhancement** - Works without JS

---

## Dependencies

### Required (CDN)
- Bootstrap 5.3+
- jQuery 3.7+ (for DataTables)
- DataTables 1.13.7+
- Chart.js 4.4+

### No Dependencies (Included)
- CSRF token handling
- Bearer token management
- Toast notifications
- Modal helpers
- Form validation
- Utility functions

---

## File Sizes

Total JavaScript: ~1,991 lines (24 KB uncompressed, ~6 KB gzipped)

---

## Best Practices

1. ✅ Load utilities after Bootstrap and jQuery
2. ✅ Include CSRF meta tags in `<head>`
3. ✅ Use error handling in try/catch
4. ✅ Debounce/throttle event listeners
5. ✅ Show user feedback for all actions
6. ✅ Validate forms before submission
7. ✅ Destroy charts when not needed
8. ✅ Use server-side DataTables for large datasets

---

## Documentation

See **JAVASCRIPT_UTILITIES_GUIDE.md** for complete API reference with examples.
