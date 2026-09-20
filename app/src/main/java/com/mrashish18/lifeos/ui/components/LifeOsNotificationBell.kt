package com.mrashish18.lifeos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Polished, reusable notification bell component for LIFEOS top headers.
 * Matches existing 36-38dp rounded button styling with an animated unread badge.
 */
@Composable
fun LifeOsNotificationBell(
    unreadCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    backgroundColor: Color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9),
    borderColor: Color? = if (isDarkMode) Color(0xFF334155) else null
) {
    val description = if (unreadCount > 0) {
        "Notifications, $unreadCount unread"
    } else {
        "Notifications, no unread messages"
    }

    Box(
        modifier = modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .then(
                if (borderColor != null) {
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp))
                } else {
                    Modifier
                }
            )
            .clickable(
                onClick = onClick,
                role = Role.Button,
                onClickLabel = "Open notifications"
            )
            .semantics {
                contentDescription = description
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔔",
            fontSize = 15.sp
        )

        AnimatedVisibility(
            visible = unreadCount > 0,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (unreadCount > 9) 14.dp else 8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444)),
                contentAlignment = Alignment.Center
            ) {
                if (unreadCount > 9) {
                    Text(
                        text = "9+",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 8.sp
                    )
                }
            }
        }
    }
}
