# Phase 02 上游复用与合规证据

**记录日期：** 2026-09-04
**阶段状态：** in progress
**范围：** `PRO-*`、`CLN-*`、`MED-*`、`IMG-*`

## 固定来源记录

| 域 | 上游仓库 | 分支 | 已观察的固定提交 | 公开许可 | 初步结论 |
| --- | --- | --- | --- | --- | --- |
| Proxy | `chen08209/FlClash` | `main` | `62addf738a76b1a492e19af2dbabdb6d572b9e72` | GPL-3.0 | Android 构建需要 Flutter、Go、Android SDK/NDK 与 submodule；目标是完整代理/core/bridge 能力复用，替换 Flutter UI、品牌和资产。 |
| Cleaner | `d4rken-org/sdmaid-se` | `main` | `b9b01ee0af648fa6af25d388bb39bacde8d5b7a9` | GPL-3.0 | 原生 Android 多模块项目；目标是完整 `app-tool-*` 与 `app-common-*` 能力闭包，品牌资产、文档与翻译不在代码许可范围。 |
| Media | `deniscerri/ytdlnis` | `main` | `13320bb64f35c8d04f01bebfa782d7947758fb66` | GPL-3.0 | 原生 Android 项目，以 yt-dlp 和独立可更新插件为基础；不得在衍生下载器中使用 `YTDLnis` 名称。 |
| Image | `T8RIN/ImageToolbox` | `master` | `cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0` | Apache-2.0 | 原生 Kotlin/Compose 多模块项目；目标是完整支持的图片处理闭包，替换上游导航、UI、品牌和资产。 |

提交来自 2026-09-04 对各公开 GitHub 仓库分支页的只读观察；网络 CLI 连接不可用时，未用未验证的本地缓存替代来源事实。

## 架构与许可约束

- FlClash、sdmaid-se、ytdlnis 的代码路线均涉及 GPL-3.0，任何批准集成必须先包含对应源代码、构建说明、补丁序列和第三方组件审查。
- sdmaid-se 的图标、logo、市场材料、文档和翻译不可随代码带入；XToolpro 仅能使用自己的设计资产和本地化文案。
- ytdlnis 的名称、上游 UI 和资产不可用于 XToolpro。媒体插件、yt-dlp、FFmpeg 与 Aria2c 必须逐项锁定版本、校验和和许可证；用户主动提供且有权访问的 Cookie/session、私有内容和付费/高级格式属于支持能力；仅排除 DRM、未授权访问、平台明确禁止自动化获取或其他访问控制绕过。
- ImageToolbox 的仓库许可为 Apache-2.0，但 native codec、可选 AI model 和传递依赖仍需独立许可证与安全审查。
- 对应拟议路线和回滚边界见 `docs/architecture/ADR/ADR-0002-*.md` 至 `ADR-0005-*.md`；所有 ADR 均为 `proposed`，不是集成批准。
- 固定归档中已确认的许可、资源、名称与未声明 plugin 许可边界见 `docs/compliance/upstream-license-source-audit.md`；该审计不替代实际解析依赖与最终法律复核。

## 更新与安全审查基线

2026-09-05 通过 GitHub 官方公开 REST API 复核仓库状态与最新 release。四个仓库均公开、未归档且未禁用；此表用于固定首次同步基线，不能替代依赖漏洞扫描、私有 Dependabot 告警审阅或最终许可证审查。

| 域 | 最近默认分支提交时间（UTC） | 最新稳定 release | 发布时间（UTC） | Release 资产数 | 后续审查节奏 |
| --- | --- | --- | --- | ---: | --- |
| Proxy | `2026-09-04T14:12:21Z` | `v0.8.96` | `2026-08-17T07:30:21Z` | 14 | 每周；网络 core 与 VPN 依赖变动须立即专项审查。 |
| Cleaner | `2026-09-03T12:31:31Z` | `v2.0.4-rc0` | `2026-08-25T19:53:44Z` | 1 | 每月；文件扫描/删除链路或高危依赖变动须立即专项审查。 |
| Media | `2026-08-31T16:05:33Z` | `v1.8.9.1` | `2026-06-14T15:53:26Z` | 11 | 每周；yt-dlp、FFmpeg、Aria2c 或插件二进制变动须立即专项审查。 |
| Image | `2026-09-04T01:00:40Z` | `4.1.0` | `2026-07-05T20:15:20Z` | 8 | 每月；native codec、AI model 或图片解码依赖变动须立即专项审查。 |

GitHub CLI 在当前工作站不可用，因此本次只读复核使用官方 REST API；后续 CI 应使用受控 token 读取仓库安全告警，并把审查结果与 SBOM 差异一并保存。任何无法访问、没有可验证校验和或未通过漏洞审查的更新都不得进入 `Approved` 状态。

## 公开源码结构复核

2026-09-04 通过各固定提交对应的公开 GitHub 分支页复核，结果仅用于收窄后续取证和 proof 范围，不代表已经复制或集成代码。

| 域 | 已复核源码/行为边界 | 对 XToolpro proof 的影响 |
| --- | --- | --- |
| Proxy | FlClash 的根目录包含 `android/`、`core/`、`services/helper/` 与 `.gitmodules`；`setup.dart` 先激活 git-pinned 的 `flutter_distributor`，然后以 `split-per-abi` 打包 Android。其公开构建说明要求先初始化子模块，随后具备 Flutter、Go、Android SDK/NDK，并执行 `dart setup.dart android`。 | proof 必须覆盖完整配置、节点/代理组、模式/规则、VPN/TUN、DNS/IPv6、测速、流量/连接、日志、更新、备份和 bridge 路径；不能把 Flutter 界面、`assets/` 或上游 Android action 当成 XToolpro 的集成方案。 |
| Cleaner | sdmaid-se 将维护能力拆分为 `app-tool-corpsefinder`、`app-tool-appcleaner`、`app-tool-systemcleaner`、`app-tool-analyzer` 与 `app-tool-deduplicator`，并共享 `app-common-*` 闭包。 | proof 必须覆盖全部工具的扫描、分类、规则解释、重复项、存储分析、SAF 文件操作、预览、清理、恢复、历史和可选 Root/Shizuku 路径；普通 SAF 无 root 仍是有效能力路径，不能只交付 CorpseFinder。 |
| Media | ytdlnis 的核心 Android 源码位于单一 `app/` 模块；固定 `app/build.gradle` 声明 `youtubedl-android` library 0.18.1、Aria2c 0.18.1、FFmpeg 0.17.2、WorkManager 与 Room，并对 x86/x86_64/armeabi-v7a/arm64-v8a 进行 ABI 打包。公开功能还包含 Cookie、私有/不可用媒体、付费格式和后台 intent 入口，插件可更新 Python、JS runtime、FFmpeg、Aria2c。 | XToolpro proof 必须覆盖完整媒体 workflow：公开媒体，以及测试账户主动提供且明确有权访问的 Cookie/session、私有内容和付费/高级格式；不得裁剪为仅队列或公开 URL。每个插件都需要单独的许可证、校验和和回滚记录；仅拒绝 DRM、未授权访问和访问控制绕过。 |
| Image | ImageToolbox 固定提交包含 `core/`、`lib/` 和多个 `feature/` 处理路径，覆盖编辑、滤镜、编解码、批处理、元数据和高级工具。 | proof 必须覆盖完整支持的处理能力与其 native/模型闭包；上游 Compose 页面、导航、品牌和 demo 内容由 XToolpro 自己实现，但不得以此缩减处理功能。 |

## 本地固定源码归档

2026-09-04 已从各固定提交的公开 GitHub 归档端点取得下列 ZIP。所有归档均在 Git 忽略的 `.tools/upstream-proofs/` 中隔离保存，并使用 `System.IO.Compression.ZipFile` 打开校验；它们不代表已复制、修改或集成到 XToolpro。

| 域 | 归档文件 | ZIP 条目数 | SHA-256 |
| --- | --- | ---: | --- |
| Proxy | `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72.zip` | 1,030 | `70042455690F88D8CFD070F7EA6B9269060C42745BD8F7E90AD7EE3826FA48A8` |
| Cleaner | `sdmaid-se-b9b01ee0af648fa6af25d388bb39bacde8d5b7a9.zip` | 7,362 | `3EF93551ECFEAA7CCAFA71772C8EA81C08F1F52EA314D5B0CB45C8310E1FCA4E` |
| Media | `ytdlnis-13320bb64f35c8d04f01bebfa782d7947758fb66.zip` | 1,067 | `F859B856D846801B42B3063B00CB36C002E6708DC469297836DEB6545FF7C98D` |
| Image | `ImageToolbox-cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0.zip` | 已在原有 proof 中解压核验 | `CBCA2FB7E9235AD96C89498B0A851D2AFAC4EDF3625BAD37D88C1188439B08C8` |

归档中的源码复核补充了以下构建边界：

- FlClash 的 Android 部分实际包含 `android:app`、`android:core`、`android:common`、`android:service`；其 `setup.dart` 会激活固定分支的 `flutter_distributor` 并以 `split-per-abi` 打包。该路径仍须 Flutter、Go、Android SDK/NDK、submodule 和 native core 才能开始真实 engine proof。
- 固定提交对应的上游 CI 配置进一步锁定了首轮可复现 proof 的工具链：Flutter `3.44.4`、Go `1.26.4`、Android NDK `r28c`，并且 checkout 使用 `submodules: recursive`。本机的 `core/Clash.Meta` 目录目前不含 `go.mod`，因此是归档留下的空 gitlink 目录，不是已取得的内核源码。
- sdmaid-se 的 Wrapper 固定为 Gradle 9.7.1，并带 SHA-256 `acd53f1edaf02f1a8ff99879f8a34b302661a057d9b063ae9e35b552f804d20a`。CorpseFinder 的十个直接 `app-common-*` 项目依赖已从本地固定源码复核。
- ytdlnis 的 Wrapper 固定为 Gradle 8.13；根构建固定 AGP 8.13.2、Kotlin 2.3.0 与 KSP 2.3.4。`app/build.gradle` 直接声明 `youtubedl-android` library 0.18.1、Aria2c 0.18.1、FFmpeg 0.17.2，并启用四 ABI split。

当前工作站的最小启动复核结果如下：

- `Get-Command flutter,dart,go` 没有返回任何命令。FlClash 的 `.gitmodules` 声明 `core/Clash.Meta`，而 GitHub ZIP 归档不会包含该 submodule。2026-09-05 已通过官方 contents API 将该 gitlink 锁定到 `0f7f05adff5e2c49775a112dcfe05a6aa36fda0c`；在未安装 Flutter/Go、取得该精确子模块和 native core 前，不能诚实地声称已调用其真实代理能力。
- 2026-09-05 已从 `https://github.com/chen08209/Clash.Meta.git` 克隆到 Git 忽略的隔离 proof 工作树，并 detach checkout `0f7f05adff5e2c49775a112dcfe05a6aa36fda0c`；`git rev-parse HEAD` 返回相同值，工作树干净，`LICENSE` 为 GPL-3.0。此操作只补齐上游源码，不引入任何 XToolpro 生产模块，也没有启动构建或设备操作。
- 在固定 sdmaid-se 源码目录执行 `gradlew.bat --no-daemon --version`，其 Gradle 9.7.1 Wrapper 在 `org.gradle.wrapper.Download.download` 报 `java.net.SocketException: Permission denied: getsockopt`。因此尚未进入上游项目配置或 CorpseFinder 扫描阶段。
- 对 `downloads.gradle.org` 的只读请求会重定向到官方 GitHub Release；在本机该 Release 连接被重置或在 15 秒内超时。没有使用第三方镜像规避来源、校验和或供应链审查。

## ImageToolbox 源码取证

- 已从公开 GitHub 页面下载 `master` 分支归档；下载时页面显示该分支提交为上表的 `cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0`。
- 隔离归档的 SHA-256：`CBCA2FB7E9235AD96C89498B0A851D2AFAC4EDF3625BAD37D88C1188439B08C8`。
- 本地归档位于 Git 忽略的 `.tools/upstream-proofs/`，未复制到任何 XToolpro 生产模块。
- `settings.gradle.kts` 确认 `feature:resize-convert`、`feature:weight-resize`、`feature:format-conversion` 等功能依赖于多个 `core:*` 与 `lib:*` 模块。源代码检索也确认图像处理接口与实现贯穿 domain、data 和 presentation 层，不能把单个 UI 文件当作可复用引擎。

