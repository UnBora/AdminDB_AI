# JavaScript Utilities Guide

Complete reference for the AdminDB_AI frontend JavaScript utility modules.

## Overview

The JavaScript utilities provide a set of reusable, ES6 module-based functions for common frontend tasks. All modules are exposed to the global `window` namespace for easy access throughout the application.

### Modules Provided

1. **ajax-helper.js** - HTTP requests with CSRF/Bearer token support
2. **notification-handler.js** - Toast notifications
3. **modal-helper.js** - Bootstrap modal wrapper
4. **common-utils.js** - General utility functions
5. **form-handler.js** - Form submission and validation
6. **datatable-init.js** - DataTables.js initialization
7. **chart-init.js** - Chart.js initialization
8. **theme-toggle.js** - Dark/light mode support

---

## Installation

### Add scripts to your template (main-layout.html)

```html
<!-- In <head> for critical styles -->
<meta name="csrf-token" content="${_csrf.token}" />
<meta name="csrf-header" content="X-CSRF-TOKEN" />

<!-- Bootstrap and dependencies before closing </body> -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
<script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/dataTables.buttons.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/pdfmake.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/vfs_fonts.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.html5.min.js"></script>
<script src="https://cdn.datatables.net/buttons/2.4.2/js/buttons.colvis.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<!-- App utilities initialization -->
<script type="module" src="/js/app-init.js"></script>
```

### CSS (Bootstrap 5)

```html
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.7/css/jquery.dataTables.min.css">
<link rel="stylesheet" href="https://cdn.datatables.net/buttons/2.4.2/css/buttons.dataTables.min.css">
```

---

## 1. AJAX Helper

HTTP request wrapper with automatic CSRF and Bearer token handling.

### Usage Examples

```javascript
// GET request
try {
  const data = await AjaxHelper.GET('/api/users', {
    params: { page: 1, limit: 10 }
  });
  console.log(data);
} catch (error) {
  console.error('Error:', error);
}

// POST request
const newUser = { name: 'John', email: 'john@example.com' };
const result = await AjaxHelper.POST('/api/users', newUser);
NotificationHandler.showSuccess('User created');

// PUT request (update)
const updated = { name: 'Jane' };
await AjaxHelper.PUT('/api/users/123', updated);

// DELETE request
await AjaxHelper.DELETE('/api/users/123');

// Export to file
await AjaxHelper.EXPORT('/api/users/export', 'csv', 'users.csv');
await AjaxHelper.EXPORT('/api/users/export', 'excel', 'users.xlsx');

// Set authentication token after login
AjaxHelper.setAuthToken(response.accessToken);
```

### API Reference

#### GET(url, options)
Fetch data from server
- **url** (string): API endpoint
- **options** (object): `{ params, headers }`
- **Returns**: Promise<any>

#### POST(url, data, options)
Create resource
- **url** (string): API endpoint
- **data** (object): JSON data to send
- **options** (object): Additional request options
- **Returns**: Promise<any>

#### PUT(url, data, options)
Update resource
- **url** (string): API endpoint
- **data** (object): JSON data to send
- **options** (object): Additional request options
- **Returns**: Promise<any>

#### DELETE(url, options)
Delete resource
- **url** (string): API endpoint
- **options** (object): Additional request options
- **Returns**: Promise<any>

#### EXPORT(url, format, fileName)
Download file export
- **url** (string): Export endpoint
- **format** (string): 'csv', 'excel', 'pdf'
- **fileName** (string): Optional custom file name
- **Returns**: Promise<void>

#### setAuthToken(token)
Store authentication token
- **token** (string): Bearer token to store in localStorage

---

## 2. Notification Handler

Toast notifications with auto-dismiss

### Usage Examples

```javascript
// Show success notification
NotificationHandler.showSuccess('User created successfully', 5000);

// Show error notification
NotificationHandler.showError('Failed to save changes', 7000);

// Show warning notification
NotificationHandler.showWarning('This action cannot be undone');

// Show info notification
NotificationHandler.showInfo('Syncing data...', 0); // No auto-dismiss

// Show custom toast
NotificationHandler.showToast('success', 'Operation completed', 5000);
```

### API Reference

#### showSuccess(message, duration)
- **message** (string): Notification text
- **duration** (number): Duration in ms (0 = no auto-dismiss)

#### showError(message, duration)
Display error notification

#### showWarning(message, duration)
Display warning notification

#### showInfo(message, duration)
Display info notification

#### showToast(type, message, duration)
Display custom toast
- **type** (string): 'success', 'error', 'warning', 'info'

