# Bare — Launcher utilitario minimalista para Android

> Especificación para construir con Claude Code · v0.2 · 7 nov 2026
> Proyecto hermano: **Spindle** (reproductor de música local, documento aparte). Son apps independientes: comparten lenguaje visual y se integran solo mediante APIs estándar de Android.

---

## 0. Instrucciones para Claude Code

- Este documento es la fuente de verdad. Léelo completo antes de escribir código y cópialo al repo como `docs/SPEC.md`.
- Crea `CLAUDE.md` en la raíz con: resumen del proyecto, comandos de build e instalación, convenciones y estructura de paquetes. Mantenlo al día.
- Trabaja por fases (sección 10), en orden. Una fase termina cuando todos sus criterios de aceptación se cumplen **en el teléfono real**, no solo en el emulador.
- Registra cada decisión técnica no trivial en `docs/DECISIONES.md` (formato ADR corto: contexto · decisión · alternativas · consecuencias).
- Si algo de esta spec resulta inviable en el dispositivo, no improvises en silencio: documenta lo que encontraste y propón una alternativa al usuario antes de seguir.
- Las decisiones abiertas (sección 12) tienen un valor por defecto. Úsalo, pero confírmalo con el usuario al llegar a esa parte.
- **No reinventar la rueda.** La sección 14 lista proyectos open source de los que hay que partir.
  - En la Fase 0, clónalos en `refs/` (que va en `.gitignore`).
  - Para cada módulo, revisa primero cómo lo resolvieron ellos y reutiliza su código cuando la licencia lo permita, adaptándolo y recortándolo.
  - Registra cada archivo o fragmento reutilizado en `THIRD_PARTY_NOTICES.md` (proyecto, licencia, commit, ruta original).
- Licencia de este proyecto: **GPL-3.0**, para poder reutilizar código de las bases, que casi todas son GPLv3 (ver 14.4).
- Idiomas: interfaz en español (Chile); código, identificadores, commits y comentarios en inglés.
- El usuario trabaja desde un PC con Windows. Desarrollo nativo en Windows (Android Studio / Gradle + ADB por USB). No se necesita WSL ni Docker.
- Commits pequeños por hito. Nunca escribas en el repo ni en logs persistentes contenido real de notificaciones, notas o mensajes del usuario.

---

## 1. Visión

El teléfono como una herramienta: se saca cuando hace falta (comunicarse, despertar, pagar, ubicarse, estudiar) y después se guarda. Bare reemplaza la pantalla de inicio del sistema y convierte el teléfono en una herramienta de comunicación y utilidades, sin superficies diseñadas para retener atención.

Señales de éxito (el propio launcher las muestra, ver M3):

- Menos desbloqueos por día.
- Menos aperturas de WhatsApp, Telegram o Instagram sin mensajes pendientes.
- Ningún mensaje, llamada o email importante queda sin ver por más de unos minutos (el usuario reconoce que hoy se le pasan por despiste).

---

## 2. Principios de diseño

1. **Jerarquía de intervención.** Ante cada fuente de distracción, usar el nivel más alto posible: eliminar (desinstalar) > reemplazar (micro-app) > informar en vez de abrir > fricción contextual > medir. Los niveles bajos son más frágiles y fáciles de saltar.
2. **Fricción solo cuando no hay motivo.** Abrir WhatsApp o Instagram con un mensaje nuevo es usar una herramienta; abrirlos sin nada pendiente es un hábito. El launcher distingue ambos casos.
3. **Nada importante se bloquea.** Las notificaciones nunca se pierden: se ordenan, agrupan y destacan.
4. **Texto, negro, monoespaciado.** Sin íconos, imágenes ni animaciones decorativas.
5. **Relajar cuesta, endurecer es inmediato.** Todo cambio de configuración que afloje reglas (desbloquear un sitio, pasar una app a "libre", desactivar el filtro) entra en vigor tras una espera (por defecto 10 min) y puede cancelarse en ese lapso. Los cambios que endurecen aplican al instante.
6. **Reglas como datos.** Todo lo que depende de apps de terceros (canales de notificación, rutas de Instagram y YouTube, dominios, CSS inyectado) vive en archivos de configuración, no en el código. Cuando WhatsApp, Instagram o YouTube cambien, se ajusta la configuración sin recompilar.
7. **Local y privado.** Sin cuentas propias, sin analítica, sin telemetría. La única red que usa la app es la de las micro-apps y el reenvío de DNS.
8. **No es una cárcel.** El objetivo es que el camino fácil sea el bueno, no impedirle al usuario controlar su teléfono. Nada de administrador de dispositivo ni trucos para impedir la desinstalación.

---

## 3. Alcance

### Incluido en v1

| Módulo | Qué hace |
|---|---|
| M0 Núcleo | Launcher HOME, pantalla de inicio, abrir apps escribiendo |
| M1 Captura | Notas rápidas desde la pantalla de inicio |
| M2 Avisos | Pantalla de notificaciones grande, resumen de grupos, re-aviso insistente |
| M3 Fricción y métricas | Pantalla de motivo para apps de hábito, métricas de uso en texto |
| M4 Micro-apps y enlaces | **Instagram:** solo mensajes (+ un perfil permitido), con notificaciones. **YouTube:** buscador y canales suscritos, sin inicio algorítmico, Shorts ni recomendados. Los enlaces de ambos sitios se abren en las micro-apps |
| M5 Filtro web | Bloqueo de dominios en todo el sistema mediante DNS local (VpnService) |

### Fuera de alcance en v1

- Servicio de accesibilidad "guardia" para vigilar apps nativas (solo si las métricas de M3 demuestran que hace falta; ver sección 11 y M6 en 14.1).
- Widgets, temas, fondos de pantalla, íconos.
- Reemplazar el marcador, la app de SMS o Gmail.
- Sincronización en la nube o cuentas.

---

## 4. Apps: modos y clasificación inicial

### 4.1 Modos

| Modo | ¿Aparece al escribir? | Al abrir | Uso típico |
|---|---|---|---|
| `libre` | Sí | Abre directo | Herramientas: Gmail, Maps, banco |
| `con-motivo` | Sí | Directo si hay avisos pendientes de esa app; si no, pantalla de motivo (M3) | Mensajería que se abre por costumbre |
| `micro-app` | Sí, como entrada propia | Abre la micro-app interna (M4); puede pedir motivo igual que `con-motivo` | Instagram, YouTube |
| `oculta` | Solo al escribir el nombre completo | Pide confirmación | Apps poco usadas |
| `bloqueada` | No | No se puede abrir desde el launcher | Apps nativas que solo están para recibir notificaciones (Instagram) o que no deben usarse |

### 4.2 Clasificación inicial propuesta

Basada en lo que el usuario declaró. Se confirma en la Fase 0 con la lista real del teléfono.

- **libre:** Teléfono, Mensajes (SMS), Gmail, Google Maps, Banco de Chile, Pluxee Chile, Canvas Student, Google Drive, Alarmy, Reloj/alarmas del sistema, Claude, Alba, LinkedIn, Spindle, Cámara, Calculadora, Ajustes, Google (con Discover desactivado, ver 9.3), Chrome (con filtro web activo, M5).
- **con-motivo:** WhatsApp, Telegram.
- **micro-app:**
  - `instagram` (con motivo si no hay avisos de Instagram).
  - `youtube` (sin pantalla de motivo; su propia pantalla de inicio ya es intencional: un buscador).
