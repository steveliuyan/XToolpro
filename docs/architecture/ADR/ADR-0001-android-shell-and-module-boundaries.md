# ADR-0001: Android Shell And Module Boundaries

**Status:** accepted
**Date:** 2026-09-04

## Context

XToolpro needs a stable Android shell that can integrate four mature open-source domains without allowing each upstream implementation to spread across the application. The application also needs a source-backed Compose UI and testable contracts for task/file infrastructure that will be added in later phases.

## Decision

- Use Kotlin, Gradle Kotlin DSL, Android Gradle Plugin 8.8.2, JDK 17, compile/target SDK 35, min SDK 26, and Jetpack Compose for XToolpro-owned shell code.
- Use `com.steveliuyan.xtoolpro` as the production application ID. Development builds append `.dev`.
- Maintain one application module, `app-shell`, and separate `core-model`, `core-platform`, `feature-*`, and `engine-*` modules.
- Only `engine-*` modules may depend directly on upstream repositories, native libraries, CLI tools, or isolated processes.
- Features depend on core contracts and their matching engine only. Features may not depend on one another.
- The root `verifyModuleBoundaries` task enforces the approved project-dependency graph.

## Consequences

- Upstream upgrades, licensing isolation, and engine crashes can be contained within a domain adapter.
- Cross-domain coordination must be expressed through versioned core contracts, which adds initial structure but prevents hidden coupling.
- The source Open Design package is converted into Compose tokens/components in Phase 01, rather than shipped as HTML/CSS/JS.

## Rollback

This decision is foundational. A future replacement of the build stack or a module merger/split requires a superseding ADR, a dependency-graph migration, build verification, and release rollback plan.

## Related

- `docs/specs/00-project-foundation.md`
- `docs/specs/01-design-system-and-app-shell.md`
- `docs/specs/02-upstream-reuse-and-compliance.md`
- `docs/architecture/upstream-reuse-ledger.md`
