# 上游固定源码许可审计（Phase 02）

**状态：** 调查中，不构成法律意见或发布批准

**适用阶段：** [Phase 02：上游复用与合规](../specs/02-upstream-reuse-and-compliance.md)

**需求追踪：** `PRO-*`、`CLN-*`、`MED-*`、`IMG-*`

**审计日期：** 2026-09-05

## 审计范围与方法

本记录只核对 `.tools/upstream-proofs/` 中四个固定 GitHub 源码归档内可读到的根 `LICENSE`、`NOTICE`、插件许可文件和 README 明示限制。归档哈希与提交锁定见 [Phase 02 证据](../specs/evidence/02-upstream-reuse-and-compliance-2026-09-04.md)，直接依赖范围见 [上游直接依赖盘点](upstream-dependency-inventory.md)。

本记录不会推定下列尚未解析项目的许可：Gradle 传递依赖、Maven/JitPack 解析物、AAR/JAR/SO 内容、Git submodule、yt-dlp/FFmpeg/Aria2c 插件、AI 模型或网络下载资源。它们只能在真实 proof 产生可验证的 resolved dependency tree 和二进制清单后进入最终 SBOM。

## 结果总览

| 域 | 固定源码中的根许可 | 可验证的附加边界 | 当前门禁 |
| --- | --- | --- | --- |
| Proxy / FlClash | GPL-3.0 | 候选 plugin 的三个 `LICENSE` 文件是 `TODO` 占位；另有 MIT 子组件。 | 未解决 placeholder 许可前，不得把对应 plugin 源码或二进制带入 XToolpro。 |
| Cleaner / sdmaid-se | GPL-3.0 | README 明确排除图标、logo、吉祥物、营销材料、assets、文档和翻译。 | 完整审查所有支持的代码闭包；所有被排除材料一律不移植。 |
| Media / ytdlnis | GPL-3.0 | README 禁止衍生下载器使用 `YTDLnis` 名称；上游功能含 Cookie、私有/不可用和高级格式流程。 | 采用 XToolpro 名称和自有界面；完整保留用户授权的会话能力；仅排除 DRM/访问控制绕过。 |
| Image / ImageToolbox | Apache-2.0 | 固定归档存在根 `LICENSE`，但没有根 `NOTICE`；可选功能涉及 native codec、OCR 与可下载模型。 | 分发时保留 Apache 许可和所选代码的版权归属；每个解析出的第三方/模型另审。 |

## Proxy：FlClash

固定提交 `62addf738a76b1a492e19af2dbabdb6d572b9e72` 的根 `LICENSE` 是 GNU GPL v3.0 文本。候选路径中同时观察到下列文件：

| 源码路径 | 可读许可结论 | 处理要求 |
| --- | --- | --- |
| `plugins/proxy/LICENSE` | 内容为 `TODO: Add your license here.` | 许可未声明；不得复制、分发或把该 plugin 当作批准依赖。 |
| `plugins/rust_api/LICENSE` | 内容为 `TODO: Add your license here.` | 许可未声明；不得复制、分发或把该 plugin 当作批准依赖。 |
| `plugins/window_ext/LICENSE` | 内容为 `TODO: Add your license here.` | 许可未声明；该 plugin 不在 Android engine 的默认候选范围，仍不得带入。 |
| `plugins/rust_api/cargokit/LICENSE` | MIT，版权声明为 Matej Knopp（2022） | 只有在实际闭包包含该代码时，才将完整 MIT 声明纳入 NOTICE。 |
| `plugins/wifi_ssid/LICENSE` | MIT，版权声明为 FlClash（2025） | 只有在实际闭包包含该代码时，才将完整 MIT 声明纳入 NOTICE。 |
| `core/Clash.Meta` | `.gitmodules` 声明外部子模块，固定归档不含其内容 | 在取得固定子模块提交、根许可、第三方 notices 和 native 依赖前，不得构建或分发 core。 |

这不是“FlClash 根 GPL 覆盖全部嵌套代码”的推断。针对具有 `TODO` 占位许可的组件，必须先从上游版权方取得明确许可声明或证明 engine route 完全不包含该组件，才能进入 `Approved`。

### FlClash Android 路径排除复核（2026-09-06）