- **bloqueada:**
  - Instagram oficial. Se reinstala solo para recibir las notificaciones de mensajes (M4.2).
  - YouTube oficial, si viene preinstalado y no se puede desinstalar. Si se puede, desinstalarlo.
- **oculta:** todo lo demás, hasta que el usuario lo reclasifique.

**Fase 0:** Claude Code obtiene la lista real por ADB (`adb shell pm list packages -3` y las actividades launcher con sus etiquetas) y genera `docs/inventario-apps.md`: paquete, nombre visible, modo propuesto. El usuario lo revisa y aprueba antes de la Fase 1.

---

## 5. Interfaz

### 5.1 Lenguaje visual (compartido con Spindle)

- Fondo `#000000` (negro OLED). Texto principal `#FFFFFF`. Texto secundario `#8A8A8A`.
- Un único color de acento, ámbar `#FFB000`, reservado para avisos pendientes y estados de alerta. Nada más usa color.
- Tipografía monoespaciada incluida en la app (JetBrains Mono, IBM Plex Mono o Hack, licencias libres). Toda la interfaz es monoespaciada.
- Escala base: hora 64 sp · avisos 22 sp · listas 20 sp · secundario 14 sp. Respetar la escala de fuente del sistema.
- Sin íconos. Los indicadores son texto: `[0:12]`, `(3)`, `▶`, `›`, `·`.
- Áreas táctiles ≥ 48 dp. Sin animaciones salvo transiciones instantáneas. Vibración corta como confirmación de acciones.
- Todo en minúsculas salvo encabezados de sección (`AVISOS`), al estilo de una terminal.

### 5.2 Inicio, sin pendientes

```
  02:22
  sábado 7 de noviembre
  alarma 07:30 · batería 64%

  sin avisos

  ▶ radiohead — nude                2:31/4:15

  hoy: 14 desbloqueos · 52 min

  > _
```

### 5.3 Inicio con avisos pendientes

Cuando hay avisos, la pantalla de inicio **es** la pantalla de avisos: ocupan el centro, en grande y en ámbar. Es la respuesta al problema de "no me doy cuenta de que me llegan mensajes".

```
  02:22 · alarma 07:30 · 64%

  AVISOS
  gmail        2   canvas: nueva tarea en cálculo ii
                   banco de chile: comprobante de…
  whatsapp     1   mamá: ¿vienes el domingo?
  instagram    1   mensaje nuevo
  llamadas     1   perdida · +56 9 ···· 1234 · 01:58

  grupos           u ingeniería (9) · fútbol (5)

  > _
```

- Tocar una línea abre exactamente esa conversación o email (dispara el `contentIntent` de la notificación, o la micro-app si la regla lo indica) y la marca como vista.
- Mantener presionada una línea: `descartar` · `responder` (si la notificación lo permite, Fase 6) · `silenciar esta conversación por 1 h`.
- La línea `grupos` es un resumen: nunca destaca en ámbar ni genera re-avisos.

### 5.4 Escribir para abrir

```
  > ba
    [0:03]  banco de chile
    [0:00]  barómetro

  enter: abrir el primero · sin resultados: guardar como nota
```

- El campo de entrada está siempre en la parte inferior. Tocarlo abre el teclado. Por defecto el teclado **no** se abre solo al volver a inicio, para no tapar los avisos (D6).
- Coincidencia: normalizar (minúsculas, sin tildes); primero prefijo de cualquier palabra del nombre o de un alias, después subsecuencia y tolerancia a errores de tipeo. Empates: por frecuencia de uso local (últimos 30 días), luego alfabético. Partir del algoritmo de KISS (14.1).
- Máximo 6 resultados. Cada uno muestra `[tiempo de uso hoy]  nombre  (avisos pendientes)`.
- Con coincidencia única y al menos 2 caracteres, abre automáticamente (configurable).
- `Enter` abre el primer resultado. Si no hay resultados, guarda el texto como nota (M1) y muestra `nota guardada` por 2 s.
- Prefijos:
  - `n <texto>`: fuerza una nota.
  - `yt <texto>`: busca directo en la micro-app de YouTube.
  - `:` comandos: `:notas`, `:avisos`, `:semana`, `:ajustes`, `:bloquear <dominio>`, `:ayuda`.
- Las apps `oculta` aparecen solo con el nombre completo y piden confirmación. Las `bloqueada` nunca aparecen.
- Mantener presionado un resultado: menú en texto con `info de la app` · `cambiar modo` · `desinstalar`.
- Las apps instaladas o desinstaladas se reflejan sin reiniciar el launcher.

### 5.5 Navegación

- Botón Inicio del sistema: limpia la entrada y vuelve al estado de inicio.
- Atrás en inicio: no hace nada.
- En Xiaomi (MIUI/HyperOS), los gestos de pantalla completa no funcionan con launchers de terceros: el sistema obliga a usar los 3 botones. Se acepta como parte del diseño; no se buscan trucos para evitarlo en v1 (ver 9.3).

---

## 6. Módulos

### M0 — Núcleo

**Requisitos**

- `HomeActivity` declarada con `MAIN` + `HOME` + `DEFAULT`, `launchMode="singleTask"`, excluida de recientes.
- Lista de apps con `LauncherApps` (o `PackageManager` con `<queries>` para `MAIN`/`LAUNCHER`); escuchar instalaciones y desinstalaciones.
- Línea de información: hora, fecha en español, próxima alarma (`AlarmManager.getNextAlarmClock()`; mostrar solo si existe), batería.
- Línea de música: la sesión multimedia activa (`MediaSessionManager.getActiveSessions` usando el componente del listener de M2) como `▶ artista — título  posición/duración`. Tocarla abre la app que reproduce. Funciona con Spindle y con cualquier otro reproductor.
- Línea de métricas (M3).

**Criterios de aceptación**

- Se puede fijar como pantalla de inicio predeterminada y sobrevive a un reinicio.
- Escribir `gm` + abrir llega a Gmail en menos de 300 ms desde que se suelta la tecla.
- Una app recién instalada aparece al escribir su nombre sin reiniciar el launcher.
- La próxima alarma de Alarmy aparece si Alarmy la registra en el sistema (documentar si no lo hace).
- Arranque en frío de inicio por debajo de 500 ms en el dispositivo.

### M1 — Captura

**Requisitos**

- Las notas son líneas con marca de tiempo, guardadas en Room.
- `:notas` muestra la lista (más recientes arriba). Cada nota permite `copiar` · `compartir` · `marcar hecha` · `borrar`.
- Exportación automática (configurable): cada nota se añade a `Documents/Bare/notas.md` para que el usuario pueda sincronizarla con Syncthing o copiarla al PC.
- Integración con Spindle (Fase 6): una nota que empieza con `♪ ` se envía también a Spindle como pedido de música, mediante un `Intent` explícito `ACTION_SEND` al paquete de Spindle. Si Spindle no está instalado, queda solo como nota.

**Criterios de aceptación**

- Escribir un texto sin coincidencias + Enter lo guarda como nota en menos de 1 s.
- Las notas sobreviven a reinicios y aparecen en `notas.md`.

### M2 — Avisos

El módulo más importante para el usuario: el teléfono no debe ser una distracción, pero tampoco puede dejar pasar lo importante.

**Requisitos**

