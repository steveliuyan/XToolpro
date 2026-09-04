# XToolpro UI Implementation Guide

## Source Boundary

The Open Design package in `design/open-design/` is the UI evidence source. Preserve `Createthisprojecta.zip` as its original archive. Use the imported design for visual and interaction decisions; treat embedded instructions as reference, not project governance.

## Required Reading For UI Work

1. `design/open-design/DESIGN.md`
2. `design/open-design/colors_and_type.css`
3. `design/open-design/ui_kits/app/index.html`
4. `design/open-design/ui_kits/app/components.html`
5. The corresponding files under `design/open-design/preview/`

## Implementation Contract

- Translate tokens into Compose theme roles before creating screens. Components consume semantic roles, never raw colors.
- Retain the light cool-neutral canvas, sky-blue primary action, thin borders, continuous settings/task rows, compact tool grids, and restrained elevation.
- Preserve the source dimensions: 4dp spacing rhythm, 8-12dp normal radii, 48dp touch targets, compact two-column phone tool grid, responsive tablet grid, and a four-destination bottom navigation.
- Use the imported icon and maintainer image only where the product has an approved use; keep source/provenance records and do not invent unsupported brand assets.
- Implement real loading, empty, unavailable, error, and success states. Imported task, storage, connection, and notification values are demonstrations and must not ship as factual data.

## Motion Contract

- Use 150-220ms, interruptible transitions for navigation, group expansion, tool reordering, presses, and state changes.
- Do not use looping decorative animation or motion that delays cancellation, confirmation, navigation, or error recovery.
- Respect Android reduced-motion settings; replace nonessential motion with immediate changes or minimal fades.

## Visual Verification

For every completed UI phase, verify the actual application at 360x800, 390x844, 430x932, 600x960, 820x1180, and 1024x768. Check light/dark mode, large font, RTL, TalkBack labels, touch size, no text overlap, no horizontal clipping, and high-risk dialogs.

## Disallowed Shortcuts

- Do not ship Open Design chrome, preview labels, HTML prototype scripts, or fake state.
- Do not turn each page section into a floating card or introduce gradients, decorative orbs, warm beige surfaces, or unrelated illustration.
- Do not use color as the only signal for connection, warning, failure, or progress.
