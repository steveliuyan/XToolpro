# Open Design Source Inventory

## Source

- Original archive: `Createthisprojecta.zip` (local-only; excluded from the public repository)
- Extracted reference directory: `design/open-design/`
- Extracted file count: 61 at import time; generated previews and personal materials are local-only.
- Purpose: visual and interaction evidence only; it is not Android production source.

## Required Source Files

| File | Use |
| --- | --- |
| `DESIGN.md` | Visual foundation, layout, components, motion, and anti-patterns |
| `colors_and_type.css` | Canonical tokens to map into Compose semantic roles |
| `ui_kits/app/index.html` | Composed reference shell |
| `ui_kits/app/components.html` | Component structures and states |
| `ui_kits/app/components/kit.css` | Applied component styling reference |
| `ui_kits/app/components/behaviors.js` | Interaction/motion/persistence reference |
| `assets/xtoolpro-icon.png` | App icon evidence |
| `DESIGN-MANIFEST.json` | Surface and viewport inventory |

## Handling Rules

- Keep the ZIP archive unchanged in the local workspace; publish only the required, non-personal design references.
- Keep generated drawings, screenshots, duplicate icon exports, and maintainer photographs local-only.
- Preserve source provenance when creating tokens, assets, or screen implementations.
- Do not execute imported HTML/JavaScript in the Android app and do not ship design-process annotations or demo values.
- Any visual departure requires a design ADR or an explicit product request.
