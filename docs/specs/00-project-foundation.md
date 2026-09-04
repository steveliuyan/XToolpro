# Phase 00: Project Foundation

**Status:** in progress
**Depends on:** none
**Blocks:** all implementation phases

## Objective

Create a reproducible Android project and governance baseline without implementing domain features. Establish module boundaries, build tooling, design-source traceability, code quality checks, and a safe contribution path.

## Inputs

- `AGENTS.md`
- `XToolpro-功能需求文档.md`
- `docs/product/PRD.md`
- `docs/design/UI-IMPLEMENTATION-GUIDE.md`
- `design/open-design/` source package

## Required Deliverables

1. Gradle Kotlin DSL project with a reproducible wrapper, AndroidX settings, app identifier, supported API declaration, debug/release build types, and version catalog or equivalent dependency management.
2. Empty but compiling modules for `app-shell`, `core-model`, `core-platform`, `feature-proxy`, `feature-cleaner`, `feature-media`, `feature-image`, and the four `*-engine` boundaries.
3. Enforced acyclic dependency graph that permits features to depend only on core contracts and their own engine adapter.
4. Repository standards: `.gitignore`, formatting/static-analysis configuration, contribution instructions, issue/PR template or equivalent checklist, and CI skeleton.
5. Design package inventory with checksums or source references; no imported HTML prototype is executed as application code.
6. Initial `ADR-0001` recording the Android shell stack and module dependency rules.

## Constraints

- Do not add an upstream dependency or copy upstream source in this phase.
- Do not create feature screens with fake completed data.
- New shell code should use Kotlin and Compose unless ADR-0001 approves a different boundary.
- Avoid irreversible project metadata choices until package id, signing ownership, and distribution channels are confirmed.

## Acceptance Criteria

- A clean clone runs the documented debug build successfully.
- Module dependency verification rejects a feature-to-feature dependency and accepts intended core/engine dependencies.
- CI runs formatting, static analysis, unit test discovery, and debug build at minimum.
- The app opens a neutral initialization state without requesting unrelated permissions.
- The architecture diagram and ADR match the actual Gradle module graph.

## Evidence

Attach build output, dependency graph output, CI result, and the ADR link to the phase completion record.

Current local evidence: `docs/specs/evidence/00-project-foundation-2026-09-04.md`.