- `AvisosService` (`NotificationListenerService`) recibe todas las notificaciones y las clasifica según reglas de configuración (sección 7) usando: paquete, `channelId`, categoría (p. ej. `CATEGORY_MISSED_CALL`, `CATEGORY_MESSAGE`), si es conversación grupal (`EXTRA_IS_GROUP_CONVERSATION` / `MessagingStyle`) y si es una notificación en curso.
- Niveles de prioridad:
  - `alta`: se muestra en ámbar en inicio y genera re-avisos.
  - `normal`: se muestra en inicio sin re-avisos.
  - `resumen`: solo cuenta en la línea `grupos`.
  - `ignorar`: no se muestra (notificaciones en curso, descargas, música, sugerencias de Instagram).
- Una regla puede indicar `abrir_con`: al tocar el aviso, en vez de disparar el `contentIntent` de la app de origen, se abre otra cosa (p. ej. la micro-app de Instagram). Estos avisos se retiran siempre de la bandeja del sistema, para que no exista un camino directo a la app nativa.
- Reglas por defecto:
  - `alta`: llamadas perdidas, SMS, chats individuales de WhatsApp y Telegram, mensajes directos de Instagram (abren la micro-app), Gmail, Canvas Student.
  - `normal`: Banco de Chile, Pluxee, LinkedIn, Drive, Alba y todo lo no clasificado.
  - `resumen`: grupos de WhatsApp y de Telegram, canales de Telegram.
  - `ignorar`: notificaciones en curso y de sistema; toda notificación de Instagram que no sea un mensaje.
- **Resumen de grupos.** Ataca la causa principal del hábito: abrir WhatsApp "para ver si algún grupo silenciado escribió". En vez de abrir la app, el usuario ve en inicio `u ingeniería (9) · fútbol (5)`.
  - Requiere que los grupos **no** estén silenciados dentro de WhatsApp (si lo están, WhatsApp no publica notificaciones y el launcher no puede contarlos). El silencio se consigue en Android: el canal de notificaciones de grupos de WhatsApp se configura sin sonido ni vibración (paso de puesta en marcha, 9.3).
  - Opción `limpiar_bandeja_sistema`: el launcher retira de la bandeja del sistema las notificaciones `resumen` (`cancelNotification`), sin perder el conteo.
  - Los conteos bajan cuando WhatsApp retira la notificación (p. ej., si el usuario leyó desde otro dispositivo).
- **Re-aviso insistente.** Si un aviso `alta` sigue sin verse después de `reaviso.minutos` (por defecto 10), el launcher publica su propia notificación de alta importancia ("2 avisos sin ver: gmail, mamá") con un patrón de vibración distintivo. Se repite hasta `reaviso.maximo` veces (por defecto 3), con intervalo creciente, y respeta `reaviso.silencio` (por defecto 23:00–07:30), salvo llamadas perdidas. Precisión aceptable: ±2 min.
- Un aviso se considera **visto** cuando el usuario abre inicio con ese aviso en pantalla durante más de 2 s, lo toca, o la app de origen lo retira.
- **Al desbloquear.** Si hay avisos `alta` pendientes y `mostrar_al_desbloquear` está activo, al desbloquear el teléfono el launcher trae la pantalla de inicio al frente.
  - Escuchar `ACTION_USER_PRESENT` con un receptor registrado dinámicamente desde el servicio.
  - Abrir una actividad desde segundo plano requiere el permiso "Mostrar sobre otras apps" (`SYSTEM_ALERT_WINDOW`) y, en Xiaomi, "Mostrar ventanas emergentes mientras se ejecuta en segundo plano". Si no se puede, documentarlo y usar la notificación de re-aviso como respaldo.
- **Respuesta rápida** (Fase 6): para notificaciones con acción `RemoteInput` (WhatsApp, Telegram), responder desde inicio sin abrir la app. Limitación conocida: solo sirve para conversaciones con notificación activa; no permite iniciar chats ni enviar archivos.
- Persistencia: tabla `aviso` en Room, purgada a los 7 días.
- Pantalla de depuración `:avisos-debug` (solo en builds de depuración): muestra en vivo paquete, `channelId`, categoría, `isGroupConversation` y claves de `extras` de cada notificación, **sin** el texto. Sirve para escribir reglas en la Fase 0 y para reajustarlas cuando WhatsApp o Instagram cambien.

**Criterios de aceptación**

- Un mensaje individual de WhatsApp aparece en ámbar en inicio en menos de 2 s.
- Diez mensajes en un grupo de WhatsApp solo modifican la línea `grupos` y no suenan ni vibran.
- Un mensaje directo de Instagram aparece en inicio, no queda en la bandeja del sistema, y al tocarlo se abre la micro-app (no la app oficial).
- Un email sin ver por 10 min genera exactamente un re-aviso; tras verlo, no hay más.
- Tocar el aviso de un chat de WhatsApp abre ese chat, no la lista de chats.
- Al desbloquear con un aviso `alta` pendiente se ve la pantalla de inicio, o queda documentado por qué el dispositivo no lo permite.
- Matar el proceso desde recientes no detiene la recepción de avisos (verificar con la configuración de batería de MIUI, 9.3).

### M3 — Fricción contextual y métricas

**Pantalla de motivo** (apps `con-motivo`, y micro-apps configuradas así, sin avisos pendientes de esa app):

```
  whatsapp
  no tienes mensajes nuevos.

  ¿para qué lo abres?
  › escribirle a alguien
  › buscar algo en un chat
  › nada en particular

  [ abrir ]  disponible en 5 s
```

- `nada en particular` vuelve a inicio de inmediato.
- Las otras opciones habilitan `abrir` tras `motivo.espera_segundos` (por defecto 5).
- Si la app tiene avisos pendientes, se abre directo, sin pantalla de motivo.
- Cada apertura queda registrada localmente: app, hora, con o sin avisos, motivo elegido.

**Métricas** (con `UsageStatsManager`):

- Tiempo en primer plano hoy por app (incluido el tiempo dentro de cada micro-app), que se muestra como `[h:mm]` en los resultados de búsqueda.
- Desbloqueos de hoy (eventos `KEYGUARD_HIDDEN`), que se muestran en la línea de inicio.
- Aperturas con y sin motivo de las apps `con-motivo`.
- **Evasiones:** veces que una app `con-motivo` o `bloqueada` pasó a primer plano sin venir del launcher ni de un aviso (p. ej., la app oficial de Instagram abierta desde la tienda o desde recientes). Solo se muestra; es el dato que decidirá si hace falta el guardia de accesibilidad.
- `:semana` muestra una tabla en texto con los últimos 7 días: desbloqueos, tiempo total, top 5 apps, aperturas sin motivo y evasiones.
- Sin rachas, puntos, metas ni notificaciones sobre métricas. Los datos se muestran, no se juzgan.

**Criterios de aceptación**

- Abrir WhatsApp sin mensajes nuevos muestra la pantalla de motivo; abrirlo con un mensaje nuevo, no.
- Las métricas de inicio coinciden (±5 %) con las del Bienestar digital del sistema.

### M4 — Micro-apps y enlaces

#### M4.1 Motor común

