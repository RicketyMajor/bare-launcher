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
| Build (`ro.build.version.incremental`) | V816.0.6.0.ULSMIXM |
| Parche de seguridad | 2024-08-01 |
| ABI | arm64-v8a |
| Pantalla | 1080×2408, densidad 440 |

## Estado inicial (2026-11-07)

| Ajuste | Valor | Comentario |
|---|---|---|
| Launcher actual | `app.olauncher/.MainActivity` (Olauncher) | El usuario ya usa un launcher de texto |
| Navegación (`navigation_mode`) | 0 = 3 botones | Ya cumple 9.3 |
| DNS privado (`private_dns_mode`) | sin definir (Android usa "automático") | Hay que dejarlo en **desactivado** para M5 |
| Apps de terceros | 106 paquetes, 121 actividades launcher | Inventario completo en `inventario-apps.md` |
| Instagram oficial | no instalado | Se reinstala en F4 solo para recibir notificaciones |
| YouTube oficial | instalado (`com.google.android.youtube`); YouTube Music también | Desinstalar o desactivar (9.3) |
| Instalar vía USB | **desactivado** | `installDebug` falla con `INSTALL_FAILED_USER_RESTRICTED` |

## Consecuencias

- Android 14 trae "ajustes restringidos" para apps instaladas por ADB. Hay que verificar si bloquean el acceso a notificaciones y a accesibilidad (spec 9.2).
- HyperOS 1.0 aplica las mismas restricciones de segundo plano y de gestos que MIUI (spec 9.3 y 11).
- `targetSdk 37` en un dispositivo con SDK 34: las APIs nuevas requieren comprobaciones de `Build.VERSION`.

- El parche de seguridad es de agosto de 2024, con más de dos años de antigüedad: o el modelo ya no recibe actualizaciones, o hay una pendiente sin instalar. Hay que diseñar para el SDK 34 sin contar con una actualización.
