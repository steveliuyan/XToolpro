# XToolpro

XToolpro is an Android-first, local-first toolbox that will integrate approved upstream capabilities for proxy connectivity, device maintenance, public-link media extraction, and image processing.

## Project Status

The project is in Phase 00. The current baseline contains the Android module graph, reusable build wrapper, local design evidence, core contracts, and quality gates. It intentionally contains no integrated upstream functionality yet.

## Build Prerequisites

- JDK 17
- Android SDK Platform 35 and Build Tools 35.0.0
- A local `local.properties` file pointing to the Android SDK

## Commands

```powershell
.\gradlew.bat verifyProject
.\gradlew.bat :app-shell:assembleDebug
.\gradlew.bat :app-shell:lintDebug
```

## Documentation

- [Agent rules](AGENTS.md)
- [Product requirements](XToolpro-功能需求文档.md)
- [Phase specs](docs/specs/README.md)
- [Upstream reuse ledger](docs/architecture/upstream-reuse-ledger.md)
- [Design implementation guide](docs/design/UI-IMPLEMENTATION-GUIDE.md)
