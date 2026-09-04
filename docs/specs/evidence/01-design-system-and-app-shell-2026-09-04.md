# Phase 01 Local Evidence

**Recorded:** 2026-09-04
**Phase status:** in progress
**Requirement scope:** HOM-*, BOX-*, global UI requirements

**Device verification:** Xiaomi M2102J2SC (`thyme`), Android device serial `bf353dda`, package `com.steveliuyan.xtoolpro.dev`.

## Implemented

- Semantic light/dark Material 3 color roles in `app-shell/src/main/kotlin/com/steveliuyan/xtoolpro/ui/theme/XToolproTheme.kt`.
- Four-destination shell navigation: Home, Toolbox, Tasks, and Settings.
- Responsive two-to-four-column toolbox grid with source-backed cool-neutral surfaces, sky-blue action state, thin borders, and compact 8dp radii.
- Reusable top bar, glyph action, tool card, notice, empty state, settings row, and outlined action primitives.
- 150-220ms interruptible destination fades with a system transition-scale reduced-motion path.
- Chinese default strings and English resources; all new user-visible copy is resource-backed.
- No proxy, cleaner, media, image, connection, storage, or task result is fabricated.
- Unicode glyph placeholders were replaced with Compose Material vector icons for navigation, tools, settings, empty state, info, and chevron actions. Stable-frame visual checks after the replacement are saved as `artifacts/phase01/icons-light-home.png` and `artifacts/phase01/icons-dark-home.png`.

## 真机手动验证

- 设备：Xiaomi M2102J2SC（`bf353dda`）。
- 验证对象：仅直接启动 `com.steveliuyan.xtoolpro.dev/com.steveliuyan.xtoolpro.MainActivity`；未运行测试 APK，未改变设备显示规格。
- 通过底部导航实际打开首页、工具箱、任务和设置。每次操作后 `mCurrentFocus` 都保持为 XToolpro 的 `MainActivity`，未跳转到测试或其他应用。
- 有效截图：
  - `artifacts/phase01/manual-dev-home-reverified.png`
  - `artifacts/phase01/manual-dev-toolbox-reverified.png`
  - `artifacts/phase01/manual-dev-tasks-reverified.png`
  - `artifacts/phase01/manual-dev-settings-reverified.png`
- 从首页快速入口进入工具箱，并点按“网络代理”；入口停留在应用内并显示“该功能将在引擎集成完成后开放。”：`artifacts/phase01/manual-dev-toolbox-unavailable.png`。
- 点按“语言”设置项同样停留在应用内并显示明确的未开放提示：`artifacts/phase01/manual-dev-settings-unavailable.png`。
- 首页当前层级已保存为 `artifacts/phase01/manual-dev-home-reverified.xml`。Xiaomi 的 UI Automator 会输出缺失 `theme_compatibility.xml` 的系统警告，但层级文件可正常生成；该警告不来自 XToolpro。

## Verified Locally

| Gate | Command or artifact | Result |
| --- | --- | --- |
| Formatting | `gradle --no-daemon --offline spotlessApply` | Passed after Compose naming suppression |
| Kotlin compilation | `:app-shell:compileDebugKotlin` | Passed |
| Android static analysis | `:app-shell:lintDebug` | `No issues found.` |
| Debug package | `:app-shell:assembleDebug` | Passed: `app-shell/build/outputs/apk/debug/app-shell-debug.apk` |
| Navigation test compilation | `:app-shell:compileDebugAndroidTestKotlin` | Passed: `ShellNavigationTest` covers Toolbox, Tasks, and Settings navigation outcomes. |
| Connected navigation tests | `:app-shell:connectedDebugAndroidTest` | Passed: 3 tests, 0 failures, 0 errors. Result XML: `app-shell/build/outputs/androidTest-results/connected/debug/TEST-M2102J2SC - 13-_app-shell-.xml`. |

## 自动化测试边界

- `connectedDebugAndroidTest` 会安装和管理 `com.steveliuyan.xtoolpro.dev.test` 测试包，因此它不能替代用户可见的真机手动验收。
- 后续该任务仅在独立模拟器或 CI 中执行；用户连接的手机只安装、启动并操作 `com.steveliuyan.xtoolpro.dev` 本体。
- 设备显示规格保持为原始 `1080x2340`、`440dpi`；不再为视口验证修改它。

## Remaining Gates

- 360x800、390x844、430x932、600x960、820x1180、1024x768 的截图比较仍待在独立模拟器完成；当前工作站没有已配置的独立 Android 虚拟设备，不能将用户手机的显示覆盖当作替代方案。
- 完整自动化无障碍扫描仍待补齐。导航测试已通过，但它不替代 TalkBack 与目标规格下的人工视觉验收。
- Phase 01 must remain in progress until those visual and device checks have fresh evidence.
