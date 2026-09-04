# 30ef9dd4-794c-4ea1-858b-927c22960ade implementation handoff

This archive is the source of truth for turning the design into production code. Start from `preview/index.html`, then preserve the visual system, responsive behavior, and interactions found in the exported files.

## Implementation target
- Build production UI from the exported design, not a loose reinterpretation.
- Preserve typography scale, spacing rhythm, color tokens, border radii, shadows, motion timing, and component states.
- Replace static placeholders only when the target app has real data or functional equivalents.
- Keep generated product UI free of OpenDesign chrome, preview labels, or design-process annotations.
- Treat this handoff as a visual contract: if implementation choices conflict, match the exported pixels and behavior first, then refactor internals.

## Source map
- Primary entry: `preview/index.html`
- HTML screens detected: 17
- Stylesheets detected: 2
- Script/component files detected: 1
- Supporting assets detected: 39

## Responsive contract
Validate the implementation across this 2025–2026 viewport matrix:
- Mobile compact: 360×800
- Mobile standard: 390×844
- Mobile large: 430×932
- Foldable / small tablet: 600×960
- Tablet portrait: 820×1180
- Tablet landscape: 1024×768
- Laptop: 1366×768
- Desktop: 1440×900
- Wide desktop: 1920×1080

For responsive web exports, treat these as a modern breakpoint system for one adaptive web experience, not three fixed screenshots. Do not split responsive web into unrelated native app screens unless the project explicitly includes native targets. Use semantic layout thresholds, fluid `clamp()` type/spacing, and container queries where component width matters more than viewport width. Preserve any CSS media queries, container queries, fluid `clamp()` scales, and layout changes already present in the exported files.

## Design fidelity contract
- Extract reusable tokens before writing components: background, surface, foreground, muted text, border, accent, radius, shadow, spacing, type scale, and motion duration/easing.
- Map product screens, in-app modules/components, optional landing page, and optional OS widget surfaces before coding. Keep these surfaces separate in the target architecture.
- Match layout geometry: max-widths, gutters, grid columns, card proportions, sticky/fixed elements, and viewport-specific navigation.
- Preserve real copy, labels, and data shown in the export. Do not replace specific text with generic marketing filler.
- Preserve interactive affordances: hover, focus, pressed, disabled, loading, validation, copy/share, tab/accordion, modal/sheet, and keyboard states where present.
- Preserve accessibility semantics when converting: headings stay hierarchical, controls remain buttons/links/inputs, focus states stay visible.
- Do not keep prototype-only annotations, frame labels, or OpenDesign chrome in the production UI.

## CJX-ready UX contract
- Use `DESIGN-MANIFEST.json` as the machine-readable map for screens, app modules, OS widgets, landing pages, tokens, interactions, and viewport checks.
- Screen-file-first: when multiple user-facing surfaces exist, implement each HTML screen as its own route/file. Treat `index.html` as a launcher/overview when the manifest marks it that way, not as a combined final UI.
- If `landing.html`, app screens, platform screens, or OS widget files exist, preserve those boundaries in the target app instead of merging them into one page.
- A single self-contained `preview/index.html` is acceptable only when the export truly contains one user-facing screen and its CSS/JS are structured enough to extract tokens, components, states, and behavior.
- If separate `css/` or `js/` files exist, treat them as source of truth for token/component/interactions before porting to React, Vue, SwiftUI, Compose, or another target stack.
- In-app modules/components are product UI blocks inside the app. OS widgets are home-screen/lock-screen/quick-access surfaces outside the app. Do not merge those concepts.

## Color and brand contract
- Use the exported design tokens and product/domain context as the color source of truth.
- Do not introduce warm beige / cream / peach / pink / orange-brown background washes unless they are already explicit brand/reference colors in the export.
- A stylesheet or design/token file was detected; inspect it for canonical color variables before choosing framework theme tokens.

