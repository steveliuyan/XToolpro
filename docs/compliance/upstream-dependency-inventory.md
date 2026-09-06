# 上游直接依赖盘点（Phase 02）

**状态：** 调查中，不是可发布 SBOM

**适用阶段：** [Phase 02：上游复用与合规](../specs/02-upstream-reuse-and-compliance.md)

**需求追踪：** `PRO-*`、`CLN-*`、`MED-*`、`IMG-*`
**盘点日期：** 2026-09-05

## 用途与边界

本文是四个固定上游归档的**直接源码声明依赖**索引，供后续 `engine-*` 隔离 proof、许可证审查和 CycloneDX SBOM 生成使用。它不是完整依赖树、许可证结论、漏洞扫描结果或发布许可声明。

以下项目在真实 Gradle/Flutter 解析成功前均保持“待解析”：传递依赖、版本冲突后的实际选择、variant 条件依赖、插件解析结果、AAR/JAR/SO 内容、原生二进制 SHA-256、许可证文本与安全公告。任何此类项目不得仅凭本文转为 `Approved`。

| 域 | 固定提交 | 本地归档 SHA-256 | 直接来源清单 |
| --- | --- | --- | --- |
| Proxy | `62addf738a76b1a492e19af2dbabdb6d572b9e72` | `70042455690F88D8CFD070F7EA6B9269060C42745BD8F7E90AD7EE3826FA48A8` | `pubspec.yaml`、`.gitmodules`、`android/**/build.gradle.kts`、`android/gradle/libs.versions.toml` |
| Cleaner | `b9b01ee0af648fa6af25d388bb39bacde8d5b7a9` | `3EF93551ECFEAA7CCAFA71772C8EA81C08F1F52EA314D5B0CB45C8310E1FCA4E` | `app-tool-corpsefinder/build.gradle.kts`、相关 `app-common-*/build.gradle.kts`、根构建与 wrapper |
| Media | `13320bb64f35c8d04f01bebfa782d7947758fb66` | `F859B856D846801B42B3063B00CB36C002E6708DC469297836DEB6545FF7C98D` | `app/build.gradle`、根 `build.gradle`、`settings.gradle`、wrapper |
| Image | `cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0` | `CBCA2FB7E9235AD96C89498B0A851D2AFAC4EDF3625BAD37D88C1188439B08C8` | `gradle/libs.versions.toml`、所选 `core/`、`lib/`、`feature/` 模块构建文件与 wrapper |

归档位置在 Git 忽略的 `.tools/upstream-proofs/`；完整取证限制和当前 proof 状态见 [Phase 02 证据](../specs/evidence/02-upstream-reuse-and-compliance-2026-09-04.md)，批准状态以 [上游复用台账](../architecture/upstream-reuse-ledger.md) 为准。

Cleaner 的直接来源表以 `app-tool-corpsefinder` 作为已读取的构建入口，不代表最终范围；完整复用还必须解析并纳入 `app-tool-appcleaner`、`app-tool-systemcleaner`、`app-tool-analyzer`、`app-tool-deduplicator` 及其新增依赖。

## 盘点规则

1. “直接”表示固定提交的 manifest 或构建脚本直接声明；它不表示最终 APK 必然打包该组件。
2. `project(...)`、`projects.*`、path、git 和 submodule 依赖与 Maven 坐标同等记录，因为它们会进入候选 engine 闭包。
3. 记录完整候选 engine 闭包中可能影响运行时的模块；上游 UI、品牌、图标、翻译、演示数据和营销资源不属于可复用范围。
4. build logic 的 `addAndroidCore()` 等辅助方法没有在本次受阻构建中求值；其展开后的外部坐标必须由成功的依赖树确认。
5. 每个 proof 成功后，必须输出解析后的 dependency tree、resolved lockfile/metadata、native 库列表、SHA-256、许可证与来源 URL，并据此更新本文和发布 SBOM。

## Proxy：FlClash

**拟议边界：** 完整复用 Android `core`、`service`、`common`、native core 与 Flutter 到 Android 的 bridge 能力；仅不复用 Flutter UI、资源、字体、图标或本地化内容。对应 `PRO-*`，详见 [ADR-0002](../architecture/ADR/ADR-0002-flclash-proxy-engine-investigation.md)。