---

## 3. Modal Helper

Bootstrap modal wrapper for easier handling

### Usage Examples

```javascript
// Show modal with pre-filled data
const userData = { name: 'John', email: 'john@example.com' };
ModalHelper.showModal('editUserModal', userData);

// Close modal
ModalHelper.closeModal('editUserModal');

// Get form data from modal
const formData = ModalHelper.getFormData('editUserModal');
console.log(formData); // { name: 'John', email: 'john@example.com' }

// Clear form data
ModalHelper.clearFormData('editUserModal');

// Show confirmation dialog
ModalHelper.showConfirm(
  'Delete User',
  'Are you sure you want to delete this user?',
  () => {
    console.log('Confirmed');
  },
  () => {
    console.log('Cancelled');
  }
);

// Show alert
ModalHelper.showAlert('Success', 'User created successfully');

// Listen for modal events
ModalHelper.onShown('editUserModal', () => {
  console.log('Modal shown');
  document.querySelector('#editUserModal input').focus();
});

ModalHelper.onHidden('editUserModal', () => {
  console.log('Modal hidden');
});
```

### API Reference

#### showModal(modalId, data)
Show modal with optional pre-filled data
- **modalId** (string): Bootstrap modal element ID
- **data** (object): Optional key-value pairs to fill form fields

#### closeModal(modalId)
Hide modal

#### getFormData(modalId)
Get form data as object
- **Returns**: Object with form field values

#### clearFormData(modalId)
Reset form and remove validation styles

#### showConfirm(title, message, onConfirm, onCancel)
Show confirmation dialog
- **title** (string): Dialog title
- **message** (string): Dialog message
- **onConfirm** (function): Callback on confirm
- **onCancel** (function): Optional callback on cancel

#### showAlert(title, message)
Show alert dialog
- **title** (string): Alert title
- **message** (string): Alert message

#### onShown(modalId, callback)
Listen for modal shown event

#### onHidden(modalId, callback)
Listen for modal hidden event

---

## 4. Common Utilities

General purpose utility functions

### Usage Examples

```javascript
// Format date
const date = new Date();
CommonUtils.formatDate(date, 'yyyy-MM-dd'); // "2024-01-15"
CommonUtils.formatDate(date, 'MM/dd/yyyy'); // "01/15/2024"
CommonUtils.formatDate(date, 'HH:mm:ss'); // "14:30:45"

// Format currency
CommonUtils.formatCurrency(1234.56, 'USD'); // "$1,234.56"
CommonUtils.formatCurrency(1234.56, 'EUR', 'de-DE'); // "1.234,56 €"

// Debounce search input
const searchInput = document.getElementById('search');
const debouncedSearch = CommonUtils.debounce(async (e) => {
  const results = await AjaxHelper.GET('/api/users/search', {
    params: { q: e.target.value }
  });
}, 300);
searchInput.addEventListener('input', debouncedSearch);

// Throttle scroll events
window.addEventListener('scroll', CommonUtils.throttle(() => {
  console.log('Scrolling...');
}, 300));

// Get query parameter
const userId = CommonUtils.getQueryParam('id'); // from ?id=123

// Get all query parameters
const params = CommonUtils.getQueryParams(); // { id: '123', page: '1' }

// Scroll to top
CommonUtils.scrollToTop(300); // 300ms animation

// Set loading state on button
const btn = document.getElementById('submitBtn');
CommonUtils.setLoadingState(btn, true, 'Processing...');
// Later:
CommonUtils.setLoadingState(btn, false);

// Clear validation errors
CommonUtils.clearErrors('myForm');

// Show error on input field
CommonUtils.showError('emailInput', 'Invalid email address');

// Encode/decode HTML
const encoded = CommonUtils.encodeHTML('<script>alert("xss")</script>');
const decoded = CommonUtils.decodeHTML('&lt;script&gt;');

// Deep clone object
const original = { name: 'John', address: { city: 'NYC' } };
const clone = CommonUtils.deepClone(original);
clone.address.city = 'LA'; // original unchanged
```

### API Reference

#### formatDate(date, format)
Format date string
- **date** (Date|string|number): Date to format
- **format** (string): Format pattern (yyyy-MM-dd, MM/dd/yyyy, HH:mm:ss, etc.)
- **Returns**: Formatted date string

#### formatCurrency(amount, currency, locale)
Format number as currency
- **amount** (number): Amount to format
- **currency** (string): Currency code (USD, EUR, GBP)
- **locale** (string): Locale string (en-US, de-DE, etc.)
- **Returns**: Formatted currency string