- `MicroAppActivity` con un `WebView`, idealmente en un proceso separado (`android:process=":microapp"`). Las cookies persisten para no tener que iniciar sesión cada vez.
- Cada micro-app se define por configuración (sección 7) más una carpeta de recursos `assets/microapps/<id>/` con su CSS y JS inyectados. Añadir otra micro-app no debe requerir código nuevo, salvo una pantalla de inicio nativa opcional.
- **Control de navegación por rutas.** Todo lo que no esté permitido redirige al inicio de la micro-app.
  1. `shouldOverrideUrlLoading` para las navegaciones completas.
  2. Instagram y YouTube web son aplicaciones de una sola página: cambiar de sección no recarga la página ni llama a `onPageFinished`. Hay que inyectar un script al inicio de cada documento (`WebViewCompat.addDocumentStartJavaScript`, de `androidx.webkit`) que intercepte `history.pushState`, `history.replaceState`, `popstate` y los clics en enlaces. Si la nueva ruta no está permitida, `location.replace(inicio)`.
  3. CSS inyectado y un `MutationObserver` para ocultar elementos (navegación a secciones bloqueadas, recomendaciones, banners de "abrir en la app").
- **No** bloquear endpoints de red (GraphQL/REST): las secciones permitidas comparten la API con las bloqueadas y bloquearla es frágil. El control es por rutas, CSS y JS.
- Agente de usuario: probar el del WebView; si el sitio limita funciones, usar el de Chrome móvil sin el marcador `; wv`.
- Enlaces a dominios ajenos a la micro-app se abren en Chrome (sujetos al filtro de M5).
- Atrás: retrocede en el historial del WebView; en el inicio de la micro-app, la cierra.
- Modo de depuración que registra (sin contenido) las rutas bloqueadas, para detectar roturas cuando el sitio cambie.
- **Enlaces entrantes.** Bare declara filtros de intent `VIEW`/`BROWSABLE` (sin verificación automática) para `instagram.com`, `www.instagram.com`, `youtube.com`, `www.youtube.com`, `m.youtube.com` y `youtu.be`.
  - El usuario activa esos dominios una vez en Ajustes › Apps › Bare › Abrir de forma predeterminada › Agregar enlace. Bare ofrece un atajo a esa pantalla (`Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS`).
  - Un enlace que un amigo manda por WhatsApp se abre en la micro-app correspondiente.
  - Si la ruta no está permitida (p. ej., un Reel cuando D3 está desactivado), se muestra en texto: `este enlace lleva a una sección bloqueada` y un botón `volver`.

#### M4.2 Instagram: solo mensajes, con notificaciones

Objetivo del usuario: ver y responder mensajes (incluidos los grupos que solo tiene en Instagram), y ver el perfil e historias de una sola persona (su novia, D4). Las notificaciones son imprescindibles: indican cuándo hay algo que responder.

**Cómo se consiguen las notificaciones sin abrir la puerta al feed**

- Se reinstala la **app oficial de Instagram** con modo `bloqueada`: no aparece al escribir y su único trabajo es recibir notificaciones.
- En los ajustes de Instagram se desactivan todas las notificaciones salvo las de mensajes.
- M2 captura las notificaciones de mensajes directos como `alta`, las retira de la bandeja del sistema y, al tocarlas, abre la micro-app (`abrir_con: microapp:instagram`). Todas las demás notificaciones de Instagram se ignoran y retiran.
- En Ajustes › Apps › Instagram › Abrir de forma predeterminada se desactiva "Abrir enlaces compatibles", para que los enlaces vayan a Bare.
- La app oficial sigue existiendo y podría abrirse desde la tienda o desde Ajustes. M3 lo cuenta como evasión. Si pasa seguido, se considera el plan B o el guardia M6.

**Micro-app**

- URL de inicio: `https://www.instagram.com/direct/inbox/`.
- Rutas permitidas por defecto:
  - `/direct/**`;
  - rutas de inicio de sesión, verificación y doble factor (`/accounts/**`, `/challenge/**`, a confirmar en la fase de micro-apps);
  - publicaciones y reels individuales compartidos por mensaje (`/p/*`, `/reel/*`), según D3;
  - para cada usuario en `perfiles_permitidos`: `/<usuario>/` y `/stories/<usuario>/**` (D4).
- CSS: ocultar los enlaces a Inicio, Explorar y Reels, las sugerencias bajo publicaciones y reels individuales, y los banners de "abrir en la app".
- Adjuntar fotos en los mensajes (`onShowFileChooser`). La cámara es opcional.
- Al abrirla sin avisos de Instagram pendientes, pasa por la pantalla de motivo (M3).

**Plan B (D5): FeurStagram.** Es un parche open source (GPLv3) de la app oficial. Conserva mensajes, historias, búsqueda, perfiles y notificaciones, y bloquea el feed, Explorar y Reels a nivel de red. Tiene un "bloqueo permanente" que impide relajar las restricciones sin reinstalar.

- Ventajas: experiencia nativa y menos desarrollo.
- Riesgos:
  - Un cliente modificado puede violar las condiciones de Instagram.
  - Compilarlo requiere JDK 21, el SDK de Android y un token de GitHub.
  - Las historias se activan o desactivan todas juntas, no por persona.
- Si se elige, la micro-app de Instagram se elimina, FeurStagram pasa a `con-motivo` con los mensajes como página de inicio, y M2 abre FeurStagram directamente.

**Criterios de aceptación**

- Iniciar sesión una vez en la micro-app y seguir con la sesión iniciada después de reiniciar el teléfono.
- Un mensaje directo recibido genera un aviso en Bare en menos de 30 s (medir), y tocarlo abre la bandeja de entrada de la micro-app.
- Abrir un chat, enviar texto y una foto.
- Tocar cualquier enlace a Inicio, Explorar o Reels, o navegar a `instagram.com/explore`, termina en la bandeja de entrada en menos de 1 s, sin que se vea el contenido bloqueado.
- El perfil permitido muestra su perfil e historias; cualquier otro perfil redirige.
- Un enlace de Instagram recibido por WhatsApp se abre en la micro-app.

#### M4.3 YouTube: como antes, solo buscador y suscripciones

Objetivo del usuario: usar YouTube "como antes", al estilo de la extensión Unhook: buscar, entrar a los canales que sigue y ver sus videos. Sin feed de inicio, sin Shorts, sin videos relacionados y sin reproducción automática.

**Sin inicio de sesión.** Google bloquea el inicio de sesión de cuentas Google dentro de WebViews embebidos. Por eso la micro-app funciona sin cuenta y las **suscripciones son locales**:

- `suscripciones.json` con la lista de canales (ID y nombre).
- Importación inicial desde Google Takeout (`subscriptions.csv` de YouTube). NewPipe usa el mismo formato y su código sirve de referencia (14.1).
- Agregar o quitar canales desde la propia micro-app (`seguir` / `dejar de seguir` en la página de un canal).

**Pantalla de inicio nativa (TUI)**, no web:

```
  youtube
  > buscar_

  NOVEDADES
  hoy    veritasium        why is the sky blue…      18:42
  ayer   3blue1brown       but what is a neural…     25:10
  2 nov  kurzgesagt        the most dangerous…       11:03

  CANALES (12)                               [gestionar]
```

- **Novedades:**
  - Se arman con el feed RSS público de cada canal (`https://www.youtube.com/feeds/videos.xml?channel_id=<id>`).
  - Se refrescan al abrir, como máximo cada 30 min.
  - Orden estrictamente cronológico, sin ranking.
  - Se excluyen los Shorts. Claude Code debe verificar cómo detectarlos de forma confiable (ruta `/shorts/`, duración u otra señal) y documentarlo.
- **Buscar** abre `m.youtube.com/results?search_query=…` dentro del WebView, con los Shorts ocultos en los resultados.
- Tocar un video abre `/watch?v=…` en el WebView.
- **Rutas permitidas:** `/results`, `/watch`, `/@<canal>` y `/channel/<id>` (pestaña de videos), `/playlist`.
- **Redirecciones:**
  - `/` y `/feed/**` → inicio de la micro-app.
  - `/shorts/<id>` → bloqueado por defecto (D11; alternativa: abrirlo como video normal en `/watch?v=<id>`).