| 来源路径 | 直接声明 | 对拟议 proof 的意义 | 状态 |
| --- | --- | --- | --- |
| `.gitmodules` | `core/Clash.Meta`，`git@github.com:chen08209/Clash.Meta.git`，分支 `FlClash` | 原生 core 的必需子模块；GitHub ZIP 不包含其内容。 | 待获取并固定具体 commit |
| `pubspec.yaml` | Flutter SDK（SDK `>=3.8.0 <4.0.0`）、`flutter_localizations`；本地 package `plugins/proxy`、`window_ext`、`wifi_ssid`、`rust_api`、`setup`；git package `window_manager`、`launch_at_startup`、`tray_manager`、`yaml_writer` | 说明该上游应用本身依赖 Flutter package 图；只有 `proxy`、`rust_api`、`setup` 可能与 engine 路径有关，仍需源码和运行 proof 确认。 | 待 Flutter `pub get` 解析 |
| `pubspec.yaml` | 数据/平台相关的直接 package：`path_provider`、`shared_preferences`、`file_picker`、`app_links`、`ffi`、`dio`、`drift`、`drift_flutter`、`crypto`、`device_info_plus`、`connectivity_plus` | 这些是上游 app 的直接声明，不等于 XToolpro engine 的批准依赖；如候选路径实际引用，必须单独审查。 | 待引用分析 |
| `android:app` | projects `:service`、`:common`、`:core`；`androidx.core:core-splashscreen:1.0.1`、`gson:2.13.1`、`smali-dexlib2:3.0.9`、Firebase BOM `34.15.0`、Crashlytics NDK、Analytics | `:app` 是 Flutter Android 容器，不是拟议引擎 API；Firebase 与启动页等 app 层项默认不带入 XToolpro。 | 待裁剪证明 |
| `android:service` | projects `:core`、`:common`；`gson:2.13.1`、`androidx.core:core-ktx:1.17.0` | 这是 VPN/service 候选闭包的一部分。 | 待真实 core 启动 |
| `android:core` | `androidx.annotation:annotation-jvm:1.9.1`；CMake `3.22.1`；NDK `28.2.13676358` | 该模块直接要求 native build；解析出的 core 二进制、ABI 与许可证尚未知。 | 待 native proof |
| `android:common` | `androidx.core:core-ktx:1.17.0`、`gson:2.13.1`、Firebase BOM `34.15.0`、Crashlytics NDK、Analytics | 该通用层仍夹带应用观测依赖，需在 fork-port 设计中明确保留或剔除。 | 待裁剪证明 |
| `android/gradle/wrapper/gradle-wrapper.properties` | Gradle `9.2.1-all` | Flutter/Android proof 的构建分发前置条件。 | 未运行 |

### Proxy Android 依赖裁剪复核（2026-09-06）

| 候选 package | 固定源码平台声明 | Android proof 观察 | 结论 |
| --- | --- | --- | --- |
| `plugins/proxy` | Windows plugin | APK ZIP 成员和 `classes.dex` ASCII 字符串均未包含 `ProxyPlugin` 或 plugin 成员；`proxy` 命中仅为应用空状态图标 | Android engine 路径排除；桌面路径仍受未声明许可阻塞 |
| `plugins/rust_api` | iOS/Linux/macOS/Windows FFI plugin | APK ZIP 成员和 `classes.dex` ASCII 字符串均未包含 `RustLib` 或 `rust_api` plugin 成员；Android `main` 的初始化受 `system.isDesktop` 条件保护 | Android engine 路径排除；桌面路径仍受未声明许可阻塞 |
| `plugins/window_ext` | Windows/macOS plugin | APK ZIP 成员和 `classes.dex` ASCII 字符串均未包含 `WindowExtPlugin` 或 plugin 成员 | Android engine 路径排除；桌面路径仍受未声明许可阻塞 |

`plugins/rust_api/rust/Cargo.lock` 在固定归档中记录 87 个包（SHA-256 `B258BC0B66CBC29884BA75746090B7F5B82FFA5303BB06F68F5228B21A60B843`），其中直接包为 `flutter_rust_bridge`、`interprocess` 和 Windows 条件依赖 `windows-sys`。Cargo.lock 不提供完整许可证字段；这些桌面候选传递依赖仍须通过 `cargo metadata`/许可证数据库逐项审查，不能因 Android APK 排除而视为已批准。

该复核针对固定 Android proof APK SHA-256 `4F374C68570EB4837B94D7026D594237035E18A97B84B27AAEEEBA4FAD7355EC`。它只证明当前产物的依赖裁剪结果，不替代 Flutter `pub get`、Gradle resolved dependency tree、native/传递依赖许可证或未来 engine 构建的重复检查。