## Implementation sequence for AI coding tools
1. Open `preview/index.html` and `DESIGN-MANIFEST.json`; identify every screen file, launcher/overview file, app module, and interaction before coding.
2. If multiple HTML screens exist, map them to separate routes/surfaces first; do not merge `landing.html`, product app screens, platform screens, or OS widgets into one route.
3. Extract a token table from CSS/root styles and inline styles before building framework components.
4. Build product screens and domain-specific in-app modules from largest layout regions down to controls; avoid starting with isolated atoms that lose spatial intent.
5. Port responsive behavior across the modern viewport matrix and test each semantic breakpoint before cleanup.
6. Port interactions and states, then replace static placeholders only with real app data or functional equivalents.
7. Keep optional landing page and OS widget surfaces as separate surfaces if present.
8. Compare final screenshots against the export at 360×800, 390×844, 430×932, 820×1180, 1024×768, 1366×768, 1440×900, and 1920×1080 before declaring done.

## Entry points
- `preview/applied-ui.html`
- `preview/assets.html`
- `preview/colors-primary.html`
- `preview/colors.html`
- `preview/components-buttons.html`
- `preview/components.html`
- `preview/index.html`
- `preview/radius-shadows.html`
- `preview/spacing-tokens.html`
- `preview/surfaces.html`
- `preview/themes.html`
- `preview/type-spacing.html`
- `preview/typography-specimens.html`
- `source_examples/xtoolpro-mobile-prototype.html`
- `ui_kits/app/components.html`
- `ui_kits/app/index.html`
- `xtoolpro-mobile-prototype.html`

## Styles
- `colors_and_type.css`
- `ui_kits/app/components/kit.css`

## Scripts/components
- `ui_kits/app/components/behaviors.js`

## Assets and supporting files
- `维护者.jpg`
- `assets/maintainer.jpg`
- `assets/xtoolpro-icon.png`
- `brand-spec.md`
- `brand.json`
- `context/provenance.md`
- `context/source-context.md`
- `DESIGN.md`
- `drawing-2026-09-03T23-46-16-923Z.png`
- `drawing-2026-09-04T00-06-17-543Z.png`
- `drawing-2026-09-04T00-52-30-627Z.png`
- `drawing-2026-09-04T00-56-06-988Z.png`
- `drawing-2026-09-04T00-59-24-355Z.png`
- `drawing-2026-09-04T01-09-27-987Z.png`
- `drawing-2026-09-04T01-10-47-519Z.png`
- `drawing-2026-09-04T01-13-12-572Z.png`
- `drawing-2026-09-04T03-05-54-107Z.png`
- `drawing-2026-09-04T03-52-37-990Z.png`
- `drawing-2026-09-04T03-53-54-542Z.png`
- `drawing-2026-09-04T03-57-53-251Z.png`
- `drawing-2026-09-04T04-01-54-550Z.png`
- `drawing-2026-09-04T04-07-01-203Z.png`
- `drawing-2026-09-04T04-08-13-323Z.png`
- `drawing-2026-09-04T04-10-24-205Z.png`
- `drawing-2026-09-04T04-15-45-267Z.png`
- `drawing-2026-09-04T04-21-18-248Z.png`
- `drawing-2026-09-04T04-36-58-389Z.png`
- `drawing-2026-09-04T04-45-14-611Z.png`
- `preview/manifest.md`
- `preview/README.md`
- `README.md`
- `screenshot-2026-09-04T04-30-41-226Z.png`
- `SKILL.md`
- `ui_kits/app/README.md`
- `XToolpro-功能需求文档.md`
- `xtoolpro-creative-direction-preview.png`
- `xtoolpro-icon-tone-preview.png`
- `xtoolpro-polish-preview.png`
- `xtoolpro图标.png`

## Coding checklist for AI tools
1. Inspect `preview/index.html` and `DESIGN-MANIFEST.json` first and identify reusable components before coding.
2. Implement each user-facing screen file as its own route/surface; keep launcher, landing, app, platform, and OS widget files separate.
3. Extract design tokens into the target stack: colors, type scale, spacing, radius, shadows, and motion.
4. Implement layout with real 2025–2026 responsive breakpoints, fluid type/spacing, and container-query-aware component behavior; test with no horizontal overflow.
5. Preserve interactive controls, hover/focus/pressed states, form behavior, validation, and copy actions where present.
6. Implement domain-specific in-app modules with real states; do not flatten them into generic cards.
7. Keep landing page, product screens, and OS widget/quick-access surfaces separate when present.
8. Confirm the production result visually matches the exported design before refactoring internals.
9. Reject implementation shortcuts that flatten the design into generic cards, generic gradients, placeholder stats, or framework-default typography.
10. If a detail is ambiguous, keep the exported HTML/CSS/JS behavior rather than inventing a new pattern.