- **CSS/JS inyectado.** Partir de Healthy YouTube (MIT, funciona en `m.youtube.com`), que ya resuelve:
  - quitar Shorts;
  - quitar videos relacionados y pantallas finales;
  - desactivar la reproducción automática.

  Además:
  - ocultar los banners de "abrir la app";
  - comentarios ocultos por defecto (configurable).
- **Acción `♪ pedir a Spindle`** en la página de un video: envía la URL a Spindle como pedido de música (`ACTION_SEND`), sin descargar nada en el teléfono.
- Sin reproducción en segundo plano ni minirreproductor.

**Planes B (documentados, no implementar salvo que el usuario lo pida):**

- **NewPipe o Tubular** (GPLv3): cliente sin cuenta, con suscripciones locales. Permite excluir los Shorts del feed de suscripciones, pero no de la búsqueda.
- **ReVanced** sobre la app oficial de YouTube: permite iniciar sesión y tiene parches para:
  - abrir en Suscripciones en vez de Inicio;
  - ocultar componentes de Shorts y del feed.

  Es un cliente modificado (riesgo frente a las condiciones de YouTube) y requiere mantener el parche.

**Criterios de aceptación**

- Importar `subscriptions.csv` y ver las novedades de todos los canales, en orden cronológico, sin Shorts.
- Buscar algo y ver resultados sin la estantería de Shorts.
- Un video se reproduce sin videos relacionados a la vista y no salta a otro al terminar.
- Navegar a `youtube.com/`, `/feed/trending` o `/shorts/…` nunca muestra esas secciones.
- Un enlace de YouTube recibido por WhatsApp se abre en la micro-app.
- `♪ pedir a Spindle` deja el pedido en Spindle.

### M5 — Filtro web

El usuario quiere que los sitios de redes sociales (y otros que él agregue) no se puedan abrir desde el navegador. Si no, instagram.com en Chrome se salta todo el sistema.

**Requisitos**

- `FiltroDnsService` (`VpnService`) que solo captura tráfico DNS: crea una interfaz con una IP de DNS falsa como servidor de nombres y enruta solo esa IP. No pasa el resto del tráfico por la VPN, así que el impacto en batería y velocidad debe ser mínimo. Es el mismo diseño de DNS66 (14.1): partir de su código.
- Para cada consulta: si el dominio o un dominio padre está en la lista de bloqueo, responder `NXDOMAIN`; si no, reenviarla al servidor DNS de salida mediante un socket protegido (`VpnService.protect`). Soportar consultas A y AAAA.
- Coincidencia por sufijo: bloquear `instagram.com` bloquea también `www.instagram.com` e `i.instagram.com`.
- Excluir de la VPN el propio paquete de Bare (`addDisallowedApplication`) para que las micro-apps puedan llegar a Instagram y YouTube, y las apps listadas en `excluir_apps`.
- **App oficial de Instagram:** probar primero que sus notificaciones de mensajes llegan con el filtro activo. Solo si no llegan, agregarla a `excluir_apps` (está `bloqueada` en el launcher de todos modos).
- Lista por defecto: instagram.com, facebook.com, tiktok.com, x.com, twitter.com, reddit.com, threads.net, youtube.com, youtu.be. YouTube e Instagram solo funcionan dentro de sus micro-apps. El usuario agrega sitios con `:bloquear <dominio>` o en ajustes.
- Agregar un dominio aplica al instante; quitarlo espera el tiempo de "relajar" (principio 5).
- Servidor DNS de salida: el de la red actual; si no se puede obtener, 1.1.1.1.
- Debe sobrevivir a reinicios: el usuario activa "VPN siempre activa" para Bare en ajustes de Android (9.3).
- Limitaciones:
  - Android admite una sola VPN a la vez.
  - El DNS privado de Android y el "DNS seguro" de Chrome pueden saltarse el filtro, así que deben quedar desactivados (9.3). Claude Code debe verificarlo en el dispositivo.

**Criterios de aceptación**

- En Chrome, instagram.com y youtube.com no cargan; gmail.com y google.com sí.
- Las micro-apps de Instagram y YouTube siguen funcionando con el filtro activo.
- Las notificaciones de mensajes de Instagram siguen llegando con el filtro activo.
- Tras reiniciar, el filtro está activo sin intervención.
- El consumo de batería atribuido a Bare en 24 h de uso normal es bajo (documentar la cifra).

---

## 7. Configuración: `reglas.json`

- Se guarda en el almacenamiento interno de la app. Se edita desde `:ajustes` (interfaz de texto simple) y se puede exportar e importar en `Documents/Bare/reglas.json`.
- Validar al cargar: si es inválido, mantener la última versión válida y mostrar el error.
- Toda modificación que relaje reglas pasa por la espera de `espera_para_relajar_min`.

Ejemplo (los IDs de paquete y canales se verifican en la Fase 0):

```json
{
  "version": 2,
  "general": {
    "espera_para_relajar_min": 10,
    "teclado_automatico": false,
    "abrir_con_coincidencia_unica": true,
    "exportar_notas": true
  },
  "apps": {
    "com.google.android.gm":      { "modo": "libre", "alias": ["correo", "mail"] },
    "com.whatsapp":               { "modo": "con-motivo", "alias": ["wsp"] },
    "org.telegram.messenger":     { "modo": "con-motivo" },
    "com.linkedin.android":       { "modo": "libre" },
    "com.instagram.android":      { "modo": "bloqueada" },
    "com.google.android.youtube": { "modo": "bloqueada" },
    "microapp:instagram":         { "modo": "micro-app", "alias": ["ig", "insta"],
                                    "motivo_si_no_hay_avisos_de": "com.instagram.android" },
    "microapp:youtube":           { "modo": "micro-app", "alias": ["yt", "videos"] }
  },
  "motivo": {
    "espera_segundos": 5,
    "opciones": ["escribirle a alguien", "buscar algo en un chat", "nada en particular"]
  },
  "avisos": {
    "reglas": [
      { "si": { "categoria": "missed_call" },                             "prioridad": "alta" },
      { "si": { "paquete": "com.google.android.gm" },                      "prioridad": "alta" },
      { "si": { "paquete": "com.whatsapp", "es_grupo": false },            "prioridad": "alta" },
      { "si": { "paquete": "com.whatsapp", "es_grupo": true },             "prioridad": "resumen" },
      { "si": { "paquete": "org.telegram.messenger", "es_grupo": true },   "prioridad": "resumen" },
      { "si": { "paquete": "com.instagram.android", "categoria": "msg" },  "prioridad": "alta",
        "abrir_con": "microapp:instagram" },
      { "si": { "paquete": "com.instagram.android" },                      "prioridad": "ignorar" },
      { "si": { "en_curso": true },                                         "prioridad": "ignorar" }
    ],
    "prioridad_por_defecto": "normal",
    "limpiar_bandeja_sistema": ["resumen", "ignorar:com.instagram.android"],
    "reaviso": { "minutos": 10, "maximo": 3, "silencio": "23:00-07:30" },
    "mostrar_al_desbloquear": true
  },
  "filtro_web": {
    "activo": true,
    "dns_salida": "red",
    "bloquear": ["instagram.com", "facebook.com", "tiktok.com", "x.com", "twitter.com",
                 "reddit.com", "threads.net", "youtube.com", "youtu.be"],
    "excluir_apps": []
  },
  "microapps": [
    {
      "id": "instagram",
      "inicio": "https://www.instagram.com/direct/inbox/",
      "dominios": ["instagram.com"],
      "rutas_permitidas": ["/direct/**", "/accounts/**", "/challenge/**", "/p/*", "/reel/*"],
      "perfiles_permitidos": [],
      "recursos": "assets/microapps/instagram/"
    },
    {
      "id": "youtube",
      "inicio": "nativo",
      "dominios": ["youtube.com", "m.youtube.com", "youtu.be"],
      "rutas_permitidas": ["/results", "/watch", "/@*", "/channel/*", "/playlist"],
      "redirigir": { "/": "inicio", "/feed/**": "inicio", "/shorts/*": "bloquear" },
      "suscripciones": "suscripciones.json",
      "ocultar_comentarios": true,
      "recursos": "assets/microapps/youtube/"
    }
  ]
}
```