## Cleaner：sdmaid-se

**拟议边界：** 完整复用所有支持的 `app-tool-*` 维护能力及其 `app-common-*` 闭包；不复用上游 UI、图标、logo、翻译、文档或市场内容。扫描 proof 可先从非破坏性路径开始，但不能将其作为最终能力范围。对应 `CLN-*`，详见 [ADR-0003](../architecture/ADR/ADR-0003-sdmaid-cleaner-engine-investigation.md)。

下表以 `app-tool-corpsefinder` 的十个 `app-common-*` 模块作为已观察起点；完整 proof 还必须纳入其余 `app-tool-*` 工具及它们在源码中直接拉入的项目与显式坐标。`addAndroidCore`、`addDI`、`addCoroutines`、`addSerialization`、`addIO`、`addRoomDb`、`addCompose`、`addNavigation3`、`addCoilApi` 等约定函数的实际展开待 Gradle dependency tree。

| 模块 | 直接项目依赖（固定源码） | 显式外部坐标（固定源码） | 风险与下一步 |
| --- | --- | --- | --- |
| `app-tool-corpsefinder` | `app-common`、`app-common-ui`、`app-common-io`、`app-common-coil`、`app-common-pkgs`、`app-common-root`、`app-common-shell`、`app-common-data`、`app-common-exclusion`、`app-common-setup` | `com.android.tools:desugar_jdk_libs:${Versions.Desugar.core}` | proof 起点；源码定义的十模块闭包并非最终解析树。 |
| `app-common` | `app-common-test`（测试） | desugar；`androidx.navigation:navigation-common:${Versions.AndroidX.Navigation.core}`；`androidx.navigation3:navigation3-runtime-android:${Versions.AndroidX.Navigation3.core}`；`io.github.z4kn4fein:semver:3.0.0` | 共享模型与导航版本必须由成功配置确认。 |
| `app-common-ui` | `app-common`、`app-common-io`、`app-common-data`、`app-common-test`（测试） | desugar | UI 依赖不应直接跨越 XToolpro feature 边界。 |
| `app-common-io` | `app-common`、`app-common-root`、`app-common-adb`、`app-common-shell`；测试还引用 `app-common-coil` 与 `app-common-test` | desugar；测试 `androidx.test.ext:junit:1.3.0` | `app-common-adb` 是由所选十模块外溢出的项目依赖，不能遗漏。 |
| `app-common-coil` | `app-common`、`app-common-ui`、`app-common-io`、`app-common-data`、`app-common-pkgs`、`app-common-test`（测试） | desugar；`io.coil-kt:coil-compose:2.7.0`（API）；`io.github.panpf.zoomimage:zoomimage-view-coil2:1.4.0` | 上游 Compose 图片展示应与清理 engine 解耦。 |
| `app-common-pkgs` | `app-common`、`app-common-shell`、`app-common-io`、`app-common-root`、`app-common-test`（测试） | desugar；测试 `androidx.test.ext:junit:1.3.0` | 包管理/Root 相关能力仅作为可用性分支，不是 SAF proof 前提。 |
| `app-common-root` | `app-common`、`app-common-shell` | desugar | 需明确在非 Root 场景不可用并验证不提升权限。 |
| `app-common-shell` | `app-common`、`app-common-test`（测试） | desugar；测试 `androidx.test.ext:junit:1.3.0` | shell 调用是高风险边界，须在 engine adapter 内隔离。 |
| `app-common-data` | `app-common`、`app-common-io`、`app-common-pkgs`、`app-common-root`、`app-common-adb`、`app-common-shell`、`app-common-test`（测试） | desugar | 包含数据/ADB/Root 交叉依赖；首次 proof 不允许隐式使用高权限路径。 |
| `app-common-exclusion` | `app-common`、`app-common-ui`、`app-common-io`、`app-common-coil`、`app-common-data`、`app-common-pkgs`、`app-common-test`（测试） | desugar；`io.coil-kt:coil-compose:2.7.0`；`androidx.documentfile:documentfile:1.1.0` | 与排除规则和 SAF 范围有关，需纳入非破坏性扫描测试。 |
| `app-common-setup` | `app-common`、`app-common-io`、`app-common-root`、`app-common-adb`、`app-common-pkgs`、`app-common-shell`、`app-common-test`（测试） | desugar | 设置流程不应成为 XToolpro app-shell 的隐性依赖。 |

