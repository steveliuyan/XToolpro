# Phase 05: Proxy Module

**Status:** planned
**Depends on:** Phases 02 and 04
**Requirement IDs:** `PRO-01` to `PRO-07`

## Objective

Integrate FlClash-derived proxy capability through `proxy-engine` while providing an XToolpro-native workflow, task/diagnostic contract, and Android VPN lifecycle.

## Required Deliverables

1. Approved FlClash reuse route recorded in the ledger and its integration ADR.
2. `proxy-engine` contract covering capability discovery, configuration import/update/validation, profile lifecycle, group/node selection, latency testing, connect/disconnect, traffic state, logs, and health status.
3. Configuration UI for permitted import sources, validation feedback, safe management, update results, and encrypted/Keystore-backed secret handling.
4. VPN UI for Android consent, connect/disconnect, connecting, connected, failure, and competing-VPN states.
5. Proxy mode, groups/nodes, optional rules and diagnostics only where the approved upstream route supports them.
6. Redacted connection, rule, and engine-log exports available only by explicit user action.

## Constraints

- Never claim anonymity, access-control bypass, or unrestricted operation.
- Do not expose subscription URL, proxy credentials, or private configuration in UI logs, notifications, backups, or analytics.
- Do not implement unsupported TUN, LAN, DNS, per-app routing, or kill-switch controls as inert UI; capability discovery decides visibility.

## Acceptance Criteria

- Fresh-device VPN permission flow, rejection, later grant, connection, disconnect, engine crash, and competing VPN state are verified on device.
- Invalid configuration fails before connection with clear remediation and leaves prior valid state untouched.
- Configuration secrets are encrypted at rest and excluded from default diagnostics.
- Engine contract tests cover success, unavailable feature, cancellation, process failure, and version mismatch.
- Connection status and traffic facts are derived from the engine, not simulated state.

## Evidence

Ledger/ADR links, device test recordings, contract tests, security review, and redacted diagnostic sample.
