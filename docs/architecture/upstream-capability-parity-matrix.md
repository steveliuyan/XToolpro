# 上游完整能力对照矩阵

**状态：** Phase 02 基线；FlClash 已记录 2026-09-06 真机 capability-parity 结果，其余固定提交待逐项确认

**适用范围：** `PRO-*`、`CLN-*`、`MED-*`、`IMG-*`

## 使用规则

这份矩阵是四个固定上游提交的完整能力基线。`XToolpro 映射`描述能力应位于哪个 `engine-*` 合同后；它不允许把上游能力改写成相似的自研版本。`Verified` 表示该合并行内的能力均有真实上游行为证据；`Partial` 表示只验证了其中一部分或仅确认真机入口；`Pending` 表示仍未形成足够真机证据。每一行必须有真实上游调用、依赖/许可证记录和对应测试证据后，才能将状态改为 `Verified`。

允许替换的内容只有 XToolpro 品牌、图标、翻译、统一导航、任务/通知壳和 Android 平台适配。上游明确排除的品牌材料、未声明许可的组件、设备不支持的能力或合规禁止的绕过流程，必须记录为 `Blocked` 或 `Unavailable`，不得静默删除。

## FlClash：代理能力

固定提交：`62addf738a76b1a492e19af2dbabdb6d572b9e72`
引擎：`engine-proxy`
来源记录：[ADR-0002](ADR/ADR-0002-flclash-proxy-engine-investigation.md)、[依赖盘点](../compliance/upstream-dependency-inventory.md)

