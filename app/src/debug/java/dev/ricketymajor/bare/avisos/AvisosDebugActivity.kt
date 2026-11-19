package dev.ricketymajor.bare.avisos

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Live notification metadata (no text) to write rules in `reglas.json` (SPEC M2). */
class AvisosDebugActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val events by AvisosService.events.collectAsState()
            val style = TextStyle(color = Color.White, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
            val dim = style.copy(color = Color(0xFF8A8A8A))
            LazyColumn(
                Modifier.fillMaxSize().background(Color.Black).systemBarsPadding().padding(16.dp),
            ) {
                item {
                    BasicText("AVISOS-DEBUG · ${events.size} eventos", style = style)
                    if (!hasAccess()) {
                        BasicText(
                            "sin acceso a notificaciones › conceder",
                            style = style.copy(color = Color(0xFFFFB000)),
                            modifier = Modifier.clickable {
                                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            }.padding(vertical = 12.dp),
                        )
                    }
                }
                items(events) { e ->
                    BasicText(
                        "${e.kind} ${DateFormat.format("HH:mm:ss", e.time)}  ${e.pkg}",
                        style = style,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                    BasicText(
                        """
                        |  ch   ${e.channel} · imp ${e.importance}
                        |  cat  ${e.category} · grupo ${e.groupConversation.si()} · conv ${e.conversation.si()} · curso ${e.ongoing.si()}
                        |  tpl  ${e.template}
                        |  keys ${e.extrasKeys.joinToString { it.removePrefix("android.") }}
                        """.trimMargin(),
                        style = dim,
                    )
                }
            }
        }
    }

    private fun hasAccess() = getSystemService(NotificationManager::class.java)
        .isNotificationListenerAccessGranted(ComponentName(this, AvisosService::class.java))
}

private fun Boolean.si() = if (this) "sí" else "no"
