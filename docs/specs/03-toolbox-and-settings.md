# Phase 03: Toolbox And Settings

**Status:** planned
**Depends on:** Phases 00-01
**Requirement IDs:** `BOX-01` to `BOX-12`, settings and language preferences

## Objective

Deliver the customizable Toolbox and durable Settings layer that users use to discover all reused capabilities. It must work before any particular domain engine is enabled.

## Required Deliverables

1. Toolbox sections for All, Favorites, and Recent; default groups for Network, Device Maintenance, Media, and Image.
2. Edit mode with reorder, cross-group move, visibility management, favorite order, custom group create/rename/delete, compact/wide cards, section collapse, search, and guarded reset.
3. Versioned local layout schema that introduces newly available tools without overwriting user customization.
4. Settings persistence for theme, language, region format, notifications, output defaults, task concurrency, and layout preferences; unavailable settings are explicit rather than nonfunctional.
5. Import/export or reset behavior for user preferences only after a format and privacy ADR defines it.

## Constraints

- Toolbox cards represent actual available capabilities. Disabled engines show an explanation and supported action, not a fake launch.
- Delete-group behavior must require moving or hiding contained tools; it may not discard tools silently.
- Reset needs a confirmation sheet and option to preserve favorites.

## Acceptance Criteria

- Layout changes survive process death and application restart; clearing cache does not erase them.
- Search indexes name, alias, and functional keywords and can launch an available tool or locate it in edit mode.
- Phone uses two columns by default; adaptive widths use 3-6 columns and wide cards span two columns without overflow.
- Large font, RTL, TalkBack, dark mode, split screen, and reduced motion retain usable ordering and editing behavior.
- Automated tests cover schema migration, stable sorting, hide/restore, deletion confirmation, and reset semantics.

## Evidence

UI tests, persistence/migration unit tests, accessibility screenshots, and manual verification across required viewport/device classes.