根构建直接声明 AGP `9.3.2`、KSP `2.3.6`、Compose 插件 `2.2.10`、Kover `0.9.8`，wrapper 为 Gradle `9.7.1`（SHA-256 `acd53f1edaf02f1a8ff99879f8a34b302661a057d9b063ae9e35b552f804d20a`）。当前 wrapper 下载被受限 HTTPS 阻断，尚未得到可验证的解析结果。

## Media：ytdlnis

**拟议边界：** `app/` 中的完整媒体工作流位于 `engine-media` 后；保留 Cookie/session、用户授权的私有内容和高级格式能力，不复用名称、UI、资产，也不实现 DRM/平台访问控制绕过。对应 `MED-*`，详见 [ADR-0004](../architecture/ADR/ADR-0004-ytdlnis-media-engine-investigation.md)。

| 直接声明类别 | 固定源码中的坐标或配置 | 审查要求 |
| --- | --- | --- |
| 媒体执行组件（`github`、`foss`、`izzy` 三个 variant 均声明） | `io.github.junkfood02.youtubedl-android:library:0.18.1`；`aria2c:0.18.1`；`ffmpeg:0.17.2` | 每个 variant 实际解析物、插件/二进制、ABI、SHA-256、许可证和回滚版本都必须单列；本表不代表获准分发。 |
| 任务与存储 | `androidx.work:work-runtime-ktx:2.11.0`；Room `runtime`、`ktx`、`compiler`、`paging`、`testing` `2.8.4`；Paging `3.3.6`；`com.anggrayudi:storage:1.5.5` | WorkManager/Room 可能进入 task adapter；存储范围须符合 SAF/MediaStore。 |
| 网络、解析和并发 | OkHttp `5.3.2`；Gson `2.13.2`；Kotlinx serialization JSON `1.9.0`；coroutines `1.10.2`；`com.github.teamnewpipe:newpipeextractor:v0.26.5` | URL/站点解析和网络依赖的许可证、更新和合规限制待逐项确认。 |
| 预览/播放 | Media3 ExoPlayer、DASH、HLS、UI、RTSP `1.9.0`；ExoMedia `5.2.0` | 完整媒体能力 proof 必须验证可复用的预览/播放路径；UI 展示由 XToolpro shell 重建，但不能因首个下载 proof 而从最终能力基线删除。 |
| 上游应用层与终端相关 | AppCompat `1.7.1`、Material `1.10.0`、Compose UI `1.10.1`、Picasso、Markwon、Termux `terminal-view`/`termux-shared`/`terminal-emulator` `v0.118.3` | 上游 UI/终端实现默认不进入 XToolpro；不得因存在这些声明而扩大集成范围。 |
| 构建 | Gradle wrapper `8.13`；根脚本 AGP `8.13.2`、Kotlin `2.3.0`、KSP `2.3.4`；`settings.gradle` 还包含 `:common`、`:library`、`:ffmpeg` | 当前在 wrapper HTTPS 下载前失败，尚无 dependency tree 或 APK/native 清单。 |

## Image：ImageToolbox

**拟议边界：** 从 `core/`、`lib/` 和所有支持的非 UI `feature/` 处理代码形成完整 `engine-image` 能力闭包；不复用上游导航、页面、图标、品牌或 demo 内容。对应 `IMG-*`，详见 [ADR-0005](../architecture/ADR/ADR-0005-imagetoolbox-image-engine-investigation.md)。

Image 的直接来源表先列出当前已读取的处理模块；最终 proof 必须扩展到固定提交中所有支持的非 UI 处理、编解码、批处理、元数据、OCR/模型和格式路径。

下表是固定提交中为基础编辑、裁剪、转换、压缩和滤镜路径读到的直接项目依赖。feature 模块普遍通过 convention plugin 获得公共依赖，因此没有被明确写在模块 `dependencies` 块中的组件仍待解析。

