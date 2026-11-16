# Inventario de apps

Obtenido el 2026-11-07 desde Bare (`LauncherApps`, 121 actividades launcher). Los nombres aparecen en inglés porque el teléfono está en inglés. **Estado:** el usuario decidió los casos dudosos el 2026-11-07 (con motivo, YouShorts, apps de IA y Tailscale). Falta su aprobación final del documento completo.

Modos (spec 4.1): `libre` · `con-motivo` · `micro-app` · `oculta` · `bloqueada`. Lo que no esté listado aquí queda `oculta`.

## Definidos por la spec (4.2)

| App | Paquete | Modo |
|---|---|---|
| Phone | `com.google.android.dialer` | libre |
| Messages | `com.google.android.apps.messaging` | libre |
| Gmail | `com.google.android.gm` | libre |
| Maps | `com.google.android.apps.maps` | libre |
| Mi Banco (Banco de Chile) | `cl.bancochile.mi_banco` | libre |
| Pluxee Chile | `com.sodexo.consumers.chile` | libre |
| Canvas | `com.instructure.candroid` | libre |
| Drive | `com.google.android.apps.docs` | libre |
| Alarmy | `droom.sleepIfUCan` | libre |
| Clock | `com.android.deskclock` | libre |
| Claude | `com.anthropic.claude` | libre |
| Alba | `org.chromium.webapk.a0eec64c4d4fe59bb_v2` | libre (ver nota 1) |
| LinkedIn | `com.linkedin.android` | libre |
| Camera | `com.android.camera` | libre |
| Calculator | `com.miui.calculator` | libre |
| Settings | `com.android.settings` | libre |
| Google | `com.google.android.googlequicksearchbox` | libre (desactivar Discover) |
| Chrome | `com.android.chrome` | libre (filtro M5) |
| WhatsApp | `com.whatsapp` | con-motivo |
| Telegram | `org.telegram.messenger` | con-motivo |
| YouTube | `com.google.android.youtube` | bloqueada (app del sistema: desactivar con `pm disable-user`) |
| — | `microapp:instagram`, `microapp:youtube` | micro-app |

Instagram oficial: **no instalado**. Spindle: aún no existe.

## Propuesta: herramientas en modo `libre`

| App | Paquete |
|---|---|
| Mi Pass (token del banco) | `cl.bancochile.mi_pass2` |
| Calendar | `com.google.android.calendar` |
| Contacts | `com.google.android.contacts` |
| Wallet | `com.google.android.apps.walletnfcrel` |
| Authenticator (Google) | `com.google.android.apps.authenticator2` |
| Authenticator (Oracle) | `oracle.idm.mobile.authenticator` |
| Authenticator (Blizzard) | `com.blizzard.bma` |
| Carga Activa (Metro) | `es.indra.mobile_ticketing.metro_santiago_android_app` |
| Uber | `com.ubercab` |
| DiDi | `com.didiglobal.passenger` |
| Mercado Pago | `com.mercadopago.wallet` |
| Tenpo | `com.krealo.tenpo` |
| ConectaUDP | `cl.udp.conectaudp` |
| Outlook | `com.microsoft.office.outlook` |
| Zoom | `us.zoom.videomeetings` |
| Keep Notes | `com.google.android.keep` |
| Files | `com.google.android.apps.nbu.files` |
| Photos | `com.google.android.apps.photos` |
| Gallery | `com.miui.gallery` |
| CamScanner | `com.intsig.camscanner` |
| QR & Barcode Scanner | `com.gamma.scan` |
| Recorder | `com.android.soundrecorder` |
| Weather | `com.miui.weather2` |
| WPS Office | `cn.wps.moffice_eng` |
| Tailscale | `com.tailscale.ipn` (ver nota 3) |
| ntfy | `io.heckel.ntfy` |
| Play Store | `com.android.vending` |
| Gemini | `com.google.android.apps.bard` |
| Perplexity | `ai.perplexity.app.android` |
| Copilot | `com.microsoft.office.officehubrow` |
| Notebook (NotebookLM) | `com.google.android.apps.labs.language.tailwind` |
| Assistant | `com.google.android.apps.googleassistant` |

## Decidido por el usuario: `con-motivo`

| App | Paquete |
|---|---|
| Discord | `com.discord` |
| Spotify | `com.spotify.music` |
| YT Music | `com.google.android.apps.youtube.music` |
| Netflix | `com.netflix.mediaclient` |
| Pinterest | `com.pinterest` |

Spotify, Netflix y Pinterest casi nunca generan avisos de mensajes, así que en la práctica siempre pedirán motivo.

## Decidido por el usuario: `bloqueada`

| App | Paquete |
|---|---|
| Mi Video + YouShorts | `com.miui.videoplayer` (se bloquea el paquete completo) |

## Propuesta inicial `oculta`

- **Posibles distracciones:** FANDOM, IMDb, Letterboxd, News, Pou, Olymptrade, Lemuroid, Play Games, Game Center, Eagle Ride, Club Dominó, WikiDex, Perfect Viewer.
- **Música y podcasts:** AntennaPod, Podcasts, Music (MIUI), FM Radio.
- **Estudio, salud y otros:** Duolingo, EnglishScore, Photomath, Habits, Yazio, Smart Fit, Slack, Indeed, Booking.com, Entel, movistar (SIM), Cenco Malls, Papa Johns, Whoosh.
- **Proyectos propios:** Transmisor (`cl.alonso.bunker`), paws_app (`com.example.paws_app`), Army Forge, BattleScribe, Club (WebAPK).
- **Utilidades poco usadas:** OneDrive, FolderSync, RAR, ShareMe, Mi Mover, Mi Remote, Fing, Scanner (Xiaomi), File Manager (Xiaomi), Downloads, Security, Safety, Google One, Sticker Maker, Tabs, JBL Headphones, Sound Connect, Chrome Beta (nota 4), Max VPN (nota 3), Olauncher.
- **Sistema Xiaomi (candidatas a `bloqueada`):** App vault, Themes, Mi Picks, Services & feedback.

## Notas

1. **WebAPK (Alba, Army Forge, Club):** Chrome genera estos paquetes con un hash (`org.chromium.webapk.…`) que cambia si se reinstala la PWA. Las reglas por paquete son frágiles; en la Fase 1 conviene identificarlas también por etiqueta.
2. **YouShorts** es un feed de videos cortos de Xiaomi y comparte paquete (`com.miui.videoplayer`) con Mi Video. Los modos son por paquete, así que se bloquean los dos (decisión del usuario).
3. **VPN:** Tailscale es hoy la VPN siempre activa. Android admite una sola VPN a la vez y el filtro DNS de M5 la necesita. Resuelto en ADR-004: Tailscale se enciende a mano cuando haga falta y Bare queda como VPN siempre activa. Max VPN (Opera) también la reclamaría: mantenerla `oculta` y sin abrir.
4. **Chrome Beta** es un segundo navegador: también hay que desactivarle el "DNS seguro" (spec 9.3), o bloquearlo.
