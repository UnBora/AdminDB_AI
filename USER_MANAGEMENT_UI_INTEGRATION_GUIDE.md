# User Management UI Integration Guide

## Quick Start

### 1. Access the Page
Navigate to `/user-management` route in your Spring Boot application:
```
http://localhost:8080/user-management
```

### 2. Routing Setup (Spring Controller)
Add this to your controller (e.g., UserController):

```java
@Controller
@RequestMapping("/")
public class UserController {
    
    @GetMapping("user-management")
    public String userManagement() {
        return "pages/user-management";
    }
}
```

### 3. Required Dependencies
All dependencies are included via CDN in the HTML file:
- Bootstrap 5.3.0 CSS/JS
- jQuery 3.6.0
- DataTables 1.13.6
- Font Awesome 6.4.0
- DataTables Buttons (CSV, Excel, PDF export)

### 4. Translation Keys Configuration
Add these keys to your messages.properties files:

```properties
# Titles and Subtitles
user.management.title=User Management
user.management.subtitle=Manage system users, roles, and permissions

# Action Buttons
user.management.actions.create=Create User
user.management.actions.filter=Advanced Filters
user.management.actions.export=Export
user.management.actions.delete=Delete
user.management.actions.apply=Apply
user.management.actions.reset=Reset
user.management.actions.cancel=Cancel
user.management.actions.save=Save

# Column Headers
user.management.columns.name=Name
user.management.columns.email=Email
user.management.columns.role=Role
user.management.columns.status=Status
user.management.columns.created_date=Created Date
user.management.columns.actions=Actions

# Filter Labels
user.management.filters.title=Filter Users
user.management.filters.role=Role
user.management.filters.status=Status
user.management.filters.created_date=Created Date From
user.management.filters.created_date_to=Created Date To
user.management.filters.search=Search (Name/Email)

# Status Labels
user.management.status.active=Active
user.management.status.inactive=Inactive
user.management.status.deleted=Deleted

# Form Labels and Validation
user.management.form.first_name=First Name
user.management.form.last_name=Last Name
user.management.form.email=Email
user.management.form.password=Password
user.management.form.roles=Roles
user.management.form.status=Status
user.management.form.active=Active

# Validation Messages
user.management.validation.first_name_required=First name is required
user.management.validation.last_name_required=Last name is required
user.management.validation.email_required=Valid email is required
user.management.validation.password_required=Password is required
user.management.validation.password_help=Minimum 8 characters, include uppercase, lowercase, number, and special character

# Modal Titles and Messages
user.management.modal.create_title=Create User
user.management.modal.delete_title=Delete User
user.management.modal.delete_warning=This action cannot be undone. Are you sure you want to delete this user?
user.management.modal.roles_title=Assign Roles
user.management.modal.export_title=Export Users
user.management.modal.export_description=Select the format for exporting user data:
user.management.modal.export_note=Note: Export includes filtered results with currently visible columns.
```

### 5. CSRF Configuration
Ensure CSRF token is in HTML head:

```html
<meta name="csrf-token" content="${_csrf.token}">
<meta name="csrf-header" content="${_csrf.headerName}">
```

### 6. API Endpoint Verification

#### Required Endpoints
Ensure these endpoints are implemented in your API:

**GET /api/users** (Server-side pagination)
```bash
curl -X GET "http://localhost:8080/api/users?page=0&size=10&sort=createdAt&direction=desc" \
  -H "Authorization: Bearer {token}" \
  -H "X-CSRF-TOKEN: {csrf_token}"
```

**GET /api/roles**
```bash
curl -X GET "http://localhost:8080/api/roles" \
  -H "Authorization: Bearer {token}"
```

**POST /api/users** (Create)
```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "roleIds": ["role-id-1", "role-id-2"],
    "active": true
  }'
```

