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
| YAML、剪贴板、URL/订阅导入与导出 | `lib/views/profiles/add.dart`、`lib/widgets/input.dart`、`lib/providers/actions/profiles.dart`、`android/`、`core/` | 配置导入、校验、备份恢复、错误回退 | Partial：真机识别文件配置；确认二维码、文件、URL 添加入口；配置导出经 SAF 提交，系统文件选择器中配置卡片标签与导出文件名精确匹配 1 项，未打开或读取文件；本地备份经 SAF 保存后应用报告成功，系统 ZIP 选择器可见对应文件；独立配置剪贴板导入在固定 Android 包中为 Unavailable，源码和真机添加页均只有二维码、文件、URL 三项，URL 文本框长按未显示“粘贴”动作且未改写剪贴板，标准文本粘贴仍未验证；未验证备份恢复 |
| 订阅更新、重命名、复制、删除和持久化 | `lib/views/profiles/profiles.dart`、`lib/views/profiles/edit.dart`、`lib/providers/actions/profiles.dart`、配置服务 | 配置仓库和版本化变更 | Partial：真机确认编辑、预览、覆写和删除入口；后续当前配置出现仅限 URL 类型的“同步”和“复制链接”，编辑页存在 3 个非空输入框、自动更新项和保存动作，未读取字段值或提交变更；固定 Android UI/源码没有复制或克隆配置动作，“复制链接”不能替代配置复制；未执行订阅更新、重命名、删除或持久化行为验证 |
| 规则/全局/直连模式 | `core/Clash.Meta`、配置路径 | 模式选择与状态持久化 | Verified：真机确认三种模式；全局和直连均经强停重启保持；直连模式下公开 HTTPS 返回 `200` 且 TUN 收发计数增长，结束时恢复规则模式并再次强停重启确认 |
| 代理组展开、节点切换和当前节点 | `core/`、Android bridge | 代理组/节点页面与运行状态 | Partial：规则模式下真机显示多个代理组、节点卡，以及“自动选择”“故障转移”组入口；切换节点产生持久状态变更并可重启，当前节点未能通过无敏感语义独立复核；直连模式按上游逻辑隐藏代理入口，恢复规则模式后入口重新出现 |
| 节点协议、地区、标签筛选 | `core/`、Clash.Meta provider | 筛选、排序和可用性标记 | Partial：固定 Clash.Meta provider 源码存在 `filter`/`exclude-filter` 正则字段；真机代理页未出现独立协议、地区或标签筛选控件，当前仅能确认上游内部字段，未验证用户可见筛选行为 |
| 单节点与批量测速 | `core/`、服务层 | 测速任务、结果和失败原因 | Verified：单节点返回 `103 ms`；当前组批量测速显示 10 个结果节点和 9 个不同延迟值 |
| Android VPN 启停与竞争 VPN 处理 | `android/service`、`android/core` | `VpnService` 生命周期和权限流程 | Partial：真机多次完成启动、停止和恢复；未验证竞争 VPN、首次授权拒绝或撤销 |
| 始终开启、断线阻止 | Android VPN bridge、系统能力 | 能力检测、系统设置入口和明确 unavailable 状态 | Partial：ADB 可打开 Android `Settings$VpnSettingsActivity`；secure settings 显示始终开启 VPN 未配置、lockdown 未启用；未验证 FlClash 设置入口的写入行为或断线阻止实际效果 |
| TUN、系统代理、局域网共享 | `android/service/src/main/java/com/follow/clash/service/VpnService.kt`、`core/`、Android bridge | 运行模式和局域网开关 | Partial：`tun0` 与真实 HTTPS 流量已验证；真机“系统代理”为开启，运行态 Android VPN `LinkProperties` 报告 loopback `7890` HTTP proxy，停止后对应代理消失；“局域网代理”为关闭，运行态 mixed port 的 4 条 TCP 监听均仅绑定 loopback且无 wildcard，验证了关闭边界；未开启或验证局域网共享访问 |
| IPv6、DNS、Fake-IP/Host、流量嗅探 | `core/Clash.Meta` | DNS/IPv6/嗅探配置和诊断 | Partial：真机确认 IPv6、系统 DNS、DNS 劫持和 DNS 进阶入口；未改变配置或验证 Fake-IP/Host/嗅探行为 |
| 按应用代理/绕过 | `core/`、Android package bridge | 应用选择与路由策略 | Partial：真机进入访问控制应用列表，并确认“允许应用绕过 VPN”入口；未改变应用选择或验证路由结果 |
| 域名/IP/GeoIP 规则集和命中日志 | `core/` | 规则管理、命中详情和脱敏日志 | Partial：真机确认附加规则入口和规则模式；未读取规则内容、命中详情或日志 |
| 实时上下行速率、会话流量、连接列表 | `core/`、service bridge | 实时状态、历史摘要和任务事件 | Verified：运行态显示实时/累计流量；公开 HTTPS 流量后连接页显示 10 个可见记录 |
| 请求、规则、内核日志与崩溃诊断 | `core/`、service bridge | 脱敏诊断导出 | Partial：直连公开 HTTPS 流量后请求页显示 10 个可见记录，其中 6 个 accessibility 节点带 `DIRECT` 路由语义；临时启用 `info` 和日志捕获后日志页显示 6 条可见/部分可见 `info` 记录，随后恢复原设置；规则模式重测返回 HTTP `000`，未形成可归因的新记录；未读取正文，仍未验证规则命中日志或崩溃诊断 |
| 内核版本、更新和回滚 | `lib/views/about.dart`、`lib/common/request.dart`、`android/core/src/main/cpp/CMakeLists.txt` | 组件管理、校验和、回滚 | Unavailable（固定 Android 包）：真机关于页仅显示应用版本；“检查更新”检查 FlClash 应用 Release，“内核”只打开上游源码链接；未提供运行时内核版本、内核更新或内核回滚控件，`libclash.so` 在构建期链接，更新需随受审计的 engine/APK 发布 |
| 启动连接、自动更新、快捷方式/小组件 | `android/`、平台集成 | 后台入口和设备能力开关 | Partial：真机临时开启“自动运行”后，应用强停再打开会自动建立 `VpnService`、`tun0` 和系统 VPN；恢复关闭后再次强停再打开保持停止，确认开关行为和持久化；自动检查更新开关可见，手动更新检查完成 Release 元数据查询并返回当前应用已是最新版，未进入下载/安装分支；固定 Android 包未声明静态快捷方式或 AppWidget，相关能力为 Unavailable |

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