## 2026-09-05 构建分发与工具链复核

本次复核只读取本机工具链与缓存，并对 Gradle 官方地址执行无落盘的 `HEAD` 和 1 KiB range 请求；没有启动上游 Gradle 构建、下载完整分发包、改写 wrapper、使用第三方镜像或操作设备。

| 检查项 | 结果 | 结论 |
| --- | --- | --- |
| 本机命令 | 仅发现 Temurin JDK `17.0.20.1` 与 Git；`flutter`、`dart`、`go` 不存在 | FlClash 仍无法开始 Flutter/Go/submodule 真实 core proof。 |
| Android SDK | XToolpro 的 `local.properties` 已声明 SDK 路径；`ANDROID_HOME`、`ANDROID_SDK_ROOT`、`ANDROID_NDK_HOME` 均未设置 | SDK 可用性必须在每个隔离 proof 目录以显式本地配置复核，不能假设继承。 |
| Gradle 本地缓存 | 完整的 Gradle `8.10.2` 与本项目有关；所需 Gradle `9.7.1` 只有 `4,091,904` 字节的 `zip` 和 `6,778,496` 字节的 `.part`，且 ZIP 缺失中央目录；Gradle `8.13` `.part` 为 `0` 字节 | 这些文件不是可用分发，不得用于 ImageToolbox、sdmaid-se 或 ytdlnis proof。 |
| 受限执行环境的官方 HEAD | 连接 `services.gradle.org:443` 失败 | 受限执行环境不能为 wrapper 获取官方分发。 |
| 受控网络下的官方 HEAD | `services.gradle.org` 正常重定向到 Gradle 官方 GitHub Release 资产，并返回 Gradle `9.7.1` 文件长度 `151,433,392` 字节 | 官方元数据路径可访问，但这不证明正文可下载。 |
| 受控网络下的官方正文 range | 默认网络和强制 IPv4 均在连接 `github.com:443` 的 15 秒超时；请求只针对 `0-1023` 字节并输出到空设备 | 当前阻断位于 GitHub Release 正文连接，和 wrapper 配置、Gradle ZIP 内容、IPv4/IPv6 选择无关。 |

当前假设已由两种地址族下相同的最小请求支持：本机到 GitHub Release 正文端点的连接不可用或不稳定，而不是上游 Gradle 构建脚本错误。恢复真实 proof 的最低条件为：

1. 恢复到官方 Gradle 分发及其 GitHub Release 资产的稳定正文下载；或由受控、可审计的渠道预置**官方原文件**，并在使用前以官方发布的校验和验证。
2. 分别取得 upstream wrapper 指定的 Gradle `9.7.1`（ImageToolbox、sdmaid-se）和 `8.13`（ytdlnis），而不是复用本机的 `8.10.2`。
3. 为 FlClash 安装并记录兼容的 Flutter、Dart、Go、Android NDK，并以固定 commit 获取 `core/Clash.Meta` 子模块。

在上述条件满足前，四个台账行继续保持 `Investigating`；本节记录的是可复现的环境不可用证据，不是对上游复用路线的技术否决。

## Proof 状态

## ytdlnis 与 yt-dlp 核心链路复核（2026-09-05）

本次复核确认 ytdlnis 不是独立实现的站点下载器，而是 yt-dlp 的 Android 适配与工作流编排层。固定提交的 README 明确说明其使用 yt-dlp；源码的 `app/build.gradle` 对三个 product flavor 均声明以下 Android 运行时依赖：

| 层 | 固定来源/版本 | 作用 |
| --- | --- | --- |
| Android bridge/runtime | `io.github.junkfood02.youtubedl-android:library:0.18.1` | 在 Android 进程内初始化并执行 yt-dlp |
| 下载加速 | `io.github.junkfood02.youtubedl-android:aria2c:0.18.1` | Aria2c 原生下载组件 |
| 后处理 | `io.github.junkfood02.youtubedl-android:ffmpeg:0.17.2` | FFmpeg/FFprobe 原生处理组件 |
| yt-dlp 核心 | ytdlnis `YTDLUpdater` 管理的 `yt-dlp` 独立二进制 | 从 GitHub release API 按 stable/nightly/master 或指定版本更新 |
| 运行时插件 | ytdlnis component-management 路径 | Python、Node/Deno、FFmpeg、Aria2c 的版本切换、更新和回滚 |

固定源码还公开声明了播放列表、格式选择、字幕/元数据/章节、SponsorBlock、时间裁剪、模板/自定义命令、内置终端、Cookie/session、用户授权的私有/不可用/高级格式、后台队列、历史/日志、备份恢复等能力。XToolpro 的 `media-engine` 必须复用这条完整链路；只复用 ytdlnis UI 或自行调用一个简化下载接口均不满足 MED 能力基线。

### ytdlnis 官方固定提交构建证据

- 固定提交：`13320bb64f35c8d04f01bebfa782d7947758fb66`。
- 官方 Gradle 8.13 分发 SHA-256：`20F1B1176237254A6FC204D8434196FA11A4CFB387567519C61556E8710AED78`；在隔离 `GRADLE_USER_HOME` 和 `-Dorg.gradle.native=false` 环境下运行成功。
- 命令：` :app:assembleGithubDebug`，结果 `BUILD SUCCESSFUL`，耗时约 36 分钟；完整日志见 `artifacts/phase02/ytdlnis-assemble-github-debug-2.log`。
- 生成 APK：
  - `YTDLnis-1.8.9.2-arm64-v8a-debug.apk`，SHA-256 `496BE8A73D313C45FE39F2D1B4A72B3B63264F32A02DF358AAD5A26F60D5F6B7`。
  - `YTDLnis-1.8.9.2-armeabi-v7a-debug.apk`，SHA-256 `96CA55DFCB3BB5FE485AD97B0BCF8C48F22F393AE9F921FA3593092F43D3AD8E`。
  - `YTDLnis-1.8.9.2-universal-debug.apk`，SHA-256 `A5847C3D14E8E81E43224F2D5C9A728D72471D5CFB11286DE9A0D8A8F7A069DA`。
  - `YTDLnis-1.8.9.2-x86_64-debug.apk`，SHA-256 `C0BBE1F261D21D546FAB8D5CE9D5FA7543FB6CE8F6A8CFF5C7E60F41A7C87747`。
  - `YTDLnis-1.8.9.2-x86-debug.apk`，SHA-256 `4F6527C0B961B2AD7E786B48BF49D14B196320CEF3E83554DBA394A6B0158773`。
- universal APK 的四个 ABI 均包含 `libpython`、`libffmpeg`、`libffprobe`、`libaria2c`、`libqjs` 和 `libtermux`；这证明构建保留了 yt-dlp Android 运行时闭包，而不是只打包 Java/Kotlin UI。
- universal APK 还包含 `res/raw/ytdlp`，大小 `3,170,726` 字节；其内容以 Python shebang 后接 ZIP 包开始，并包含 `yt_dlp/YoutubeDL.py`。从包内 `yt_dlp/version.py` 复核到版本 `2025.11.12`、上游提交 `335653be82d5ef999cfc2879d005397402eebec1`、`ORIGIN=yt-dlp/yt-dlp`、`CHANNEL=stable`。该内置 zipapp 的 SHA-256 为 `89A0D9058EA9018E380B7771898FF46E393A1986DCD13FEF331693C87CE1FCA4`。因此 yt-dlp 核心确实随 Android 产物进入运行时，`YTDLUpdater` 负责后续按 release channel 更新它。
- 固定提交的 `settings.gradle` 仍声明不存在的 `common`、`library`、`ffmpeg` 目录。Gradle 8.13 目前只给出弃用警告并继续构建；该上游结构问题必须在 fork/port ADR 中显式处理，不能把 proof-only 配置目录当作生产修复。

以上仅证明固定 ytdlnis/yt-dlp 运行时可以构建并保留原生闭包，不证明 XToolpro 已完成真实解析、Cookie/session、私有/高级格式、下载恢复或设备验证。因此 Media 台账仍保持 `Investigating`/`Pending`，下一道门禁是 `media-engine` 的公开 URL 与用户授权 session 合同测试。

| 域 | 真实能力 proof | 当前证据 | 状态 |
| --- | --- | --- | --- |
| Proxy | arm64 core/bridge 已完成；设备验证未启动 | 固定源码归档、GPL 文本、Android 模块边界和 Flutter packaging 路径已验证；arm64 Clash.Meta core、JNI bridge 和 Android service AAR 已完成真实构建 | Pending |
| Cleaner | 未完成 | 固定源码归档、GPL 文本、Gradle 9.7.1 wrapper 与 CorpseFinder 模块闭包已验证；已启动 `:app-tool-corpsefinder:assembleDebug`，但在 `buildSrc` 的外部构件下载读取阶段阻塞，真实 scanner 仍未启动 | Pending |
| Media | 未启动 | 固定源码归档、README 的 yt-dlp 适配声明、Gradle 8.13 构建、五个 ABI APK 及 Python/FFmpeg/Aria2c/JS/Termux 原生闭包已验证；真实 parse/download、授权 session 和设备测试仍未启动 | Pending |
| Image | 未完成 | 固定源码、模块图、归档哈希与 Gradle 9.7.1 已验证；已启动 `:lib:image:assembleDebug`，但在外部依赖解析/下载阶段阻塞，真实图像处理器仍未启动 | Pending external dependency connectivity |

Image proof 的有效命令为：

```powershell
$env:GRADLE_USER_HOME = 'D:\xtoolpro\.gradle-upstream-image'
.\gradlew.bat --no-daemon :lib:image:assembleDebug
```

执行日志为 `artifacts/phase02/imagetoolbox-network-proof.log`。上游 Wrapper 开始下载 `gradle-9.7.1-bin.zip` 后，在五分钟内没有任何进度或构建配置输出，因此进程被中止；不得将其视为构建通过或上游能力已整合。

2026-09-04 的后续网络复核显示，官方分发地址可完成 HTTPS `HEAD` 请求并返回 `151433392` 字节的包大小。Wrapper JVM 仍无法写入下载内容；使用同一地址的 `curl` 可以传输数据，但吞吐约为 `50 KB/s`，预估完整下载超过 40 分钟。因此下载在 `4091904` 字节处被人工停止，现有隔离缓存仅用于后续续传，不能用于解压、构建或证明。该环境限制不改变 ImageToolbox 的 `Blocked by external distribution download` 状态。

同一受限环境下，固定 ytdlnis 源码目录运行下列命令会在 Wrapper 下载 Gradle 8.13 前失败：

```powershell
$env:GRADLE_USER_HOME = 'D:\xtoolpro\.gradle-upstream-media'
.\gradlew.bat --no-daemon --version
```

失败位置为 `org.gradle.wrapper.Download.downloadInternal`，异常为 `java.net.SocketException: Permission denied: connect`。这证明当前阻塞发生在 Wrapper 的受限 HTTPS 连接边界，而不是 ytdlnis 项目配置、插件或源代码编译阶段。该命令没有构建 APK、下载媒体、使用 Cookie 或操作设备。

## 取证目录与质量门禁隔离

- 上游归档和解压源码仅保存在 Git 忽略的 `.tools/upstream-proofs/`，不进入任何 XToolpro 生产模块或发布产物。
- 根 `spotless` 配置仅目标化 XToolpro 声明模块的 `src/**/*.kt`、项目根/模块根 Gradle Kotlin 脚本，并排除 `.tools/**` 与本地 Gradle 缓存目录；这避免把未修改的第三方源码套用 XToolpro 的格式规则，同时仍会校验所有 XToolpro 模块 Kotlin 源码、构建脚本和受版本控制的 Markdown 文件。
- 该调整由 `Phase 02` 的“隔离 proof”要求驱动，不改变上游源码、许可证结论或台账状态。

