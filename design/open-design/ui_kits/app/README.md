# XToolpro Applied UI Kit

## Structure

This reusable application kit turns the preserved XToolpro mobile prototype into a composed, interactive shell. `index.html` mounts Home, Toolbox, Tasks, and Settings with source-backed navigation, task state controls, notifications, and high-impact confirmation.

| Path | Responsibility |
| --- | --- |
| `index.html` | Composed mobile-first shell. It loads `../../colors_and_type.css`, then the modular component stylesheet and behavior helper. |
| `components.html` | Component catalog for the structures reused by the composed shell. |
| `components/kit.css` | Shared applied-component styling, semantic surface hooks, and theme-toggle affordances. |
| `components/behaviors.js` | Reusable pressed-state, notification, and persisted light/dark-theme helpers. |
| `../../colors_and_type.css` | Root token contract that must load before kit styles. |

## Components

The following source-backed patterns are demonstrated by the component files and composed in `index.html`.

- **Tool card:** 34px icon container, name, concise task description, and independent entry/favorite affordances.
- **Task row:** icon, task name, status metadata, filled progress encoding where needed, and one trailing action.
- **Status notice:** an 8px status dot paired with current condition and next-step copy.
- **Settings row:** an approximately 64px continuous-list item with a function icon, short explanation, and arrow or switch.
- **Bottom navigation:** four fixed destinations for Home, Toolbox, Tasks, and Settings with sky-blue selected state.
- **Confirmation dialog:** bottom-aligned high-impact confirmation with title, effect explanation, cancel action, and exactly one primary confirmation action.

## Usage Workflow

1. Read `../../DESIGN.md`, `../../context/source-context.md`, and `../../context/provenance.md` to lock source constraints and visual rules.
2. Load `../../colors_and_type.css` first and `components/kit.css` second; retain both paths for every derived app surface.
3. Load `components/behaviors.js` before inline page wiring. Use `XToolproKit.restoreTheme()` at startup and `XToolproKit.setTheme()` for an explicit theme control instead of duplicating persistence logic.
4. Start from `index.html` for a complete shell. For a single feature, pair `components.html` with `../../source_examples/xtoolpro-mobile-prototype.html` to select the source-backed structure.
5. Bind only real status, permission, path, and task data. Replace demonstration values with explicit empty or loading states when no data exists.
6. Compare the result with `../../preview/` and verify touch targets, focus visibility, narrow-screen wrapping, state copy, and the single-primary-action rule.

## Reuse Guide

Use this applied kit as a Claude Design package: retain the token stylesheet and both component modules, compose only the documented component patterns, and validate a derived surface against the source prototype and focused previews. Do not copy demonstration task values into production; connect task, storage, permission, and connection UI to real local application state.

## How To Reuse This Kit

1. Open `index.html` as the composed reference surface, then choose the matching pattern from `components.html`.
2. Keep the stylesheet order: `../../colors_and_type.css` first and `components/kit.css` second. Load `components/behaviors.js` before the inline event wiring that calls its helpers.
3. Reuse the relevant tool card, task row, notice, settings row, navigation, or confirmation dialog structure without changing its state pairing, minimum touch target, or single-primary-action behavior.
4. Replace only the demonstration values with the product's real local state. When a task, path, permission, or connection record is unavailable, render an explicit empty, loading, or unavailable state.
5. Compare the result with `../../source_examples/xtoolpro-mobile-prototype.html` and the focused files under `../../preview/` before shipping.

## Component Composition

Compose a new XToolpro surface from the actual package files, in this order: load `../../colors_and_type.css`, load `components/kit.css`, place the source-backed structure selected from `components.html`, load `components/behaviors.js`, and then wire feature-specific events. Use the role tokens for surfaces, text, controls, focus, progress, and semantic state. `index.html` is the working reference for a composed mobile shell, not a static mock.

## Design Notes

The kit uses role tokens rather than direct color values: `--canvas` and `--surface-raised` establish layers; `--text-primary` and `--text-secondary` establish reading hierarchy; `--action-primary`, `--border-interactive`, `--focus-ring`, and `--status-*` establish interaction and state. The default is light; `[data-theme="dark"]` supplies a same-hue dark suite. Tool grids and continuous lists organize information without a wall of floating cards. Blue is reserved for actionable, selected, and in-progress states. Success, warning, and failure are always paired with copy or an icon; danger color never floods a task row.

## Source Basis

- `../../source_examples/xtoolpro-mobile-prototype.html` supplies component dimensions, layouts, interaction patterns, and source copy tone.
- `../../XToolpro-功能需求文档.md` supplies module scope, cleanup preview, permission timing, local-first processing, and background-task requirements.
- `../../colors_and_type.css`, `../../DESIGN.md`, and `../../brand-spec.md` supply tokens and system-level rules.
- `../../assets/xtoolpro-icon.png` supplies the local brand icon rendered by the composed shell.

The visible connection and task text in the kit are interaction examples, not device results. Production implementations must replace them with real application state. Inspect `../../preview/themes.html` before shipping a dark surface.
