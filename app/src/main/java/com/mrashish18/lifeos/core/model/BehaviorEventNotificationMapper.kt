package com.mrashish18.lifeos.core.model

import com.mrashish18.lifeos.ui.navigation.LifeOsDestination

/**
 * Deterministically maps genuine LIFEOS behavior events to user-facing notifications.
 * Filters out low-level/trivial events to prevent notification noise.
 */
object BehaviorEventNotificationMapper {

    fun map(event: BehaviorEvent): LifeOsNotification? {
        val meta = event.metadata
        val timestamp = event.timestamp.toEpochMilli()

        return when (event.type) {
            BehaviorEventType.TASK_CREATED -> {
                val taskId = meta["taskId"] ?: event.id
                val title = meta["title"] ?: "Task"
                val priority = meta["priority"] ?: "MEDIUM"
                LifeOsNotification(
                    id = "notif_task_created_$taskId",
                    category = NotificationCategory.PERSONAL,
                    title = "Task created",
                    message = "$title ($priority priority) scheduled",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.TASKS,
                    entityId = taskId
                )
            }
            BehaviorEventType.TASK_STARTED -> {
                val taskId = meta["taskId"] ?: event.id
                val title = meta["title"] ?: "Task"
                LifeOsNotification(
                    id = "notif_task_started_$taskId",
                    category = NotificationCategory.PERSONAL,
                    title = "Task in progress",
                    message = "Working on: $title",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.TASKS,
                    entityId = taskId
                )
            }
            BehaviorEventType.TASK_COMPLETED -> {
                val taskId = meta["taskId"] ?: event.id
                val title = meta["title"] ?: "Task"
                LifeOsNotification(
                    id = "notif_task_completed_$taskId",
                    category = NotificationCategory.PERSONAL,
                    title = "Task completed",
                    message = "Completed: $title • Productivity registered",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.TASKS,
                    entityId = taskId
                )
            }
            BehaviorEventType.TASK_POSTPONED -> {
                val taskId = meta["taskId"] ?: event.id
                val title = meta["title"] ?: "Task"
                LifeOsNotification(
                    id = "notif_task_postponed_$taskId",
                    category = NotificationCategory.PERSONAL,
                    title = "Task postponed",
                    message = "Rescheduled: $title • Priority recalibrated",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.TASKS,
                    entityId = taskId
                )
            }
            BehaviorEventType.TASK_ABANDONED -> {
                val taskId = meta["taskId"] ?: event.id
                val title = meta["title"] ?: "Task"
                LifeOsNotification(
                    id = "notif_task_abandoned_$taskId",
                    category = NotificationCategory.PERSONAL,
                    title = "Task abandoned",
                    message = "Archived: $title",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.TASKS,
                    entityId = taskId
                )
            }
            BehaviorEventType.CLAIM_VERIFIED -> {
                val resultId = meta["resultId"] ?: event.id
                val verdict = meta["verdict"] ?: "Verified"
                val confidence = meta["confidence"] ?: "0"
                val evidenceCount = meta["evidenceCount"] ?: "0"
                LifeOsNotification(
                    id = "notif_claim_verified_$resultId",
                    category = NotificationCategory.TRUTH,
                    title = "Investigation completed",
                    message = "Verdict: $verdict ($confidence% confidence) • $evidenceCount sources analyzed",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.REALITY_CHECK,
                    entityId = resultId
                )
            }
            BehaviorEventType.EMERGENCY_STARTED -> {
                LifeOsNotification(
                    id = "notif_emergency_started_${event.id}",
                    category = NotificationCategory.EMERGENCY,
                    title = "Emergency workflow initiated",
                    message = "RescueMesh protocol armed. Mesh broadcast channels active.",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.RESILIENCE,
                    entityId = null
                )
            }
            BehaviorEventType.EMERGENCY_MESSAGE_CREATED -> {
                val messageId = meta["messageId"] ?: event.id
                val priority = meta["priority"] ?: "NORMAL"
                LifeOsNotification(
                    id = "notif_msg_created_$messageId",
                    category = NotificationCategory.EMERGENCY,
                    title = "Emergency message created",
                    message = "Priority $priority packet prepared for mesh transmission",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.RESILIENCE,
                    entityId = messageId
                )
            }
            BehaviorEventType.EMERGENCY_MESSAGE_QUEUED -> {
                val messageId = meta["messageId"] ?: event.id
                LifeOsNotification(
                    id = "notif_msg_queued_$messageId",
                    category = NotificationCategory.MESH,
                    title = "Emergency message queued",
                    message = "Packet placed in store-and-forward queue. Awaiting node encounter.",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.RESILIENCE,
                    entityId = messageId
                )
            }
            BehaviorEventType.EMERGENCY_MESSAGE_RELAYED -> {
                val messageId = meta["messageId"] ?: event.id
                LifeOsNotification(
                    id = "notif_msg_relayed_${messageId}_${event.id.take(6)}",
                    category = NotificationCategory.MESH,
                    title = "Emergency message relayed",
                    message = "Packet forwarded across local peer mesh hops",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.RESILIENCE,
                    entityId = messageId
                )
            }
            BehaviorEventType.EMERGENCY_MESSAGE_SENT,
            BehaviorEventType.EMERGENCY_MESSAGE_DELIVERED -> {
                val messageId = meta["messageId"] ?: event.id
                LifeOsNotification(
                    id = "notif_msg_delivered_$messageId",
                    category = NotificationCategory.EMERGENCY,
                    title = "Emergency message delivered",
                    message = "Direct transmission confirmation received • Packet delivered",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.RESILIENCE,
                    entityId = messageId
                )
            }
            BehaviorEventType.RECOMMENDATION_ACCEPTED -> {
                val recId = meta["recommendationId"] ?: event.id
                val title = meta["notes"]?.takeIf { it.isNotBlank() } ?: "Recommendation"
                LifeOsNotification(
                    id = "notif_rec_accepted_${event.id}",
                    category = NotificationCategory.LEARNING,
                    title = "Recommendation accepted",
                    message = "$title • Cognitive loop tuning priorities",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.INTELLIGENCE,
                    entityId = recId
                )
            }
            BehaviorEventType.SESSION_COMPLETED -> {
                val duration = meta["durationSeconds"]?.toLongOrNull()?.let { "${it / 60}m" } ?: "session"
                LifeOsNotification(
                    id = "notif_session_completed_${event.id}",
                    category = NotificationCategory.LEARNING,
                    title = "Focus session completed",
                    message = "Deep work ($duration) recorded • Habit trajectory updated",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.INTELLIGENCE,
                    entityId = null
                )
            }
            BehaviorEventType.SESSION_INTERRUPTED -> {
                LifeOsNotification(
                    id = "notif_session_interrupted_${event.id}",
                    category = NotificationCategory.LEARNING,
                    title = "Focus session interrupted",
                    message = "Focus session ended early • Behavior model updated",
                    timestampEpochMillis = timestamp,
                    isRead = false,
                    destination = LifeOsDestination.INTELLIGENCE,
                    entityId = null
                )
            }
            else -> null // Trivial internal state transitions do not spam notifications
        }
    }
}