| 来源模块 | 直接项目依赖 | 直接外部坐标/版本目录别名 | 说明 |
| --- | --- | --- | --- |
| `core:domain` | `core:resources` | JUnit（测试） | 基础领域层；其可独立移植性待源码引用分析。 |
| `core:data` | `core:domain`、`core:resources`、`core:filters`、`core:settings`、`core:di`、`core:utils` | Coil 3.6.1、Ktor 3.5.2、DataStore 1.3.0-alpha10、AVIF coder 3.0.0-alpha15、JXL coder 2.6.2、Jixel 3.0.0-alpha04、Aire 0.18.1、JPEGli 1.0.2、Moshi 1.15.2、DocumentFile 1.1.0，以及 ImageToolboxLibs `gif-converter`、`exif`、`raw-coder`、`tiff-coder`、`qoi-coder`、`jp2decoder`、`awebp`、`psd`、`apng`、`djvu-coder` | 编解码与文件 I/O 的高风险汇合层；待拉取解析物与 native 清单。 |
| `core:filters` | `core:domain`、`core:ui`、`core:resources`、`core:settings`、`core:utils`、`core:ksp`、`lib:curves`、`lib:ascii`、`lib:neural-tools` | Trickle 1.9.3 | 处理实现与 UI/AI 路径并未天然分离，需先削出无 UI 的受控闭包。 |
| `lib:image` / `lib:cropper` | `lib:gesture`；cropper 还依赖 `core:resources` | Palette 1.1.0-alpha01；Coil 3.6.1；ImageToolboxLibs `exif` | 候选基础编辑/裁剪支持库。 |
| `feature:resize-convert` / `feature:format-conversion` | `feature:compare`、`feature:settings` | 无额外直接坐标 | 上游页面入口对应的处理能力必须纳入 engine contract，但页面层实现不直接作为 XToolpro engine API。 |
| `feature:crop` | `lib:opencv-tools`、`lib:cropper` | ImageToolboxLibs `advanced-crop` | 裁剪路径涉及 OpenCV 及第三方 native 组件。 |
| `feature:single-edit` | `feature:crop`、`feature:erase-background`、`feature:draw`、`feature:filters`、`feature:pick-color`、`feature:compare`、`feature:settings`、`lib:curves`、`lib:cropper` | 无额外直接坐标 | 是上游编辑能力聚合层；处理逻辑纳入 engine，页面编排由 XToolpro UI 重建。 |
| `feature:filters` | `core:filters`、`core:ksp`、`feature:draw`、`feature:pick-color`、`feature:compare`、`feature:archive-tools`、`lib:opencv-tools`、`lib:neural-tools`、`lib:curves`、`lib:ascii`、`lib:archive` | Kotlin reflect；Aire；Trickle；ImageToolboxLibs `gpuimage`、`jhlabs`、`gmic` | 包含可能的 native/AI/处理依赖，必须先完成逐件许可和二进制审计。 |
| `lib:opencv-tools` / `lib:neural-tools` | 前者依赖 `lib:image`、`lib:zoomable`、`lib:gesture` | OpenCV `5.0.0.1`；ONNX Runtime Android `1.29.0`；Aire 0.18.1；ImageToolboxLibs `exif` | 非基础 MVP 必须在 feature flag 后，并完成 ABI、模型与隐私审查。 |

固定版本目录还直接列出可能进入完整图片处理 closure 的 ImageToolboxLibs `androidwm`、`apng`、`advanced-crop`、`awebp`、`gif-converter`、`gmic`、`gpuimage`、`histogram`、`jp2decoder`、`jhlabs`、`qoi-coder`、`raw-coder`、`tiff-coder`、`exif` 等，均为版本 `8.0.6`。这只是 source catalog 观察，尚未证明任何一个组件会由 engine 解析或被允许分发。

wrapper 为 Gradle `9.7.1`；根构建直接声明 AGP `9.3.2`、Kotlin `2.4.10`、Hilt `2.60.1`、KSP `2.3.11`、Detekt `1.23.8`、AboutLibraries `15.2.0` 等构建插件。当前官方分发下载未完成，故不存在有效解析依赖树或真实图像处理 proof。

## 进入批准前的补证清单

对每个候选 `engine-*`，必须在隔离目录中完成以下顺序，才可更新台账状态：

1. 通过官方分发和固定提交完成真实 build/proof，不使用未审查镜像。
2. 导出每个受选 variant 的 runtime 与 build dependency tree；将约定插件、path/git/submodule 的实际解析结果纳入清单。
3. 枚举 AAR/JAR/SO、媒体插件、模型和 CLI，保存来源 URL、版本、ABI、SHA-256、许可证与版权声明。
4. 对 GPL 路线补齐对应源代码提供方式、fork、补丁序列、可复现构建和回滚 commit；对 Apache 路线保留所需 NOTICE。
5. 用真实上游能力验证 success、unavailable、cancel、crash、version mismatch 五类契约场景，见 [engine 契约测试计划](../architecture/engine-contract-test-plan.md)。
6. 由维护人 `steveliuyan` 完成许可证、安全、隐私、应用商店和回滚复核后，才允许将对应行改为 `Approved`。