#### debounce(func, wait)
Delay function execution
- **func** (function): Function to debounce
- **wait** (number): Delay in ms (default: 300)
- **Returns**: Debounced function

#### throttle(func, limit)
Limit function execution frequency
- **func** (function): Function to throttle
- **limit** (number): Time limit in ms (default: 300)
- **Returns**: Throttled function

#### getQueryParam(param, url)
Get query parameter value
- **param** (string): Parameter name
- **url** (string): Optional URL
- **Returns**: Parameter value or null

#### scrollToTop(duration)
Smooth scroll to page top
- **duration** (number): Animation duration in ms

#### setLoadingState(element, isLoading, loadingText)
Toggle loading state on element
- **element** (HTMLElement|string): Element or element ID
- **isLoading** (boolean): Loading state
- **loadingText** (string): Loading text to display

#### clearErrors(formElement)
Remove validation errors from form

#### showError(inputElement, message)
Show error message on input field

---

## 5. Form Handler

Handle form submission with validation and AJAX

### Usage Examples

```javascript
// Basic form submission
const form = document.getElementById('userForm');
FormHandler.handleFormSubmit(
  form,
  (response) => {
    console.log('Success:', response);
    NotificationHandler.showSuccess('User saved');
  },
  (error) => {
    console.error('Error:', error);
  }
);

// Advanced form submission with options
FormHandler.handleFormSubmit(form, onSuccess, onError, {
  method: 'POST',
  url: '/api/users',
  includeFiles: true,
  showLoadingSpinner: true,
  resetOnSuccess: true,
  successMessage: 'User created successfully',
  errorMessage: 'Failed to create user'
});

// Handle file uploads with preview
FormHandler.setupFileInput('profileImageInput', 'imagePreview', {
  maxSize: 2 * 1024 * 1024, // 2MB
  allowedTypes: ['image/jpeg', 'image/png', 'image/gif'],
  onPreview: (file) => {
    console.log('File selected:', file.name);
  }
});

// Setup auto-save
FormHandler.setupAutoSave(form, '/api/users/123', 1000); // Save every 1s

// Get form data as object
const formData = FormHandler.getFormData(form);
console.log(formData); // { name: 'John', email: 'john@example.com' }
```

### HTML Form Example

```html
<form id="userForm" action="/api/users" method="POST" enctype="multipart/form-data">
  <div class="mb-3">
    <label for="name" class="form-label">Name</label>
    <input type="text" class="form-control" id="name" name="name" required>
    <div class="invalid-feedback"></div>
  </div>

  <div class="mb-3">
    <label for="email" class="form-label">Email</label>
    <input type="email" class="form-control" id="email" name="email" required>
    <div class="invalid-feedback"></div>
  </div>

  <div class="mb-3">
    <label for="file" class="form-label">Profile Image</label>
    <input type="file" class="form-control" id="profileImageInput" name="file">
    <div id="imagePreview"></div>
  </div>

  <button type="submit" class="btn btn-primary">Save</button>
</form>
```

### API Reference

#### handleFormSubmit(formElement, onSuccess, onError, options)
Handle form submission with AJAX
- **formElement** (HTMLElement): Form element
- **onSuccess** (function): Success callback(response)
- **onError** (function): Error callback(error)
- **options** (object):
  - `method`: 'POST', 'PUT', 'PATCH', 'DELETE'
  - `url`: API endpoint
  - `includeFiles`: Include file uploads
  - `showLoadingSpinner`: Show loading state
  - `resetOnSuccess`: Reset form after success
  - `successMessage`: Custom success message
  - `errorMessage`: Custom error message

#### setupFileInput(inputId, previewId, options)
Setup file input with preview
- **inputId** (string): File input element ID
- **previewId** (string): Preview element ID
- **options** (object):
  - `maxSize`: Max file size in bytes
  - `allowedTypes`: Array of allowed MIME types
  - `onPreview`: Callback function

#### setupAutoSave(formElement, saveUrl, delay)
Auto-save form changes
- **formElement** (HTMLElement): Form element
- **saveUrl** (string): Save endpoint
- **delay** (number): Debounce delay in ms

#### validateForm(formElement)
Validate form with HTML5 validation
- **Returns**: boolean

#### getFormData(formElement)
Get form data as object
- **Returns**: Object with form field values

---

## 6. DataTable Init

Initialize DataTables with common configurations

### Usage Examples