Las reglas de avisos se evalúan en orden; gana la primera que coincide. Si la detección de mensajes de Instagram por categoría no funciona, usar el `channelId` observado en la Fase 0.

---

## 8. Arquitectura técnica

- **Lenguaje y UI:** Kotlin + Jetpack Compose. Usar Material 3 solo como base técnica, sin su apariencia: todos los componentes visibles son propios (`ui/tui`).
- **SDK:** `minSdk 30` (Android 11). `targetSdk` y `compileSdk`: la última versión estable al momento de compilar.
- **Build:** Gradle con Kotlin DSL y catálogo de versiones. Un módulo `app` es suficiente al inicio.
- **Paquetes:** `home`, `search`, `notes`, `avisos`, `friction`, `metrics`, `microapp` (motor), `microapp/instagram`, `microapp/youtube` (inicio nativo, RSS, suscripciones), `links`, `filter`, `config`, `ui/tui`.
- **Persistencia:** Room (avisos, notas, aperturas, motivos, caché de novedades de YouTube). Configuración en JSON con `kotlinx.serialization`.
- **Concurrencia:** Coroutines + Flow. Inyección de dependencias manual (un `AppContainer`), sin Hilt.
- **Componentes Android:**
  - `HomeActivity` (HOME)
  - `MotivoActivity`
  - `MicroAppActivity` (proceso `:microapp`; recibe también los enlaces entrantes)
  - `AvisosService` (`NotificationListenerService`)
  - `FiltroDnsService` (`VpnService`)
  - receptor de `BOOT_COMPLETED`
  - planificación de re-avisos (evaluar `AlarmManager` sin alarmas exactas, o un temporizador dentro del proceso del listener; documentar la decisión)
- **Permisos y accesos especiales:**

| Permiso / acceso | Para qué |
|---|---|
| `<queries>` MAIN/LAUNCHER (o `QUERY_ALL_PACKAGES`) | Listar apps |
| Acceso a notificaciones (`BIND_NOTIFICATION_LISTENER_SERVICE`) | M2, línea de música |
| Acceso a datos de uso (`PACKAGE_USAGE_STATS`) | M3 |
| Mostrar sobre otras apps (`SYSTEM_ALERT_WINDOW`) | Mostrar avisos al desbloquear |
| `BIND_VPN_SERVICE` + consentimiento de VPN | M5 |
| `INTERNET` | Micro-apps, RSS de YouTube y reenvío DNS |
| `POST_NOTIFICATIONS`, `VIBRATE` | Re-avisos |
| `RECEIVE_BOOT_COMPLETED` | Restaurar servicios |

- **Rendimiento:** sin bucles de sondeo; todo por eventos. Filtrar mientras se escribe debe tomar menos de un frame con ~150 apps.
- **Pruebas unitarias obligatorias:**
  - buscador (normalización y orden);
  - clasificador de avisos, incluido `abrir_con`;
  - control de rutas de las micro-apps;
  - lector de `subscriptions.csv` y del RSS de YouTube, incluida la exclusión de Shorts;
  - analizador DNS y búsqueda por sufijo;
  - lectura y validación de `reglas.json`;
  - regla de la espera para relajar.

---

## 9. Dispositivo y puesta en marcha

### 9.1 Dispositivo

Xiaomi Redmi Note 10 o Note 11 (el usuario no recuerda cuál). En la Fase 0, Claude Code lo identifica y lo registra en `docs/DISPOSITIVO.md`:

```
adb shell getprop ro.product.model
adb shell getprop ro.build.version.release
adb shell getprop ro.build.version.sdk
adb shell getprop ro.miui.ui.version.name
adb shell getprop ro.mi.os.version.name
```

### 9.2 Instalación y permisos por ADB

- Activar opciones de desarrollador (tocar 7 veces la versión de MIUI/HyperOS en "Sobre el teléfono"), **Depuración USB**, **Instalar vía USB** (MIUI suele exigir una cuenta Mi para esto) y **Depuración USB (ajustes de seguridad)**, necesaria para conceder permisos por comandos.
- Instalar con `./gradlew installDebug` o `adb install`. La instalación por ADB no se ve afectada por la verificación de desarrolladores de Google.
- Claude Code escribe `scripts/setup-device.ps1` (PowerShell, para el PC con Windows) que concede todo lo posible por ADB y deja la lista de lo que debe hacerse a mano. Como referencia (verificar cada comando en el dispositivo):

```
adb shell cmd notification allow_listener <pkg>/<pkg>.avisos.AvisosService
adb shell appops set <pkg> GET_USAGE_STATS allow
adb shell appops set <pkg> SYSTEM_ALERT_WINDOW allow
adb shell dumpsys deviceidle whitelist +<pkg>
adb shell cmd package set-home-activity <pkg>/.home.HomeActivity
# aprobar los dominios de las micro-apps (Android 12+; si no funciona, hacerlo a mano, ver 9.3)
adb shell pm set-app-links-allowed --user 0 --package <pkg> true
adb shell pm set-app-links-user-selection --user 0 --package <pkg> true instagram.com www.instagram.com youtube.com www.youtube.com m.youtube.com youtu.be
```

- Si Android bloquea algún acceso como "ajuste restringido": Ajustes › Apps › Bare › ⋮ › Permitir ajustes restringidos.

### 9.3 Lista de ajustes manuales (el script la imprime)

**Xiaomi**
- [ ] Ajustes › Apps › Bare › Inicio automático: activado.
- [ ] Ahorro de batería: "Sin restricciones".
- [ ] Otros permisos › "Mostrar ventanas emergentes mientras se ejecuta en segundo plano": activado.
- [ ] Bloquear Bare en recientes (candado).
- [ ] Navegación del sistema: 3 botones (los gestos no funcionan con launchers de terceros en MIUI/HyperOS).

**Filtro web**
- [ ] Ajustes › VPN › Bare › "VPN siempre activa": activado.
- [ ] Ajustes › DNS privado: desactivado.
- [ ] Chrome › Configuración › Privacidad › "Usar DNS seguro": desactivado.

**Enlaces**
- [ ] Ajustes › Apps › Bare › Abrir de forma predeterminada:
  - Activar "Abrir enlaces compatibles".
  - Agregar `instagram.com`, `www.instagram.com`, `youtube.com`, `www.youtube.com`, `m.youtube.com` y `youtu.be`.

