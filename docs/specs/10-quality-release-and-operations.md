# Phase 10: Quality, Release, And Operations

**Status:** planned
**Depends on:** Phases 00-09
**Requirement IDs:** all release, reliability, privacy, and operational requirements

## Objective

Turn the integrated application into a release-managed product with measurable stability, controlled upstream updates, compliant distribution artifacts, and a tested rollback path.

## Required Deliverables

1. CI pipeline covering format, static analysis, dependency and license scan, unit/contract/UI tests, debug build, and release-candidate verification.
2. Test matrix for supported APIs, ABI, low-memory devices, storage modes, languages, RTL, dark mode, reduced motion, and relevant network/VPN states.
3. Release checklist covering versioning, signing, SBOM, notices, privacy policy, data safety declarations, upstream ledger review, permission audit, changelog, and rollback artifact.
4. Privacy-safe observability plan for startup time, task success/failure, ANR, crash, recovery, engine health, and release thresholds; no content or secret collection.
5. Staged rollout, incident triage, halt/rollback thresholds, and support playbook.
6. Fork/upstream update cadence, security response policy, compatibility test suite, and emergency pin/rollback procedure.

## Acceptance Criteria

- All phase gates have linked evidence; no planned/unknown ledger item is included in a release feature claim.
- Clean install, upgrade, and rollback preserve or safely migrate data and task records.
- Release candidate passes critical proxy, cleanup, media, image, task recovery, localization, accessibility, and file-safety regressions on the device matrix.
- SBOM and notices exactly represent shipped components; license obligations and source-distribution plan are approved.
- Observability shows thresholds for crash, ANR, task failure, and recovery; a threshold breach has a tested stop/rollback action.

## Evidence

Release-candidate CI report, signed artifact checksums, SBOM/notices, test matrix, privacy review, rollback rehearsal, and release approval record.