## Engine 契约测试

`docs/architecture/engine-contract-test-plan.md` 已为 Proxy、Cleaner、Media 和 Image engine 固定 success、unavailable、cancel、crash、version mismatch 五类测试场景。完整能力条目见 [上游完整能力对照矩阵](../../architecture/upstream-capability-parity-matrix.md)。这些文件是 Phase 02 的测试设计证据，不是通过 mock 宣称已完成的上游集成；可执行测试须等每个上游真实 proof 获批后，在对应 `engine-*` 模块中实现。

其中 Media success proof 必须同时提供公开 URL 和测试账户授权 Cookie/session 的真实结果，并附 capability-parity matrix；授权会话、私有内容和付费/高级格式不得因合规审查被删除，只有 DRM、未授权访问和访问控制绕过属于拒绝场景。

## 后续门禁

### GitHub 检查点状态（2026-09-05，已复核）

- 本地检查点提交：`16becda`（`docs(phase02): back up upstream proof checkpoint`），内容仅包括 `AGENTS.md` 的 GitHub 检查点规则、本证据文件和 `scripts/phase02-central-mirror.init.gradle`；未包含 SDK、缓存、上游归档、设备截图、构建输出、凭据或其他混合工作树变更。
- 此前直接使用 Windows Schannel 的推送曾因 `Failed to connect to github.com:443` 失败；本次通过仓库已配置的 Karing 代理并以 Git 的 OpenSSL TLS 后端执行 `git -c http.sslBackend=openssl ls-remote origin refs/heads/main`，远端返回 `85a1a79c9f92fec10a25d7a667f200bbb11f9735`。
- 因此，`16becda` 检查点及其后记录失败原因的 `85a1a79` 均已确认存在于 GitHub `origin/main`；本节此前的“未完成 GitHub 远程备份”状态已解除。该复核不代表 Phase 02 或任何 engine 已批准。
- 2026-09-06 本次真机证据提交 `b6d6ed26474b45083b8abc5112253c4ed89e455e` 已通过同一 Karing/OpenSSL Git 通道推送，`git ls-remote origin refs/heads/main` 返回相同哈希；仅该证据文件进入提交，SDK、缓存、上游归档、设备原始输出和混合工作树变更均未进入提交。

### 2026-09-05 Phase 02 preflight

2026-09-05 09:13 UTC 通过 `scripts/phase02-preflight.ps1 -Domain all` 重新检查当前工作站，结果为 10 项通过、2 项阻断；完整 JSON 记录见 `artifacts/phase02/preflight-report.json`。已确认 JDK、Git、ADB、本地 Android SDK、固定的 `core/Clash.Meta` 子模块、Go `1.26.4`、Android NDK `r28c`、Gradle `9.7.1`（cleaner/image）和已校验的 Gradle `8.13`（media）可用。Go 归档的 SHA-256 为 `3CA8FB4630B07C419CBDD51F754E31363CFCFB83B3A5354D9E895C90BE2CC345`，Gradle 9.7.1 的 SHA-256 为 `ACD53F1EDAF02F1A8FF99879F8A34B302661A057D9B063AE9E35B552F804D20A`；NDK `source.properties` 复核为 `Pkg.ReleaseName = r28c`。剩余阻断仅为 Flutter/Dart `3.44.4` 尚在官方 BITS 下载与 SHA-1 校验流程中。脚本以非零退出码结束，未启动任何上游功能、下载媒体、使用 Cookie 或操作设备。

2026-09-05 09:21 UTC 重新运行同一预检后，结果为 **12 项通过、0 项阻断**。Flutter `3.44.4` 官方归档通过 Google Storage 的 `Content-Length=1899506906` 与 `x-goog-hash` MD5 `NRaqNggA2DJ3XQLrMGCW6A==` 校验；本地 SHA-256 为 `8F2D6224FC6872D2F7F180DE86CDE989FCEA3776EFE0EDF48A9AAC2CD9BE2B1B`。归档由受控工具账户解压，因此预检只在本进程临时设置 Git `safe.directory`，并设置 `FLUTTER_SUPPRESS_ANALYTICS=true`，避免 Flutter telemetry 写入用户目录；两者都不修改全局 Git 或用户级 Flutter 配置。Flutter 输出为 `3.44.4`，内置 Dart 输出为 `3.12.2`。这只解除构建环境阻塞，不改变四域仍为 `Investigating` 的台账状态。

### FlClash arm64 core 首次启动证据（2026-09-05）

在固定 FlClash 提交与已 checkout 的 Clash.Meta gitlink 上，通过上游 Windows 入口 `plugins/setup/buildkit/run_build_tool.cmd android --arch arm64 --force` 启动 arm64 proof。`run_build_tool.cmd` 已完成其 build-tool 的 `pub get` 和 kernel 编译，随后真实调用 `go list -deps -tags=with_gvisor` 收集 Clash.Meta 依赖图；这是上游 `go_builder.dart` 在实际 `go build -buildmode=c-shared` 之前的固有步骤，而不是 XToolpro 自定义替代链路。

本轮没有生成 `libclash.so`：2026-09-05 09:28 UTC，`go.exe` 对 `142.250.73.81:443`（Go module 代理路径）维持多条 `SYN_SENT`，项目隔离 `GOMODCACHE` 未得到有效模块文件。该现象将 proof 阻塞在上游远程依赖连接前，不能解读为 native core 构建失败或代理能力已验证。为避免无进展网络等待占用资源，已停止本次 proof 的根进程；固定源码、已验证工具链和隔离缓存均保留。网络连通恢复后从相同命令继续，且只有产生 `libclash.so`、JNI header、ABI/哈希与 Android bridge 证据后才能改变 Proxy 状态。

### FlClash arm64 core 与 Android bridge proof（2026-09-05）

- 通过 Karing 代理在进程级设置 `HTTP_PROXY`、`HTTPS_PROXY`、`ALL_PROXY=http://127.0.0.1:3067`，并使用隔离 `GOMODCACHE`、`GOCACHE`、Flutter telemetry 目录和 Android SDK；未写入用户凭据、Cookie 或生产模块。固定源码仍为 FlClash `62addf738a76b1a492e19af2dbabdb6d572b9e72`，Clash.Meta gitlink 仍为 `0f7f05adff5e2c49775a112dcfe05a6aa36fda0c`。
- 上游入口 `plugins/setup/buildkit/run_build_tool.cmd android --arch arm64 --force` 已完成 build tool 的 `pub get`、kernel 编译和 `go list -deps -tags=with_gvisor`。在 Windows 上它随后因把 NDK r28c 的 Unix 风格无扩展 clang wrapper 交给 Dart `Process.runSync` 而失败；其编译器输出还触发了 Dart UTF-8 解码异常。没有修改 SDK 或上游归档。
- 在同一固定 `core/`、同一 `with_gvisor`、`-buildmode=c-shared` 和 `-ldflags=-w -s` 参数下，使用 NDK r28c 原生 `.cmd` clang wrapper 执行等价的上游 Go 编译成功。`libclash.so` 为 `59,369,072` 字节，SHA-256 为 `63AB20BA293921883701B72DA1F6D604042C1195FEEF87769A01EB66AB4134D0`；生成的 `libclash.h` 为 `2,485` 字节，SHA-256 为 `2ECF4B027C536023ECC000A551EDFF35AEB39DCDD02BA775BC3F041B35EFDDD6`。库文件头为 ELF64 little-endian、`EM_AARCH64 (0x00B7)`。
- 按上游 `go_builder.dart` 的 `_adjustAndroidOutput` 布局，将上述库和 `bride.h` 放入 proof 工作树的 `android/core/src/main/jniLibs/arm64-v8a` 与 `cpp/includes/arm64-v8a`；没有进入 XToolpro 生产模块。随后通过短路径 junction `D:\xtoolpro\fc`，使用隔离 Gradle `9.7.1`、Phase 02 proof-only Maven init script 和 `:core:assembleDebug --no-daemon --no-configuration-cache --no-parallel --max-workers=2` 编译上游 Android core bridge，结果为 `BUILD SUCCESSFUL`，`37 actionable tasks: 33 executed, 4 up-to-date`，耗时约 2 分 6 秒。CMake 明确输出 `Found libclash.so and headers for ABI arm64-v8a`。
- AAR 产物位于 Git 忽略的本地构建输出 `D:\xtoolpro\build\core\outputs\aar\core-debug.aar`，大小 `19,459,205` 字节，SHA-256 为 `0E004AA7B29750724BC15B75DD9BEE8829AF983650C5F25EE204FAC62056E5FE`。AAR 内 `jni/arm64-v8a/libclash.so` 为 `59,369,064` 字节、SHA-256 `859D6BA4E32FE719D417410811D31176E2E18297A26B3D67200A6049ED9EE29F`，`jni/arm64-v8a/libcore.so` 为 `265,728` 字节、SHA-256 `F6B4B2E89C62802478CFAABFAFD8D44165756974AE4F41EB6C3548A8A2F37935`；两者均核验为 `EM_AARCH64 (0x00B7)`。其余 ABI 只生成上游 bridge 的无 core 变体，不能被误报为已完成多 ABI core proof。
- 本 proof 证明固定 Clash.Meta core、JNI bridge 和 arm64 AAR 打包链路可在当前 Windows/Karing 环境完成；它仍未验证 VPN/TUN 设备流量、配置/节点/代理组、规则/DNS/IPv6、测速、日志、更新、备份或完整 capability-parity matrix。Proxy 台账继续保持 `Investigating`，不得进入 `Approved`。

### FlClash Android service/VPN bridge proof（2026-09-05）

- 在已验证的 arm64 core/JNI bridge proof 工作树中，使用同一隔离 Gradle `9.7.1`、Karing JVM 代理、Phase 02 proof-only Maven init script 和短路径 junction，执行上游原生命令 `:service:assembleDebug --no-daemon --no-configuration-cache --no-parallel --max-workers=2`。结果为 `BUILD SUCCESSFUL in 1m 4s`，`45 actionable tasks: 33 executed, 12 up-to-date`；Kotlin daemon 的用户目录权限警告触发了 Gradle fallback compiler，但没有导致构建失败。
- service AAR 位于 Git 忽略的本地构建输出 `D:\xtoolpro\build\service\outputs\aar\service-debug.aar`，大小 `88,426` 字节，SHA-256 为 `387D6DD64EDF1B325E2DD6CFFC0B2DDC8D5C21E48245736939EAA1F22B336E6F`。AAR 的 `classes.jar` 为 `93,765` 字节，包含 `com/follow/clash/service/VpnService.class`、`ProxyService.class`、`FilesProvider.class` 及其 service modules/models。
- AAR manifest 保留上游 VPN/service 边界：`VpnService` 使用 `android.permission.BIND_VPN_SERVICE`、`android.net.VpnService` intent-filter 和 `specialUse`/`vpn` 前台服务声明；`ProxyService` 保留 `specialUse`/`proxy` 声明；`FilesProvider` 保留 `MANAGE_DOCUMENTS` 权限和 `${applicationId}.files` authority。固定上游 `VpnService.kt` 的真实源码路径还调用 `Core.startTun`、`Core.stopTun`，证明 service 与已生成 JNI core 的调用链存在。
- 该 proof 证明固定 FlClash Android service/VPN bridge 的编译闭包和 manifest/class 产物存在；它没有启动 Android `VpnService`、请求用户 VPN 授权、建立真实 TUN、验证代理流量/通知/停止恢复或运行完整 capability-parity matrix。Proxy 台账继续保持 `Investigating`，不得将 AAR 构建误报为设备 VPN 通过。

### FlClash Flutter arm64 APK proof（2026-09-05）