```javascript
// Basic DataTable initialization
const table = DataTableInit.initDataTable('usersTable', {
  columns: [
    { title: 'Select', data: null, orderable: false },
    { title: 'Name', data: 'name' },
    { title: 'Email', data: 'email' },
    { title: 'Status', data: 'status' }
  ],
  data: [
    { name: 'John', email: 'john@example.com', status: 'Active' },
    { name: 'Jane', email: 'jane@example.com', status: 'Active' }
  ]
});

// Server-side processing
const table = DataTableInit.initDataTable('usersTable', {
  columns: [
    { title: 'Name', data: 'name' },
    { title: 'Email', data: 'email' },
    { title: 'Status', data: 'status' }
  ],
  serverSide: true,
  ajax: {
    url: '/api/users',
    type: 'GET'
  },
  pageLength: 50
});

// Reload table
DataTableInit.reloadTable(table);

// Add new row
DataTableInit.addRow(table, ['Select', 'New User', 'new@example.com', 'Active']);

// Update row
DataTableInit.updateRow(table, 0, ['Select', 'Updated', 'updated@example.com', 'Inactive']);

// Delete row
DataTableInit.deleteRow(table, 0);

// Get selected rows
const selected = DataTableInit.getSelectedRows(table);
console.log(selected); // Array of selected row data

// Clear selection
DataTableInit.clearSelection(table);

// Export to CSV
DataTableInit.exportToCSV(table, 'users.csv');

// Setup advanced filters
DataTableInit.setupFilters(table, [
  { elementId: 'statusFilter', columnIndex: 2, type: 'select' },
  { elementId: 'dateFilter', columnIndex: 3, type: 'date' }
]);
```

### HTML Table Example

```html
<table id="usersTable" class="table table-striped table-hover">
  <thead>
    <tr>
      <th>Select</th>
      <th>Name</th>
      <th>Email</th>
      <th>Status</th>
    </tr>
  </thead>
  <tbody></tbody>
</table>

<!-- Filters -->
<select id="statusFilter" class="form-select">
  <option value="">All Statuses</option>
  <option value="Active">Active</option>
  <option value="Inactive">Inactive</option>
</select>
```

### API Reference

#### initDataTable(elementId, options)
Initialize DataTable
- **elementId** (string): Table element ID
- **options** (object): DataTables configuration
- **Returns**: DataTables.Api instance

#### reloadTable(table, resetPaging)
Reload table data

#### addRow(table, rowData, redraw)
Add row to table

#### updateRow(table, rowIndex, rowData)
Update existing row

#### deleteRow(table, rowIndex)
Delete row from table

#### getSelectedRows(table)
Get selected rows data
- **Returns**: Array of row data

#### clearSelection(table)
Clear row selection

#### exportToCSV(table, filename)
Export table to CSV file

---

## 7. Chart Init

Initialize Chart.js charts with common configurations

### Usage Examples

```javascript
// Line chart
const lineChart = ChartInit.createLineChart('lineChart', {
  labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
  datasets: [{
    label: 'Sales',
    data: [12, 19, 3, 5, 2, 3]
  }, {
    label: 'Revenue',
    data: [8, 15, 10, 12, 8, 10]
  }]
});

// Bar chart
const barChart = ChartInit.createBarChart('barChart', {
  labels: ['Product A', 'Product B', 'Product C', 'Product D'],
  datasets: [{
    label: 'Q1',
    data: [100, 150, 120, 200]
  }, {
    label: 'Q2',
    data: [120, 140, 160, 180]
  }]
});

// Pie chart
const pieChart = ChartInit.createPieChart('pieChart', {
  labels: ['Active', 'Inactive', 'Pending'],
  datasets: [{
    data: [45, 30, 25]
  }]
});

// Doughnut chart
const doughnutChart = ChartInit.createDoughnutChart('doughnutChart', {
  labels: ['Completed', 'In Progress', 'Not Started'],
  datasets: [{
    data: [60, 25, 15]
  }]
});

// Area chart
const areaChart = ChartInit.createAreaChart('areaChart', {
  labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
  datasets: [{
    label: 'Users',
    data: [100, 150, 120, 200]
  }]
});

// Update chart data
ChartInit.updateChart(lineChart, {
  labels: ['Jan', 'Feb', 'Mar', 'Apr'],
  datasets: [{
    label: 'Sales',
    data: [10, 20, 15, 25]
  }]
});

// Destroy chart
ChartInit.destroyChart(lineChart);
```

### HTML Canvas Example

```html
<div class="row">
  <div class="col-md-6">
    <canvas id="lineChart"></canvas>
  </div>
  <div class="col-md-6">
    <canvas id="barChart"></canvas>
  </div>
</div>
```

