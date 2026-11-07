# Decisiones técnicas

Formato ADR corto: contexto · decisión · alternativas · consecuencias. Más reciente arriba.

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