- 使用固定 FlClash 提交、同一已 checkout 的 Clash.Meta gitlink、项目隔离 `GRADLE_USER_HOME`、本地 Android SDK/NDK、Flutter `3.44.4` 和 Karing `HTTP_PROXY`/`HTTPS_PROXY`，执行上游 Flutter 构建命令 `flutter build apk --debug --target-platform android-arm64 --no-pub`。构建实际进入 Gradle `assembleDebug`，并通过 SDK manager 安装了缺失的 Android SDK Platform 34；这一步没有修改上游源码或 XToolpro 生产模块。
- 构建运行 `18m 58s` 后失败，退出码为 `1`。失败任务为 `:setup:buildGoCore`：上游 `build_tool_runner` 在执行其固有的 `go version` 检查时收到 Windows `ProcessException: 系统找不到指定的文件`，随后 Gradle 报告 `Process 'command 'cmd'' finished with non-zero exit value 1`。本次 Flutter 子进程没有继承可执行的 Go 路径；该结果是构建环境入口问题，不是 APK、Flutter UI 或 Clash.Meta core proof 的成功证据。
- 未生成 `app-debug.apk`；在固定 FlClash proof 工作树递归检查没有发现 APK 产物。因此不能声称 Flutter packaging、arm64 APK 内的 `libclash.so`/`libcore.so`、Android manifest 合并或安装运行已通过。既有独立 arm64 core/JNI bridge/service AAR proof 仍有效，但 Proxy 台账继续保持 `Investigating`。
- 该失败日志来自本次真实命令的标准输出；下一次重试必须显式把已校验的 Go `1.26.4` 加入 Flutter/Gradle 子进程 `PATH`，并继续使用同一隔离缓存和 Karing 代理。只有产生并检查 arm64 APK，且后续在授权设备上完成 VPN/TUN 流量与恢复测试，才可推进对应门禁。
- 2026-09-05 随后复用已完成的 Go core 输出，使用 `android\gradlew.bat assembleDebug --no-daemon --no-configuration-cache --no-parallel --max-workers=2 -x :setup:buildGoCore -Ptarget-platform=android-arm64` 进行原生 packaging。该轮已显式设置 `FLUTTER_SUPPRESS_ANALYTICS=true`，成功完成 `:app:compileFlutterBuildDebug`、arm64 CMake 配置并确认 `Found libclash.so and headers for ABI arm64-v8a`；但在 `:app_links:checkDebugAarMetadata` 阶段因 Java 无法通过直连下载 AndroidX/Google Maven 构件失败，典型构件为 `androidx.profileinstaller:profileinstaller:1.3.1` 和 `androidx.core:core-ktx:1.13.1`，错误为 `Permission denied: getsockopt`，共报告 44 个同类失败。该命令退出码为 `1`，没有产生可验证 APK。
- 失败发生在依赖解析而非 Go core、JNI、CMake 或 Flutter Dart 资源编译；未将 `scripts/phase02-central-mirror.init.gradle` 的镜像规则偷偷写入上游源码。下一轮应显式加载该 proof-only init script，并核对镜像构件校验和后再重试；仅有部分 task 成功仍不能改变 Proxy 台账的 `Investigating` 状态。
- 加载 `scripts/phase02-central-mirror.init.gradle` 后，以相同固定源码、arm64 core 输出、隔离 Gradle 缓存、Karing 代理和 `FLUTTER_SUPPRESS_ANALYTICS=true` 重试，命令为 `android\gradlew.bat assembleDebug --no-daemon --no-configuration-cache --no-parallel --max-workers=2 --init-script D:\xtoolpro\scripts\phase02-central-mirror.init.gradle -x :setup:buildGoCore -Ptarget-platform=android-arm64`。结果为 `BUILD SUCCESSFUL in 3m 15s`，`651 actionable tasks: 252 executed, 399 up-to-date`，退出码为 `0`。
- 生成的真实上游 debug APK 为 `build\app\outputs\apk\debug\app-debug.apk`，大小 `124,122,508` 字节，SHA-256 为 `E388D9C06924F79D15E5A40CD6FBCFF8F062AF172C4162565BE526EF8923DC77`。APK 包名为 `com.follow.clash.dev`；其 `lib/arm64-v8a/libclash.so` 大小 `59,369,064` 字节、SHA-256 为 `859D6BA4E32FE719D417410811D31176E2E18297A26B3D67200A6049ED9EE29F`，`lib/arm64-v8a/libcore.so` 大小 `265,728` 字节、SHA-256 为 `D3520A46D3A8DA72306A1B18F4415B1AAA588FE0C3C6EB5F470D7901F17663FC`；`libclash.so` 与 `libcore.so` 均已由此前 AAR proof 核验为 `ELF64 AARCH64`，APK 中的 `libclash.so` 哈希与 AAR 内文件一致。
- 使用 Android build-tools `36.0.0` 的 `aapt` 检查 APK manifest：保留 `com.follow.clash.service.VpnService`、`android.permission.BIND_VPN_SERVICE`、`android.net.VpnService` intent-filter、`specialUse`/`vpn` 前台服务声明，以及 `com.follow.clash.service.ProxyService` 的 `specialUse`/`proxy` 声明；同时包含 `android.permission.INTERNET` 和 `android.permission.FOREGROUND_SERVICE`。这证明固定 FlClash 的 arm64 Flutter packaging 与 Android service manifest 闭包已形成真实 APK，但不证明设备安装、VPN 用户授权、TUN 建立、代理流量、停止恢复或完整 capability-parity matrix。
- 因当前 ADB 设备仍为 `bf353dda unauthorized`，本轮没有安装 APK、请求 VPN 授权或触发任何设备状态改变。Proxy 台账继续保持 `Investigating`；设备授权后下一道安全任务是对该 APK 做安装前校验、VPN/TUN start-stop、恢复和最小真实流量测试，并保存脱敏结果。

### FlClash arm64 APK 小米 10S 真机启动 proof（2026-09-06）

- 真机经用户明确授权后显示为 `bf353dda device`：`Xiaomi M2102J2SC`（device `thyme`）、Android API `33`、支持 ABI `arm64-v8a,armeabi-v7a,armeabi`。因此该设备与 APK 的 arm64 目标兼容；本节所有 ADB 操作使用项目隔离 Android SDK 的 platform-tools `37.0.1`。
- 对已校验 SHA-256 `E388D9C06924F79D15E5A40CD6FBCFF8F062AF172C4162565BE526EF8923DC77` 的上游 `app-debug.apk` 执行 `adb install -r`，结果为 `Success`。设备侧 `pm path com.follow.clash.dev` 返回安装路径，`dumpsys package` 复核版本 `0.8.96`、`versionCode=2026081701`、`minSdk=24`、`targetSdk=36`。没有通过 ADB 授予额外运行时权限。
- 使用 `adb shell monkey -p com.follow.clash.dev 1` 启动后，`com.follow.clash.MainActivity` 成为可见、已聚焦 Activity，系统报告 `Displayed` 和 `Fully drawn`。但 UI automation 随后读取到上游错误页 `Init Failed`：`MissingPluginException(No implementation found for method getDeviceInfo on channel dev.fluttercommunity.plus/device_info)`。对应 stack trace 在 `System.init` 调用 `device_info_plus` 时终止；logcat 同时记录 `ClassNotFoundException: Didn't find class "io.flutter.plugins.GeneratedPluginRegistrant"`。
- APK 解包和固定 proof 工作树均未找到 Android `GeneratedPluginRegistrant` 源；仅存在 Dart registrant 和桌面平台 registrant。由此可确认本次由 Flutter `3.44.4` / 固定 FlClash 组合生成的 Android debug APK 未打入该原生插件注册器。该真机结果证明安装、arm64 装载和 Activity 绘制可行，但**不**证明上游 Flutter 初始化、配置导入、VPN 授权、TUN、代理流量或停止恢复可用。
- 不为使 proof 通过而在固定上游归档中手改 registrant，亦未自动点击 VPN 授权或启动任何代理。Proxy 保持 `Investigating`；下一步需要以可审计的上游兼容性修复/版本选择记录解决 Android 插件注册闭包，再从相同真机重跑启动和 VPN/TUN 门禁。

### Cleaner 与 ImageToolbox Gradle 依赖解析取证（2026-09-05）

- 使用已校验的本地 Gradle `9.7.1` 启动固定 sdmaid-se 源码的 `:app-tool-corpsefinder:assembleDebug --stacktrace`。日志进入 `:buildSrc:compileKotlin`，但没有产生 APK 或 scanner 产物。17:14 本地时间取得的 daemon 线程转储显示 buildSrc 的 classpath/artifact resolution 正在 HTTPS TLS socket 读取和 `DownloadAction` 中等待；对应转储保存在 `artifacts/phase02/sdmaid-se-gradle-daemon-thread-dump.txt`。
- 同一 Gradle 版本启动固定 ImageToolbox 源码的 `:lib:image:assembleDebug --stacktrace`。日志完成 task graph 计算，但没有产生 AAR 或图像处理器产物。17:14 本地时间的 daemon 线程转储显示多个 Gradle `DownloadAction` 正在外部 artifact resolution 中等待；其中部分请求停在 `java.net.SocksSocketImpl.connect`。对应转储保存在 `artifacts/phase02/imagetoolbox-gradle-daemon-thread-dump.txt`。
- 两个 proof 均使用隔离 `GRADLE_USER_HOME`：`D:\\xtoolpro\\.gradle-upstream-cleaner-runtime` 与 `D:\\xtoolpro\\.gradle-upstream-image-runtime`。控制会话未检测到 `HTTP_PROXY`、`HTTPS_PROXY`、`ALL_PROXY` 或 SOCKS 环境变量，上游源码的根 `gradle.properties` 也未检出 proxy/SOCKS 配置；该记录不能据此判定系统层代理或远端仓库故障的唯一原因。
- 为避免将尚未完成的长时网络传输误作构建 proof，已在保留源码、隔离缓存、标准输出和线程转储的前提下结束这两个 Gradle 进程。该动作没有删除缓存、修改上游源码或改变台账的 `Investigating` 状态。网络通路稳定且有足够的连续运行时间后，应从相同固定提交、同一 Gradle 版本和隔离缓存重试；只有成功解析依赖、构建目标产物并执行完整能力矩阵后，才可能进入审批。
- 随后在受控网络环境使用 `curl --head https://repo1.maven.org/maven2/` 得到 HTTP `200`，说明 Maven Central 根入口可达。带 `--info` 的第二轮 Cleaner proof 明确显示 Gradle 等待 `https://repo.maven.apache.org/maven2/` 的 `org.jetbrains.kotlin:kotlin-build-tools-impl:2.4.0` 和 `org.jetbrains.kotlin:kotlin-compiler-embeddable:2.4.0`；17:50 本地时间线程转储 `artifacts/phase02/sdmaid-se-gradle-daemon-thread-dump-2.txt` 显示 `DownloadAction` 在 HTTPS TLS 读取中等待。两条精确 JAR URL 的 `curl --head` 均返回 HTTP `200`，并分别报告 `3,156,999` 与 `60,351,320` 字节。中止后保留的隔离临时目录含多个 `gradle_download*.bin`，最大为约 `17 MB`，证明 Gradle 已接收部分 JAR 数据但未在有限观察时间内完成缓存提交；模块缓存仍为 207 个文件、约 98.36 MB。该对比将阻断定性为 Gradle/Java 的低吞吐或长时读取/重试链路，而不是仓库对象缺失或源码/版本锁定问题；不能将其标记为构建通过。第二轮 process 已被停止，临时文件与缓存保留用于后续续传/诊断。

### sdmaid-se CorpseFinder 构建边界 proof（2026-09-05）

- 为解决受限 Java/Gradle 访问官方 Maven Central 与 Google Maven 的 TLS 连接失败，新增了 `scripts/phase02-central-mirror.init.gradle`。该文件仅由本节的隔离 proof 命令通过 `--init-script` 显式加载，映射 Maven Central 和 Google Maven 到 Aliyun 对应镜像，并对 Google 仓库限制为 `androidx`、`com.android`、`com.google` 组；它不改变 XToolpro 或上游项目的生产仓库配置，也不映射 JitPack。
- 首个阻断的 Google 构件 `com.android.tools:desugar_jdk_libs:2.1.5` 已在使用前核验：Google 官方 Maven 的 `jar.sha1` 与镜像的 `jar.sha1` 均为 `2aa1e5e7eb1f9082b4d88990c5f7aea7e95efc0a`。该核验不替代最终的完整依赖锁定与 SBOM 审查。
- 通过项目隔离 SDK 的官方 `sdkmanager` 安装了上游要求的 `build-tools;36.0.0`。第一次开始 Android 编译后，Windows `aidl.exe` 在原始隔离目录的长路径上以 `Failed to GetFullPathName` 和 `0xC0000005` 失败；没有修改上游归档。随后在 `D:\\xtoolpro\\.p` 创建指向同一解压来源的短路径 junction，并在 `D:\\xtoolpro\\.g` 创建指向同一隔离 Gradle 缓存的短路径 junction。复跑后两个先前失败的 AIDL 任务成功，证明此问题为本机路径长度边界。
- 可重复构建命令（进程环境变量均为本次 proof 临时设置）：

