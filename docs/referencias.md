# Referencias open source

Repos clonados con `git clone --depth 1` en `refs/<owner>__<repo>` (git-ignored) el 2026-11-07. Licencias verificadas en el propio repo.

## Inventario

| Repo | Commit | Licencia verificada | Uso permitido |
|---|---|---|---|
| Neamar/KISS | `14a56e2` | GPLv3 | copiar |
| tanujnotes/Olauncher | `2606973` | GPLv3 | copiar |
| DroidWorksStudio/mLauncher | `5a2847d` | GPLv3 | copiar |
| jrpie/launcher (μLauncher) | `732a00f` | MIT | copiar |
| 256x/launcher (Literal Launcher) | `9febd6c` | MIT (*la spec decía "verificar"*) | copiar |
| fandreuz/TUI-ConsoleLauncher | `058f541` | MIT (*la spec decía Apache-2.0*) | copiar |
| xscriptor/x.tui.launcher | `902e7fe` | MIT | copiar |
| MM2-0/Kvaesitso | `c4805f3` | GPLv3 | patrones, no copiar en bloque |
| curbox-app/curbox-android | `8a5852f` | GPLv3 | copiar |
| julian-klode/dns66 | `3e80e35` | GPLv3 | copiar |
| celzero/rethink-app | `07fe537` | Apache-2.0 | copiar |
| jean-voila/feurstagram | `bad4cd0` | GPLv3 | copiar (plan B y rutas de Instagram) |
| gkpo/healthy-youtube | `58d785c` | MIT (URL confirmada) | copiar |
| TeamNewPipe/NewPipe | `8917ff1` | GPLv3 | copiar |
| caasion/FeedLock | `06cef6c` | README dice MIT, **sin archivo LICENSE** | solo ideas |
| insin/control-panel-for-youtube | `9c2ed52` | sin licencia | solo ideas |
| akaMrNagar/Mindful | `b6eb68d` | GPL-2.0 (sin "o posterior") | solo ideas |
| IngoZenz/personaldnsfilter | `94182b2` | GPL-2.0 | solo ideas |
| nethical6/digipaws | — | **repo vaciado**: discontinuado a favor de Curbox | no clonado; usar Curbox |
| ReVanced/revanced-patches | — | **no disponible**: GitHub responde 403 por DMCA (2026-03-12) | no clonado; plan B de YouTube sin referencia |

## Mapa módulo → archivos

Rutas relativas a `refs/`. "Estudiar" = leer antes de diseñar; "base" = punto de partida para adaptar y copiar.

### M0 Núcleo y buscador
- **Base del buscador (KISS):** `Neamar__KISS/app/src/main/java/fr/neamar/kiss/normalizer/StringNormalizer.java` (normalización sin tildes), `…/kiss/utils/fuzzy/FuzzyScoreV2.java` (+ su test) y `…/dataprovider/AppProvider.java` (coincidencia y puntuación), `…/pojo/RelevanceComparator.java`, `…/db/HistoryMode.java`, `…/db/ValuedHistoryRecord.java` (orden por uso).
- **Estudiar HOME y lista de apps:** `tanujnotes__Olauncher/app/src/main/java/app/olauncher/MainActivity.kt`, `…/helper/Utils.kt` (LauncherApps, apps ocultas).
- **Estudiar UI Compose:** `256x__launcher/app/src/main/java/fumi/day/literallauncher/ui/LiteralLauncherApp.kt`, `…/ui/LauncherViewModel.kt` (fecha y batería).
- **Estudiar fuente:** `jrpie__launcher/app/src/main/java/de/jrpie/android/launcher/preferences/theme/Font.kt`.
- **Estudiar comandos y alias:** `fandreuz__TUI-ConsoleLauncher/app/src/main/java/ohi/andre/consolelauncher/commands/`.

### M1 Captura
Desarrollo propio.

### M2 Avisos
Desarrollo propio. Ideas de agrupación de Mindful (sin copiar código).

### M3 Fricción y métricas
- **Tiempo de uso:** `DroidWorksStudio__mLauncher/app/src/main/java/com/github/codeworkscreativehub/mlauncher/helper/analytics/AppUsageMonitor.kt`.
- **Estudiar:** `curbox-app__curbox-android/app/src/main/java/neth/iecal/curbox/data/db/AppUsage*.kt`, `…/blockers/AppBlocker.kt`. La lógica de demora para relajar reglas se ubica en la Fase 3.

### M4 Micro-apps
- **YouTube, CSS/JS (base):** `gkpo__healthy-youtube/blockers.css`, `gkpo__healthy-youtube/content.js`.
- **Suscripciones y novedades (estudiar):** `TeamNewPipe__NewPipe/app/src/main/java/org/schabi/newpipe/database/feed/` y `…/local/subscription/`. El lector de Takeout está en NewPipeExtractor, que no está clonado; se clona en la Fase 4 si hace falta.
- **Rutas de Instagram (estudiar):** `jean-voila__feurstagram/patches/src/`.
- **Qué ocultar en YouTube móvil:** control-panel-for-youtube, solo como lista de ideas.

### M5 Filtro DNS
- **Base:** `julian-klode__dns66/app/src/main/java/org/jak_linux/dns66/vpn/AdVpnThread.java` (interfaz VPN solo DNS), `…/vpn/DnsPacketProxy.java` (parseo y reenvío), `…/vpn/AdVpnService.java`, `…/vpn/VpnWatchdog.java`, `…/db/RuleDatabase.java`.
- **Estudiar detalles modernos:** `celzero__rethink-app/app/src/main/java/com/celzero/bravedns/`.

### M6 Guardia (futuro)
- **Estudiar:** `curbox-app__curbox-android/app/src/main/java/neth/iecal/curbox/blockers/ReelBlocker.kt`, `…/KeywordBlocker.kt`.
- **Ideas:** FeedLock, solo eso.

## Pendiente
- Cada módulo se profundiza al comenzar su fase (spec §14.3, paso 1): leer el código y resumir aquí cómo lo resuelve.
