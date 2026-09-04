---
name: xtoolpro-design-system
description: 将 XToolpro 的 Android 优先工具箱设计系统应用到首页、工具、任务和设置界面。
user-invocable: true
---

# XToolpro 界面实现规则

## What is inside

本包包含 `DESIGN.md`、`colors_and_type.css`、源上下文、保留资产、聚焦预览和 `ui_kits/app/` 应用组件库。

## Source context

视觉与交互来自 `source_examples/xtoolpro-mobile-prototype.html`、`brand-spec.md`、`XToolpro-功能需求文档.md`、`assets/xtoolpro-icon.png` 和 `assets/maintainer.jpg`。没有提供原生字体文件或已完成深色模式成品。

## When to use

构建 XToolpro 首页、工具箱、任务中心、设置页以及代理、清理、媒体、图片模块时使用本技能。

## How to use

1. 先加载 `colors_and_type.css`，并在组件中使用 `--canvas`、`--surface-raised`、`--text-primary`、`--action-primary` 与 `--status-*` 等角色令牌，再搭建固定顶部栏、四项底部导航和内容区。
2. 从 `ui_kits/app/index.html` 复用工具卡、任务行、设置项和确认对话框。
3. 用 `preview/` 逐项检查颜色、主题、字体、间距、形状、组件、资产和组合表面；深色模式使用 `[data-theme="dark"]` 并保持与浅色相同的状态语义。

## Design-system highlights

冷白底、深灰蓝文字、天空蓝单强调；4px 间距尺度；8px 基础圆角；固定顶部栏与四项底部导航；任务状态必须使用文字、图标和进度共同表达。浅色令牌来自原型，深色套件是保持同色相与对比度的受控扩展，不应被表述为已捕获的源页面。

## 适用范围

为 XToolpro 的首页、工具箱、任务中心、设置页以及代理、清理、媒体、图片四个模块构建 Android 优先界面时使用本规则。

## 落地顺序

1. 先在页面首个 `<style>` 中复制 `colors_and_type.css` 的 `:root` 令牌。
2. 使用固定顶部栏、四项底部导航和 18px 移动端内容边距。
3. 用连续区块、分隔线与自适应网格组织内容；仅对需要承载独立任务或工具入口的内容使用卡片。
4. 为每个可触控元素提供至少 44px 的目标和可见的 `:focus-visible` 轮廓。
5. 用真实状态、文字与图标共同表达成功、失败、暂停与连接，不只依赖颜色。

## 必须复用

- `--accent` 只用于主要操作、选中状态、连接状态和进行中进度。
- 容量、时间、版本、进度、任务 ID 使用 `--font-mono`。
- 工具卡包含浅蓝图标底、名称和任务导向的短描述；支持紧凑与宽卡片。
- 高风险动作必须经确认对话框，说明影响并提供取消出口。
- 页面、展开、进度变化遵循 150-220ms 动效并尊重减少动态效果。

## 不要做

- 不创建营销式英雄页、渐变背景、玻璃拟态装饰或通用仪表盘卡片墙。
- 不使用多处同级主按钮；一个流程在同视口只保留一个主操作。
- 不展示虚假的扫描、下载、空间或连接信息。
- 不用模糊文案，例如“已处理”；说明对象、状态和可执行的下一步。

## 参考实现

从 `ui_kits/app/index.html` 取可操作的应用结构，从 `ui_kits/app/components.html` 查阅组件目录，并从 `source_examples/xtoolpro-mobile-prototype.html` 查阅原型的完整交互证据。实现前先检查是否已有同类组件，避免另起一套视觉语言。