| 上游能力 | 来源路径/组件 | XToolpro 映射 | 状态 |
| --- | --- | --- | --- |
| YAML、剪贴板、URL/订阅导入与导出 | `lib/views/profiles/add.dart`、`lib/widgets/input.dart`、`lib/providers/actions/profiles.dart`、`lib/common/link.dart`、`android/`、`core/` | 配置导入、校验、备份恢复、错误回退 | Partial：真机识别文件配置；确认二维码、文件、URL 添加入口；配置导出经 SAF 提交，系统文件选择器中配置卡片标签与导出文件名精确匹配 1 项，未打开或读取文件；本地备份经 SAF 保存后应用报告成功，系统 ZIP 选择器可见对应文件；独立配置剪贴板导入在固定 Android 包中为 Unavailable，源码和真机添加页均只有二维码、文件、URL 三项，URL 文本框长按未显示“粘贴”动作且未改写剪贴板，标准文本粘贴仍未验证。固定 Android manifest 未声明标准 `ACTION_SEND`，真机 package-restricted `ACTION_SEND` 文本分派也在启动前被 Android 拒绝；该入口为 Unavailable。固定包声明 `clash`、`clashmeta`、`flclash` 的 `install-config` 深链接；真机以公开保留域名分别在应用已初始化和强停后的冷启动状态触发 `flclash`，均显示一次导入确认并返回取消；对 `clash`、`clashmeta` 的 package-restricted `ACTION_VIEW` 也各显示一次确认并取消，均未写入或联网。未读取全局默认处理者，故不外推系统 resolver 选择。该入口将 URL 置于 deep-link query，不是系统分享文本入口；未验证备份恢复。源码隐私审计：`LinkManager` 将完整 URI（含 `url` query）传给 `commonPrint.log`；app attach 后该未脱敏 payload 同时进入 `debugPrint` 和最多 500 项的内存 `logsProvider`，并可在日志页作为可选择文本显示。日志页的显式导出会把每条 `Log.toString()` 未脱敏拼接后经临时文件交给系统文件选择器；临时文件随后删除。`openLogs` 只控制 core log capture 与日志入口显示，不拦截 `commonPrint` 的写入。URL 下载失败时，`getFileResponseForUrl` 还会将原始 `DioException.toString()` 送入 `commonPrint`；自动更新会记录再次抛出的异常，批量更新则把原始异常文本与配置标签交给 UI。固定 Android `FilesProvider` 为受 `MANAGE_DOCUMENTS` 限制的导出 `DocumentsProvider`，但把整个 app `filesDir` 作为根目录，列出子项并允许按请求模式打开；canonical path 检查阻止越出根目录，普通文件仍标记为可写。未枚举设备文件或验证系统 URI 授权；XToolpro 不得用该整目录 provider 代替显式、最小化的 SAF 导出。静态源码不足以断言任一运行时异常必含 URL，且本轮未发送真实订阅 URL。XToolpro 在任何 engine adapter/诊断导出获批前必须在边界处把失败原因归一为稳定、脱敏的类别或代码，禁止未脱敏订阅 URL 或异常文本进入 UI、应用/系统日志、导出或遥测；当前不构成正式集成批准 |
| 订阅更新、重命名、复制、删除和持久化 | `lib/views/profiles/profiles.dart`、`lib/views/profiles/edit.dart`、`lib/providers/actions/profiles.dart`、配置服务 | 配置仓库和版本化变更 | Partial：真机确认编辑、预览、覆写和删除入口；后续当前配置出现仅限 URL 类型的“同步”和“复制链接”，编辑页存在 3 个非空输入框、自动更新项和保存动作，未读取字段值或提交变更；固定 Android UI/源码没有复制或克隆配置动作，“复制链接”不能替代配置复制；未执行订阅更新、重命名、删除或持久化行为验证 |
| 规则/全局/直连模式 | `core/Clash.Meta`、配置路径 | 模式选择与状态持久化 | Verified：真机确认三种模式；全局和直连均经强停重启保持；直连模式下公开 HTTPS 返回 `200` 且 TUN 收发计数增长，结束时恢复规则模式并再次强停重启确认 |
| 代理组展开、节点切换和当前节点 | `core/`、Android bridge | 代理组/节点页面与运行状态 | Partial：规则模式下真机显示多个代理组、节点卡，以及“自动选择”“故障转移”组入口；固定源码将节点点击写入当前 Profile 的 `selectedMap` 和 Drift `profiles.selected_map`；设备内只读查询确认当前 Profile 的映射非空、当前组选择非空，强停重启前后条目数与规范化 SHA-256 完全一致，且未输出组名、节点名或原始值；直连模式按上游逻辑隐藏代理入口，恢复规则模式后入口重新出现；无敏感 UI 语义仍不能独立标识所选卡片，且未覆盖多个组/节点切换，故保持 `Partial` |
| 节点协议、地区、标签筛选 | `core/`、Clash.Meta provider | 筛选、排序和可用性标记 | Partial：固定 Clash.Meta provider 源码存在 `filter`/`exclude-filter` 正则字段；真机代理页未出现独立协议、地区或标签筛选控件，当前仅能确认上游内部字段，未验证用户可见筛选行为 |
| 单节点与批量测速 | `core/`、服务层 | 测速任务、结果和失败原因 | Verified：单节点返回 `103 ms`；当前组批量测速显示 10 个结果节点和 9 个不同延迟值 |
| Android VPN 启停与竞争 VPN 处理 | `android/service`、`android/core`、`android/service/.../NotificationModule.kt`、`lib/common/preferences.dart` | `VpnService` 生命周期和权限流程 | Partial：真机多次完成启动、停止和恢复；规则模式下应用退到后台后，系统 VPN 与 `tun0` 归属仍存在，公开 HTTPS 返回 `204` 后仍保持连接，回到应用可正常停止并清理。运行态强停应用后 VPN/TUN 均被清理，重新启动后不自动残留连接；系统通知服务中与 FlClash 匹配的记录计数在 VPN 停止后从 13 降至 5，未读取通知正文。源码隐私审计：Flutter `currentProfile.label` 被传为 `NotificationParams.title`，前台服务更新时将其写入 Android 系统通知标题；初始中性 `FlClash` 标题随后会被覆盖，正文仅来自速度/流量文本，停止按钮来自本地化文案。Flutter 还将完整 Android 启动 `SharedState` 作为 JSON 写入 SharedPreferences，供原生读取；字段含测试 URL、配置标签、代理组选择映射、按应用路由包名列表和绕过域名，未见该路径的应用层加密。未读取设备通知或偏好内容，且不外推具体当前值；XToolpro 必须使用中性固定标题，且将 native 启动状态最小化、加密并禁止持久化订阅 URL、节点/组名或应用选择。未验证竞争 VPN、首次授权拒绝或撤销 |
| 始终开启、断线阻止 | Android VPN bridge、系统能力 | 能力检测、系统设置入口和明确 unavailable 状态 | Partial：ADB 可打开 Android `Settings$VpnSettingsActivity`；secure settings 显示始终开启 VPN 未配置、lockdown 未启用；未验证 FlClash 设置入口的写入行为或断线阻止实际效果 |
| TUN、系统代理、局域网共享 | `android/service/src/main/java/com/follow/clash/service/VpnService.kt`、`core/`、Android bridge | 运行模式和局域网开关 | Partial：`tun0` 与真实 HTTPS 流量已验证；真机“系统代理”为开启，运行态 Android VPN `LinkProperties` 报告 loopback `7890` HTTP proxy，停止后对应代理消失；停止态显式连接该 HTTP proxy 返回状态 `000`、退出码 `7`，未保留可用监听。设备在运行态显式通过 `127.0.0.1:7890` HTTP 代理并禁用 `NO_PROXY` 访问公开 HTTPS，返回 `204`，并以同一端口的 SOCKS5 hostname 模式再次返回 `204`，证明该 loopback mixed port 可实际转发两种 TCP 代理协议；同端点 4 个并发 HTTP proxy 请求均返回 `204`。“局域网代理”为关闭时 mixed port 的 4 条 TCP 监听均仅绑定 loopback且无 wildcard；临时开启后强停重启仍为开启，运行态 mixed port 有 8 条监听且 wildcard `:7890` 计数为 1，公开 HTTPS 返回 `200`，停止并恢复关闭后再次强停重启仍为关闭。该 proof 只验证本机 loopback TCP 功能性并发转发和监听边界，不从其他局域网设备发起连接，故不外推为 UDP、性能压力或跨设备共享访问通过 |
| 查找进程模式 | `lib/views/config/general.dart`、`lib/providers/state.dart`、`lib/models/clash_config.dart`、`lib/models/core.dart` | 进程识别与请求归因开关 | Partial：固定源码将“查找进程”开关映射为 `FindProcessMode.always/off`，并传入 `UpdateParams.findProcessMode`/`find-process-mode`；真机初始为关闭，临时开启后强停重启仍为开启，恢复关闭后再次强停重启仍为关闭，期间未建立 VPN 且结束时 `VPN CONNECTED=0`、`tun0` 不存在；未验证实际进程识别、请求归因或性能开销 |
| TCP 并发 | `lib/views/config/general.dart`、`lib/providers/state.dart`、`lib/models/clash_config.dart`、`lib/models/core.dart` | TCP 连接并发参数 | Partial：固定源码将“TCP 并发”绑定到 `state.tcpConcurrent`，并传入 `UpdateParams.tcpConcurrent`/`tcp-concurrent`；真机初始为开启，临时关闭后强停重启仍为关闭，恢复开启后再次强停重启仍为开启，期间未建立 VPN 且结束时 `VPN CONNECTED=0`、`tun0` 不存在；未验证实际并发连接行为或性能变化 |
| 统一延迟 | `lib/views/config/general.dart`、`lib/providers/state.dart`、`lib/models/clash_config.dart`、`lib/models/core.dart` | 延迟测量显示策略 | Partial：固定源码将“统一延迟”绑定到 `state.unifiedDelay`，并传入 `UpdateParams.unifiedDelay`/`unified-delay`；真机初始为开启，临时关闭后强停重启仍为关闭，恢复开启后再次强停重启仍为开启，期间未建立 VPN 且结束时 `VPN CONNECTED=0`、`tun0` 不存在；未验证测速结果是否实际去除握手等额外延迟 |
| 测速链接与延迟测试输入 | `lib/views/config/general.dart`、`lib/providers/config.dart`、`lib/providers/actions/proxies.dart`、`lib/core/interface.dart`、`lib/common/compute.dart` | 测速 URL 配置与校验 | Partial：固定源码将“测速链接”绑定到 `appSettingProvider.testUrl`，要求非空且通过 URL 校验，并在代理测速链路作为 `test-url` 参数传给 core；真机对当前公开测试链接打开编辑对话框，非法输入显示“测速链接必须为 URL”，取消后原值仍为 `https://www.gstatic.com/generate_204`。随后保存公开 Cloudflare 204 URL，强停重启后基本配置页仍显示该 URL；建立 VPN/TUN 后从代理页触发延迟测试，得到 6 个可见延迟值 `57`、`69`、`91`、`96`、`104`、`138 ms`，之后停止 VPN 并通过“重置”恢复默认公开 URL。未捕获核心请求，不能独立证明每个测速请求的实际目标或全部节点/组的行为 |
| IPv6、DNS、Fake-IP/Host、流量嗅探 | `lib/views/config/network.dart`、`lib/views/config/general.dart`、`lib/providers/actions/setup.dart`、`lib/common/task.dart`、`android/service/src/main/java/com/follow/clash/service/VpnService.kt`、`core/Clash.Meta` | DNS/IPv6/嗅探配置和诊断 | Partial：真机确认 `IPv6=false`、`DNS 劫持=false` 的关闭边界；临时开启 DNS 劫持后强停重启仍为开启，源码将开启值传为 `Core.startTun(dns = "0.0.0.0")`，同时 Android VPN 仍发布固定 DNS stub；开启态 `tun0` 有 1 条 IPv4、0 条全局 IPv6 地址，系统 VPN、DNS stub 与 `tun0` 各匹配 1 项，公开主机名解析成功且 HTTPS 返回 `200`；停止并恢复关闭后再次强停重启仍为关闭。临时开启 IPv6 后强停重启仍为开启，开启态 `tun0` 有 1 条 IPv4 和 1 条全局 IPv6 地址，VPN `LinkProperties` 含 IPv4/IPv6 DNS stub 各 1 项、`::/0` 路由匹配 2 项，公开 HTTPS 返回 `200`；停止并恢复 `IPv6=false` 后再次强停重启仍为关闭。追加系统 DNS 开关初始为关闭，临时开启后强停重启仍为开启；开启态 `tun0` 地址行数为 2、系统 VPN 为 1、IPv4 DNS stub 匹配为 1、IPv6 DNS stub 匹配为 0，公开 HTTPS 返回 `200`，随后恢复关闭并再次强停重启仍为关闭。固定源码把该开关传入 profile 构造，并向 `rawConfig['dns']['nameserver']` 追加 `system://`；本轮未读取生成配置，不能证明该值已实际写入最终 nameserver 列表。强制指定 DNS 服务器的请求在 DNS 劫持两态均成功，不能区分 TUN 捕获与代理侧解析，尚未形成任意目标 DNS 的包级捕获证据；Fake-IP/Host、流量嗅探仍未验证 |
| 按应用代理/绕过 | `core/`、`lib/views/access.dart`、Android `AppPlugin`/`PackageResolver` | 应用选择与路由策略 | Partial：真机进入访问控制页，页面说明选中应用将被排除在 VPN 之外；“允许应用绕过 VPN”初始为开启，临时关闭后强停重启仍为关闭，关闭态可建立并停止 `VpnService`/`tun0`，恢复开启后再次强停重启仍为开启；固定源码把该值传入 `VpnOptions.allowBypass` 并在建 VPN 时调用 `VpnService.Builder.allowBypass()`。2026-09-07 当前固定包在目标设备未获取到可选应用列表，ADB UI tree 仅见页面说明、无应用条目或错误/重试控件；包已声明且被授予 `QUERY_ALL_PACKAGES`。固定源码显示页面只在首次创建时调用一次 `getPackages()`，空结果直接渲染“无数据”，没有错误态或重试入口；无法在不读取应用清单或原始诊断的前提下判定是 MIUI、包查询或 bridge 的具体根因。Android 13 `dumpsys` 未暴露 `allowBypass` 字段，故未选择应用或验证应用主动绕过后的实际流量路径 |
| 域名/IP/GeoIP 规则集和命中日志 | `core/` | 规则管理、命中详情和脱敏日志 | Partial：真机确认附加规则入口和规则模式；用户在设备侧启用日志捕获后，规则模式 VPN/TUN 运行期间的一次公开 HTTPS 请求返回 `204`，并在设备日志页人工确认新增记录。ADB 仅确认日志入口/系统 VPN 状态，不读取或导出日志正文、规则、节点或配置；尚未证明具体规则条目命中或命中详情 |
| 实时上下行速率、会话流量、连接列表 | `core/`、service bridge | 实时状态、历史摘要和任务事件 | Verified：运行态显示实时/累计流量；公开 HTTPS 流量后连接页显示 10 个可见记录 |
| 请求、规则、内核日志与崩溃诊断 | `core/`、service bridge、`android/common/GlobalState.kt` | 脱敏诊断导出 | Partial：直连公开 HTTPS 流量后请求页显示 10 个可见记录，其中 6 个 accessibility 节点带 `DIRECT` 路由语义；临时启用 `info` 和日志捕获后日志页显示 6 条可见/部分可见 `info` 记录，随后恢复原设置；规则模式早期重测返回 HTTP `000`，但用户在设备侧选择可用路径后，规则模式 VPN/TUN 建立且公开 Cloudflare 204 HTTPS 返回 `204`，请求页可见且未出现 `DIRECT` 路由语义。随后用户开启日志捕获，规则模式下另一公开 HTTPS `204` 请求后，在设备日志页人工确认新增记录；强停重启后 VPN 未自动建立且“日志”入口仍可见。ADB UI tree 只暴露该入口，未读取或导出任何日志正文，也不能直接读取日志捕获开关值。该结果证明真机可见的日志捕获事件与入口跨进程保留，不证明具体规则命中详情、日志字段完整性或崩溃诊断。源码隐私审计：固定 Android 模块引用 Firebase Analytics 与 Crashlytics NDK，服务同步时将 Flutter `crashlytics` 设置传给 `FirebaseCrashlytics.isCrashlyticsCollectionEnabled`；Dart 默认值为关闭，但应用启动的“上次崩溃”查询仍会初始化 Firebase，且 manifest 未提供静态默认禁用声明。固定源码未发现直接把 `commonPrint`、URI 或配置字段提交给 Crashlytics 的调用，也未对 SDK 自动采集/上传字段做设备或网络验证。XToolpro 不得直接复用该第三方遥测路径；必须默认不初始化/不上传，取得明确同意后才可启用，并以脱敏契约测试和独立隐私审查证明 |
| 内核版本、更新和回滚 | `lib/views/about.dart`、`lib/common/request.dart`、`android/core/src/main/cpp/CMakeLists.txt` | 组件管理、校验和、回滚 | Unavailable（固定 Android 包）：真机关于页仅显示应用版本；“检查更新”检查 FlClash 应用 Release，“内核”只打开上游源码链接；未提供运行时内核版本、内核更新或内核回滚控件，`libclash.so` 在构建期链接，更新需随受审计的 engine/APK 发布 |
| 启动连接、自动更新、快捷方式/小组件 | `android/`、`QuickActionActivity`、`TileService`、`ServiceBroadcastReceiver`、`lib/manager/tile_manager.dart`、平台集成 | 后台入口和设备能力开关 | Partial：真机临时开启“自动运行”后，应用强停再打开会自动建立 `VpnService`、`tun0` 和系统 VPN；恢复关闭后再次强停再打开保持停止，确认开关行为和持久化；自动检查更新开关可见，手动更新检查完成 Release 元数据查询并返回当前应用已是最新版，未进入下载/安装分支。固定源码在应用初始化时通过 `ShortcutManagerCompat` 注册动态 `toggle` 快捷方式，真机启动主界面后系统仅按该固定 ID 计数为 1，且 `VPN CONNECTED=0`、`tun0` 不存在。导出 `QuickActionActivity` 将 START/STOP action 分派至 `ServiceState`；真机在 VPN 停止态触发 START 后，系统报告 `VPN CONNECTED=1`，但 14 秒内始终未见 `tun0`，故未发起流量测试。再次先初始化主界面再触发同一 action，7 秒后仍为 `VPN CONNECTED=1`、`tun0` 不存在，排除冷启动时机作为该现象的充分解释；随后 STOP action 均使 `VPN CONNECTED=0`、`tun0` 不存在。强停固定包后直接目标启动同一 START action，20 秒后仍为 `VPN CONNECTED=1`、无 `tun0`，以 STOP action 又在 5 秒后恢复停止；固定源码仅在 `MainActivity` 附着 Flutter engine，此 intent 不启动该 Activity，故已覆盖原生 fallback 但仍未形成 TUN。固定 Android manifest 还声明了 `TileService`；在停止态临时添加并点击该精确系统快捷设置磁贴后，7 秒和 14 秒均为 `VPN CONNECTED=1`、无 `tun0`，未发起流量。第二次磁贴点击及移除磁贴后请求仍未自行清理，已用既有固定 STOP action 恢复为 `VPN CONNECTED=0`、无 `tun0`。源码显示 QuickAction 与磁贴共用 `ServiceState`：engine 已附着时该路径仅经 `TilePlugin` 向 Dart `TileManager` 的监听者发送 `start`/`stop`，并跳过 Android 偏好读取、core setup 和 `ServiceController.start`；无 engine 时才走原生 fallback。`VpnService` 只有在 `Builder.establish()` 取得 descriptor 后才调用 `Core.startTun`，但上层 `ServiceController` 的运行状态不独立核验 `tun0`、core 健康或可转发流量；故系统 VPN 连接不能作为任一入口可用的充分证据。固定 `ServiceBroadcastReceiver` 虽为导出 receiver，但以 `${applicationId}.permission.RECEIVE_BROADCASTS` 的签名级权限保护，且只接受 `VPN_START_REQUESTED`、`VPN_REVOKED`；这条系统回调边界与无权限的 `QuickActionActivity` 不同，不能降低后者的跨应用状态变更风险。完整 manifest 审计显示 `TileService` 以 `BIND_QUICK_SETTINGS_TILE` 受系统绑定保护，`VpnService`、`ProxyService` 均不导出；但磁贴点击仍显式转到无权限 `QuickActionActivity`，系统 service 的绑定权限不能覆盖这条下游状态变更入口。`quickSetup` 非空返回值和启动异常还会未经脱敏进入 Android `Log.d`，前者同时进入 Toast；不得将其作为 XToolpro 的 UI、默认日志、诊断导出或遥测字段。`QuickActionActivity` 又是无组件权限的导出 Activity，且未验证调用者，仅按 action 调度 START/STOP/TOGGLE；XToolpro 不得复用该跨应用状态变更入口。该 UI 生命周期依赖与真机现象一致，但静态源码不能确定已附着分支试验时的 engine/事件状态，未形成根因证明。该结果只证明动态快捷方式、action 与磁贴启动入口可达，不证明任一入口可独立完成可用 VPN/TUN 生命周期；固定 Android 包未声明静态快捷方式或 AppWidget，相关能力为 Unavailable |