```powershell
$env:ANDROID_HOME = 'D:\\xtoolpro\\.tools\\android-sdk'
$env:ANDROID_SDK_ROOT = 'D:\\xtoolpro\\.tools\\android-sdk'
$env:GRADLE_USER_HOME = 'D:\\xtoolpro\\.g'
Set-Location -LiteralPath 'D:\\xtoolpro\\.p'
& 'D:\\xtoolpro\\.tools\\gradle-9.7.1\\gradle-9.7.1\\bin\\gradle.bat' --no-daemon `
  --init-script 'D:\\xtoolpro\\scripts\\phase02-central-mirror.init.gradle' `
  ':app-tool-corpsefinder:assembleDebug' --stacktrace
```

- 结果：`BUILD SUCCESSFUL in 3m 26s`，共 `175 actionable tasks: 172 executed, 3 up-to-date`。产物为 `app-tool-corpsefinder/build/outputs/aar/app-tool-corpsefinder-debug.aar`，大小 `993,591` 字节，SHA-256 为 `7F41314E67062C7FCF13C8D74C442FFB4B23CCF266BA7042A6982C205450CF2B`。
- 结论：本证据只证明固定来源的 CorpseFinder 与其共用模块闭包能在本机完成 Android AAR 构建。它没有验证扫描、预览、清理、恢复、SAF、Root/Shizuku，亦未覆盖其余 `app-tool-*` 能力；Cleaner 台账继续保持 `Investigating`。

### ImageToolbox `lib:image` 构建观察（2026-09-05）

- 使用同一套 proof-only Maven 初始化脚本、项目隔离 Android SDK 和 Gradle `9.7.1`，从 `D:\\xtoolpro\\.i` 短路径 junction（指向固定 `cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0` 解压来源）运行 `:lib:image:assembleDebug --stacktrace`。`D:\\xtoolpro\\.gi` 是指向既有 ImageToolbox 隔离 Gradle 缓存的短路径 junction。两个 junction 均未复制、删除或改写上游归档。
- 第一次观察在 `:build-logic:convention:compileKotlin` 约五分钟无输出后中断。后续延长窗口的第二次运行证明该任务可以完成并生成 `build-logic` jar，因而第一次中断不能作为 build-logic 不兼容或死锁证据。
- 构建逻辑完成后，Gradle 自动向隔离 SDK 安装了上游要求的 Android Platform 37.0。随后主 daemon 出现空闲等待：只读线程转储显示 Gradle daemon 正等待 worker，Kotlin daemon 已空闲且没有其他子进程。该观察不能归因于 ImageToolbox 业务代码。
- 第三次运行在命令行加入 `--no-configuration-cache --no-parallel --max-workers=2`，未改写固定上游 `gradle.properties`。该路径通过 build-logic 并进入 `:lib:gesture:compileFossDebugKotlin`、`lib:image` 的资源和依赖任务；`lib:gesture` 首次 Kotlin 编译超过十分钟仍未完成，主 JVM CPU 增长已趋于极低，故结束会话并保留缓存、SDK、源码和输出。没有产生 `lib:image` AAR。
- 结论：Image 台账继续保持 `Investigating`。目前已排除 Maven 下载和 build-logic 无法编译两种结论，但需要在可长时间持续执行的环境中完成 `lib:gesture` 与 `lib:image` 的 Kotlin 编译、记录 AAR 哈希，之后才能进入完整能力对照验证。

### FlClash Android plugin registrant 与小米 10S 初始化 proof（2026-09-06）

- 根因复核确认：Flutter `3.44.4` 的 `FlutterCommand.verifyThenRunCommand` 仅在 `shouldRunPub=true` 时调用 `regeneratePlatformSpecificTooling`；使用 `--no-pub` 会跳过 `injectPlugins(androidPlatform=true)`，因此不会生成 `android/app/src/main/java/io/flutter/plugins/GeneratedPluginRegistrant.java`。固定 FlClash 工程的 `.flutter-plugins-dependencies` 已包含 `device_info_plus`，所以此前缺失属于构建流程入口问题，不是插件元数据缺失。
- 在不改动 XToolpro 生产模块和固定上游归档的前提下，使用已缓存依赖完成 Flutter 平台工具注入，生成的 registrant 明确包含 `dev.fluttercommunity.plus.device_info.DeviceInfoPlusPlugin`。随后以隔离 SDK/NDK、Gradle `9.7.1`、proof-only Maven init script、`-x :setup:buildGoCore` 和 `-Ptarget-platform=android-arm64` 执行 `assembleDebug`，结果为 `BUILD SUCCESSFUL in 6m 16s`，`652 actionable tasks: 642 executed, 10 up-to-date`。
- 新 APK 位于固定 FlClash proof 工作树的 `build/app/outputs/apk/debug/app-debug.apk`，大小 `124,124,104` 字节，SHA-256 为 `4F374C68570EB4837B94D7026D594237035E18A97B84B27AAEEEBA4FAD7355EC`。APK dex 已核验包含 `io.flutter.plugins.GeneratedPluginRegistrant`、`DeviceInfoPlusPlugin` 和 `dev.fluttercommunity.plus/device_info`；arm64 `libclash.so` 为 `59,369,064` 字节、SHA-256 `859D6BA4E32FE719D417410811D31176E2E18297A26B3D67200A6049ED9EE29F`，`libcore.so` 为 `265,728` 字节、SHA-256 `D3520A46D3A8DA72306A1B18F4415B1AAA588FE0C3C6EB5F470D7901F17663FC`。
- 小米 10S（`M2102J2SC`、Android API `33`、`arm64-v8a`）上，旧测试包因签名不同无法覆盖安装；仅卸载包名 `com.follow.clash.dev` 的旧 proof 包后重新安装，ADB 返回 `Success`，`pm path` 和版本 `0.8.96`/`versionCode=2026081701` 均复核通过。启动后的前台 Activity 为 `com.follow.clash.MainActivity`，UI dump 显示真实“仪表盘”页面及配置/工具标签，不再出现 `Init Failed` 或 `MissingPluginException`；相关 logcat 未再出现 `GeneratedPluginRegistrant`/`device_info` 初始化错误。UI dump 同时产生了 MIUI 缺失 `/data/system/theme_config/theme_compatibility.xml` 的系统警告，但 dump 成功且不属于应用崩溃。
- 本检查点只证明 Flutter 原生插件注册、APK 安装和应用初始化已通过；尚未请求 VPN 用户授权、启动/停止 TUN、验证代理流量或恢复流程。Proxy 台账继续保持 `Investigating`，不可将初始化 proof 宣称为 VPN/代理 capability parity 通过。

### FlClash 小米 10S 配置后 VPN/TUN 闭环 proof（2026-09-06）

- 用户已在设备上的配置页添加真实配置；本记录不保存配置内容、节点信息、订阅 URL、凭据或 Cookie。设备仍为 `bf353dda`、Xiaomi `M2102J2SC`、Android API `33`、`arm64-v8a`，前台 Activity 为 `com.follow.clash.dev/com.follow.clash.MainActivity`。
- 启动状态由 ADB 独立复核：`com.follow.clash.dev/com.follow.clash.service.VpnService` 为 `isForeground=true`，系统 `dumpsys connectivity` 报告 `VPN CONNECTED`；接口为 `tun0`，地址 `172.19.0.1/30`，DNS `172.19.0.2`，VPN session 为 `FlClash`，owner UID 为 `10300`，本地 HTTP proxy 为 `127.0.0.1:7890`。本轮未出现新的 VPN 授权弹窗，记录仅证明当时系统已有授权状态并成功建立连接，不推断授权来源。
- 最小真实流量验证通过：设备侧 `curl --fail --silent --max-time 15 https://example.com` 返回 `HTTP 200`；请求前后 `tun0` 统计从接收 `81,501`/发送 `86,530` 字节增长到接收 `89,046`/发送 `94,059` 字节。界面同时显示运行计时 `00:00:45`、速度 `↑ 24B/s ↓ 1.4KB/s`、累计上传 `73.8KB`、下载 `86.6KB`。该结果证明当前配置下的最小可达流量和 TUN 计数变化，不证明所有代理协议或节点均可用。
- 停止验证通过：点击运行控件后等待 3 秒，`ip addr show tun0` 报告 `Device "tun0" does not exist`，`dumpsys activity services com.follow.clash.dev` 不再列出运行中的 `VpnService`，连接状态中也不再保留该 VPN 网络。
- 恢复验证通过：再次使用停止态实际启动控件，等待 6 秒后 `tun0` 重新为 `UP`，地址恢复为 `172.19.0.1/30`；`VpnService` 恢复 `isForeground=true`，系统重新报告 `VPN CONNECTED`，底层网络为原始 Wi-Fi `wlan0`。随后 HTTPS 请求返回 `HTTP 200` 且 `tun0` 字节计数继续增长。
- 该检查点扩大了 FlClash arm64 proof 的真实设备边界，仍未完成配置/节点/代理组全量矩阵、规则/DNS/IPv6、测速、日志、更新、备份、通知、异常崩溃和权限拒绝场景，也未完成许可证、SBOM 和完整 upstream capability parity 审查。Proxy 台账继续保持 `Investigating`，不得进入正式 engine 集成或改写为能力完整。

### FlClash 小米 10S capability-parity 真机复核（2026-09-06）

