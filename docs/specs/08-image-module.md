# Phase 08: Image Module

**Status:** planned
**Depends on:** Phases 02 and 04
**Requirement IDs:** `IMG-01` to `IMG-07` and applicable ImageToolbox parity items

## Objective

Integrate ImageToolbox-derived image capabilities through `image-engine`, preserving local processing, real previews, batch reliability, and original-file safety.

## Required Deliverables

1. Approved ImageToolbox reuse route and `image-engine` contract for import, inspection, edits, transformations, batch execution, metadata policy, export, cancellation, and errors.
2. Source selection through system picker/SAF, multi-select, and camera only when camera use is implemented and approved.
3. Reused upstream capabilities prioritized for crop, rotate, flip, resize, format conversion, compression, quality/size controls, filters/adjustments, metadata management, and batch presets.
4. Feature-gated advanced capabilities such as collage, PDF/GIF/SVG/QR work, OCR, watermarking, and background operations only when supported by the approved upstream path.
5. Non-destructive edit/session model with undo/redo where supported; output uses Phase 04 file transactions.
6. Large-image protection through sample sizing, tiling, memory limits, progress, cancellation, and recovery messaging.

## Constraints

- Processing is local unless a separately approved feature explicitly says otherwise.
- EXIF, ICC, GPS, and other metadata preservation/removal choices are visible before export.
- Do not overwrite the input asset by default; collision behavior is explicit.

## Acceptance Criteria

- Preview matches actual export within documented codec/color-space limitations.
- Batch processing isolates failures: one bad input neither crashes the queue nor loses successful outputs.
- Cancellation, process death, memory pressure, unsupported format, permission loss, and low storage preserve originals and report recoverable state.
- Large-image device tests meet defined memory/performance budgets without ANR.
- Contract tests cover transform success, unsupported operation, canceled batch, engine crash, and output verification.

## Evidence

Ledger/ADR links, visual regression samples, metadata tests, batch/recovery tests, and large-image device report.
