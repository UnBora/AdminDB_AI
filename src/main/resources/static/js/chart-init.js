/**
 * Chart Initialization Module
 * Wrapper around Chart.js for common chart configurations
 */

export const ChartInit = {
  /**
   * Color palettes for charts
   */
  PALETTES: {
    primary: ['#0d6efd', '#0dcaf0', '#198754', '#ffc107', '#fd7e14', '#dc3545'],
    muted: ['#6c757d', '#a0a9b8', '#c3cad5', '#dde1e6', '#e9ecef', '#f8f9fa'],
    vibrant: ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#98D8C8', '#F7DC6F'],
    grayscale: ['#f8f9fa', '#e9ecef', '#dee2e6', '#adb5bd', '#6c757d', '#495057']
  },

  /**
   * Get color palette based on theme
   * @returns {Array} Color array
   */
  getColorPalette() {
    const isDark = document.documentElement.getAttribute('data-bs-theme') === 'dark' ||
                   window.matchMedia('(prefers-color-scheme: dark)').matches;
    return isDark ? this.PALETTES.vibrant : this.PALETTES.primary;
  },

  /**
   * Get common chart options
   * @returns {Object} Default chart options
   */
  getDefaultOptions() {
    return {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'top',
          labels: {
            padding: 15,
            font: { size: 12, weight: '500' }
          }
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          padding: 12,
          cornerRadius: 4,
          titleFont: { size: 13, weight: 'bold' },
          bodyFont: { size: 12 }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: { font: { size: 11 } },
          grid: { drawBorder: false, color: 'rgba(0, 0, 0, 0.05)' }
        },
        x: {
          ticks: { font: { size: 11 } },
          grid: { display: false, drawBorder: false }
        }
      }
    };
  },

  /**
   * Create line chart
   * @param {string} elementId - Canvas element ID
   * @param {Object} data - Chart data with labels and datasets
   * @param {Object} options - Chart options
   * @returns {Chart} Chart instance
   */
  createLineChart(elementId, data, options = {}) {
    const canvas = document.getElementById(elementId);
    if (!canvas) {
      console.error(`Canvas element with ID "${elementId}" not found`);
      return null;
    }

    const ctx = canvas.getContext('2d');
    const chartOptions = {
      ...this.getDefaultOptions(),
      ...options
    };

    // Add gradient colors to datasets
    data.datasets = data.datasets.map((dataset, index) => {
      const gradient = ctx.createLinearGradient(0, 0, 0, 400);
      const colors = this.getColorPalette();
      const color = colors[index % colors.length];

      gradient.addColorStop(0, color + '33');
      gradient.addColorStop(1, color + '00');

      return {
        fill: true,
        tension: 0.4,
        borderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6,
        ...dataset,
        borderColor: color,
        backgroundColor: gradient
      };
    });

    return new Chart(ctx, {
      type: 'line',
      data: data,
      options: chartOptions
    });
  },

  /**
   * Create bar chart
   * @param {string} elementId - Canvas element ID
   * @param {Object} data - Chart data with labels and datasets
   * @param {Object} options - Chart options
   * @returns {Chart} Chart instance
   */
  createBarChart(elementId, data, options = {}) {
    const canvas = document.getElementById(elementId);
    if (!canvas) {
      console.error(`Canvas element with ID "${elementId}" not found`);
      return null;
    }

    const chartOptions = {
      ...this.getDefaultOptions(),
      ...options
    };

    const colors = this.getColorPalette();
    data.datasets = data.datasets.map((dataset, index) => ({
      backgroundColor: colors[index % colors.length],
      borderColor: colors[index % colors.length],
      borderWidth: 1,
      borderRadius: 4,
      ...dataset
    }));

    const ctx = canvas.getContext('2d');
    return new Chart(ctx, {
      type: 'bar',
      data: data,
      options: chartOptions
    });
  },

  /**
   * Create pie chart
   * @param {string} elementId - Canvas element ID
   * @param {Object} data - Chart data with labels and datasets
   * @param {Object} options - Chart options
   * @returns {Chart} Chart instance
   */
  createPieChart(elementId, data, options = {}) {
    const canvas = document.getElementById(elementId);
    if (!canvas) {
      console.error(`Canvas element with ID "${elementId}" not found`);
      return null;
    }

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'right',
          labels: {
            padding: 15,
            font: { size: 12, weight: '500' }
          }
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const label = context.label || '';
              const value = context.parsed || 0;
              const total = context.dataset.data.reduce((a, b) => a + b, 0);
              const percentage = ((value / total) * 100).toFixed(1);
              return `${label}: ${value} (${percentage}%)`;
            }
          }
        }
      },
      ...options
    };

    const colors = this.getColorPalette();
    data.datasets = data.datasets.map((dataset) => ({
      backgroundColor: colors.slice(0, data.labels.length),
      borderColor: '#fff',
      borderWidth: 2,
      ...dataset
    }));

    const ctx = canvas.getContext('2d');
    return new Chart(ctx, {
      type: 'pie',
      data: data,
      options: chartOptions
    });
  },

  /**
   * Create doughnut chart
   * @param {string} elementId - Canvas element ID
   * @param {Object} data - Chart data with labels and datasets
   * @param {Object} options - Chart options
   * @returns {Chart} Chart instance
   */
  createDoughnutChart(elementId, data, options = {}) {
    const canvas = document.getElementById(elementId);
    if (!canvas) {
      console.error(`Canvas element with ID "${elementId}" not found`);
      return null;
    }

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            padding: 15,
            font: { size: 12, weight: '500' }
          }
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const label = context.label || '';
              const value = context.parsed || 0;
              const total = context.dataset.data.reduce((a, b) => a + b, 0);
              const percentage = ((value / total) * 100).toFixed(1);
              return `${label}: ${value} (${percentage}%)`;
            }
          }
        }
      },
      ...options
    };

    const colors = this.getColorPalette();
    data.datasets = data.datasets.map((dataset) => ({
      backgroundColor: colors.slice(0, data.labels.length),
      borderColor: '#fff',
      borderWidth: 2,
      ...dataset
    }));

    const ctx = canvas.getContext('2d');
    return new Chart(ctx, {
      type: 'doughnut',
      data: data,
      options: chartOptions
    });
  },

  /**
   * Create area chart
   * @param {string} elementId - Canvas element ID
   * @param {Object} data - Chart data
   * @param {Object} options - Chart options
   * @returns {Chart} Chart instance
   */
  createAreaChart(elementId, data, options = {}) {
    const chartOptions = {
      ...this.getDefaultOptions(),
      fill: true,
      ...options
    };

    return this.createLineChart(elementId, data, chartOptions);
  },

  /**
   * Update chart data
   * @param {Chart} chart - Chart instance
   * @param {Object} newData - New chart data
   */
  updateChart(chart, newData) {
    if (chart) {
      chart.data = newData;
      chart.update('active');
    }
  },

  /**
   * Destroy chart
   * @param {Chart} chart - Chart instance
   */
  destroyChart(chart) {
    if (chart) {
      chart.destroy();
    }
  }
};

export default ChartInit;
