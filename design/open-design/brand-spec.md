# XToolpro 视觉基线

XToolpro 采用清爽而克制的工具视觉：白色阅读底、深灰蓝文字、细边界和单一天空蓝强调色，优先让任务状态、文件信息和操作层级保持清晰且富有活力。

```css
:root {
  --bg: oklch(99% 0.004 235);
  --surface: oklch(100% 0 0);
  --fg: oklch(23% 0.026 240);
  --muted: oklch(49% 0.025 240);
  --border: oklch(89% 0.014 235);
  --accent: oklch(52% 0.15 235);
  --font-display: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif;
  --font-body: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui, sans-serif;
  --font-mono: 'JetBrains Mono', 'IBM Plex Mono', ui-monospace, Menlo, monospace;
}
```

- 使用信息密度适中的边界与留白，不将每一块内容包进浮层卡片。
- 天空蓝仅表达已连接、可执行和主要操作；其他状态用文字、图标和边框共同说明。
- 数字、容量、进度和任务标识使用等宽字体，便于快速扫读。
- 主导航固定在底部；页面标题、搜索和编辑等高频工具固定在可达区域。
- 过渡仅用于切换、展开和状态变化，时长控制在 150-220ms。
