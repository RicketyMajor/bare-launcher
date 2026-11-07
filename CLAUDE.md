# CLAUDE.md — Bare

Minimalist text-only Android launcher (HOME replacement). Source of truth: `docs/SPEC.md` (Spanish). Read it before coding; work phase by phase (SPEC §10). A phase closes only when its acceptance criteria pass **on the real phone**.

Sibling project: **Spindle** (`../spindle-app`, local music player). Independent apps; they share visual language and talk only through standard Android APIs (`MediaSession`, explicit `ACTION_SEND`).

## Device

Redmi 10 5G (`22041219NY`, codename `thunder`), Android 14 / SDK 34, HyperOS OS1.0. Details: `docs/DISPOSITIVO.md`.

## Commands (Windows, PowerShell or Git Bash)

```
# JDK: use Android Studio's bundled JBR (JDK 21), not the system Java 23
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"   # adb is not on PATH

./gradlew assembleDebug        # build
./gradlew installDebug         # install on the USB-connected phone
./gradlew testDebugUnitTest    # unit tests
& $adb devices -l              # check the phone is connected
```

## Conventions

- Kotlin + Jetpack Compose. Material 3 only as technical base; every visible component is custom (`ui/tui`).
- `minSdk 30`; `targetSdk`/`compileSdk` = latest stable. Gradle Kotlin DSL + version catalog. Single `app` module.
- Coroutines + Flow. Manual DI through one `AppContainer` — no Hilt. Room for persistence, `kotlinx.serialization` for `reglas.json`.
- applicationId / namespace: `dev.ricketymajor.bare`.
- UI text in Spanish (Chile), lowercase terminal style. Code, identifiers, comments and commits in English.
- Visual tokens: bg `#000000`, text `#FFFFFF`, secondary `#8A8A8A`, single accent `#FFB000` (pending alerts only). Font: JetBrains Mono, everywhere. No icons, no decorative animation.
- Event-driven only: no polling loops.
- Rules that depend on third-party apps (channels, routes, domains, injected CSS) live in config, never in code.
- Never write real notification/note/message content to the repo or persistent logs.
- Commits: Conventional Commits (`feat(search): …`), small, one per milestone.

## Package layout (`dev.ricketymajor.bare`)

`home` · `search` · `notes` · `avisos` · `friction` · `metrics` · `microapp` (engine) · `microapp/instagram` · `microapp/youtube` · `links` · `filter` · `config` · `ui/tui`

## Reusing open-source code

- Reference repos are cloned (depth 1) into `refs/` (git-ignored). Map module → reference files in `docs/referencias.md`.
- Copy only GPLv3/GPLv3+, MIT or Apache-2.0 code. GPL-2.0-only, unlicensed or unverified projects: ideas only.
- Keep original copyright headers; log every reused file in `THIRD_PARTY_NOTICES.md` (project, license, commit, original path).

## Docs

- `docs/SPEC.md` — specification.
- `docs/DECISIONES.md` — technical decisions (short ADRs).
- `docs/DISPOSITIVO.md`, `docs/inventario-apps.md`, `docs/canales-notificacion.md`, `docs/referencias.md` — Phase 0 outputs.

## Emergency exit

To go back to the Xiaomi launcher: Ajustes › Apps › Apps predeterminadas › Pantalla de inicio. Or: `adb uninstall dev.ricketymajor.bare`.
