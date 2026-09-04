# Phase 06: Cleaner Module

**Status:** planned
**Depends on:** Phases 02 and 04
**Requirement IDs:** `CLN-01` to `CLN-07`

## Objective

Integrate sdmaid-se-derived device maintenance while preserving Android storage constraints and ensuring every destructive action is previewable, explainable, and recoverable where possible.

## Required Deliverables

1. Approved sdmaid-se reuse route and `cleaner-engine` contract for scanning, category/risk information, duplicate detection, cancellation, cleanup execution, recovery state, and diagnostics.
2. Scope selection and scan workflow for supported categories: caches, residuals, thumbnails, temporary files, empty folders, install packages, large files, and duplicate files.
3. Grouped results with path/file detail, size, count, rule explanation, risk label, selection/exclusion, and protected-item handling.
4. Two-step cleanup confirmation that shows impact; recovery staging or explicit irrecoverability policy per category.
5. Storage analysis, search, and supported SAF file actions only within granted access.
6. Cleanup history with succeeded, skipped, failed, recovered, and blocked items.

## Constraints

- Non-root builds must not claim access to private app data, system partitions, or unsupported system-cleaning operations.
- System-critical, recently used, user-favorited, and protected paths are excluded by default unless an approved rule permits them.
- Never classify a file as disposable without source/rule evidence visible to the user.

## Acceptance Criteria

- User can inspect and deselect any candidate before final cleanup.
- Interrupted cleanup preserves originals or reports exact completed/skipped items; it never reports an uncertain result as success.
- Duplicate grouping has deterministic comparison criteria and does not auto-delete every duplicate.
- Permission loss, external storage removal, file mutation, low storage, and injected engine failure are tested.
- Device tests demonstrate behavior for scoped storage and SAF access on supported Android versions.

## Evidence

Ledger/ADR links, risk-rule inventory, preview/confirmation UI tests, file-safety tests, and physical-device storage test report.
