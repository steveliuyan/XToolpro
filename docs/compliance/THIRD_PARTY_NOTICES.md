# 第三方声明底稿

**状态：** Phase 02 草案，不得作为 release 产物发布

**范围：** 四个候选上游的固定源码基线。此文件不等同于完整 SBOM，也不表示任何组件已获准集成。固定源码的许可事实见[许可审计](upstream-license-source-audit.md)。

## 已锁定的候选上游

| 域 | 组件 | 固定来源 | 许可证 | 发布前必须补齐 |
| --- | --- | --- | --- | --- |
| Proxy | FlClash full-capability engine 候选 | `chen08209/FlClash@62addf738a76b1a492e19af2dbabdb6d572b9e72` | GPL-3.0 root；Android proof 排除了候选 plugin，但其许可仍未声明 | 对应完整源代码、构建说明、补丁序列、Clash.Meta submodule、native core 和传递依赖清单；Android engine 必须保留对 `plugins/proxy`、`rust_api`、`window_ext` 的排除检查，任何桌面/full-capability 路径仍须先解决 `TODO` 许可占位。 |
| Cleaner | sdmaid-se full-capability maintenance 候选 | `d4rken-org/sdmaid-se@b9b01ee0af648fa6af25d388bb39bacde8d5b7a9` | GPL-3.0 | 对应全部 `app-tool-*` 与 `app-common-*` 源代码、构建说明、传递依赖清单与许可证。 |
| Media | ytdlnis full-capability workflow 候选 | `deniscerri/ytdlnis@13320bb64f35c8d04f01bebfa782d7947758fb66` | GPL-3.0 | 对应完整源代码、构建说明、Cookie/session 路径、yt-dlp、Python/JS runtime、FFmpeg、Aria2c 和每个二进制的版本、校验和与许可证。 |
| Image | ImageToolbox full-capability processing 候选 | `T8RIN/ImageToolbox@cb73d7a2e3094fb49e4d32cb07ad2903b62f8ac0` | Apache-2.0；固定源码根目录无 NOTICE | Apache-2.0 许可与适用版权归属、完整非 UI 处理闭包的依赖树、native codec、AI model 与图片解码依赖清单；实际解析出的 NOTICE 逐项纳入。 |

## 不可带入的材料

- 不复用 FlClash、sdmaid-se、ytdlnis 或 ImageToolbox 的产品名称、图标、UI、演示数据、营销素材或翻译。
- sdmaid-se 的图标、logo、市场材料、文档和翻译不在其 GPL 代码许可范围内。
- ytdlnis 名称不能用于 XToolpro 的衍生下载器；用户主动提供且有权使用的 Cookie/session、私有内容和高级格式在支持范围内；DRM 或绕过平台访问控制的流程不在支持范围。
- 不将用户 URL、Cookie、代理凭据、文件内容、文件名或未脱敏设备诊断写入任何 NOTICE、SBOM 或构建日志。

## 进入发布声明的门禁

1. 对应 `engine-*` 真实 capability proof 完成，并记录固定 commit、构建命令、依赖树、native 库与 SHA-256。
2. 每个许可证、NOTICE、版权和二进制来源经人工审查；未知或不可验证项目阻止进入 release。
3. GPL 路线通过对应源代码提供、构建说明和修改说明审查；最终许可证结论由维护人确认。
4. 生成 debug 与 release CycloneDX JSON SBOM，并与该文件和 `docs/architecture/upstream-reuse-ledger.md` 对照。
5. 许可证页面仅展示已批准且实际随构建分发的组件；调查中的候选组件不得在应用中宣称已集成。

## 关联记录

- `docs/compliance/SBOM-AND-NOTICE-PLAN.md`
- `docs/architecture/upstream-reuse-ledger.md`
- `docs/architecture/upstream-source-sync-policy.md`
- `docs/specs/02-upstream-reuse-and-compliance.md`
