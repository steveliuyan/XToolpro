# Phase 01: Design System And App Shell

**Status:** planned
**Depends on:** Phase 00
**Requirement IDs:** `HOM-*`, `BOX-*`, global UI requirements

## Objective

Port the Open Design visual system into a reusable Android Compose design layer and create the navigable application shell. This phase produces genuine UI states, not feature simulations.

## Source Evidence

- `design/open-design/DESIGN.md`
- `design/open-design/colors_and_type.css`
- `design/open-design/ui_kits/app/index.html`
- `design/open-design/ui_kits/app/components.html`
- `docs/design/UI-IMPLEMENTATION-GUIDE.md`

## Required Deliverables

1. Semantic Compose theme tokens for canvas, surfaces, text, borders, action, status, spacing, shape, type, elevation, and motion; light and dark themes must share the same role contract.
2. Reusable components: top app bar, four-item navigation, icon button, primary/secondary/text actions, status notice, progress indicator, tool card, task row, settings row, confirmation sheet, empty/loading/error states.
3. Home, Toolbox, Tasks, and Settings shell routes with correct selected navigation and placeholder states explicitly labeled unavailable until later phases.
4. Responsive layouts for compact phones, large phones, foldable/small tablet, tablet portrait, and tablet landscape.
5. Focus, pressed, disabled, loading, and reduced-motion behavior for every interactive primitive.

## Non-Goals

- No real VPN, cleanup, media extraction, image processing, or persistent user settings.
- Do not copy web DOM, CSS, or JavaScript directly into the Android runtime.

## Acceptance Criteria

- UI roles visually match the source package: cool neutral canvas, sky-blue action/selected state, thin borders, restrained elevation, 4dp rhythm, 8-12dp radii, compact grid and continuous rows.
- All interactive elements are at least 48dp, expose meaningful accessibility labels, and use visible focus indicators.
- Screen captures at 360x800, 390x844, 430x932, 600x960, 820x1180, and 1024x768 show no overlap, clipped text, or horizontal overflow.
- System reduced motion causes nonessential transitions to become immediate or minimal without changing navigation behavior.
- No demo connection, storage, or task result is represented as real user data.

## Evidence

Provide screenshot comparisons, accessibility scan output, and UI test results for navigation and semantic states.
