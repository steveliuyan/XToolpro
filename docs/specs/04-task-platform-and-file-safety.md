# Phase 04: Task Platform And File Safety

**Status:** planned
**Depends on:** Phases 00-01
**Requirement IDs:** `HOM-03`, `HOM-04`, shared task/file requirements

## Objective

Create the durable cross-module platform that drives scan, download, conversion, export, and engine work. This is the shared safety boundary for all high-impact operations.

## Required Deliverables

1. Versioned task contract with identifiers, input snapshot, output URIs, progress, lifecycle timestamps, normalized error, cancellation reason, recovery policy, and diagnostic reference.
2. Persistent state machine: queued, preparing, running, paused, canceling, succeeded, failed, and canceled. Invalid transitions are rejected and diagnosed.
3. Android-compliant scheduler/foreground-task implementation with notification channels and action handling.
4. Unified Tasks UI with filters, detail screen, pause/resume/cancel/retry, result actions, and no-data/error states.
5. SAF/MediaStore-based file access and transaction helpers implementing preflight, temporary destination/recovery staging, atomic commit, and post-write verification.
6. Common error mapping for permission, network, storage, unsupported format, user cancellation, missing engine, and unexpected failure.

## Safety Rules

- Retried work must be idempotent and must not overwrite originals or duplicate destructive changes.
- Process death, reboot, notification action, network change, storage exhaustion, and revoked access produce a recoverable state or explicit failure.
- Diagnostics are redacted by default; no raw paths, URLs, subscriptions, cookies, or content leave the device automatically.

## Acceptance Criteria

- A test task survives process recreation and resumes, waits for action, or fails explicitly rather than remaining indefinitely running.
- Pause, resume, cancel, and retry are independently testable and reflected consistently in database, UI, and notification.
- File transaction tests prove original preservation on cancellation, permission failure, collision, insufficient storage, and injected crash before commit.
- Engine modules can submit work only through the shared task contract, not by bypassing persistence or notifications.

## Evidence

State-machine tests, database migration tests, file-failure tests, notification/UI tests, and physical-device recovery results.