## sdmaid-se：设备维护能力

固定提交：`b9b01ee0af648fa6af25d388bb39bacde8d5b7a9`
引擎：`engine-cleaner`
来源记录：[ADR-0003](ADR/ADR-0003-sdmaid-cleaner-engine-investigation.md)、[依赖盘点](../compliance/upstream-dependency-inventory.md)

| 上游能力 | 来源路径/组件 | XToolpro 映射 | 状态 |
| --- | --- | --- | --- |
| 残留、缓存、系统缓存、缩略图、日志、临时文件 | `app-tool-corpsefinder`、`app-tool-systemcleaner` | 分类扫描、规则解释和候选预览 | Pending |
| 空目录、安装包、大文件 | `app-tool-systemcleaner`、`app-tool-analyzer` | 扫描结果、排序、定位和安全策略 | Pending |
| 卸载残留识别、排除和误报反馈 | `app-tool-corpsefinder`、`app-common-exclusion` | 规则详情、排除项和反馈状态 | Pending |
| 应用缓存和应用清理 | `app-tool-appcleaner`、`app-common-pkgs` | 按应用清理、批量操作和风险确认 | Pending |
| 系统清理和高风险项目 | `app-tool-systemcleaner`、`app-common-root` | 权限检测、二次确认和不可用原因 | Pending |
| 重复文件名称/大小/哈希分组 | `app-tool-deduplicator` | 分组、保留建议、逐项选择和预览 | Pending |
| 图片、视频、文档重复项分类 | `app-tool-deduplicator` | 类型筛选、缩略图和结果汇总 | Pending |
| 存储目录树、类型分布、最大/最近文件 | `app-tool-analyzer` | 存储分析图表、列表和定位 | Pending |
| 路径搜索、排序、最近文件和收藏保护 | `app-tool-analyzer`、common data | 搜索、排序、保护规则 | Pending |
| SAF 范围内浏览、重命名、移动、复制、分享 | `app-common-io`、`app-common-shell` | 文件操作事务和结果验证 | Pending |
| SAF 删除、回收站、恢复、安全擦除 | `app-common-io`、清理工具 | 预览、暂存、恢复和不可恢复提示 | Pending |
| 已安装/系统应用列表与信息导出 | `app-tool-appcleaner`、`app-common-pkgs` | 应用信息页和显式导出 | Pending |
| 冻结、停用等高风险操作 | `app-tool-appcleaner`、Root/Shizuku 路径 | 权限门禁、二次确认和回滚状态 | Pending |
| Root、Shizuku、ADB 可选增强 | `app-common-root`、`app-common-adb` | 能力检测；无权限时不得伪造成功 | Pending |
| 定时扫描、Wi-Fi/充电、低电量策略 | 工具设置与 WorkManager 路径 | 后台计划、通知和跳过原因 | Pending |
| 通知摘要、执行历史、白名单/黑名单 | common data、设置路径 | 任务中心、策略导入导出和审计 | Pending |