- 复核继续使用设备 `bf353dda`（Xiaomi `M2102J2SC`、Android API `33`、`arm64-v8a`）和已安装的固定来源 proof 包 `com.follow.clash.dev` `0.8.96`。通过 ADB 在设备上生成临时 UI hierarchy，并只向主机输出预先允许的能力标签、状态布尔值和记录计数；未保存或输出配置名、节点名、订阅 URL、凭据、Cookie、请求地址或日志内容。
- 模式与持久化：真机显示规则、全局、直连三种模式。实际切换为全局后强停并重启应用，全局仍被选中；随后恢复为规则，最终 UI 中规则对应单选项为 `checked=true`。直连模式只确认入口，没有切换。
- 代理组、节点和测速：代理页显示多个真实代理组与节点卡；单节点测速返回 `103 ms`。当前组批量测速显示 10 个结果节点，其中可见的 9 个不同结果为 `66`、`68`、`97`、`100`、`101`、`104`、`106`、`109`、`115 ms`。实际点击另一个节点后，设备私有数据库内容发生变化且应用可强停重启；由于无敏感 accessibility 语义不能独立确认当前节点名，本记录不把“当前节点恢复/持久化”升级为完整通过，也不保存数据库内容或哈希。
- 配置路径：当前真实配置为文件类型。配置菜单确认编辑、来源预览、更多、覆写、导出文件和删除入口；添加配置面板确认二维码、本地文件和 URL 三种入口。固定 Android UI 未出现独立剪贴板导入项。点击导出文件会启动 Android `CREATE_DOCUMENT` SAF 选择器；本轮主动返回取消，未创建导出文件，因此只证明导出路径可达，不证明导出提交成功。当前文件配置不具备可验证的订阅更新场景。
- 备份与工具路径：备份与恢复页确认备份、恢复和 WebDAV 入口，但未输入远端凭据、未创建备份、未执行恢复。工具页确认请求、连接、外部资源、语言、主题、备份与恢复、访问控制、基本配置、进阶配置和应用程序入口。访问控制页可进入真实应用列表；本轮未改变应用选择。
- 配置能力入口：基本配置页确认局域网代理、IPv6、追加系统 DNS、查找进程、日志等级、TCP 并发、统一延迟、测速链接和用户代理；进阶配置页确认附加规则、网络、DNS、按需运行和覆写脚本；网络页确认 VPN、系统代理、DNS 劫持、允许应用绕过 VPN、IPv6、栈模式、路由模式、路由地址和排除域名。本轮均保持原设置，不把可见入口误记为 DNS/Fake-IP/Host/嗅探、局域网共享或按应用路由行为通过。
- 应用设置页确认自动运行、选项卡动画、日志捕获、自动关闭连接、仅统计代理、崩溃分析和自动检查更新开关；Android 页面不显示桌面端自启动项。对已安装包的只读 manifest/package 状态检查未发现 `android.app.shortcuts`、`APPWIDGET_UPDATE`/`AppWidgetProvider` 或 `BOOT_COMPLETED` 声明，因此固定 Android proof 包的静态快捷方式、小组件和开机广播路径记为 `Unavailable`，不能用应用打开后的“自动运行”替代。
- 运行态复核：从停止态启动后，系统列出 `com.follow.clash.dev/com.follow.clash.service.VpnService`，`tun0` 为 `UP`，并存在 1 条该包的活动通知。Android 系统当前未把 FlClash 配置为始终开启 VPN，断线阻止值为未配置；本轮没有改变系统 VPN 设置。结合上一节设备侧 HTTPS `HTTP 200` 和 TUN 字节增长证据，本轮再打开公开 HTTPS 地址后，请求页与连接页各显示 10 个可见记录；只记录计数，不记录目标地址、应用或规则内容。
- 结束状态复核：停止后 `tun0` 不存在，FlClash `VpnService` 不再运行，活动通知记录为 0，规则模式保持选中。一次节点切换用于验证持久状态变更，但原节点无法在不暴露设备私有名称的前提下识别，因此不声明节点选择已恢复。除该已披露的节点选择外，本轮未改变配置、访问控制、DNS、系统代理、局域网、自动运行、更新、备份或系统 VPN 设置。
- 判定已同步到 [上游完整能力对照矩阵](../../architecture/upstream-capability-parity-matrix.md)。当时完整通过仅包括单节点/批量测速，以及实时/累计流量与连接列表两个合并行；其余已观察能力保持 `Partial` 或 `Pending`。仍缺用户可见节点筛选行为、订阅更新、导出提交、备份/恢复提交、竞争 VPN、首次授权拒绝/撤销、始终开启/断线阻止、DNS/Fake-IP/Host/嗅探行为、规则命中/日志、更新/回滚、异常崩溃和对应契约测试。Proxy 台账继续保持 `Investigating`，不得进入正式 engine 集成。

### FlClash 小米 10S 代理组与筛选入口补充复核（2026-09-06）

- 在同一设备和固定 proof 包上仅打开代理页并读取 ADB UI hierarchy；摘要包含 66 个节点、33 个可点击控件，且“自动选择”“故障转移”各有 2 个可访问语义节点。未选择节点、未切换组、未修改配置或 VPN 状态。
- 对当前代理页的可访问文本和 content-desc 做精确关键词统计，没有出现独立的“筛选”“过滤”“协议”“地区”控件；此前命中的“标签”来自底部导航的“第 N 个标签”语义或卡片描述，不构成筛选入口。
- 固定 Clash.Meta 的 `adapter/provider/provider.go` 仍可见 `filter` 与 `exclude-filter` provider 正则字段。这证明内核/provider 配置层存在过滤能力，但不证明 FlClash Android 当前 UI 暴露了协议、地区或标签筛选行为；矩阵对应行因此由 `Pending` 调整为 `Partial`，不升级为 `Verified`。

### FlClash 小米 10S Android VPN 系统设置入口复核（2026-09-06）

- 在 FlClash 停止态通过 ADB 只读启动 Android `Settings$VpnSettingsActivity`，证明系统 VPN 设置入口可达；未选择任何 VPN、未修改系统设置，也未读取或输出其他 VPN 的名称和配置。
- 通过 `settings get secure` 读取布尔状态：始终开启 VPN 未配置，VPN lockdown/断线阻止未启用。该状态与当前 FlClash 停止态一致；本轮没有尝试写入设置、触发断线或启动竞争 VPN。
- 系统设置页面的 MIUI UI hierarchy 未返回可解析控件文本，因此不把页面渲染内容推断为功能行为。矩阵中的“始终开启、断线阻止”由 `Pending` 调整为 `Partial`，仍缺 FlClash 设置写入、断线阻止效果、竞争 VPN 和首次授权拒绝/撤销验证。

### FlClash 小米 10S 直连与日志补充 proof（2026-09-06）

- 在同一固定来源 proof 包和真机上将模式切换为直连，强停并重启后 UI 单选序列仍为 `false,false,true`，证明直连模式持久化。直连运行态的 `tun0` 和 FlClash `VpnService` 均存在，活动通知记录为 1；设备侧公开 HTTPS 请求返回 `200`，请求前后 `tun0` 接收增加 `8,693` 字节、发送增加 `8,677` 字节。
- 直连流量后，请求页显示 10 条可见记录，其中 6 个 accessibility 节点包含允许记录的 `DIRECT` 路由语义。未读取或输出请求地址、应用、节点、规则或其他正文。结合此前全局模式强停重启 proof，矩阵中的“规则/全局/直连模式”合并行升级为 `Verified`；该结论不外推为所有代理协议、规则或 DNS 行为通过。
- 日志验证前，核心日志等级为 `error`，应用“日志捕获”为关闭。验证期间仅临时切换到 `info` 并启用日志捕获；工具页随即出现日志入口，日志页标题可见，共显示 6 条可见/部分可见 `info` 记录。ADB 处理 UI hierarchy 时仅输出页面标题存在性、级别标签和计数，未输出日志正文、URL、应用名、规则或目标。
- 用户观察到直连模式下底部没有“代理”选项。固定上游 `lib/providers/state.dart` 的 `currentGroupsState` 在 `Mode.direct` 下返回空组，`lib/common/navigation.dart` 又仅在 `hasProxies` 时显示代理页，因此该现象是上游模式语义而非配置丢失。切回规则模式并强停重启后，单选序列为 `true,false,false`，“代理”入口恢复，代理页再次显示“自动选择”和“故障转移”组入口。
- 验证结束后已恢复原设备设置：核心日志等级为 `error`，“日志捕获”关闭且工具页日志入口消失，模式为规则；`tun0` 不存在，FlClash `VpnService` 不再运行，活动通知记录为 0。未更改配置内容、节点选择、订阅 URL、凭据、Cookie、访问控制、DNS、系统代理或系统 VPN 设置。

### FlClash 小米 10S 规则模式重测边界（2026-09-06）

- 在已恢复的规则模式停止态再次启动固定 proof 包，VPN 图标和仪表盘运行态出现；设备侧公开 HTTPS 请求返回 `000`，未形成成功响应或可归因的新增请求记录。
- 请求页当时仍可见 6 个 `DIRECT` 路由语义节点，但无法证明这些记录来自本次失败请求；未读取或输出请求地址、应用、节点、规则或日志正文，因此不把该观察计入通过证据，矩阵状态保持不变。
- 随后从 FlClash 仪表盘执行停止，复核 `tun0` 地址行数为 0、系统 `VPN CONNECTED` 行数为 0；本次重测未改变模式、日志捕获、系统 VPN 或其他设备配置。

### FlClash 小米 10S 内核更新能力边界（2026-09-06）

- 在停止态打开固定 proof 包的工具、应用程序和关于页面，只统计允许的控件语义。关于页有 1 个版本样式节点，以及各 1 个可点击的“检查更新”和“内核”入口；“更新内核”“核心版本”和“回滚”控件计数均为 0。本轮未点击更新或外部链接，未发起下载。
- 固定源码 `lib/views/about.dart` 明确将该版本节点绑定到 `packageInfo.version`；`lib/common/request.dart` 的“检查更新”请求 FlClash 应用 Release，并以应用包版本比较。关于页的“内核”动作仅打开固定 Clash.Meta 源码链接，不返回或管理设备上的内核版本。
- 固定 Android `android/core/src/main/cpp/CMakeLists.txt` 从 ABI 对应的 `jniLibs` 链接 `libclash.so`；Flutter 运行时源码未找到 `updateCore`、`coreUpdate`、`rollbackCore` 或 `coreRollback` 路径。数据库写入失败回滚 helper 不属于内核二进制回滚。
- 因此矩阵中的“内核版本、更新和回滚”在固定 Android 包上由 `Pending` 调整为 `Unavailable`：内核更新必须作为受审计的 engine/APK 新版本完成校验、发布和回滚，不能把应用更新检查或外部源码链接误记为运行时内核更新。结束时 `tun0` 和系统 `VPN CONNECTED` 行数均为 0，设备设置未改变。

### FlClash 小米 10S 应用更新检查 proof（2026-09-06）

- 在关于页点击一次手动“检查更新”，等待元数据请求完成后，对话框显示“当前应用已经是最新版了”；“发现新版本”和“前往下载”语义计数均为 0，因此未进入下载、安装或外部跳转分支。
- 固定源码 `lib/common/request.dart` 将该动作限定为读取 FlClash Release 元数据并比较 `packageInfo.version`；本轮没有把应用更新结果外推为内核更新能力。
- 关闭结果对话框后，应用程序页的 9 个开关中“自动检查更新”对应末项仍为 `checked=true`，与检查前一致。结束时 `tun0` 和系统 `VPN CONNECTED` 行数均为 0；未修改更新设置、设备配置或本地文件。

### FlClash 小米 10S 本地备份导出 proof（2026-09-06）

- 在 VPN 停止态打开“备份与恢复”，远程区域显示未绑定 WebDAV；本轮只点击本地“备份”，没有输入远程地址、用户名或密码，也没有调用 WebDAV 路径。
- 固定源码 `lib/views/backup_and_restore.dart` 的本地流程先调用 `backupActionProvider.backup()` 生成应用私有 ZIP，再通过 SAF `saveFileWithPath` 保存。系统 `CREATE_DOCUMENT` 页面出现后保留不含配置内容的默认文件名并提交，FlClash 随后显示“备份成功”。
- ADB shell 和 MediaStore 未获得该 MIUI 文档提供者的直接文件路径，因此没有绕过权限读取文件大小或 ZIP 内容。随后只读打开系统 ZIP 选择器，FlClash 备份文件名语义计数为 2，证明该文件在文档提供者中可见；未选择或打开文件。
- 恢复会读取 ZIP 并写回配置/数据库，本轮为避免覆盖真实设备配置而未执行。测试备份保留在设备文档提供者中，未上传或复制到主机；结束时 `tun0` 和系统 `VPN CONNECTED` 行数均为 0。

### FlClash 小米 10S 配置文件导出 proof（2026-09-06）

- 在同一固定 proof 包和 VPN 停止态，从当前文件配置的菜单进入“更多 -> 导出文件”。固定源码 `lib/views/profiles/profiles.dart` 的 `_handleExportFile` 读取该配置私有文件字节并调用 `picker.saveFile(profile.realLabel, bytes)`；`lib/common/picker.dart` 在 Android 上将字节交给 `FilePicker.saveFile`，因此真实提交边界是系统 SAF `CREATE_DOCUMENT`。
- 系统保存页位于“下载内容”，保留应用给出的默认文件名并点击“保存”，随后返回 FlClash 配置页。为避免在主机输出配置名，ADB UI hierarchy 只在进程内比较配置卡片候选标签与系统文件选择器的可见文件名：配置卡片候选数为 1、精确匹配数为 1，`corresponding_export_visible=true`。
- 随后的文件选择器复核没有选择、打开、读取或复制导出文件，也没有输出配置名、节点、URL、凭据或文件内容。一次用于测试非敏感文件名输入的第二次保存页调用因 ADB 文本输入未生效而返回取消，不计入通过且未生成第二个测试文件。
- 导出文件保留在设备文档提供者中；本轮未重新导入、恢复、删除或修改当前配置。结束时应用已回到仪表盘，`tun0` 行数为 0、系统 `VPN CONNECTED` 行数为 0。矩阵对应合并行仍为 `Partial`，因为备份恢复尚未验证；独立配置剪贴板入口的结论见下一节，Proxy 台账继续保持 `Investigating`。

