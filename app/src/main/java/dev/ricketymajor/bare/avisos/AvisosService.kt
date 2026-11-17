package dev.ricketymajor.bare.avisos

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/** Notification metadata only — never titles, texts or people (SPEC §0, M2). */
data class AvisoEvent(
    val kind: Char, // '+' posted, '-' removed, '=' already active when the listener connected
    val time: Long,
    val pkg: String,
    val channel: String?,
    val category: String?,
    val groupConversation: Boolean,
    val conversation: Boolean,
    val ongoing: Boolean,
    val importance: Int,
    val template: String?,
    val extrasKeys: List<String>,
)

class AvisosService : NotificationListenerService() {

    override fun onListenerConnected() {
        activeNotifications?.forEach { record(it, currentRanking, '=') }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification, rankingMap: RankingMap) =
        record(sbn, rankingMap, '+')

    override fun onNotificationRemoved(sbn: StatusBarNotification, rankingMap: RankingMap) =
        record(sbn, rankingMap, '-')

    private fun record(sbn: StatusBarNotification, rankingMap: RankingMap, kind: Char) {
        val ranking = Ranking().also { rankingMap.getRanking(sbn.key, it) }
        val n = sbn.notification
        val event = AvisoEvent(
            kind = kind,
            time = System.currentTimeMillis(),
            pkg = sbn.packageName,
            channel = redact(n.channelId),
            category = n.category,
            groupConversation = n.extras.getBoolean(Notification.EXTRA_IS_GROUP_CONVERSATION),
            conversation = ranking.isConversation,
            ongoing = sbn.isOngoing,
            importance = ranking.channel?.importance ?: -1,
            template = n.extras.getString(Notification.EXTRA_TEMPLATE)?.substringAfter('$'),
            extrasKeys = n.extras.keySet().sorted(),
        )
        events.update { (listOf(event) + it).take(MAX_EVENTS) }
    }

    companion object {
        private const val MAX_EVENTS = 100

        // ponytail: in-memory metadata log for :avisos-debug; F2 replaces it with the Room `aviso` table
        val events = MutableStateFlow(emptyList<AvisoEvent>())

        // Conversation channel ids embed the shortcut id, often a phone number.
        private val longDigits = Regex("\\d{6,}")
        private fun redact(s: String?) = s?.replace(longDigits, "#")
    }
}
