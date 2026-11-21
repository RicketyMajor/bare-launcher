# Canales de notificación observados

Datos de `:avisos-debug` y de `dumpsys notification` (sin texto) en el Redmi 10 5G, 2026-11-07. **Solo metadatos.** Los identificadores con datos personales están anonimizados: `<cuenta>`, `#`.

## WhatsApp (`com.whatsapp`)

| Caso | Canal | Importancia | Plantilla | `isGroupConversation` | Flags |
|---|---|---|---|---|---|
| Chat individual | `individual_chat_defaults_4` | 4 | MessagingStyle | false | — |
| Chat de grupo | `group_chat_defaults_5` | **4 (suena)** | MessagingStyle | **true** | `0x8` (ONLY_ALERT_ONCE) |
| Resumen ("N mensajes de M chats") | `silent_notifications_6` | 2 | InboxStyle | — | `0x200` (**GROUP_SUMMARY**) |

- El sufijo numérico de los canales (`_4`, `_5`, `_6`) cambia cuando WhatsApp recrea el canal; por ejemplo, al tocar sus ajustes de sonido. **Las reglas no deben depender del `channelId` exacto**: usar `es_grupo` (`EXTRA_IS_GROUP_CONVERSATION`), como pide la spec (§11).
- WhatsApp **sí** marca `isGroupConversation=true` en los grupos, así que la regla `es_grupo` de la spec funciona.
- El canal de grupos tiene hoy importancia 4 (con sonido). Falta el paso manual de 9.3: dejarlo sin sonido ni vibración en Ajustes de Android.
- El resumen `silent_notifications_*` también trae categoría `msg`: sin la regla de `GROUP_SUMMARY`, se contaría como un mensaje más.

## Gmail (`com.google.android.gm`)

| Caso | Canal | Importancia | Plantilla | Flags |
|---|---|---|---|---|
| Email individual | `^nc_1_mail_<cuenta>` | 3 | BigTextStyle | `0x11` |
| Resumen de varios emails | `^nc_1_mail_<cuenta>` | 3 | InboxStyle | `0x210` (GROUP_SUMMARY) |

- Hay un canal por cuenta, y el ID incluye la dirección de email. Las reglas deben usar el paquete, no el canal.

## Canales declarados (sin muestra todavía)

Leídos de `dumpsys notification` (sección `AppSettings`, la definición de cada canal) el 2026-11-07, sin que llegara ninguna notificación. Sirven para saber qué esperar; **la categoría y `isGroupConversation` solo se confirman con una notificación real.**

| App | Canal | Importancia | Probable uso |
|---|---|---|---|
| Telegram (`org.telegram.messenger`) | `0channel_private_<hash>` | 4 | Chats individuales |
| | `0channel_groups_<hash>` | 4 | Grupos |
| | `0channel_groups_ia_<hash>` | 4 | Grupos (variante) |
| | `0channel_silent_<hash>` | 2 | Silenciosas |
| | `Other3` | 3 | Otras |
| Mensajes (`com.google.android.apps.messaging`) | `bugle_default_channel` | 4 | SMS/RCS |
| Sistema (`com.android.phone`) | `sms` | 4 | SMS del sistema (¿clase 0 / operador?) |
| Teléfono (`com.google.android.dialer`, marcador predeterminado) | `phone_missed_call` | 3 | Llamada perdida |
| Telecom de HyperOS (`com.android.server.telecom`) | `MiuiTelecomMissedCalls` | 4 | Llamada perdida (**segundo candidato**) |

- Los canales de Telegram llevan un hash, igual que los de WhatsApp llevan un número. Las reglas van por paquete + `es_grupo`.
- Telegram todavía no creó un canal para los **canales** de difusión. Los crea con la primera notificación de ese tipo, así que probablemente están silenciados.
- **Llamadas perdidas: hay dos posibles emisores.** Si publican los dos, el clasificador debe quedarse con uno y no contar la llamada dos veces. Hay que confirmarlo con una llamada real.

## Otras observadas

| App | Canal | Categoría | Notas |
|---|---|---|---|
| Discord (`com.discord`) | `voice_activity_low` | `social` | MessagingStyle, `isConversation` = sí, importancia 2 |
| Tailscale (`com.tailscale.ipn`) | `tailscale-status` | — | Estado persistente: `ignorar` |
| MIUI Cleaner (`com.miui.cleaner`) | `com.miui.cleaner` | — | Sistema: `ignorar` |

## Consecuencias para M2 (Fase 2)

1. **Ignorar los resúmenes de grupo** (`FLAG_GROUP_SUMMARY`): WhatsApp y Gmail publican un resumen además de cada mensaje. Contarlos inflaría los avisos. La spec no lo menciona: es una regla implícita del clasificador, no de `reglas.json`.
2. `es_grupo` es confiable en WhatsApp. Falta verificarlo en Telegram.
3. En la configuración, preferir paquete + categoría + `es_grupo` y dejar `channelId` solo como último recurso.

## Pendiente (antes de F2)

Muestras reales. Cuando llegue una de estas notificaciones, el usuario la deja en la bandeja y se lee con `dumpsys notification`:

- **Telegram**: chat individual, grupo y canal. Confirmar `isGroupConversation` en los grupos.
- **SMS**: confirmar canal y categoría (¿`msg`?).
- **Llamada perdida**: ¿`CATEGORY_MISSED_CALL`? ¿La publica el marcador, Telecom de HyperOS o los dos?
- **Canvas Student**, Banco de Chile y Pluxee: confirmar categoría cuando llegue alguna.
- **Instagram**: en la Fase 4, con la app oficial reinstalada.