### FlClash 小米 10S 配置剪贴板导入边界（2026-09-06）

- 固定源码 `lib/views/profiles/add.dart` 的配置添加页只构造二维码、文件和 URL 三项；URL 路径使用 `InputDialog`，其 `lib/widgets/input.dart` 实现为普通 `TextFormField`，提交后才调用 `addProfileFormURL`。配置添加路径没有专用 `Clipboard.getData`、剪贴板识别或独立剪贴板菜单项。
- 源码中的 `Clipboard.getData('text/plain')` 位于 `lib/views/access.dart`，用于导入访问控制应用列表；配置卡片的 `Clipboard.setData` 仅为 URL 类型配置的“复制链接”。两者都不能作为配置剪贴板导入通过的证据。
- 真机添加配置页通过 ADB 确认恰有二维码、文件、URL 三个可点击入口。打开“从URL导入”后存在 1 个空 `EditText`；长按输入框后“粘贴”动作计数仍为 0，输入值保持为空。本轮没有执行粘贴或提交，没有读取、输出或改写设备剪贴板内容。
- 因此独立配置剪贴板导入在固定 Android 包中记为 `Unavailable`；普通 `TextFormField` 在剪贴板含文本时是否暴露系统标准粘贴仍未验证，不能从 Flutter 控件类型推断为真机通过。退出对话框后应用回到仪表盘，`tun0` 和系统 `VPN CONNECTED` 行数均为 0；矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S URL 配置生命周期入口边界（2026-09-06）

- 本轮当前配置菜单出现 1 个“同步”入口；固定源码 `lib/views/profiles/profiles.dart` 只在 `profile.type == ProfileType.url` 时构造该入口，因此当前真机状态已具备 URL 配置的菜单分支。该观察取代此前“当前仅有文件配置”的设备现状，但不改写早先证据发生时的历史状态。
- “更多”子菜单中“复制链接”为 1，“复制”和“重命名”独立菜单项均为 0。编辑页显示 3 个非空 `EditText`、1 个“自动更新”项和 1 个“保存”动作；ADB 只记录控件计数，没有读取或输出配置名、订阅 URL、更新间隔或其他字段值，随后通过返回键退出且未保存。
- 固定源码 `lib/views/profiles/edit.dart` 将这三个字段对应为名称、URL 和启用时的自动更新间隔，只有 `_handleConfirm` 才会写回或因 URL 变化触发更新。`lib/views/profiles/profiles.dart` 的“复制链接”会把真实订阅 URL 写入剪贴板；为避免泄露或改写剪贴板，本轮未点击。“同步”会发起真实订阅请求并更新配置，本轮也未点击。
- 固定配置菜单和 action provider 未发现复制或克隆配置动作；`Profile.checkAndUpdateAndCopy` 只在本地配置文件缺失时调用订阅更新以补齐文件，不是用户可见的配置克隆。因此“复制链接”不能替代需求中的配置复制，固定 Android 包的配置克隆记为 `Unavailable`；订阅更新、重命名、删除和持久化行为仍待可回滚的测试配置验证。
- 退出后应用回到仪表盘，`tun0` 和系统 `VPN CONNECTED` 行数均为 0；没有修改当前配置、订阅 URL、自动更新、剪贴板、配置选择或 VPN 状态。矩阵对应行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 自动运行 proof（2026-09-06）

- 固定源码 `lib/providers/actions/setup.dart` 在初始化时以既有运行状态或 `appSettingProvider.autoRun` 计算 `shouldRun`，命中后调用 `setRunning(true, initialize: true)`；`lib/views/application_setting.dart` 将“自动运行”开关持久化为 `autoRun`。该能力语义是应用打开时启动连接，不是系统开机自启动。
- 验证前“自动运行”对应 Switch 为 `checked=false`，`tun0` 和系统 `VPN CONNECTED` 行数均为 0。临时开启开关并强停应用后，`tun0` 仍为 0；重新打开应用并等待 8 秒，`tun0` 地址行数为 2、系统 `VPN CONNECTED` 行数为 1，系统服务状态存在 FlClash `VpnService`，证明启动来自应用重新初始化而非强停前残留。
- 随后将“自动运行”恢复为 `checked=false` 并强停应用，`tun0` 行数恢复为 0；再次打开并等待 8 秒后，`tun0` 和系统 `VPN CONNECTED` 行数仍均为 0，应用回到仪表盘且底部仪表盘标签为 `selected=true`。设备上的临时 UI hierarchy 已删除。
- 本轮只改变并恢复“自动运行”开关，没有读取或输出配置名、节点名、订阅 URL、凭据、Cookie、请求地址或日志正文，也没有修改配置、节点选择、自动更新、系统 VPN 设置或其他应用设置。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 系统代理与局域网监听边界（2026-09-06）

- 固定源码 `android/service/src/main/java/com/follow/clash/service/VpnService.kt` 在 Android 10 及以上且 `VpnOptions.systemProxy` 为真时，通过 `VpnService.Builder.setHttpProxy()` 写入以 `LOCAL_HOST`、mixed port 和绕过域名构造的 `ProxyInfo`。真机网络页中“系统代理”对应 Switch 为 `checked=true`；本轮只读该状态，没有点击或更改。
- 从停止态启动后，`tun0` 地址行数为 2、系统 `VPN CONNECTED` 行数为 1；对应 VPN `LinkProperties` 恰有 1 条 HTTP proxy，地址为 loopback、端口为 `7890`。停止后 `tun0`、系统 `VPN CONNECTED` 和 HTTP proxy 行数均为 0，证明该代理随 FlClash VPN 生命周期建立和撤销。
- 真机基本配置页中“局域网代理”对应 Switch 为 `checked=false`。运行态 `netstat -ltn` 对 mixed port 返回 4 条 TCP 监听，全部仅绑定 loopback，wildcard 监听计数为 0；该结果验证当前关闭状态的监听边界，不证明开启后的局域网共享访问可用。本轮没有打开该开关，也没有从其他局域网设备发起连接。
- 验证结束后再次打开应用，`tun0` 和系统 `VPN CONNECTED` 行数仍均为 0，仪表盘标签为 `selected=true`，设备临时 UI hierarchy 已删除。没有读取或输出配置、节点、订阅 URL、凭据、Cookie、请求或日志内容，也没有修改任何配置开关。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S DNS 与 IPv6 关闭边界（2026-09-06）

- 固定源码 `lib/views/config/network.dart` 将 Android VPN 的 IPv6 与 DNS 劫持分别绑定到 `vpnSettingProvider.ipv6` 和 `vpnSettingProvider.dnsHijacking`。依据该页面固定的控件顺序和真机 Switch 状态，本轮开始时两者均为 `checked=false`；验证过程没有点击或更改这些开关。
- 从停止态启动后，`tun0` 有 1 条 IPv4 地址、0 条 scope global IPv6 地址，系统存在 1 条 `VPN CONNECTED`；对应 VPN `LinkProperties` 中 `172.19.0.2` DNS stub 匹配 1 条，`::/0 unreachable` 默认路由匹配 1 条。设备对公开测试主机名 `example.com` 的一次解析及 ICMP 连通检查退出码为 0；该结果证明当前 DNS stub 可支持主机名解析，不外推为所有 DNS 模式或代理规则可用。
- 停止后，`tun0` IPv4、全局 IPv6、系统 `VPN CONNECTED` 和该 DNS stub 的匹配数均恢复为 0，仪表盘标签为 `selected=true`，设备临时 UI hierarchy 已删除。本轮没有读取 DNS 查询正文、配置、节点、订阅 URL、凭据、Cookie、请求或日志内容。
- 当前结果只验证 IPv6 关闭时的接口/路由边界和现有 DNS stub 的最小解析路径；DNS 劫持、Fake-IP/Host、流量嗅探及其错误和恢复场景仍未验证。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 代理组选择强停持久化 proof（2026-09-06）

- 固定源码 `lib/views/proxies/card.dart` 以 `selectedProxyNameProvider(groupName)` 驱动节点卡片的选中状态，节点点击依次调用 `updateCurrentSelectedMap(groupName, nextProxyName)` 和 `changeProxyDebounce(...)`。`lib/providers/actions/profiles.dart` 将选择写入当前 Profile 的 `selectedMap` 并调用 profile 持久化；`lib/database/profiles.dart` 又把该字段映射到 Drift `profiles.selected_map` 文本列。`lib/widgets/card.dart` 的选中态只改变视觉颜色/边框，并显示没有独立 accessibility 标签的 check 图标，因此不从 UI hierarchy 猜测节点名称或选中卡片。
- 设备没有可用的 `sqlite3` CLI，FlClash 也没有暴露该表的 ContentProvider。本轮在 `run-as com.follow.clash.dev` 沙箱内临时运行只读 Android SQLite 探针：以结构化 XML/JSON API 解析 `currentProfileId`，再以 `SQLiteDatabase.OPEN_READONLY` 只查询该 Profile 的 `selected_map` 和 `current_group_name`。探针只向主机输出布尔值、条目数和按排序键/长度前缀规范化后的 SHA-256；没有输出或复制数据库、配置、profile ID、组名、节点名、订阅 URL、凭据、Cookie 或任何原始字段值。
- 强停前，当前 Profile 可解析且存在，`selected_map` 非空并含 1 条映射，当前组已记录且其选择非空。执行 `am force-stop --user 0 com.follow.clash.dev` 后确认进程消失、`tun0` 不存在、FlClash `VpnService` 计数为 0；重新通过 launcher 启动并等待初始化后重复只读查询，以上布尔值和条目数不变，规范化 SHA-256 与强停前完全一致。该结果证明当前代理组选择由固定包持久化并在进程重建后恢复，不外推为多个组、所有节点协议或实时 core 切换均通过。
- 验证结束时应用位于仪表盘，仪表盘节点 `selected=true` 计数为 1；`tun0` 地址行数、FlClash `VpnService` 和系统 legacy `VPN CONNECTED` 行数均为 0。主机临时探针目录、设备 app 私有临时 dex、`/data/local/tmp` 探针和 UI hierarchy 均已删除；没有修改数据库、配置、节点选择、SDK、缓存、上游归档或构建产物。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 允许应用绕过 VPN 开关 proof（2026-09-06）

- 固定源码 `lib/views/config/network.dart` 的 `AllowBypassItem` 将“允许应用绕过 VPN”绑定到 `vpnSettingProvider.allowBypass`；`lib/providers/state.dart` 将该值传入 `VpnOptions.allowBypass`；`android/service/src/main/java/com/follow/clash/service/VpnService.kt` 在建 VPN 时对真值调用 Android `VpnService.Builder.allowBypass()`。该路径与“访问控制”的允许/排除应用列表不同，本轮没有读取、枚举或修改任何应用清单。
- 真机初始 Switch 为 `checked=true`。在停止态临时切换为 `false` 后执行 `am force-stop --user 0 com.follow.clash.dev`，确认进程和 `tun0` 均消失；重新启动并等待初始化后 Switch 仍为 `checked=false`，证明关闭值在进程重建后保持。
- 在 `checked=false` 状态从仪表盘启动，`tun0` 地址行数变为 2 且 FlClash `VpnService` 存在；随后停止，`tun0`、`VpnService` 和系统 legacy `VPN CONNECTED` 均恢复为 0。Android API 33 的 `dumpsys connectivity` 与 `dumpsys vpn_management` 没有暴露 `allowBypass` 字段，本轮也没有让测试应用调用网络绑定 API，因此不能把源码调用或 VPN 成功启动外推为实际绕过流量已验证。
- 随后将 Switch 恢复为 `checked=true`，再次强停并启动后仍为 `checked=true`。结束时应用位于仪表盘且仪表盘节点 `selected=true` 计数为 1，`tun0`、FlClash `VpnService` 和系统 legacy `VPN CONNECTED` 均为 0，设备临时 UI hierarchy 已删除；未改变应用选择、配置内容、节点、订阅 URL、凭据、Cookie 或其他网络开关。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S DNS 劫持开关 proof（2026-09-06）

