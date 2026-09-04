# Phase 07: Media Module

**Status:** planned
**Depends on:** Phases 02 and 04
**Requirement IDs:** `MED-01` to `MED-07`

## Objective

Integrate ytdlnis-derived public-link media functionality as a compliant, resumable task workflow with real format information and controlled output handling.

## Required Deliverables

1. Approved ytdlnis reuse route and `media-engine` contract for URL analysis, available streams, queue management, download, post-processing, cancellation, recovery, diagnostics, and component version state.
2. URL input via paste, share, history, and supported batch input; show validation before enqueueing.
3. Detail/format selection showing real title, duration, thumbnail, media type, quality, codec/container, subtitle availability, and estimated size where supplied by the engine.
4. Queue controls for concurrency, pause, resume, cancel, retry, output selection, collision policy, and results/open/share actions.
5. Component management for approved yt-dlp/FFmpeg or equivalent artifacts, including version, source, integrity verification, update, and rollback policy.
6. User-controlled proxy usage via the current proxy engine only where both modules support it.

## Compliance Constraints

- Clearly state users must have rights to download and use content.
- Do not build a workaround for DRM, paid content, geographic restrictions, login/access controls, or platform protections.
- Cookies or credentials are user-supplied, encrypted, removable, excluded from logs, and never uploaded by default.

## Acceptance Criteria

- Analysis failure, unsupported URL, no network, engine update failure, output collision, low storage, and cancellation show actionable outcomes.
- A queue of at least ten independent tasks preserves correct order/state and allows individual pause/resume/cancel/retry.
- Restart and process-death tests resume only where supported, otherwise provide an explicit safe failure/retry path.
- Downloaded output is verified, named according to policy, and never silently overwrites an existing user file.
- Contract tests exercise success, unavailable format, partial transfer, engine crash, and component-version mismatch.

## Evidence

Ledger/ADR links, component supply-chain record, queue and recovery tests, compliance copy review, and device download report.
