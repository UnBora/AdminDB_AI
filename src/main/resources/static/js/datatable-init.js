/**
 * DataTable Initialization Module
 * Wrapper around DataTables.js for common configurations
 */

export const DataTableInit = {
  /**
   * Initialize a DataTable with common options
   * @param {string} elementId - Table element ID
   * @param {Object} options - DataTables options
   * @returns {DataTables.Api} DataTable instance
   */
  initDataTable(elementId, options = {}) {
    const element = document.getElementById(elementId);
    if (!element) {
      console.error(`Table element with ID "${elementId}" not found`);
      return null;
    }

    const {
      columns = [],
      columnDefs = [],
      data = null,
      serverSide = false,
      ajax = null,
      order = [[0, 'asc']],
      pageLength = 25,
      lengthMenu = [10, 25, 50, 100],
      searching = true,
      ordering = true,
      paging = true,
      info = true,
      responsive = true,
      select = { style: 'multi', selector: 'td:first-child input[type="checkbox"]' },
      buttons = ['csv', 'excel', 'pdf'],
      language = {
        emptyTable: 'No data available',
        loadingRecords: 'Loading...',
        processing: 'Processing...',
        zeroRecords: 'No matching records found'
      }
    } = options;

    // Default column definitions
    const defaultColumnDefs = [
      {
        targets: 0,
        orderable: false,
        render: function() {
          return '<input type="checkbox">';
        }
      }
    ];

    // Build complete config
    const config = {
      dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>' +
           '<"row"<"col-sm-12"tr>>' +
           '<"row"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7"p>>',
      columns: columns,
      columnDefs: [...defaultColumnDefs, ...columnDefs],
      order: order,
      pageLength: pageLength,
      lengthMenu: lengthMenu,
      searching: searching,
      ordering: ordering,
      paging: paging,
      info: info,
      responsive: responsive,
      select: select,
      language: language,
      initComplete: function() {
        this.api().columns().every(function() {
          const column = this;
          const header = column.header();
          if (header.textContent.toLowerCase().includes('search')) {
            const select = document.createElement('select');
            select.className = 'form-control form-control-sm';
            select.innerHTML = '<option value="">-- Filter --</option>';
            
            column.data().unique().sort().each(function(d) {
              if (d) {
                const option = document.createElement('option');
                option.textContent = d;
                option.value = d;
                select.appendChild(option);
              }
            });

            select.addEventListener('change', function() {
              column.search(this.value ? '^' + this.value + '$' : '', true, false).draw();
            });

            header.appendChild(select);
          }
        });
      }
    };

    // Add server-side processing if enabled
    if (serverSide && ajax) {
      config.serverSide = true;
      config.ajax = typeof ajax === 'string' ? ajax : ajax;
    } else if (data) {
      config.data = data;
    }

    // Initialize DataTable
    const table = $(element).DataTable(config);

    // Add button export functionality if buttons provided
    if (buttons && buttons.length > 0) {
      new $.fn.dataTable.Buttons(table, {
        buttons: buttons.map(btn => ({
          extend: btn,
          className: 'btn btn-sm btn-outline-secondary'
        }))
      });
      table.buttons().container().appendTo(element.parentElement);
    }

    // Add row selection with checkboxes
    element.addEventListener('click', (e) => {
      if (e.target.type === 'checkbox' && e.target.closest('thead')) {
        const allCheckboxes = element.querySelectorAll('tbody input[type="checkbox"]');
        const isChecked = e.target.checked;
        allCheckboxes.forEach(cb => cb.checked = isChecked);
      }
    });

    return table;
  },

  /**
   * Reload DataTable data
   * @param {DataTables.Api} table - DataTable instance
   * @param {boolean} resetPaging - Reset to first page
   */
  reloadTable(table, resetPaging = true) {
    if (table) {
      table.ajax.reload(null, resetPaging);
    }
  },

  /**
   * Add row to DataTable
   * @param {DataTables.Api} table - DataTable instance
   * @param {Array} rowData - Row data
   * @param {boolean} redraw - Redraw table after adding
   */
  addRow(table, rowData, redraw = true) {
    if (table) {
      table.row.add(rowData);
      if (redraw) {
        table.draw();
      }
    }
  },

  /**
   * Update row in DataTable
   * @param {DataTables.Api} table - DataTable instance
   * @param {number} rowIndex - Row index
   * @param {Array} rowData - Updated row data
   */
  updateRow(table, rowIndex, rowData) {
    if (table) {
      table.row(rowIndex).data(rowData).draw();
    }
  },

  /**
   * Delete row from DataTable
   * @param {DataTables.Api} table - DataTable instance
   * @param {number} rowIndex - Row index
   */
  deleteRow(table, rowIndex) {
    if (table) {
      table.row(rowIndex).remove().draw();
    }
  },

  /**
   * Get selected rows data
   * @param {DataTables.Api} table - DataTable instance
   * @returns {Array} Array of selected rows data
   */
  getSelectedRows(table) {
    if (!table) return [];
    return table.rows({ selected: true }).data().toArray();
  },

  /**
   * Clear DataTable selection
   * @param {DataTables.Api} table - DataTable instance
   */
  clearSelection(table) {
    if (table) {
      table.rows().deselect();
    }
  },

  /**
   * Setup advanced filters for DataTable
   * @param {DataTables.Api} table - DataTable instance
   * @param {Array} filterConfigs - Filter configurations
   */
  setupFilters(table, filterConfigs = []) {
    if (!table) return;

    filterConfigs.forEach(config => {
      const {
        elementId,
        columnIndex,
        type = 'text'
      } = config;

      const element = document.getElementById(elementId);
      if (!element) return;

      element.addEventListener('change', () => {
        const value = element.value;
        table.column(columnIndex).search(value).draw();
      });

      if (type === 'date') {
        element.addEventListener('change', () => {
          const date = new Date(element.value);
          const dateStr = date.toISOString().split('T')[0];
          table.column(columnIndex).search(dateStr).draw();
        });
      } else if (type === 'range') {
        let minValue = '';
        let maxValue = '';

        element.addEventListener('input', () => {
          minValue = element.value;
          table.column(columnIndex).search(minValue).draw();
        });
      }
    });
  },

  /**
   * Export DataTable to CSV
   * @param {DataTables.Api} table - DataTable instance
   * @param {string} filename - Export filename
   */
  exportToCSV(table, filename = 'export.csv') {
    if (!table) return;

    const rows = [];
    const headers = [];

    table.columns().header().to$().each(function() {
      headers.push(this.textContent);
    });
    rows.push(headers.join(','));

    table.rows({ search: 'applied' }).data().each(function(row) {
      const values = Array.isArray(row) ? row : Object.values(row);
      rows.push(values.map(v => `"${v}"`).join(','));
    });

    const csv = rows.join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }
};

export default DataTableInit;
