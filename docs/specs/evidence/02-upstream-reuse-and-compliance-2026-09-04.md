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

- `Get-Command flutter,dart,go` 没有返回任何命令。FlClash 的 `.gitmodules` 声明 `core/Clash.Meta`，而 GitHub ZIP 归档不会包含该 submodule。2026-09-05 已通过官方 contents API 将该 gitlink 锁定到 `0f7f05adff5e2c49775a112dcfe05a6aa36fda0c`，但本机尚未 checkout 该固定子模块。因此在未安装 Flutter/Go、取得该精确子模块和 native core 前，不能诚实地声称已调用其真实代理能力。
- 2026-09-05 已从 `https://github.com/chen08209/Clash.Meta.git` 克隆到 Git 忽略的隔离 proof 工作树，并 detach checkout `0f7f05adff5e2c49775a112dcfe05a6aa36fda0c`；`git rev-parse HEAD` 返回相同值，`LICENSE` 为 GPL-3.0。此操作只补齐上游源码，不引入任何 XToolpro 生产模块，也没有启动构建或设备操作。
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

1. 对每个固定提交完成可重复的真实能力 proof，并保存命令、依赖树、native 库与二进制校验和。
2. 为每个 `engine-*` 定义 success、unavailable、cancel、crash、version mismatch 五类契约测试。
3. 完成 GPL 源码发布方案、完整 SBOM、NOTICE、上游 fork 与补丁同步审查后，才可将台账行从 `Investigating` 改为 `Approved`。
