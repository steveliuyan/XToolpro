# XToolpro PRD

**Status:** active product baseline
**Maintainer:** steveliuyan
**Product:** XToolpro for Android phones and tablets

## Product Definition

XToolpro is a local-first utility toolbox that unifies four proven capability domains: proxy connectivity, device maintenance, public-link media extraction, and image processing. The app provides one coherent navigation model, task center, permission experience, settings system, localization model, and visual language while retaining mature capabilities from the chosen upstream projects.

The detailed functional baseline is [XToolpro-功能需求文档.md](../../XToolpro-功能需求文档.md). This PRD organizes its product decisions for implementation; the two documents must be updated together when scope changes.

## Users And Outcomes

| User need | Product outcome |
| --- | --- |
| Quickly connect or change proxy configuration | Visible connection status, safe configuration import, and controllable VPN lifecycle |
| Reclaim storage without accidental data loss | Explainable scan results, preview, selection, and recovery-aware cleanup |
| Save media a user is entitled to use offline | Explicit format choices, resumable queue, output control, and compliance boundaries |
| Process images without uploading private content | Local editing, conversion, compression, batch work, and safe export |
| Find frequent tools quickly | Customizable toolbox, favorites, recent tools, search, and persistent layout |

## Product Scope

### In Scope

- Home, Toolbox, Tasks, and Settings as fixed primary destinations.
- Feature parity through upstream reuse for proxy, cleaner, media, and image domains.
- Local task persistence, task recovery, notifications, file output management, localization, accessibility, and motion.
- User-controlled language, theme, tool layout, notification, output, and privacy settings.

### Out Of Scope

- Accounts, cloud sync, social features, online drives, or telemetry containing content.
- Bypassing DRM, payment, geographic restrictions, platform access control, or copyright protections.
- Silent cleanup, unbounded background downloading, or claims of absolute anonymity/security.
- Unsupported root-level access or private-data cleaning on non-root devices.

## Experience Principles

1. Local and explicit: show real device state, required permissions, file impact, and task outcome.
2. Reuse mature capabilities: do not recreate behavior that a selected upstream project already provides.
3. Safe by default: preview destructive work, preserve originals, and recover or clearly fail interrupted tasks.
4. Compact and calm: dense enough for repeated work, without decorative surfaces or promotional UI.
5. Accessible globally: support specified languages, dynamic type, RTL, dark mode, screen readers, and reduced motion.

## Success Measures

- Core flow success rate at least 95% on supported devices.
- Crash rate below 0.5% and recoverable background-task interruption rate at least 90%.
- A user reaches any main domain in no more than two actions from Home.
- Every cleanup operation provides item-level preview before final deletion.
- No default collection of file content, proxy traffic, media content, cookies, subscriptions, or unredacted logs.

## Product Release Rule

No feature is releasable merely because a screen exists. It must meet the active phase spec, trace to an upstream reuse record or approved exception, handle permission/empty/error/recovery states, support required localization/accessibility behavior, and pass its risk-appropriate automated and device checks.