## ytdlnis：媒体能力

固定提交：`13320bb64f35c8d04f01bebfa782d7947758fb66`
引擎：`engine-media`
来源记录：[ADR-0004](ADR/ADR-0004-ytdlnis-media-engine-investigation.md)、[媒体阶段规范](../specs/07-media-module.md)

| 上游能力 | 来源路径/组件 | XToolpro 映射 | 状态 |
| --- | --- | --- | --- |
| URL、批量 URL、系统分享、剪贴板识别 | `app/` intent、输入流程 | 解析入口和批量校验 | Pending |
| 播放列表/频道、部分选择、增量下载 | `app/`、yt-dlp 参数路径 | 队列拆分、跳过已下载和进度 | Pending |
| 标题、作者、封面、时长、描述、章节、元数据 | yt-dlp/NewPipe extractor | 真实详情页和任务快照 | Pending |
| 视频、音频、缩略图、字幕、元数据下载 | yt-dlp、FFmpeg | 格式任务和输出验证 | Pending |
| 容器、编码、分辨率、帧率、音质、语言选择 | yt-dlp format selection | 格式选择器和能力提示 | Pending |
| 音视频合并、音频提取、转码 | FFmpeg | 后处理任务、取消和恢复 | Pending |
| 嵌入封面/字幕、元数据写入、时间裁剪 | FFmpeg/yt-dlp | 后处理选项和结果验证 | Pending |
| Cookie 文件、浏览器 Cookie、登录会话 | `app/` session flow | 加密本地存储、撤销、删除和授权提示 | Pending |
| 用户授权的私有、付费和高级格式 | yt-dlp authenticated path | 会话任务；仅限用户有权访问内容 | Pending |
| 并发、暂停、恢复、取消、重试、断点 | WorkManager、Room、task flow | 持久任务状态机和通知 | Pending |
| 命名/路径模板、冲突策略、按列表分目录 | `app/` settings/template flow | 模板预览、SAF 输出和原子提交 | Pending |
| 历史、取消记录、备份恢复 | Room、设置/备份路径 | 任务历史和可恢复快照 | Pending |
| 后台通知、完成动作、开机恢复 | WorkManager、intent entry | 后台任务和恢复策略 | Pending |
| yt-dlp、FFmpeg、Aria2c、Python/JS runtime 管理 | `app/` component management | 版本、来源、校验、更新和回滚 | Pending |
| 终端和自定义命令 | Termux components、command path | 明确授权、沙箱边界和脱敏输出 | Pending |

