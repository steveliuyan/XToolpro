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

### FlClash 小米 10S TCP 并发开关 proof（2026-09-07）

- 固定源码 `lib/views/config/general.dart` 的 `TcpConcurrentItem` 读取并更新 `patchClashConfigProvider` 的 `state.tcpConcurrent`；`lib/providers/state.dart` 将该值传入 `UpdateParams.tcpConcurrent`，`lib/models/clash_config.dart` 和 `lib/models/core.dart` 分别以 `tcp-concurrent` 保持配置/核心参数映射。本轮只复核源码路径和设备侧开关状态，没有读取配置正文或网络日志。
- 真机初始开关为 `checked=true`。在 VPN 停止态临时关闭后执行 `am force-stop --user 0 com.follow.clash.dev` 并重新启动，基本配置页中的子 `Switch` 仍为 `checked=false`，证明关闭值可跨进程重建保持；本轮没有启动 VPN，复核时系统 `VPN CONNECTED` 计数为 0。
- 随后将开关恢复为 `checked=true`，再次强停重启后仍为开启；最终复核 `tcp_concurrent=true`，随后停止应用，`VPN CONNECTED=0`、`tun0` 不存在，设备临时 UI hierarchy 已删除。该 proof 只证明开关映射和持久化，不证明 Clash.Meta 实际并发连接行为或性能变化。没有读取或输出配置、节点、订阅 URL、凭据、Cookie、请求或日志内容。矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 统一延迟开关 proof（2026-09-07）

- 固定源码 `lib/views/config/general.dart` 的 `UnifiedDelayItem` 读取并更新 `patchClashConfigProvider` 的 `state.unifiedDelay`；`lib/providers/state.dart` 将该值传入 `UpdateParams.unifiedDelay`，`lib/models/clash_config.dart` 和 `lib/models/core.dart` 分别以 `unified-delay` 保持配置/核心参数映射。本轮只复核源码路径和设备侧开关状态，没有读取配置正文或测速日志。
- 真机初始开关为 `checked=true`。在 VPN 停止态临时关闭后执行 `am force-stop --user 0 com.follow.clash.dev` 并重新启动，基本配置页中的子 `Switch` 仍为 `checked=false`，证明关闭值可跨进程重建保持；本轮没有启动 VPN，复核时系统 `VPN CONNECTED` 计数为 0。
- 随后将开关恢复为 `checked=true`，再次强停重启后仍为开启；最终复核 `unified_delay=true`，随后停止应用，`VPN CONNECTED=0`、`tun0` 不存在，设备临时 UI hierarchy 已删除。该 proof 只证明开关映射和持久化，不证明测速结果实际去除握手等额外延迟。没有读取或输出配置、节点、订阅 URL、凭据、Cookie、请求或日志内容。矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 测速链接校验与取消边界 proof（2026-09-07）

- 固定源码 `lib/views/config/general.dart` 的 `TestUrlItem` 从 `appSettingProvider` 读取 `testUrl`，使用默认公开测试链接作为重置值，并拒绝空值或非 URL；`lib/providers/actions/proxies.dart` 将该设置作为默认测速链接传入代理组计算，`lib/core/interface.dart` 以 `test-url` 传入 core 的延迟测试请求，`lib/common/compute.dart` 用它参与延迟状态和排序。本轮只复核源码路径和 UI 校验边界。
- 真机基本配置页显示当前公开测试链接为 `https://www.gstatic.com/generate_204`。打开“测速链接”编辑对话框后，ADB 临时输入的非法文本触发 UI 校验提示“测速链接必须为 URL”；本轮没有提交该非法值，也没有运行测速或读取延迟结果。
- 点击取消后，基本配置页仍显示原公开测试链接；随后停止应用，系统 `VPN CONNECTED=0`，设备临时 UI hierarchy 已删除。该 proof 只证明入口、非空/URL 校验和取消不落盘边界，不证明新测速 URL 的保存、跨进程持久化、可达性或实际节点测速行为。没有读取或输出订阅 URL、凭据、Cookie、节点名、请求或日志内容。矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 测速链接保存、重启与延迟测试 proof（2026-09-07）

- 用户在真机“基本配置”中保存公开 URL `https://cp.cloudflare.com/generate_204`。随后对 `com.follow.clash.dev` 执行 `am force-stop --user 0` 并重新启动；通过 ADB UI hierarchy 仅检查“测速链接”项和该公开 URL，二者均存在，证明本次保存值可跨进程重建保留。
- 在规则模式下启动 VPN 后，系统 `VPN CONNECTED=1`，`tun0` IPv4 地址行数为 1。代理页的“延迟测试”操作返回 6 个可见延迟值：`57`、`69`、`91`、`96`、`104`、`138 ms`。本轮不记录代理组、节点、配置、请求地址或日志正文；该结果证明已保存的测速链接可参与一次真实设备延迟测试流程，但没有捕获 core 请求，不能独立归因每个请求的最终 URL，也不外推为所有节点或代理组均通过。
- 验证结束后停止应用，`VPN CONNECTED=0`、`tun0` 不存在。通过“测速链接”对话框的“重置”动作恢复默认公开 URL `https://www.gstatic.com/generate_204`，并确认无 URL 校验错误；设备临时 UI hierarchy 和主机临时截图均已删除。矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 规则模式公开 HTTPS 流量 proof（2026-09-07）

- 用户在设备侧选择了可用代理路径；未读取、输出或保存其节点、代理组、订阅 URL、凭据或 Cookie。ADB 返回仪表盘“规则”模式为选中状态，启动后系统 `VPN CONNECTED=1`、`tun0` IPv4 地址行数为 1。
- 在该运行态使用设备 `curl` 访问公开 `https://cp.cloudflare.com/generate_204`，HTTP 状态为 `204`。随后进入请求页，页面可访问且未出现 `DIRECT` 路由语义。本轮只记录公开 URL、状态码和语义存在性，不读取或输出请求目标、应用、规则、节点或日志正文。
- 先前规则模式请求曾返回 `000`；本次成功证明该固定 proof 包在用户选择的可用路径下能够建立规则模式 VPN/TUN 并传输真实公开 HTTPS 流量，但不证明具体规则条目命中、所有规则/节点行为或日志诊断完整性。结束时停止应用，`VPN CONNECTED=0`、`tun0` 不存在、`VpnService` 无匹配项，设备临时 UI hierarchy 已删除。矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 规则模式日志捕获可见性 proof（2026-09-07）

- 用户在设备设置的“应用程序”项中启用了“日志捕获”；该用户设置未由本轮 ADB 操作改写。只通过 ADB UI hierarchy 确认设置主页存在“日志”入口，未读取日志内容、配置、节点、规则、订阅 URL、凭据或 Cookie。
- 在仪表盘保持“规则”模式并启动后，Android 系统确认 VPN 已连接且 VPN `LinkProperties` 归属 `tun0`；设备对公开 `https://connectivitycheck.android.com/generate_204` 的 HTTPS 请求返回 `204`。请求后，用户在设备日志页人工确认新增记录。ADB 的无障碍层只暴露“日志”页标题，未暴露每条记录的文本；因此没有导出、保存或解析任何日志正文。
- 验证后停止 VPN，系统复核 `VPN CONNECTED=0`、`tun0` 不存在，设备临时 UI hierarchy 已删除。该 proof 证明固定 Android 包在规则模式真实流量后具有用户可见的日志捕获事件；它不证明某个具体规则条目命中、日志字段完整性、所有代理路径或崩溃诊断。矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 日志入口跨进程重建 proof（2026-09-07）

- 上一项日志可见性 proof 完成后，设备保持 VPN 停止态。对 `com.follow.clash.dev` 执行 `am force-stop --user 0` 并从 launcher 重新启动；启动后系统复核 `VPN CONNECTED=0`、`tun0` 不存在，未发生自动联网。
- 通过 ADB UI hierarchy 返回工具/设置主页，只对固定 accessibility 标签“日志 / 日志捕获记录”进行设备端计数，结果为 1；未读取日志正文、设置内容、节点、规则、订阅 URL、凭据或 Cookie。该入口在进程重建后仍可见，符合用户已启用日志捕获后的可见 UI 状态。
- 日志捕获底层开关和日志条目文本均未暴露给本轮 ADB 无障碍树，因而本 proof 只证明入口跨进程保留，不证明该布尔值的存储字段、日志写入完整性或任何规则命中详情。结束时临时 UI hierarchy 已删除，矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 规则模式后台持续运行 proof（2026-09-07）

- VPN 停止态下，仪表盘仍有“规则”模式入口。启动后将 FlClash 退到 Android 后台；系统仅以布尔值确认 `VPN CONNECTED=1`，且 VPN `LinkProperties` 仍归属 `tun0`。本轮不读取或输出 VPN 配置、代理节点、规则、订阅 URL、凭据、Cookie、请求或日志正文。
- 在该后台状态由设备访问公开 `https://connectivitycheck.android.com/generate_204`，HTTP 状态为 `204`；请求完成后 `VPN CONNECTED` 仍为 1。该结果证明该固定包在目标设备后台期间保持已建立的规则模式 VPN/TUN 并允许真实公开 HTTPS 流量。
- 将应用带回前台后通过仪表盘停止 VPN，复核 `VPN CONNECTED=0`、`tun0` 不存在，并删除设备临时 UI hierarchy。该 proof 不覆盖竞争 VPN、首次授权拒绝/撤销、始终开启或断线阻止，矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S VPN 进程终止清理 proof（2026-09-07）

- VPN 停止态从仪表盘启动固定 FlClash proof 包。启动后系统以布尔值确认 `VPN CONNECTED=1`，且 VPN `LinkProperties` 归属 `tun0`；不读取或输出配置、代理节点、规则、订阅 URL、凭据、Cookie、请求或日志正文。
- 在该运行态执行 `am force-stop --user 0 com.follow.clash.dev`。随后系统复核 `VPN CONNECTED=0`、`tun0` 不存在，证明进程被终止时未保留系统 VPN/TUN。通过 launcher 重新启动应用后再次复核两项均为 0，未自动恢复陈旧 VPN 会话。
- 该 proof 仅覆盖应用进程被 Android 强停时的 VPN 清理与重新启动边界；不模拟 core 崩溃、竞争 VPN、首次授权拒绝/撤销、始终开启或断线阻止。结束时设备临时 UI hierarchy 已删除，矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S VPN 通知生命周期 proof（2026-09-07）

- VPN 停止态从仪表盘启动后，系统确认 `VPN CONNECTED=1`。不读取 `dumpsys notification` 的任何记录正文，只在设备端统计包含 FlClash 包名的匹配行数，运行态为 13。
- 通过仪表盘停止 VPN 后，系统复核 `VPN CONNECTED=0`；同一脱敏计数降为 5。该变化表明 VPN 运行期关联的系统通知服务记录在停止后被移除。
- 计数不等同于单一通知的用户可见数量，且本轮不验证通知正文、通知渠道、权限提示、点击动作或 Android 系统状态栏呈现。结束时设备临时 UI hierarchy 已删除，矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S loopback mixed proxy 转发 proof（2026-09-07）

- VPN 停止态强停并重新启动固定 proof 包，以回到仪表盘的已知停止状态；不改变任何基本配置、网络开关、节点、规则、订阅 URL、凭据或 Cookie。启动 VPN 后系统确认 `VPN CONNECTED=1`。
- 设备使用显式 HTTP proxy `127.0.0.1:7890` 访问公开 `https://connectivitycheck.android.com/generate_204`，并传入空 `NO_PROXY` 覆盖以避免直连回退；HTTP 状态为 `204`。本轮不读取或保存 CONNECT 元数据、响应头、请求正文、代理配置或日志内容，结果只证明该运行态 loopback mixed proxy 可转发一次真实公开 HTTPS 请求。
- 通过仪表盘停止 VPN 后，复核 `VPN CONNECTED=0`、`tun0` 不存在，设备临时 UI hierarchy 已删除。该 proof 不验证 SOCKS、鉴权、局域网客户端、跨设备访问、全部端口或全部协议，矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S loopback mixed SOCKS5 转发 proof（2026-09-07）

- VPN 停止态从仪表盘启动固定 proof 包，系统确认 `VPN CONNECTED=1`。不更改任何配置、代理节点、规则、订阅 URL、凭据或 Cookie。
- 设备经显式 SOCKS5 hostname proxy `127.0.0.1:7890` 访问公开 `https://connectivitycheck.android.com/generate_204`，HTTP 状态为 `204`。该命令指定 SOCKS5 协议，因此成功结果证明同一运行态 loopback mixed port 能接收并转发一次 SOCKS5 TCP HTTPS 请求；未读取或保存 SOCKS 握手、请求/响应正文、代理配置或日志内容。
- 通过仪表盘停止 VPN 后，复核 `VPN CONNECTED=0`、`tun0` 不存在，设备临时 UI hierarchy 已删除。该 proof 不验证 SOCKS UDP、鉴权、局域网客户端、跨设备访问、全部端口或全部协议，矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S loopback mixed proxy 功能性并发 proof（2026-09-07）

- 在 VPN 停止态强停并重新启动固定 proof 包，以回到仪表盘的已知停止状态；没有变更应用访问控制、基本配置、网络开关、节点、规则、订阅 URL、凭据或 Cookie。启动后系统确认 `VPN CONNECTED=1`。
- 设备并发发起 4 个显式 HTTP proxy `127.0.0.1:7890` 请求，均访问公开 `https://connectivitycheck.android.com/generate_204` 且禁用 `NO_PROXY` 回退。设备端仅将每个 HTTP 状态码写入临时文件后汇总，得到 `4/4` 成功 `204`，随即删除临时状态文件；未读取或输出请求/响应正文、连接元数据、代理配置或日志内容。
- 停止 VPN 后，复核 `VPN CONNECTED=0`、`tun0` 不存在，并删除设备临时 UI hierarchy。该 proof 只证明此受限并发的功能性转发，不是吞吐、延迟、资源泄漏、TCP 并发开关因果关系或压力测试证据；矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S loopback mixed proxy 停止态关闭 proof（2026-09-07）

- 确认 VPN 停止态 `VPN CONNECTED=0` 后，设备显式经 HTTP proxy `127.0.0.1:7890` 访问公开 `https://connectivitycheck.android.com/generate_204`，并传入空 `NO_PROXY` 覆盖以避免直连回退。
- 请求返回 HTTP 状态 `000`、curl 退出码 `7`，表示无法建立该显式 proxy 连接。未读取或输出任何请求/响应正文、端口枚举、代理配置、日志、节点、订阅 URL、凭据或 Cookie。
- 该结果证明这个已验证 mixed HTTP port 在 VPN 停止后不保留可用监听；它不验证全部端口、SOCKS、UDP、Android 系统代理对象或所有异常关闭路径。矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S 按应用访问控制列表不可用边界（2026-09-07）

- 用户在“访问控制”页准备选择 UC 浏览器作为一次可恢复的绕过 VPN 测试目标，但固定包没有获取到可选应用列表。ADB UI hierarchy 只确认页面说明“选中应用将会被排除在 VPN 之外”，没有暴露应用条目、错误文本或重试控件；本轮没有点击“取消全选”或修改任何应用选择。
- 只读 `dumpsys package com.follow.clash.dev` 确认固定包声明 `android.permission.QUERY_ALL_PACKAGES` 且系统授予状态为 `true`。该结果排除该单一运行时权限缺失作为列表不可见的解释，但不确定上游页面、MIUI、包查询、Flutter UI 或其他运行时因素中的具体根因。
- 固定源码的 `lib/views/access.dart` 仅在 `initState` 发起一次 `getPackages()`；`FutureBuilder` 在请求结束后遇到空 `packages` 直接渲染“无数据”，没有错误态或重试入口。其 Android `AppPlugin` 将请求转给 `PackageResolver`，后者在 Android 13 调用 `PackageManager.getInstalledPackages(PackageInfoFlags)`；源码与 manifest 均具备该路径，但不足以在不读取设备应用清单或原始诊断的前提下判定返回空集的具体原因。
- 验证期间 `VPN CONNECTED=0`，未读取应用清单、已选应用、配置、节点、规则、订阅 URL、凭据、Cookie、请求或日志内容；设备临时 UI hierarchy 已删除。因此不能声明按应用实际绕过已验证，矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 小米 10S QuickAction 启停入口边界（2026-09-07）

- 固定源码的导出 `QuickActionActivity` 将 `${applicationId}.action.START` 和 `${applicationId}.action.STOP` 分别分派给 `ServiceState.handleStartAction()` 与 `handleStopAction()`；这是一条 Android 平台启动入口，不是静态快捷方式或小组件。
- 在 VPN 停止态以固定包的 START action 启动，等待 14 秒后系统 `VPN CONNECTED=1`，但 `tun0` 仍不存在；因此本轮没有发起任何流量请求，也不把该 action 记作可用 VPN 启动。随后调用同一固定包的 STOP action，5 秒后 `VPN CONNECTED=0`、`tun0` 不存在，恢复路径成功。
- 固定源码在应用初始化后以 `ShortcutManagerCompat.setDynamicShortcuts` 注册 ID 为 `toggle` 的动态快捷方式。真机启动 FlClash 主界面后，系统只按该固定 ID 计数为 1，且 `VPN CONNECTED=0`、`tun0` 不存在；未读取快捷方式标签或系统中其他应用的快捷方式记录。
- 为排除冷启动时机，保持主界面已初始化且 VPN 停止后再次触发相同 START action；7 秒后仍为 `VPN CONNECTED=1`、`tun0` 不存在。随后 STOP action 在 5 秒后再次恢复 `VPN CONNECTED=0`、`tun0` 不存在。该重测未读取或修改配置、快捷方式、节点、规则或网络设置。
- 进一步从停止态强停固定包后，直接 target 启动同一 `QuickActionActivity` 的 START action；20 秒后仍为 `VPN CONNECTED=1`、`tun0` 不存在，未发起流量。固定源码只在 `MainActivity.configureFlutterEngine()` 附着 Flutter engine，而本次 intent 没有启动该 Activity，因此该结果覆盖无附着 engine 时的原生 fallback。随后通过同一 STOP action 在 5 秒后恢复 `VPN CONNECTED=0`、`tun0` 不存在。
- 本轮没有读取或输出配置、节点、规则、订阅 URL、凭据、Cookie、请求、日志或通知正文。该入口的真实 TUN/流量启动缺口保持 `Partial`，不改变 Proxy 台账 `Investigating` 状态。

### FlClash 小米 10S 系统快捷设置磁贴入口边界（2026-09-07）

- 固定源码 `AndroidManifest.xml` 声明导出的 `.TileService`，要求 `android.permission.BIND_QUICK_SETTINGS_TILE`；`TileService.onClick()` 调用 `QuickAction.TOGGLE.quickIntent`，再由 `QuickActionActivity` 分派至 `ServiceState.handleToggleAction()`。这是一条系统快捷设置磁贴入口，不等同于前述动态应用快捷方式。
- 目标设备 Android 13 的 `cmd statusbar` 明确支持 `add-tile`、`click-tile` 和 `remove-tile`。从 `VPN CONNECTED=0`、无 `tun0` 的停止态，仅临时添加并点击 `com.follow.clash.dev/com.follow.clash.TileService`；7 秒及 14 秒后均为 `VPN CONNECTED=1`、无 `tun0`，故未发起任何流量。
- 再次点击同一磁贴并移除该临时磁贴后，系统仍为 `VPN CONNECTED=1`、无 `tun0`。随后使用前述已验证的固定 STOP action，5 秒后恢复 `VPN CONNECTED=0`、无 `tun0`。该结果不证明磁贴的独立停止路径可用，也不改变前述 QuickAction 直接 STOP action 的恢复结论。
- 本轮未读取快捷设置现有布局、配置、节点、规则、订阅 URL、凭据、Cookie、请求、日志或通知正文；没有修改配置或网络设置，且临时磁贴已移除。启动与停止的真实 TUN 生命周期缺口保持 `Partial`，Proxy 台账继续为 `Investigating`。

### FlClash 磁贴 Flutter 生命周期依赖源码追踪（2026-09-07）

- 固定 Android `MainActivity` 创建 Flutter engine 后注册 `TilePlugin` 并调用 `ServiceState.attachFlutterEngine()`。该 engine 存在时，`ServiceState.handleStartAction()` / `handleStopAction()` 不直接调用 `loadPreferencesAndStart()` 或 `requestStop()`，而是通过 `TilePlugin` 的 `${packageName}/tile` MethodChannel 发送 `start` / `stop`。
- 固定 Dart `Tile` 将该 channel 事件广播给监听者；`TileManager` 作为 widget 的 `TileListener` 仅在其 `initState` 注册，在 `onStart` / `onStop` 中调用 `setupActionProvider.setRunning(true/false)`。没有附着 engine 时，Android 才走读取已保存 `setupParams` 与 `vpnOptions`、设置 core 并请求服务的后备路径。
- 该 UI 生命周期依赖与本轮磁贴和 QuickAction 路径中“系统出现 VPN 请求但未建立 `tun0`”的真机观察一致，但没有读取日志、状态对象或配置来确认触发时的实际 engine/监听者状态，故不将其记为已证明根因，也不改变 `Partial` 或 `Investigating` 状态。

### FlClash QuickAction 与磁贴共享 Flutter 生命周期源码追踪（2026-09-07）

- 固定 Android manifest 将导出的 `QuickActionActivity` 的 START、STOP、TOGGLE action 映射到 `ServiceState.handleStartAction`、`handleStopAction` 和 `handleToggleAction`；`TileService` 点击也创建同一 `QuickAction.TOGGLE` intent。因此这两个系统入口进入相同的启动/停止分派，而不是两套独立的 Android service 启动实现。
- `ServiceState` 在 `tilePlugin` 存在时调用 `TilePlugin.handleStart` 或 `handleStop` 并立即返回。该 plugin 仅向 Flutter MethodChannel 发送 `start`/`stop`；Dart `TileManager` 的监听者才调用 `setupActionProvider.setRunning(true/false)`。这一分支不会执行原生 `loadPreferencesAndStart`、`setupCore`、`requestStart` 或 `ServiceController.start`。只有 Flutter engine 未附着时，原生 fallback 才从 SharedPreferences 读取启动状态、完成 core setup 并请求 service start。
- 该数据流与此前 QuickAction START 和磁贴点击均达到系统 VPN 连接、但观测窗口内没有 `tun0` 的真机现象相容；但固定源码不能确定试验时 Flutter engine 是否附着、MethodChannel 事件是否投递或 Dart 侧状态机为何未完成，故不将其表述为根因。未再次触发入口、未读取 logcat、应用日志、配置、节点、订阅 URL、凭据、Cookie 或通知正文。
- XToolpro 后续若获准设计 adapter，系统快捷入口必须有可独立完成或可显式恢复的持久启动合同，不能把可用 VPN/TUN 生命周期隐式绑定于存活的 Flutter UI 监听者。在这一入口的冷启动、已附着 engine、事件丢失、取消和失败路径均完成脱敏契约测试前，Proxy 路径保持 `Investigating`，矩阵保持 `Partial`，不进入正式 engine 集成。

### FlClash QuickAction TUN 建立与运行状态源码边界（2026-09-07）

- 固定 `VpnService.start()` 先启动 service modules，再调用 `handleStart()`；后者通过 `VpnService.Builder.establish()` 取得 TUN file descriptor，随后才调用 JNI `Core.startTun(...)`。任一步抛出异常时会调用 `stop()` 清理。因此真正的 TUN 建立路径包含 Android builder 建立、descriptor 交接和 native core 启动三个环节。
- 但 `ServiceController.start()` 只要受绑定 service 的 `start()` 未返回失败，就记录非零 `runTimeMillis`；`ServiceState` 随之可进入 `STARTED`。该状态机没有独立检查 `tun0` 是否存在、native core 是否健康，或是否已有实际可转发流量。系统 `VPN CONNECTED=1` 同样不足以证明上述全部步骤持续成功。
- 这解释了为何本次证据把 QuickAction 的系统 VPN 连接与可用 VPN/TUN 严格区分：强停 cold-start 后观测到 `VPN CONNECTED=1`、`tun0=0`，只能证明请求达到系统 VPN 层，不能证明 `establish`、`Core.startTun` 或后续运行中的哪一环失败。未读取 logcat、应用日志、配置、节点、订阅 URL、凭据、Cookie、请求或通知正文，也没有再次启动 VPN。
- XToolpro 的未来 adapter 契约必须把 Android service 已绑定、TUN 已建立、core ready 和公开受控流量成功建模为不同状态，并为每个失败/取消路径提供脱敏错误代码；未完成这些独立状态的设备验证前，快捷入口保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash QuickAction 启动失败错误传播源码审计（2026-09-07）

- 原生 fallback 的 `setupCore()` 将 `SharedState.setupParams` JSON 交给 `Core.quickSetup`。回调返回非空 message 时，固定源码把完整 message 拼入 `GlobalState.log("Unable to set up core: $message")`，再把同一 message 传给 `showConfigError`；异常时也把完整 exception 写入该日志，并将 `error.message` 交给 Toast。`GlobalState.log` 直接调用 Android `Log.d("FlClash", text)`。
- 随后的 `requestStart()` 捕获启动异常时同样把完整异常插入 `GlobalState.log("Unable to process service start request: $error")`。只有缺少配置、无权限和通用启动失败等少数路径使用固定文案；core 返回值、异常消息和其潜在字段没有在这条路径上做脱敏或归类。
- 静态源码不能断言任何具体 core message 或 exception 一定包含订阅 URL、配置、路径、节点或网络细节，也不能证明本机曾产生或显示过此类内容。本轮未触发 QuickAction、未读取 Toast、logcat、应用日志、配置、节点、订阅 URL、凭据、Cookie、请求或通知正文。
- XToolpro 不得直接复用原始 core message/exception 作为 UI、Android 日志、诊断导出或遥测字段。未来 adapter 必须只在受访问控制的内部诊断中保留脱敏原因，并向用户与默认日志输出稳定错误类别/代码；在该失败路径的脱敏契约测试完成前，快捷入口保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash QuickAction 导出调用者边界源码审计（2026-09-07）

- 固定 Android manifest 将 `QuickActionActivity` 声明为 `exported="true"`，并为 START、STOP、TOGGLE 注册 `DEFAULT` intent filter；该 Activity 未声明 `android:permission`。相比之下，同一 manifest 的 `ServiceBroadcastReceiver` 明确使用应用私有 permission，说明该 QuickAction 组件没有沿用受权限保护的入口模式。
- `QuickActionActivity.onCreate()` 不读取或验证 calling package、calling activity、签名、权限或一次性 token，而是仅按 `intent.action` 直接调度 `ServiceState.handleStartAction()`、`handleStopAction()` 或 `handleToggleAction()`，随后结束 Activity。按 Android 导出组件规则，知道组件或 action 的外部应用可请求这些状态变更；静态源码不能证明任一第三方应用曾实际发起请求，也不能保证每个请求都会形成可用 TUN。
- 本轮没有再发送 intent，也没有读取设备 Activity 记录、logcat、配置、节点、订阅 URL、凭据、Cookie、请求、通知或日志内容；仅以无内容计数确认设备仍为 `VPN CONNECTED=0`、`tun0=0`。
- XToolpro 不得直接复用无权限的导出 START/STOP Activity。系统快捷方式、通知 action 与快捷设置磁贴应优先使用不可导出的组件或仅由系统持有的 immutable `PendingIntent`；若业务确有跨应用入口，必须以签名级 permission、显式调用者校验和重放防护收窄合同。在该攻击面完成 Android 安全审查和契约测试前，快捷入口保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash VPN 系统回调广播权限边界源码审计（2026-09-07）

- 与无权限的 `QuickActionActivity` 不同，固定 `android/common` manifest 定义 `${applicationId}.permission.RECEIVE_BROADCASTS` 为 `signature` 保护级别；app manifest 为导出的 `ServiceBroadcastReceiver` 设置同一 permission。该 receiver 只处理 `VPN_START_REQUESTED` 和 `VPN_REVOKED` 两个 action。
- `ServiceBroadcastReceiver` 将前者分派到 `ServiceState.handleStartAction()`，后者分派到 `handleVpnRevokeAction()`；receiver 本身没有额外调用者校验，但 Android signature permission 已在组件分派前限制普通第三方应用。固定源码还显示 service 发送这两个内部广播时使用同一 permission。
- 该正向结果只证明固定源码的 permission 合同，不证明 Android 系统 always-on VPN、lockdown、授权撤销或任何外部调用在本机的实际行为；这些状态未按本轮边界进行测试。未读取系统设置、广播记录、logcat、配置、节点、订阅 URL、凭据、Cookie、请求或通知正文，设备仍为 `VPN CONNECTED=0`、`tun0=0`。
- XToolpro 若需要系统级 VPN 回调，必须保留或强化签名级权限，并把 action 集合最小化、显式化；不得因该受保护 receiver 的存在而放宽 QuickAction、通知或磁贴入口的调用者约束。完成 Android 组件暴露面清单和契约测试前，快捷入口保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android 专用配置深链接入口边界（2026-09-07）

- 固定 Android manifest 仅为 `MainActivity` 声明 `clash`、`clashmeta`、`flclash` scheme 与 `install-config` host 的 `ACTION_VIEW` / `BROWSABLE` filter；未声明标准 `ACTION_SEND` / `SENDTO`。`pubspec.yaml` 固定 `app_links` 依赖，`LinkManager` 监听该 host 的 URL query，并将 `url` 参数交给导入确认流程。
- 源码在确认后才调用 `Profile.normal(url).update()`。为避免触碰用户配置，本轮先打开已初始化的主界面，再以公开保留域名作为深链接参数发送 `ACTION_VIEW`；ADB UI hierarchy 只按该公开值计数，确认对话框中匹配 1 次。随后使用系统返回键取消，未确认、未联网、未写入配置。强停固定包后，以相同公开 deep link 冷启动，确认对话框仍匹配 1 次；再次返回取消后仍未写入配置。
- 使用 package-restricted（非 `-n` 强制 component）的 `ACTION_VIEW` 分别触发 `clash` 和 `clashmeta` scheme，ADB UI hierarchy 对两个不同公开保留域名均各匹配 1 次确认对话框；两次均用返回键取消。未读取或枚举设备上其他应用的 scheme 处理者，因此不声明系统全局 resolver 一定选择 FlClash。
- 对同一固定包发送 package-restricted `ACTION_SEND`、`text/plain` 与公开保留域名文本时，Android 在启动前拒绝该 intent；未打开 FlClash。该真机结果与 manifest 中缺少 `ACTION_SEND` filter 一致，因此固定 Android 包的标准系统分享文本入口为 `Unavailable`，不能以专用 deep link 替代。
- 该真机结果证明专用深链接到导入确认的分派可达；URL 位于 deep-link query，不能替代 Android 标准系统分享文本入口，也不证明任何真实订阅 URL 的下载、校验、保存或恢复。结束时 `VPN CONNECTED=0`、无 `tun0`，未读取或输出现有配置、节点、订阅 URL、凭据、Cookie、请求或日志，Proxy 台账保持 `Investigating`。

### FlClash 配置深链接日志与导出边界源码审计（2026-09-07）

- 对固定 FlClash 提交做只读源码追踪：`lib/common/link.dart` 的 `LinkManager` 在分派 `install-config` 前执行 `commonPrint.log('onAppLink: $uri')`。因此 URI 的完整字符串会进入该日志调用；当 `url` query 承载订阅地址时，该地址不会在这一层被脱敏。
- `lib/common/print.dart` 的 `commonPrint.log` 先调用 `debugPrint`，并在 `GlobalState.attach()` 完成后将相同 payload 作为 `Log.app` 写入 `logsProvider`。`lib/providers/actions/setup.dart` 在完整 setup 时将该列表重置为 `FixedList(500)`；`FixedList` 只保留内存中的最后 500 项。源码检索未发现 `logsProvider` 的自动持久化路径，不能据此声称普通深链接会自动写入应用文件或被上传。
- 但 `lib/views/logs.dart` 提供显式导出动作：`lib/providers/app.dart` 调用 `encodeLogsTask(value.list)`，其实现逐条拼接 `Log.toString()`，不做字段脱敏；随后写入临时文件并交给系统 `FilePicker.saveFile`。`lib/common/picker.dart` 在该调用后删除临时文件。该导出路径仍可将未脱敏日志内容写入用户选定位置，不能作为 XToolpro 默认诊断导出方案直接复用。
- 设置中的 `openLogs` 会控制 core controller 的 `startLog`/`stopLog` 和日志入口可见性，但不会阻止上述 `commonPrint` 在 app attach 后写入内存日志。因此“日志捕获”开关不是配置深链接 URI 的脱敏屏障。
- 本轮未向设备发送真实订阅 URL，也没有读取 logcat、应用日志、配置、节点、凭据、Cookie 或任何导出文件；仅复核固定本地源码。将来若进入 engine adapter 设计，必须先在入口、应用诊断、系统调试输出和导出路径统一删除或脱敏 URI query/订阅 URL，并以不含敏感值的契约测试证明。该合规缺口阻止把当前 Proxy 路径标为 `Approved`；矩阵保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash Firebase Crashlytics 初始化与遥测开关源码审计（2026-09-07）

- 固定 Android Gradle 配置启用了 `com.google.gms.google-services` 与 `com.google.firebase.crashlytics` plugin，并直接依赖 Firebase Analytics 和 Crashlytics NDK。`android/common/.../GlobalState.kt` 的 `setCrashlytics` 会初始化 `FirebaseApp`，随后写入 `FirebaseCrashlytics.isCrashlyticsCollectionEnabled`；服务每次同步 Flutter `SharedState` 和 setup 时均调用该方法。
- Dart `AppSettingProps.crashlytics` 的默认值是 `false`，设置页也将该值写入 `SharedState`，再通过 service MethodChannel 传给 Android。因此正常 Flutter 完成状态同步后的 collection 开关可由用户设置控制。固定源码没有发现 `commonPrint`、完整 URI、订阅 URL、配置字段或 Flutter 异常被直接作为 Crashlytics `log`、custom key 或 `recordException` 参数提交的调用。
- 但应用初始化会经 `AppPlugin.didCrashOnPreviousExecution` 调用 `FirebaseApp.initializeApp` 和 `FirebaseCrashlytics.didCrashOnPreviousExecution()`，此时尚未由当前 Flutter 设置显式写入 collection 开关；固定 Android manifest 中也未声明静态默认禁用的 Firebase collection metadata。静态源码不能确定 Firebase SDK 在该时点的有效 collection 状态、自动采集字段、是否实际传输或任何第三方服务端处理，故不得把依赖存在推断为已上传数据，也不得把 UI 开关视为完整隐私保证。
- 本轮未读取设备 Firebase 状态、Crashlytics/logcat 内容、网络请求、配置、节点、订阅 URL、凭据、Cookie 或导出文件，也没有改动设备设置。XToolpro 的拟议 adapter 必须默认禁用并避免初始化第三方遥测，只有在明确同意后才允许受审计的最小化崩溃报告；在许可、数据字段、网络目的地、撤回/删除行为和 URI 脱敏契约完成独立审查前，该路径不可进入 `Approved` 或正式 engine 集成。矩阵保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash VPN 前台通知配置标签边界源码审计（2026-09-07）

- 固定 Dart `sharedStateProvider` 将 `currentProfileProvider` 的 `label` 填入 `SharedState.currentProfileName`，Android `ServiceState.applySharedState()` 再以该值构造 `NotificationParams.title`。这条数据流不需要读取任何当前配置值即可由固定源码确定。
- `android/service/.../NotificationModule.kt` 的 builder 初始标题是中性字符串 `FlClash`，但每次运行态 `update` 都以 `setContentTitle(params.title)` 覆盖它；`params.title` 即上述配置显示标签。通知正文仅由 core 的速度/流量文本生成，停止动作文字来自 `stopText`。源码未显示将订阅 URL、节点/组名或规则直接写入该通知，但用户可控制的配置标签本身可能含敏感语义，不能作为中性系统通知标题使用。
- 本轮未读取 `dumpsys notification` 正文、状态栏内容、配置标签、节点、订阅 URL、凭据、Cookie、请求或日志，也没有启动 VPN 或修改设备设置。此前的真机检查仅以计数验证通知生命周期，不能替代本次字段来源审计。
- XToolpro 的未来 VPN shell/adapter 必须采用固定中性前台通知标题，禁止把配置标签、订阅 URL、节点/组名、规则或代理目标写入标题、正文、操作、channel 或 notification extras；在脱敏契约测试和 Android 通知隐私复核完成前，该路径不可进入 `Approved` 或正式 engine 集成。矩阵保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash Android 启动共享状态持久化边界源码审计（2026-09-07）

- 固定 `sharedStateProvider` 组装 Android service 所需的 `SharedState`：配置显示标签、`SetupParams.testUrl`、`SetupParams.selectedMap`、`VpnOptions.accessControlProps`（含 accept/reject 应用包名列表）以及绕过域名等。`AndroidManager` 在 state 变化后延迟调用 `preferences.saveShareState`，setup 路径也会直接调用同一保存方法。
- `lib/common/preferences.dart` 将该对象直接 `json.encode` 后写入 Flutter SharedPreferences 的 `sharedState` key；Android `Application.sharedState` 再从 `FlutterSharedPreferences` 的 `flutter.sharedState` string 用 Gson 解码。该固定路径未出现应用层加密、Android Keystore 或字段级脱敏；本结论不否定 Android 系统级设备加密，也不涉及或推断其他配置文件的存储方式。
- 审计到的 `SharedState` 模型不含订阅 URL 正文字段，不能据此判断订阅 URL 是否由其他配置/数据库路径持久化；但测试 URL、用户标签、节点/代理组选择和应用路由数据本身已足以构成不应直接复用的敏感启动状态集合。
- 本轮没有读取设备 SharedPreferences、应用数据、配置、节点、应用列表、订阅 URL、凭据、Cookie、日志或通知正文，也没有改动任何设置。XToolpro 必须令 native cold-start 合同只含启动所必需的最小字段，并以受 Keystore 保护的版本化存储和过期清理替代该明文 JSON 状态；订阅 URL、节点/组名、应用包名列表与测试 URL 默认不得进入该合同。完成存储威胁建模、迁移/清理和脱敏契约测试前，Proxy 路径不得进入 `Approved` 或正式 engine 集成。矩阵保持 `Partial`，台账保持 `Investigating`。

### FlClash URL 配置更新失败隐私边界源码审计（2026-09-07）

- 固定源码中，`Profile.normal(url: url).update()` 与既有 URL 配置的 `Profile.update()` 都调用 `request.getFileResponseForUrl(url)`。该方法在捕获异常时先执行 `commonPrint.log('getFileResponseForUrl error ${e.toString()}')`；对于 `DioException`，未知错误和坏响应被转为本地化错误，其他类型则重新抛出。日志调用保留原始异常字符串，未见字段脱敏。
- 自动更新路径在捕获 `updateProfile` 抛出的异常后再次调用 `commonPrint.log(e.toString())`。批量更新页面则以 `UpdatingMessage(label: profile.realLabel, message: e.toString())` 收集失败，并把该消息列表交给 UI 对话框。配置标签还可能由 HTTP `content-disposition` 更新，因此错误展示不应把标签视为天然无敏感性。
- 静态源码不能证明 Dio 在任一具体失败场景的 `toString()` 一定含 URL、请求头或响应内容，也不能证明这些值曾在设备上出现。本轮未触发 URL 更新、未发送真实订阅 URL，未读取设备配置、节点、应用日志、系统日志、请求、响应、Cookie 或导出内容。
- 将来若进入 adapter 设计，XToolpro 必须在网络和 UI 边界把更新失败映射为稳定且脱敏的错误类别或代码；原始异常文本、订阅 URL、配置标签和协议细节不得进入 UI、应用/系统日志、诊断导出或遥测。相关脱敏契约和失败路径测试完成前，Proxy 路径不得进入 `Approved` 或正式 engine 集成。矩阵保持 `Partial`，台账保持 `Investigating`。

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

### FlClash Android 文档提供者私有文件边界源码审计（2026-09-07）

- 固定 `android/service` manifest 将 `FilesProvider` 声明为导出的 `DocumentsProvider`，使用 `android.permission.MANAGE_DOCUMENTS`，并允许 URI grant。该组件服务于 `DOCUMENTS_PROVIDER` action；`VpnService`、`ProxyService` 均为 `exported=false`，其中前者还声明 `BIND_VPN_SERVICE`。本结论仅描述固定 manifest 合同，不把该系统权限推断为任意第三方应用可直接取得。
- `FilesProvider` 以 app `filesDir` 的 canonical path 为根。`queryChildDocuments` 遍历该根目录及其子目录，`includeFile` 使用绝对路径作为 document ID；`openDocument` 按请求的 `ParcelFileDescriptor` mode 打开解析后的文件，普通文件在 document flags 中标记 `FLAG_SUPPORTS_WRITE`。它未实现 create、delete、rename、copy 或 move 方法。
- `resolveFile` 对 document ID 做 canonicalization，并要求结果等于根或以根路径加目录分隔符开头，因而固定实现拒绝根目录外路径；但它不是专用导出目录，系统文件选择器或获用户授权的 URI 流程可面对 app 私有文件根。静态源码无法确定运行时是否存在敏感文件、系统实际授予了哪些 URI 权限，或任何外部应用曾读取/写入；本轮没有查询 provider、枚举文件、读取 URI、打开文件或改动设备配置。
- XToolpro 不得直接复用将完整 engine 私有文件目录暴露给 DocumentsProvider 的设计。未来配置/备份导出必须经用户显式触发、仅将最小化且已审查的输出提交给 SAF；不得把配置数据库、订阅 URL、凭据、缓存、日志或内部状态作为可浏览根暴露。完成 provider 授权、文件范围、写入原子性及撤销后的契约测试前，配置导出行保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android 平台组件暴露面完整性源码审计（2026-09-07）

- 固定 `android/settings.gradle.kts` 仅纳入 `app`、`core`、`service`、`common`，且 app 直接依赖后三者。对这些 manifest 的只读盘点显示：`MainActivity`、`QuickActionActivity`、`TileService`、`ServiceBroadcastReceiver` 与 `FilesProvider` 为导出组件；前述深链接、QuickAction、签名广播与 DocumentsProvider 边界分别已有专门记录。`VpnService` 和 `ProxyService` 均为 `exported=false`，前者另声明 `BIND_VPN_SERVICE`。
- `TileService` 以 `BIND_QUICK_SETTINGS_TILE` 和 `QS_TILE` action 供系统快捷设置绑定；其实现只在监听时订阅 `ServiceState`，点击时则构造 `QuickAction.TOGGLE.quickIntent` 并启动 `QuickActionActivity`。因此系统绑定权限保护磁贴 service 自身，但不保护该无权限导出 Activity 已存在的 START/STOP/TOGGLE 状态变更面。
- `MainActivity` 的公开入口为 launcher、`QS_TILE_PREFERENCES` 与此前已审计的专用配置深链接；固定 `wifi_ssid` plugin manifest 只增加 Wi-Fi/位置权限，`setup`、`rust_api` plugin manifest 为空，未见额外 Android 组件。此结论是固定源码和 Gradle 模块关系的静态结果，不等同于对任一 Android 系统调用者、合并 manifest 或设备行为的运行时证明。
- 本轮未发送 intent、绑定 service、查询 package 或 provider，未读取配置、节点、订阅 URL、凭据、Cookie、通知、日志或文件；无内容复核结果仍为 `VPN CONNECTED=0`、`tun0=0`。XToolpro 的 VPN shell 必须保持内部 service 不导出，系统 TileService 仅保留系统绑定权限，并使所有状态变更只经不可导出或签名保护的内部入口；在完整 merged-manifest 审计与调用者契约测试前，快捷入口保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 按应用访问控制清单空态与隐私边界源码审计（2026-09-07）

- 固定 `AppPlugin` 的 `getPackages` 在 IO coroutine 中直接将 `PackageResolver.installedPackages` Gson 序列化为 MethodChannel 字符串，不提供异常到稳定错误代码的映射。`PackageResolver` 使用 `PackageManager.getInstalledPackages(GET_PERMISSIONS)`，排除自身与 `android` 后为每项输出包名、显示标签、系统应用标记、`INTERNET` 声明标记与最后更新时间；manifest 声明 `QUERY_ALL_PACKAGES`，但源码无法证明设备实际返回任何数量。
- Flutter `SystemAction.getPackages` 仅在内存 `packagesProvider` 为空时请求 bridge；`AccessView.initState` 对同一页面实例只调用一次。Future 完成后，空列表直接显示“无数据”，该页面未提供错误类别、当前页刷新或重试控件。若方法抛出异常，源码也没有在 native branch 或该 view 内将其转为可区分的界面状态；静态追踪无法把这些路径中的任一项认定为本机空列表根因。
- 这解释了此前目标设备上“无条目、无错误/重试”的观察，不需要读取或输出任何实际应用清单。应用包名、标签、网络声明和更新时间共同构成敏感设备使用信息；固定源码把完整集合跨 Android/Flutter 边界传输，未见该 path 的字段最小化或专用隐私合同。
- 本轮未调用 `getPackages`、`getChinaPackageNames`、图标查询或任何 package-manager 命令，未选择应用或更改访问控制，且未读取配置、节点、订阅 URL、凭据、Cookie、日志、通知或文件；设备仍为 `VPN CONNECTED=0`、`tun0=0`。XToolpro 未来仅可在用户显式打开按应用路由时按需获取最小字段，结果仅保留在内存并禁止日志、导出、遥测和默认持久化；还必须提供 empty、permission/unavailable、engine-error 与 retry 状态。完成隐私、错误映射与真实绕过流量契约测试前，该能力保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash VPN 前台通知停止动作边界源码审计（2026-09-07）

- 固定 `NotificationModule.update()` 将 `QuickAction.STOP.quickIntent.toPendingIntent` 加为前台 VPN 通知的停止 action。`QuickAction.quickIntent` 显式指向 `Components.quickActionActivity`，其 action 为 `${applicationId}.action.STOP`；`Intent.toPendingIntent` 以 `PendingIntent.getActivity(..., FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT)` 创建系统管理 token。
- `FLAG_IMMUTABLE` 正向限制已获该 token 的通知接收方修改其 encapsulated intent；它不改变 token 所指向 Android 组件的 manifest 暴露面。固定 app manifest 将 `QuickActionActivity` 声明为 `exported=true`，未设置组件 permission，并为 START/STOP/TOGGLE 注册 action；其 `onCreate()` 未校验 caller、permission 或一次性 token，只按 action 调度 `ServiceState.handleStartAction()`、`handleStopAction()` 或 `handleToggleAction()`。因此通知 token 不可变不能阻止第三方绕过该 token、直接调用同一公开 STOP Activity。
- 本轮未读取 `dumpsys notification`、状态栏文本、notification extras、配置、节点、订阅 URL、凭据、Cookie、请求或日志，也没有点击通知或发送 intent。仅以无内容计数确认目标设备仍为 `VPN CONNECTED=0`、`tun0=0`。
- XToolpro 未来 VPN 通知必须使用中性、脱敏内容，并将停止动作绑定到不可导出或签名保护的内部组件；`PendingIntent` 仍应 immutable，但它是 token 完整性要求，不是公开组件调用者校验的替代品。完成 merged-manifest、notification action 和第三方直接调用的契约测试前，通知停止动作保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android 13 前台通知权限门控源码审计（2026-09-07）

- 固定 app manifest 声明 `android.permission.POST_NOTIFICATIONS`。`ServiceState.requestStart()` 在 Flutter `AppPlugin` 已附着时先调用 `requestNotificationPermission`，回调为 `false` 才让本次 request 失败；因此 Android bridge 的回调语义决定是否进入 VPN/core 启动路径。
- API 33+ 的 `AppPlugin.requestNotificationPermission()` 先检查 `POST_NOTIFICATIONS`，未获准时以 `ActivityCompat.requestPermissions` 请求。其 permission-result listener 只核对 request code，既不读取 `permissions` 也不读取 `grantResults`，而是无条件将 `skipNotificationPermissionRequest=true` 并以 `true` 调用启动回调；如果当时没有 attached Activity，原方法也直接以 `true` 回调。固定源码因此没有把通知权限拒绝、撤销、空 Activity 或结果异常映射为稳定的 unavailable/cancel 状态。
- 目标 Android API 33 设备仅做只读状态检查：`POST_NOTIFICATION` app-op 为 `allow`，notification dump 中该包名匹配计数为 `5`，结束时 `VPN CONNECTED=0`、`tun0=0`。本轮未修改授权、未启动 VPN、未读取 notification 文本、extras、配置、节点、订阅 URL、凭据、Cookie、请求或日志；当前允许状态不能覆盖拒绝或撤销路径。
- XToolpro 的未来 adapter 必须把 Android 13+ 的真实 permission result、Activity 可用性和前台通知可见性作为 VPN 启动的显式门控；拒绝、撤销或无法请求时保持未启动并暴露可恢复的 unavailable/cancel 状态。不得复用无条件 continue/skip 分支。完成 allow、deny、revoke、process recreation 与 foreground-notification visibility 的设备契约测试前，该能力保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android 13 通知权限撤销下原生启动契约（2026-09-08）

- 小米 10S（`bf353dda`，API 33）在停止基线仅确认 `tun0=0`、dev 进程数为 0、`POST_NOTIFICATION` 为允许后，使用正常 Android 权限操作 `pm revoke com.follow.clash.dev android.permission.POST_NOTIFICATIONS` 临时撤销该单一权限。撤销后 UID 层 app-op 显示 `POST_NOTIFICATION: ignore`；同次 package 层查询仍显示 `allow`，故本证据保留两个原始状态层级，不把它们合并为单一授权结论。
- 随后强停 dev 变体，并以已解析的 `com.follow.clash.dev/com.follow.clash.QuickActionActivity` 与 `com.follow.clash.dev.action.START` 发起冷启动。第 7 秒和第 14 秒仅确认 `tun0` 仍不存在、dev 进程存在；未读取系统 VPN、通知正文或 extras、日志、配置、节点、订阅 URL、凭据、Cookie、请求、数据库或文件，亦未发送任何流量。
- 为排除冷启动本身作为充分解释，在再次正常撤销同一权限后，先启动 dev `MainActivity` 并等待 8 秒，再发出同一精确 START action；第 7 秒和第 14 秒仍仅确认 `tun0` 不存在、dev 进程存在。该轮同样只观察 TUN、进程与 permission/app-op 层状态，不读取 Flutter/MethodChannel 内部状态或任何受限内容。
- 同一精确 STOP action 后 5 秒仍无 `tun0`、dev 进程存在。最后以正常 `pm grant` 恢复 `POST_NOTIFICATIONS`，package 层 app-op 显示 `allow`；设备没有遗留 TUN。该还原只恢复本轮开始时的允许状态，并不改变用户的其他设置。
- 结果仅证明这个冷启动原生入口在本设备已撤销态的观察窗口内没有建立 TUN；它不能证明上游实际走过 permission callback、前台通知可见、系统 VPN 是否曾短暂请求、用户能看到稳定 unavailable/cancel 状态，或完整 core 健康。结合 callback 忽略 `grantResults` 的固定源码，XToolpro 仍须将实际授权结果、Activity 可用性及前台通知可见性作为可等待门控，并以拒绝、撤销、进程重建和可见性契约测试验证稳定状态。在完成前该能力保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash VPN service 断开与系统恢复源码审计（2026-09-07）

- 固定 `VpnService.onRevoke()` 先执行 `stop()`，后发送受签名 permission 保护的 `VPN_REVOKED` 广播；`stop()`/`onDestroy()` 均会停止 service modules 与 `Core.stopTun()`。`onStartCommand()` 则发送 `VPN_START_REQUESTED` 广播，注释说明 Android always-on VPN 经此 callback 而非普通 bound-service 路径启动；它随后直接 `return super.onStartCommand(...)`，固定源码没有声明独立的 service restart mode。
- `ServiceBroadcastReceiver` 将系统启动广播交给 `ServiceState.handleStartAction()`。没有 Flutter engine 时，该方法走 native fallback：读取已有 shared state、调用 `quickSetup`，再请求启动服务。因此源码存在系统重启后尝试恢复的分派路径，但依赖先前持久化的状态与 core setup，不能替代实际 always-on、进程重建或 reboot proof。
- 对仍存活 app process 内的绑定 service，`ServiceController` 处理 `onServiceDisconnected`、`onBindingDied` 与 `onNullBinding`：清空 binding、将 `runTimeMillis=0`，再使 `ServiceState.handleServiceLost()` 把当前 request 收敛为 `STOPPED`。该路径没有 retry/backoff、持久化 failure reason 或用户可见的明确恢复状态；app process 自身死亡时这些内存对象也不构成恢复记录。
- 目标设备仅以无内容状态确认 `VPN CONNECTED=0`、`tun0=0`、FlClash 进程数 `0`。本轮未杀进程、未重启设备、未配置 always-on/lockdown、未撤销 VPN/通知权限，未读取配置、节点、订阅 URL、凭据、Cookie、通知、请求或日志。XToolpro 必须以独立持久状态机记录运行意图、失败类别和恢复条件，并在 service loss、process death、reboot、VPN revoke 与通知权限不可用场景各自完成设备契约测试；在此之前能力保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 前台服务类型与 VPN 组件边界源码审计（2026-09-07）

- 固定 `android/service` manifest 将 `VpnService` 与 `ProxyService` 都声明为 `android:exported="false"`，并使用 `android:foregroundServiceType="specialUse"`；前者另保留 `android.permission.BIND_VPN_SERVICE` 与 `android.net.VpnService` action。两个 service 分别声明 `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` 值 `vpn` 与 `proxy`。
- 固定 `android/common/.../Ext.kt` 的 `Service.startForeground` helper 在 API 34+ 调用三参数重载并传入 `FOREGROUND_SERVICE_TYPE_SPECIAL_USE`，低于 API 34 使用双参数兼容重载；这与 manifest 的 special-use 类型和两个 subtype 声明一致。该源码边界没有把 VPN service 公开给普通第三方应用。
- 目标设备仅做无内容状态复核：`VPN CONNECTED=0`、`tun0=0`、FlClash 进程数 `0`。本轮未启动 service、未读取通知正文或系统日志，不能把静态类型一致性外推为 Android 14+ 的实际前台启动、通知可见性或类型拒绝测试。XToolpro 可保留该“声明与调用一致”的正向证据，但仍需在目标 API 上验证启动失败、通知权限不可用和服务类型违规时的稳定错误映射；Proxy 台账保持 `Investigating`。

### FlClash Android bridge 日志脱敏边界源码审计（2026-09-07）

- 固定 `android/common/.../GlobalState.kt` 的 `GlobalState.log(text)` 直接调用 `Log.d("FlClash", text)`，未见统一的字段分类、截断或脱敏层。`android/service/.../VpnService.kt` 的路由循环把 `route.address` 与 `route.prefixLength` 直接写入 `Log.d`；该字段可能是实际路由地址，因此不能作为默认诊断日志合同复用。
- `android/app/.../ServiceState.kt` 将 core setup 返回的完整 `message` 或异常对象拼入 `Unable to set up core: ...`，并将启动/停止请求失败对象写入日志；`ServiceController.kt` 还将 bind/start/stop/disconnect/unbind 失败消息直接拼接，`ServiceBroadcastReceiver.kt` 将 action、超时和异常对象直接写入日志。固定源码不能证明任一具体 `message` 或 exception 必然包含订阅 URL、凭据、Cookie、节点名、请求目标或响应内容，但这些值若由下游异常提供，当前调用点没有屏障。
- 本轮仅做固定提交源码审计，没有读取设备 `logcat`、应用日志正文、配置、节点、订阅 URL、凭据、Cookie、请求或响应，也没有触发失败路径；因此不推断设备上实际出现过敏感日志内容。此前真机结束状态仍为 `VPN CONNECTED=0`、`tun0=0`、FlClash 进程数 `0`，且本轮没有改变设备设置。
- XToolpro 的未来 adapter 必须将 native/core 失败映射为稳定错误类别或代码，默认关闭敏感诊断，并在 Android 日志、UI、导出与遥测边界统一过滤地址、URL、凭据、节点/组名、应用包名列表和异常正文；应以 success、unavailable、cancel、crash、version mismatch 及日志脱敏契约测试证明。完成前该能力保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android MethodChannel 错误正文边界源码审计（2026-09-07）

- 固定 `android/app/.../plugins/ServicePlugin.kt` 的 `invokeMethod` 在 `ServiceController.invokeMethod` 失败时调用 `result.error("CORE_ERROR", error.message, null)`；该错误 code 虽然固定，但 message 仍来自原始异常，没有稳定类别映射或字段脱敏。`AppPlugin` 的 `tip` 分支把调用方传入的 `message` 直接交给 `GlobalState.application.showToast`，而 `showToast` 只做空值判断后显示完整文本。
- 这条边界与前述 Android `Log.d`/Flutter `commonPrint` 路径不同：即使未开启日志捕获，core/native 异常正文也可能经 MethodChannel 进入 Flutter 错误处理或 Toast。静态源码不能证明具体异常一定包含 URL、节点、凭据、Cookie、路径或响应内容，也不能证明设备上实际展示过这些值。
- 本轮未触发 MethodChannel 失败、未读取 Toast、logcat、应用日志、配置、节点、订阅 URL、凭据、Cookie、请求或响应；只做固定提交源码审计，没有改变设备设置。XToolpro adapter 必须把 native/core 失败转换为稳定错误 code 与本地化、脱敏用户文案，原始异常仅可留在受访问控制的内部诊断并默认禁用；完成错误映射和 UI/日志脱敏契约测试前，Proxy 台账保持 `Investigating`，矩阵保持 `Partial`，不进入正式 engine 集成。

### FlClash Flutter commonPrint 敏感字段生产者边界源码审计（2026-09-07）

- 固定 `lib/common/http.dart` 将待检查的完整 `url` 拼入 `commonPrint.log('find $url proxy: ...')`；`lib/common/request.dart` 将 URL 获取异常和 IP 检查异常正文直接记录。`lib/providers/actions/setup.dart` 将 `profile?.realLabel` 写入日志，`lib/manager/connectivity_manager.dart` 将 Wi‑Fi SSID 写入日志，`lib/application.dart` 记录 `ConnectivityResult.toString()`，`lib/providers/app.dart` 记录 IP 检查响应对象。上述调用点都在 `commonPrint.log` 前没有统一的 URL、标签、网络标识或响应字段过滤。
- 这些生产者可能把订阅/测速 URL、用户配置标签、局域网标识或网络响应细节送入 Flutter `debugPrint` 与内存 `logsProvider`；该静态结果只说明数据流存在，不能断言任一具体值在设备上产生，也不证明 IP 响应对象含有何种字段。
- 本轮未发送请求、未读取 Wi‑Fi SSID、IP 响应、logcat、应用日志、配置、节点、订阅 URL、凭据、Cookie 或导出文件；仅复核固定源码，设备设置未改变。XToolpro 必须在日志 API 入口按字段类型脱敏/拒绝、默认不记录 URL/SSID/响应正文，并对日志 UI/导出复用同一过滤合同；完成静态调用点扫描和设备契约测试前，Proxy 台账保持 `Investigating`，矩阵保持 `Partial`，不进入正式 engine 集成。

### FlClash 小米 10S 代理组入口重复真机复核（2026-09-07）

- 目标设备 `bf353dda` 上固定 dev 包 `com.follow.clash.dev` 的 launcher 组件解析为 `com.follow.clash.dev/com.follow.clash.MainActivity`；此前使用 `com.follow.clash.dev/.MainActivity` 的启动尝试返回 Activity 不存在，本次未修改 APK 或应用数据。
- 通过该真实 launcher 进入仪表盘并切换到“代理”标签后，ADB UI hierarchy 对固定文本“自动选择”和“故障转移”各计数 `1`。该复核只记录能力语义和出现次数，不读取节点名、配置正文、订阅 URL、凭据、Cookie、日志正文或 IP 内容；VPN 未启动，未改变代理设置。
- 结果与既有规则模式 capability-parity 证据一致：代理组入口在真实设备上可达，但本轮未切换具体节点或组、未发起流量、未证明选择结果持久化，因此矩阵行继续保持 `Partial`，Proxy 台账继续为 `Investigating`。

### FlClash 小米 10S 代理组入口进程重建复核（2026-09-07）

- 在不启动 VPN、不切换节点且不读取配置正文的前提下，对 `com.follow.clash.dev` 执行 `am force-stop`，再以真实 launcher 组件 `com.follow.clash.dev/com.follow.clash.MainActivity` 重启并进入“代理”标签。
- 重建后的 ADB UI hierarchy 对固定文本“自动选择”和“故障转移”各计数 `1`，与重建前结果一致。该计数只证明代理组入口在进程重建后仍可见，不读取节点名、组名、配置、订阅 URL、凭据、Cookie、日志正文或网络响应。
- 本轮未启动 VPN、未发起测试流量、未改动代理设置；该结果不覆盖节点切换、组选择持久化或流量路由命中，矩阵行继续保持 `Partial`，Proxy 台账继续为 `Investigating`。

### FlClash 代理组选择持久化路径源码复核（2026-09-07）

- 固定 `lib/providers/actions/profiles.dart` 的 `ProfilesAction.updateCurrentSelectedMap` 将组到节点的映射写入当前 Profile 的 `selectedMap`，`lib/providers/actions/proxies.dart` 的 `updateCurrentGroupName` 将当前组写入 `currentGroupName`；两条路径均经 `profilesProvider.put` 更新，而非仅停留在页面局部状态。
- 固定 `lib/database/profiles.dart` 与生成 Drift schema 将 `selectedMap` 和 `currentGroupName` 分别映射到 `selected_map`、`current_group_name` 字段；profile provider 启动时从数据库重新读取，因此源码合同包含持久化和重建恢复路径。该结论不等同于本轮改变或读取设备上的原始值。
- 本轮仅复核固定源码，未点击节点或组、未读取设备数据库、配置、节点名、组名、订阅 URL、凭据、Cookie、日志或网络请求；矩阵保持 `Partial`，因为尚未完成用户可见的具体切换、失败回滚和路由结果契约测试，Proxy 台账保持 `Investigating`。

### FlClash 代理节点选择与 core 同步失败边界源码审计（2026-09-07）

- 固定 `lib/views/proxies/card.dart` 的节点卡 `onPressed` 调用 `_changeProxy`；对可选择组，该方法先调用 `ProfilesAction.updateCurrentSelectedMap` 写入 Profile，再调用 `ProxiesAction.changeProxy`。后者经过 debounce 调用 `coreController.changeProxy`，成功后再重置/关闭连接并增加 IP 检查计数。
- 固定 `core/hub.go` 的选择接口在组不存在、组类型不可选或 `selector.Set(proxyName)` 失败时返回错误字符串；但 Flutter 点击链路在先写入 Profile 后没有显示的事务回滚，卡片 `onPressed` 也没有把异步失败映射为稳定的用户状态。因而 core 选择失败时，持久化 `selectedMap` 可能暂时领先于 core 实际选择，需由未来 adapter 以原子更新、回滚和 success/unavailable/cancel/crash/version-mismatch 契约覆盖。
- 本轮只复核固定源码，未点击节点、未读取设备数据库或原始选择值，也未读取配置、订阅 URL、凭据、Cookie、日志或网络请求；结论不证明该失败在设备上实际发生。矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 小米 10S 代理组详情展开复核（2026-09-07）

- 在代理页点击进入一个既有策略组详情，仅观察页面结构；ADB UI hierarchy 对“自动选择”和“故障转移”组标签合计计数 `2`，协议卡片计数 `6`。本轮不输出任何节点名称、流量额度、延迟值或其他卡片字段。
- 未点击节点卡、未切换当前选择、未启动 VPN、未发起流量，也未读取配置、数据库、订阅 URL、凭据、Cookie 或日志正文。该结果证明真实设备上的组展开和节点卡渲染可达，但不证明节点选择、失败回滚或路由命中；矩阵行继续保持 `Partial`，Proxy 台账继续为 `Investigating`。

### FlClash 小米 10S 代理组标签切换复核（2026-09-07）

- 在同一代理详情页仅切换两个既有组标签，ADB UI hierarchy 对每个标签各计数 `1`；两个标签页面均统计到 `6` 个协议卡片。切换完成后恢复到原标签。
- 本轮未点击节点卡、未改变当前节点选择、未启动 VPN、未发起流量，也未读取节点名称、配置、订阅 URL、凭据、Cookie 或日志正文。该结果证明多个组标签均可渲染并可逆切换，不证明组选择持久化、节点切换或实际路由命中；矩阵行继续保持 `Partial`，Proxy 台账继续为 `Investigating`。

### FlClash 延迟测试失败日志边界源码审计（2026-09-07）

- 固定 `lib/views/proxies/common.dart` 的 `proxyDelayTest` 在 `coreController.getDelay(url, proxyName)` 失败时调用 `commonPrint.log('Delay test failed for ${state.proxyName}: $error', ...)`；因此失败日志包含实际代理名称和原始异常对象。测试 URL 作为 `getDelay` 参数传递，但未在该模板中直接拼入日志；异常对象仍可能由下游携带请求细节。
- `coreFailureLogLevel` 仅按异常类型选择 debug/warning 级别，不执行字段脱敏；失败后 UI 状态写入延迟值 `-1`，但固定路径未见稳定错误代码、重试原因或日志过滤合同。该静态结果不证明任一失败在设备上实际发生。
- 本轮未执行测速、未读取节点名、测试 URL、配置、logcat、应用日志、凭据、Cookie 或网络请求；XToolpro 必须把延迟失败映射为稳定类别，禁止默认日志包含节点/组名、URL 或异常正文，并为重试与 unavailable 状态提供脱敏契约测试。矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 请求/连接列表字段边界源码审计（2026-09-07）

- 固定 `lib/models/common.dart` 的 `Metadata`/`TrackerInfo` 模型包含 UID、进程与进程路径、网络类型、源/目的 IP 与端口、host、规则及 rule payload、代理链、源/目的 GeoIP、ASN、DNS mode、special proxy/rules 和 remote destination。`TrackerInfo.desc` 还组合网络类型、host/目的 IP 与目的端口作为列表摘要；这些字段均来自 core 返回的连接记录。
- 固定 `lib/views/connection/connections.dart` 每秒调用 `coreController.getConnections()`，异常路径调用 `commonPrint.log('updateConnections error: $error', ...)`；空结果直接显示连接空态。固定 `lib/views/connection/item.dart` 在详情页直接渲染进程/UID、网络、规则、host、源/目的地址、上传/下载、GeoIP/ASN、DNS、special proxy/rules、remote destination 和代理链。
- `TrackerInfosStateExt.list` 的搜索字段仅包含 network、host、destination IP、process 和 proxy chains；源/目的端口、规则 payload、GeoIP/ASN、DNS、special proxy/rules 与 remote destination 不参与搜索。该差异属于能力覆盖边界，不代表这些字段在详情页不可见。
- 本轮仅复核固定提交源码，未读取设备连接正文、网络标识、请求目标、配置、节点、订阅 URL、凭据、Cookie、日志或导出文件，也未改变设备设置。该结果证明请求/连接列表及详情入口存在，但未证明设备上任一具体字段的实际值或敏感内容出现。
- XToolpro 的未来 `engine-proxy` 合同必须按字段最小化返回，默认隐藏或截断应用包名、UID、地址、host、规则 payload、GeoIP/ASN、代理链与远端目标，并统一将读取失败映射为稳定、脱敏错误；完成字段级脱敏、搜索/详情一致性和 success/unavailable/cancel/crash/version-mismatch 契约测试前，该行保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 请求内存保留与清理边界源码审计（2026-09-08）

- 固定 `lib/core/event.dart` 将 core `request` event 解析成完整 `TrackerInfo`；`lib/manager/core_manager.dart` 的 `onRequest` 直接调用 `requestsProvider.notifier.addRequest(trackerInfo)`。`Requests` provider 为 keep-alive 内存状态，追加前只复制 `FixedList`，未见字段脱敏、持久化或加密路径。
- 固定 `lib/providers/actions/setup.dart` 的 `fullSetup()` 将 requests provider 重置为 `FixedList(500)`；`FixedList.add` 在追加后截断到最大长度。启动初始 `AppState` 使用容量 `1000`，但完成 setup 后实际请求缓存容量为 `500`。`RequestsView` 仅提供搜索和自动滚动切换；固定路径未见用户清除、单项删除、保留期或导出入口。
- 这意味着请求记录是有界、进程内的完整连接元数据缓存；重新 setup 会替换为空列表，应用进程重建也不会从存储恢复。该生命周期减少长期留存，但不解决运行期间完整地址、host、应用识别、规则和代理字段进入内存/详情页的最小化与脱敏缺口。
- 本轮只读复核固定源码，并以 ADB 确认目标设备仍在线、FlClash 主界面可见；未打开“更多”菜单、不盲点按、不读取请求或连接正文、网络标识、配置、节点、订阅 URL、凭据、Cookie、日志或导出文件，也未改动设备状态。
- XToolpro 未来应将请求诊断明确设为默认关闭、短生命周期且按字段最小化的内存队列，提供本地清除和可验证的进程/重设清理合同；任何详情或导出必须再经同一脱敏层。完成保留上限、清理、脱敏与 success/unavailable/cancel/crash/version-mismatch 契约测试前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 连接关闭结果边界源码审计（2026-09-08）

- 固定 `lib/views/connection/connections.dart` 的单条阻断按钮直接以连接 ID 调用 `coreController.closeConnection` 后刷新；顶栏“关闭全部”图标没有确认步骤，调用 `coreController.closeConnections()` 时未 `await`，随后立即刷新列表。两条 UI 路径均未把布尔结果或异常映射为稳定的成功、失败、取消或重试状态。
- 固定 `core/hub.go` 的 `handleCloseConnections()` 遍历 trackers 并调用 `Close()`；任一项出错会停止剩余遍历，但该 handler 仍返回 `true`。`handleCloseConnection()` 对不存在的 ID 返回 `false`，但对已找到 tracker 的 `Close()` 错误使用空白赋值忽略并返回 `true`。Dart interface 再将缺失/空 bridge 返回值降级为 `false`，但页面调用点不读取这个结果。
- 节点切换后若“自动关闭连接”设置开启，`ProxiesAction.changeProxy` 同样调用 `closeConnections()`；关闭设置时改为 `resetConnections()`，其 core 实现仅重置 resolver connection。这两种路径都没有为用户提供逐条结果、部分失败或回滚语义。
- 本轮只读审计固定源码，未打开连接页、未点击关闭/阻断、不读取连接 ID 或正文，未发起流量、未改动 VPN、配置、节点、订阅 URL、凭据、Cookie、日志或导出文件。静态结论不代表目标设备发生过关闭失败。
- XToolpro 的未来 adapter 必须将单条与批量关闭视为有副作用的操作：先取得明确用户意图，await 受限结果，报告关闭成功/未找到/部分失败/不可用，并保留可重新建立连接的说明；不得把未执行或部分失败报告为成功。完成 success、partial-failure、cancel、engine-crash 与 version-mismatch 契约测试前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 请求缓存停止、重启与重设清理边界源码审计（2026-09-08）

- 固定 `lib/providers/actions/setup.dart` 中只有 `fullSetup()` 显式把 `requestsProvider` 重置为新的 `FixedList(500)`。`CoreManager` 仅在 `currentProfileIdProvider` 发生变化时调用 `fullSetup()`；这证明 Profile 切换会清空该内存队列，但不能外推为每次 VPN 停止、重连或配置应用都会清空。
- 普通 `setRunning(false)` 进入 `_stop()`：先停止 core listener/service，再调用 `resetCoreTraffic()`，清空 `trafficsProvider`，并将 `totalTrafficProvider` 置为零；该函数及其同步调用链未对 `requestsProvider` 执行 clear/reset。随后 `setRunning(true)` 只启动 listener 后以 debounce 调用 `applyProfile(force: true)`；`applyProfile()` 的 `_runSetup()` 也未重置请求队列。
- VPN 配置变化提示的“重启”明确按 `setRunning(false)` 后 `setRunning(true)` 执行。独立 core restart 则先完成 `coreController.restart()`，再依据运行状态重放 `setRunning(true, initialize: true)` 或 `applyProfile(force: true)`；两条路径同样不调用 `fullSetup()`。因此，在 Flutter app process 仍存活的前提下，普通停止/重连、该 VPN 配置重启和 core restart 均没有由这些固定调用点清理既有请求缓存的机制；不能据此断言 listener 停止期间仍会收到新 request event，亦不能替代实际设备生命周期 proof。
- 目标小米 10S 本轮仅做无内容 ADB 状态检查，确认序列号 `bf353dda` 仍为 `device`；未启动或停止 VPN、未进入“更多”菜单、未读取请求/连接、网络标识、配置、节点、订阅 URL、凭据、Cookie、日志、通知或任何设备数据库内容，也没有改动设备设置。
- XToolpro 的 future `engine-proxy` 必须在用户可感知的停止、重设、切换 Profile 和进程死亡边界上定义独立的请求诊断清理合同，并以无敏感字段的设备断言证明实际清理；诊断默认关闭、字段最小化，并提供本地清除。完成清理、脱敏与 success/unavailable/cancel/crash/version-mismatch 契约测试前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 请求事件回调与停止确认边界源码审计（2026-09-08）

- 固定 `android/app/.../plugins/ServicePlugin.kt` 的 `stop()` 只调用 `ServiceState.requestStop()`，随即向 Flutter MethodChannel 返回 `true`，没有 await 其 `Deferred<Boolean>`。`requestStop()` 再通过 `GlobalState.launch` 在后台协程调用 `ServiceState.stop()`；因此 Dart `service.stop()` 的成功只表示停止请求已提交，不能表示 service、TUN 或 core 已经停止。
- 固定 `ServiceController.stop()` 会在其协程内调用 bound `ManagedService.stop()`；VPN 实现同步执行 service modules cleanup、`Core.stopTun()` 与 `stopSelf()`。但 `stopSelf()` 没有提供 service 销毁完成回执；若 bound-service 调用失败，controller 仅写日志，仍 clear binding 并把 `runTimeMillis` 置零。`ServiceState.stop()` 不检查该调用的成功结果，仍设 `RunState.STOPPED`。故代码确实尝试同步清理 TUN，却没有把 Android service 销毁、TUN 消失或 core 健康停止核验为对 Flutter 的停止完成合同。
- 固定 `ServicePlugin.initialize()` 注册 `ServiceController.setEventListener(::sendEvent)`；只有 `onDetachedFromEngine()` 显式调用 `setEventListener(null)`。普通 stop 没有解除这个 core listener，且存活的 `CoreManager` 仍保有 request event consumer。该事实与请求缓存未在普通 stop 清空的结论相互独立；本轮不能据此推断 listener 停止期间仍会接收新的 request event。
- 固定 `core/lib.go` 的 `stopTun()` 会先在锁内关闭 `TunHandler`，随后同步调用 `handleStopListener()`，后者把 `isRunning` 置为 false 并停止 inbound listeners；这收窄了停止后产生新 TUN 请求事件的推断。但 `RequestMessage` 使用独立的全局 bulk queue，常驻 batcher 每 16ms 批量投递，普通 stop 不清空该 queue，也不解除 `eventListener`。因此已在停止前入队而尚未投递的 request batch 仍可能在 stop 返回后送达 Flutter；这是源码并发边界，未在设备上读取或验证任何请求。
- 固定 `core/lib.go` 将 `eventListener` 作为无显式同步原语的全局 `unsafe.Pointer`；`setEventListener()` 在 engine detach 期间先经 C bridge 的 `DeleteGlobalRef` 释放旧 listener 再赋值，`sendMessageBatch()` 则独立读取该指针并经 `CallVoidMethod` 投递。固定 C++ 路径未显示能把解绑、队列投递和 in-flight JNI callback 串行化的锁、引用计数或 completion 等待。因此 detach 与 batcher 并发时，存在“已读取 listener、尚未 invoke、引用被释放或替换”的静态生命周期风险；本轮未构造 race、未读取设备事件，不能据此认定设备发生过 crash、UAF 或事件泄露。
- 停止态小米 10S 真机中，dashboard 的核心状态控件自身没有可访问性标签，只有其父节点暴露“核心状态”语义；按精确 child button 边界触发后立即以 Android BACK 取消。其后只读复核 `tun0` 不存在、FlClash 系统 VPN 标记计数为 0，且仍停留 `MainActivity`。固定 `CoreStatusButton` 源码在确认结果不为 `true` 时直接返回，因此这只形成取消不启动的无内容证据，不证明确认文案、重启成功或任何请求/诊断数据行为。
- 本轮除对停止态核心状态控件执行一次精确触发并立即以 BACK 取消外，仅做固定 FlClash 提交源码审计和无内容 ADB 状态检查；未启动 VPN、未改变配置或持久化设备状态，未读取请求/连接、日志、网络标识、通知、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。
- XToolpro 的 future `engine-proxy` 必须将“停止请求已提交”与“实际停止已核验”分为不同状态：await 受限的停止结果，并在实际停止完成或失败/超时时，以单一生命周期锁或串行执行器停止事件生产、drain/drop 队列、解绑消费者、等待 in-flight callback 后再释放 JNI/engine 引用，随后清理诊断队列并给出稳定脱敏错误。需以无敏感字段的设备断言覆盖成功、失败、超时、取消、engine crash、detach/restart racing 与 version mismatch；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 仪表盘启动请求与 TUN 回执真机复核（2026-09-08）

- 小米 10S（`bf353dda`，API 33）处于停止态时，先以 ADB 无内容检查确认 `tun0` 不存在且 FlClash 进程数为 1。固定 `lib/views/dashboard/widgets/start_button.dart` 显示 `StartButton.handleSwitchStart()` 调用 `toggleRunning()`；经 UI hierarchy 的“00:00:00”运行时控件精确边界触发该按钮后，在 10 秒及再等待 10 秒的检查中 `tun0` 仍不存在。未读取按钮附近的配置、网络标识或错误文本。
- 随后用同一已定位运行控制再次触发，5 秒后仍为无 `tun0`、FlClash 进程数 1；未发起流量，也未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。该操作仅尝试运行时启停，未修改配置或其他持久化设备设置。
- 因未形成 TUN，未执行原计划的运行态 force-stop / process-death 测试，也不把该 UI 请求写为 VPN 启动成功，更不由此判断配置、core 或系统权限的故障原因。该次复核与先前成功建立 VPN/TUN 的证据并存，表明固定包的 UI 请求缺少可复现、可观察的实际启动回执。
- 固定 `lib/providers/actions/common.dart` 的 `toggleRunning()` 只将反转后的布尔值交给 `setRunning()`；后者先执行 `_setLocalRunning(running)`。已初始化的启动分支仅 await `_setCoreRunning()`，之后以不 await 的 debounce 排队 `applyProfile()`；实际 Android profile/core setup 因此不属于该 UI 操作的完成条件。`_setCoreRunning()` 不检查 `setCoreRunning()` 返回的 `startListener()` 布尔结果，故该值不会阻止后续 profile 应用。`ServicePlugin.start()` 同样只提交 `ServiceState.requestStart()` 并立即返回 `true`。直到 `VpnService.handleStart()` 的 `Builder.establish()` 返回 descriptor，代码才设置 `tunRunning` 并调用 `Core.startTun()`。固定路径没有把 UI 请求、listener/service start、profile setup、system establish、TUN 存在及流量可用性合并为单一完成回执。
- XToolpro 的 future `engine-proxy` 必须将用户请求、`VpnService` establish、TUN 存在、core health 与可转发流量区分为独立、受限且可观测的状态；在无回执、超时或错误时保持可恢复的 unavailable/error 状态，不得呈现“已连接”。完成 success、unavailable、cancel、crash、process-death、竞争 VPN、permission revoke 与 version-mismatch 契约测试前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 配置应用失败与启动状态一致性源码审计（2026-09-08）

- 固定 `lib/providers/actions/setup.dart` 的 `_setupConfig()` 把配置写入和 `coreController.setupConfig()` 放入 `globalState.loadingRun(silence: true)`。`lib/state.dart` 中的 `safeRun()` 会捕获任意异常、记录它，并直接把 `e.toString()` 交给通知；随后返回 `null`。`_setupConfig()` 不检查该返回值，仍返回 `completed`。因此，配置应用异常不会经 `_runSetup()` 作为失败结果回传给启动调用者。
- 初始化启动把 `_interface.setupConfig(params)` 与 `_setCoreRunning()` 放入 `Future.wait`；普通启动则在 `_setCoreRunning()` 后不 await 地 debounce `applyProfile()`。但 `_setCoreRunning()` 的返回类型为 `Future<void>`，其内部只 await `setCoreRunning()`，没有检查 `startListener()` 返回的布尔值。故 listener/service 请求失败及后续配置应用失败均不构成可靠的启动失败回执或自动本地状态回滚。
- 下层失败传播并非完全缺失：`VpnService.start()` 在 `Builder.establish()` 返回空 descriptor 或 `Core.startTun()` 抛错时会 stop 并抛出；`ServiceController.start()` 会记录失败、清理 binding 并令 runtime 为零；`ServiceState.start()` 据此返回 `false`。但是 `ServicePlugin.start()` 仅调用 `ServiceState.requestStart()`，立即经 MethodChannel 返回 `true`，不 await `Deferred<Boolean>`。这与真机仪表盘请求后未出现 `tun0` 的观察相容，但不能单凭静态路径判定该次失败的具体原因。
- 固定 native `core/lib.go` 的 `quickSetup()` 在解析 setup 参数和调用 `handleSetupConfig()` 之前先将全局 `isRunning` 置为 `true`；解析或应用配置失败后仅返回错误字符串，未在该函数中复位该标记。`startTUN()` 则根据同一标记选择 `handleStartListener()` 或 `handleResetConnections()`。该全局状态与 Dart 侧并行 `.wait` 没有形成 config-before-listener 的完成顺序；这是静态状态一致性风险，不能据此断定上述真机启动未形成 TUN 的根因。
- 固定 `VpnService.handleStart()` 会在调用 `Core.startTun()` 前设置 `tunRunning=true`。Android `Core.kt` 将 JNI `startTun` 声明为无返回值，C++ bridge 也为 `void`；Go `startTUN()` 虽声明 `bool`，但它在调用 `handleStartTun()` 后无条件返回 `true`，该值没有被 JNI/Kotlin 消费。因此除 JNI 抛错外，service 不具备 core TUN handler 已健康启动的返回信号；`tunRunning` 不能单独作为 core health 或真实转发可用性的证据。
- 本轮只读取固定上游源码，未操作设备，也未读取任何配置、错误文本、日志、通知、请求/连接、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。不能将原始异常文本的存在外推为具体敏感字段已暴露。
- XToolpro 的 future `engine-proxy` 必须以单一可 await、可取消状态机串联配置写入、core setup、permission、service start、`establish()`、TUN 核验和最小的连通性证明；所有失败、取消和超时必须收敛为稳定、脱敏错误码并回滚本地运行状态。需以无敏感字段的真机契约测试覆盖成功、配置错误、permission deny/revoke、竞争 VPN、`establish()` 拒绝、超时、engine crash、process death 与 version mismatch；在此之前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 原生 START、强停与重开恢复真机验证（2026-09-08）

- 小米 10S（`bf353dda`，API 33）先以无内容 ADB 检查确认 `tun0` 不存在、dev 进程存在。设备同时安装默认与 dev 包；对默认包的 `.QuickActionActivity` 调用返回“不存在”，未触及应用状态。随后仅解析 dev 变体的 `com.follow.clash.dev.action.START`，得到组件 `com.follow.clash.dev/com.follow.clash.QuickActionActivity`。固定 `QuickAction.action` 以运行时 `applicationId` 组成 action，故这一步避免把错误变体的 component/action 当成能力结果。
- 调用已解析的 dev `START` action 后，仅检查 `/sys/class/net/tun0` 的存在性和 dev 进程：第 7 秒与第 14 秒均为 `tun0` 存在、dev 进程存在。没有发送流量，也没有读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。
- 随后执行 `am force-stop com.follow.clash.dev`；第 5 秒 `tun0` 不存在且 dev 进程不存在。再解析并打开 dev `MainActivity`，第 8 秒 dev 进程存在而 `tun0` 仍不存在，证明此轮强停后的正常打开没有在没有新的启动请求时恢复 TUN。
- 该 evidence 证明正确变体的原生显式启动可建立 TUN，并覆盖一次运行态 force-stop 清理和无意自动恢复边界；它不证明 core health、可转发流量、系统重启、always-on/lockdown、竞争 VPN、通知权限撤销或 service callback 的恢复行为。XToolpro 的 future `engine-proxy` 仍须以独立持久状态机、脱敏稳定错误和 TUN/健康/流量分别核验；完成 success、unavailable、cancel、crash、reboot、permission revoke 与 version-mismatch 契约测试前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 运行态 `am kill` 边界真机观察（2026-09-08）

- 通过已解析的 dev START action 建立运行态；第 7 秒仅确认 `tun0` 存在、dev 进程存在。随后发出 `am kill com.follow.clash.dev`；第 5 秒和第 10 秒仍均为 `tun0` 存在、dev 进程存在。
- 因此前台进程和 TUN 未被该命令终止，本轮没有制造实际 process death 或 crash，不能把它记录为恢复能力通过。随后单独正确 dev STOP action 在第 5 秒和第 10 秒均使 `tun0` 不存在、dev 进程存在，恢复停止基线。
- 全程未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。仍需专门、可恢复的 engine crash/process-death、reboot、always-on、permission revoke 与 version-mismatch 真机契约测试；在此之前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 原生 STOP 与实际 TUN 清理真机验证（2026-09-08）

- 通过已解析的 `com.follow.clash.dev/com.follow.clash.QuickActionActivity` 和 `com.follow.clash.dev.action.START` 建立运行态；第 7 秒仅检查到 `tun0` 存在、dev 进程存在。
- 随后向同一已解析组件发出 `com.follow.clash.dev.action.STOP`。第 5 秒 `tun0` 不存在而 dev 进程仍存在；再等 5 秒，第 10 秒结果相同。该证据证明本轮原生显式 STOP 实际清理了 TUN，同时保留应用 UI 进程。
- 本轮没有发送流量、读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件，也没有改动持久化配置。该实际最终状态不等价于 Flutter `ServicePlugin.stop()` 的同步回执，亦不能证明 service destroy completion、core health、流量/请求缓存清理、失败/超时、reboot、always-on/lockdown、竞争 VPN 或 permission revoke 行为。
- XToolpro 的 future `engine-proxy` 仍须将“停止请求已提交”和“实际停止已核验”区分为可 await 状态，并以脱敏错误、TUN/服务/core 分别断言覆盖 success、unavailable、cancel、timeout、crash、process death、reboot、permission revoke 与 version mismatch；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev TOGGLE 共享 action 真机验证（2026-09-08）

- 停止基线下仅检查到 `tun0` 不存在。向已解析的 dev `QuickActionActivity` 发出 `com.follow.clash.dev.action.TOGGLE` 后，第 7 秒 `tun0` 存在且 dev 进程存在。
- 在该运行态向同一组件发出同一 TOGGLE action。第 5 秒、第 10 秒均检查到 `tun0` 不存在，dev 进程仍存在。该轮证明共享 action 在目标设备上能完成实际的 TUN 启动和停止切换。
- 固定源码中动态快捷方式的 `toggle` intent 以及 `TileService` 的点击路径均创建/打开 `QuickAction.TOGGLE.quickIntent`；本轮直接验证共享 action，而没有操作系统桌面快捷方式或快捷设置磁贴 UI。因此不外推为系统入口本身已在此轮验证，也不证明 core health、可转发流量、通知状态、异常/超时、reboot、竞争 VPN、permission revoke 或辅助功能表现。
- 全程未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。XToolpro 未来仍须将该类入口改为受保护的内部状态机命令并 await 实际 TUN/health 回执；完成相应无敏感字段的契约测试前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 冷启动原生 fallback START/STOP 真机验证（2026-09-08）

- 先强停 `com.follow.clash.dev`，3 秒后仅确认 `tun0` 不存在、dev 进程不存在。未启动 dev `MainActivity`，直接向已解析的 `QuickActionActivity` 发出正确 dev START action；第 7 秒和第 14 秒均确认 `tun0` 存在。
- 再向同一组件发出正确 dev STOP action；第 5 秒 `tun0` 不存在、dev 进程存在。该轮表明未先附着 Flutter 主界面时，原生 fallback 路径仍能在目标设备建立并清理 TUN。
- 固定 `ServiceState.handleStartAction()` 在没有 `TilePlugin` 时落入 `loadPreferencesAndStart()`；仅当 Flutter engine 已附着并提供 TilePlugin 时走 listener 分派。真机结果与原生 fallback 可达相容，但不单独证明哪个内部时序、配置或核心状态造成结果，也不验证已附着 listener 分支。
- 本轮未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。未覆盖 core health、可转发流量、通知权限、reboot/always-on、竞争 VPN、异常/超时或持久恢复。XToolpro future `engine-proxy` 仍须将启动来源、实际 TUN/health 回执和恢复意图隔离为可 await 的受保护状态机；完成契约测试前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 主界面已启动后的原生 START/STOP 真机验证（2026-09-08）

- 固定 `MainActivity.configureFlutterEngine()` 会注册 AppPlugin、ServicePlugin、TilePlugin 并调用 `ServiceState.attachFlutterEngine()`。本轮先打开正确 dev `MainActivity`，等待 8 秒；仅检查到 `tun0` 仍不存在。
- 随后发出已解析的 dev START action；第 7 秒、第 14 秒均检查到 `tun0` 存在。再发出同一组件的 STOP action；第 5 秒检查到 `tun0` 不存在。该轮证明在主界面已启动的 app 生命周期下，原生 action 仍能建立并清理 TUN。
- 真机检查只覆盖 TUN 存在性与进程，不读取 Flutter engine/plugin、Tile listener、Dart 状态或 UI 内容。因此不能从该结果判定 action 必然经过或绕过 listener 分派；也不验证 core health、流量、通知权限、异常/超时、reboot、竞争 VPN 或 permission revoke。
- 本轮未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。XToolpro future `engine-proxy` 必须提供受保护、可 await 的命令与实际 TUN/health 回执；完成契约测试前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 快速 START→STOP 取消收敛真机验证（2026-09-08）

- 强停 dev 变体后，停止基线仅确认 `tun0` 不存在。紧接着连续发出已解析的 dev START 与 STOP action；5 秒后 `tun0` 仍存在、dev 进程存在。因此该 STOP 请求在本次快速序列中没有成为实际最终停止状态。
- 为恢复测试基线，单独再次发出正确 dev STOP action；第 5 秒和第 10 秒均确认 `tun0` 不存在，dev 进程仍存在。未发送流量、未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。
- 固定 `QuickActionActivity` 为每个 action 各自启动 `GlobalState.launch` 协程。`handleStartAction()` 先进入 `loadPreferencesAndStart()`，仅在 `setupCore()` 成功后创建 running request；`handleStopAction()` 若当前尚无 running request 会直接返回。故存在“STOP 早于 START 建立 request 而被忽略，随后 START 继续”的静态可行顺序，与本轮最终 TUN 观察相容；本轮没有读取内部调度，不能据此断定唯一根因或发生过特定竞态。
- XToolpro future `engine-proxy` 必须在 command intake 即以单一串行、可取消状态机记录 STOP 意图，使其能取消/覆盖 setup、permission、service-start 与 TUN-start 的任意未完成阶段；仅在实际 TUN/core health 停止核验后才报告停止成功。需以无敏感字段的 rapid-start-stop、rapid-stop-start、timeout、crash、process death、reboot、permission revoke 与 version-mismatch 契约测试覆盖该边界；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 快速 STOP→START 启动收敛真机验证（2026-09-08）

- 停止基线仅确认 `tun0` 不存在、dev 进程存在。紧接着连续发出已解析的 dev STOP 与 START action；第 5 秒和第 10 秒均为 `tun0` 存在、dev 进程存在。因此本次快速相反序列最终形成实际 TUN 运行态。
- 为恢复测试基线，单独再次发出正确 dev STOP action；第 5 秒和第 10 秒均确认 `tun0` 不存在、dev 进程仍存在。全程未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。
- 本轮只观察最终 TUN/进程状态，未读取内部调度，故不能证明命令经过串行化、旧 STOP 被取消或任何特定协程时序。它仅表明此观察窗口内后到的 START 可收敛为 TUN 运行；与快速 START→STOP 结果一起说明，外部 action 请求本身不是可 await 的实际最终状态回执。
- XToolpro future `engine-proxy` 必须将相反命令的意图线性化，且在返回最终 running/stopped 前分别核验实际 TUN 与 core health；需以无敏感字段的 rapid-start-stop、rapid-stop-start、timeout、crash、process death、reboot、permission revoke 与 version-mismatch 契约测试覆盖。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 重复 START 请求真机验证（2026-09-08）

- 停止基线仅确认 `tun0` 不存在、dev 进程存在。紧接着连续发出两个已解析的 dev START action；第 5 秒和第 10 秒均为 `tun0` 存在、dev 进程存在。
- 随后单独发出正确 dev STOP action；第 5 秒和第 10 秒均确认 `tun0` 不存在、dev 进程仍存在，恢复停止基线。全程未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。
- 固定 `handleStartAction()` 在 `isRunningRequested()` 为真时返回；但本轮不读取 running request 的创建时间、协程调度、listener/core 次数或资源状态。因此只能证明该一轮重复 action 最终形成且可清理 TUN，不能声称请求严格幂等、仅发生一次启动或无泄漏。
- XToolpro future `engine-proxy` 必须以单一串行的去重 command contract 处理冗余启动，并在返回成功前核验实际 TUN/core health；还须以无敏感字段的重复启动、rapid-start-stop、rapid-stop-start、timeout、crash、process death、reboot、permission revoke 与 version-mismatch 契约测试覆盖。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 重复 STOP 请求真机验证（2026-09-08）

- 通过已解析的 dev START action 建立运行态；第 7 秒仅确认 `tun0` 存在、dev 进程存在。随后连续发出两个正确 dev STOP action；第 5 秒和第 10 秒均为 `tun0` 不存在、dev 进程存在。
- 本轮最终恢复停止基线。全程未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。
- 固定 `handleStopAction()` 在 `isRunningRequested()` 为假时返回；本轮没有读取 running request 的清除时机、service/core stop 次数、callback 或资源销毁状态。因此只能证明此轮实际 TUN 最终清理，不能声称两条 STOP 严格幂等、各自均完成或不存在重复释放风险。
- XToolpro future `engine-proxy` 必须以单一串行的停止 contract 合并冗余 STOP，并在回报停止前核验实际 TUN/core health；还须以无敏感字段的重复停止、rapid-start-stop、rapid-stop-start、timeout、crash、process death、reboot、permission revoke 与 version-mismatch 契约测试覆盖。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 快速双 TOGGLE 停止态收敛真机验证（2026-09-08）

- 停止基线仅确认 `tun0` 不存在、dev 进程存在。紧接着连续发出两个已解析的 dev TOGGLE action；第 5 秒和第 10 秒均为 `tun0` 不存在、dev 进程存在。
- 为确认设备仍在停止基线，单独再发出正确 dev STOP action；5 秒后 `tun0` 仍不存在、dev 进程存在。全程未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。
- 固定 `handleToggleAction()` 先读取 `isRunningRequested()` 再委托 start/stop；本轮没有读取该值、任何内部调度或短时间状态。因此结果仅与“连续两次切换最终回到停止态”相符，不能证明各 action 线性化、期间从未启动，或运行态快速双 TOGGLE 同样安全。
- XToolpro future `engine-proxy` 不得以并发读取后切换作为唯一命令模型，必须以线性化的显式目标状态和实际 TUN/core health 回执完成；仍需无敏感字段的快速双切换、重复请求、timeout、crash、process death、reboot、permission revoke 与 version-mismatch 契约测试。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 快速双 TOGGLE 运行态收敛真机验证（2026-09-08）

- 先以已解析的 dev START action 建立运行态；第 7 秒仅确认 `tun0` 存在、dev 进程存在。紧接着连续发出两个正确 dev TOGGLE action；第 5 秒和第 10 秒均为 `tun0` 存在、dev 进程存在。
- 随后单独发出正确 dev STOP action；5 秒后 `tun0` 不存在、dev 进程存在，恢复停止基线。全程未发送流量，未读取系统 VPN、通知、日志、请求/连接、配置、节点、订阅 URL、凭据、Cookie、设备数据库或导出文件。
- 此结果与运行态偶数次切换最终保持运行相符。但本轮不读取 `isRunningRequested()`、短时间 TUN 状态、协程调度或资源计数，不能证明 action 逐个线性化、期间没有反向状态或不存在并发资源竞争。
- XToolpro future `engine-proxy` 必须以线性化的显式目标状态、去重/取消语义和实际 TUN/core health 回执替代并发“读取后切换”；仍需无敏感字段的快速双切换、重复请求、timeout、crash、process death、reboot、permission revoke 与 version-mismatch 契约测试。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android 13 通知权限撤销下共享 TOGGLE 契约（2026-09-08）

- 小米 10S（`bf353dda`，API 33）停止基线下，在确认 permission/app-op 允许后，以正常 `pm revoke` 临时撤销 dev 变体 `POST_NOTIFICATIONS`。UID 层 app-op 显示 `POST_NOTIFICATION: ignore`，同次 package 层查询仍显示 `allow`；因此不将二者压缩为单一授权状态。
- 强停 dev 变体后，以已解析的 `com.follow.clash.dev/com.follow.clash.QuickActionActivity` 与 `com.follow.clash.dev.action.TOGGLE` 冷启动；第 7 秒及第 14 秒均仅确认 `tun0` 不存在、dev 进程存在。随后同一精确 STOP action 后仍无 TUN，最后以正常 `pm grant` 恢复 permission，package 层显示 `allow`。
- 固定源码表明动态快捷方式和 `TileService` 共用该 TOGGLE action；本轮只证明这个共享 action 在撤销态观察窗口没有形成 TUN，不能外推至桌面快捷方式/系统磁贴 UI、前台通知可见性、Android permission callback、用户可见 unavailable/cancel、core 健康或流量。未发送流量，未读取系统 VPN、通知正文或 extras、日志、配置、节点、订阅 URL、凭据、Cookie、请求、数据库或文件。
- XToolpro 未来必须使所有 VPN 入口在真实授权结果不可用时一致收敛为可恢复且脱敏的未启动状态；用相同的有界 completion/health 回执覆盖 direct start、toggle、notification 和 tile 路径。在完整 adapter 与五类契约测试完成前，该行保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android 13 通知权限 callback 并发语义静态审计（2026-09-08）

- 对固定 FlClash `62addf738a76b1a492e19af2dbabdb6d572b9e72` 的公开 `android/app/.../plugins/AppPlugin.kt` 逐行复核：`requestNotificationPermission` 只保存一个 `requestNotificationCallback`。每个新请求会先向旧槽位回调 `false`，再覆盖为新 callback；若 `isRequestingNotificationPermission` 已为 true，新请求不调用 Android 权限 API、直接返回，等待正在进行的系统 dialog。
- 系统 listener 只检查 request code，随后设置进程内 `skipNotificationPermissionRequest=true`，并对当前槽位无条件回调 `true`，不读取 `permissions` 或 `grantResults`。因此正在等待的第二个启动意图可接收第一个 dialog 的无条件成功回调，而原始 callback 已先收到 `false`；该实现没有逐请求 transaction、真实授权结果对应关系或稳定的拒绝/取消/不可用映射。activity 或 engine detach 仅以 `false` 结束当时的当前槽位。
- 本轮仅通过公开固定提交的只读源码审计完成；未下载、写入或修改上游源码、SDK、缓存或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须为每次 permission request 保留可取消、可归因的 completion，读取实际 grant result，并将 denied/revoked/no-activity/detach 统一收敛为脱敏且可恢复的未启动结果；还需以单/并发请求、拒绝、撤销、process recreation 与通知可见性真机契约覆盖。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 通知权限等待期间 STOP 取消收敛静态审计（2026-09-08）

- 固定 `ServiceState.requestStart()` 在调用 `AppPlugin.requestNotificationPermission()` 后立即返回一个 `CompletableDeferred`，但没有将该 Deferred 或 notification callback 登记为可由 `requestStop()` 取消的资源。`AppPlugin` 只提供 `cancelVpnPreparation`，没有对等的 notification-permission cancel API。
- 若用户/入口在系统通知权限 dialog 未返回前调用 `requestStop()`，`ServiceState.createRequest(false)` 只替换 `latestRequest`，随后异步执行 stop；原 start Deferred 仍等待 AppPlugin 的 callback，直到系统结果或 plugin detach。回调若为当前槽位的无条件 `true`，`start()` 会因 request token 已不再 current 返回 `false`，故可避免该回调继续实际启动；然而不代表原请求获得即时或有界的 `Cancelled` completion，也没有证明 dialog、UI loading 或后续重入会一致收敛。
- 本轮仅只读审计固定提交的公开 `ServiceState.kt` 与 `AppPlugin.kt`；未写入/下载上游源码、未修改 SDK、缓存或构建产物，未使用 ADB，也未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须令 STOP/生命周期销毁立即取消所有尚未完成的 permission/start transaction，完成一次且仅一次的脱敏 `Cancelled` 结果，并以有界 timeout 防止 UI 或调用方无限等待；还需在真机覆盖 permission dialog 等待期间的 STOP、重复 START、detach/recreate、deny/revoke 与前台通知可见性。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 通知权限 callback 后撤销绕过静态审计（2026-09-08）

- 固定 `AppPlugin` 的 `skipNotificationPermissionRequest` 初始为 `false`，唯一写入点是 notification permission result listener 的无条件 `true` 设置；在同一 plugin/Flutter engine 生命周期中没有将它复位为 `false` 的路径。
- 后续 `requestNotificationPermission()` 先取得 `ContextCompat.checkSelfPermission`，但条件为“系统已授权 **或** skip flag 为 true”时即回调 success。因此在某次 callback 后，即使 Android 系统随后撤销 `POST_NOTIFICATIONS` 且 `checkSelfPermission` 不再为 granted，后续请求仍会由 skip flag 直接走 success，不会重新请求或返回 unavailable/cancel。该结论是固定源码控制流，不声称设备已复现此序列。
- 本轮仅通过公开固定提交只读检索 `AppPlugin.kt` 完成；未下载、写入或修改上游源码、SDK、缓存或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须在每次 VPN 启动前重新基于系统授权状态作决定，不得用跨请求/跨撤销的 skip flag 替代权限结果；还需真机验证已允许后撤销、拒绝后重试、process recreation 与通知可见性，并将未授权状态收敛为脱敏、可恢复的未启动结果。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash VPN 授权等待期间 STOP 取消收敛静态审计（2026-09-08）

- 固定 `ServiceState.start()` 在 `startPreparationLock` 内 await `prepareVpn()`。后者借 `suspendCancellableCoroutine` 把 `AppPlugin.prepareVpn()` 的 callback 转为 Boolean；只有该 coroutine 自身被取消时，才会调用 `AppPlugin.cancelVpnPreparation(callback)`。
- `ServiceState.requestStop()` 不持有或取消正在执行的 start coroutine，只以新的停止 `RunRequest` 替换 token 后异步执行 stop。因此若 Android `VpnService.prepare()` 已展示/等待系统 consent，STOP 不会立即触发 `cancelVpnPreparation` 或完成原 start Deferred；等待会持续至系统 result、plugin detach 或其他上游完成。若 token 已失效，随后 `start()` 会在锁中拒绝继续启动，但这不是有界的 `Cancelled` completion，也不证明 consent UI 或调用方 loading state 已收敛。
- 本轮仅只读审计固定公开 `ServiceState.kt` 与此前已读取的 `AppPlugin.kt`；未下载、写入或修改上游源码、SDK、缓存或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须以单一可取消 transaction 绑定通知权限、VPN consent、core setup 与 service start；STOP、detach 和 timeout 都必须令每个等待者一次性完成脱敏 `Cancelled`/`Unavailable`，再以真机验证 consent 等待期间的 STOP、deny/revoke、竞争 VPN、detach/recreate 与实际 TUN 清理。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 配置变更期间权限等待状态静态审计（2026-09-08）

- 固定 `AppPlugin.onDetachedFromActivityForConfigChanges()` 仅把 `activity` 置空；与完全 activity detach/engine detach 不同，它不以 `false` 完成 notification 或 VPN callback。`onReattachedToActivityForConfigChanges()` 重新保存 Activity 并注册 activity-result 与 permission-result listener；既有 callback 槽和 `isRequestingNotificationPermission` 标志均在内存中保留。
- 这不是对 Android 结果必然丢失的断言：重新附着的 listener 可能收到系统结果。但固定实现没有 transaction ID、持久恢复记录、timeout 或 stable state 来验证“旧 Activity 发起的 dialog 结果”与“新 binding 上仍等待的原请求”可靠对应。对 VPN consent 也同样只依赖随后 delivery 到 `onActivityResult`；没有 configuration-change 真机契约证明 callback、loading 和 STOP/retry 一致收敛。
- 本轮先以允许范围内 ADB 只读复核小米 10S `bf353dda`：设备连接、`tun0=0`、dev 进程计数为 1、`POST_NOTIFICATION=allow`；随后只读固定公开 `AppPlugin.kt` 与 `ServiceState.kt`。未写入设备或上游源码，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须为 permission/consent transaction 定义可恢复或显式取消的生命周期合同，并以真机覆盖配置变更期间的 notification permission、VPN consent、STOP、retry、deny/revoke 与实际 TUN/前台通知状态；未证实前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 通知权限撤销后 BACK 取消尝试真机验证（2026-09-08）

- 小米 10S（`bf353dda`，API 33）先强停 dev 变体并以正常 `pm revoke` 临时撤销其单一 `POST_NOTIFICATIONS` permission。撤销后 UID 层 `POST_NOTIFICATION` 为 `ignore`，同次 package 层仍显示 `allow`；开始时 `tun0=0`、dev 进程计数为 0。
- 随后从 launcher 启动 dev 主界面，等待 5 秒后对精确 `QuickActionActivity` 发送 `com.follow.clash.dev.action.START`。2 秒后仅以普通 Android BACK keyevent 发送取消尝试，未读取任何窗口、permission dialog 或通知内容。第 7 秒和第 14 秒只确认 `tun0=0`、dev 进程存在；再发送同一精确 STOP action 后 5 秒仍为 `tun0=0`、进程存在。
- 本轮未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL，也未发送流量。最后以正常 `pm grant` 恢复 `POST_NOTIFICATIONS`，package 层 app-op 显示 `allow`；设备没有遗留 TUN。因取消发生时的可见窗口内容未被读取，本轮不能证明系统 permission dialog 当时存在、BACK 实际触发 permission callback，或 UI 已显示稳定 `Cancelled`/`Unavailable`。
- 结果仅补充“已撤销态 attached-activity + BACK 取消尝试”观察窗口内未形成 TUN；它不能替代对实际 callback result、前台通知可见性、permission dialog 生命周期或完整 VPN/core health 的真机契约。XToolpro future `engine-proxy` 仍须以逐请求、可观察的真实授权结果和一次性 `Cancelled` completion 作为启动门控；完成 deny/revoke/retry/recreate/visibility 设备契约前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 通知权限撤销期间进程终止与重开边界（2026-09-08）

- 小米 10S（`bf353dda`，API 33）停止基线下，强停 dev 变体并以正常 `pm revoke` 临时撤销 `POST_NOTIFICATIONS`。UID 层 `POST_NOTIFICATION` 为 `ignore`，同次 package 层仍显示 `allow`；此时 `tun0=0`、进程计数为 0。
- 从 launcher 启动主界面、等待 5 秒并对精确 START action 仅等待 2 秒后，以 `am force-stop --user 0` 终止该包。5 秒后只确认 `tun0=0`、进程计数为 0、权限/app-op 状态仍处于同一撤销层级。随后再次从 launcher 请求启动，等待 8 秒时仍只观察到 `tun0=0`、进程计数为 0；未读取窗口、Activity、permission dialog 或任何应用内容，故不将该计数外推为 launcher 渲染、callback 或自动恢复的证明。
- 发送同一精确 STOP action 后 5 秒仍无 TUN、进程存在；最后以正常 `pm grant` 恢复 `POST_NOTIFICATIONS`，package 层显示 `allow`。全程未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL，亦未发送流量。
- 该检查仅证明本次撤销态下的强停会留下无 TUN/无进程状态，并且后续观察窗口未见自动 TUN 恢复；它不替代实际 permission callback、前台通知可见性、用户可见 unavailable/cancel、VPN consent 或 core health 验证。XToolpro future `engine-proxy` 必须把 process death/recreate 期间的每个等待 transaction 显式收敛为可恢复、脱敏结果，并以真机覆盖 deny/revoke/recreate/visibility 与 TUN 清理；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash VpnService native Error 清理边界静态审计（2026-09-08）

- 固定 `VpnService.handleStart()` 在 `Builder.establish()` 成功后立即调用 `detachFd()`，再在同一 service 进程中设 `tunRunning=true` 并调用 `Core.startTun()`。如 `establish()` 返回空，代码抛出 `Exception`，外层 `start()` 会调用 `stop()`；这一已知分支有显式清理尝试。
- 但 `Core.startTun()` 周围和 `start()` 外层都只捕获 `Exception`。`UnsatisfiedLinkError`、`ExceptionInInitializerError` 或其他 `Error` 在 descriptor 已脱离 Java 管理后会绕过这两个 service 内 catch。`ServiceController.useService()` 的 `runCatching` 能把异常感知为失败并尝试 `stopIfConnected()`，但补偿 `cleanup()` 仍需调用同一 `Core.stopTun()`，没有稳定 `EngineCrashed`/`Unavailable` 映射、独立 descriptor 清理，或可等待的 TUN 已消失回执。
- 本审计不声称上述 Error 已发生、descriptor 已泄漏或系统 TUN 必然遗留：固定源码不足以证明 native fd 所有权和系统最终回收。它只确认错误类别、清理路径与回执之间不存在可验证的隔离合同。未修改/下载上游源码、SDK、缓存或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须在建立 VPN 前完成 native manifest/health 验证，并以 `Throwable` 安全边界、明确 fd/TUN 所有权和实际 TUN 清理回执映射加载/符号/运行失败；随后用受控 native error、缺失 artifact、version mismatch、cancel 和 success fixture 做隔离/真机契约测试。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash QuickAction 已附着 engine 的无确认投递边界静态审计（2026-09-08）

- 固定 `QuickActionActivity` 对 START/STOP/TOGGLE 均在 `GlobalState` coroutine 中委托 `ServiceState` 后立即 `finish()`。当 `TilePlugin` 已由 `MainActivity.configureFlutterEngine()` 附着时，`ServiceState.handleStartAction()`/`handleStopAction()` 不会走 native preference/core/service 路径，而只调用 `TilePlugin.handleStart()`/`handleStop()`。
- `TilePlugin` 将这两个调用实现为单向 `MethodChannel.invokeMethodOnMainThread("start"/"stop")`：没有 MethodChannel result、Dart acknowledgement、内存/持久队列、重试或 engine detach 后重放。`onDetachedFromEngine()` 只清理 native method-call handler，`ServiceState.detachFlutterEngine()` 也只清空 engine 引用。因此在 Flutter/Dart listener 未就绪、Activity/engine 销毁或投递竞争时，固定 Android 层没有可验证的“已交付、已取消或回退 native start/stop”合同。
- 本轮仅只读固定公开 `QuickActionActivity.kt`、`TilePlugin.kt`、`MainActivity.kt` 与 `ServiceState.kt`；此前的正常允许状态设备基线经 ADB 复核为强停后 `tun0=0`、进程计数为 0、`POST_NOTIFICATION=allow`。未启动 VPN、未写入上游源码/SDK/缓存/产物，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须将快捷入口命令持久化或交给可 await 的串行 dispatcher，并以 delivery acknowledgement/timeout 确认 Flutter/engine path；未交付时只能明确 `Cancelled`/`Unavailable` 或安全 native fallback，不能隐式丢弃。还需真机覆盖 listener 未就绪、engine detach/recreate、START/STOP racing 与实际 TUN/health 回执。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash QuickAction 异步异常完成边界静态审计（2026-09-08）

- 固定 `GlobalState` 委托给 `CoroutineScope(SupervisorJob() + Dispatchers.Default)`，没有由 Activity、QuickAction request 或 service lifecycle 管理的 parent job。`QuickActionActivity.onCreate()` 用该 scope 的 `launch` 分派 START/STOP/TOGGLE，然后立即 `finish()`，未保留 `Job`、Deferred 或可观测 completion。
- `SupervisorJob` 可避免某一 child failure 自动取消同级任务，但不会把 child exception 变为 QuickAction 调用方可识别的稳定结果。固定 activity 也没有 completion callback、超时、一次性取消记录或异常类别映射；因此 action 中的 setup/service/native 失败不能由这一入口本身可靠区分为 `Cancelled`、`Unavailable` 或 `EngineCrashed`。该结果不推断特定异常已在设备发生。
- 本轮只读固定公开 `GlobalState.kt` 并关联此前已审计 `QuickActionActivity.kt`/`ServiceState.kt`；未使用 ADB、未启动 VPN、未写入上游源码/SDK/缓存/构建产物，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须令每个外部/快捷入口命令经受监督且可取消的 transaction 执行，返回一次性、脱敏的稳定结果并以 timeout/health/TUN 回执关闭；还需验证 setup、permission、native failure、engine detach 与相反命令 racing。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android 13 通知权限人工“允许”与实际授权不一致真机验证（2026-09-08）

- 小米 10S（`bf353dda`，API 33）停止基线中，以正常 `pm revoke` 临时撤销 dev 变体 `POST_NOTIFICATIONS`，随后强停、从 launcher 打开主界面。主机仅确认初始 `tun0=0`、进程计数为 0、UID 层 `POST_NOTIFICATION=ignore`；未读取屏幕、通知或配置。
- 用户在设备主界面启动控制后报告选择“允许”。紧接着与 7 秒后的只读核验均为 `tun0=0`、dev 进程计数为 1、`dumpsys package` 当前 runtime permission `granted=false`、UID app-op `POST_NOTIFICATION=ignore`。因此不把用户操作意图、可能的系统窗口或上游 callback 视为实际授权或 VPN 启动成功。
- 随后以正常 `pm grant` 恢复通知权限，并发送精确 dev STOP action；最终只读基线为 `tun0=0`、进程计数为 1、runtime permission `granted=true`、`POST_NOTIFICATION=allow`。全程未发送流量，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- 由于本轮不读取任何窗口内容，不能将授权状态不变归因于某一特定系统 dialog、用户界面控件或 FlClash callback 分支；它只证明该入口缺少将用户操作、实际 grant/app-op、启动 transaction 与 TUN/health 关联的可验证回执。XToolpro future `engine-proxy` 必须在每个 permission transaction 后重新读取实际授权状态，并以 TUN/health 收敛 `Success` 或脱敏 `Unavailable`/`Cancelled`，不得以点击结果、进程存活或入口返回替代。完成 deny/revoke/retry/recreate/visibility 契约前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash dev 获准通知权限原生 START 回归边界（2026-09-08）

- 小米 10S（`bf353dda`，API 33）先强停 dev 变体，确认 `tun0=0`、进程计数为 0、`POST_NOTIFICATION=allow`。随后直接发送已解析的 `com.follow.clash.dev.action.START`，第 7 秒和第 14 秒仅确认 `tun0=0`、dev 进程存在，app-op 仍为 allow。
- 随后发送同一精确 STOP action，5 秒后仍为 `tun0=0`、dev 进程存在。全程未发送流量，未读取系统 VPN、通知正文或 extras、日志、配置、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- 本轮与此前获准态可形成 TUN 的正向 proof 不一致；由于不读取 core、系统 VPN 或日志内容，不能判定是配置、native health、服务时序或设备状态差异。该结果只证明获准通知权限并不足以构成可复现的 TUN 启动回执，原生 START/STOP 行保持 `Partial`，Proxy 台账保持 `Investigating`。
- XToolpro future `engine-proxy` 必须把 permission allow、service start、native health 和实际 TUN 建立分成可等待、可诊断的阶段，并在任一阶段失败时返回稳定脱敏结果；不得以 permission allow、进程存在或系统入口返回成功替代 TUN/health 回执。在完成重复 success、unavailable、cancel、crash、version-mismatch 和设备重测前，不进入正式 engine 集成。

### FlClash dev 已附着主界面原生 START 可重复性边界（2026-09-08）

- 小米 10S（`bf353dda`，API 33）通知权限保持 allow。强停后先确认 `tun0=0`、进程计数为 0；从 launcher 请求启动主界面并等待 8 秒，仅确认进程存在、TUN 仍不存在。随后发送精确 dev START action，第 7 秒仅确认 `tun0=0`、进程存在、`POST_NOTIFICATION=allow`；第 14 秒同样为无 TUN、进程存在。
- 该轮初始长命令在最后观察窗输出不完整，故未用其推断 STOP 结果；后续单独只读复核仍为无 TUN、进程存在、allow，随后显式发送 STOP 并在 5 秒后再确认 `tun0=0`、进程存在、allow。全程未发送流量，未读取系统 VPN、通知正文或 extras、日志、配置、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- 此结果与此前“主界面已启动后同一 START 形成 TUN”的正向观察不一致。由于本轮没有读取 Flutter plugin、Dart listener、core、系统 VPN 或日志，不能将差异归因于 engine 附着、listener、配置、native health 或时序；只能确认该入口在许可态并不提供可重复 TUN 回执，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`。
- XToolpro future `engine-proxy` 必须让快捷/主界面入口返回同一可 await 的 command result，并以独立 TUN/engine health 回执判定 success；重复运行得到相反 TUN 结果时应暴露脱敏 unavailable/failed 状态，不能以 process、permission 或入口返回成功掩盖。完成重复成功/失败、listener-ready/detach、cancel、crash、version-mismatch 设备契约前，不进入正式 engine 集成。

### XToolpro `engine-proxy` Phase 02 契约执行门禁静态审计（2026-09-08）

- 受版本控制文件盘点显示，`engine-proxy` 当前只有 `build.gradle.kts` 和空的 `src/main/AndroidManifest.xml`；前者仅声明 Android library/Kotlin plugin 及对 `:core-model` 的依赖。该模块没有 Kotlin/Java 源文件、FlClash dependency、native library、公开 engine API、capability/health/version 模型或测试源。
- `core-model` 当前仅定义通用 `ModuleId.Proxy`；检索不到 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed` 或 `VersionMismatch` 的 Proxy engine 结果实现。`docs/architecture/engine-contract-test-plan.md` 虽已为 FlClash 固定五类场景和断言，但其执行门禁明确要求先完成上游真实 proof，之后才在相应 `engine-*` 中实现；测试计划不能替代可执行 adapter 契约证据。
- 本轮仅检查 XToolpro 受版本控制的 module 文件和 Phase 02 测试计划，没有修改 engine、SDK、缓存、上游源码归档、构建产物或设备状态。该结果符合当前“暂不进入正式 engine 集成”范围，但意味着 XToolpro 自身尚未能运行五类 Proxy engine 契约测试。
- 后续仅在 Phase 02 gate 准许的前提下，才可先实现受签名 native manifest 核验、版本化公开 contract、最小化 capability/health 和稳定脱敏结果映射，再用完整、缺失/不可用、取消、受控崩溃和版本错配的隔离 test fixture 执行五类测试。在此之前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android native core/bridge 版本不匹配边界静态审计（2026-09-08）

- 在固定 FlClash `62addf738a76b1a492e19af2dbabdb6d572b9e72` 的隔离源码归档中，`android/core/src/main/cpp/CMakeLists.txt` 只有在当前 `${ANDROID_ABI}` 同时存在 `jniLibs/<ABI>/libclash.so`、`cpp/includes/<ABI>/libclash.h` 与 `bride.h` 时才定义 `LIBCLASH`、加入 include/link 路径并链接 `clash`；缺少任一输入时仍构建 `core`，但不链接 `libclash.so`。
- 对应 `android/core/src/main/cpp/core.cpp` 的 `#else` 保留 Kotlin 所需 JNI 符号，却将 `startTun`、`stopTun`、`quickSetup`、`invokeMethod`、事件监听、DNS、暂停和 GC 实现为空操作，并让两种流量查询返回 `{}`。`android/core/.../Core.kt` 仅执行 `System.loadLibrary("core")`，其公开 JNI 表面和生成的 `libclash.h` 均没有 FlClash/Clash.Meta commit、engine API、ABI 或 artifact hash 查询/校验。
- 归档当前只含 `arm64-v8a` 的 `libclash.so` 与两份生成头文件；这只说明该归档的一个 ABI 输入完整，不能证明其他 ABI、bridge/core 同步或设备加载路径。因 fallback 与完整路径的 Kotlin/JNI 名称相同，构建成功或 `System.loadLibrary` 成功不能作为锁定完整 core 的证明，也不能将错配自动解释为可恢复的 `VersionMismatch`。
- 本轮仅读取隔离归档，未修改 SDK、缓存、上游源码归档或构建产物。设备端仅作允许范围内的停止基线读取：`tun0=0`、`com.follow.clash.dev` 进程计数为 `1`；未读取日志、配置、通知、节点、请求、数据库或其他敏感内容，亦未使用 ADB 发出 action 或改变任何设备设置。
- XToolpro future `engine-proxy` 必须在任何配置写入、导出或 VPN 请求前，基于受签名 manifest 核验 FlClash/Clash.Meta commit、engine API、ABI、bridge 和 `libclash.so` SHA-256，以及必需符号/资源集合；缺失、ABI 不符或不一致时必须返回脱敏 `Unavailable`/`VersionMismatch` 并阻止启动。仍需在隔离环境以完整 core 与刻意缺失/错配 artifact 运行 success、unavailable、version-mismatch、cancel、crash 五类契约测试；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android native load 与 fallback 错误映射静态审计（2026-09-08）

- 固定 `Core.kt` 在对象初始化时直接 `System.loadLibrary("core")`。对固定 `android/core`、`android/app` 与 `android/service` 源码的针对性检索未发现 `UnsatisfiedLinkError`、`ExceptionInInitializerError` 或 `LinkageError` 的显式处理；`ServiceController` 的 `runCatching` 只能捕获 `Exception`，`VpnService` 调用 `Core.startTun()` 时也只捕获 `Exception`，因此 native 加载/链接类 `Error` 没有被明确映射为脱敏 `Unavailable` 或 `EngineCrashed`。
- 这不是唯一故障模式：前一项已确认的 CMake fallback 在 core 输入不完整时仍提供可加载的 `libcore.so` 与同名 JNI 符号，因而绕过链接错误。`VpnService` 可在 `Core.startTun()` 空返回后保留已建立的 TUN；`ServiceController.quickSetup()` 等待 native callback，而 fallback `quickSetup()` 不调用 callback，故可能无限等待，固定代码未为该 callback 建立 timeout/版本不匹配映射。
- 本轮仅静态读取隔离归档；未触发缺库、错 ABI 或崩溃，未修改 SDK、缓存、上游源码归档或构建产物，也未使用 ADB。该审计不声称任何特定设备已发生上述情况。
- XToolpro future `engine-proxy` 必须先验证受签名 native manifest，再以限定 timeout 包装初始化/回调和健康握手；将 hash/ABI/API/commit 不一致映射为 `VersionMismatch`，缺失组件映射为 `Unavailable`，加载、符号或运行健康失败映射为 `EngineCrashed`，在任何配置写入、导出或 VPN 请求前拒绝执行。仍需隔离的完整/缺失/错配 artifact 契约测试和真机健康回执；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 冷启动 core setup 等待期间 STOP/detach 取消边界静态审计（2026-09-08）

- 固定 `ServiceState.handleStartAction()` 在没有 `TilePlugin` 的冷启动路径直接调用 `loadPreferencesAndStart()`。该函数先读取已有 shared state，随后 await `setupCore()`；只有后者成功返回后才创建 `requestStart()` 的 running request。因此 setup 等待窗口内 `latestRequest` 仍是默认 `running=false`。
- `setupCore()` 委托 `ServiceController.quickSetup()`；后者的 `suspendCancellableCoroutine` 仅注册 native callback，没有 timeout 或 `invokeOnCancellation`。在该窗口调用 `handleStopAction()` 时，`isRunningRequested()` 为假而直接返回，不会取消或关联正在等待的 `quickSetup` coroutine。`ServiceState.detachFlutterEngine()` 同样只清空 engine 引用；`QuickActionActivity` 所用 `GlobalState` scope 不由 Activity lifecycle 取消。故原生 fallback 的 STOP 或 detach 对 core-setup in-flight work 没有有界的 `Cancelled` completion 合同。
- 本轮仅只读固定 FlClash `62addf738a76b1a492e19af2dbabdb6d572b9e72` 隔离归档中的 `ServiceState.kt` 与 `ServiceController.kt`；未修改 SDK、缓存、上游源码归档或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。该静态结论不声称目标设备已发生挂起或在任何具体时序中留下 TUN。
- XToolpro future `engine-proxy` 必须以一个受监督、可取消且有 deadline 的启动 transaction 覆盖 config/core setup、permission、VPN establish 与 health；STOP/detach 必须取消所有 in-flight 阶段，并只在实际清理/稳定脱敏 `Cancelled` 结果完成后结束。仍需在隔离 fixture 和真机上验证 setup callback timeout、STOP、detach/recreate、success、unavailable、crash 与 version mismatch；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash core setup callback 所有权与取消释放边界静态审计（2026-09-08）

- 固定 JNI `Core_quickSetup` 将 Kotlin callback 提升为 global reference 后交给 Go `quickSetup`；后者在独立 goroutine 中以 `defer releaseObject(callback)` 延迟释放。`releaseObject` 通过 bridge 的 `release_object` 回到 JNI，因此 callback reference 的释放取决于该 goroutine 返回，而不是 Kotlin continuation 的取消、QuickAction Activity 结束、STOP 或 Flutter engine detach。
- 对固定 Android bridge、生成 header 与 core export 的针对性检索未发现 `cancelQuickSetup`、setup cancellation token 或提前释放该 callback reference 的公开入口。结合 Kotlin wrapper 未注册 `invokeOnCancellation`，上层即使取得 cancellation 也无法向 native setup 传达一次性取消/释放请求；这不是对实际泄漏的断言，只确认 native in-flight setup 的 callback 所有权与有界释放没有可验证合同。
- 本轮只读固定 FlClash `62addf738a76b1a492e19af2dbabdb6d572b9e72` 隔离归档中的 `core.cpp`、`jni_helper.h`、`bride.go` 与 `lib.go`；未修改 SDK、缓存、上游源码归档或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。
- XToolpro future `engine-proxy` 必须将每个 native callback 绑定到可取消 transaction，定义 native acknowledgement、deadline 后的确定性清理及一次性释放所有权；STOP/detach、timeout、crash、success 与 version mismatch 都必须给出稳定、脱敏的 terminal result。仍需隔离 fixture 与真机验证释放、无 TUN 残留及无重复 callback；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android ABI 构建面与逐 ABI 验证边界静态审计（2026-09-08）

- 固定 `plugins/setup/buildkit/build_tool` 的 `Target` 定义三个 Android c-shared 目标：`armeabi-v7a`、`arm64-v8a`、`x86_64`；未传 `--arch` 或 `--target-platform` 时会选中全部三个。`plugins/setup/buildkit/gradle/plugin.gradle` 将 Go core task 设为 `:core` 的 CMake configure、external native build 与 merge native libs 的前置依赖，说明这是原生桥接的构建输入链，而不是 Flutter APK split 的单独展示设置。
- `setup.dart` 同时启用 Android `split-per-abi`，并允许请求单一 arm/arm64/amd64 目标。这表示发布策略可减少单个 APK 的 ABI 范围，但不允许把一个 ABI 的 `libclash.so`、生成头、bridge 或健康结果外推为其他已发布 ABI 已验证。
- 对既有固定 dev APK 的只读解包目录复核，仅列出 `lib/arm64-v8a`，其中 `libclash.so` SHA-256 为 `859D6BA4E32FE719D417410811D31176E2E18297A26B3D67200A6049ED9EE29F`，`libcore.so` 为 `D3520A46D3A8DA72306A1B18F4415B1AAA588FE0C3C6EB5F470D7901F17663FC`，均与此前 APK proof 一致。`readelf` 仅确认二者为 AArch64；`libclash.so` 具有 `quickSetup`、`startTUN`、`stopTun`、`setEventListener`、`invokeMethod` 等 Go export，`libcore.so` 具有对应 Kotlin JNI symbols。这只是该归档 arm64 pair 的存在性/符号面，不含 commit、engine API 或运行健康证明。
- 同一只读 ELF dynamic table 显示 `libcore.so` 有 `NEEDED libclash.so`，并把 `quickSetup`、`startTUN`、`stopTun`、`setEventListener`、`invokeMethod`、`result_func` 与 `release_object_func` 保留为 unresolved imports；故该已解包 arm64 pair 的 bridge 链接关系存在，不是两份无关联文件的并列。该检查不执行加载、不会验证动态 linker 实际解析，也不核对 commit、API、header/hash manifest 或健康握手。
- 本地固定归档仍只观察到 `arm64-v8a` 的 `libclash.so`、`libclash.h` 与 `bride.h`；本轮未构建、修改、复制或删除任何上游/SDK/缓存/产物。因而只能确定上游源码声明的三 ABI 构建面，不能声称另外两种 ABI 的实际产物、符号集、hash、安装或设备行为已通过。
- XToolpro future `engine-proxy` 的受签名 native manifest、artifact hash、符号/健康握手、版本不匹配拒绝和回滚均必须逐个已发布 ABI 执行；未纳入发布的 ABI 应明确 `Unavailable`，不得回落到空 JNI bridge。仍需在各支持 ABI 的隔离构建和目标设备上运行完整/缺失/错配 artifact 的五类契约测试；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android ABI 构建声明与发布投递边界静态审计（2026-09-08）

- 固定 `.github/workflows/build.yaml` 的唯一 Android matrix job 调用未带 `--arch` 的 `dart setup.dart android`；`setup.dart` 会启用 Flutter `split-per-abi`，而固定 buildkit 的默认 Android target 解析为 `armeabi-v7a`、`arm64-v8a`、`x86_64`。因此 CI 源码声明的是三 ABI 构建输入，而不是单一 arm64 构建。
- 同一固定 workflow 的 F-Droid 后处理只复制名称匹配 `*android-arm64-v8a*` 的文件；固定 `release_telegram.py` 的 release 附件关键字同样只列出 `android-arm64`。这两个筛选点仅描述下游投递选择：未读取任一 release、artifact 或外部服务，不能据此断言其他 ABI 是否生成、上传、发布或缺失，更不能把它们外推为逐 ABI native 配对已经通过。
- 固定归档及既有 proof 仍只有 arm64 的 `libclash.so`、头文件、bridge pair/hash/symbol 观察；CI 的默认目标和投递文件名均不携带 FlClash/Clash.Meta commit、engine API、bridge revision、逐 artifact hash、符号集或健康结果。它们不能填补每个已发布 ABI 的受签名 manifest 缺口，也不能防止空 JNI fallback 被误判为完整 core。
- 本轮只读固定上游归档的 workflow、setup 和 build-tool 源文件；未触发构建或发布，未修改 SDK、缓存、上游源码归档或构建产物，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。XToolpro future `engine-proxy` 必须把支持 ABI 明确列入受签名 manifest，并逐 ABI 验证完整/缺失/错配 artifact 的 hash、bridge、commit/API、健康与五类结果；未发布 ABI 必须稳定返回 `Unavailable`。完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android native provenance 缓存与运行时版本接口静态审计（2026-09-08）

- 固定 buildkit 的 `FingerprintBuilder` 将源码和构建工具输入的字节、target、Go/NDK/编译器环境及 build arguments 计算为 SHA-256；`GoBuilder` 为 Android core 使用 c-shared，并固定传入 `-ldflags=-w -s`。这能驱动本地 cache 失效，但不向 Go binary 注入可查询的 FlClash/Clash.Meta commit、engine API 或 bridge revision。
- 固定 `BuildCache` record 对实际输出仅保存相对路径、大小和修改时间；其 fingerprint 与 record 是本地 cache 工作资料，未见其被打入 APK、由 `Core.kt`/JNI 导出或在安装时重算。固定 Go export 仅有 setup、TUN、event、traffic、suspend、GC 与 DNS 表面；`Core.kt` 同样没有 version/commit/hash 查询。因此不能以曾经匹配 cache 的构建输出、`System.loadLibrary` 成功或 JNI 符号存在，证明设备加载的 core 与固定源码、headers 或 bridge 完整配对。
- 本轮只读固定上游归档的 build-tool、core export 与 Kotlin bridge；未运行构建、未读取或写入 cache record，未修改 SDK、缓存、上游源码归档或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。XToolpro future `engine-proxy` 必须把受签名且随交付物保存的 provenance manifest 与 runtime handshake 分开实现：前者逐 ABI 绑定 artifact hash、FlClash/Clash.Meta commit 和 bridge/API，后者在启动前验证并以脱敏 `Unavailable`/`VersionMismatch`/`EngineCrashed` 收敛。完整/缺失/错配 artifact 五类隔离契约仍未执行；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash 已获通知授权的人工启动 TUN 回执真机复核（2026-09-08）

- 小米 10S（`bf353dda`，API 33）上，用户报告从 FlClash dev 主界面点击启动。主机未读取屏幕、通知、配置、日志或任何网络内容；只在用户操作后立即及 7 秒后读取允许范围内的状态。
- 两次结果均为 `tun0=0`、`com.follow.clash.dev` 进程计数为 1、`POST_NOTIFICATIONS granted=true`、UID `POST_NOTIFICATION=allow`。因此该轮的用户操作与通知授权都不能代替实际 TUN/health 的成功回执；也不能将 TUN 未出现归因于任何具体 UI、permission callback、VPN consent 或 native core 分支。
- 本轮未发送流量、未改变权限或 VPN 设置，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。XToolpro future `engine-proxy` 必须把用户命令、权限、VPN establish、TUN 存在和 core health 作为可归因的独立阶段，只有 health/TUN 回执才能产生 `Success`；否则在有界 deadline 后产生脱敏 `Unavailable`/`Cancelled`/`EngineCrashed`。完成完整真机与隔离五类契约前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android native linkage failure 结果映射静态审计（2026-09-08）

- 固定 `Core.kt` 在 object 初始化时直接 `System.loadLibrary("core")`，没有本地 `try/catch` 或可查询的 load/version result。`VpnService.handleStart()` 在 `Core.startTun()` 周围只捕获 `Exception`，`VpnService.start()` 也只捕获 `Exception`；因此 `UnsatisfiedLinkError`、`ExceptionInInitializerError` 或其他 `LinkageError` 不会在 service 边界变成受控 native error。
- 上述错误可继续到 `ManagedServiceBinding.useService()` 的 `runCatching`，使 `ServiceController.start()` 记录原始异常字符串、清理 binding 并返回 `0L`；`ServiceState.start()` 仅据该数值设置 `STOPPED` 并返回 `false`。同时固定 `ServicePlugin.start()` 在提交 `ServiceState.requestStart()` 后立即 MethodChannel `success(true)`，不等待这个结果。故这条路径只可能形成异步泛化失败，无法区分缺失 artifact、ABI/bridge/version mismatch 与运行期 core crash，也没有稳定、脱敏的外部 terminal result。
- 本轮只读固定上游归档的 Kotlin bridge、service/controller/state 源文件；未执行或诱发 native 失败，未修改 SDK、缓存、上游源码归档或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。XToolpro future `engine-proxy` 必须在加载前完成受签名 manifest 核验；在 throwable 边界把缺失映射为 `Unavailable`、不一致映射为 `VersionMismatch`、加载/运行异常映射为 `EngineCrashed`，并使调用方等待一次性、脱敏的 terminal result。完整/缺失/错配 artifact 的隔离五类契约仍未执行；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### FlClash Android 启动健康回执表面静态审计（2026-09-08）

- 固定 `VpnService.handleStart()` 在 `Builder.establish()?.detachFd()` 后、调用 `Core.startTun()` 前就把私有 `tunRunning` 设为 true；Kotlin `Core.startTun` 是 `Unit`，虽然 Go export 返回 `true`，JNI 调用方没有接收该值。`ServiceController` 只以 service 调用完成和 `runTimeMillis` 记录开始时间，不读取 TUN、listener 或 core health。
- 固定 Android core 表面没有 health/readiness/TUN-established 方法。现有 `getTraffic`/`getTotalTraffic` 被 UI 和通知用于流量展示；原生 fallback 保留相同 JNI 方法并返回 `{}`，故其可调用性或空流量对象不能证明完整 `libclash`、listener、TUN 或可转发性。该审计不读取设备流量或通知内容。
- 本轮只读固定上游归档的 Android/Go/Dart bridge 源文件；未执行 core、未修改 SDK、缓存、上游源码归档或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。XToolpro future `engine-proxy` 必须增加独立、最小化且无敏感字段的 health handshake，并将 system establish、TUN 存在和 engine readiness 作为不同阶段；只有这些受限回执可形成 `Success`。其余路径必须在 deadline 后稳定收敛为脱敏 terminal result。完整/缺失/错配 artifact 五类隔离契约仍未执行；完成前矩阵保持 `Partial`，Proxy 台账保持 `Investigating`，不进入正式 engine 集成。

### Phase 02 acceptance gate 只读缺口汇总（2026-09-08）

- 依据 active spec 的验收条件，对 `upstream-capability-parity-matrix.md` 与 `upstream-reuse-ledger.md` 做只读统计：矩阵当前 `Verified=4`、`Partial=51`、`Pending=32`、`Unavailable=1`、`Blocked=0`；Proxy、Cleaner、Media、Image 四行台账均为 `Investigating`，没有 `Approved` 或 `Blocked` 域行。因此尚未达到“每个 PRO/CLN/MED/IMG 家族映射到 approved 或 explicitly blocked ledger row”以及“每个 engine 完成 capability-parity matrix”的 acceptance 条件。
- `engine-contract-test-plan.md` 已为 Proxy、Cleaner、Media、Image 分别列出 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch` 的期望场景，但当前 `engine-proxy` 没有 adapter/公开 contract/health-version handshake 或测试实现；该计划本身也明确矩阵未完成时不能以少量成功场景宣称完整复用。它是未来执行标准，不是已满足的 contract evidence。
- 未完成的直接 gate 工作包括：完成四域完整 capability matrix，完成每个已发布 ABI 的受签名 artifact/bridge/commit manifest 及完整/缺失/错配隔离验证，落实并执行五类 engine contract，完成 GPL/SBOM/NOTICE/传递依赖与 app-store/privacy 审查；FlClash 还缺真实 permission/consent/recreate/取消、健康和 TUN 回执契约。保持所有矩阵/台账既有 `Partial`、`Pending`、`Investigating` 状态，暂不进入正式 engine 集成。
- 本轮只读项目规格、矩阵、台账和测试计划；未修改 SDK、缓存、上游源码归档或构建产物，未使用 ADB，未读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie 或订阅 URL。

### 已授权启动窗口的系统 VPN 标记与 TUN 分离复核（2026-09-08）

- 在上一轮用户手动启动后的稳定窗口，仅读取系统连接状态中的 VPN 连接标记计数、`tun0` 行数、目标进程计数与通知授权/app-op；结果为 `vpn_connected_markers=1`、`tun0=0`、dev 进程 `1`、`POST_NOTIFICATIONS granted=true`、UID `POST_NOTIFICATION=allow`。
- 该组合说明系统层 VPN 标记可能存在而实际 TUN 接口不存在；本轮未读取连接详情、通知、日志、配置、节点、请求或数据库，不能据此归因具体 service/core 分支。它只强化了“系统 VPN 标记、进程存活、权限允许均不是 engine readiness 的充分回执”的边界。
- XToolpro future `engine-proxy` 必须分别记录系统 VPN consent/establish、TUN 存在、native health/readiness 和可转发性，并在这些阶段不一致时阻止 `Success`，返回稳定、脱敏的 terminal result。完成对应真机与隔离契约前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`。

### 已授权启动窗口的全 TUN 类型接口复核（2026-09-08）

- 为排除只检查固定名称 `tun0` 的歧义，本轮仅统计 `ip` 所报告的 TUN 类型接口总数，并重取此前允许的系统 VPN 连接标记、`tun0` 存在性和目标进程计数；结果为 `all_tun_interfaces=0`、`tun0=0`、`vpn_connected_markers=1`、dev 进程 `1`。
- 未输出任何接口地址、路由、DNS、连接详情、配置或流量内容。该结果只能说明当前没有任何内核报告的 TUN 类型接口，不能归因 FlClash 的具体 service/core/UI 分支；它排除了单纯“接口不是 tun0”的解释，但不替代 core health 或可转发性验证。
- XToolpro future `engine-proxy` 仍必须将系统 VPN 状态与 TUN/engine health 分离，并以最小化、脱敏、可测试的 health/TUN 回执决定 `Success`。完成对应五类契约前，矩阵保持 `Partial`，Proxy 台账保持 `Investigating`。

### 已授权 STOP action 的系统 VPN 标记收敛复核（2026-09-08）

- 根据用户明确授权的受限 ADB action 规则，对已解析 dev component `com.follow.clash.dev/com.follow.clash.QuickActionActivity` 发送 `com.follow.clash.dev.action.STOP`。操作前只读汇总为 `vpn_connected_markers=1`、`all_tun_interfaces=0`、dev 进程 `1`、`POST_NOTIFICATIONS granted=true`、UID `POST_NOTIFICATION=allow`；操作后等待 7 秒，汇总为 `vpn_connected_markers=0`、`all_tun_interfaces=0`、dev 进程 `1`，permission/app-op 不变。
- STOP 与系统 VPN 标记从 1 收敛到 0 相符，但操作前已没有任何 TUN 类型接口，故不能把这次结果表述为实际 TUN 清理成功，也不能归因于某一 UI、permission、service 或 native core 分支。它进一步说明系统 VPN 标记、进程存活、权限允许和 TUN/engine health 是彼此独立的回执层级。
- 本轮没有读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie、订阅 URL、地址、路由、DNS 或流量内容；没有修改配置、生成流量或进入 engine 集成。future `engine-proxy` 必须只在可归因的 TUN 与独立健康回执均满足时产生 `Success`，其余情况在有界 deadline 内返回稳定、脱敏的 terminal result。矩阵保持 `Partial`，Proxy 台账保持 `Investigating`。

### 已授权 START→STOP action 的 TUN 配对复核（2026-09-08）

- 在同一小米 10S（`bf353dda`，API 33）停止基线下，受限汇总为 `vpn_connected_markers=0`、`tun0=0`、dev 进程 `1`、UID `POST_NOTIFICATION=allow`。向已解析 dev component 发送 `com.follow.clash.dev.action.START` 后等待 7 秒，汇总为 `vpn_connected_markers=1`、`tun0=1`、dev 进程仍为 `1`、app-op 不变；随即发送同一 component 的 STOP action，7 秒后为 `vpn_connected_markers=0`、`tun0=0`、dev 进程仍为 `1`、app-op 不变。
- 为避免将受限读操作失败误记成阴性结果，曾尝试的 TUN 类型总数查询返回权限错误，未纳入本轮结果；本证据只依赖无内容的 `tun0` 存在性检查。该配对支持“本次正确 action 可建立并清理 TUN”的有限结论，但不能以 VPN 标记、进程或 notification app-op 替代 native health/readiness，也不能证明 core health、可转发性、通知可见性或实际 permission callback。
- 本轮没有读取日志、配置、通知正文或 extras、节点、请求、数据库、文件、凭据、Cookie、订阅 URL、地址、路由、DNS 或流量内容；没有修改配置、生成流量或进入 engine 集成。future `engine-proxy` 仍须以可归因的 TUN 与独立、脱敏 health handshake 共同决定 `Success`，并覆盖五类 terminal result；矩阵保持 `Partial`，Proxy 台账保持 `Investigating`。

### ytdlnis Cookie/session 本地存储与导出边界静态审计（2026-09-08）

- 固定 ytdlnis `13320bb64f35c8d04f01bebfa782d7947758fb66` 的 `CookieItem` 是含 `url`、`content`、`description`、enabled 的普通 Room entity；`DBManager` 以 `Room.databaseBuilder(..., "YTDLnisDatabase")` 创建数据库。对 app 源码和 Gradle 的针对性检索未发现 SQLCipher、EncryptedSharedPreferences 或 Keystore 对 Cookie 数据库的保护。
- `CookieViewModel.updateCookiesFile()` 将所有已启用 Cookie 组合为 Netscape 文本并写入内部 `cacheDir/cookies.txt`；`YTDLPUtil` 和 terminal 会话在 `use_cookies` 开启时将该路径作为 yt-dlp `--cookies` 参数。该 ViewModel 还可以读取该文件至系统剪贴板，或复制到导出目录；`BackupSettingsUtil.backupCookies()` 将实体序列化入 JSON 备份。用户确认删除全部 Cookie 时，固定 UI 会删除 Room 行并将当前 cache 文件写空，但源码不为先前的剪贴板、导出或备份副本提供撤销/安全擦除合同。
- 本轮仅只读固定隔离上游归档的模型、Room、Cookie、备份与调用路径；未使用、导入、读取或输出任何真实 Cookie/session、私有内容、URL、下载、日志、设备数据库或文件。该结论不能断言任一设备上实际文件权限或数据泄露。future `engine-media` 必须使用 Keystore 绑定的加密会话 vault，只在短生命周期内部文件生成最小 yt-dlp 输入，默认禁止原始 Cookie 进入日志、剪贴板、通用备份或导出；删除、撤销、恢复和授权 session 都需独立脱敏 contract。Media 矩阵相应项从 `Pending` 调整为 `Partial`，Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 终端与自定义命令边界静态审计（2026-09-08）

- 固定 ytdlnis 的 `MkSession` 创建交互式 shell，并生成 Python、FFmpeg、Node、Deno、Aria2c 与 yt-dlp 的 shell functions；启用 `use_cookies` 时，yt-dlp function 会附带内部 Cookie 文件路径的 `--cookies` 参数。`TerminalFragment` 将用户选中的 command-template 或 shortcut 内容直接写入 session；`CommandTemplate` 以普通 Room entity 持久化完整内容，且可经剪贴板导入/导出。固定路径没有 command allowlist、结构化参数模型或逐次风险确认。
- `TerminalItem` 与 `TerminalDao` 会在普通 Room 表中保存 command 和 terminal log；对该链路的针对性检索未见 URL、Cookie、令牌或输出的统一脱敏。因此通用交互 shell 既不是受限的 yt-dlp 参数接口，也不能在未隔离时安全继承用户会话边界。
- 本轮只读固定隔离上游归档的 terminal、template 与 Room 源码；没有执行命令、读取真实终端输出、Cookie、URL、下载、日志、设备数据库或文件。该结论不声称任一命令可执行或曾发生泄露。future `engine-media` 如确需补充命令能力，必须采用独立受限环境、逐次明确授权、结构化允许参数、最小 SAF scope、可取消进程与默认脱敏输出；不得直接暴露上游通用终端或未审计模板。Media 矩阵相应项从 `Pending` 调整为 `Partial`，Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ImageToolbox 背景擦除模型完整性与失败边界静态审计（2026-09-08）

- 固定 ImageToolbox `cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0` 的 `feature:erase-background` 依赖 `lib:neural-tools`；后者使用 ONNX Runtime 与 `aire`。`BgRemover` 枚举 RMBG1_4、InSPyReNet、U2NetP/U2Net、BiRefNet/BiRefNetTiny、MODNet、ISNet 与 YOLO。U2NetP 随 `lib:neural-tools/src/main/assets/u2netp.onnx` 交付并提取到内部目录；其余 remover 的 model path 由 `HF_BASE_URL`（Hugging Face `T8RIN/imagetoolbox-models`）与模型文件名构成，并经 `NeuralTool` 注入的 downloader 下载至 `context.filesDir/ai_models`。
- `AndroidDownloadManager.download()` 使用同目录临时文件、下载完成后 `Os.rename` 原子替换目标，并在 failure/cancel 的 finally 路径删除临时文件。可是 `GenericBackgroundRemover.checkModel()` 只检查模型文件存在且长度大于零；下载后同样只调用该检查，未见绑定模型 ID、版本、预期长度或 SHA-256 的完整性校验。固定背景擦除路径中也未发现该模型集合的逐项许可证/provenance manifest。非空但错误或过期的模型会被视为已下载并直接交给 ONNX Runtime 创建 session。
- `BgRemover.removeBackground()` 以 `runCatching` 返回结果，而 feature 的 `AndroidAutoBackgroundRemover` 把任意 failure 交给通用 `makeLog()`/failure toast；没有稳定区分网络不可用、用户取消、文件/版本不匹配和 ONNX load/inference crash 的 engine terminal result。该审计未发起模型下载、未加载模型、未处理图像，且未修改 SDK、缓存、上游归档或构建产物。
- XToolpro future `engine-image` 必须锁定每个模型的来源、许可证、版本、大小与 SHA-256，下载至临时文件后校验再原子发布；以可取消、脱敏、有界的 contract 区分 `Unavailable`、`Cancelled`、`EngineCrashed` 与 `VersionMismatch`，并用真实模型完成五类场景。背景擦除矩阵项从 `Pending` 调整为 `Partial`；Image 台账保持 `Investigating`，不进入正式 engine 集成。

### ImageToolbox native/GPU/AI 运行时闭包静态审计（2026-09-08）

- 固定 ImageToolbox version catalog 声明 OpenCV `5.0.0.1`、ONNX Runtime Android `1.29.0`、`aire` `0.18.1` 与 ImageToolboxLibs `8.0.6`。`lib:opencv-tools` 导出 OpenCV；`lib:neural-tools` 导出 ONNX Runtime 并依赖 aire；多个 feature 依赖 OpenCV/GPUImage。ImageToolboxLibs 的固定版本坐标包含 GPUImage、GIF/APNG、JP2、QOI、awebp、PSD、DjVu、RAW、TIFF、GMIC、archive 与其他 codec/processor 模块，不能由根仓库 Apache-2.0 许可替代其各自许可证审查。
- `app/build.gradle.kts` 仅声明 `armeabi-v7a`、`arm64-v8a` 与 `x86_64`；非 app bundle 构建启用 split ABI 和 universal APK，JNI packaging 对 `libcoder.so` 使用 `pickFirst`，并保留 debug symbols。`market` flavor 另加入 ML Kit subject/selfie segmentation，`foss` flavor 不包含该依赖。固定归档本身没有 app `jniLibs` 目录，因此真实 native 库来自尚未解析的第三方 artifact。
- 本轮未解析或下载任何依赖，未构建 AAR/APK，未加载 native library 或处理图像；因此没有逐 ABI/variant 的库清单、SHA-256、符号/加载、内存、GPU fallback、设备兼容性、许可证/NOTICE/SBOM 或 rollback 证据。XToolpro future `engine-image` 必须为每个实际交付 artifact/ABI/flavor 锁定来源、许可证与 hash，并真实执行 GPU/CPU fallback、unsupported ABI、cancel、native crash 与 version-mismatch contract。矩阵 native/GPU/AI 项从 `Pending` 调整为 `Partial`；Image 台账保持 `Investigating`，不进入正式 engine 集成。

### ImageToolbox OCR 模型与导入边界静态审计（2026-09-08）

- 固定 `feature:recognize-text` 同时依赖 Tesseract4Android 与 `lib:neural-tools` 的 ONNX PaddleOCR。PaddleOCR 提供 CJK、Korean、Latin、EastSlavic、Thai、Greek、English、Cyrillic、Arabic、Devanagari、Tamil、Telugu 和 UniversalV6 等 13 个 bundle，从 Hugging Face `T8RIN/imagetoolbox-models` 的 `main` 分支下载 ZIP；Tesseract best/standard/fast 训练数据也由三个 `tessdata*` 仓库 `main` 分支的 `.traineddata` URL 获取。固定 URL 没有 immutable commit/tag 或哈希。
- Paddle 只通过 `det.onnx`、`rec.onnx`、`cls.onnx` 与 dictionary 文件的存在性决定模型可用，Tesseract 只检查目标 `.traineddata` 是否存在；两条下载路径都没有预期长度或 SHA-256 验证，也未在固定代码中发现逐模型许可证/provenance manifest。Paddle ZIP 解包前未做完整性验证；Tesseract 语言模型允许导出，并在导入 ZIP 时直接以 entry name 相对 `filesDir/tesseract` 创建输出，未先作 canonical-path containment 检查，故该边界不能接受未审计导入包。
- 缺失数据分别返回 `NoData`/`NoPaddleData`，其他识别异常作为带原始 throwable 的 `Error` 返回；批量文字输出会写入其 message，UI 也可显示通用 failure。该路径没有把 missing、cancel、损坏/版本错配、ONNX/Tesseract load 或运行异常收敛为脱敏稳定 terminal result。本轮只读隔离上游源码，未下载模型、导入语言包、处理图像或读取任何文本内容。
- future `engine-image` 必须固定每个 OCR bundle/训练数据的来源、许可证、版本、长度与 SHA-256，校验 archive 及其 canonical 解包目标，并默认避免将原始 OCR 文本或 throwable 写入诊断/批量输出。完成真实模型的 success、unavailable、cancel、crash、version-mismatch 和隐私输出契约前，OCR 矩阵项从 `Pending` 调整为 `Partial`；Image 台账保持 `Investigating`，不进入正式 engine 集成。

### ImageToolbox EXIF/metadata 输出与诊断边界静态审计（2026-09-08）

- 固定 `core:data`、`feature:edit-exif` 与 `feature:delete-exif` 支持读取、编辑、预设标签删除和全 metadata 清理；删除流程把原图复制到新 save/cache 目标，携带经 `clearAttributes()` 处理的 metadata，并以 `keepOriginalMetadata=false` 调用 file controller。该 controller 在无保留 metadata 的分支执行 `clearAllAttributes()`，可选仅复制日期标签；固定源码还含 metadata round-trip instrumented tests，但本轮未构建或运行，不能把测试源视为格式/SAF 行为已验证。
- `AndroidFileController` 在 save/move/read/copy metadata 链路将 URI、完整 initial/source/destination metadata 和异常以 `makeLog` 传出；OCR 工作流还可将识别文本写进 `MetadataTag.UserComment` 后调用 `writeMetadata`。这些位置、设备、注释或 OCR 文本字段在固定路径未见统一脱敏；本轮不读取任一图片、metadata、日志或输出文件，故不声称实际设备上发生泄露。固定源码同样没有本轮可用的输出 read-back、SAF 格式覆盖、cache/share 副本清理或取消/崩溃恢复实证。
- XToolpro future `engine-image` 必须默认隐藏并禁止记录敏感 metadata，采用临时输出、原子提交和格式化 read-back 验证，并分别对 SAF 不可写、用户取消、metadata/codec crash 与格式/版本不匹配给出脱敏稳定结果。完成真实设备与五类 contract 后才能提升状态；本项从 `Pending` 调整为 `Partial`，Image 台账保持 `Investigating`，不进入正式 engine 集成。

### sdmaid-se SAF 删除与恢复边界静态审计（2026-09-08）

- 固定 sdmaid-se `b9b01ee0af648fa6af25d388bb39bacde8d5b7a9` 的 `SAFGateway` 按 persisted URI permission 定位 `SAFDocFile`；无匹配 grant 时抛出 `MissingUriPermissionException`，删除入口会把非取消异常归一为带目标 path 的 `WriteException`。`delete()` 保留 `CancellationException`，非空目录的非递归删除先检查子项；递归删除先请求 provider 对目录直接级联删除，若失败且目录仍存在则后序枚举子项逐个删除。`SAFDocFile.delete()` 实际调用 `DocumentsContract.deleteDocument`；其返回 `false` 时网关仅在 `existsStrict()` 明确证明目标已经消失时才当作成功，避免把 provider 不可达误记为已删。
- 重复项路径的 `DeduplicatorDetailsViewModel` 在未确认时发出 `ConfirmDeletion`，`PreviewDeletionDialog` 展示删除确认与预览；确认后才提交 `DeduplicatorDeleteTask`。`DuplicatesDeleter` 按选中项逐个执行 `dupe.path.delete(gatewaySwitch)` 并在本地路径通知 MediaStore。固定代码未在此删除链发现回收站移动、文件内容恢复、undo delete 或安全擦除；现有 `undoExclude` 仅撤销扫描排除规则，不能恢复已删除文件。删除循环没有逐项异常封装或失败结果集合，某一项抛出异常会中断任务，而 `DeduplicatorDeleteTask.Success` 仅在完成后报告受影响路径/空间；取消虽在网关保留，却没有独立、持久化的部分成功/取消收敛模型。
- 静态日志会拼接 SAF path、document URI、可读路径或异常；本轮未运行测试、未访问设备文件、未请求/修改 SAF grant，也没有删除、移动、恢复或读取任何用户数据。固定单元测试源码存在但未执行，不能视为真实 provider 行为或恢复 proof。
- future `engine-cleaner` 必须在提交前固化用户可核验的预览快照；以可恢复暂存为优先，若系统/provider 仅支持不可逆删除则在确认前明确说明；对 permission loss、provider unavailable、cancel、单项失败/部分成功、crash 与版本差异提供逐项、脱敏、可持久化 terminal result，并分别在真实 SAF provider 上验证。该矩阵项从 `Pending` 调整为 `Partial`；Cleaner 台账保持 `Investigating`，不进入正式 engine 集成。

### sdmaid-se SAF 浏览、mutation 与外部打开边界静态审计（2026-09-08）

- 固定 `SAFGateway` 根据 persisted URI permission 找到最深匹配 scoped tree，提供 `listFiles`、lookup、walk、`canRead/canWrite`、`file`、create 与 delete。SAF 列表是调用时的 provider 快照，失败收敛为 path 关联的 `ReadException`；创建目标时会拒绝把 provider 改名/唯一化后的结果当作原请求成功。`PathMapper.takePermission()` 请求读写 persistable grant；升级失败时只尝试释放本次新增 flags。上述是有限的浏览、权限与创建边界，不证明任意 provider、Android 版本或中途 permission loss 的实机行为。
- `APathGateway` 和 `GatewaySwitch` 的固定合同并没有 rename、move 或 copy；对 `app-common-io` 的针对性检索也未发现 `DocumentsContract.renameDocument`、`moveDocument` 或 `copyDocument` 调用。因此矩阵所列的 rename/move/copy 不能由该固定上游 SAF 路径声称已覆盖，更没有临时输出、原子发布、内容/metadata read-back、跨 provider 回滚或逐项部分成功结果。
- `ViewIntentTool` 只接受 `LocalPathLookup`，经 `FileProvider` 构造 `ACTION_VIEW` chooser，并同时设置读/写 URI grants；SAF lookup 直接返回 unsupported。该路径是外部“打开”而非 `ACTION_SEND` 分享或受控导出，固定代码未见对 receiving app、生命周期结束或授权撤销的闭环。grant、path、lookup、URI 和异常会写入静态日志。
- 本轮只读固定隔离上游源码；未访问、创建、复制、移动、分享、打开或读取任何设备/用户文件，也未修改 SAF grant 或运行测试。future `engine-cleaner` 必须将浏览与 mutation 分离，以同一 scoped grant 内的预检、临时输出、read-back、原子发布和失败清理实现每次 rename/move/copy；外部分享只可授最小只读、最短生命周期 URI，并验证撤销。完成真实 provider 的 success、unavailable、cancel、crash、version-mismatch 和隐私输出验证前，该项从 `Pending` 调整为 `Partial`，Cleaner 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis runtime 组件更新与回滚边界静态审计（2026-09-08）

- 固定 ytdlnis `13320bb64f35c8d04f01bebfa782d7947758fb66` 的 `app/build.gradle` 声明 youtubedl-android library/Aria2c `0.18.1` 与 FFmpeg `0.17.2`；`RuntimeManager` 列出 Python、FFmpeg、Aria2c、NodeJS、Deno、QuickJS 和 yt-dlp，并在 no-backup/internal 目录初始化路径和运行环境。初始 yt-dlp 可由 APK resource `R.raw.ytdlp` 写入；运行时以 latch 在命令构造时等待 update 完成。固定源码尚不能证明任一实际 APK 的 ABI、native artifact、许可证、NOTICE、SBOM 或加载结果。
- `YTDLUpdater` 从 stable/nightly/master release API 的 JSON 读取 tag 和按 asset name 匹配的 `browser_download_url`，将文件下载到 `cacheDir` 临时文件。它不验证下载资产的 SHA-256、签名、长度、provenance 或运行时兼容性；安装时先删除现有 yt-dlp 目录、再新建并复制临时文件，非 staging 校验后的原子切换。复制失败会删除目录并调用 `initYTDLP()` 恢复内置 resource，但不保留/验证上一已知可用的已更新版本；版本标签/名称仅写入 SharedPreferences。
- `RuntimeManager` 会把 `IOException` 包装为带原始原因的 `ExecuteException`，`YTDLUpdater.fetchJsonFromUrl()` 直接记录 exception；不存在稳定的 unavailable、cancel、bad asset/version mismatch、native/process crash 或 rollback result。Python/FFmpeg/Aria2c/Node/Deno/QuickJS 的实际下载/更新 artifact 在本轮没有解析或运行，因此不能用 yt-dlp updater 推断它们的供应链闭包。
- 本轮仅只读固定隔离源码和 Gradle 声明，未请求 release API、下载/执行/更新 runtime、读取日志、URL、媒体、Cookie、设备文件或构建 artifact。future `engine-media` 必须对每个实际 ABI/runtime 使用签名或 SHA-256 锁定 manifest、来源、许可证、兼容矩阵及 SBOM；在隔离 staging 校验后原子发布，保留可验证 last-known-good rollback，并以脱敏、可取消 contract 覆盖 success、unavailable、cancel、crash、version mismatch。该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 下载任务并发、终止、恢复与重试边界静态审计（2026-09-08）

- 固定 ytdlnis `DownloadRepository.startDownloadWorker()` 用时间戳作 unique work 名，并以 `ExistingWorkPolicy.REPLACE` 提交 tagged `DownloadWorker`；worker 在前台运行，从 Room 读取 `Queued/Active/Paused/Error/Cancelled` 状态。`concurrent_downloads` 限制本轮可启动数量，设置下载间隔时以 mutex 串行化；但同一队列没有稳定、持久的批次标识或逐项 completion receipt，`REPLACE` 也不是完整并发/排队结果模型。
- 用户级暂停、恢复与取消确有有限实现：暂停/取消 receiver 和 ViewModel 会取消 tagged work、通过内存 `RuntimeManager.idProcessMap` 尝试终止子进程，再异步改写 Room 状态；恢复把 paused 项重置为 queued 后重新排队。该 map 不持久化，进程死亡、重启或 map 缺项时无法归因；receiver/worker 多处 `runCatching` 吞掉异常，暂停全部依赖一秒延时后写状态，取消/暂停没有可持久验证的进程终止、数据库写入和输出清理 completion receipt。
- `YTDLPUtil.buildYTDLRequest()` 可向 yt-dlp 传递用户设置的 `--retries` 与 `--fragment-retries`，且默认不加 `--no-part`；然而任务开始时和 failure 路径均直接删除 cache 输出目录。worker 对某一下载失败标为 `Error` 后自身仍整体返回 `Result.success()`；取消类异常直接返回而不收敛逐项状态。恢复路径不做已产出文件的 read-back、hash/容器完整性或部分成功核验。命令、URL 和原始异常还能进入 log/notification 相关路径，不能作为 XToolpro 的隐私安全合同。
- 本轮仅只读固定隔离上游归档中的 Worker、Room repository/viewmodel、receiver、runtime 和 request-builder 源码；未执行下载、请求、命令、媒体处理、文件操作、日志读取、Cookie/session 访问或设备操作。future `engine-media` 必须提供可持久化的每项状态机及终止 receipt，以脱敏、稳定的 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch` 收敛并发、暂停、恢复、取消、重试和断点；还须在真机验证真实断点、输出完整性、部分成功和进程重启恢复。该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 输出命名、SAF 迁移与提交边界静态审计（2026-09-08）

- 固定 `FolderSettingsModule` 为音频、视频、命令和缓存目录使用 `ACTION_OPEN_DOCUMENT_TREE`，申请读写 persistable URI grant；模板设置可写入音视频 filename template，`save_subdirectory` 可启用 website/playlist 等子目录。`YTDLPUtil` 将模板和目录传给 yt-dlp。文件系统目标冲突会追加递增编号；tree move callback 选择 `CREATE_NEW`，因此存在有限的命名、目录与冲突处理能力。
- `FileUtil.moveFile()` 的直接文件系统分支以 move/copy 后删除源；SAF fallback 经 `DocumentsContract.createDocument` 创建最终目标并以 input/output stream 直接拷贝，再删除源。该链路没有同目录临时 staging、fsync、长度/hash/容器或 metadata read-back、原子 publish、跨 provider rollback 或可持久化的逐项 commit receipt。移动的 per-file exception 仅记录后继续，最后仍返回累积路径并扫描媒体；failure fallback 和 `keepCache=false` 均会递归删源目录。独立 `MoveCacheFilesWorker` 对 API 26+ 使用 `REPLACE_EXISTING`，完成后递归删除缓存子目录，也未提供中断/部分成功/恢复模型。
- 路径格式化会把 tree URI 转为路径样式文本；打开/分享由 FileProvider 或现有 DocumentFile URI 给外部 app 授予 URI 权限，固定路径还会将路径和异常写入日志。该审计不能断言实际 provider 行为或任何设备上发生泄露。
- 本轮只读固定隔离上游的目录设置、request builder、文件迁移和 cache worker 源码；未发起 SAF picker、访问/创建/移动/删除/分享用户文件，也未读取日志、URL、Cookie、媒体或设备数据。future `engine-media` 必须在最小 scoped grant 内使用预检、临时输出、read-back、原子提交和失败清理，并以脱敏、逐项持久 receipt 覆盖 provider unavailable、冲突、permission loss、cancel、crash 和 version mismatch。完成真实 provider 与输出完整性验证前，该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 历史、取消记录与备份恢复边界静态审计（2026-09-08）

- 固定 Room history 保存 URL、标题、作者、缩略图、输出路径、format、文件大小和 command；`DownloadDao` 还保留 queued/scheduled/cancelled/error/saved 状态集合，固定 UI/repository 能分页列出和再入队未完成任务。任务成功时可插入 history，因此上游确有有限历史及取消记录能力。
- `BackupSettingsUtil` 和 `SettingsViewModel.backup()` 将 history、queued、scheduled、cancelled、error、saved、Cookie、偏好、模板等直接 Gson 序列化进未加密 JSON，并写到备份目录。restore 会先清空 SharedPreferences 或目标表、再逐条插入；整个跨偏好和多表恢复不使用 transaction、staging、备份完整性/签名/版本/来源校验、冲突预览、逐项 receipt 或 rollback。尽管 backup 包含 `scheduled`，固定 `restoreData()` 没有相应 scheduled restore 分支。
- `HistoryRepository.delete()` 可先删 Room 行、再尝试删除输出文件；底层删除错误被吞掉。cleanup worker 也会清除 cancelled/error 条目及 cache，故记录与输出的可恢复关联没有原子保证。历史、备份及 restore 中的 URL、路径、命令和 Cookie 均未见统一脱敏或加密边界。
- 本轮只读固定隔离的 Room model/DAO/repository、backup/restore 与 cleanup 源码；未导出、导入、读取或删除任何真实备份、媒体、URL、Cookie、日志或设备数据。future `engine-media` 必须最小化并加密敏感快照，校验 schema/版本/来源，先预览后原子或可回滚恢复，并以脱敏逐项 terminal receipt 覆盖损坏/错配、取消、部分失败和 crash。完成真实备份/恢复契约验证前，该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 后台通知、完成动作与开机恢复边界静态审计（2026-09-08）

- 固定 `DownloadWorker` 是 foreground WorkManager worker，`NotificationUtil` 为运行任务提供暂停/取消 action；完成通知可以打开/分享输出，失败通知可重新配置或跳转日志。`ScheduleAlarmReceiver` 也可将有 network constraint 的下载 worker 入队，故上游有有限后台、通知和排程能力。
- 这些通知以 `VISIBILITY_PUBLIC` 放置下载标题、完成输出路径和失败原始 error；完成/分享动作向外部 app 传 URI。固定路径没有统一对标题、路径、异常、日志或 URI 授权作脱敏、最小化或生命周期撤销。通知权限/通道不可用、receiver/work 异常和 action 处理没有写入同一可持久 terminal receipt。
- manifest 的 BOOT_COMPLETED/MY_PACKAGE_REPLACED receiver 是 `ObserveBootReceiver`，只重建 observe-source alarms，未恢复下载队列/排程或核验未完成任务。`AlarmScheduler.schedule*()` 创建 broadcast PendingIntent，但 `cancel()` 以 `getService` 寻找，故取消并非可证明的匹配清理路径。未发现下载任务重启、reboot、通知权限变化、定时 alarm 或 worker crash 后的闭环 read-back。
- 本轮仅只读固定隔离上游的 manifest、worker、notification、alarm 和 receiver 源码；未发通知、启动排程、访问任何设备/用户数据或读取通知正文、日志、URL、Cookie、媒体和数据库。future `engine-media` 必须提供默认最小化、脱敏的通知和最小读 URI 授权，且以持久化状态机和真实设备验证覆盖通知权限、reboot、排程、取消、crash、unavailable 与 version mismatch。完成前该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis URL、分享与剪贴板输入边界静态审计（2026-09-08）

- 固定 manifest 将 exported `ShareActivity` 注册为 `ACTION_SEND text/plain` 与多组 HTTP(S) `ACTION_VIEW` handler；其从 `EXTRA_TEXT` 或 intent data 取得输入并 `extractURL()`。homepage 的 `checkClipboard()` 可读取 primary clipboard，按换行筛出多个 `Patterns.WEB_URL`；`MainActivity` 还会读取分享 text file 的完整内容并传给 URL 输入。因此上游具备 URL、批量 URL、分享与剪贴板的有限入口。
- 入口按偏好可直接后台 queue download，没有逐 URL 明确确认、结构化/规范化结果、来源 allowlist、内容类型/大小边界、批量部分失败或 cancel receipt。`ShareActivity` 会 `Log.e` 整个 intent，URL 与 extras 因此可进入系统日志；共享、clipboard 和一般错误路径也能把原始字符串送入 UI/clipboard。固定源码没有为这些敏感输入提供默认脱敏/最小化持久化合同。
- 本轮仅只读固定隔离的 manifest、activity、主页 clipboard 与 UI helper 源码；未触发 share/deep link、读取实际剪贴板、URL、文件、日志、Cookie、媒体或设备数据。future `engine-media` 必须只在显式用户动作后，采用脱敏的结构化解析和逐项确认处理 share/clipboard 输入，禁止自动持久化或后台请求，并以真实设备验证拒绝、取消、批量部分失败、crash 和 version mismatch。完成前该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 播放列表、部分选择与增量下载边界静态审计（2026-09-08）

- 固定 `YTDLPUtil` 解析 playlist title/URL/index，结果表可按 playlist title 筛选。构建单项下载请求时，可对 playlist URL 使用 yt-dlp `--match-filter` 或 `-I` 选择成员；可选 `--download-archive` 支持避免已下载内容。因此存在有限播放列表、部分选择与增量下载基础。
- 上游没有不可变成员快照、source/version/etag、成员授权决定或每成员状态/部分成功 receipt；成员变动时不提供一致性模型。archive 开启且 final paths 为空时仅显示“already exists”，不能稳定区分 archive skip、空产出、输出迁移失败或执行失败。playlist URL、标题、成员 metadata、filter 和 command 又会进入 Room/history/log 路径。
- 本轮仅只读固定隔离上游的 parser、request builder、结果 DAO 和 worker 源码；未解析或访问任何真实播放列表、频道、媒体、URL、Cookie、日志或设备数据。future `engine-media` 必须在明确用户选择后持久化可审计成员快照与逐项状态，以脱敏稳定结果区分 skipped/success/failed/cancelled，并在真实 playlist/channel、增量、部分选择、成员变动、取消与版本错配上验证。完成前该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 视频、音频、缩略图、字幕与元数据输出边界静态审计（2026-09-08）

- 固定 `YTDLPUtil` 的结果解析读取 title、author、duration、thumbnail、chapters 与 available subtitles。构建下载请求时可组合 yt-dlp `--write-thumbnail`、`--convert-thumbnails`、`--write-description`、`--write-subs`、`--write-auto-subs`、`--embed-subs`、`--sub-format`、`--convert-subtitles`、`--sub-langs`、`--embed-thumbnail` 与 `--embed-metadata`；音频、视频路径还可分别配置缩略图/metadata 嵌入，故固定上游有有限的媒体及辅助输出请求能力。
- `DownloadWorker` 的成功链仅从 stdout 中特定的 `/storage` 文本或 cache-to-destination 迁移返回值推导 `finalPaths`。随后它有意过滤 thumbnail container、subtitle format、`description` 和 `txt`，并只对首个剩余媒体路径读取大小、扩展名及 Android 媒体时长；若执行成功但没有可保留的媒体路径，仍会删除队列、发完成通知且不写历史。迁移异常仅展示/打印而继续后续路径，worker 总体仍返回 `Result.success()`；失败和取消没有逐输出的最终状态或可核验部分结果。
- 固定路径未见视频/音频容器、编码、字幕内容、缩略图、description、嵌入 metadata 或副作用的 read-back、hash、原子发布、逐输出 terminal receipt 与 rollback。stdout、history、notification、log 与异常路径还可能携带标题、URL、路径、命令或原始错误，不能作为 XToolpro 的隐私安全合同。
- 本轮只读固定隔离上游归档中的 parser、request builder 和 worker 源码；未运行 yt-dlp、FFmpeg 或媒体任务，未解析/下载任何 URL，未读写媒体、缩略图、字幕、metadata、日志、Cookie、设备数据或用户文件。future `engine-media` 必须以经用户确认的格式任务为单位，使用临时输出、逐输出 read-back 与原子提交；以脱敏、可持久的 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch` receipt 覆盖媒体和每类辅助输出。完成真实设备格式/输出、部分成功、取消/失败清理与隐私验证前，该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 容器、编码、分辨率、帧率、音质与语言选择边界静态审计（2026-09-08）

- 固定 `arrays.xml` 声明 audio/video container 与音视频质量档；`DownloadViewModel` 和 `FormatUtil` 根据 format ID、container、codec、resolution、size、DRC、音频/字幕语言等 SharedPreferences 与解析到的 format 列表选择或排序。`YTDLPUtil` 把音频偏好组装为 `-x`、`-f`、`--audio-format` 及 `-S`（`abr`、`acodec`、`aext` 等）；视频路径组装带音轨 language filter 的 `-f` fallback，并以 `-S` 注入 video/audio codec、resolution、container、size 或 worst-quality 的 `+br,+res,+fps` 排序。因此固定上游具有有限 format preference 与 fallback 请求能力。
- 选择基于当次解析 JSON 与用户偏好；源码没有为 source 所支持的 format、container/codec mux 组合、精确 fps/分辨率/码率/语言、实际 fallback 原因或设备解码性建立稳定 capability declaration。视频 language 排序分支在固定 request builder 中仍被注释，音轨选择是 `language^=` filter 后再 fallback；任何 provider 变化、format 消失或 runtime/version 差异均不能由静态偏好推断成功。
- `DownloadWorker` 只从首个最终媒体文件记录 extension、大小和 Android 媒体时长，未对请求的 container、codec、fps、resolution、bitrate、audio/subtitle language、merge/recode 结果做 read-back、hash、逐输出 receipt 或 unsupported/fallback terminal state。因此 UI 资源与 request string 不是实际可用能力、兼容性提示或输出一致性的证明。
- 本轮仅只读固定隔离上游的资源、view model、format utility、request builder 和 worker 源码；未解析/下载 URL、运行 yt-dlp/FFmpeg、读取日志/配置/媒体/设备数据或修改任何用户数据。future `engine-media` 必须在明确用户选择后对可用 format 建立版本化 snapshot，明确选择与 fallback，提交前 read-back 实际 stream/container/codec/resolution/fps/bitrate/language，并以脱敏持久 receipt 覆盖 unsupported、unavailable、cancel、crash 与 version mismatch。完成真实 source/format、合并、fallback 和目标设备兼容性验证前，该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 音视频合并、音频提取与转码边界静态审计（2026-09-08）

- 固定 `YTDLPUtil` 可为音频请求加入 `-x`、`--audio-format` 与 `--audio-quality`；视频请求可使用 `--merge-output-format` 或 `--recode-video`。compatibility mode 固定请求 mp4/h264/aac 与 FFmpeg baseline profile；裁剪和移除音轨会构建 `FFmpegCopyStream` postprocessor 参数。因此上游具有有限的请求级合并、提取和转码入口。
- 这些参数只是传给受固定 runtime/version/ABI 约束的 yt-dlp/FFmpeg；固定代码没有可审计的 post-process 输入/输出映射、临时 staging、原子发布、container/stream 解码 read-back、hash、目标设备可播放性或逐步骤结果。worker 总体成功并不等于每一 merge/recode/extract 步骤成功，且其 cache 移动与 failure cleanup 不能提供部分成功、取消、native crash 或回滚 receipt。
- 本轮仅只读固定隔离上游的 request builder 与既有 worker 输出/清理路径；未运行 yt-dlp、FFmpeg 或命令，未下载/读取/写入任何 URL、媒体、日志、配置、Cookie、设备或用户文件。future `engine-media` 必须在受确认任务下记录版本化 post-process plan，以临时输出和逐阶段 read-back/原子提交收敛 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch`，并在真实 ABI/设备上验证合并、提取、转码、取消、crash 与恢复。完成前该矩阵项从 `Pending` 调整为 `Partial`；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 真机运行时前置检查（2026-09-08）

- 仓库现有 `AGENTS.md` 的 Phase 02 ADB 授权允许控制范围内测试包的生命周期和检查非敏感设备状态，同时明确禁止 root、日志、配置、通知正文、节点、请求、数据库、文件、凭据、Cookie、订阅 URL 与流量内容读取。本轮按该受限规则只确认一台设备已连接，并对已知 ytdlnis 与 XToolpro dev package 作 `pidof` 进程存在性检查；两者均未运行。
- 主机亦未发现可执行的 `ffmpeg`、`ffprobe` 或 `yt-dlp`。没有以未知版本的设备安装包或替代 host runtime 冒充固定 ytdlnis `13320bb64f35c8d04f01bebfa782d7947758fb66` 的运行时；未安装、启动、强停或卸载任何 package，未下载/处理测试媒体，未读取任何应用或设备内容。
- 真机 ABI/API 只读结果为 arm64-v8a/API 33，符合固定 app `minSdk 24`。但固定归档的 `settings.gradle` 声明 `:common`、`:app`、`:library`、`:ffmpeg`，归档中实际缺少 `library` 与 `ffmpeg` module；因此不能把该不完整归档直接构建为可归因的固定提交 APK，更不能以临时替换的未知 artifact 声称 runtime parity。
- 因此当前只证明真机验证环境的 runtime/commit 可归因性仍不可用，不构成媒体解析、下载、合并或转码 success/unavailable proof，也不改变矩阵状态。future 验证需在版本、ABI、许可证与 artifact hash 均已锁定的隔离 Android 宿主中进行，并在先前定义的受限 ADB 范围内仅记录脱敏 terminal result；Media 台账保持 `Investigating`，不进入正式 engine 集成。

### ytdlnis 固定提交源码闭包树核验（2026-09-08）

- 在隔离 clone `D:\xtoolpro\.tmp-ytdlnis-source-verify-20260908` 中只读执行 `git rev-parse HEAD`、`git ls-tree -r --name-only 13320bb64f35c8d04f01bebfa782d7947758fb66 -- library ffmpeg common` 和 `git ls-tree 13320bb64f35c8d04f01bebfa782d7947758fb66`。固定提交为 `13320bb64f35c8d04f01bebfa782d7947758fb66`；路径过滤对 `library`/`ffmpeg` 无输出，顶层树仅含 `.github`、`.idea`、`.kotlin`、`app`、`gradle`、`fastlane` 及构建文件，未包含两模块。
- 同一 clone 的历史只读核验显示祖先 `0ef8f5bf`（`Init`）曾包含 `common/`、`library/`、`ffmpeg/`；`2b28f347`（`1.1.0`）对 `library/src/...` 与 `ffmpeg/src/...` 执行删除，后续固定提交仍没有这些目录。固定 `settings.gradle` 却继续 `include ':common', ':app', ':library', ':ffmpeg'`，`app/build.gradle` 继续声明 `io.github.junkfood02.youtubedl-android:library:0.18.1`、`aria2c:0.18.1`、`ffmpeg:0.17.2` 外部坐标。
- 本轮没有联网补取缺失对象，没有构建、安装或运行该提交，也没有读取媒体、Cookie、日志、配置、数据库或设备文件。结论是固定提交源码归档本身不构成可独立重建的完整闭包；不能以未知 artifact、设备 APK 或临时替换模块冒充固定提交 runtime parity。Media 台账保持 `Investigating`，相关矩阵行保持 `Partial`，Phase 02 gate 不变。

### FlClash 请求、规则、内核日志与崩溃诊断数据流静态审计（2026-09-09）

- 固定来源提交为 `62addf738a76b1a492e19af2dbabdb6d572b9e72`。本轮仅在固定隔离源码上执行 `git rev-parse HEAD`、`git ls-tree`、`Get-FileHash -Algorithm SHA256`、`rg` 与 `Get-Content`；关键文件哈希为：`core/hub.go` `9EE8E64E73390F9120B088D893EFF836F318F1FC2E11372B172317C6E3EB0AD0`，`core/lib.go` `0C2A7D1F5527FD9478F27255DB59B6328D2736AE211DB41EFB0C0F318A31E831`，`core/constant.go` `9088411E269CAE0C027159E5BB3CE3BD052A271789827430F106BEB7E22F60BE`，`core/Clash.Meta/tunnel/statistic/manager.go` `D6633D8CA012523924C7BB8103800189679EF2E127524C1EFFFDFDDF40FFDB3F`，`lib/core/event.dart` `BD48F0771D2D3155089EF22D12566DB19EBA89300CAADC76095617C5C921D335`，`lib/manager/core_manager.dart` `D1E55B4ADCB151F99846CCE1C7A6D59BDE7FB159788C6D1E05A12C87EA9BCC53`，`lib/providers/app.dart` `467C46177978D5E3FCE6CCC9320FB568BE3899D7828E4DB7349DF9679F424114`，`lib/common/print.dart` `A2FCF1E617588CC7F9E6D90917673070BBA6078B55BB998AAE3035B2F80BCDA2`，`lib/views/logs.dart` `16B4B7D8750440BAC99CD5CC0D6C8A25238769DEA688BDF6A89CD6842376BD2C`，`lib/views/connection/requests.dart` `9EC3B75EB42C202CC568D82D5C2F6F63E19EA24A6CF48BBDC4CD586157918981`，`lib/views/connection/item.dart` `EB8B5A571B5388BD9D55DF8DBFBE4EC56DA8F18305DB32F1895DCF5672753815`，`android/common/.../GlobalState.kt` `BF8E96216436333B3D036CAFECD89687EC67ECC41D89BF3996838D2E98BEBE42`。
- `core/hub.go` 的 `handleStartLog()` 订阅 core log 并按 log level 过滤，`handleStopLog()` 取消订阅；`statistic.DefaultRequestNotify` 把 tracker 作为 `RequestMessage` 发送，`handleCrash()` 直接 `panic("handle invoke crash")`。`core/message.go` 的 bulk 队列容量为 256、批量为 32，满载丢弃同类最旧事件且不阻塞 core；`core/lib.go` 在无 listener 时丢弃，存在 listener 时把事件 JSON 序列化后回调。
- Dart `lib/core/event.dart` 分发 log/request/crash；`CoreManager.onLog` 写入 `logsProvider`、error 级别直接显示 notifier，`onRequest` 写入 `requestsProvider`，`openLogs` 仅控制 core capture。`lib/common/print.dart` 在 app attach 后无论 `openLogs` 是否开启都把 app 日志写入内存 `logsProvider`；`lib/providers/app.dart` 的 `exportLogs()` 将全部日志拼接到临时文件并经 SAF 保存，`lib/views/logs.dart` 以 `SelectableText` 显示正文。请求详情页直接展示 process、rule、rulePayload、host、source/destination IP、DNS mode、special rules、remote destination、chains 等字段。
- Android `GlobalState.kt` 使用 `Log.d("FlClash", text)`；`ServicePlugin.kt` 可将原始 `error.message` 经 MethodChannel 返回，`ServiceState.kt` 在 setup 失败时将原始 message 写入 Android log 并 Toast。固定模块引用 Firebase Analytics 与 Crashlytics NDK；Dart 默认关闭 Crashlytics collection，但“上次崩溃”查询仍初始化 Firebase，manifest 未提供静态默认禁用声明。未发现直接将 `commonPrint`、URI 或配置字段提交给 Crashlytics 的调用，也未进行 SDK 自动采集/上传或 logcat 验证。
- 本轮未执行 ADB，未读取日志正文、请求/连接正文、规则或配置、通知内容、崩溃报告、订阅 URL、凭据、Cookie、数据库、文件、地址、路由、DNS 或流量内容；未触发 crash、网络请求、日志导出或遥测上传。结论仅证明固定源码的数据流与隐私边界，不构成运行时字段泄露或崩溃诊断能力的 success proof。future `engine-proxy` 必须在边界处最小化并脱敏 process/host/IP/rule/chains 等字段，统一稳定错误码，默认不初始化或上传遥测，显式同意后才启用；日志与请求队列需有可核验的 drain/drop 生命周期，导出使用最小 SAF grant，所有 success、unavailable、cancel、crash、version mismatch 均需逐项持久 terminal receipt。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash always-on 与 lockdown 能力边界静态审计（2026-09-09）

- 固定 `VpnService.onStartCommand()` 将系统启动回调发送为受签名权限保护的 `VPN_START_REQUESTED` 广播；`ServiceBroadcastReceiver` 再交给 `ServiceState.handleStartAction()`。当 Flutter engine 未附着时，该路径读取已有 shared state、调用 `quickSetup` 并请求 native service start；它不是普通 bound-service 的可 await 启动回执。
- 固定 `VpnService.onRevoke()` 先执行 service/module/TUN stop，再发送同样受保护的 `VPN_REVOKED` 广播。源码未声明独立的 service restart mode，也没有持久化运行意图、重试退避、reboot 后健康核验、lockdown 检测/设置 API 或竞争 VPN 仲裁；app process 死亡时内存中的 `ServiceState`/controller 不能提供恢复记录。
- 本轮仅复核固定源码和既有只读系统入口证据；未写入 always-on/lockdown 设置、未重启设备、未杀进程、未触发 VPN revoke 或竞争 VPN，也未读取配置、通知、日志、请求、节点、订阅 URL、凭据、Cookie、数据库或设备文件。因此不能把系统设置入口可达或当前“未配置/未启用”状态当作该能力的 success proof。
- future `engine-proxy` 必须把 always-on、lockdown、reboot、process death、service loss、VPN revoke 与竞争 VPN 分成可观察的 capability/health 状态；不支持或无法检测时返回脱敏 `Unavailable`，恢复意图和失败原因进入持久状态机，并以真实设备契约验证启动、断线阻止、恢复、取消、crash 与 version mismatch。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash always-on/lockdown 受限 ADB 停止基线复核（2026-09-09）

- 仅执行 `adb devices`、目标包 `pidof`、`cmd appops get com.follow.clash.dev POST_NOTIFICATION`、`settings get secure always_on_vpn_app`、`settings get secure vpn_lockdown`，以及设备端计数命令 `dumpsys connectivity | grep -c 'VPN CONNECTED'`、`grep -c 'tun' /proc/net/dev`。设备 `bf353dda` 在线；`com.follow.clash.dev` 与 `com.steveliuyan.xtoolpro` 均无进程，`POST_NOTIFICATION` 为 `allow`，always-on/lockdown 均为 `null`，VPN 标记计数为 `0`，TUN 计数为 `0`。
- `adb shell ip -o link show type tun` 被设备 shell 返回 `Permission denied`；`pm check-permission` 不是该设备 shell 的可用命令。未 root、未绕过权限、未读取完整 connectivity 输出或接口正文，不据此推断更多状态。
- 本轮未写入 always-on/lockdown、未启动/停止/杀除应用、未重启设备、未触发 revoke 或竞争 VPN，也未读取配置、通知正文、日志、请求、节点、订阅 URL、凭据、Cookie、数据库、文件或流量内容。结果只是停止态状态复核，不构成 always-on、lockdown、reboot、process-death 或恢复 success proof；Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### engine-proxy 五类契约闭包静态核验（2026-09-09）

- 仅只读复核 `docs/architecture/engine-contract-test-plan.md`、`engine-proxy/build.gradle.kts`、`engine-proxy/src/main/AndroidManifest.xml` 与 `core-model/src/main/kotlin/com/steveliuyan/xtoolpro/core/model/ModuleId.kt`。计划统一定义 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch`，并要求 task ID、能力/版本/健康入口及脱敏日志；Proxy 表格也列出固定 FlClash 提交与五类前置/断言。
- 固定模块实际只有 Android library 壳、`implementation(project(":core-model"))` 和空 manifest；没有 Kotlin/Java adapter、native/FlClash 依赖、公开 contract、版本/健康 handshake、任务状态模型或测试源。只读 `Get-FileHash -Algorithm SHA256` 结果为：计划 `20161EFB1E7DFCA70CD31F88CA504328E172B0E534D7275F05E038D1E3BC77F6`，Gradle `5A152141FE75A58515AC09C45ED9A6EEB623767CCB44BF25C4299CE0E7D2CC65`，manifest `571F2735FAF5E857752EE057AC8EF63425576A721616C60B86EE5D4C0A2D682F`，`ModuleId.kt` `80B8B427F487858772F3D9FBEFD3878BDD7855AD4AFDB0FDD06628E87B0610ED`。
- 本轮未读取或纳入任何 `build/` 产物、SDK、缓存、上游源码归档、凭据或设备数据，也未运行契约测试、构建 adapter、注入故障或启动 VPN。结论是计划与模块源码闭包不完整，不构成任何五类结果执行证据；future `engine-proxy` 必须先提供版本化公开 contract、签名 provenance/health manifest、可取消任务状态机和隔离 success/unavailable/cancel/crash/version-mismatch 测试，再由真实 FlClash proof 绑定。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash URL profile 更新、重命名、删除与持久化边界静态审计（2026-09-09）

- 只读复核固定路径 `lib/views/profiles/profiles.dart`、`lib/views/profiles/edit.dart`、`lib/providers/actions/profiles.dart`、profile service 与此前已记录的 `Profile.normal(url).update()`/`Profile.update()` 调用。URL profile 的“同步”进入 update；编辑页的名称、URL、自动更新间隔仅在确认动作后写回，URL 变化还会触发更新；“复制链接”写入剪贴板，不是 profile clone，未发现用户可见复制/克隆 action。
- 更新失败路径会把原始异常传给 `commonPrint` 或批量更新 UI；本轮未见统一脱敏错误码、逐项结果、版本冲突处理或回滚 receipt。固定源码与真机入口能证明可达性，但不能证明 URL 下载成功后配置文件、标签、自动更新状态与数据库记录以原子方式提交，也不能证明删除/重命名失败时可恢复。
- 本轮未发送订阅 URL、未写入剪贴板、未执行同步、重命名、删除或保存，未读取配置、日志、数据库、文件、凭据、Cookie 或设备内容。future `engine-proxy` 必须以显式用户确认和版本化 snapshot 执行更新，先校验再原子提交；删除/重命名需可回滚并以脱敏 `Success`/`Unavailable`/`Cancelled`/`EngineCrashed`/`VersionMismatch` terminal receipt 收敛。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash 竞争 VPN 与 establish 冲突处理静态审计（2026-09-09）

- 固定启动链只通过 Android `VpnService.prepare()` 等待系统授权，再由 `VpnService.Builder.establish()` 取得 descriptor；未见 active VPN 查询、竞争 VPN 仲裁、保留原 VPN 的策略或独立 conflict capability。`ServiceController`/`ServiceState` 的运行状态由 service 调用和 request token 驱动，不以系统已有 VPN、TUN、core health 或流量回执作最终条件。
- `Builder.establish()` 返回空或抛出时，底层 service 有 stop/异常传播路径，但 `ServicePlugin.start()` 仍可先向 MethodChannel 返回 `true`，且上层错误容易退化为原始异常或普通布尔失败；未发现稳定脱敏 `Unavailable`/`Cancelled` 分类来说明“竞争 VPN 占用”或恢复条件。该静态结论不推断 Android 系统在任意具体冲突场景的实际选择结果。
- 本轮仅复核固定源码及既有停止态证据；未启动第二个 VPN、未修改系统 VPN、未发送流量、未读取系统 VPN 正文、日志、配置、节点、订阅 URL、凭据、Cookie、数据库或设备文件。future `engine-proxy` 必须在 prepare/establish 前后读取受限的系统能力摘要，显式区分 conflict、permission deny、unavailable、cancel 和 engine failure，保留其他 VPN 状态并以 TUN/health 回执确认最终结果；真实竞争 VPN、首次授权拒绝、取消、crash 与 version mismatch 契约完成前，Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`。

### FlClash TUN、系统代理与局域网监听范围静态审计（2026-09-09）

- 固定路径将 Android `VpnService.Builder.establish()` 的 TUN 生命周期与 core mixed proxy 监听分开处理；“局域网代理”开关改变监听范围，关闭态只保留 loopback，开启态允许 wildcard `:7890`。停止路径可撤销该监听，但未见独立的 LAN 客户端 allowlist、认证策略、UDP 能力声明或跨设备健康回执。
- 既有真机 proof 仅覆盖本机 `127.0.0.1:7890` 的 HTTP/SOCKS5 TCP 请求和有限并发，以及开关前后的监听计数；未从其他局域网设备连接，也未验证鉴权、跨网段防火墙、UDP、性能、异常关闭或端口复用。wildcard 监听表示网络暴露面扩大，不能直接视为安全共享成功。
- 本轮只读复核固定源码和既有脱敏计数证据；未启动/停止 VPN、未切换局域网开关、未发送流量、未读取代理配置、节点、请求/响应、日志、通知、订阅 URL、凭据、Cookie、数据库或设备文件。future `engine-proxy` 必须在显式确认后声明 bind scope、认证和协议能力，默认 loopback，针对 LAN 暴露提供风险/不可用状态，并以停止清理、跨设备受控 fixture、UDP/冲突/取消/crash/version mismatch 契约验证。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash mixed listener 与认证绑定边界静态复核（2026-09-09）

- 固定 Clash.Meta 配置模型声明 `mixed-port`（默认 `7890`）、`allow-lan` 与 `bind-address`，并同时保留 `authentication`、`skip-auth-prefixes`、`lan-allowed-ips`、`lan-disallowed-ips` 字段；这些字段说明上游具备配置表达能力，但不等于每个 listener 都消费了相同的访问控制合同。
- 固定源码中已定位的 authentication middleware 挂接在 external controller/HTTP API 路径；mixed HTTP/SOCKS5 listener 的创建与 TCP/UDP 接受路径未在同一调用链中显示该 middleware 的绑定。因此不能把 controller 鉴权静态外推为 mixed proxy 入站鉴权，也不能把 `allow-lan=true` 或 wildcard `:7890` 监听视为已完成安全 LAN 共享。
- 既有真机证据只覆盖本机 loopback HTTP/SOCKS5 TCP、有限并发和 wildcard 监听计数；没有跨设备连接、凭据输入、认证成功/拒绝、UDP、性能、异常关闭或端口复用验证。本轮未启动或停止 VPN、未切换 LAN 开关、未发送流量、未读取配置、节点、请求/响应、日志、通知正文、订阅 URL、凭据、Cookie、数据库或设备文件，未使用 ADB。
- future `engine-proxy` 必须默认 loopback，并在用户明确确认后以版本化配置声明 bind scope、协议集合、认证方式、LAN allow/deny 规则和风险状态；mixed 入站与 controller 管理面必须分别建模和测试，认证缺失/拒绝、LAN 不可用、UDP 不支持、端口冲突、取消、crash 与 version mismatch 均返回脱敏 terminal receipt。完成受控跨设备 fixture 与上述契约测试前，矩阵对应行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash mixed listener TCP/UDP 协议范围静态复核（2026-09-09）

- 固定上游在 mixed 入站之外保留独立的 TCP listener 与 UDP relay/association 处理路径；配置中的 `mixed-port` 表示共享端口入口，但不自动构成 TCP、UDP、DNS 或不同代理协议均已可用的单一健康信号。
- Android bridge 仅向系统发布 loopback HTTP `ProxyInfo`，既有 device proof 也只验证 HTTP/SOCKS5 TCP；固定 bridge 没有暴露 UDP association 的能力查询、会话计数、超时/取消回执或逐协议错误分类。因而不能由 TCP `204` 响应、wildcard 监听或 `tun0` 存在推断 UDP relay 已建立或停止后已清理。
- 本轮只读复核固定源码与既有脱敏计数证据，未生成 UDP 流量、未读取 DNS/请求/响应/配置/节点/订阅 URL、凭据、Cookie、日志、数据库或设备文件，未使用 ADB。future `engine-proxy` 必须把 HTTP、SOCKS5 TCP、SOCKS5 UDP、DNS 和 TUN 能力拆成可探测 capability，分别报告 unsupported、timeout、cancel、crash 与 version mismatch，并在 stop 时等待 TCP/UDP 会话收敛。
- 在受控协议 fixture、LAN scope、端口冲突和停止清理契约完成前，矩阵对应行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash Android 系统代理注入与撤销边界静态复核（2026-09-09）

- 固定 `VpnService.kt` 在 Android 10/API 29+ 且 `VpnOptions.systemProxy=true` 时调用 `VpnService.Builder.setHttpProxy()`，以 loopback、mixed port 和绕过域名构造 `ProxyInfo`；停止路径撤销该 VPN builder 状态。该调用只证明系统 VPN 对象被设置，不证明每个应用遵循 HTTP proxy、PAC、HTTPS CONNECT 或绕过域名语义。
- Android bridge 未提供 `setHttpProxy()` 成功/失败、应用侧生效、PAC/HTTPS 兼容性或绕过域名命中回执；既有真机只核对过 `LinkProperties` 中 loopback `7890` 与停止后的撤销，未读取应用请求内容或做逐应用对照。
- 本轮只读复核固定源码与既有脱敏状态证据，未启动/停止 VPN、未切换系统代理、未发送流量、未读取配置、请求、日志、通知、节点、订阅 URL、凭据、Cookie、数据库或设备文件，未使用 ADB。future `engine-proxy` 必须把 system-proxy 注入、应用遵循、绕过域名和撤销分别建模，在 API 不支持、设置失败、冲突、取消、crash 与 version mismatch 时返回脱敏 terminal receipt。
- 完成受控 API 版本、应用类型、绕过域名和停止撤销契约测试前，矩阵对应行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash external controller 管理面鉴权与监听边界静态复核（2026-09-09）

- 固定 Clash.Meta external controller 使用独立 HTTP 管理 listener，并在 controller 路径挂接 authentication middleware；该管理面与 mixed HTTP/SOCKS5 proxy listener 分离，不能用 controller 的鉴权实现证明代理入站已鉴权。
- 固定 Android bridge 未提供 controller bind scope、secret 轮换、权限分级、管理 API capability/health 查询或停止时的 controller listener 回执。配置模型中的 `authentication`/secret 字段存在，不等于固定 Android 包已启用、已安全存储或已覆盖所有管理方法。
- 本轮仅复核固定源码与既有脱敏证据，未连接 controller、未发送管理请求、未读取 secret、配置、节点、订阅 URL、凭据、Cookie、日志、数据库或设备文件，未使用 ADB。future `engine-proxy` 必须默认将管理面限制为 loopback，采用 Keystore 保护的 secret、最小权限 API、明确轮换/撤销和独立健康回执；管理面不可用、鉴权拒绝、端口冲突、取消、crash 与 version mismatch 均需脱敏 terminal receipt。
- 在 controller 与 mixed 入站分别完成 bind、鉴权、权限、停止清理和异常契约测试前，矩阵对应行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash 配置导出 `FilesProvider` 范围与 URI 授权静态复核（2026-09-09）

- 固定 Android manifest/`FilesProvider` 以 `MANAGE_DOCUMENTS` 保护 provider，但将整个 app `filesDir` 作为根目录；canonical path 检查用于阻止越出根目录，普通文件仍可按请求模式打开并标记为可写。该实现约束了路径逃逸，却没有把配置导出限定到专用临时目录或只读文档集合。
- 既有真机 proof 只确认配置导出 SAF 选择器可达、配置卡片与导出文件名存在对应关系；未打开或读取导出文件，未验证 URI grant 生命周期、撤销、复制传播或 provider 崩溃恢复。因此不能把文件名匹配或 provider 可达性视为最小授权、导出提交或隐私安全的证明。
- 本轮只读复核固定 manifest/provider 源码和既有脱敏 UI 证据，未发起导出、未读取设备文件、配置、节点、订阅 URL、凭据、Cookie、日志或 URI 正文，未使用 ADB。future `engine-proxy`/配置仓库必须使用专用临时 staging、最小只读 SAF URI、一次性或可撤销 grant，并在成功/取消/失败后验证清理；权限丢失、provider 不可用、取消、crash 与 version mismatch 均返回脱敏 terminal receipt。
- 在真实 provider 的 grant、复制传播、撤销、部分失败和恢复契约完成前，矩阵新增导出范围行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash 应用更新 metadata 与 artifact 信任边界静态复核（2026-09-09）

- 固定 `lib/common/request.dart` 的更新动作只读取 FlClash Release metadata，并与 `packageInfo.version` 比较；`about.dart` 的“内核”入口只是打开固定源码链接。真机 proof 仅确认“当前应用已经是最新版”，没有进入下载、安装或外部 artifact 处理。
- 固定路径未形成 artifact 签名/哈希、来源绑定、兼容性校验、安装前 staging、last-known-good 保留、失败回滚或更新 terminal receipt。因此 metadata 新旧比较、版本展示或下载入口可见性都不能作为更新成功、内核替换或回滚能力证明。
- 本轮只读复核固定源码与既有脱敏 UI 证据，未请求 release API、下载/安装 artifact、点击外部链接、读取配置、日志、节点、订阅 URL、凭据、Cookie、数据库、文件或设备内容，未使用 ADB。future `engine-proxy` 必须把 app/core 更新拆分为受签名 manifest、hash/ABI/API 校验、staging、原子发布和 last-known-good rollback，并将不可用、取消、崩溃、版本错配分别映射为脱敏 terminal receipt。
- 在真实 artifact 校验、安装失败恢复、取消和回滚契约完成前，矩阵新增更新信任行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash 查找进程模式与请求归因边界静态审计（2026-09-09）

- 固定 `FindProcessItem` 仅把 UI 开关映射为 `FindProcessMode.always/off`，再经 `UpdateParams.findProcessMode` 写入 `find-process-mode`；该路径没有 per-request attribution result、能力探测、权限/不可用分类、性能预算或回滚 receipt。开关持久化不等于 Clash.Meta 已完成进程识别。
- Android `PackageResolver`/`getPackages()` 是访问控制页的独立包清单入口，读取 `GET_PERMISSIONS` 安装包信息；它既不是 find-process 的运行时证明，也不应被 adapter 用作默认诊断或日志字段。固定请求/连接详情仍可能携带 process/path，未见统一最小化和脱敏边界。
- 本轮仅复核固定源码与既有开关持久化 proof；未启动 VPN、未读取应用清单、进程名、请求/连接、配置、日志、节点、订阅 URL、凭据、Cookie、数据库或设备文件。future `engine-proxy` 必须将进程识别声明为可选 capability，在权限/引擎不可用时返回脱敏 `Unavailable`，对每项归因提供最小字段和可验证状态，并以真实流量、性能、取消、crash 与 version mismatch 契约测试确认；Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash TCP 并发开关与运行时行为边界静态审计（2026-09-09）

- 固定 `TcpConcurrentItem` 只读写 `state.tcpConcurrent`，由 `UpdateParams.tcpConcurrent` 传入 `tcp-concurrent`；可证明的是配置布尔值跨进程持久化，不是 core 已接受该参数或建立了可观测并发策略。路径未提供 core ack、有效范围/资源预算、每会话并发指标或失败回执。
- 既有 4 路 loopback HTTP 请求 proof 仅证明一次受限并发转发，未在 TCP 并发开关两态下使用相同 fixture 做对照，也未隔离 TUN、代理池、系统调度和网络条件，不能把它归因于 `tcp-concurrent`。
- 本轮只读复核固定源码与既有持久化证据；未启动 VPN、未发起压力/并发流量、未读取连接元数据、配置、日志、节点、订阅 URL、凭据、Cookie、数据库或设备文件。future `engine-proxy` 必须在受控 fixture 下验证开关两态的实际并发上限、资源与取消收敛，失败时返回脱敏 `Unavailable`/`EngineCrashed`，并以稳定 terminal receipt 覆盖重设、超时、crash 和 version mismatch。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash 统一延迟开关与测速语义边界静态审计（2026-09-09）

- 固定源码链路为 `UnifiedDelayItem` → `state.unifiedDelay` → `UpdateParams.unifiedDelay` → `unified-delay`；该链路只证明 UI 设置能够进入 profile/update 参数，未见独立的 core 接收确认、版本协商或运行时健康回执。
- 既有真机 proof 仅覆盖开关关闭/开启后的跨进程持久化，并在结束时保持 `VPN CONNECTED=0`、`tun0` 不存在；它没有启动 VPN 或执行新的测速。已有测速 URL 校验、保存、重启和可见延迟值证据也未隔离 unified-delay 两态、同一节点/同一网络和重复样本，不能归因于该开关。
- 固定路径未提供握手、连接、TLS 或响应阶段的延迟分解，也未给出超时、取消、core 拒绝、无结果或部分结果的稳定脱敏映射；因此不能证明“统一延迟”语义已去除额外握手时间，或其结果会按预期影响排序。未发起网络请求，未读取日志、请求、配置、节点、订阅 URL、凭据、Cookie、数据库或设备文件。
- future `engine-proxy` 必须把参数应用、测速样本和排序结果绑定到可追踪 task ID，以脱敏 terminal receipt 区分 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch`，并在受控同节点/同网络 fixture 下分别验证开关两态、握手排除、超时/取消和失败收敛。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash 测速链接与延迟测试输入契约静态审计（2026-09-09）

- 固定路径由 `TestUrlItem` 读取 `appSettingProvider.testUrl`，拒绝空值/非 URL；`proxies.dart` 将其作为延迟测试输入，`interface.dart` 以 `test-url` 传入 core，`compute.dart` 再参与延迟状态和排序。该映射没有单独的输入版本、目标归属或 core 接收确认。
- 既有真机 proof 仅覆盖公开测试 URL 的编辑校验、保存/重启保留、一次代理页可见延迟结果和恢复默认值；未使用敏感或订阅 URL，未捕获 core 请求，未隔离每个节点/组，也未验证重定向、不可达、超时、取消、部分结果或重复样本的一致收敛。
- 固定 `proxyDelayTest` 的失败日志模板包含代理名和原始异常对象；本轮未见稳定的 URL 脱敏、目标范围限制、请求与 task ID 绑定或逐节点 terminal receipt。静态路径不能据此断言任意 URL 一定泄露，也不能证明任意 URL 均被安全拒绝或仅访问用户预期目标。
- 本轮只读复核固定源码与既有脱敏 proof；未发起新的网络请求、未运行测速、未读取日志/请求/配置/节点、订阅 URL、凭据、Cookie、数据库或设备文件。future `engine-proxy` 必须默认禁止敏感 URL 进入日志/遥测，采用受控允许范围和稳定脱敏错误码，并在同一 fixture 下覆盖 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch` 及逐节点结果完整性；Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash DNS/IPv6 与 Fake-IP/Host/嗅探能力边界静态审计（2026-09-09）

- 固定 `network.dart`/`general.dart` 的开关进入 profile 构造；DNS 劫持开启时 setup 路径向 `Core.startTun` 传入 `dns = "0.0.0.0"`，Android `VpnService` 同时发布固定 DNS stub；追加系统 DNS 路径向 `rawConfig['dns']['nameserver']` 追加 `system://`。这些是参数/配置映射，不是最终配置写入或 core 生效回执。
- 既有设备摘要仅证明 IPv6、DNS 劫持和追加系统 DNS 开关可跨进程持久化，并在特定开启态观察到受限的 TUN、VPN、DNS stub、IPv6 地址/路由计数和公开 HTTPS 成功；停止后已恢复关闭。DNS 请求结果未做包级捕获，不能区分 TUN 捕获、代理侧解析或系统 resolver 路径。
- 固定源码未提供 Fake-IP、Host 重写或流量嗅探的独立 capability/health 查询，也没有为 DNS 失败、IPv6 不可用、Fake-IP 冲突、嗅探超时或取消建立稳定脱敏分类。未读取生成配置、DNS/路由正文、请求、日志、节点、订阅 URL、凭据、Cookie、数据库或设备文件。
- 本轮只读复核固定源码与既有脱敏计数证据，未切换设置、启动 VPN、发送新流量或执行包级 DNS/嗅探验证。future `engine-proxy` 必须将 DNS、IPv6、Fake-IP、Host 和 sniffing 拆成可探测 capability，默认最小化 DNS/host 字段，区分 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch` 并以受控 fixture 覆盖地址/路由、解析、冲突、超时和取消；Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash 按应用代理与 VPN bypass 契约边界静态审计（2026-09-09）

- 固定 `AccessView` 只在初始化时请求一次 `getPackages()`；Android `PackageResolver` 使用 `getInstalledPackages(GET_PERMISSIONS)` 生成含包名、标签、系统/网络标记与更新时间的完整 JSON 列表。空列表直接进入“无数据”渲染，未见可区分权限、查询失败、解析失败或暂时不可用的稳定结果/重试合同。
- 选择列表与 Android VPN bypass 是独立输入：启动 shared state 会携带 access-control accept/reject 包名列表，`VpnOptions.allowBypass` 则在建 VPN 时调用 `VpnService.Builder.allowBypass()`。固定路径没有把列表加载、选择写入、profile 应用、VPN 建立和每应用实际路由连接成可 await 的原子结果或版本/回滚 receipt。
- 既有真机 proof 只确认 allow-bypass 开关可跨进程保留，并在关闭态建立/停止过 TUN；当时没有可用应用列表，也未选择应用、读取清单、发送按应用流量或读取路由/请求字段。因此不能把 `QUERY_ALL_PACKAGES` 声明、系统 API 调用或 TUN 存在当作任一应用已按预期代理/绕过的证明。
- 本轮只读复核固定源码与既有脱敏证据，未读取应用清单、包名、标签、权限、配置、日志、请求、节点、订阅 URL、凭据、Cookie、数据库或设备文件，未切换 bypass 或路由设置。future `engine-proxy` 必须在用户进入该流程后最小化且仅内存内处理应用标识，使用版本化选择快照和可撤销 apply，逐项以脱敏 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch` 收敛；受控 fixture 还需覆盖权限缺失、空/失败列表、安装包变化、取消、崩溃与实际 bypass 路由。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash 规则集、命中归因与日志事件边界静态审计（2026-09-09）

- 固定请求/连接模型包含 `rule`、`rulePayload`、special rules 等规则相关字段，日志事件则经独立 bulk queue 进入 Dart 日志状态；固定源码未见规则版本、匹配阶段、命中原因或请求事件与规则事件之间的稳定关联 ID/terminal receipt。
- 既有真机 proof 只确认附加规则入口、规则模式下公开 HTTPS 成功，以及启用日志捕获后日志页出现新增记录；ADB 未读取规则、日志或请求正文，也未验证某一域名/IP/GeoIP 条目命中、优先级、fallback 或命中后路由选择。
- 日志/请求字段可能包含 host、地址、规则 payload、代理链等敏感诊断数据；本轮不据静态字段推断设备实际泄露，但确认固定路径没有统一脱敏或最小化结果合同。未发起流量、未读取日志/请求/配置、节点、订阅 URL、凭据、Cookie、数据库或设备文件。
- future `engine-proxy` 必须对规则集来源/版本和每次匹配提供最小化、可审计但不含原始目标的结果，明确 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch`，并以受控 fixture 覆盖规则优先级、域名/IP/GeoIP 命中、无命中、更新失败、取消和日志队列清理。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash 请求/连接详情、筛选与清理契约静态审计（2026-09-09）

- 固定 `TrackerInfo`/`Metadata` 对外携带 UID、进程/路径、host、源/目的地址与端口、规则及 payload、GeoIP/ASN、DNS、special rules、remote destination 和代理链；详情页直接渲染这些字段，而搜索只覆盖 network、host、destination IP、process 和 proxy chains，未建立字段最小化或搜索/详情一致性合同。
- 请求事件会进入容量受限但未脱敏的常驻内存 `requestsProvider`。`fullSetup()` 只在 Profile ID 变化时重置；普通 stop、VPN 配置重启和 core restart 的固定调用链不清空 requests。单条关闭未将结果映射到稳定状态，“关闭全部”不 await，core 对部分关闭错误仍可能返回成功。
- 既有真机 proof 仅确认请求页/连接页入口和脱敏计数，不读取任何连接正文、地址、应用或规则字段；本轮未执行关闭、停止、重启、导出或搜索行为。因此不能把 UI 可达性或列表计数当作字段安全、清理完成或关闭成功证明。
- 本轮只读复核固定源码与既有证据，未读取请求/连接、日志、配置、节点、订阅 URL、凭据、Cookie、数据库或设备文件，未发起流量。future `engine-proxy` 必须默认关闭诊断、按字段最小化并提供用户清除；单条/批量关闭和 stop/restart 清理需 await、可重试且能报告部分失败，所有结果使用脱敏 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch` terminal receipt。完成字段、搜索、清理与关闭契约测试前，Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash 日志、请求事件与崩溃诊断导出边界静态审计（2026-09-09）

- 固定 core log/request/crash 事件经容量 256、批量 32 的 bulk queue 投递，满载丢弃同类最旧事件；无 listener 时整批丢弃，有 listener 时 JSON 序列化后回调 Dart。该策略没有对 XToolpro 暴露队列深度、丢弃计数、drain completion 或 crash terminal receipt。
- `openLogs` 仅控制 core log capture；app attach 后 `commonPrint` 仍写入 `debugPrint` 和最多 500 项内存日志，日志页可显示可选择正文并将全部 `Log.toString()` 交给 SAF 导出。请求详情仍可能携带 process、host、源/目的地址、规则 payload、DNS、代理链等字段，固定路径未见统一字段脱敏层。
- 固定 Android 模块引用 Firebase Analytics/Crashlytics NDK；虽然 Dart 默认关闭 collection，启动时的“上次崩溃”查询仍初始化 Firebase，manifest 未提供静态默认禁用声明。未发现直接把 URI/配置交给 Crashlytics 的调用，但静态路径不足以证明 SDK 未自动采集或上传。
- 本轮只读复核固定源码与既有证据，未触发 crash、未导出日志、未读取 logcat、日志/请求/规则正文、通知、配置、节点、订阅 URL、凭据、Cookie、数据库或设备文件，未发起网络请求。future `engine-proxy` 必须默认不初始化/不上传第三方遥测，按字段最小化并以稳定错误码区分 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch`；日志队列需可核验 drain/drop，崩溃与导出需逐项 terminal receipt。Proxy 台账保持 `Investigating`，矩阵对应行保持 `Partial`，Phase 02 acceptance gate 不变。

### FlClash 内核版本、更新与回滚不可用边界静态审计（2026-09-09）

- 固定 `lib/views/about.dart` 将版本展示绑定到 `packageInfo.version`；`lib/common/request.dart` 的“检查更新”比较的是 FlClash 应用 Release 元数据；“内核”入口只打开固定上游源码链接。固定源码未见运行时读取 native core 版本或独立的更新/回滚 API。
- `android/core/src/main/cpp/CMakeLists.txt` 在构建期按 ABI 链接 `libclash.so`，运行时 Kotlin 仅加载已打包的 `core` library。现有关于页真机 proof 只确认应用版本、更新入口和源码链接语义，不构成设备内核版本、替换、回滚或失败恢复 proof。
- 固定路径未提供 artifact provenance/hash manifest、last-known-good 保留、原子替换、回滚触发器或更新失败 terminal receipt；不能把应用更新检查、外部源码链接、`System.loadLibrary` 成功或既有 APK hash 当作内核更新能力。
- 本轮只读复核固定源码与既有脱敏 UI 证据，未点击更新/源码链接、未下载或替换任何 native artifact，未读取构建产物、SDK、缓存、配置、日志、节点、订阅 URL、凭据、Cookie、数据库或设备文件。该项继续标记 `Unavailable`；future `engine-proxy` 必须随受签名版本 manifest 提供 core/bridge/ABI/hash 校验、staging + 原子发布、last-known-good rollback，并以脱敏 `Success`、`Unavailable`、`Cancelled`、`EngineCrashed`、`VersionMismatch` terminal receipt 覆盖升级、失败、取消和回滚。Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash native core/bridge 配对与 `VersionMismatch` 拒绝边界复核（2026-09-09）

- 固定 `android/core/src/main/cpp/CMakeLists.txt` 只有在当前 ABI 同时存在 `libclash.so`、`libclash.h` 与 `bride.h` 时才定义 `LIBCLASH` 并链接完整 core；缺少任一输入仍构建同名 `libcore.so`。对应 `core.cpp` 的 fallback 保留 Kotlin JNI 表面，但 `startTun`、`stopTun`、`quickSetup`、事件、DNS 等为空操作，流量查询返回 `{}`，因此构建成功或 JNI 可加载不能证明完整 core 已配对。
- 既有 arm64 APK/AAR 的 SHA-256、ELF 架构和 Go/JNI 符号检查只证明这一 artifact pair 的存在性与链接关系。固定 bridge/API 未查询或核验 FlClash/Clash.Meta commit、ABI、engine API、bridge revision、必需符号集合与 artifact hash 的受签名 provenance manifest；其余声明 ABI 也没有逐项实际产物和健康证据。
- `Core.kt` 无条件调用 `System.loadLibrary("core")`。`UnsatisfiedLinkError`、`ExceptionInInitializerError` 等 `LinkageError` 未被 service 的 `catch (Exception)` 明确映射，外层可能只得到泛化的 `0L`/`false`；fallback 又可能不抛错且不回调 `quickSetup`，造成静默假成功或无界等待。固定路径没有稳定脱敏的 `VersionMismatch`、`EngineCrashed`、加载/符号失败、健康超时或 terminal receipt。
- 本轮仅做固定源码与既有校验记录的静态复核，未构建、加载、替换或删除 native artifact，未读取 SDK、缓存、构建产物、配置、日志或任何敏感数据，未使用 ADB。future `engine-proxy` 必须在配置写入、导出或 VPN 请求前核验受签名 manifest 的 commit/API/ABI/bridge/symbol/hash；任一缺失、错配或不一致立即阻止启动并返回脱敏 `Unavailable`/`VersionMismatch`，加载/回调/运行健康失败分别返回 `EngineCrashed` 或 `Unavailable`。实际完整 core 与刻意缺失/错配 artifact 的五类隔离契约测试尚未执行，保持 `Partial`、Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash 停止时事件队列 drain/drop 与 listener 解绑顺序静态复核（2026-09-09）

- 固定 `core/message.go` 在包初始化时启动常驻 batcher；priority 与 bulk 两个 256 容量 channel 只按类别丢弃最旧事件，未提供 stop/close、drain、drop count 或 flush receipt。batcher 仅在两个输入 channel 都关闭后退出，而固定 `stopTun()`/`handleStopListener()` 路径没有关闭这些全局 channel 的动作，因此停止 core listener 不等于停止事件生产或清空排队消息。
- 固定 `core/lib.go` 的 `stopTun()` 先关闭 TUN，再在 `isRunning` 时调用 `handleStopListener()`；`setEventListener(nil)` 则释放旧 JNI global reference 并立即覆盖指针。`sendMessageBatch()` 在另一条 batcher goroutine 中直接读取 `eventListener`，未见锁、引用快照、in-flight 等待或与 release 的顺序屏障；停止/解绑期间存在回调读取旧指针、丢弃批次或释放后仍尝试投递的未建模窗口。该静态风险不推断设备已发生崩溃或回调竞态。
- 本轮仅只读复核固定提交的 `core/message.go`、`core/lib.go` 与既有证据，未构建、注入故障、启动/停止 VPN 或读取日志/请求正文、配置、节点、订阅 URL、凭据、Cookie、数据库、文件或设备内容，未使用 ADB。future `engine-proxy` 必须把停止建模为可等待事务：先阻止新事件，再有界 drain/drop 并返回计数，解绑 listener 后等待 in-flight callback，最后确认 TUN/core 停止；无法完成时返回脱敏 `Cancelled`/`EngineCrashed`/`Unavailable`，不得以 MethodChannel `true` 或 `STOPPED` 状态替代 terminal receipt。实际队列故障注入和五类隔离契约测试尚未执行，保持 `Partial`、Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash 配置导入校验、临时文件与备份恢复路径静态审计（2026-09-09）

- 固定 `lib/models/profile.dart:188-200` 的 `Profile.saveFile()` 将输入写入 `appPath.tempFilePath`，调用 `coreController.validateConfig(path)`，校验成功后用 `tempFile.copy(mFile.path)` 覆盖 profile 文件，再删除临时文件；校验失败、复制失败或协程取消均没有 `finally` 清理。`saveFileWithPath()` 在 `:202-210` 同样校验后直接覆盖目标。固定 `lib/common/file.dart:23-28` 的 `safeWriteAsBytes()` 只是创建后调用 `writeAsBytes()`，未提供 fsync、原子 rename 或 read-back；`lib/common/path.dart:76-79` 的临时路径按调用生成，但不形成提交事务或并发锁。
- 固定 `core/hub.go:86-93` 的 `handleValidateConfig()` 只读取给定路径并调用 `config.UnmarshalRawConfig`，返回原始错误字符串；它没有 schema/version 上限、资源大小、来源绑定或稳定错误码。`lib/providers/actions/profiles.dart:87-132` 的文件、URL、二维码入口在 profile 保存/更新异常时依赖通用 loading/safe-run 路径，未见每项 terminal receipt 或失败后的临时文件核验。
- 固定 `lib/common/task.dart:537-552` 的 `_restoreTask()` 将每个 ZIP entry 以 `posix.normalize(file.name)` 拼到 restore 目录后直接写出，未见 canonical-path containment、绝对路径拒绝或条目大小/数量限制；随后 `:590-617` 以并发 `File.copy()` 将 profile/script 迁移到正式目录。该路径存在归档 entry 路径逃逸和部分迁移不可回滚的静态风险；本轮未构造恶意归档、未读取设备文件、未执行恢复。
- 本轮仅读取固定提交 `62addf738a76b1a492e19af2dbabdb6d572b9e72` 的隔离归档源码与既有文档，未修改上游文件，未启动构建、未导入真实配置/订阅 URL、未读取日志、配置、节点、请求、数据库、凭据、Cookie 或设备文件，未使用 ADB。XToolpro 后续 adapter 必须在受限临时目录内执行大小/条目/格式预检和 canonical containment，验证后再以同目录原子发布；失败、取消、崩溃和版本错配要清理临时输入并返回脱敏 terminal receipt，恢复还必须具备 staging、逐项 read-back 与回滚。实际恶意归档、损坏/超限、取消、部分失败与版本错配契约测试尚未执行，矩阵相关行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash Profile script 覆写与配置生成边界静态审计（2026-09-09）

- 固定 `lib/providers/actions/setup.dart:289-303` 在 `OverwriteType.script` 下从 `setupState.script?.content` 读取整段脚本，并调用 `handleEvaluate()`；`lib/common/javascript.dart:6-27` 使用锁定 pubspec 中的 `flutter_js ^0.8.7`，把完整 raw config JSON 与脚本拼接为 `scriptContent + main(<config>)` 后交给 `getJavascriptRuntime().evaluateAsync()`。调用点未传入超时或取消令牌，也未见 runtime dispose、执行配额、网络/文件/环境沙箱、输入大小限制或脚本来源授权；该包装器本身没有声明这些边界，不能据此推断底层 runtime 提供了受限能力。
- `handleEvaluate()` 在 `res.isError` 时直接抛出 runtime 的 `stringResult`，成功路径把 `rawResult` 强制转换为 `Map<String,dynamic>`，没有输出 schema、版本/hash、字段白名单或 secret 约束；`getProfileWithId()` 又把异常 `toString()` 直接传给 notifier。脚本结果随后进入 `makeRealProfileTask()` 生成 YAML，并由 `_setupConfig()` 写入配置文件，未形成独立的可等待、可取消、脱敏 `Success`/`Unavailable`/`Cancelled`/`EngineCrashed`/`VersionMismatch` receipt。
- 固定 `lib/models/common.dart:637-660` 的 `Script.content` 会整体读取内部 JS 文件；`save()` 使用普通 `writeAsString()` 覆盖，`saveWithPath()` 调用 `File(copyPath).copy(copyPath)` 将源路径复制到自身，疑似无法导入。两条保存路径均未见原子写入、fsync、read-back、大小/hash/版本绑定或失败回滚。未执行脚本、未读取设备文件/配置、未使用 ADB；结论仅限固定源码静态边界。
- future `engine-proxy` 必须在受限 runtime 中绑定脚本来源、版本和 hash，预检输入大小与 schema，禁止未授权网络/文件/环境访问，支持有界 timeout/cancellation，归一化并脱敏错误；脚本失败、取消、runtime 崩溃、版本错配及配置发布失败都必须清理临时状态并返回可持久的 terminal receipt。完成受控 fixture 与五类契约测试前，矩阵新增脚本覆写行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash URL profile 自动更新调度与失败收敛边界静态审计（2026-09-09）

- 固定 `lib/application.dart:86-90` 只在 Flutter application 完成 attach 后建立固定 20 分钟的 one-shot `Timer`；callback 必须先完整等待 `autoUpdateProfiles()`，再递归安排下一轮。未见 WorkManager/Alarm、持久化 next-run、网络约束、跨进程恢复或 `finally` 重排程，因此进程死亡、detach 或 callback 外层异常都可能让后续自动更新静默停止；`dispose()` 仅取消当前 timer。
- 固定 `lib/providers/actions/profiles.dart:36-50` 顺序遍历当前 profile 快照，依据 `autoUpdate`、`lastUpdateDate + autoUpdateDuration` 和 URL 类型筛选，每项调用 `updateProfile()` 没有取消令牌、超时、并发去重、条件请求/版本冲突或逐项 terminal receipt；异常原文直接写入 warning log。`updateProfiles()` 的批量路径同样顺序 await，未提供部分成功/失败汇总。
- 固定 `profiles.dart:66-84` 的 `updateProfile()` 先把旧 profile 放回 provider，再调用网络 `Profile.update()`；后者拉取响应后执行 `saveFile()`，成功后才返回更新元数据。文件提交与 provider/数据库更新不在同一事务，进程中断、文件复制成功但元数据写回失败等中间态没有恢复标记或回滚。既有 `saveFile()` 的临时文件清理、原子发布和校验边界缺口会叠加到自动更新路径。
- 本轮只读固定隔离归档源码与既有证据，未发送订阅 URL、未执行更新、未读取配置/日志/数据库/文件/凭据/Cookie/设备内容，未使用 ADB。future `engine-proxy` 必须以持久化任务状态机记录 next-run、in-flight、cancelled、部分成功和失败原因，使用有界网络/文件事务、条件版本或 snapshot 校验、原子发布与回滚，并以脱敏 `Success`/`Unavailable`/`Cancelled`/`EngineCrashed`/`VersionMismatch` terminal receipt 收敛每个 profile；真实调度、取消、进程重建、网络失败和版本冲突契约完成前，矩阵订阅生命周期行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

### FlClash profile 下载请求与响应边界静态审计（2026-09-09）

- 固定 `lib/common/request.dart:18-50` 的 `_clashDio` 使用 `IOHttpClientAdapter` 和应用代理回调，`getFileResponseForUrl()` 直接按传入 URL 请求 `ResponseType.bytes`。固定构造未设置该请求的显式 connect/receive/send timeout、响应字节上限、Content-Type/长度预检、来源绑定或重定向策略；响应可能先完整驻留内存，再进入配置校验和临时文件路径。
- 请求异常在 `:39-49` 先将原始 `DioException.toString()` 写入 `commonPrint`，随后只把部分 Dio 类型转换为本地化网络错误，不能保证 URL、代理信息或底层错误字段已从日志路径移除。未见 profile 下载请求的独立取消 token；自动更新和手动更新均依赖上层 Future 完成。
- 本轮只读固定隔离归档源码与既有证据，未发送订阅 URL、未发起网络请求、未读取配置/日志/数据库/文件/凭据/Cookie/设备内容，未使用 ADB。future `engine-proxy` 必须在受限网络客户端中声明允许的 scheme/redirect、超时、最大响应大小、Content-Type 与配置来源校验，并支持有界取消；请求异常必须归一为脱敏类别，禁止 URL、认证信息和响应正文进入 UI/日志/导出。完成受控 HTTP fixture 的成功、超限、错误状态、超时、取消、重定向和版本错配契约前，矩阵导入/订阅生命周期行保持 `Partial`，Proxy 台账保持 `Investigating`，Phase 02 acceptance gate 不变。

#### 本检查点远端备份状态（2026-09-09，profile 下载请求审计）

- focused commit `e76443f` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，自动更新调度审计）

- focused commit `dfc64a2` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，脚本覆写审计）

- focused commit `c3aa4af` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，配置导入/恢复审计）

- 本次 focused commit `78b6f97` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，停止事件队列审计）

- 本次 focused commit（当前 `HEAD`）尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，mixed listener/认证边界审计）

- 本检查点 focused commit `f276768` 及本次状态记录提交（当前 `HEAD`）均尚未 push；按要求等待用户对 push 的明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，mixed listener TCP/UDP 审计）

- 本检查点 focused commit `bf30473` 及本次状态记录提交（当前 `HEAD`）均尚未 push；按要求等待用户对 push 的明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，系统代理注入审计）

- 本检查点 focused commit `545220b` 及本次状态记录提交（当前 `HEAD`）均尚未 push；按要求等待用户对 push 的明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，external controller 鉴权审计）

- 本检查点 focused commit `887b707` 及本次状态记录提交（当前 `HEAD`）均尚未 push；按要求等待用户对 push 的明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，FilesProvider 导出范围审计）

- 本检查点 focused commit `97684ae` 及本次状态记录提交（当前 `HEAD`）均尚未 push；按要求等待用户对 push 的明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，应用更新 artifact 信任审计）

- 本检查点 focused commit `fd5d56d` 及本次状态记录提交（当前 `HEAD`）均尚未 push；按要求等待用户对 push 的明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，native core/bridge 配对审计）

- 本次 focused commit（当前 `HEAD`）尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，内核更新审计）

- focused commit `2835b57` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，诊断审计）

- focused commit `d7f537b` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，请求详情审计）

- focused commit `58ddd9d` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，规则命中审计）

- focused commit `5ea564c` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，按应用 bypass 审计）

- focused commit `b9a4b96` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，DNS/IPv6 审计）

- focused commit `48c4606` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，测速链接审计）

- focused commit `7c3de45` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，统一延迟审计）

- focused commit `eeb6c1a` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，TCP 并发审计）

- focused commit `311d17d` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，进程归因审计）

- focused commit `82ebdd8` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，LAN 暴露审计）

- focused commit `ee898ba` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，VPN 冲突审计）

- focused commit `f47e736` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，profile 生命周期审计）

- focused commit `2762655` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，契约闭包审计）

- focused commit `7c111b7` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，ADB 基线）

- focused commit `1656a16` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，always-on 审计）

- focused commit `8e2bb94` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09）

- focused commit `a9842d9` 尚未 push；按要求等待用户对 push 的明确确认。未远端备份路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-08）

- focused commits 的远端备份暂未完成：对 `origin/codex/phase02-flclash-direct-logs` 的三次 push 均未返回远端更新，随后 `ls-remote` 未返回该 ref 的可核验 hash。未备份路径仅为 `AGENTS.md`、`docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件；未纳入任何其他既有工作区改动或临时产物。待远端可用时必须先推送并核验这些 focused commits，之后才能将本检查点描述为远端备份。

#### 上一检查点远端备份状态（2026-09-07）

- 本检查点 focused commit `392b7946d6c3cae25f0b91ce83f0d1cd2ad1306c` 已成功推送到 `origin/codex/phase02-flclash-direct-logs`，远端分支核验结果与该提交一致；涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件。此前短暂出现的 GitHub CLI 网页回调超时不影响 Git push，未将凭据或验证码写入证据。

#### 本检查点远端备份状态（2026-09-07）

- 本检查点 focused commit `b021cfb8fe6ee9a425966cf02917c4d8b5f36a5e` 已成功推送到 `origin/codex/phase02-flclash-direct-logs`，远端分支核验结果与该提交一致；涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件。工作树中的其他既有修改和临时产物未纳入提交。

### FlClash GeoIP/GeoSite/GeoData 更新链路静态审计（2026-09-09）

- 审计对象为固定归档 `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72`，未启动上游功能、未发起下载、未发送真实 URL、未读取设备数据或使用 ADB。相关源码位于 `core/Clash.Meta/component/updater/update_geo.go`、`core/Clash.Meta/component/resource/vehicle.go`、`core/Clash.Meta/component/geodata/init.go`、`lib/providers/actions/geo_resource.dart`。
- `update_geo.go:52-194` 的 MMDB、ASN、GeoIP、GeoSite 更新均从 `geox-url` 解析出的 URL 创建 HTTP vehicle，读取既有文件 hash，使用 `defaultHttpTimeout=90s` 的 context、2xx 状态检查和内存 hash 去重；GeoIP/GeoSite 在写入前调用 standard loader 的 `LoadIPByBytes`/`LoadSiteByBytes`，MMDB/ASN 使用 MaxMind reader 解析。相同 hash 只刷新 mtime 并返回 skipped。
- `resource/vehicle.go:122-172` 支持调用方 context、ETag（仅全局开关打开时）和可选 `sizeLimit`，但本更新路径传入 `sizeLimit=0`、空 header 和空 proxy；响应正文通过 `io.ReadAll` 完整进入内存。固定路径未见 Content-Type 校验、响应长度上限、签名/外部 hash、版本 provenance、来源绑定或独立取消 token。
- `resource/vehicle.go:38-47,114-116` 的 `safeWrite` 创建父目录后直接 `os.WriteFile` 覆盖目标，未见临时文件、fsync、原子 rename、read-back 或失败回滚。定时更新失败会返回包装错误并发送 `sendGeoUpdateStatus`，但未形成持久化、逐资源 terminal receipt。
- 初始化路径 `geodata/init.go:71-87` 也直接打开目标并 `io.Copy`；不存在目标或校验失败时，`initGeoSite`、`initGeoIP`、`initASN` 分支会直接下载覆盖，部分无效文件路径先 `os.Remove` 旧文件再下载。下载失败因此可能留下缺失或部分文件。`InitGeo*` 使用每资源 mutex；`UpdateGeoDatabases` 使用进程级 `updatingGeo` 布尔防重入，并通过 `errgroup` 并行更新启用资源。自动更新 ticker 以四类资源中首个存在文件的 mtime 判断整体更新时间，未见跨进程恢复或每资源来源锁定。
- Flutter `GeoResourceAction.updateGeoResource()` 仅把资源名转发到 `coreController.updateGeoData()`；`updateGeoResourceUrl()` 只做 URL 形状校验并写入 patch config，没有稳定的成功、失败、取消或来源验证回执。
- 结论：该上游具备可配置 URL、超时、hash 去重和内容解析校验，但不满足 XToolpro 要求的有界响应、可取消下载、原子发布、旧数据保留、版本/来源 provenance 与稳定 terminal receipt。对应 parity 行保持 `Partial`，Proxy 台账保持 `Investigating`，不得据此进入正式 engine 集成。

### FlClash rule-provider 更新与命中边界静态审计（2026-09-09）

- 审计对象仍为固定归档 `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72`，未发起网络请求、未读取设备数据、未使用 ADB。相关源码为 `core/Clash.Meta/rules/provider/parse.go`、`core/Clash.Meta/rules/provider/provider.go`、`core/Clash.Meta/component/resource/fetcher.go`、`core/Clash.Meta/component/resource/vehicle.go`、`core/Clash.Meta/rules/provider/rule_set.go`。
- `rules/provider/parse.go` 的 provider schema 支持 `file`、`http`、`inline` vehicle，以及 HTTP 的 URL、proxy、header、interval、`size-limit`、bundle fallback 和可选安全路径；HTTP 缓存路径默认由 URL hash 派生，显式 `path` 仍经 `C.Path.IsSafePath` 检查。
- `component/resource/fetcher.go` 为远端 provider 建立可取消 context，按 interval 周期拉取并以 backoff 缩短失败重试；`Initial()` 按本地文件、bundle 文件、远端更新顺序回退。`loadBuf()` 先对完整 buffer 执行 parser，只有解析成功后才调用 vehicle.Write，因此 parser 失败通常不会替换旧规则；相同 hash 只刷新 mtime。关闭 provider 会取消 fetcher context 和 watcher。
- `component/resource/vehicle.go` 的 HTTP 读取支持 context timeout、2xx 检查和可选 `sizeLimit`，但 `io.LimitReader` 到达上限时不报告截断，且规则 provider 默认配置可不设上限。成功内容仍完整进入内存并由 `safeWrite` 直接 `os.WriteFile` 覆盖，未见临时文件、fsync、原子 rename、read-back、签名/版本 provenance 或旧文件备份。
- `rules/provider/provider.go` 在更新回调中直接替换 provider strategy 并发出全局 `RuleUpdateCallback`；固定路径未见 provider 级读写锁、逐项 terminal receipt、稳定错误码或 UI 取消状态。`rule_set.go` 在 provider 名称不存在时返回 `(false, "")`，未区分 provider 缺失、未初始化、更新失败和规则未命中。
- 结论：上游 rule-provider 具备 parser-before-write、bundle/local fallback、取消 context 和失败 backoff，但仍缺少严格超限检测、原子发布、来源/版本证明、命中原因回执及缺失/失败状态分类。对应 parity 行继续保持 `Partial`，不得把 provider API JSON 的 `ruleCount/updatedAt` 视为经过 XToolpro 审计的 terminal receipt。

### FlClash external-controller 管理面与认证边界静态审计（2026-09-09）

- 补充核验：`startUnix` 与 `startPipe` 均以空 secret 调用同一 `router`，因此 Unix socket/named pipe 管理面绕过 `Authorization: Bearer` 与 websocket query token 校验；Unix socket 随后以 `os.Chmod(addr, 0o666)` 暴露 socket 文件。固定 `core/Clash.Meta/hub/route/server.go` SHA-256 为 `15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。本轮仅只读源码，未创建 socket、未请求管理 API、未读取设备配置或 secret；新增 parity 行保持 `Partial`。

- 审计对象仍为固定归档 `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72`，未启动管理面、未请求 API、未读取设备配置/secret、未使用 ADB。相关源码为 `core/Clash.Meta/hub/route/server.go`、`core/Clash.Meta/config/config.go`、`lib/common/task.dart`。
- `hub/route/server.go:92-135,162-245,249-325` 可并行创建 TCP、TLS、Unix socket 和 named pipe server；HTTP/TLS 共享同一个 router，路由包含 configs、proxies、groups、rules、connections、providers、cache 等管理面。
- `authentication(secret)` 仅在 `secret != ""` 时挂 middleware；普通 HTTP 使用 `Authorization: Bearer <secret>`，websocket 在缺少自定义 header 时接受 query `token=<secret>`。secret 为空时整套路由无鉴权。比较使用 constant-time compare，但未见 secret 轮换、过期、权限分级、审计事件或限流合同。
- `config.go` 默认 `ExternalControllerCors` 为 `AllowOrigins=["*"]` 和 `AllowPrivateNetwork=true`；debug 模式还会挂载 `/debug` profiler。配置模型虽支持 external-controller、TLS、Unix/pipe、CORS、client-auth、routing mark 和 secret，固定 Flutter `lib/common/task.dart:115` 只把 external-controller 地址写入 raw config，未提供 XToolpro 所需的最小 bind scope、管理面 allowlist、TLS/client-cert 健康回执或稳定错误类别。
- 结论：该上游管理面具备 secret Bearer/query-token 认证和多种本地传输入口，但默认 CORS/私网策略宽泛，空 secret 会关闭鉴权，且 Android bridge 未形成可验证的 controller 绑定范围、secret 生命周期、权限分级或 terminal health contract。对应 parity 行保持 `Partial`，不得将 external-controller 认证外推到 mixed proxy inbound 或 LAN sharing。

### FlClash external-controller 重建并发与失败回执静态审计（2026-09-09）

- 固定 `hub/hub.go` SHA-256 为 `A92B15683A6FF7245F5D34F36EA99D58AC9F38B6293DDE9B34EAB9620716CCD4`；`ApplyConfig` 将 controller 配置传给 `route.ReCreateServer`，后者以四个 goroutine 并发调用 `start`、`startTLS`、`startUnix` 和可选 `startPipe`。每个 `start*` 都先关闭各自全局 server 指针，再异步监听并覆盖指针；监听、证书、目录、socket 或 `Serve` 失败只写日志/返回，无跨 transport 聚合结果、generation、串行化或 terminal receipt。本轮只读源码，未启动管理面、未改配置、未使用 ADB；新增 parity 行保持 `Partial`。

### FlClash external-controller `/restart` 完成与失败回执静态审计（2026-09-09）

- 固定 `core/Clash.Meta/hub/route/restart.go` SHA-256 为 `0F3B70A1B7A23283882F41DBE2333042965DF4F5FE1F889F9A3AB5FB291ED5E9`。`restart` 在 `os.Executable()` 成功后立即返回 JSON `{"status":"ok"}` 并 flush，再以后台 goroutine 调用 `executor.Shutdown()` 和 `syscall.Exec`（Windows 为 `cmd.Start`）；执行失败只调用 `log.Fatalln`，没有可等待的完成回执、失败分类、旧实例保留、回滚或重启后健康确认。本轮未调用 `/restart`、未启动/停止 core、未改配置、未使用 ADB；新增 parity 行保持 `Partial`。

### FlClash external-controller debug 路由鉴权边界静态审计（2026-09-09）

- 固定 `core/Clash.Meta/hub/route/server.go` SHA-256 为 `15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。`router` 先在 secret 保护的管理 group 外挂载 `/debug`，debug 模式下提供 profiler 与 `PUT /debug/gc`；后续 group 才按非空 secret 挂载 `authentication()`。因此 debug 路由不继承 controller Bearer/query-token 鉴权。本轮未启用 debug、未请求路由、未读取设备数据或配置；新增 parity 行保持 `Partial`。

### FlClash proxy-provider 订阅、健康检查与 API 字段边界静态审计（2026-09-09）

- 审计对象仍为固定归档 `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72`，未发起订阅请求、未读取节点/配置/设备数据、未使用 ADB。相关源码为 `core/Clash.Meta/adapter/provider/provider.go`、`core/Clash.Meta/adapter/provider/healthcheck.go`、`core/Clash.Meta/hub/route/provider.go`。
- `adapter/provider/provider.go` 使用 `resource.Fetcher[[]C.Proxy]`，更新 context 可取消；parser 成功后才由 `setProxies` 在 RWMutex 下替换代理列表并递增 version，解析失败保留旧列表。HTTP response header 的 `subscription-userinfo` 会写入 cachefile 并解析为 upload/download/total/expire 摘要。
- `providerForApi` 的 JSON 包含完整 `[]C.Proxy`、provider `testUrl`、expected status、更新时间和 subscription info；`hub/route/provider.go` 暴露 `GET /providers/proxies`、单 provider `GET`、`PUT` 更新、healthcheck 及单节点 healthcheck 路由。更新失败返回 `503` 和原始 `err.Error()`，健康检查只返回空正文成功，不提供逐节点 `Success`/`Cancelled`/`Timeout`/`EngineCrashed` 回执。
- `healthcheck.go` 使用 provider 级 singleflight 防重复，errgroup 最多 10 个并发，单代理使用 context timeout（默认 5 秒）；自动检查 ticker、lazy touch 和 extra URL/filter 会改变执行集合。日志直接拼接 proxy name、测试 URL、alive 和 delay，固定路径未见字段最小化、URL 脱敏或日志开关边界。
- 结论：上游具备可取消拉取、解析后替换、RWMutex/version 和并发健康探测，但管理 API 和日志仍会暴露节点对象、测试 URL、subscription usage 与原始错误/探测字段，且缺少稳定的逐节点 terminal contract。对应 parity 行保持 `Partial`，不得将 provider version、healthcheck HTTP 204 或 API JSON 当作 XToolpro 的 engine health proof。

### FlClash 原生 START/STOP 与 VPN 标记/TUN 收敛受限复核（2026-09-09）

- 设备为已授权测试设备 `bf353dda`；仅使用已解析的 dev 组件 `com.follow.clash.dev/com.follow.clash.QuickActionActivity` 和固定 action `com.follow.clash.dev.action.START`/`STOP`。每次 mutation 前后只采集允许的进程存在性、`POST_NOTIFICATION` app-op、系统 `VPN CONNECTED` 标记计数和所有 `tun*` 接口计数；未读取日志、通知正文或 extras、配置、节点、请求、路由、DNS、文件、凭据、Cookie、URL 或流量。
- 操作前停止基线为 `vpn_connected_markers=0`、`all_tun_interfaces=0`、dev 进程存在、`POST_NOTIFICATION=allow`。发送 START 后等待 7 秒，结果为进程存在、app-op 仍为 `allow`、`vpn_connected_markers=1`，但 `all_tun_interfaces=0`；随后发送同一 STOP 并等待 7 秒，结果为 `vpn_connected_markers=0`、`all_tun_interfaces=0`，进程和 app-op 不变。
- 该轮没有发起流量，也没有把系统 VPN 标记解释为 TUN、core 或转发成功。它仅证明该 action 序列的系统标记可收敛；START 的标记/TUN 分离再次说明 UI/系统标记、service 请求和实际 TUN 健康不是同一完成回执。对应矩阵行保持 `Partial`，Phase 02 gate、Proxy 台账 `Investigating` 和 `engine-proxy` 未变。

### FlClash 快速双 TOGGLE 最终状态受限复核（2026-09-09）

- 设备为已授权测试设备 `bf353dda`；使用已解析的 dev 组件 `com.follow.clash.dev/com.follow.clash.QuickActionActivity` 连续发送两次 `com.follow.clash.dev.action.TOGGLE`，随后发送同一组件的 `STOP` 恢复基线。每次 mutation 前后只采集进程存在性、`POST_NOTIFICATION` app-op、系统 `VPN CONNECTED` 标记计数和所有 `tun*` 接口计数。
- 操作前停止基线为 `vpn_connected_markers=0`、`all_tun_interfaces=0`、dev 进程存在、`POST_NOTIFICATION=allow`。两次 TOGGLE 后等待 7 秒，仍为 `vpn_connected_markers=0`、`all_tun_interfaces=0`，进程和 app-op 不变；随后 STOP 等待 7 秒，摘要保持不变。
- 本轮未发送流量，未读取日志、通知正文或 extras、配置、节点、请求、路由、DNS、文件、凭据、Cookie、URL 或流量。结果只支持该次偶数切换最终保持停止摘要，不证明两个 action 的线性化、中间状态、幂等性、资源释放或 core health；对应矩阵行保持 `Partial`，Phase 02 gate 不变。

### FlClash 重复 STOP 请求最终收敛受限复核（2026-09-09）

- 设备为已授权测试设备 `bf353dda`；使用已解析的 dev `QuickActionActivity` 发送 START，等待 7 秒后连续发送两次同一 `STOP` action。每次 mutation 前后只采集进程存在性、`POST_NOTIFICATION` app-op、系统 `VPN CONNECTED` 标记计数和所有 `tun*` 接口计数。
- 操作前停止基线为 `vpn_connected_markers=0`、`all_tun_interfaces=0`、dev 进程存在、`POST_NOTIFICATION=allow`。START 后 7 秒为 `vpn_connected_markers=1`、`all_tun_interfaces=0`；两次 STOP 后 7 秒回到 `vpn_connected_markers=0`、`all_tun_interfaces=0`，进程和 app-op 不变。
- 本轮未发送流量，未读取日志、通知正文或 extras、配置、节点、请求、路由、DNS、文件、凭据、Cookie、URL 或流量。结果只证明该轮最终停止摘要收敛，不能证明 STOP 请求幂等、内部 callback 次数、资源销毁或 core health；对应矩阵行保持 `Partial`，Phase 02 gate 不变。

### FlClash 快速 START→STOP 取消收敛受限复核（2026-09-09）

- 设备为已授权测试设备 `bf353dda`；在停止基线对已解析 dev `QuickActionActivity` 连续发送 START 后立即发送 STOP。7 秒、14 秒及最终再次 STOP 后 5 秒，仅采集进程存在性、`POST_NOTIFICATION` app-op、系统 `VPN CONNECTED` 标记计数和所有 `tun*` 接口计数。
- 操作前为 `vpn_connected_markers=0`、`all_tun_interfaces=0`、dev 进程存在、`POST_NOTIFICATION=allow`。立即 START→STOP 后 7 秒和 14 秒均为 `vpn_connected_markers=1`、`all_tun_interfaces=0`，进程和 app-op 不变；再次发送 STOP 后 5 秒回到 `vpn_connected_markers=0`、`all_tun_interfaces=0`。
- 本轮未发送流量，未读取日志、通知正文或 extras、配置、节点、请求、路由、DNS、文件、凭据、Cookie、URL 或流量。结果表明已提交 STOP 与最终系统停止摘要存在可观察延迟/竞态，不能证明取消已覆盖 setup、service callback 或 core health；对应矩阵行保持 `Partial`，Phase 02 gate 不变。

### FlClash 快速 STOP→START 启动收敛受限复核（2026-09-09）

- 设备为已授权测试设备 `bf353dda`；在停止基线对已解析 dev `QuickActionActivity` 连续发送 STOP 后立即发送 START。7 秒、14 秒及最终再次 STOP 后 5 秒，仅采集进程存在性、`POST_NOTIFICATION` app-op、系统 `VPN CONNECTED` 标记计数和所有 `tun*` 接口计数。
- 操作前为 `vpn_connected_markers=0`、`all_tun_interfaces=0`、dev 进程存在、`POST_NOTIFICATION=allow`。立即 STOP→START 后 7 秒和 14 秒均为 `vpn_connected_markers=1`、`all_tun_interfaces=0`；再次 STOP 后 5 秒回到 `vpn_connected_markers=0`、`all_tun_interfaces=0`，进程和 app-op 不变。
- 本轮未发送流量，未读取日志、通知正文或 extras、配置、节点、请求、路由、DNS、文件、凭据、Cookie、URL 或流量。结果只证明后到 START 可保留系统标记，不能证明实际 TUN/core 运行、命令串行化或取消覆盖；对应矩阵行保持 `Partial`，Phase 02 gate 不变。

### FlClash 重复 START 请求最终收敛受限复核（2026-09-09）

- 设备为已授权测试设备 `bf353dda`；从停止基线对已解析 dev `QuickActionActivity` 连续发送两次 START，等待 7 秒和 14 秒后发送 STOP 恢复基线。每次 mutation 前后只采集进程存在性、`POST_NOTIFICATION` app-op、系统 `VPN CONNECTED` 标记计数和所有 `tun*` 接口计数。
- 操作前为 `vpn_connected_markers=0`、`all_tun_interfaces=0`、dev 进程存在、`POST_NOTIFICATION=allow`。重复 START 后 7 秒和 14 秒均为 `vpn_connected_markers=1`、`all_tun_interfaces=0`；STOP 后 5 秒回到 `vpn_connected_markers=0`、`all_tun_interfaces=0`，进程和 app-op 不变。
- 本轮未发送流量，未读取日志、通知正文或 extras、配置、节点、请求、路由、DNS、文件、凭据、Cookie、URL 或流量。结果只支持系统标记最终可清理，不证明启动请求去重、core/listener 次数、资源释放或实际 TUN 健康；对应矩阵行保持 `Partial`，Phase 02 gate 不变。

### FlClash 运行态快速双 TOGGLE 受限复核（2026-09-09）

- 设备为已授权测试设备 `bf353dda`；先向已解析 dev `QuickActionActivity` 发送 START，等待 7 秒，再连续发送两次 TOGGLE，最后发送 STOP。每次 mutation 前后只采集进程存在性、`POST_NOTIFICATION` app-op、系统 `VPN CONNECTED` 标记计数和所有 `tun*` 接口计数。
- 停止基线为 `vpn_connected_markers=0`、`all_tun_interfaces=0`、dev 进程存在、`POST_NOTIFICATION=allow`。START 后 7 秒为 `vpn_connected_markers=1`、`all_tun_interfaces=0`；双 TOGGLE 后 7 秒仍为 `vpn_connected_markers=1`、`all_tun_interfaces=0`；STOP 后 5 秒回到 0/0，进程和 app-op 不变。
- 本轮未发送流量，未读取日志、通知正文或 extras、配置、节点、请求、路由、DNS、文件、凭据、Cookie、URL 或流量。结果只证明该轮运行态偶数切换保留系统标记并可由 STOP 清理，不能证明实际 TUN/core 运行、线性化、中间反向状态或资源安全；对应矩阵行保持 `Partial`，Phase 02 gate 不变。

### Phase 02 parity gate 计数一致性复核（2026-09-09）

- 对 `docs/architecture/upstream-capability-parity-matrix.md` 的状态列做只读正则统计，当前为 `Verified=4`、`Partial=56`、`Pending=29`、`Unavailable=1`、`Blocked=0`。矩阵顶部汇总已同步为该数值；此前 evidence 中的 `Partial=51` 与矩阵顶部旧值 `Partial=54` 均保留为历史 checkpoint，不作为当前 gate。
- 四域 upstream ledger 仍为 `Investigating`，`engine-proxy` 仍只有壳模块和测试计划；本次只修正文档计数，没有改变任何能力状态、台账状态、实现或集成范围。Phase 02 acceptance gate 继续不通过。

### FlClash 强停后 launcher 冷启动无意恢复受限复核（2026-09-09）

- 设备为已授权测试设备 `bf353dda`；对 `com.follow.clash.dev` 执行强停，等待 3 秒后以 launcher 启动包，等待 8 秒，再次强停恢复停止基线。只采集进程存在性、`POST_NOTIFICATION` app-op、系统 `VPN CONNECTED` 标记计数和所有 `tun*` 接口计数。
- 强停后 3 秒为进程不存在、`vpn_connected_markers=0`、`all_tun_interfaces=0`；launcher 启动后 8 秒为进程存在但 `vpn_connected_markers=0`、`all_tun_interfaces=0`；最终强停后 3 秒保持全 0，app-op 始终为 `allow`。
- 本轮未发送流量，未读取 UI、日志、通知正文或 extras、配置、节点、请求、路由、DNS、文件、凭据、Cookie、URL 或流量。结果只支持本轮无新 START 请求时未观察到 VPN/TUN 意外恢复，不替代 crash/reboot/always-on 或 core health proof；对应矩阵行保持 `Partial`，Phase 02 gate 不变。

### FlClash DNS/Fake-IP cache 持久化与 DNS 日志边界静态审计（2026-09-09）

- 审计对象为固定归档 `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72`，未启动 core、未发起 DNS/网络请求、未读取设备数据或使用 ADB。关键文件及 SHA-256：`core/Clash.Meta/dns/client.go` `9EC3B48BCA8954EFF66C4A45A9275A7FA803186857789E45FB91F723689889D5`；`dns/util.go` `963C52846ED690B96C60CFDCB7F900E35FBE8BE8481CEA80B016C37B9AC374F3`；`component/fakeip/pool.go` `99E1323AF5E5D25013F08A1E63201463E86319859FAF892B4F419196BB134817`；`component/fakeip/cachefile.go` `D2B685FEA40EE7795ED5163EFA9BA784AFA06071EB488011F17FD0F1AA5ED749`；`component/profile/cachefile/cache.go` `F78DB1582F01FAAFF1BB6260398A2A747809ABFF30C67EE03AC0F477ADE3227B`。
- `fakeip` 的 cachefile store 将 host↔fake-IP、IPv4/IPv6 pool 偏移和 cycle marker 委托给 profile bbolt cache；`cache.go` 以 `0666` 打开 cache，`bbolt.Batch` 写 selected/cache bucket，遇到 `ErrInvalid`、checksum 或 version mismatch 时先 `os.Remove(C.Path.Cache())` 再重建。固定路径没有应用层加密、同目录 staging/read-back、旧 cache 保留或恢复 receipt；删除/重建失败会把 cache path 和原始 error 送入 warning log。
- DNS client 使用 `DialContext` 和 5 秒 DNS client timeout，但固定 `miekg/dns ExchangeWithConn` 路径的取消语义受库限制；`dns/client.go`、`dns/util.go` 的 debug/warn 模板包含 DNS server address、query name、ECS prefix、截断重试和 cache/ACME 分支信息。未见统一字段脱敏、DNS query 日志开关边界、cache 清除/过期 terminal receipt 或独立 Fake-IP/Host/sniffing health/capability API。
- 结论：上游具备 DNS transport、有限 timeout、内存/持久 Fake-IP 映射和 5 分钟 server-failure cache 规则，但 cache 文件权限、错误恢复和 DNS/Fake-IP 诊断字段不满足 XToolpro 的最小化、加密、可回滚和稳定错误合同。未执行真实解析、Fake-IP 冲突、cache 损坏、取消或嗅探验证；对应矩阵行保持 `Partial`，Proxy 台账和 Phase 02 gate 不变。

### FlClash HTTP/TLS/QUIC sniffing dispatcher 边界静态审计（2026-09-09）

- 固定来源关键文件及 SHA-256：`component/sniffer/dispatcher.go` `4B04CBFA8D4A21C5C305DEB6E8E5B8719D6FC047CB3F7A176C6805C7C2045B81`；`base_sniffer.go` `B11A1E3C557C8F38ECA3C1C289E12313BB83641D9874F987363AF41D09238C20`；`http_sniffer.go` `383EFD22A42E50AF06C62B5C4C833096DDBF1307156CCB075A27F11EFBCD79F2`；`tls_sniffer.go` `CE8EF5E462226143F8950F3F9974960141CDCF8C077406A7A7F7F5CD7546045E`；`quic_sniffer.go` `C451AC34D588B2FD180DAC75A2EC3904CC3274902AB72A9FF8451DA7430EA5E1`。本轮只读固定归档，未启动 core、未发送流量或读取设备数据。
- dispatcher 支持 HTTP/TLS/QUIC sniffer，按 network/port、源/目的地址、skip/force domain、`forceDnsMapping` 和 `parsePureIp` 决定是否读取首包；成功后会改写 `Metadata.SniffHost`、`Host` 和必要时 `DstIP`/`DNSMode`，失败会按目的地址写入 128 项、600 秒 LRU skip cache。未知 sniffer 类型会返回 `unsupported sniffer`，全部 sniffer 失败返回 `all sniffer failed`，但未形成 XToolpro 可消费的 capability、取消、超时或 terminal receipt。
- debug/error 日志模板包含源/目的地址、目标/嗅探 host、sniffer protocol、首包不足和失败原因；固定路径未见统一字段脱敏、首包大小/时间预算的外部合同或 sniff 数据生命周期清理。该能力可以改变规则匹配的 host/destination 语义，不能以“sniffer 代码存在”推断已启用、命中或安全。
- 结论：上游具备协议级嗅探与有限失败退避，但 XToolpro 仍需默认关闭敏感 host/address 诊断，显式声明可探测能力和取消/超时/失败分类，并在受控 fixture 覆盖 HTTP/TLS/QUIC、skip/force、DNS mapping、失败缓存和 metadata 改写前保持 `Partial`。

### FlClash platform hosts resolver 加载与空结果边界静态审计（2026-09-09）

- 固定来源文件为 `core/Clash.Meta/component/resolver/hosts/hosts.go`，SHA-256 `2EA91E5A1F9E06D848A941AA6573F87AF0D50229C9DC6109ABFF2749D6F7A5F6`；Windows 路径适配文件为 `hosts_windows.go`，SHA-256 `F791005F68CDF1C30E48B0966CB89689F346D3DD03AACA60E94F97A268DFD2B5`。本轮只读固定归档，未读取任何设备或系统 hosts 文件，也未启动 core 或发起网络请求。
- resolver 以平台 hosts 路径为输入，按 mtime/size 和 5 秒 `cacheMaxAge` 缓存完整的 host→IP、IP→host 映射；解析忽略注释、无效 IP 和格式不足行，查询返回防御性副本。读取文件时 `not found`/`permission denied` 不报错，直接写入空映射；其他 open/stat/read failure 同样没有稳定错误、用户可见 unavailable、来源/version 或 terminal receipt。
- 固定实现未见文件大小/行数预算、canonical provenance、显式 cache 清除/过期回执或针对 hosts 内容的字段最小化合同。虽然此组件本身没有新增日志调用，但 host/IP 映射会进入 resolver/routing metadata，不能把“静默空结果”误认作不存在 hosts 覆写或功能成功。
- 结论：上游可使用 platform hosts 作为 DNS/路由输入，但 XToolpro 必须在隔离边界提供最小化、可撤销的 hosts 配置、读取失败的稳定状态和缓存生命周期；完成受控 hosts fixture、缺失/权限失败、超限、更新、清除、取消和 version mismatch 契约前，对应矩阵行保持 `Partial`。

### FlClash DoH/DoT/DoQ DNS transport 静态审计（2026-09-09）

- 审计对象为固定归档 `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72`，只读 `core/Clash.Meta/dns/doh.go`（SHA-256 `4B37A02973B0E97C94FEEDCF1979B9E821D798BC541ED95B7C474EC50005CA24`）、`dot.go`（`02FB4DA8A303AC26E68C591A48ADB931D6ACB09ED7F2E7DB87A5C6B2448B788C`）和 `doq.go`（`0E41381374782B541AA875AD1D3751D25F7151823C061E2FDD7221327C413588`）；未启动 core、未发起 DNS/网络请求、未读取设备数据或使用 ADB。
- DoH 使用 context-bound HTTP 请求、10 秒拨号/TLS 握手预算、5 分钟 idle/30 秒 ping 参数、TLS 1.2 minimum、连接复用与超时/QUIC/0-RTT reset 后最多两次重建重试；DoT/DoQ 使用 context-aware 拨号、默认 5 秒 DNS timeout、复用连接失败重试和显式 Reset/Close，DoQ 区分 idle/stateless reset 等可重试错误。三者均接受 `skip-cert-verify` 与 `name-cert-verify` 参数。
- 固定实现没有 XToolpro-facing 证书策略、取消/超时/错误分类、每上游 health/capability 或终端回执；连接/拨号错误日志可能包含上游地址、耗时和原始错误。未执行真实 DoH/DoT/DoQ、证书失败、取消、重试或隐私边界验证。
- 结论：上游具备三类加密 DNS transport、有限超时和连接重建能力，但这些实现不能直接证明 XToolpro 的证书策略、可取消性、稳定错误/健康合同或脱敏边界；对应矩阵行保持 `Partial`，Proxy 台账与 Phase 02 gate 不变。

### FlClash 应用更新 metadata 与 artifact 信任边界静态审计（2026-09-09）

- 审计对象为固定归档 `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72`，只读 `lib/common/request.dart`（SHA-256 `8073CEFF3D34FF7F2487AA0BE2EC8309AD3A68B5CFB63C0C8404ADB634E09095`）、`lib/views/about.dart`（`671166BF7057D4C3E7DC29DC86DF9E694EA9678443809683BEE05052CCE5CBCF`）和 `lib/providers/actions/common.dart`（`9AA4F5391C40485EC1A869B5329A6D0979AE458C5389DBF298BA0ABB879EAD6B`）；未发起 GitHub 请求、未下载 release asset、未安装 APK、未读取设备数据或使用 ADB。
- `checkForUpdate()` 只调用固定 GitHub `releases/latest` URL，检查 HTTP 200，读取 `tag_name`，与本地 `packageInfo.version` 比较；异常被记录为无敏感的固定 warning 文本并返回空结果。更新提示只解析 release body 摘要；用户确认后打开固定 GitHub latest 页面，自动检查被拒绝后可关闭自动检查偏好。固定路径不选择或下载 asset。
- 未发现 artifact 签名/哈希、来源绑定、staging、ABI/版本兼容校验、安装回滚或 update terminal receipt；“已是最新版”只代表 metadata 比较结果，不能证明可安装 artifact 或 native core 兼容。未执行真实更新、恶意/错误 metadata、取消、下载失败、安装失败或回滚验证。
- 结论：固定上游仅提供 release metadata 提示和外部页面跳转，不能作为 XToolpro 的更新信任链或回滚证明；对应矩阵行保持 `Partial`，Proxy 台账与 Phase 02 gate 不变。

### FlClash provider 协议/地区/标签筛选语义静态审计（2026-09-09）

- 审计对象为固定归档 `FlClash-62addf738a76b1a492e19af2dbabdb6d572b9e72`，只读 `core/Clash.Meta/adapter/provider/provider.go`（SHA-256 `546ED6CDAC36E1BA14F619C065F4BF59F2CBAB7E89DC25BC56E04F40C6B290B`）、`parser.go`、`lib/views/proxies/proxies.dart`（`271197BA005F48A922046A4256AA2485734D65EB6064707DCE6AFF5C9136249F`）和 `lib/views/proxies/list.dart`（`F54613F01F8A718557B119A958A708292AF73F1E1CCB889B05510136FB73A79E`）；未启动 core、未读取设备代理数据或使用 ADB。
- `NewProxiesParser` 将 `filter`/`exclude-filter` 按反引号拆分为正则，先按 `exclude-type` 不区分大小写排除协议类型，再按代理 `name` 应用排除/包含规则；重复名称去重，正则语法错误、override 错误或单项代理解析错误会令 provider parse 失败。无匹配时分别返回 filter 不匹配或无代理错误。
- Flutter 代理页只有通用搜索输入、布局/展开与 provider 入口；固定源码未形成协议、地区、标签的独立筛选模型、结果原因、空态/重试或 terminal receipt。未执行恶意正则、空结果、重复名称或真实 provider fixture 验证。
- 结论：上游存在基于类型/名称的 provider parser 过滤能力，但不能证明 FlClash Android UI 提供用户级协议/地区/标签筛选，也不能直接作为 XToolpro 的筛选合同；对应矩阵行保持 `Partial`，Proxy 台账与 Phase 02 gate 不变。

### FlClash 代理组策略、健康选择与空成员边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`parser.go`=`1F7D0AE433C0DCC501D8354646D7BDD4685B8B170426F19831C8A509E1638DEA`；`groupbase.go`=`13964372390663420A63BD06C787A083CA40712CE52E5F2B1739AFEEA2F08EC2`；`selector.go`=`682FCEF95A0E9D7F97FB0ADDA6A72C82F772839ADC0C1E5FFB4A6D51B29AF1B9`；`urltest.go`=`5970E1AB00F269C53108213F677702F00B8F690E06A5880E74F58F0AFDEC7E36`；`fallback.go`=`EA6FDC60688ABF5813A77E25D8A6E16874B21F41FA0E4DA8B51465021DC2E7F5`；`loadbalance.go`=`DD8E5C052E614F744E810778B88234E85721BF91CA5FA62A2182120376D87270`；`healthcheck.go`=`5D839304F21C054811B4EF4E34C8DAE1FB8EC7BF0A36CE877CD5B6D581D3A4AA`。
- `ParseProxyGroup` 只注册 `url-test`、`select`、`fallback`、`load-balance`；`relay` 返回已移除错误。`GroupBase.GetProxies` 应用 filter/exclude 规则并按 provider version 缓存；空结果返回 `empty-fallback`，默认名为 `COMPATIBLE`。
- `select` 只接受现有成员，选择名不存在时使用首项；`url-test` 以 10 秒 singleflight 缓存健康选择，支持 fixed selection 与 tolerance；`fallback` 优先健康成员，固定成员失效后清除并回退首项；`load-balance` 支持 consistent-hashing、round-robin、sticky-sessions，可用成员耗尽时回退首项且 `Now()` 为空。
- 组级失败计数默认达到 5 次后触发健康检查；provider 检查按 interval 周期运行、lazy 模式可跳过、每轮最多 10 并发且每代理默认 5 秒 timeout。未见空 provider、全成员失败、超时、选择失败或 core 同步失败的稳定用户状态、取消/回滚回执；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。
- 本轮未使用 ADB，未读取设备 UI、配置、节点、订阅 URL、凭据、Cookie、日志或网络内容；未修改 SDK、缓存、上游归档或构建产物。

### FlClash 实时流量、连接快照与详情字段静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/tunnel/statistic/manager.go`=`D6633D8CA012523924C7BB8103800189679EF2E127524C1EFFFDFDDF40FFDB3F`；`tracker.go`=`5F82775600E8F068D53C51A543601BAFE71B7890F422C67B6E92543393C30943`；`hub/route/connections.go`=`FBE4948D9B506F661E4AC5936E493F82AFC1FB2F1DC84DB8BA7FF108423AD83A`；`hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`；`lib/models/common.dart`=`A9188ABFDC22B3935AB8B63F17DFBE8C09E474561C2E2A8CEFFB0F425A450BB5`；`lib/views/dashboard/widgets/network_speed.dart`=`54A288630E3900BE129FA7EAF6CED4E4B37AB3EA7933A1CA32EB792C25032860`；`lib/views/dashboard/widgets/traffic_usage.dart`=`1D6D2C7A15DD46EA5EA37E8F805851B53BF1CA2B5D72C99C82A20D174BE6AC39`；`lib/views/connection/connections.dart`=`33680C6754F305A5550867AC6CEC587814D4CB3486267E8D2CD0B0A1D3A6D8BC`；`lib/views/connection/item.dart`=`EB8B5A571B5388BD9D55DF8DBFBE4EC56DA8F18305DB32F1895DCF5672753815`。
- `statistic.Manager` 维护瞬时速率与累计字节，`PushUploaded/PushDownloaded` 对 DIRECT 链路和 proxy 链路分别累计；`Snapshot()` 聚合当前连接及上下行累计值。`/traffic` 使用 1 秒 ticker 推送 `up/down/upTotal/downTotal`，`/connections` 的 REST 请求返回一次 snapshot，WebSocket 默认每 1000ms 推送并接受自定义 `interval`；单条和全部关闭调用 tracker `Close()` 后返回无正文 204，未提供逐项失败/取消结果。
- Flutter `Traffic`/dashboard provider 消费速率和累计值；连接页通过 polling reader 刷新列表，支持清空全部和关闭单条。`TrackerInfo`/`Metadata` 与详情页直接覆盖 UID、进程/路径、网络、host、源/目的 IP/端口、规则 payload、GeoIP/ASN、DNS mode、special proxy/rules、remote destination、上下行字节和代理链；列表搜索只覆盖部分字段。该字段集合和原始错误写日志的路径尚无脱敏/最小化边界，且关闭、重置流量与请求缓存清理并非同一 terminal receipt。
- 本轮仅审计固定源码，未启动 core、未使用 ADB、未读取设备连接正文、地址、节点、配置、订阅 URL、凭据、Cookie、日志或网络内容；对应矩阵行从 `Verified` 调整为 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash rule-provider 车辆与 `/rules` 命中 API 静态审计（2026-09-09）

- 固定归档文件 SHA-256：`rules/provider/parse.go`=`763F74A57B32688AACEA86BA389DE52FDC2F74FB5829874AE296DB6E32C2B472`；`provider.go`=`3B37837D9A3B25CB9BC8A1D06E18E1CDC0143D379AC5000F4FC3B024DC9B7E6A`；`rule_set.go`=`B58CC52A7C44758B06E66118ADF38F9249D42EDE2192078D2758AC2E5F50B267`；`rules/parser.go`=`8B3004C05CA7318E654239920BC876428BFD1F4CEF48E7CF214AC9411D60EBA4`；`hub/route/rules.go`=`2295A989D65BF2F602320506EDDC9A4361BB937CD17F494E5D7CBDD631C9CDB9`。
- 解析器接受 `file`、`http`、`inline` vehicle，yaml/text/mrs 格式以及 `interval`、`size-limit`、header/proxy、bundle fallback；规则 provider 在解析成功后替换 strategy 并触发更新 callback。`ParseRule` 覆盖 DOMAIN/GEOSITE/GEOIP/IP-CIDR/IP-ASN、RULE-SET、AND/OR/NOT 等类型。
- `/rules` 返回 index/type/payload/proxy/size 以及 disabled、hitCount、hitAt；PATCH `/rules/disable` 按 index 改变运行态。RuleSet 找不到 provider 时静默返回 false。固定路径未见签名/来源绑定、原子 staging/read-back、规则版本 hash、逐项更新 receipt、稳定错误分类或 payload/host 脱敏。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未下载或修改规则数据，未读取设备规则、域名、IP、配置、订阅 URL、凭据、Cookie、日志或网络内容；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash `/providers/rules` 管理 API 静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/provider.go`=`6B3A4B59E42AA13E8E04544F3BA269FE9906695B3BCC7BD46B80CEFA4B3DAD80`；`core/Clash.Meta/rules/provider/provider.go`=`3B37837D9A3B25CB9BC8A1D06E18E1CDC0143D379AC5000F4FC3B024DC9B7E6A`；`core/Clash.Meta/rules/provider/parse.go`=`763F74A57B32688AACEA86BA389DE52FDC2F74FB5829874AE296DB6E32C2B472`；`core/Clash.Meta/constant/provider/interface.go`=`01F7FFE95C10540254FC8482C0CE25A651FDF23463410EE72DCD00BBA5191FA9`。
- `server.go` 在 secret 管理 group 下挂载 `GET /providers/rules` 和 `PUT /providers/rules/{name}`。列表响应为 `providers` map；`RuleSetProvider.MarshalJSON` 暴露 `behavior`、`format`、`name`、`ruleCount`、`type`、`vehicleType`、`updatedAt`，inline provider 另外暴露完整 `payload`。找不到 provider 返回 404；`Update()` 成功返回空正文 204，失败返回 503 并包装原始错误文本。
- `ruleSetProvider.Update()` 调用 `resource.Fetcher.Update()`，其 fetch 使用可取消 context，解析成功后替换 strategy 并触发 `RuleUpdateCallback`；`inlineProvider.Update()` 仅刷新时间戳并返回成功，不改变既有 payload。parser 支持 `file`、`http`、`inline` vehicle，校验安全路径，HTTP vehicle 的缓存路径默认按 URL hash 生成。
- 固定路径未见请求体/并发/generation 限制、版本回读、逐项 terminal receipt、稳定错误类别或 payload/来源脱敏；Unix/named-pipe transport 传入空 secret，继承本地管理面未鉴权边界。XToolpro 未来只能在受鉴权 adapter 中复用最小字段，并增加来源/版本绑定、原子 staging、取消传播和脱敏 `Success`/`Unavailable`/`Cancelled`/`EngineCrashed`/`VersionMismatch` 结果。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未发起 provider 更新、未下载或修改规则数据，未读取设备规则、域名、IP、配置、订阅 URL、凭据、Cookie、日志或网络内容；新增 parity 行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，`/providers/rules` 管理 API 静态审计）

- focused commit `aababa7` 已创建但尚未 push；本次仅修改 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

### FlClash rule-provider 自动拉取调度、取消与失败退避静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/component/resource/fetcher.go`=`D6076850B2068A1CA25430B6F2A4CE07CE6E43635726BD9260A84417DE9A4CFB`；`core/Clash.Meta/component/resource/vehicle.go`=`2B5AE763906EF70523A40830FCF04EAF1FE0076F54DCD36AFE4E7033D4327AFF`；`core/Clash.Meta/component/slowdown/backoff.go`=`A3DB7F37CE8C88449AD98F955E0EB03567866147320FEE3799D258101633EFDA`。
- `Fetcher.Initial()` 按本地文件→bundle→远端顺序恢复；即使首次远端更新失败，也会启动 pull loop。file vehicle 通过 fswatch 监听，HTTP/inline 等非 file vehicle 在 `interval>0` 时启动 ticker。失败调用 `Backoff.AddAttempt()`，Factor=2、上限为 interval；成功或 hash 未变化时重置退避。解析成功后才执行 vehicle.Write，再更新 hash/时间并触发 onUpdate，因此解析失败通常保留旧内容。
- `Fetcher` 仅创建内部 `context.WithCancel(context.Background())`，`Close()` 才取消；管理 API 调用的 `Update()` 不接收请求 context，pull loop 也只监听该内部 context。未见启动/关闭幂等、重复 watcher/loop 防护、逐次任务 ID、更新 generation、失败后的旧版本 terminal receipt 或调度持久化；`loadBufMutex` 只覆盖单个 fetcher 的解析/发布，不提供跨 provider 原子快照。
- 本轮仅静态读取固定归档，未启动 core、未触发 watcher/ticker、未发起 provider 更新、未下载或修改规则数据、未使用 ADB，未读取设备规则、域名、IP、配置、订阅 URL、凭据、Cookie、日志或网络内容；新增 parity 行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，rule-provider 自动拉取调度、取消与失败退避静态审计）

- focused commit `bb8e65b` 已创建但尚未 push；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

### FlClash provider ETag/304 缓存与敏感 URL 持久化边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/component/resource/vehicle.go`=`2B5AE763906EF70523A40830FCF04EAF1FE0076F54DCD36AFE4E7033D4327AFF`；`core/Clash.Meta/component/profile/cachefile/etag.go`=`477D1BBBE97EF51DDE00ABEBF7B82293006F60EC34EF4BE54C790914A4670D6A`；`core/Clash.Meta/component/profile/cachefile/cache.go`=`F78DB1582F01FAAFF1BB6260398A2A747809ABFF30C67EE03AC0F477ADE3227B`；`core/Clash.Meta/hub/executor/executor.go` 中 `resource.SetETag(general.ETagSupport)` 的调用将配置开关同步到进程级状态。
- ETag 开启且旧 hash 有效时，HTTP vehicle 只在缓存 hash 与当前 hash 一致并存在 ETag 时发送 `If-None-Match`；304 返回旧 hash，Fetcher 将其视为 same 并刷新时间。2xx 响应则将 ETag、内容 hash 和时间写入 bbolt `etag` bucket。
- `SetETagWithHash` 直接以完整 URL 作为 bbolt key；固定代码未见 URL 脱敏/不可逆标识、TTL、容量/条目上限、配置移除后的清理或逐项 terminal receipt。cache DB 以 `0666` 模式打开，ETag 读取/反序列化错误不向调用方返回，写入失败只记 cache 路径和原始错误，无法区分命中、持久化失败或版本错配。
- 本轮仅静态读取固定归档，未开启 ETag、未发送 HTTP 请求、未读写 cache DB、未启动 core、未使用 ADB，未读取设备规则、配置、订阅 URL、凭据、Cookie、日志或网络内容；新增 parity 行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，provider ETag/304 缓存与敏感 URL 持久化边界静态审计）

- focused commit `c8257a3` 已创建但尚未 push；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入。按要求不得自动 push，须先获得用户明确确认。

### FlClash proxy-provider 过滤、覆盖与 age 解密解析边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/adapter/provider/parser.go`=`47616A987CFDC9E793D7C431F2EB450DB02A3E7EA492E02F1DAA134B00D3CD52`；`core/Clash.Meta/adapter/provider/provider.go`=`546ED6CDACF36E1BA14F619C065F4BF59F2CBAB7E89DC25BC56E04F40C6B290B`。
- `ParseProxyProvider` 支持 file/http/inline vehicle；HTTP vehicle 默认按 URL hash 生成缓存路径，显式 path 需通过 `C.Path.IsSafePath`，header、proxy、size-limit 直接传递给 HTTP vehicle。`NewProxiesParser` 将 filter/exclude-filter 按反引号拆分并编译 regexp2，随后应用 exclude-type、名称筛选、`dialer-proxy`、override 和 `adapter.ParseProxy`；支持 `age-secret-key` 校验与解密，YAML 失败时回退 V2Ray 转换。
- 无匹配或无 proxies 返回普通错误；错误包装可能携带 proxy index、filter、解密或解析原文。固定路径未见正则复杂度/输入大小预算、字段白名单、header/secret 脱敏、解析版本/generation 或部分成功回执；显式 override 会就地修改解析中的 mapping，未形成可回滚快照。
- 本轮仅静态读取固定归档，未解析真实 provider、未使用 age key、未发起 HTTP 请求、未下载或修改代理数据、未启动 core、未使用 ADB，未读取设备配置、节点、URL、凭据、Cookie、日志或网络内容；新增 parity 行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，proxy-provider 过滤、覆盖与 age 解密解析边界静态审计）

- focused commit `3ea2060` 已创建但尚未 push；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

### FlClash proxy-provider 更新后的连接收敛与 healthcheck 并发边界静态审计（2026-09-10）

- 固定归档文件 SHA-256：`core/Clash.Meta/adapter/provider/provider.go`=`546ED6CDACF36E1BA14F619C065F4BF59F2CBAB7E89DC25BC56E04F40C6B290B`；`core/Clash.Meta/adapter/provider/healthcheck.go`=`5D839304F21C054811B4EF4E34C8DAE1FB8EC7BF0A36CE877CD5B6D581D3A4AA`；`core/Clash.Meta/tunnel/statistic/tracker.go`=`5F82775600E8F068D53C51A543601BAFE71B7890F422C67B6E92543393C30943`。
- `baseProvider.setProxies` 在 provider mutex 下替换 proxies、递增 version、更新 healthcheck 列表并异步触发检查；但 `HealthCheck.setProxies` 未持有 `mu`，与 `execute` 遍历 `hc.proxies` 并发时没有锁或不可变快照。healthcheck 以 1 秒 singleflight、errgroup 最多 10 并发和每 proxy timeout 执行，`Close()` 只取消内部 context，不等待 in-flight 检查完成。
- `proxySetProvider.Initial()` 完成初次加载后才调用 `closeAllConnections()`；后续 Fetcher 更新经 `setProxies` 不再清理 provider chain 中的活动 tracker，tracker `Close()` 错误也被忽略。固定实现没有版本关联、逐 proxy 结果、旧连接策略或取消/完成回执。
- 本轮仅静态读取固定归档，未启动 core、未更新 provider、未运行 healthcheck、未关闭连接、未使用 ADB，未读取设备连接、节点、URL、配置、凭据、Cookie、日志或流量内容；新增 parity 行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-10，proxy-provider 更新后的连接收敛与 healthcheck 并发边界静态审计）

- focused commit `810042d` 已创建但尚未 push；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

### FlClash proxy-provider `subscription-userinfo` 缓存与配额字段边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/adapter/provider/provider.go`=`546ED6CDACF36E1BA14F619C065F4BF59F2CBAB7E89DC25BC56E04F40C6B290B`；`core/Clash.Meta/adapter/provider/subscription_info.go`=`123565A529C8C5CD74F836AEAE79092007533DB7A8D49256EF91C90283E021B1`；`core/Clash.Meta/component/profile/cachefile/subscriptioninfo.go`=`07D9A7FCFB4C5112CC2FE3A603A9227959F1E5F165EE22B50500FEB32F26A92E`；cache 初始化实现位于 `cache.go`（`F78DB1582F01FAAFF1BB6260398A2A747809ABFF30C67EE03AC0F477ADE3227B`）。
- HTTP provider 在响应头读取 `subscription-userinfo`，解析 `upload`、`download`、`total`、`expire` 四个数值，更新内存 `SubscriptionInfo` 并写入 bbolt `subscriptioninfo` bucket；provider 初始化按 name 恢复该原始 header 文本。provider JSON 将四项数值作为 `subscriptionInfo` 暴露。
- `NewSubscriptionInfo` 对无法解析的值记录包含原始值的 warning，未知字段静默忽略；缓存没有 TTL、来源/版本绑定、容量上限、provider 删除清理或撤销回执。cache DB 以 `0666` 模式打开，写入失败只记 cache 路径和原始错误，调用方无法区分持久化失败或过期状态。
- 本轮仅静态读取固定归档，未发起 provider 请求、未构造或持久化真实 `subscription-userinfo`、未启动 core、未使用 ADB，未读取设备缓存、配置、节点、URL、凭据、Cookie、日志或网络内容；新增 parity 行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，proxy-provider `subscription-userinfo` 缓存与配额字段边界静态审计）

- focused commit `a42e9d5` 已创建但尚未 push；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

### FlClash HTTP/SOCKS/mixed 入站认证与 UDP 边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`listener/inbound/mixed.go`=`93844489F2101CBB3B9CFEA9CFFFD79641C24481547132DF53DF8D405648D361`；`listener/inbound/http.go`=`0E6AA5C473793663DEBED926F37316C7C2C16C2053F9C697FA7411727C5C9A73`；`listener/inbound/socks.go`=`7C40D8EA28C04F970E3F6151B1131B30AD3576509D4D6D6396A519767EB29777`；`listener/inbound/auth.go`=`D80505C45B1E3B02D5109B77D5FF5A00E10DB5EFA728F37CEDB945746D70098E`；`component/auth/auth.go`=`93F4E431F51124258E43AB75B858DD351C9586A892EDEE56A74E8FC132ECD542`；`listener/inbound/base.go`=`ACC0AF28BF0C444261134006B255DEC9ECA3FD8A83BC48097913F268012A7A86`。
- HTTP、SOCKS4/5、mixed listener 均把 `users` 转换为内存 `AuthStore`；缺失用户时使用 default store，空用户列表使用 nil store。mixed 读取首字节后分派 SOCKS4、SOCKS5 或 HTTP；`udp=true` 时为每个 TCP 地址建立独立 UDP listener。
- 入站空地址默认归一到 `0.0.0.0`；默认 listener 额外执行远端地址拒绝与 skip-auth 规则。SOCKS 握手或 HTTP `Proxy-Authorization` 解析/认证失败时关闭连接；listener close 聚合错误，但没有独立 health、失败分类或完成回执。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未发起代理请求，未读取设备配置、凭据、Cookie、节点、URL、日志或网络内容；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 命名 inbound listener 重载与失败回滚静态审计（2026-09-09）

- 固定归档文件 SHA-256：`listener/listener.go`=`5D9BCDED93B4ED821D80827DF0A6EB198F844AA27EB10C74AC887C8FFD3FE019`；`listener/parse.go`=`D66DC47E30E10F7112EB8CFD85037A30C5CA5366526CB453B297774095FE7EC5`；`config/config.go`=`49296DD48FB4BFBE1EB3B8236DE72FD07FB9E8EFFB122D09B137DD3D44982B86`；`hub/executor/executor.go`=`3AE623D0272443453ED2957B4E973532FBBB2CC759232E646C3DE1DAAF1F17FE`。
- `parseListeners` 逐项调用 `listener.ParseListener`，缺失 `type`、未知协议、解码失败或重复 listener name 会使整个 map 返回错误；不会产生部分成功的命名 listener 集合。`hub/executor.updateListeners` 随后调用 `PatchInboundListeners(listeners, tunnel.Tunnel, true)`。
- `PatchInboundListeners` 在同名配置变化时先关闭旧 listener，再调用新 listener 的 `Listen`；新 listener 失败时只写错误日志并继续，旧 listener 仍可能以已关闭实例保留在 `inboundListeners` map 中。只有 `dropOld=true` 且名称从新 map 消失时才删除旧项；函数不返回错误、健康状态、回滚或完成 receipt。
- 内置 HTTP/SOCKS/redir/tproxy/mixed 等端口由 `updateListeners` 后续的 `ReCreate*` 函数单独重建，不能据此证明命名 listener 在 Android bridge 上具有同样的可用性或失败语义。XToolpro 需要在 adapter 层执行 prepare/validate、原子切换和可核验的失败/取消/重试结果。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未修改配置或监听器，未读取设备文件、日志、节点、URL、凭据、Cookie 或网络内容；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 内置 HTTP/SOCKS/redir/tproxy/mixed 端口重建与 UDP 失败边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`listener/listener.go`=`5D9BCDED93B4ED821D80827DF0A6EB198F844AA27EB10C74AC887C8FFD3FE019`；`hub/executor/executor.go`=`3AE623D0272443453ED2957B4E973532FBBB2CC759232E646C3DE1DAAF1F17FE`。
- `ReCreateHTTP`、`ReCreateSocks`、`ReCreateRedir`、`ReCreateTProxy`、`ReCreateMixed` 均按共享 `allowLan`/`bindAddress` 计算地址并使用独立 mutex；地址变化时先关闭旧实例，端口为零或空时直接返回。
- HTTP/TProxy 在新 TCP listener 创建失败后不恢复旧实例；SOCKS/mixed 的 UDP listener 创建失败会关闭本轮新建 TCP，但不返回稳定失败结果；redir/TProxy UDP 创建失败只记录 warning，TCP listener 仍被保留并记录主监听成功。
- `updateListeners` 调用这些重建函数时不接收返回值、不汇总 TCP/UDP 部分成功，也没有统一健康、回滚、取消或完成 receipt。XToolpro 需在 adapter 层先校验资源，再原子切换并显式区分成功、部分成功、不可用、取消和重试。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未修改端口或配置，未读取设备文件、日志、节点、URL、凭据、Cookie 或网络内容；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，代理组策略静态审计）

- focused commit `e5b2f3a` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，实时流量与连接静态审计）

- focused commit `3b60951` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，rule-provider API 静态审计）

- focused commit `67d831d` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，platform hosts resolver 静态审计）

- focused commit `8b141c8`（DoH/DoT/DoQ DNS transport 静态审计）已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

- focused commit `4804bed`（provider 协议/地区/标签筛选语义静态审计）已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

- focused commit `188db09`（应用更新 metadata 与 artifact 信任边界静态审计）已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

- focused commit `0bbfe95` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，sniffing dispatcher 静态审计）

- focused commit `1c4a864` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，DNS/Fake-IP cache 静态审计）

- focused commit `55b0836` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，强停后 launcher 冷启动无意恢复复核）

- focused commit `df32aa8` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，Phase 02 gate 计数一致性复核）

- focused commit `3b57460` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，运行态快速双 TOGGLE 复核）

- focused commit `6dd5293` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，重复 START 请求最终收敛复核）

- focused commit `de53d07` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，快速 STOP→START 启动收敛复核）

- focused commit `724db80` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，快速 START→STOP 取消收敛复核）

- focused commit `064bd7a` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，重复 STOP 请求最终收敛复核）

- focused commit `3832883` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，快速双 TOGGLE 最终状态复核）

- focused commit `19bb590` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，原生 START/STOP 与 TUN 收敛受限复核）

- focused commit `79a53a2` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，GeoData 更新链路静态审计）

- focused commit `7c23efa` 已创建但尚未 push；随后将以 evidence-only commit 固化本 hash 与未 push 状态。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，rule-provider 更新与命中边界静态审计）

- focused commit `0e983f6` 已创建但尚未 push；本次仅修改 parity matrix 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

#### 本检查点远端备份状态（2026-09-09，external-controller 管理面与认证边界静态审计）

- focused commit `2b38353` 已创建但尚未 push；本次仅修改 parity matrix 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

#### 本检查点远端备份状态（2026-09-09，proxy-provider 订阅与健康检查静态审计）

- focused commit `0856859` 已创建但尚未 push；本次仅修改 parity matrix 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

#### 本检查点远端备份状态（2026-09-09，内置端口重建与 UDP 失败边界静态审计）

- focused commit `10ac147` 已创建但尚未 push；本次仅修改 parity matrix 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

#### 本检查点远端备份状态（2026-09-09，多地址 HTTP/SOCKS listener 创建与连接资源边界静态审计）

- focused commit `a50b166` 已创建但尚未 push；本次仅修改 parity matrix 与本 evidence 文件，其他工作树改动和临时产物未纳入。按要求不得自动 push，须先获得用户明确确认。

### FlClash 多地址 HTTP/SOCKS listener 创建与连接资源边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`listener/inbound/http.go`=`0E6AA5C473793663DEBED926F37316C7C2C16C2053F9C697FA7411727C5C9A73`；`listener/inbound/socks.go`=`7C40D8EA28C04F970E3F6151B1131B30AD3576509D4D6D6396A519767EB29777`；`listener/inbound/base.go`=`ACC0AF28BF0C444261134006B255DEC9ECA3FD8A83BC48097913F268012A7A86`；`listener/http/server.go`=`5F155948A183E63186481123AFCA464CE99EACDFDD7871C2424ED956960DA1F8`；`listener/socks/tcp.go`=`7A4E5DDDA9C6B3C2415081898309E66F9D3207ED10D22911FC82E4C02391637E`；`listener/socks/udp.go`=`A641A6E552DF1EC4FB2FC006FCB523D0E7DE90A7F47D41AF09813BFB4CC5A416`。
- `inbound.HTTP.Listen` 和 `inbound.Socks.Listen` 按逗号分隔的地址逐项创建 TCP listener；SOCKS 在 `udp=true` 时为每个地址继续创建 UDP listener。底层 HTTP/SOCKS `NewWithConfig` 先绑定 TCP，再初始化 certificate/private-key、ECH、client-auth CA 或 Reality。
- 任一 TLS/证书/Reality 初始化失败会直接返回，已绑定的 socket 未见统一关闭；多地址中后续地址或 UDP listener 创建失败时，先前已创建的实例同样没有统一回收，调用方无法得到部分成功、资源泄漏、回滚或单地址失败分类。
- 接受连接后以 goroutine 分派握手；SOCKS4/5 协议错误、认证失败或 UDP 数据包解析失败会关闭/丢弃，但固定 HTTP/SOCKS listener 路径未见统一 read/write deadline、请求体上限或 adapter 级并发预算。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未修改监听器或证书，未读取设备文件、日志、节点、URL、凭据、Cookie 或网络内容；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 全局停止对命名 inbound/tunnel listener 的清理覆盖静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/listener/patch.go`=`36281094CCBE59F5365D49759385027641353EDCB79B875D80F9EAF3EF82BE15`；`core/Clash.Meta/listener/listener.go`=`5D9BCDED93B4ED821D80827DF0A6EB198F844AA27EB10C74AC887C8FFD3FE019`。
- `listener.StopListener()` 仅关闭全局 `socks/http/redir/tproxy/mixed`、TUN、ShadowSocks、VMess 与 TUIC listener 变量；源码未见遍历或清空 `inboundListeners`、`tunnelTCPListeners`、`tunnelUDPListeners`。后者在 `listener.go:42-44` 定义为进程级 map；命名 inbound 仅在 `PatchInboundListeners` 删除差异项时移除，tunnel map 仅在 `PatchTunnel` 差异路径删除。
- 调用链静态核对：`core/hub.go:handleStopListener` 与 `core/common.go:stopListeners` 直接调用 `listener.StopListener()`；`core/Clash.Meta/hub/executor/executor.go:Shutdown` 调用 `listener.Cleanup()`，而 `listener.Cleanup()`（`listener.go:725`）只调用 `closeTunListener()`。未发现其他停止/Shutdown 路径会统一关闭并清空上述 map，也未见 in-flight 等待、失败分类或 terminal receipt。
- 该结果仅证明固定源码的清理覆盖缺口，不能断言某次设备运行必然遗留 socket；本轮未启动 core、未使用 ADB、未读取日志、配置、通知、节点、请求、数据库、文件、凭据、Cookie、URL、地址、路由、DNS 或流量内容。矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash Tunnel listener target 解析失败与资源回收静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/listener/tunnel/tcp.go`=`90EAD0ED90B273E967AFCC34BE085F89895EEFEE5E9C83C27B0737136F002048`；`core/Clash.Meta/listener/tunnel/udp.go`=`158FA0EFD7382AAB3B3CFB09DDB94EE44D1586278DF4BDD676E3EEDA4CAAE288`。
- `tunnel.New` 与 `NewUDP` 先执行 `lc.Listen`/`lc.ListenPacket`，随后才解析 `target`。当 `socks5.ParseAddr(target)` 返回 nil 时，函数直接返回 `invalid target address`，未见对已绑定 TCP/UDP socket 的 `Close`；因此失败路径存在资源回收缺口。
- 成功路径分别启动 Accept 与 ReadFrom 循环，并为每个连接/数据包派生 goroutine；循环仅依赖 `closed` 标志退出，未见 context、deadline、并发预算或 terminal receipt。`PatchTunnel` 对创建错误只写日志并继续，`updateTunnels` 不汇总逐项结果，无法区分部分成功、资源泄漏、取消或重试。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未创建 tunnel、未发起请求，未读取设备日志、配置、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash Tunnel 配置对象校验与未知 network 处理静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/listener/config/tunnel.go`=`D10302FC42CEE9C0551D0E24AFEA4AC15E3CF943D49DDC568FDDF5D8F15162A7`；`core/Clash.Meta/listener/listener.go`=`5D9BCDED93B4ED821D80827DF0A6EB198F844AA27EB10C74AC887C8FFD3FE019`。
- `Tunnel.UnmarshalYAML` 在字符串输入时校验 network、address、target；对象输入（YAML/JSON map）则直接解码到 `inner` 后赋值，未执行同等 schema/host:port 校验。`PatchTunnel` 随后只对 `network == "tcp"` 选择 TCP，任意其他值都进入 UDP 分支，未知协议不会拒绝或产生稳定 `Unavailable` 分类。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未提交配置或创建 tunnel，未读取设备日志、配置正文、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash Listener 停止与配置重载并发串行化静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/listener/patch.go`=`36281094CCBE59F5365D49759385027641353EDCB79B875D80F9EAF3EF82BE15`；`core/Clash.Meta/listener/listener.go`=`5D9BCDED93B4ED821D80827DF0A6EB198F844AA27EB10C74AC887C8FFD3FE019`。
- `PatchInboundListeners` 仅持有 `inboundMux`，`PatchTunnel` 仅持有 `tunnelMux`；`StopListener` 未获取任一 mutex，且仅关闭全局 listener 变量。停止与配置重载并发时，源码未提供全局生命周期锁、generation 检查或 in-flight 等待，可能出现交叉 close、stop 后重载重新创建，或 map 与实际 socket 状态不一致；未见统一 terminal receipt。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未并发调用 stop/reload，未读取设备日志、配置、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash UDP tunnel 队列背压与丢包可观测性静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/tunnel/tunnel.go`=`42738DFEEE87D646108764C44838EF5904E6737B0C0B74FC96DB629E753384A4`。
- `queueCapacity` 固定为 64；`initUDP` 按 `max(4,GOMAXPROCS)` 创建 worker 队列。`HandleUDPPacket` 以非阻塞 `select` 投递，队列满时直接 `packet.Drop()`，未产生计数、错误、取消或 terminal receipt；因此上层无法区分转发成功与过载丢弃，也没有 adapter 级可配置预算/健康状态。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未发起流量或读取连接内容；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash UDP tunnel 回包源地址语义静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/listener/tunnel/packet.go`=`3AD3891C5D8B4505538003A2BEAFE9AEB0FB0961519AD3D1524F2DBAC5408CEA`；`core/Clash.Meta/constant/adapters.go` 的 `UDPPacket` 合同说明要求 `WriteBack` 按传入地址设置回包源 IP/端口。
- tunnel `packet.WriteBack(b, addr)` 不使用 `addr`，直接 `pc.WriteTo(b, c.rAddr)`；SOCKS 对照实现将 `addr` 编码为 UDP response，tproxy 对照实现以 `addr` 绑定本地 socket。故 tunnel 路径没有同等回包源地址控制、NAT 映射验证或稳定错误回执。
- 本轮仅静态读取固定归档，未启动 core、未使用 ADB、未发起 UDP 流量或读取地址/流量内容；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash TUN 运行时配置 PATCH 的原子性与失败回执静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/configs.go`=`B4058F78C4CE3E975A84976A4B7F546F378DB3FB429BE87E904D04403153C390`；`core/Clash.Meta/listener/listener.go`=`5D9BCDED93B4ED821D80827DF0A6EB198F844AA27EB10C74AC887C8FFD3FE019`。
- `patchConfigs` 先应用 allow-lan、认证、bind address 等运行态 mutation，再调用 `ReCreateTun`；该函数在配置变化时先关闭旧 TUN，再调用 `sing_tun.New`。创建失败只写日志，将保存的 TUN 配置置为 disabled，不恢复旧实例，且函数无返回结果。
- `patchConfigs` 不收集任一 listener 重建结果，最后无条件 `render.NoContent`（HTTP 204）；请求方无法区分 TUN 成功、失败、部分更新、取消或回滚。本轮未调用管理面、未启动 core、未使用 ADB，未读取配置、日志、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。
- 同一 `tunSchema` 声明 `Inet4Address` 与 `Inet6Address`；但 `pointerOrDefaultTun` 中 `Inet4Address` 的赋值块被注释，`Inet6Address` 正常合并。故 `inet4-address` PATCH 可在无错误的 204 回执下被静默忽略；本轮未调用该管理面，保持 `Partial`。

### FlClash 受限 ADB 停止态状态复核（2026-09-09）

- 设备 `bf353dda` 在线；对固定 dev package `com.follow.clash.dev` 仅采集允许的摘要：目标进程存在、`POST_NOTIFICATION` app-op 为 `allow`、系统 `VPN CONNECTED` marker 计数为 `0`。
- TUN 接口计数命令受 shell 权限限制，记录为 `unavailable` 而非将其解释为无 TUN。没有发送 START、STOP、TOGGLE 或权限 grant/revoke；没有读取日志、配置、通知正文、UI 节点、请求、数据库、文件、凭据、Cookie、URL、地址、路由、DNS 或流量内容。
- 结果只确认本次可观测停止态摘要，不能证明 core/TUN 健康或否定其他运行态 proof；矩阵状态保持 `Partial`，Proxy 台账保持 `Investigating`。

### FlClash 受限原生 START/STOP 生命周期复核（2026-09-09）

- 设备 `bf353dda` 上固定 dev package `com.follow.clash.dev` 的已解析 `QuickActionActivity` 接收 `com.follow.clash.dev.action.START`/`STOP`。mutation 前摘要为：目标进程存在、`POST_NOTIFICATION` app-op=`allow`、系统 `VPN CONNECTED` marker=`0`、TUN 接口计数=`unavailable`（shell 权限限制）。
- 发送 START 后等待 7 秒，摘要为：进程存在、app-op=`allow`、VPN marker=`1`、TUN 计数仍为 `unavailable`。随后发送 STOP 作为回滚；7 秒后 marker 回到 `0`，进程存在且 app-op 未变，TUN 计数仍为 `unavailable`。START/STOP 是本轮唯一设备 mutation，未使用 root、权限 grant/revoke、配置变更、流量生成或其他 action。
- 未读取日志、配置、通知正文或 extras、UI 节点、请求、数据库、文件、凭据、Cookie、URL、地址、路由、DNS 或流量内容。系统 VPN marker 只能说明本次启动/停止可观察地收敛，不能证明 TUN、core 或可转发性；矩阵状态保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，受限原生 START/STOP 生命周期复核）

- focused commit `9cd9aff` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，受限 ADB 停止态状态复核）

- focused commit `f82a201` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，TUN 运行时配置 PATCH 的原子性与失败回执静态审计）

- focused commit `9f49b8a` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，UDP tunnel 回包源地址语义静态审计）

- focused commit `c1d84e5` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，UDP tunnel 队列背压与丢包可观测性静态审计）

- focused commit `67f2b06` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，Listener 停止与配置重载并发串行化静态审计）

- focused commit `69d646c` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，Tunnel 配置对象校验与未知 network 处理静态审计）

- focused commit `5e3f4cb` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，Tunnel listener target 解析失败与资源回收静态审计）

- focused commit `428b4ee` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，External-controller 本地传输鉴权边界静态审计）

- 本检查点文档增量已完成验证，focused commit 将仅包含 parity matrix 与本 evidence 文件；按要求不得自动 push，须先获得用户明确确认。

#### 本检查点远端备份状态（2026-09-09，External-controller 重建并发与失败回执静态审计）

- focused commit `4f3c308` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，External-controller `/restart` 完成与失败回执静态审计）

- focused commit `50a77f1` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

#### 本检查点远端备份状态（2026-09-09，External-controller debug 路由鉴权边界静态审计）

- focused commit `cdc2187` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller DoH 路由鉴权与错误边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`；`core/Clash.Meta/hub/route/doh.go`=`4CA2A97F7E2B4EC62C38921E29EDD4FC0AF8345138EADF80F038BD89A942B883`。
- `router` 先建立 secret 保护的管理 group，再在 group 外执行 `r.Mount(dohServer, dohRouter())`；DoH 因此不经过 `authentication()`，也没有从 `dohServer` 路径看到来源限制或管理面 allowlist。Unix/named-pipe 传输调用同一 router 时传入空 secret，故该 DoH 挂载也随本地传输边界继承未鉴权语义。
- `dohHandler` 在 `resolver.DefaultResolver == nil` 时返回 HTTP 500 和固定 `DNS section is disabled`；GET 以 `base64.RawURLEncoding` 解码 `dns` query，POST 要求 `Content-Type: application/dns-message` 并用 `io.LimitReader(r.Body, 65535)` 后 `io.ReadAll`，其他方法返回 405。base64 解码、DNS relay 失败和非法 content-type 分别直接返回原始 `err.Error()` 或固定文本；未见统一脱敏错误码、来源绑定、响应/并发预算或 terminal receipt。
- 本轮仅静态读取固定归档，未启动 core、未调用 DoH、未发送 DNS 请求、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller DoH 路由鉴权与错误边界静态审计）

- focused commit `05b0167` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller storage API 容量与写入回执边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/storage.go`=`8BBB28DFE8BE5D38E3D826BC4DB54C595A6F6B0D24BF6C3CCA7A8F5ED886B007`；`core/Clash.Meta/component/profile/cachefile/storage.go`=`CF67E0E7E0329469F66E1C8C192DB29683ACA98C334828DDB404FBB48E566FE5`；`core/Clash.Meta/hub/route/common.go`=`FFB303C61FC0BD4535741F5BBFB49E31A3B97EB98C4C2EDF02B64F88625E6739`。
- `storageRouter` 暴露 `GET/PUT/DELETE /{key}`，并由 `router` 在 secret 鉴权 group 内挂载；但 `startUnix`/`startPipe`/本地 transport 传入空 secret，故这些传输上的 storage API 不经过 `authentication()`。`getEscapeParam` 仅做 URL path unescape，没有长度、字符集或命名空间校验。
- `setStorage` 先执行 `io.ReadAll(r.Body)`，再做 JSON 合法性与 `1MB` 长度检查；因此路由层没有先行请求体上限。底层 `CacheFile.SetStorage` 仅对 key `64` 字节和 payload `1MB` 做检查，超限时写 warning 并直接返回；路由随后仍返回 HTTP 204，调用方无法区分实际写入、静默跳过或逐项驱逐。底层 bbolt bucket 按旧 `Time` 排序，在总数据 `1MB` 或最多 `16384` 条目超限时删除旧项，失败只记录日志。
- `getStorage` 对缺失或解码失败返回 JSON `null`，解码失败会尝试删除该 key；`deleteStorage` 无论 key 是否存在都返回 204。读写/删除错误未形成稳定脱敏的 `Unavailable`、`Cancelled`、`EngineCrashed` 或 `VersionMismatch` terminal receipt，日志包含 key、数据库路径或原始异常文本。
- 本轮仅静态读取固定归档，未启动 core、未调用 `/storage`、未写入或删除任何存储键、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller storage API 容量与写入回执边界静态审计）

- focused commit `efbba6d` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller cache flush 清理范围与完成回执静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/cache.go`=`B9DCE3EA2745EB7878C7876DB98C1D3436E7D751E79822A17A33FF20937C4E23`；`core/Clash.Meta/dns/resolver.go`=`076C077A14DDDCB744CDE2087D83FF05E2888718F33BFF58490EB5BA04115EF7`；`core/Clash.Meta/dns/enhancer.go`=`55B47DA94B15CD4C7E576DDDD8C11651B6F165998D58F00098C8A34DC924CF91`；`core/Clash.Meta/component/fakeip/pool.go`=`99E1323AF5E5D25013F08A1E63201463E86319859FAF892B4F419196BB134817`。
- `cacheRouter` 暴露 `POST /fakeip/flush` 和 `POST /dns/flush`，由 `router` 在 secret 管理 group 内挂载；`startUnix`/`startPipe` 将空 secret 传入同一 router，因此本地传输上的 cache flush 不经过 `authentication()`。Fake-IP flush 调用 resolver enhancer，按实际存在的 IPv4/IPv6 pool 逐项 `FlushFakeIP()`，成功后才重置 pool cycle/offset；错误以 HTTP 400 返回原始 `err.Error()`，没有稳定脱敏类别。
- DNS flush 调用全局 `resolver.ClearCache()`；该函数对 DefaultResolver 与 SystemResolver 分别启动 goroutine，Resolver 内部仅调用 cache.Clear，没有等待、错误返回、generation、前后计数或取消信号。路由立即返回 HTTP 204，无法证明两个 resolver 已完成清理、部分失败或在停止/重载期间收敛。
- 本轮仅静态读取固定归档，未启动 core、未调用 `/cache`、未清理任何 Fake-IP/DNS cache、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller cache flush 清理范围与完成回执静态审计）

- focused commit `ed9768d` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller upgrade UI/core/geo 更新与回滚边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/upgrade.go`=`6C3F995D1786076A80913D83812BEF199281BC6D008D986E6BCAB321038F0332`；`core/Clash.Meta/component/updater/update_core.go`=`2AE3F527FA7DC2CC7E304BCB832DC30A0D70A93A89A2EFB9B3E3B50E4D695304`；`update_ui.go`=`2646A4FDF535A2591593939E6392BA238C1F92D5E87E86AC95096787AC1F6196`；`update_geo.go`=`DB3D1C9B1A82C9BD1F7DF9B5018BA33FDC81BCBC95970D3C9C5E8CB09202F882`；`utils.go`=`C566506140F87D703A4543ED35678523A8BB6B994CF114A81FC5D172DF93D772`。
- `upgradeRouter` 暴露 `POST /ui`、非 embed 模式下的 `POST /`（core）和 `POST /geo`；这些路由位于 secret 管理 group 内，但本地 Unix/named-pipe 传输传入空 secret，因而继承未鉴权边界。成功路径统一返回 `{"status":"ok"}`，core/UI 随后 flush 或继续更新；错误以 HTTP 500 和原始 error 文本返回。
- Core updater 以 90 秒 context 下载并限制 32MB，按 channel/force 读取 latest version；固定实现未见签名、发布 hash、来源/ABI/版本 manifest 或安装前 artifact 兼容性校验。Windows 路径先将当前 executable rename 到 `meta-backup`，随后复制新文件替换；替换或重启失败没有统一旧文件恢复、启动健康核验或 terminal receipt。下载包与路径、版本会进入日志。
- UI updater 的 `downloadForBytes` 只设 90 秒超时后直接 `io.ReadAll`，未设响应大小上限；解压前清理临时目录，随后先清空现有 UI 目录再移动新文件，移动/准备失败可能留下不完整 UI，未见旧版本保留或原子切换。Geo updater 对 MMDB/ASN/GeoIP/GeoSite 按内容 hash 跳过相同数据并在加载后写入，但更新函数并发执行且只返回聚合 error，路由无法报告逐数据库状态、取消或部分成功。
- 本轮仅静态读取固定归档，未启动 core、未调用任何 `/upgrade` 路由、未下载或替换 UI/core/Geo 文件、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller upgrade UI/core/geo 更新与回滚边界静态审计）

- focused commit `a9bde50` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller DNS query API 字段与输入边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/dns.go`=`25AACDDA7D48F73BBC80F226AF0A8DCEFA0A5AE7577675C7C2F747A3A685402F`；路由鉴权与传输边界同 `core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- `dnsRouter` 暴露 `GET /query`，由 `router` 在 secret 管理 group 内挂载；本地 Unix/named-pipe transport 传入空 secret，故该 API 在这些传输上不经过 `authentication()`。`name` query 直接交给 `dns.Fqdn`，未见名称长度、标签、字符集或敏感查询限制；`type` 缺省为 `A`，未知类型返回 400。
- 查询使用 `resolver.DefaultDNSTimeout`，resolver 错误直接返回原始 `err.Error()`。成功响应包含 DNS header 状态位、Question，以及 Answer/Authority/Additional 中每条 RR 的完整 name、type、TTL 与 data；未见响应大小、并发预算或字段最小化/脱敏策略。
- 本轮仅静态读取固定归档，未启动 core、未调用 `/dns/query`、未发送 DNS 查询、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller DNS query API 字段与输入边界静态审计）

- focused commit `7642d2f` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller 外部路由注册与并发边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/external.go`=`08B3D320DD060E141953E56E88AED131502E7699F2F318051E1F6C45FEC92ACD`；`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- `Register(route ...externalRouter)` 直接向进程级 `externalRouters` slice 追加回调；`addExternalRouters` 在 `router` 的 secret 管理 group 内逐项调用，因此已注册扩展默认继承 `authentication()`，与 DoH/debug 等 group 外挂载路径形成边界差异。
- 固定 slice 无 mutex、generation、不可变快照或重复注册检查；server 重建或多 goroutine 注册/遍历并发时未见同步语义。外部回调可向传入 router 挂载任意路径，未见固定命名空间、方法/资源预算、能力 allowlist 或扩展级 terminal receipt。
- 本轮仅静态读取固定归档，未启动 core、未注册扩展路由、未调用管理 API、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller 外部路由注册与并发边界静态审计）

- focused commit `cb45f9b` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller traffic/memory 流式端点取消与资源边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`；统计实现位于固定归档 `core/Clash.Meta/component/tunnel/statistic` 路径，本轮未修改或执行该组件。
- `traffic` 与 `memory` 同时支持普通 HTTP 流和 websocket；每秒从 `statistic.DefaultManager` 读取瞬时/累计上下行或内存值，编码后 flush。两条路径都以 `for range ticker.C` 持续运行，仅在 JSON 编码、HTTP 写入或 websocket 写入错误时退出，未见对 `r.Context().Done()` 的显式监听、订阅数量上限、连接关闭回执或逐订阅 terminal receipt。
- 这些快照字段未直接包含节点名或请求正文，但累计流量和内存值仍属于运行态诊断数据；固定 endpoint 未见采样节流、权限分级、字段最小化或取消分类。客户端断开若写错误未及时返回，可能遗留 goroutine/ticker；websocket close 同样依赖写路径发现。
- 本轮仅静态读取固定归档，未启动 core、未请求 `/traffic` 或 `/memory`、未建立 websocket、未读取设备日志或运行态数据、未使用 ADB，未读取配置、通知、节点、URL、地址、路由、DNS、流量内容、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller traffic/memory 流式端点取消与资源边界静态审计）

- focused commit `73c5ec0` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller 根/版本健康响应语义静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- `router` 在 secret 管理 group 内注册 `GET /` 与 `GET /version`；本地 Unix/named-pipe transport 传入空 secret，因而这两个端点在本地传输上不经过 `authentication()`。`hello` 只返回固定 `{"hello":"mihomo"}`；`version` 只返回全局 `C.Meta` 与 `C.Version`。
- 两个端点均未检查 core 是否已启动、listener/TUN/DNS 是否健康、当前配置 generation、ABI/bridge 配对或 capability surface，也未返回 engine terminal receipt。HTTP 200 仅证明 router/transport 可响应；不能将其当作可转发、VPN 已建立或版本匹配证明。
- 本轮仅静态读取固定归档，未启动 core、未调用 `/` 或 `/version`、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller 根/版本健康响应语义静态审计）

- focused commit `b9ceaf8` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller `/logs` 流式订阅取消与背压边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`；`core/Clash.Meta/log/log.go`=`1F474A4091999E04A8C07D498DAF18260575A907316D73B95E6D7B123D22A5D8`；`core/Clash.Meta/common/observable/observable.go`=`B6A3D1B2F6D604430D0FAABE1C20D490F09117E64642BF2715EA06DDBFEE4C5F`；`subscriber.go`=`E44A28BF2A21E48778B32E32F58C565652E698D2D7C6EFCA29419FB336FDDB22`。
- `getLogs` 支持 `level`/`format=structured` 和 HTTP/websocket；每个请求调用 `log.Subscribe()`，建立 200 容量 subscriber，再由 goroutine 转发到 1024 容量 `ch`。转发使用非阻塞 `select`，`ch` 满时静默丢弃日志事件；没有丢弃计数、告警或订阅级 terminal receipt。
- 主循环只在 JSON 编码、HTTP 写入或 websocket 写入错误时退出，未显式监听 `r.Context().Done()` 或 websocket close。退出后才经 `defer log.UnSubscribe` 关闭 subscriber。`Observable.process` 持有全局 mutex 逐个调用阻塞式 `Subscriber.Emit`；慢 subscriber 的 200 缓冲耗尽时可阻塞整个日志生产者，放大背压影响。
- 本轮仅静态读取固定归档，未启动 core、未请求 `/logs`、未建立 websocket、未读取设备日志或日志正文、未使用 ADB，未读取配置、通知、节点、URL、地址、路由、DNS、流量内容、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller `/logs` 流式订阅取消与背压边界静态审计）

- focused commit `20c3684` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller `/connections` 快照、关闭与 websocket interval 边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/connections.go`=`FBE4948D9B506F661E4AC5936E493F82AFC1FB2F1DC84DB8BA7FF108423AD83A`；`core/Clash.Meta/tunnel/statistic/manager.go`=`D6633D8CA012523924C7BB8103800189679EF2E127524C1EFFFDFDDF40FFDB3F`；`tracker.go`=`5F82775600E8F068D53C51A543601BAFE71B7890F422C67B6E92543393C30943`。
- `connectionRouter` 暴露 `GET /`、`DELETE /` 和 `DELETE /{id}`，整体位于 secret 管理 group 内；本地 Unix/named-pipe transport 的空 secret 继承未鉴权边界。`getConnections` 的 HTTP 快照直接序列化 `statistic.Snapshot`，每条 `TrackerInfo` 含 `Metadata`、provider/代理链、rule、rulePayload、开始时间和上传/下载计数；固定路径未见字段最小化或脱敏。
- 单项与批量关闭均忽略 `Tracker.Close()` 返回值并无条件返回 204，无法区分不存在、关闭失败、部分成功或取消。websocket `interval` 只调用 `strconv.Atoi`，0 或负数未拒绝，随后传入 `time.NewTicker` 可能触发 panic；推送循环也未监听 request context、websocket close、订阅上限或 terminal receipt。
- 本轮仅静态读取固定归档，未启动 core、未调用 `/connections`、未建立 websocket、未关闭任何连接、未读取设备日志/连接正文或地址、未使用 ADB，未读取配置、通知、节点、URL、路由、DNS、流量内容、凭据或 Cookie；新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller `/connections` 快照、关闭与 websocket interval 边界静态审计）

- focused commit `13d1552` 已创建但尚未 push；按要求不得自动 push，须先获得用户明确确认。涉及路径仅为 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物未纳入。

### FlClash External-controller 代理、代理组与规则管理 API 字段和回执边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/proxies.go`=`51205BD6EDDDA36D7F8F60004E9E54E1171EABD4D5BCD2164A2EFD2FA9935F82`；`groups.go`=`BAAD47C7E2E3393BF914DD4D5F4A22446E8D1FBC8235381F604ED780A3B270ED`；`rules.go`=`2295A989D65BF2F602320506DEDC9A4361BB937CD17F494E5D7CBDD631C9CDB9`；`adapter/adapter.go`=`5344064E868AEEC63EB2D9AAE1FF69B50BC39BF8F1B230856B4833195642BAE5`；`adapter/outbound/base.go`=`68E01AA7F26D3AC01BA5620615B20DBB49114ED52C3E9FFC34C3C65A185E6A52`；`adapter/outboundgroup/selector.go`=`682FCEF95A0E9D7F97FB0ADDA6A72C82F772839ADC0C1E5FFB4A6D51B29AF1B9`；`adapter/outboundgroup/urltest.go`=`5970E1AB00F269C53108213F677702F00B8F690E06A5880E74F58F0AFDEC7E36`。
- `proxyRouter` 暴露 `GET /`、单代理 `GET/PUT/DELETE` 和 `/delay`；`groupRouter` 暴露 `GET /`、单组 `GET`/`delay`；`ruleRouter` 暴露 `GET /` 与非 embed 模式下的 `PATCH /disable`。这些路由在 `server.go` 的 secret 管理 group 内，但 Unix/named-pipe transport 传入空 secret，因此本地传输继承未鉴权边界。
- `Proxy.MarshalJSON` 将 adapter 自身字段与 `name`、alive、UDP/UOT、XUDP/TFO/MPTCP/SMUX、interface、routing-mark、provider-name、dialer-proxy、延迟 history 和按测试 URL 的 extra history 合并；selector/url-test adapter 另外暴露 `now`、完整 `all` 节点名列表、testUrl、expectedStatus、hidden/icon/emptyFallback。`GET /group` 直接返回完整 proxy group 对象；`GET /rules` 返回 index/type/payload/proxy/size 以及 disabled/hit/miss 统计和时间戳，未见字段白名单、分页或响应预算。
- `PUT /proxies/{name}` 只解码 `{name}`，调用 `SelectAble.Set` 后写入 cachefile selected 值并异步触发 tray callback；core 状态与缓存写入没有 generation、事务、回读或失败回滚，selector 错误原文包装为 400。`DELETE /proxies/{name}` 对非 Selector 的 SelectAble 强制清空并返回 204，其他类型统一 400；找不到代理在 middleware 阶段返回 404。
- 两类 `/delay` 仅检查整数 timeout 与 unsigned expected ranges，未拒绝 0/负 timeout、未限制 url/并发；代理 delay 在上下文超时后返回 504，其他失败返回 503，部分路径直接序列化原始 error。group delay 会先清空当前选择再测试，失败时没有恢复选择或 terminal receipt。
- `PATCH /rules/disable` 接受任意 JSON `map[int]bool`，越界 index 静默跳过，逐项直接修改运行态 wrapper，不持久化、不做版本/冲突校验，始终返回 204；embed 模式不注册该路由。固定源码未提供 success、unavailable、cancel、crash、version mismatch 的统一结果或管理面最小字段合同。
- 本轮仅静态读取固定归档，未启动 core、未调用任何 management API、未切换代理、未关闭代理、未禁用规则、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller 代理、代理组与规则管理 API 字段和回执边界静态审计）

- focused commit `513d241` 与 evidence-only commit `4b3bc75` 已由用户在本机 PowerShell 创建，均尚未 push；涉及路径仅为该 checkpoint 的矩阵与 evidence 文档，其他工作树改动和临时产物未纳入。

### FlClash External-controller `/configs` GET/PUT/PATCH 与 Geo 更新边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/configs.go`=`B4058F78C4CE3E975A84976A4B7F546F378DB3FB429BE87E904D04403153C390`；`hub/executor/executor.go`=`3AE623D0272443453ED2957B4E973532FBBB2CC759232E646C3DE1DAAF1F17FE`；`config/config.go`=`49296DD48FB4BFBE1EB3B8236DE72FD07FB9E8EFFB122D09B137DD3D44982B86`。
- 固定路由提供 `GET /configs`、非 embed 模式下 `PUT /configs`、`PATCH /configs` 与 `POST /configs/geo`；路由位于 secret 管理 group，但 Unix/named-pipe transport 传入空 secret，因此本地传输继承未鉴权边界。
- `GET /configs` 直接序列化 `executor.GetGeneral()`，包含端口、TUN、TUIC、认证用户、IP 前缀、模式、日志级别、接口和 Geo URL 等运行态字段。`PATCH` 通过指针 schema 接收部分 general/TUN/TUIC 字段，却在一次请求中按固定顺序重建多个 listener/TUN，并修改 mode、DNS、日志和接口；未见请求体大小、数值范围、字段互斥、generation、回读或逐字段结果，底层副作用可能先发生后才暴露错误。
- `PUT` 只解码 `{path,payload}`。payload 非空时完整内容交给 `config.Parse`，未见请求体上限或字段白名单；path 为空使用默认配置路径，非空仅检查绝对路径和 `C.Path.IsSafePath` 后直接 `ParseWithPath` 读取。解析失败返回 400 原始文本；解析成功后调用带进程级 mutex 的 `executor.ApplyConfig(cfg, force)`，依次更新用户、代理、规则、嗅探、hosts、general/listener、DNS、provider、profile、updater，没有事务、快照、阶段性 health check、取消或失败回滚，最终无条件返回 204。
- `config.Parse` 会先解密 age 内容再做 YAML 解码；默认配置预填网络、Geo、外部 UI 等字段，未见未知字段拒绝、秘密字段脱敏或配置 generation。`POST /geo` 仅返回无正文 204/500，不能表达逐资源成功、部分失败、取消或版本错配。
- 本轮仅静态读取固定归档，未启动 core、未调用 `/configs`、未读取或写入设备配置、未更新 Geo 数据、未使用 ADB，未读取日志、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller `/configs` GET/PUT/PATCH 与 Geo 更新边界静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller proxy-provider 管理与 healthcheck 契约静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/provider.go`=`6B3A4B59E42AA13E8E04544F3BA269FE9906695B3BCC7BD46B80CEFA4B3DAD80`；`adapter/provider/provider.go`=`546ED6CDACF36E1BA14F619C065F4BF59F2CBAB7E89DC25BC56E04F40C6B290B`；`adapter/provider/healthcheck.go`=`5D839304F21C054811B4EF4E34C8DAE1FB8EC7BF0A36CE877CD5B6D581D3A4AA`。
- 固定路由提供代理 provider 列表/单项 GET、`PUT /{providerName}` 更新、provider 级与单节点 healthcheck，以及 rule-provider 列表和更新；路由位于 secret 管理 group，但 Unix/named-pipe transport 传入空 secret，继承本地未鉴权边界。
- provider JSON 直接暴露名称、类型、vehicleType、完整 proxy 列表、testUrl、expectedStatus、updatedAt 和 `subscription-userinfo` 解析结果；单节点 GET 继续返回完整 proxy JSON。`PUT` 成功无正文 204，失败 503 包装原始 error；找不到 provider/proxy 返回 404，未见请求体/并发限制、更新 generation、旧版本回滚或逐项结果。
- healthcheck 路由只调用 `HealthCheck()` 即返回 204；实现以 provider 级 singleflight 合并约 1 秒窗口、errgroup 最多 10 并发和每 proxy timeout，但不接受请求 context 取消、不返回任务 ID/完成状态/逐节点结果，healthcheck 日志包含 proxy 名称、URL、alive 与 delay。`setProxies` 先替换切片并递增 version，再触发自动检查；更新期间没有原子快照与失败后保留旧状态的对外确认。
- 本轮仅静态读取固定归档，未启动 core、未调用 provider API、未触发更新或 healthcheck、未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller proxy-provider 管理与 healthcheck 契约静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller `/restart` 生命周期与回执契约静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/restart.go`=`0F3B70A1B7A23283882F41DBE2333042965DF4F5FE1F889F9A3AB5FB291ED5E9`；`hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- 非 embed 模式注册 `POST /restart`；路由位于 secret 管理 group，但 Unix/named-pipe transport 传入空 secret，继承本地未鉴权边界。handler 先获取 `os.Executable()`，立即返回 `{"status":"ok"}` 并 flush，然后在后台 goroutine 执行 `executor.Shutdown()` 与进程重启，因此 HTTP 成功不代表停止、启动、listener/TUN 清理或新进程健康。
- Windows 路径使用 `exec.Command` 启动同一路径后 `os.Exit(0)`，失败直接 `log.Fatalln`；其他平台使用 `syscall.Exec`，失败同样 fatal。固定实现没有旧进程保活、超时、取消、崩溃归因、last-known-good 或失败回滚，也没有返回任务 ID、版本/健康核验或 terminal receipt；重启日志包含可执行路径、参数和原始错误。
- embed 模式不注册该端点。本轮仅静态读取固定归档，未启动或重启 core、未调用 `/restart`、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller `/restart` 生命周期与回执契约静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash Debug profiler 与 GC 管理端点边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- 当 `isDebug` 为真时，router 在 secret 管理 group 之外挂载 `/debug`；子路由包含 `PUT /debug/gc`（直接调用 `runtime/debug.FreeOSMemory()`）和 `middleware.Profiler` 全部 profiler 端点，因此不经过 `authentication(secret)`。HTTP/TLS server 虽使用配置 secret，但 Unix/named-pipe server 传入空 secret，在 debug 模式下本地传输必然无鉴权。
- `isDebug` 由 general log level 是否为 DEBUG 派生，调高日志级别会同步开启 profiler/GC 管理面；未见独立 allowlist、来源绑定、请求/响应大小预算、并发限制、操作审计或稳定错误回执。`/debug/gc` 无正文成功且无完成/耗时确认，profiler 可能暴露堆、goroutine、CPU/profile 等运行时诊断。
- CORS 应用在根 router，允许配置的 origins 和 private-network 请求；本轮未启动 debug server、未调用 profiler 或 GC 端点、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，Debug profiler 与 GC 管理端点边界静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External UI 静态服务与下载发布边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`；`component/updater/update_ui.go`=`2646A4FDF535A2591593939E6392BA238C1F92D5E87E86AC95096787AC1F6196`。
- 当 `uiPath` 非空时，router 在管理 group 外挂载 `/ui`；`GET /ui` 重定向到 `/ui/`，`GET /ui/*` 通过 `http.FileServer(http.Dir(uiPath))` 提供静态文件，因此不经过 `authentication(secret)`，Unix/named-pipe 也共享该未鉴权边界。`SetUIPath` 仅调用 `C.Path.Resolve`；配置阶段只检查 external-ui 路径安全和 external-ui-name 为本地路径，未见公开服务绑定/来源 allowlist、资源预算、缓存控制、内容类型白名单或资产版本回执。
- UI updater 可按配置从 `external-ui-url` 下载；`AutoDownloadUI` 仅以目标目录是否非空决定跳过。下载后先清理目标目录，再移动临时解包目录，未见签名/哈希/来源校验、原子目录切换、旧版本保留或启动健康核验，失败可能留下不完整 UI；固定路径的错误/调试日志可包含 UI URL、路径和原始异常。
- 本轮仅静态读取固定归档，未启动 UI server、未请求 `/ui`、未下载或替换 UI、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External UI 静态服务与下载发布边界静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller TCP/TLS/Unix/named-pipe 监听与重建边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- `ReCreateServer` 并行启动 TCP、TLS、Unix 与可选 named-pipe listener；每个启动函数先关闭旧 server 引用再创建新 listener，但关闭/Serve 错误只写日志，没有统一启动 generation、等待旧 listener 完全退出、端口冲突回滚或健康回执。TCP/TLS 使用配置地址和 routing mark；TLS 动态加载证书并按 `ClientAuthType`/client CA 启用 mTLS，但未见证书 pin、最小 TLS 版本/密码套件、握手超时或 client-auth 失败的稳定类别。
- Unix 路径经 `C.Path.Resolve`，父目录自动 `MkdirAll(0755)`，bind 前 unlink 旧 socket，随后 `Chmod(0666)`；named-pipe 仅校验 `\\.\pipe\` 前缀。Unix/named-pipe handler 传入空 secret，所有管理 group 路由继承未鉴权边界；不同传输也共享 CORS/router 配置。
- 本轮仅静态读取固定归档，未启动、关闭或重建任何 listener，未调用管理 API，未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller TCP/TLS/Unix/named-pipe 监听与重建边界静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller Authorization、WebSocket token 与 CORS 组合边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`；`config/config.go`=`49296DD48FB4BFBE1EB3B8236DE72FD07FB9E8EFFB122D09B137DD3D44982B86`。
- HTTP 请求要求 `Authorization: Bearer <secret>`，比较使用常量时间函数；WebSocket 因浏览器不能自定义 header，支持 query `token`，缺少 token 时回退解析 Authorization header。失败统一返回 401 JSON，但未见失败次数限制、轮换、过期、审计事件、来源绑定或稳定错误码；query token 可能进入 URL 历史、代理和访问日志边界。
- CORS 全局允许配置的 origins、`Content-Type`/`Authorization` header 和可选 private-network 请求，默认 `AllowOrigins=["*"]`、`AllowPrivateNetwork=true`；未见凭据与宽泛跨源策略互斥校验或按路由分级。`/debug`、`/ui`、DoH 等 group 外挂载不受 middleware 保护；Unix/named-pipe 传空 secret，管理 API 无需 header。
- 本轮仅静态读取固定归档，未发送鉴权请求、未建立 WebSocket、未读取或记录任何 secret/token、未启动服务、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller Authorization、WebSocket token 与 CORS 组合边界静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller WebSocket 升级、帧写入与关闭边界静态审计（2026-09-09）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/common.go`=`FFB303C61FC0BD4535741F5BBFB49E31A3B97EB98C4C2EDF02B64F88625E6739`；`hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- `wsUpgrade` 手工校验 GET、HTTP/1.1、Host、Upgrade/Connection、24 字节 `Sec-WebSocket-Key` 和版本 13；失败直接写原始 handshake error 文本。Hijack 后清除所有 deadline，写入 101 响应并把连接交给流式 handler；未见 Origin 校验、握手/读写 deadline、最大帧/消息大小或 ping/pong 保活。
- 服务器帧写入允许任意 payload 长度；客户端断开主要依赖写错误。`traffic`、`memory`、`logs`、`connections` 等循环缺少统一 request-context/close 监听和订阅上限，部分路径仅在写失败后退出并 defer close；hijack 后错误无法再转换为结构化 HTTP 回执。
- 本轮仅静态读取固定归档，未建立 WebSocket、未发送帧、未读取连接内容、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie；矩阵新增行保持 `Partial`，Proxy 台账保持 `Investigating`。

#### 本检查点远端备份状态（2026-09-09，External-controller WebSocket 升级、帧写入与关闭边界静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash provider healthcheck 额外 URL、过滤器与 timeout 输入契约静态审计（2026-09-10）

- 固定归档文件 SHA-256：`core/Clash.Meta/adapter/provider/healthcheck.go`=`5D839304F21C054811B4EF4E34C8DAE1FB8EC7BF0A36CE877CD5B6D581D3A4AA`；`core/Clash.Meta/adapter/provider/provider.go`=`546ED6CDACF36E1BA14F619C065F4BF59F2CBAB7E89DC25BC56E04F40C6B290B`。
- 固定 healthcheck 同时支持 provider 默认 `testUrl`、按调用传入的额外 URL，以及按 filter 改变待测 proxy 集合；执行层对每个 proxy 建立独立 context timeout，默认约 5 秒，并通过 provider 级 singleflight 将约 1 秒内的重复检查合并。额外 URL/filter 会改变实际执行集合，而不是只改变展示字段。
- 固定入口未见对额外 URL 的 scheme/host/长度、重定向或来源绑定做统一校验，未见对 filter 表达式复杂度/输入大小或 timeout 上下限、总耗时预算做 adapter 级约束。空集合、无效 URL、超时和取消不会形成独立的稳定终态；管理路由也不接收请求 context，不返回任务 ID、逐 proxy 状态或取消确认，错误/探测日志可包含 proxy 名称、测试 URL、alive 与 delay。
- XToolpro 只能在受鉴权 adapter 入口接受受限 URL、过滤器与 timeout：应先完成 schema/大小/复杂度/总 deadline 预检，再以有界并发和可取消 context 执行，并将空集合、超时、取消、版本错配和引擎崩溃归一为脱敏 `Unavailable`/`Cancelled`/`VersionMismatch`/`EngineCrashed` terminal receipt；在真实隔离契约测试前保持 `Partial`，Proxy 台账保持 `Investigating`。
- 本轮仅基于固定归档进行静态审计，未启动 core、未调用 healthcheck API、未发送测速请求、未触发 provider 更新、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie。

#### 本检查点远端备份状态（2026-09-10，provider healthcheck 额外 URL、过滤器与 timeout 输入契约静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash provider healthcheck interval/lazy 调度与关闭语义静态审计（2026-09-10）

- 固定归档文件 SHA-256：`core/Clash.Meta/adapter/provider/healthcheck.go`=`5D839304F21C054811B4EF4E34C8DAE1FB8EC7BF0A36CE877CD5B6D581D3A4AA`；`core/Clash.Meta/adapter/provider/provider.go`=`546ED6CDACF36E1BA14F619C065F4BF59F2CBAB7E89DC25BC56E04F40C6B290B`。
- 固定 provider healthcheck 以 `interval` 驱动周期检查，并支持 `lazy`/touch 路径在无需探测时跳过集合；实际执行仍受 provider 级 singleflight、每轮最多 10 个并发和单 proxy timeout 约束。周期检查、手动触发和 provider 更新后的触发共享同一 healthcheck 状态，但未形成持久化调度记录。
- 静态路径未见 interval/lazy 的最小周期、范围、抖动或总耗时预算校验，未见跨进程恢复、调度 generation、重复 watcher 防护或“为何跳过”的对外回执。`Close()` 只取消内部 context，不等待进行中的检查；关闭与下一轮 ticker/touch 触发之间也未见统一生命周期锁，无法从 API 得到停止已生效或所有探测已退出的确认。
- XToolpro 只能在 adapter 层把周期调度建模为可持久化状态机：对 interval/lazy 做有界校验，记录下一次触发与跳过原因，串行化 update/healthcheck/close，停止时传播取消并等待 in-flight 收敛，再输出脱敏的 `Success`/`Unavailable`/`Cancelled`/`EngineCrashed`/`VersionMismatch` terminal receipt；在隔离契约测试完成前保持 `Partial`，Proxy 台账保持 `Investigating`。
- 本轮仅基于固定归档进行静态审计，未启动 core、未触发周期或 lazy healthcheck、未更新 provider、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie。

#### 本检查点远端备份状态（2026-09-10，provider healthcheck interval/lazy 调度与关闭语义静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller `/delay` 输入与组状态副作用静态审计（2026-09-10）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/proxies.go`=`51205BD6EDDDA36D7F8F60004E9E54E1171EABD4D5BCD2164A2EFD2FA9935F82`；`groups.go`=`BAAD47C7E2E3393BF914DD4D5F4A22446E8D1FBC8235381F604ED780A3B270ED`；`adapter/outboundgroup/selector.go`=`682FCEF95A0E9D7F97FB0ADDA6A72C82F772839ADC0C1E5FFB4A6D51B29AF1B9`；`adapter/outboundgroup/urltest.go`=`5970E1AB00F269C53108213F677702F00B8F690E06A5880E74F58F0AFDEC7E36`。
- 固定 `proxyRouter` 与 `groupRouter` 的 `/delay` 接受 timeout、测试 URL 和 expected-status 参数；handler 主要做整数/无符号解析后进入 URLTest。单代理 timeout 走 504，其他错误多走 503；未见统一的 URL scheme/host/长度/重定向约束、timeout 下限或批量总 deadline，也未见请求 context 取消映射为稳定 `Cancelled`。
- group delay 在开始测试前会清空当前选择，候选测试失败、为空或部分超时时没有恢复旧选择的事务/快照；API 不返回逐节点结果、generation 或选择变更回执。固定代码因此可能让 UI 保存的选择与 core 当前选择短暂不一致，且失败原因可能以原始 error 形式返回。
- XToolpro 只能在受鉴权 adapter 中先校验 URL、timeout、并发和总 deadline，复制不可变选择快照后执行可取消、有界批量测速；成功时原子发布新选择，失败/取消时恢复旧快照，并返回脱敏 `Success`/`Unavailable`/`Cancelled`/`EngineCrashed`/`VersionMismatch` terminal receipt。完成隔离契约测试前，新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。
- 本轮仅静态读取固定归档，未调用 `/delay`、未发送测速请求、未切换代理组、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie。

#### 本检查点远端备份状态（2026-09-10，External-controller `/delay` 输入与组状态副作用静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller `PATCH /rules/disable` 索引与持久化契约静态审计（2026-09-10）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/rules.go`=`2295A989D65BF2F602320506DEDC9A4361BB937CD17F494E5D7CBDD631C9CDB9`；`core/Clash.Meta/rules/provider/rule_set.go`=`B58CC52A7C44758B06E66118ADF38F9249D42EDE2192078D2758AC2E5F50B267`；`core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- 固定非 embed 路由接受任意 JSON `map[int]bool`，按规则索引直接修改运行态 wrapper；越界索引静默跳过，空 map、部分有效 map 与全部有效 map 均无逐项结果，handler 始终返回 204。该端点不写回配置或 profile，也不携带 generation/version/冲突校验、撤销或失败回滚。
- embed 模式不注册该端点；Unix/named-pipe transport 继续传入空 secret，继承本地管理面未鉴权边界。固定实现没有把索引快照与规则 provider 版本绑定，规则更新或重载期间可能出现索引漂移而仍返回成功。
- XToolpro 只能在 adapter 入口锁定不可变规则快照并校验索引、版本和目标范围；提交前先 staging，成功后原子发布，失败/取消恢复旧快照，并返回脱敏 `Success`/`Unavailable`/`Cancelled`/`VersionMismatch` terminal receipt。完成隔离契约测试前，新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。
- 本轮仅静态读取固定归档与既有哈希记录，未调用 `/rules/disable`、未修改运行态规则、未启动 core、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie。

#### 本检查点远端备份状态（2026-09-10，External-controller `PATCH /rules/disable` 索引与持久化契约静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller `/dns/query` 名称/type 与响应预算契约静态审计（2026-09-10）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/dns.go`=`25AACDDA7D48F73BBC80F226AF0A8DCEFA0A5AE7577675C7C2F747A3A685402F`；路由传输与鉴权边界同 `core/Clash.Meta/hub/route/server.go`=`15EACD2A4122A8BEECB57E679CBAE1FB1F42BB1A0C2C27E41FA4FD3901214C65`。
- 固定 `dnsRouter` 将 `name` 直接交给 `dns.Fqdn`，空 `type` 默认 `A`，未知类型返回 400；未见名称长度、标签数量、字符集、内部域名或批量查询限制。查询使用 resolver 默认 timeout，但错误通常直接序列化原始文本。
- 成功响应包含完整 DNS header、Question 以及 Answer/Authority/Additional 中每条 RR 的 name、type、TTL 与 data；未见响应字节/记录数上限、字段白名单、并发预算或 request-context 取消回执。Unix/named-pipe transport 传入空 secret，使该管理路由继承本地未鉴权边界。
- XToolpro 只能在 adapter 入口执行名称/type/schema 与大小限制，绑定 request context 和并发预算，按隐私策略裁剪 RR 字段，并以脱敏 `Success`/`Unavailable`/`Cancelled`/`EngineCrashed`/`VersionMismatch` terminal receipt 结束查询；完成隔离契约测试前，新增矩阵行保持 `Partial`，Proxy 台账保持 `Investigating`。
- 本轮仅静态读取固定归档，未调用 `/dns/query`、未发送 DNS 查询、未读取设备 DNS/路由或日志、未使用 ADB，未读取配置、通知、节点、URL、地址、流量、凭据或 Cookie。

#### 本检查点远端备份状态（2026-09-10，External-controller `/dns/query` 名称/type 与响应预算契约静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

### FlClash External-controller storage 请求体与驱逐回执契约静态审计（2026-09-10）

- 固定归档文件 SHA-256：`core/Clash.Meta/hub/route/storage.go`=`8BBB28DFE8BE5D38E3D826BC4DB54C595A6F6B0D24BF6C3CCA7A8F5ED886B007`；`core/Clash.Meta/component/profile/cachefile/storage.go`=`CF67E0E7E0329469F66E1C8C192DB29683ACA98C334828DDB404FBB48E566FE5`。
- 固定 `setStorage` 先对请求体执行 `io.ReadAll`，之后才检查 JSON 合法性与 1MB 长度；key 仅做 URL path unescape，未见长度、字符集或命名空间预检。底层 bbolt 再按 key 64 字节、总 payload 1MB、最多 16384 条目限制，并按旧时间驱逐。
- 超限 payload/key、驱逐失败或删除不存在 key 时，路由仍可能返回 204；没有写入成功、静默跳过、驱逐数量、删除确认或 generation 回执。日志可能包含 key、数据库路径与原始异常；本地 Unix/named-pipe 传输还继承空 secret 的未鉴权边界。
- XToolpro 只能在 adapter 入口先行限制 body/key，采用 staging 与原子提交，返回脱敏的写入/驱逐摘要和版本；取消或失败时恢复旧快照，并区分 `Success`/`Unavailable`/`Cancelled`/`EngineCrashed`/`VersionMismatch`，在隔离契约测试完成前保持 `Partial`。
- 本轮仅静态读取固定归档，未调用 `/storage`、未写入或删除任何键、未启动 core、未使用 ADB，未读取设备日志、配置、通知、节点、URL、地址、路由、DNS、流量、凭据或 Cookie。

#### 本检查点远端备份状态（2026-09-10，External-controller storage 请求体与驱逐回执契约静态审计）

- focused commit 待创建；本检查点只涉及 `docs/architecture/upstream-capability-parity-matrix.md` 与本 evidence 文件，其他工作树改动和临时产物不纳入；按要求不得自动 push，须先获得用户明确确认。

1. 对每个固定提交完成可重复的真实能力 proof，并保存命令、依赖树、native 库与二进制校验和。
2. 为每个 `engine-*` 定义 success、unavailable、cancel、crash、version mismatch 五类契约测试。
3. 完成 GPL 源码发布方案、完整 SBOM、NOTICE、上游 fork 与补丁同步审查后，才可将台账行从 `Investigating` 改为 `Approved`。