**PUT /api/users/{id}** (Update)
```bash
curl -X PUT "http://localhost:8080/api/users/{id}" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "roleIds": ["role-id-1"],
    "active": true
  }'
```

**DELETE /api/users/{id}**
```bash
curl -X DELETE "http://localhost:8080/api/users/{id}" \
  -H "Authorization: Bearer {token}"
```

**POST /api/users/{id}/roles** (Assign roles)
```bash
curl -X POST "http://localhost:8080/api/users/{id}/roles" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"roleIds": ["role-id-1", "role-id-2"]}'
```

**POST /api/users/export** (Export users)
```bash
curl -X POST "http://localhost:8080/api/users/export?format=excel" \
  -H "Authorization: Bearer {token}" \
  --output users.xlsx
```

---

## 🔧 Customization Guide

### 1. Change Table Columns
Edit the `columns` array in the JavaScript section:

```javascript
const columns = [
    { data: 'id' },                    // Add ID column
    { data: 'username' },              // Add username
    { data: 'email' },
    // ... add more columns
];
```

### 2. Add More Filters
Add filter input in HTML:
```html
<div class="form-group">
    <label for="filterDepartment">Department</label>
    <input type="text" id="filterDepartment" class="form-control form-control-sm">
</div>
```

Add filter handling in AJAX data function:
```javascript
const department = $('#filterDepartment').val();
if (department) {
    d.department = department;
}
```

### 3. Customize Styles
Modify the `<style>` section or create external CSS:

```css
/* Change primary color */
.btn-primary {
    background-color: #your-color;
    border-color: #your-color;
}

/* Change table hover effect */
.table tbody tr:hover {
    background-color: #your-hover-color;
}
```

### 4. Add Custom Validation
Add to form validation:

```javascript
// Custom validator for password strength
function validatePassword(password) {
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return regex.test(password);
}
```

### 5. Add Additional Actions
Add button to action column:

```javascript
{
    data: null,
    render: (data) => {
        return `
            <button class="btn-edit">Edit</button>
            <button class="btn-export-user">Export</button>
            <button class="btn-delete">Delete</button>
        `;
    }
}
```

---

## 🐛 Troubleshooting

### Issue: "Users table is empty"
**Cause**: API endpoint not returning data
**Solution**: 
1. Check `/api/users` endpoint returns correct format
2. Verify Bearer token is valid
3. Check console for error messages (F12)
4. Verify user has ADMIN role

### Issue: "Modal won't open"
**Cause**: Bootstrap modal instance not initialized
**Solution**:
1. Ensure `bootstrap.bundle.min.js` is loaded
2. Check modal ID in button matches modal div
3. Verify no JavaScript errors in console

### Issue: "Form validation not showing"
**Cause**: HTML5 validation not triggered
**Solution**:
1. Ensure `novalidate` attribute is NOT on form
2. Check `required` attribute on form fields
3. Call `FormHandler.validateForm()` before submit

### Issue: "CSRF token error"
**Cause**: Missing or expired CSRF token
**Solution**:
1. Add meta tags to head for CSRF token
2. Ensure AjaxHelper.buildHeaders() is called
3. Check X-CSRF-TOKEN header in network tab

### Issue: "Export not working"
**Cause**: `/api/users/export` endpoint not implemented
**Solution**:
1. Implement POST endpoint for export
2. Return binary file (xlsx or csv)
3. Set proper Content-Disposition header

---

## 🔐 Security Considerations

### CSRF Protection
✅ All POST/PUT/DELETE requests include CSRF token
✅ Token sourced from meta tags
✅ Automatically added by AjaxHelper.buildHeaders()

### Authentication
✅ Bearer token sent in Authorization header
✅ Token retrieved from localStorage (set on login)
✅ 401 response redirects to login page

### Authorization
✅ All endpoints require ADMIN role (server-side)
✅ Client shows loading overlay during requests
✅ No sensitive data in URLs