- 三个占位文件在固定归档中的 SHA-256 均为 `422E0DE8E3275FEBF5C41A5CCF891F68F16BC40E1B5DCA26E50913B307EF794E`，内容仍为 `TODO: Add your license here.`；本次没有修改或替换这些文件。
- `plugins/proxy` 仅声明 Windows plugin，`plugins/window_ext` 仅声明 Windows/macOS plugin，`plugins/rust_api` 仅声明 iOS/Linux/macOS/Windows FFI plugin。源码引用也将 `proxy`、`window_ext` 和 `rust_api` 限定在桌面路径；Android `main` 仅在 desktop 条件下初始化 `RustLib`。
- 固定 Android proof APK（SHA-256 `4F374C68570EB4837B94D7026D594237035E18A97B84B27AAEEEBA4FAD7355EC`）的 ZIP 成员和 `classes.dex` ASCII 字符串扫描均未发现 `ProxyPlugin`、`RustLib` 或 `WindowExtPlugin`，也未发现 `rust_api`/`window_ext` plugin 成员；唯一包含 `proxy` 的成员是应用空状态图标 `assets/flutter_assets/assets/images/empty/proxy.svg`。
- 因此，Android `engine-proxy` 候选路径可以将这三个未声明许可插件列为**排除项**，但该排除证明不授予它们许可证，也不解除桌面/full-capability 路径的许可阻塞。未来构建必须保留依赖裁剪检查，防止它们重新进入 APK 或 engine 闭包。

## Cleaner：sdmaid-se

固定提交 `b9b01ee0af648fa6af25d388bb39bacde8d5b7a9` 的根 `LICENSE` 是 GNU GPL v3.0 文本。根 `README.md` 的许可证说明明确排除了以下材料：SD Maid SE 图标、logo、吉祥物、营销材料和 assets；文档；翻译。

因此 `engine-cleaner` 应完整复用所有支持的源代码逻辑。资源目录、Fastlane 元数据、README/文档、翻译 XML 和任何 SD Maid SE 品牌材料均不属于 fork-port 输入。构建成功后仍须审计全部 `app-tool-*` 实际解析的每个库及其 notice，GPL 根许可不能替代该审计。

## Media：ytdlnis

固定提交 `13320bb64f35c8d04f01bebfa782d7947758fb66` 的根 `LICENSE` 是 GNU GPL v3.0 文本。根 `README.md` 明确说明：除 GPLv3 源码外，其他方不得将 `YTDLnis` 名称用于下载器应用，衍生物包括 fork 和非官方构建。

同一 README 还描述了 Cookie 登录、私有/不可用媒体和高级格式能力。XToolpro 将这些作为完整媒体能力对照范围：用户主动提供且有权使用的 Cookie/登录会话、私有内容和高级格式必须保留；仅对 DRM、未授权访问或平台访问控制绕过保持明确拒绝。最终许可证审计还必须覆盖由 `youtubedl-android`、FFmpeg、Aria2c、解析器运行时和可更新插件实际引入的源代码与二进制。

## Image：ImageToolbox

固定提交 `cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0` 的根 `LICENSE` 是 Apache License 2.0。归档根目录没有 `NOTICE` 文件；因此在当前阶段不能虚构或预先复制“ImageToolbox NOTICE”。

若未来 fork-port 真实包含 Apache-2.0 代码，发布物至少应保留 Apache-2.0 许可、相关版权/归属说明与修改说明；如果完整支持的源码或其已解析依赖包含 NOTICE，再按各自条件纳入。`feature/code-preview` 的主题许可证和 `feature/pdf-tools` 的 Android 测试 PDF notice 不属于 `engine-image` 运行时处理闭包，不能被误报为运行时依赖。

README 还列出 OCR、OpenCV、ONNX、Tesseract/PaddleOCR 和可下载 AI 模型等能力。它们均不是凭根 Apache-2.0 许可即可分发的组件，必须有独立的来源、版本、校验和、许可、隐私和离线行为审计。

## 进入批准前的许可门禁

1. 为每个 `engine-*` 保存固定源码、patch 序列、真实 dependency tree、resolved component 列表和 native/模型二进制 SHA-256。
2. 移除、替换或取得 FlClash `plugins/proxy`、`rust_api`、`window_ext` 的明确许可；不得以根许可证猜测其授权状态。
3. 仅使用自有名称、图标、UI、翻译和市场材料；严格排除 sdmaid-se 的明示排除项及 ytdlnis 名称。
4. 对 GPL 路线完成对应源代码提供方式、可复现构建说明和修改声明审查；对 Apache 路线保留实际适用的 license/NOTICE/attribution。
5. 由维护人 `steveliuyan` 审核最终法律、分发和应用商店适用性后，才可把 [上游复用台账](../architecture/upstream-reuse-ledger.md) 的行更新为 `Approved`。
