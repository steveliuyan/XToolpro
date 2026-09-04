/* Small, dependency-free helpers shared by applied kit pages. */
window.XToolproKit = Object.assign(window.XToolproKit || {}, {
  setPressed(button, pressed) {
    button.toggleAttribute('aria-pressed', pressed);
    button.dataset.state = pressed ? 'selected' : 'default';
  },
  announce(message) {
    const node = document.querySelector('[role="status"]');
    if (node) node.textContent = message;
  },
  setTheme(theme) {
    const root = document.documentElement;
    root.dataset.theme = theme;
    try { localStorage.setItem('xtoolpro-theme', theme); } catch (error) { /* Storage can be unavailable in embedded previews. */ }
    const toggle = document.querySelector('[data-theme-toggle]');
    if (toggle) {
      const dark = theme === 'dark';
      toggle.textContent = dark ? '浅色' : '深色';
      toggle.setAttribute('aria-label', dark ? '切换为浅色主题' : '切换为深色主题');
      toggle.setAttribute('aria-pressed', String(dark));
    }
  },
  restoreTheme() {
    let theme = 'light';
    try { theme = localStorage.getItem('xtoolpro-theme') || theme; } catch (error) { /* Use light when storage is unavailable. */ }
    this.setTheme(theme === 'dark' ? 'dark' : 'light');
  }
});
