# Phase 09: Localization, Accessibility, And Motion

**Status:** planned
**Depends on:** Phases 01, 03, and 04; applies to all feature phases
**Requirement IDs:** language, RTL, accessibility, and motion requirements

## Objective

Make the application globally usable without fragmenting the UI system or losing operational clarity.

## Required Deliverables

1. Localized resource architecture with no user-facing hard-coded strings in feature logic.
2. Language selector supporting System Default and Simplified Chinese, Traditional Chinese, English, Japanese, Korean, Spanish, French, German, Portuguese, Russian, and Arabic; names appear in their native language.
3. Locale/region formatting policy for dates, numbers, sizes, sort behavior, and output names; language and format preferences may differ.
4. Complete RTL layout support for Arabic while retaining LTR treatment for paths, URLs, IP addresses, code, commands, and protocol names.
5. TalkBack labels, logical focus/navigation order, dynamic text layouts, contrast validation, 48dp targets, and meaningful state copy for every active phase screen.
6. Central motion policy applied to navigation, sheets, layout reorder, progress updates, and engine-state feedback.

## Acceptance Criteria

- Language change does not lose task state, edits, preferences, or toolbox layout; any required restart is explicit and safe.
- Core flows, notifications, error messages, permission explanations, destructive-action confirmations, and licenses are localized with no missing keys or broken format variables.
- RTL screenshots show correct mirroring without corrupting LTR technical values.
- Screen reader can identify controls, state, errors, and progress without color-only cues.
- Reduced-motion mode remains fully usable and has no animation-driven input race.

## Evidence

Locale matrix, RTL and dynamic-font screenshots, accessibility scanner results, TalkBack walkthrough, and reduced-motion UI tests.
