# XToolpro Phase Specs

## Execution Order

| Phase | Spec | Goal | Exit Gate |
| --- | --- | --- | --- |
| 00 | `00-project-foundation.md` | Reproducible Android baseline and repository governance | Build, checks, module boundaries, and documentation baseline work |
| 01 | `01-design-system-and-app-shell.md` | Source-faithful Compose shell | Four destinations and design tokens verify on target viewports |
| 02 | `02-upstream-reuse-and-compliance.md` | Prove legal and technical reuse paths | Every domain has a reviewed reuse-ledger entry and ADR where needed |
| 03 | `03-toolbox-and-settings.md` | Customizable tool discovery and preferences | Toolbox personalization and settings persist safely |
| 04 | `04-task-platform-and-file-safety.md` | Shared durable task and safe file foundation | Recovery, notifications, and file transaction tests pass |
| 05 | `05-proxy-module.md` | FlClash-backed proxy integration | Approved engine contract and VPN flow work on device |
| 06 | `06-cleaner-module.md` | sdmaid-se-backed device maintenance | Preview-first scans and safe cleanup recovery work |
| 07 | `07-media-module.md` | ytdlnis-backed media integration | Queue, format selection, and compliant recovery work |
| 08 | `08-image-module.md` | ImageToolbox-backed image integration | Editing, batch output, and large-image recovery work |
| 09 | `09-localization-accessibility-motion.md` | Global, accessible, stable experience | Language, RTL, accessibility, and motion matrix passes |
| 10 | `10-quality-release-and-operations.md` | Release-ready operation | CI, SBOM, monitoring, rollback, and release gates pass |

## How To Use A Spec

An agent must read the active spec before planning or coding. Record discovered constraints in an ADR or reuse-ledger row, not only in chat. A phase is complete only when all listed exit criteria have fresh evidence. Do not combine or skip gates because a visual prototype looks complete.

## Common Definition Of Done

- Requirement IDs are traceable to the implementation and tests.
- UI follows `docs/design/UI-IMPLEMENTATION-GUIDE.md` and uses real state.
- Upstream reuse and license status are recorded where relevant.
- Error, cancellation, permission, process death, and retry behaviors are specified and tested proportionally to risk.
- Relevant documentation, localization resources, diagnostics, and release notes are updated.
