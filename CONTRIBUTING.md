# Contributing To XToolpro

## Before Coding

1. Read `AGENTS.md` and the active phase spec under `docs/specs/`.
2. Read the relevant product, architecture, reuse-ledger, and design documents.
3. Confirm that the work maps to a requirement ID and active phase acceptance criterion.
4. For a new upstream capability, update the reuse ledger and create an ADR before integration.

## Quality Gate

Run the following before requesting review:

```powershell
.\gradlew.bat verifyProject
.\gradlew.bat :app-shell:lintDebug
.\gradlew.bat :app-shell:assembleDebug
```

Do not add direct dependencies between feature modules. Do not add user-visible hard-coded strings. Do not represent demo/prototype values as real device state.

## Review Checklist

- Tests were written first for new production behavior and observed failing before implementation.
- Appropriate error, permission, cancellation, recovery, localization, accessibility, and reduced-motion states are included.
- UI follows the source-backed design guide and is tested at required form factors.
- License, source, update, and rollback obligations are recorded for upstream code or binaries.