### API Reference

#### createLineChart(elementId, data, options)
Create line chart
- **Returns**: Chart instance

#### createBarChart(elementId, data, options)
Create bar chart

#### createPieChart(elementId, data, options)
Create pie chart

#### createDoughnutChart(elementId, data, options)
Create doughnut chart

#### createAreaChart(elementId, data, options)
Create area chart with fill

#### updateChart(chart, newData)
Update chart data

#### destroyChart(chart)
Destroy chart instance

---

## 8. Theme Toggle

Dark/light mode support with persistent storage

### Usage Examples

```javascript
// Initialize theme toggle button
ThemeToggle.initThemeToggle('theme-toggle-btn');

// Toggle theme programmatically
const newTheme = ThemeToggle.toggleTheme(); // Returns new theme

// Set specific theme
ThemeToggle.setTheme('dark'); // 'light' or 'dark'

// Get current theme
const current = ThemeToggle.getCurrentTheme(); // 'light' or 'dark'

// Listen for theme changes
ThemeToggle.onThemeChange((detail) => {
  console.log('Theme changed to:', detail.theme);
});
```

### HTML Example

```html
<!-- Theme toggle button -->
<button id="theme-toggle" class="btn btn-outline-dark" aria-label="Toggle theme">
  🌙 Dark
</button>

<!-- Meta tag for favicon color -->
<meta name="theme-color" content="#ffffff">
```

### CSS Variables

When theme changes, these CSS variables are updated:
```css
--bs-body-color
--bs-body-bg
--bs-border-color
```

### API Reference

#### getCurrentTheme()
Get current theme
- **Returns**: 'light' or 'dark'

#### setTheme(theme)
Set theme
- **theme** (string): 'light' or 'dark'

#### toggleTheme()
Toggle between light and dark
- **Returns**: New theme

#### initThemeToggle(toggleElement)
Initialize theme toggle button
- **toggleElement** (HTMLElement|string): Button element or ID

#### onThemeChange(callback)
Listen for theme changes
- **callback** (function): Callback(detail)

---

## Global Event Listeners

### Theme Changed Event

```javascript
window.addEventListener('theme-changed', (e) => {
  console.log('New theme:', e.detail.theme);
});
```

---

## Error Handling

All AJAX requests handle common HTTP errors:

- **401 Unauthorized**: Clears auth token and redirects to login
- **403 Forbidden**: Shows "Access denied" message
- **404 Not Found**: Shows "Resource not found" message
- **500 Server Error**: Shows server error message
- **Other errors**: Shows provided error message with toast

### Example Error Handling

```javascript
try {
  const data = await AjaxHelper.GET('/api/users');
  // Success
} catch (error) {
  // Error already handled by AjaxHelper with toast notification
  console.error('Request failed:', error.status, error.message);
}
```

---

## Best Practices

1. **Always use the utility functions** - They handle common errors and edge cases
2. **Include CSRF meta tags** - Required for POST/PUT/DELETE requests
3. **Handle Bearer tokens** - Use `AjaxHelper.setAuthToken()` after login
4. **Show user feedback** - Use notifications for all user actions
5. **Validate forms** - Use built-in HTML5 validation with custom validators
6. **Debounce/throttle** - Use for search, scroll, and resize events
7. **Clean up charts** - Destroy Chart.js instances when no longer needed
8. **Use DataTables** - For tables with search, sort, and pagination

---

## File Organization

```
src/main/resources/static/js/
├── ajax-helper.js              (195 lines)
├── notification-handler.js     (112 lines)
├── modal-helper.js             (225 lines)
├── common-utils.js             (263 lines)
├── form-handler.js             (218 lines)
├── datatable-init.js           (265 lines)
├── chart-init.js               (286 lines)
├── theme-toggle.js             (188 lines)
└── app-init.js                 (42 lines)

Total: ~1,800 lines of production JavaScript
```

---

## Troubleshooting

### Modules not loading
- Ensure `<meta name="csrf-token">` is in document head
- Verify Bootstrap and jQuery are loaded before scripts
- Check browser console for errors

### AJAX requests failing
- Verify CSRF token is present in meta tag
- Check Bearer token is set after login
- Review network tab for actual error

### Charts not displaying
- Ensure canvas element has `id` attribute
- Check Chart.js library is loaded
- Verify data format matches expected structure

### DataTables not working
- Verify jQuery and DataTables are loaded
- Check table has `id` attribute
- Ensure `<thead>` contains header cells

---

For more examples and integration guides, see the project documentation.