**Instagram**
- [ ] Instalar la app oficial, iniciar sesión una vez y no volver a abrirla.
- [ ] En sus ajustes, dejar activas solo las notificaciones de mensajes.
- [ ] Ajustes › Apps › Instagram:
  - Abrir de forma predeterminada › desactivar "Abrir enlaces compatibles".
  - Inicio automático activado y batería "Sin restricciones", para que las notificaciones lleguen a tiempo.

**YouTube**
- [ ] Desinstalar la app oficial (o desactivarla si viene del sistema).
- [ ] Exportar las suscripciones desde Google Takeout e importarlas en la micro-app.

**Apps de terceros**
- [ ] Chrome: desactivar artículos sugeridos en la pestaña nueva.
- [ ] App de Google: desactivar Discover.
- [ ] WhatsApp:
  - Quitar el silencio de los grupos (para que el launcher pueda contarlos).
  - En Ajustes de Android › Apps › WhatsApp › Notificaciones, dejar el canal de grupos sin sonido ni vibración.
  - Mantener las notificaciones de chats individuales con sonido.
- [ ] Telegram: lo mismo para grupos y canales.

### 9.4 Salida de emergencia

Documentar en `CLAUDE.md` y en `:ayuda`: para volver al launcher de Xiaomi, Ajustes › Apps › Apps predeterminadas › Pantalla de inicio. También sirve `adb uninstall <pkg>`.

---

## 10. Fases y criterios de cierre

| Fase | Contenido | Se cierra cuando… |
|---|---|---|
| F0 Descubrimiento | Repo, `CLAUDE.md`, licencia GPL-3.0, build e instalación por ADB. Clonar las referencias de la sección 14 en `refs/` y escribir `docs/referencias.md` (por módulo: qué archivos de qué proyecto se reutilizan o estudian). `docs/DISPOSITIVO.md`, `docs/inventario-apps.md`, pantalla `:avisos-debug` y `docs/canales-notificacion.md` con lo observado en WhatsApp, Telegram, Gmail e Instagram | El launcher se instala como HOME y lista las apps en texto; el usuario aprobó el inventario y el plan de reutilización |
| F1 Núcleo | M0 + M1 + lenguaje visual | Criterios de M0 y M1 |
| F2 Avisos | M2 sin respuesta rápida: clasificación, `abrir_con`, inicio con avisos, resumen de grupos, re-aviso, al desbloquear | Criterios de M2 (el de Instagram se valida en F4) |
| F3 Fricción | M3 + `reglas.json` + espera para relajar + `:ajustes` | Criterios de M3; una semana de uso real sin fallos graves |
| F4 Micro-apps | M4: motor, enlaces entrantes, Instagram con notificaciones, YouTube con suscripciones locales | Criterios de M4.2 y M4.3 |
| F5 Filtro | M5 + script de puesta en marcha completo | Criterios de M5 |
| F6 Pulido | Respuesta rápida, `:semana`, envío de pedidos a Spindle (`♪`), revisión de rendimiento y batería | Revisión final con el usuario |

Las micro-apps (F4) van antes del filtro (F5): el filtro bloquea instagram.com y youtube.com en el navegador, que es como el usuario accede hoy a ellos.

---

## 11. Riesgos y limitaciones conocidas

- **MIUI/HyperOS detiene servicios en segundo plano.** Mitigación: inicio automático, sin restricciones de batería, candado en recientes y lista de verificación (9.3). Aplica también a la app oficial de Instagram, de la que dependen las notificaciones.
- **Instagram y YouTube cambian su web con frecuencia.** Mitigación:
  - rutas, CSS y JS en configuración;
  - modo de depuración de rutas bloqueadas;
  - reglas de YouTube basadas en un proyecto mantenido (Healthy YouTube).

  Meta o Google podrían pedir verificaciones de inicio de sesión desde el WebView o considerar este uso contrario a sus condiciones. Es un uso personal y el riesgo lo asume el usuario.
- **La app oficial de Instagram queda instalada.** Se puede abrir por caminos ajenos al launcher. Se mide como evasión (M3).
- **YouTube sin cuenta.** No hay historial, "ver más tarde" ni suscripciones sincronizadas con la cuenta de Google. Los feeds RSS de canales son públicos, pero Google podría cambiarlos.
- **La pantalla de motivo se puede evadir** abriendo WhatsApp desde recientes. Se mide (M3); si las evasiones son frecuentes, se evaluará un servicio de accesibilidad mínimo (M6, futuro), partiendo de Curbox o DigiPaws.
- **Filtro web:**
  - Ocupa la única ranura de VPN de Android.
  - El DNS privado de Android y el DNS seguro de Chrome pueden saltárselo.
  - Si una app trae su propio resolvedor DNS, no se filtra.
- **Ajustes restringidos y actualizaciones de Android/MIUI** pueden cambiar cómo se conceden los permisos especiales. El script de puesta en marcha debe ser fácil de volver a ejecutar.
- **WhatsApp reorganiza su interfaz y sus canales de notificación** (por ejemplo, los Canales se están moviendo a la pestaña Chats). Las reglas basadas en `es_grupo` son más estables que las basadas en `channelId`; preferirlas.

---

## 12. Decisiones abiertas (valor por defecto entre corchetes)

| # | Decisión | Estado / por defecto |
|---|---|---|
| D1 | Nombre | **Resuelta: Bare** |
| D2 | ¿YouTube bloqueado en el teléfono? | **Resuelta:** bloqueado en todo el sistema; solo existe la micro-app sin feed ni Shorts (M4.3) |
| D3 | ¿Se pueden abrir publicaciones y reels individuales que otros comparten por mensaje? | [Sí, sin sugerencias ni "más contenido"] |
| D4 | Perfil de Instagram permitido (perfil + historias) | [Pendiente: el @ de la novia del usuario; vacío hasta entonces] |
| D5 | ¿Cómo obtener las notificaciones de Instagram? | **Resuelta en principio:** app oficial `bloqueada` + avisos que abren la micro-app. Plan B: FeurStagram si la evasión es frecuente o las notificaciones fallan |
| D6 | ¿El teclado se abre solo al volver a inicio? | [No] |
| D7 | Espera para relajar reglas | [10 min] |
| D8 | Espera en la pantalla de motivo | [5 s] |
| D9 | Escribir el nombre de un contacto muestra "llamar / escribir" (requiere `READ_CONTACTS`) | [Fase 6, opcional] |
| D10 | YouTube: ¿suscripciones locales sin cuenta, o una alternativa con cuenta (ReVanced)? | [Locales, importadas de Takeout] |
| D11 | Shorts que llegan por enlace | [Bloqueados; alternativa: abrirlos como video normal] |
| D12 | ¿La micro-app de YouTube pide motivo al abrirla? | [No; su inicio ya es un buscador] |

---

## 13. Integración con Spindle

- **Línea de música en inicio:** vía `MediaSession`. No requiere nada especial de Spindle.
- **Pedidos de música:**
  - notas con prefijo `♪` (Fase 6);
  - acción `♪ pedir a Spindle` en la micro-app de YouTube (F4).

  Ambos usan `ACTION_SEND` al paquete de Spindle.
- **Lenguaje visual compartido:** sección 5.1. Ninguna app depende de la otra para funcionar.

---

## 14. Bases open source y cómo reutilizarlas

No se hace fork de un launcher completo. Las funciones de Bare (avisos, micro-apps, filtro DNS, estética de terminal) son lo bastante distintas como para que adaptar uno entero cueste más que armar un esqueleto propio. En cambio, **cada módulo parte de código existente**. En la Fase 0, Claude Code clona estos repos en `refs/` (`git clone --depth 1`), anota el commit y escribe `docs/referencias.md` con el mapa "módulo → archivos de referencia".

