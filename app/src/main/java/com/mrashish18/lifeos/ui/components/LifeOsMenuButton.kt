package com.mrashish18.lifeos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Polished, reusable hamburger menu button for LIFEOS top headers.
 * Connects directly to the single application ModalNavigationDrawer.
 */
@Composable
fun LifeOsMenuButton(
    onClick: () -> Unit,
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9))
            .then(
                if (isDarkMode) {
                    Modifier.border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = "Open navigation drawer and settings menu"
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "☰",
            fontSize = 17.sp,
            color = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF1E1B4B),
            fontWeight = FontWeight.Bold
        )
    }
}