- 固定源码 `lib/views/config/network.dart` 将“DNS 劫持”绑定到 `vpnSettingProvider.dnsHijacking`，`lib/providers/state.dart` 把该值传入 `VpnOptions.dnsHijacking`。`android/service/src/main/java/com/follow/clash/service/VpnService.kt` 在值为真时向 `Core.startTun` 传入 `dns = "0.0.0.0"`，值为假时传入固定 DNS stub；`VpnService.Builder.addDnsServers()` 在两态都向 Android VPN 发布固定 stub，因此系统 `LinkProperties` 本身不能证明 core 的捕获范围。
- 真机初始 `IPv6=false`、`DNS 劫持=false`。在 VPN 停止态临时开启 DNS 劫持并执行 `am force-stop --user 0 com.follow.clash.dev`，确认进程消失且 `tun0` 为 0；重新启动并返回网络页后 DNS 劫持仍为 `checked=true`，证明开启值可跨进程重建保持。
- 开启态启动 VPN 后，`tun0` 有 1 条 IPv4、0 条 scope global IPv6 地址，系统 legacy `VPN CONNECTED` 为 1；对应 `LinkProperties` 中固定 DNS stub 与 `tun0` 均各匹配 1 项。公开主机名解析退出码为 0；一次公开 HTTPS 端点连接超时并返回退出码 28，另一次公开 HTTPS 返回 `200` 且退出码为 0，因此只证明开启态存在可用的真实主机名 HTTPS 路径，不把单一端点失败归因于 DNS 劫持。
- `curl --dns-servers` 指向保留地址或 loopback 的请求在开启与关闭两态均返回成功，无法排除系统代理或代理侧解析，故不作为任意目标 DNS 已被 TUN 捕获的通过证据。随后停止 VPN，将 DNS 劫持恢复为 `checked=false`，再次强停重启后仍为关闭。结束时应用位于仪表盘且 `selected=true` 计数为 1，`tun0`、FlClash `VpnService` 和系统 legacy `VPN CONNECTED` 均为 0，设备临时 UI hierarchy 不存在；没有读取 DNS 正文、配置、节点、订阅 URL、凭据、Cookie、请求或日志内容。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S IPv6 开关 proof（2026-09-06）

- 固定源码 `lib/views/config/network.dart` 将“IPv6”绑定到 `vpnSettingProvider.ipv6`；`android/service/src/main/java/com/follow/clash/service/VpnService.kt` 在该值为真时添加固定 IPv6 TUN 地址与 `::/0` 路由，并向 `Core.startTun` 传入包含 IPv4/IPv6 的地址和 DNS 参数。`VpnService.Builder.addDnsServers()` 同时发布固定 IPv4/IPv6 DNS stub。
- 真机初始 `IPv6=false`。在 VPN 停止态临时开启 IPv6 并执行强停重启，网络页仍显示 `checked=true`，证明开关值可跨进程重建保持。开启态启动 VPN 后，`tun0` 有 1 条 IPv4 地址和 1 条 scope global IPv6 地址；系统 legacy `VPN CONNECTED` 为 1；VPN `LinkProperties` 中 IPv4 DNS stub 与 IPv6 DNS stub 各匹配 1 项，`::/0` 路由匹配 2 项；公开 HTTPS 请求返回 `200`，退出码为 0。
- 停止 VPN 后将 IPv6 恢复为 `checked=false`，再次强停重启后仍为关闭。结束时应用回到仪表盘，`tun0`、FlClash `VpnService` 和系统 legacy `VPN CONNECTED` 均为 0，设备临时 UI hierarchy 已删除；没有读取或输出配置、节点、订阅 URL、凭据、Cookie、DNS 查询正文或日志正文。该 proof 仅证明固定包在目标 API 33 设备上的 IPv6 VPN/TUN 地址、路由、DNS stub 和 HTTPS 基本行为，不外推为所有 IPv6 代理规则、Fake-IP/Host 或流量嗅探行为通过。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 局域网代理开关 proof（2026-09-06）

- 固定源码的基本配置页将“局域网代理”持久化为代理监听范围设置；此前关闭态已记录 mixed port 的 4 条 TCP 监听均为 loopback 且 wildcard 计数为 0。
- 真机在 VPN 停止态临时开启“局域网代理”，执行 `am force-stop --user 0 com.follow.clash.dev` 后重新启动，基本配置页仍为 `checked=true`，证明开关值可跨进程重建保持。开启态启动 VPN 后，`tun0` IPv4 地址行数为 1，系统 legacy `VPN CONNECTED` 为 1，mixed port 监听计数为 8，其中 wildcard `:7890` 计数为 1；公开 HTTPS 请求返回 `200`，退出码为 0。
- 本轮没有从其他局域网设备发起连接，也没有读取或输出配置、节点、订阅 URL、凭据、Cookie、请求或日志正文，因此只证明目标设备上开关对监听边界和 VPN 生命周期的影响，不证明跨设备共享访问、鉴权或防火墙路径。随后停止 VPN，将开关恢复为 `checked=false`，再次强停重启后仍为关闭；结束时应用回到仪表盘，`tun0`、FlClash `VpnService` 和系统 legacy `VPN CONNECTED` 均为 0，设备临时 UI hierarchy 已删除。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 追加系统 DNS 开关 proof（2026-09-07）

- 固定源码 `lib/views/config/general.dart` 将基本配置中的“追加系统 DNS”绑定到 `networkSettingProvider.appendSystemDns`；`lib/providers/actions/setup.dart` 将该值传入 `MakeRealProfileState`，`lib/common/task.dart` 在开启时向 `rawConfig['dns']['nameserver']` 追加 `system://`。本轮只复核源码路径和设备侧开关状态，没有读取配置正文。
- 真机初始开关为 `checked=false`。在 VPN 停止态临时开启后执行 `am force-stop --user 0 com.follow.clash.dev` 并重新启动，基本配置页仍为 `checked=true`，证明开启值可跨进程重建保持。开启态启动 VPN 后，`tun0` 地址行数为 2，系统 legacy `VPN CONNECTED` 为 1，`172.19.0.2` IPv4 DNS stub 匹配为 1，IPv6 DNS stub 匹配为 0；公开 HTTPS 请求返回 `200`，退出码为 0。
- 随后停止 VPN，将开关恢复为 `checked=false`，再次强停重启后仍为关闭。结束时应用回到仪表盘，`tun0`、FlClash `VpnService` 和系统 legacy `VPN CONNECTED` 均为 0，设备临时 UI hierarchy 已删除。由于未读取生成配置，不能把源码追加 `system://` 外推为最终 nameserver 列表已实际包含该值；本 proof 只证明开关持久化、开启态 VPN/TUN 和公开 HTTPS 基本运行边界。没有读取或输出 DNS 查询正文、配置、节点、订阅 URL、凭据、Cookie、请求或日志内容。矩阵合并行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 查找进程模式 proof（2026-09-07）

- 固定源码 `lib/views/config/general.dart` 的 `FindProcessItem` 将开关值映射为 `FindProcessMode.always` 或 `FindProcessMode.off`；`lib/providers/state.dart` 将 `state.findProcessMode` 传入 `UpdateParams.findProcessMode`，`lib/models/clash_config.dart` 和 `lib/models/core.dart` 分别以 `find-process-mode` 保持配置/核心参数映射。本轮只复核源码路径和设备侧开关状态，没有读取配置正文或进程/请求内容。
- 真机初始开关为 `checked=false`。在 VPN 停止态临时开启后执行 `am force-stop --user 0 com.follow.clash.dev` 并重新启动，基本配置页中的子 `Switch` 仍为 `checked=true`，证明开启值可跨进程重建保持；本轮没有启动 VPN，复核时系统 `VPN CONNECTED` 计数为 0。
- 随后将开关恢复为 `checked=false`，再次强停重启后仍为关闭；结束时 `VPN CONNECTED=0`、`tun0` 不存在，设备临时 UI hierarchy 已删除。该 proof 只证明开关映射和持久化，不证明 Clash.Meta 实际完成进程识别、请求归因或性能开销变化。没有读取或输出配置、进程名、节点、订阅 URL、凭据、Cookie、请求或日志内容。矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash Android plugin 许可边界与依赖裁剪复核（2026-09-06）

- 对固定 FlClash 提交的 `plugins/proxy/LICENSE`、`plugins/rust_api/LICENSE` 和 `plugins/window_ext/LICENSE` 做了只读复核；三者 SHA-256 均为 `422E0DE8E3275FEBF5C41A5CCF891F68F16BC40E1B5DCA26E50913B307EF794E`，内容仍是 `TODO: Add your license here.`。没有把根 GPL-3.0 推断为这些插件的授权，也没有修改上游归档。
- 平台声明复核显示：`proxy` 仅 Windows，`window_ext` 仅 Windows/macOS，`rust_api` 仅 iOS/Linux/macOS/Windows；固定 Android proof APK（SHA-256 `4F374C68570EB4837B94D7026D594237035E18A97B84B27AAEEEBA4FAD7355EC`）的 ZIP 成员和 `classes.dex` ASCII 字符串扫描均未发现 `ProxyPlugin`、`RustLib` 或 `WindowExtPlugin` 及对应 plugin 成员。APK 中唯一的 `proxy` 路径是应用空状态图标，不属于 plugin 代码。
- 该证据将 Android `engine-proxy` 路径的三个未声明许可插件标记为已验证排除项；它不解除桌面/full-capability 路径的许可阻塞，也不替代 Flutter/Gradle resolved dependency tree、Clash.Meta 子模块、native/传递依赖的许可证审查。后续 Android 构建必须保留依赖裁剪检查。
- `plugins/rust_api/rust/Cargo.lock` 的固定文件 SHA-256 为 `B258BC0B66CBC29884BA75746090B7F5B82FFA5303BB06F68F5228B21A60B843`，记录 87 个 Cargo 包；该锁文件不含完整许可证字段，桌面候选传递依赖仍需独立许可证解析。
- 已同步更新 [许可审计](../../compliance/upstream-license-source-audit.md)、[直接依赖盘点](../../compliance/upstream-dependency-inventory.md)、[第三方声明底稿](../../compliance/THIRD_PARTY_NOTICES.md) 和 [上游复用台账](../../architecture/upstream-reuse-ledger.md)。Proxy 台账仍为 `Investigating`，未进入正式 engine 集成。

### Clash.Meta 固定源码许可与依赖清单复核（2026-09-06）

- 隔离 proof checkout 的 `git rev-parse HEAD` 为 `0f7f05adff5e2c49775a112dcfe05a6aa36fda0c`，工作树无未提交修改；该 checkout 路径仍位于 Git 忽略的 `.tools/upstream-proofs/`，没有进入 XToolpro 生产模块、SDK、缓存或构建产物。
- 根 `LICENSE` 的 SHA-256 为 `230184F60BAE2FEAF244F10A8BAC053C8FF33A183BCC365B4D8B876D2B7F4809`，文本为 GPL-3.0；`go.mod` 的 SHA-256 为 `BAC10AEE76B477784CA48BEF18C00379DE54B6A2D803F90E9E1352D9F0D73686`；`go.sum` 的 SHA-256 为 `7982069B99FC64C5A45A40055228BB3E188EC5EC4268FA22B1748018CCFEBC90`。
- `go.mod` 约含 138 条带版本 `require` 记录。当前只完成固定来源、根许可和 Go manifest/校验锁定复核；第三方 Go 传递依赖许可证、native 闭包和 notices 仍待逐项解析，不能据此把 Proxy 台账改为 `Approved`。

1. 对每个固定提交完成可重复的真实能力 proof，并保存命令、依赖树、native 库与二进制校验和。
2. 为每个 `engine-*` 定义 success、unavailable、cancel、crash、version mismatch 五类契约测试。
3. 完成 GPL 源码发布方案、完整 SBOM、NOTICE、上游 fork 与补丁同步审查后，才可将台账行从 `Investigating` 改为 `Approved`。