### Input Validation
✅ Client-side HTML5 validation
✅ Email format validation
✅ Password strength requirements
✅ Server-side validation (must implement)

---

## 📊 Data Flow Diagrams

### User Creation Flow
```
User fills form
    ↓
Form validation (HTML5)
    ↓
AJAX POST /api/users
    ↓
Server validates & creates user
    ↓
Response with created user
    ↓
Toast notification shown
    ↓
Modal closes
    ↓
DataTable redraws
```

### Filter & Search Flow
```
User sets filters
    ↓
Click "Apply Filters"
    ↓
DataTable.draw()
    ↓
AJAX GET /api/users (with params)
    ↓
Server filters & returns data
    ↓
Table updates with filtered results
```

### Bulk Delete Flow
```
User selects checkboxes
    ↓
Bulk actions UI updates
    ↓
Click "Delete Selected"
    ↓
Confirmation modal shown
    ↓
User confirms deletion
    ↓
Loop: AJAX DELETE /api/users/{id}
    ↓
All users deleted
    ↓
Toast notification
    ↓
DataTable redraws
```

---

## 🚀 Performance Tips

1. **Reduce Page Size**: Change `lengthMenu` to `[10, 25, 50]` for faster loading
2. **Lazy Load Roles**: Load roles only when needed (already implemented)
3. **Debounce Filters**: Add debounce to search input for large datasets
4. **Pagination**: Use server-side pagination (already implemented)
5. **Caching**: Implement role caching to avoid repeated API calls
6. **Compression**: Enable gzip compression on server

---

## 📝 Maintenance

### Regular Updates
- Review API response structure annually
- Update DataTables version when available
- Test CSRF token refresh mechanism
- Monitor for deprecated Bootstrap features

### Monitoring
- Log all DELETE operations
- Monitor export requests
- Track failed validations
- Alert on role assignment changes

### Backup
- Backup user data before bulk operations
- Test export functionality regularly
- Verify deletion logs are maintained

---

## 🎓 Advanced Features

### Custom Columns
Add dynamic column rendering:
```javascript
{
    data: null,
    render: (data) => {
        return `<span style="color: ${data.active ? 'green' : 'red'}">${data.status}</span>`;
    }
}
```

### Server-Side Filtering
The filter criteria are automatically sent to server:
```javascript
// Built-in filter support
GET /api/users?firstName=John&email=example.com&active=true&dateFrom=2024-01-01
```

### Custom Toast Notifications
```javascript
NotificationHandler.showToast('success', 'Custom message', 3000);
NotificationHandler.showWarning('Warning message');
NotificationHandler.showInfo('Information message');
```

### Modal Events
```javascript
ModalHelper.onShown('userFormModal', () => {
    document.getElementById('firstName').focus();
});

ModalHelper.onHidden('userFormModal', () => {
    console.log('Modal closed');
});
```

---

## 🔄 Integration with Existing Features

### Sidebar Navigation
Add link to user-management in sidebar.html:
```html
<a href="/user-management" class="nav-link">
    <i class="fas fa-users"></i> Users
</a>
```

### Navbar
Add user count badge in navbar:
```html
<span class="badge bg-primary" id="userCount">0</span> Users
```

### Dashboard
Add user statistics:
```javascript
const stats = await AjaxHelper.GET('/api/users/stats');
```

---

## ✅ Deployment Checklist

- [ ] All translation keys added to messages.properties
- [ ] CSRF meta tags configured in main layout
- [ ] All API endpoints implemented
- [ ] User has ADMIN role for testing
- [ ] Bearer token authentication working
- [ ] CORS headers configured (if needed)
- [ ] File export endpoints returning correct format
- [ ] Email validation working
- [ ] Password strength validation implemented
- [ ] Delete operations logged/audited
- [ ] Role-based access control verified
- [ ] Mobile responsive design tested
- [ ] WCAG accessibility compliance verified
- [ ] Performance tested with large datasets

