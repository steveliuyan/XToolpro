# XToolpro Design System Package

## Product Overview

XToolpro is a local-first utility toolbox for Android phones and tablets. It brings proxy connection, device maintenance, public-link media extraction, and image processing into a single task-based product. This package preserves the source product's visual language and reusable implementation patterns for future XToolpro surfaces.

### Primary Surfaces

- **首页 / Home** presents connection status, storage use, and quick tool entry points.
- **工具 / Toolbox** groups maintenance, public-link media, and image-processing tools in a compact grid.
- **任务 / Tasks** presents running, completed, and failed jobs with filtering, pause, resume, retry, and file-impact status.
- **设置 / Settings** provides continuous rows for output location, background notifications, and layout preferences.

### Core Capabilities

- Local-first processing, background-task progress, and recoverable task actions.
- Cleanup previews before file changes and just-in-time permission explanations.
- Compact touch-first navigation, tool discovery, and settings management.
- Clear state communication: color is always paired with status copy or an icon.

## Product Context

The package is derived from **XToolpro 工具箱原型**, whose captured metadata targets `mobile-ios` and `mobile-android`. The system is Android-first in touch sizing and small-screen reflow: a cool near-white reading field, deep gray-blue text, thin borders, a single sky-blue action color, continuous lists, and compact tool grids. It does not promise anonymity, absolute security, access-control bypasses, or risk-free cleanup.

## Source And Context

- `context/source-context.md` records source project metadata, copied files, and the handoff contract.
- `context/provenance.md` records the asset evidence boundary.
- `source_examples/xtoolpro-mobile-prototype.html` is the preserved full prototype snapshot for layout, component, and behavior comparison.
- `XToolpro-功能需求文档.md` defines module scope, cleanup preview, permission timing, local processing, and background-task constraints.
- `brand-spec.md`, `colors_and_type.css`, `assets/xtoolpro-icon.png`, and `assets/maintainer.jpg` preserve visual and local asset evidence.

## Package Contents

| Path | Purpose |
| --- | --- |
| `DESIGN.md` | Product context, visual foundations, layout, components, motion, voice, and anti-patterns. |
| `SKILL.md` | Reusable implementation instructions for new XToolpro surfaces. |
| `colors_and_type.css` | Shared source, role, and theme OKLch tokens; font stacks, spacing, radii, and motion variables. |
| `brand.json` | Stable design-system identifier, source references, product principles, theme contract, and local asset map. |
| `assets/` | Preserved local app icon and maintainer image. |
| `source_examples/` | Full source prototype snapshot retained for component and interaction comparison. |
| `preview/` | Focused review cards and `preview/manifest.md`. |
| `ui_kits/app/` | Composed interactive app shell with modular component styles and behavior helpers. |
| `build/` | Reserved for representative runtime assets when supplied by source evidence. |
| `fonts/` | Reserved for licensed source fonts; none were captured, so platform stacks are used. |

## Preview Manifest

| Exact path | Review purpose | Source-backed components or assets |
| --- | --- | --- |
| `preview/index.html` | Review hub for all focused cards. | Shared tokens and package navigation. |
| `preview/colors-primary.html` | Inspect accent, neutral, and semantic state pairings. | Prototype `:root`, `brand-spec.md`, shared token CSS. |
| `preview/colors.html` | Inspect the complete token palette and state labels. | Source prototype color values. |
| `preview/themes.html` | Compare source-faithful light roles and same-hue dark roles. | Source hue relationships, semantic-state rules, shared theme tokens. |
| `preview/typography-specimens.html` | Inspect display, body, and mono hierarchy. | Platform type stacks and source text hierarchy. |
| `preview/type-spacing.html` | Inspect typography with the 4px spacing scale. | Prototype typography and spacing rules. |
| `preview/spacing-tokens.html` | Inspect rhythm, gutters, and touch-target dimensions. | Source mobile layout and interaction sizing. |
| `preview/radius-shadows.html` | Inspect 8/10/12px shape rules and dialog elevation. | Source controls and confirmation dialog. |
| `preview/components-buttons.html` | Inspect primary, secondary, text, hover, and focus button states. | Source interaction patterns and tokens. |
| `preview/components.html` | Inspect tool cards, task rows, status messaging, and settings rows. | Preserved prototype component structures. |
| `preview/surfaces.html` | Inspect control surfaces and state pairing. | Source prototype controls and focus rules. |
| `preview/assets.html` | Inspect preserved app icon and maintainer image. | `assets/xtoolpro-icon.png`, `assets/maintainer.jpg`. |
| `preview/applied-ui.html` | Inspect home, toolbox, and task patterns in combination. | Source prototype and requirements document. |

## Reuse Guide

1. Read `context/source-context.md`, `context/provenance.md`, and `DESIGN.md` before changing a surface.
2. Load `colors_and_type.css` before any page-level stylesheet. Use role tokens such as `--canvas`, `--surface-raised`, `--text-primary`, `--action-primary`, and `--status-*` in components; do not bind component CSS to raw foundations.
3. Start from `ui_kits/app/index.html`, loading `ui_kits/app/components/kit.css` and `ui_kits/app/components/behaviors.js` with the token file.
4. Use `ui_kits/app/components.html` and `source_examples/xtoolpro-mobile-prototype.html` to select a source-backed tool card, task row, settings row, notice, or confirmation-dialog structure.
5. Bind real status, permission, path, and task data. Use explicit loading or empty states when data is unavailable.
6. Review with the preview cards, including `preview/themes.html`, then verify the composed shell on a narrow touch viewport in both theme modes.

## Review Workflow

Start at `preview/index.html`, then inspect `preview/colors-primary.html`, `preview/typography-specimens.html`, and `preview/applied-ui.html`. Finish in `ui_kits/app/index.html`: switch views, filter tasks, pause and resume a task, toggle notifications, and cancel or confirm the high-impact layout-reset dialog.
