/**
 * Theme Toggle Module
 * Handle dark/light mode switching with persistent storage
 */

export const ThemeToggle = {
  /**
   * Theme constants
   */
  THEMES: {
    LIGHT: 'light',
    DARK: 'dark'
  },

  /**
   * Storage key for theme preference
   */
  STORAGE_KEY: 'app_theme_preference',

  /**
   * Get current theme
   * @returns {string} Current theme (light or dark)
   */
  getCurrentTheme() {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (stored) {
      return stored;
    }

    const isDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    return isDark ? this.THEMES.DARK : this.THEMES.LIGHT;
  },

  /**
   * Set theme
   * @param {string} theme - Theme to set (light or dark)
   */
  setTheme(theme) {
    if (!Object.values(this.THEMES).includes(theme)) {
      console.warn(`Invalid theme: ${theme}`);
      return;
    }

    const html = document.documentElement;
    
    // Update Bootstrap theme
    html.setAttribute('data-bs-theme', theme);
    
    // Store preference
    localStorage.setItem(this.STORAGE_KEY, theme);

    // Update meta theme-color
    const metaThemeColor = document.querySelector('meta[name="theme-color"]');
    if (metaThemeColor) {
      metaThemeColor.setAttribute(
        'content',
        theme === this.THEMES.DARK ? '#212529' : '#ffffff'
      );
    }

    // Update CSS variables if needed
    this.updateCSSVariables(theme);

    // Emit custom event
    window.dispatchEvent(new CustomEvent('theme-changed', {
      detail: { theme: theme }
    }));

    console.log(`Theme changed to: ${theme}`);
  },

  /**
   * Toggle between light and dark theme
   * @returns {string} New theme
   */
  toggleTheme() {
    const current = this.getCurrentTheme();
    const newTheme = current === this.THEMES.LIGHT ? this.THEMES.DARK : this.THEMES.LIGHT;
    this.setTheme(newTheme);
    return newTheme;
  },

  /**
   * Update CSS custom properties based on theme
   * @param {string} theme - Theme to apply
   */
  updateCSSVariables(theme) {
    const root = document.documentElement;
    
    if (theme === this.THEMES.DARK) {
      root.style.setProperty('--bs-body-color', '#f8f9fa');
      root.style.setProperty('--bs-body-bg', '#212529');
      root.style.setProperty('--bs-border-color', '#495057');
    } else {
      root.style.setProperty('--bs-body-color', '#212529');
      root.style.setProperty('--bs-body-bg', '#ffffff');
      root.style.setProperty('--bs-border-color', '#dee2e6');
    }
  },

  /**
   * Initialize theme toggle with HTML element
   * @param {string|HTMLElement} toggleElement - Toggle button/element ID or element
   */
  initThemeToggle(toggleElement) {
    if (typeof toggleElement === 'string') {
      toggleElement = document.getElementById(toggleElement);
    }

    if (!toggleElement) {
      console.warn('Theme toggle element not found');
      return;
    }

    // Load saved theme
    const theme = this.getCurrentTheme();
    this.setTheme(theme);
    this.updateToggleIcon(toggleElement, theme);

    // Add click handler
    toggleElement.addEventListener('click', (e) => {
      e.preventDefault();
      const newTheme = this.toggleTheme();
      this.updateToggleIcon(toggleElement, newTheme);
    });

    // Listen for system theme changes
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (!stored) {
        const newTheme = e.matches ? this.THEMES.DARK : this.THEMES.LIGHT;
        this.setTheme(newTheme);
        this.updateToggleIcon(toggleElement, newTheme);
      }
    });
  },

  /**
   * Update toggle button icon/text
   * @param {HTMLElement} toggleElement - Toggle element
   * @param {string} theme - Current theme
   */
  updateToggleIcon(toggleElement, theme) {
    if (theme === this.THEMES.DARK) {
      toggleElement.classList.remove('btn-outline-dark');
      toggleElement.classList.add('btn-outline-light');
      toggleElement.innerHTML = '☀️ Light';
      toggleElement.setAttribute('aria-label', 'Switch to light theme');
    } else {
      toggleElement.classList.remove('btn-outline-light');
      toggleElement.classList.add('btn-outline-dark');
      toggleElement.innerHTML = '🌙 Dark';
      toggleElement.setAttribute('aria-label', 'Switch to dark theme');
    }
  },

  /**
   * Initialize theme toggle on DOM ready
   * Automatically find toggle button by class or ID
   */
  autoInit() {
    const toggleButton = document.getElementById('theme-toggle') ||
                        document.querySelector('.theme-toggle') ||
                        document.querySelector('[data-theme-toggle]');

    if (toggleButton) {
      this.initThemeToggle(toggleButton);
    } else {
      // Just load the theme
      this.setTheme(this.getCurrentTheme());
    }
  },

  /**
   * Listen for theme changes (custom event)
   * @param {Function} callback - Callback function(detail)
   */
  onThemeChange(callback) {
    window.addEventListener('theme-changed', (e) => {
      callback(e.detail);
    });
  }
};

// Auto-initialize on DOM ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    ThemeToggle.autoInit();
  });
} else {
  ThemeToggle.autoInit();
}

export default ThemeToggle;