## ImageToolbox：图片能力

固定提交：`cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0`
引擎：`engine-image`
来源记录：[ADR-0005](ADR/ADR-0005-imagetoolbox-image-engine-investigation.md)、[图片阶段规范](../specs/08-image-module.md)

| 上游能力 | 来源路径/组件 | XToolpro 映射 | 状态 |
| --- | --- | --- | --- |
| 裁剪、旋转、翻转、透视、画布扩展、缩放 | `feature:crop`、`lib:cropper`、`lib:image` | 非破坏编辑会话和预览 | Pending |
| 撤销/重做、比较、实时预览 | `core:domain`、`feature:single-edit`、`feature:compare` | 编辑状态、历史和差异预览 | Pending |
| 亮度、对比度、饱和度、色相、伽马、曝光、温度、色阶、曲线 | `core:filters`、`lib:curves` | 参数面板和确定性导出 | Pending |
| 锐化、模糊、噪点、像素化和滤镜 | `core:filters`、`lib:*` filters | 滤镜列表、预览和批处理 | Pending |
| 画笔、形状、箭头、文字、贴纸、马赛克、边框、圆角、阴影、背景、图层 | `feature:draw`、编辑 feature paths | 图层编辑器和导出 | Pending |
| JPEG/PNG/WebP/HEIF 及其他支持格式转换 | `feature:format-conversion`、image codecs | 格式、质量、色彩空间和 DPI | Pending |
| 目标大小/质量压缩和尺寸控制 | `feature:resize-convert`、`core:data` | 压缩预览、估算和结果验证 | Pending |
| 批量裁剪、缩放、重命名、转换、压缩、水印、滤镜 | batch/tool feature paths | 批量任务、单项重试和汇总 | Pending |
| 拼图、长图、网格、图片叠加 | collage/composition feature paths | 布局编辑和输出 | Pending |
| 前景/背景替换、擦除背景 | `feature:erase-background`、`lib:neural-tools` | 模型能力检测、离线处理和结果确认 | Pending |
| EXIF 查看/编辑/清除、颜色/尺寸信息、哈希 | `core:data`、metadata paths | 元数据策略、信息面板和校验 | Pending |
| 取色器、调色板、曲线和颜色比较 | `feature:pick-color`、`lib:curves` | 取色、调色和编辑参数 | Pending |
| 图片转 PDF、PDF 页面转图片 | PDF/tool feature paths | 文件转换任务和输出验证 | Pending |
| GIF 帧提取、SVG、二维码/条形码 | format/archive/QR paths | 导入、编辑和导出 | Pending |
| OCR/文字提取和模型能力 | `lib:neural-tools`、OCR integrations | 本地模型、隐私提示和 unavailable 状态 | Pending |
| OpenCV、GPU、native codecs、AI models | `lib:opencv-tools`、ImageToolboxLibs | ABI、内存、版本、校验和与回滚 | Pending |

## 批准门禁

1. 每一行都必须绑定固定提交、实际源码/组件路径、依赖树、许可证和测试证据。
2. `Pending` 不能在产品文案或 UI 中宣称已集成；`Unavailable` 必须有真实设备/系统原因；`Blocked` 必须有 ADR 和回滚路径。
3. 只有四个域的矩阵均完成，且 success、unavailable、cancel、crash、version mismatch 契约测试通过，台账行才允许进入 `Approved`。
4. 上游更新后重新计算受影响行；不能用新版本行为替代锁定提交的历史证据。
