# Phase 02: Upstream Reuse And Compliance

**Status:** planned
**Depends on:** Phase 00
**Blocks:** Phases 05-08

## Objective

Turn the four selected repositories into approved, maintainable integration plans. Prove technical feasibility and legal distribution obligations before domain code enters the main application.

## Upstream Sources

- FlClash: `https://github.com/chen08209/FlClash`
- sdmaid-se: `https://github.com/d4rken-org/sdmaid-se`
- ytdlnis: `https://github.com/deniscerri/ytdlnis`
- ImageToolbox: `https://github.com/T8RIN/ImageToolbox`

## Required Deliverables

1. Lock each upstream source to a tag or immutable commit and record the exact license, notices, transitive dependencies, build requirements, and security update history.
2. Complete `docs/architecture/upstream-reuse-ledger.md` with concrete component paths, reuse mode, owner, contract test plan, update policy, and rollback target for each domain.
3. Create an ADR for each nontrivial integration choice: official module, fork-port, engine adapter, isolated process, or minimal supplement.
4. Build a small isolated proof for each selected integration route. It must invoke a real upstream capability or prove why it cannot be invoked; a mocked capability is insufficient evidence.
5. Create SBOM and license/NOTICE inclusion plan for debug and release artifacts.
6. Define source-sync policy: fork remote, upstream remote, patch series, security alert response, update cadence, and rollback procedure.

## Decision Rules

- Prefer preserving upstream domain logic over translating it into a parallel implementation.
- GPL, LGPL, binary distribution, and app-store policy implications must be reviewed before integration approval; do not assume that a wrapper changes license obligations.
- A blocked integration must be marked `Blocked` in the ledger with an ADR; it cannot silently become a self-built substitute.

## Acceptance Criteria

- Every `PRO-*`, `CLN-*`, `MED-*`, and `IMG-*` family maps to an approved or explicitly blocked ledger row.
- Each approved route has a repeatable build/proof command and a proposed engine contract.
- License, security, privacy, native binary, and app-store constraints are recorded with owner and mitigation.
- At least one contract test scenario exists per engine: success, unavailable capability, cancel, engine crash, and version mismatch.

## Evidence

Commit/tag records, license review, proof build logs, ADRs, ledger entries, SBOM artifact, and contract-test plan.
