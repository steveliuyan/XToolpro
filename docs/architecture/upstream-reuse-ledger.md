# Upstream Reuse Ledger

This ledger is required before integrating an upstream component. One row may cover a coherent capability set only when its code path, license, and update policy are the same. Replace `TBD` with evidence during Phase 02.

| Domain | Requirement IDs | Upstream repository | Locked commit/tag | Reuse mode | Upstream path/component | License status | Android compatibility | Contract tests | Owner | Update/rollback policy | Status |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Proxy | `PRO-*` | `chen08209/FlClash` | TBD | TBD | TBD | Pending review | Pending review | Pending | steveliuyan | TBD | Planned |
| Cleaner | `CLN-*` | `d4rken-org/sdmaid-se` | TBD | TBD | TBD | Pending review | Pending review | Pending | steveliuyan | TBD | Planned |
| Media | `MED-*` | `deniscerri/ytdlnis` | TBD | TBD | TBD | Pending review | Pending review | Pending | steveliuyan | TBD | Planned |
| Image | `IMG-*` | `T8RIN/ImageToolbox` | TBD | TBD | TBD | Pending review | Pending review | Pending | steveliuyan | TBD | Planned |

## Allowed Reuse Modes

`official-module`, `fork-port`, `engine-adapter`, `isolated-process`, and `minimal-supplement`. `minimal-supplement` requires a linked ADR that explains why the first four modes cannot be used.

## Status Rules

- `Planned`: no source or license decision made.
- `Investigating`: source and build evidence is being collected.
- `Approved`: commit, license, integration mode, contract, and update plan approved.
- `Integrated`: contract tests pass in XToolpro.
- `Blocked`: an ADR explains the blocking condition and fallback.
