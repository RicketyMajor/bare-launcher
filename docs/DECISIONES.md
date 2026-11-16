# Decisiones técnicas

Formato ADR corto: contexto · decisión · alternativas · consecuencias. Más reciente arriba.

## ADR-004 — Ranura de VPN: Bare siempre activa, Tailscale a demanda

- **Contexto:** en el teléfono, Tailscale es la VPN siempre activa (`always_on_vpn_app = com.tailscale.ipn`). Android admite una sola VPN, y el filtro DNS de M5 es un `VpnService`.
- **Decisión:** en la Fase 5, Bare pasa a ser la VPN siempre activa. Tailscale se enciende a mano solo cuando el usuario necesita llegar a sus equipos; mientras está encendido, el filtro queda suspendido.
- **Alternativas:** filtrar con el DNS de Tailscale y NextDNS, excluyendo a Bare del túnel (depende de un servicio externo y saca el filtro de Bare); DNS privado con filtro (afectaría también a las micro-apps).
- **Consecuencias:** M5 debe detectar cuando otra VPN le quita la ranura (`onRevoke`) y mostrarlo en inicio, para que el filtro no quede apagado sin que el usuario lo note.

## ADR-003 — Tipografía: JetBrains Mono

- **Contexto:** la spec (5.1) pide una fuente monoespaciada libre incluida en la app, compartida con Spindle.
- **Decisión:** JetBrains Mono (OFL 1.1), en toda la interfaz.
- **Alternativas:** IBM Plex Mono (más cálida), Hack (la de μLauncher).
- **Consecuencias:** legible en tamaños chicos, con tildes y ñ completas. Spindle usa la misma.

## ADR-002 — applicationId `dev.ricketymajor.bare`

- **Contexto:** los permisos especiales, la VPN y los enlaces verificados quedan atados al paquete; cambiarlo después obliga a repetir la puesta en marcha.
- **Decisión:** `dev.ricketymajor.bare`; Spindle sería `dev.ricketymajor.spindle` (destino del `ACTION_SEND` de pedidos).
- **Alternativas:** `cl.alonso.bare`, `app.bare.launcher`.
- **Consecuencias:** `setup-device.ps1` y las reglas usan este paquete.

## ADR-001 — Esqueleto propio, sin fork de launcher

- **Contexto:** spec §14: las funciones de Bare (avisos, micro-apps, filtro DNS, estética de terminal) difieren demasiado de cualquier launcher existente.
- **Decisión:** proyecto Compose propio; cada módulo parte de código de referencia en `refs/`.
- **Alternativas:** fork de Olauncher/mLauncher o Kvaesitso.
- **Consecuencias:** más trabajo inicial; menos código ajeno que mantener.
