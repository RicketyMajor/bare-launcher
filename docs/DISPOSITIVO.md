# Dispositivo

Detectado por ADB el 2026-11-07. La spec suponía un Redmi Note 10 o Note 11; el teléfono real es otro.

| Propiedad | Valor |
|---|---|
| Modelo comercial (`ro.product.marketname`) | Redmi 10 5G |
| Modelo (`ro.product.model`) | 22041219NY |
| Código (`ro.product.device`) | thunder (producto `thunder_global`) |
| Android (`ro.build.version.release`) | 14 |
| SDK (`ro.build.version.sdk`) | 34 |
| HyperOS (`ro.mi.os.version.name`) | OS1.0 |
| MIUI UI (`ro.miui.ui.version.name`) | V816 |

## Consecuencias

- Android 14 trae "ajustes restringidos" para apps instaladas por ADB. Hay que verificar si bloquean el acceso a notificaciones y a accesibilidad (spec 9.2).
- HyperOS 1.0 aplica las mismas restricciones de segundo plano y de gestos que MIUI (spec 9.3 y 11).
- `targetSdk 37` en un dispositivo con SDK 34: las APIs nuevas requieren comprobaciones de `Build.VERSION`.

## Pendiente (requiere el teléfono conectado)

- Densidad y resolución (`wm size`, `wm density`), ABI y parche de seguridad.
- Launcher actual (`cmd package resolve-activity … HOME`).
- Estado de la navegación (gestos o 3 botones), del DNS privado y de "Instalar vía USB".