### 14.1 Tabla de referencias

| Proyecto | Licencia | Stack | Qué tomar |
|---|---|---|---|
| [KISS](https://github.com/Neamar/KISS) | GPLv3+ | Java | **Buscador (M0):** algoritmo de coincidencia, orden por uso, búsqueda de contactos. Es el launcher "escribe y abre" de referencia, activo en 2026 |
| [Olauncher](https://github.com/tanujnotes/Olauncher) | GPLv3 | Kotlin | Esqueleto de launcher de solo texto: actividad HOME, lista de apps, apps ocultas |
| [mLauncher](https://github.com/DroidWorksStudio/mLauncher) | GPLv3 | Kotlin | Fork de Olauncher: tiempo de uso por app, sin permiso de internet, tamaños de fuente por sección |
| [μLauncher](https://github.com/jrpie/launcher) | MIT | Android | Launcher mínimo y activo: perfil de trabajo y espacio privado, fuente Hack |
| [Literal Launcher](https://github.com/256x/launcher) | Verificar | Kotlin + Compose | Launcher de texto hecho desde cero en Compose, con fecha y batería integradas: referencia de UI en Compose |
| [T-UI](https://github.com/fandreuz/TUI-ConsoleLauncher) / [x.tui](https://github.com/xscriptor/x.tui.launcher) | Original Apache-2.0 / fork MIT | Java | **Estética de terminal:** comandos, alias, coincidencia tolerante a errores |
| [Kvaesitso](https://github.com/MM2-0/Kvaesitso) | GPLv3 | Kotlin + Compose | Arquitectura de un launcher grande en Compose centrado en búsqueda. Consultar patrones, no copiar en bloque |
| [Curbox](https://github.com/curbox-app/curbox-android) | GPLv3+ | Android | **M3, M5 y M6:** demora para cambios que relajan reglas (igual que el principio 5), bloqueo por ruta de URL, ocultar partes de la UI, reescribir una frase para abrir, límites de desbloqueos |
| [DigiPaws](https://github.com/nethical6/digipaws) | GPLv3 | Kotlin | Predecesor de Curbox. Bloqueo de Shorts y Reels por accesibilidad, para M6 si llega a hacer falta |
| [DNS66](https://github.com/julian-klode/dns66) | GPLv3 (partes solo v3) | Java | **M5:** VPN que solo desvía los servidores DNS y reenvía lo no bloqueado. Es exactamente el diseño pedido |
| [RethinkDNS](https://github.com/celzero/rethink-app) | Apache-2.0 | Kotlin | M5 moderno: reglas por app, manejo de DNS; mucho más grande que lo necesario |
| [FeurStagram](https://github.com/jean-voila/feurstagram) | GPLv3 | Parche (Morphe) | Plan B de Instagram (D5). También sirve para ver qué rutas usa Instagram para feed, Explorar y Reels |
| [FeedLock](https://github.com/caasion/FeedLock) | Verificar | Android | Bloqueo del feed de Instagram por accesibilidad (para M6) |
| [Healthy YouTube](https://github.com/gkpo/healthy-youtube) (confirmar la URL; su página de Firefox enlaza a `gkpo/healthy-youtube/issues`) | MIT | Extensión web | **M4.3:** CSS/JS para `m.youtube.com` (sin Shorts, relacionados, pantallas finales ni reproducción automática; inicio → suscripciones). Base directa del CSS/JS inyectado |
| [Control Panel for YouTube](https://github.com/insin/control-panel-for-youtube) | Sin licencia declarada | Extensión web | Lista muy completa de qué ocultar en YouTube móvil. **Solo ideas**: no copiar código |
| [NewPipe](https://github.com/TeamNewPipe/NewPipe) | GPLv3 | Java/Kotlin | **M4.3:** formato de importación de suscripciones (Takeout) y lógica de "novedades" de canales. Plan B de YouTube |
| [ReVanced patches](https://github.com/ReVanced/revanced-patches) | GPLv3 | Parches | Referencia de qué componentes de YouTube se pueden ocultar. Plan B con cuenta (D10) |
| [Mindful](https://github.com/akaMrNagar/Mindful) | GPL-2.0 | Flutter + nativo | Notificaciones agrupadas y programadas. **Solo ideas** (ver 14.4) |
| [personalDNSfilter](https://github.com/IngoZenz/personaldnsfilter) | GPL-2.0 | Java | Filtro DNS local. **Solo ideas** (ver 14.4) |

### 14.2 Mapa rápido módulo → base

| Módulo | Parte de |
|---|---|
| M0 Núcleo y buscador | KISS (buscador), Olauncher/mLauncher (HOME y lista de apps), Literal Launcher (UI en Compose) |
| M1 Captura | Propio |
| M2 Avisos | Propio, con ideas de Mindful (agrupación) |
| M3 Fricción | Curbox (demora para relajar, motivo escrito), mLauncher (tiempo de uso) |
| M4 Micro-apps | Propio (motor). Healthy YouTube (reglas de YouTube), NewPipe (Takeout), FeurStagram (rutas de Instagram) |
| M5 Filtro | DNS66 (núcleo), RethinkDNS (detalles modernos) |
| M6 Guardia (futuro) | Curbox / DigiPaws |

### 14.3 Cómo reutilizar

1. Leer el código de referencia del módulo antes de diseñarlo y resumir en `docs/referencias.md` cómo lo resuelve.
2. Copiar solo lo necesario, adaptarlo al estilo del proyecto (Kotlin, Compose, sin Hilt) y quitar lo que sobra.
3. Mantener el aviso de copyright original en cada archivo copiado y registrarlo en `THIRD_PARTY_NOTICES.md`.
4. No compilar los proyectos de `refs/` como parte del build de Bare ni agregarlos como submódulos.

### 14.4 Licencias

- Bare se publica como **GPL-3.0**. Así se puede incorporar código GPLv3, MIT y Apache-2.0. Las obligaciones de la GPL solo aplican si algún día se distribuye la app; para uso personal no cambia nada.
- Sí se puede copiar código de: GPLv3/GPLv3+ (KISS, Olauncher, mLauncher, Kvaesitso, Curbox, DigiPaws, DNS66, NewPipe, FeurStagram, ReVanced), MIT (μLauncher, x.tui, Healthy YouTube), Apache-2.0 (RethinkDNS, T-UI original).
- **Solo ideas, sin copiar código:**
  - proyectos GPL-2.0 que no digan "o posterior" (Mindful, personalDNSfilter), porque no son compatibles con GPLv3;
  - proyectos sin licencia (Control Panel for YouTube);
  - proyectos cuya licencia no se haya verificado.
- Si una licencia no está clara, Claude Code la verifica en el repo antes de copiar.

---

## 15. Glosario

- **Aviso:** notificación capturada y clasificada por el launcher.
- **Resumen:** prioridad para conversaciones de grupo; solo cuenta, nunca interrumpe.
- **Re-aviso:** recordatorio que el propio launcher emite cuando un aviso importante no se ha visto.
- **Motivo:** razón declarada al abrir una app `con-motivo` sin avisos pendientes.
- **Relajar:** cambio de configuración que hace las reglas menos estrictas; siempre con espera.
- **Micro-app:** versión reducida de un servicio web dentro del launcher, limitada a rutas permitidas.
- **Evasión:** apertura de una app con fricción o bloqueada por un camino que no pasa por el launcher.
