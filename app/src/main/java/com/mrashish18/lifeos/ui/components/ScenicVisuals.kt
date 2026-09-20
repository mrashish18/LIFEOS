package com.mrashish18.lifeos.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Scenic mountain illustration hero for Dashboard (Reference Image 2, Screen 1).
 * Features atmospheric sky gradient, mountain ridges, and glowing horizon.
 */
@Composable
fun DashboardScenicBanner(
    title: String = "A little focus today\nA much better tomorrow",
    pillText: String = "Your life. More possible.",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(148.dp),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 3.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scenic Canvas Background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Twilight sky gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF272161),
                            Color(0xFF4338CA),
                            Color(0xFF7C3AED),
                            Color(0xFFEC4899).copy(alpha = 0.85f),
                            Color(0xFFFDE047).copy(alpha = 0.5f)
                        )
                    )
                )

                // Background mountain ridge
                val bgPath = Path().apply {
                    moveTo(0f, h * 0.70f)
                    cubicTo(w * 0.20f, h * 0.45f, w * 0.40f, h * 0.65f, w * 0.65f, h * 0.50f)
                    cubicTo(w * 0.80f, h * 0.40f, w * 0.90f, h * 0.58f, w, h * 0.52f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = bgPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF4C1D95).copy(alpha = 0.75f), Color(0xFF1E1B4B))
                    )
                )

                // Middle mountain ridge
                val midPath = Path().apply {
                    moveTo(0f, h * 0.82f)
                    cubicTo(w * 0.25f, h * 0.60f, w * 0.45f, h * 0.78f, w * 0.75f, h * 0.65f)
                    cubicTo(w * 0.88f, h * 0.60f, w * 0.95f, h * 0.72f, w, h * 0.68f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = midPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2E1065).copy(alpha = 0.85f), Color(0xFF0F172A))
                    )
                )

                // Foreground smooth ridge
                val forePath = Path().apply {
                    moveTo(0f, h * 0.90f)
                    cubicTo(w * 0.35f, h * 0.78f, w * 0.65f, h * 0.88f, w, h * 0.80f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = forePath,
                    color = Color(0xFF090D1A)
                )

                // Atmospheric warm glow near bottom left
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFB703).copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(w * 0.30f, h * 0.80f),
                        radius = w * 0.35f
                    ),
                    radius = w * 0.35f,
                    center = Offset(w * 0.30f, h * 0.80f)
                )
            }

            // Text and Pill overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 21.sp,
                        letterSpacing = (-0.2).sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.5.dp)
                ) {
                    Text(
                        text = pillText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

/**
 * Scenic watercolor mountain banner for RealityCheck (Reference Image 2, Screen 6).
 * Theme-aware: renders a dramatic sunset in dark mode, soft pastel in light mode.
 */
@Composable
fun RealityCheckScenicHeader(
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        val w = size.width
        val h = size.height

        if (isDarkMode) {
            // ── Dark mode: dramatic dusk sky with warm horizon ──
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF4C1D95),
                        Color(0xFF7C3AED).copy(alpha = 0.7f),
                        Color(0xFFEC4899).copy(alpha = 0.45f),
                        Color(0xFFFDE047).copy(alpha = 0.3f)
                    )
                )
            )

            // Distant mountain ridge
            val bgPath = Path().apply {
                moveTo(0f, h * 0.55f)
                cubicTo(w * 0.15f, h * 0.35f, w * 0.35f, h * 0.50f, w * 0.55f, h * 0.38f)
                cubicTo(w * 0.75f, h * 0.30f, w * 0.88f, h * 0.45f, w, h * 0.40f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = bgPath,
                color = Color(0xFF4C1D95).copy(alpha = 0.55f)
            )

            // Middle mountain ridge
            val midPath = Path().apply {
                moveTo(0f, h * 0.72f)
                cubicTo(w * 0.25f, h * 0.50f, w * 0.45f, h * 0.65f, w * 0.70f, h * 0.52f)
                cubicTo(w * 0.85f, h * 0.46f, w * 0.95f, h * 0.62f, w, h * 0.58f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = midPath,
                color = Color(0xFF2E1065).copy(alpha = 0.7f)
            )

            // Foreground ridge
            val forePath = Path().apply {
                moveTo(0f, h * 0.88f)
                cubicTo(w * 0.35f, h * 0.78f, w * 0.65f, h * 0.86f, w, h * 0.80f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = forePath, color = Color(0xFF0B1020))

            // Warm horizon glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFB703).copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(w * 0.50f, h * 0.55f),
                    radius = w * 0.40f
                ),
                radius = w * 0.40f,
                center = Offset(w * 0.50f, h * 0.55f)
            )
        } else {
            // ── Light mode: soft pastel sky ──
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDDD6FE).copy(alpha = 0.6f),
                        Color(0xFFE0E7FF).copy(alpha = 0.4f),
                        Color(0xFFF8FAFC)
                    )
                )
            )

            // Distant soft purple mountains
            val bgPath = Path().apply {
                moveTo(0f, h * 0.55f)
                cubicTo(w * 0.15f, h * 0.35f, w * 0.35f, h * 0.50f, w * 0.55f, h * 0.38f)
                cubicTo(w * 0.75f, h * 0.30f, w * 0.88f, h * 0.45f, w, h * 0.40f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = bgPath,
                color = Color(0xFFC4B5FD).copy(alpha = 0.35f)
            )

            // Middle mountain ridge
            val midPath = Path().apply {
                moveTo(0f, h * 0.72f)
                cubicTo(w * 0.25f, h * 0.50f, w * 0.45f, h * 0.65f, w * 0.70f, h * 0.52f)
                cubicTo(w * 0.85f, h * 0.46f, w * 0.95f, h * 0.62f, w, h * 0.58f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = midPath,
                color = Color(0xFFA78BFA).copy(alpha = 0.25f)
            )

            // Foreground soft haze
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xFFF8FAFC).copy(alpha = 0.9f)),
                    startY = h * 0.6f,
                    endY = h
                )
            )
        }
    }
}

/**
 * Dramatic RescueMesh Scenic Mountain Hero (Reference Image 1, Screen 8).
 * Features hikers on a mountain ridge helping each other against a warm twilight sky.
 */
@Composable
fun RescueMeshScenicHero(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sunset/dusk sky gradient: deep navy through magenta to rich orange/gold horizon
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF07152F),
                        Color(0xFF1E1B4B),
                        Color(0xFF4C1D95),
                        Color(0xFF9D174D),
                        Color(0xFFF97316),
                        Color(0xFFFBBF24)
                    )
                )
            )

            // Warm horizon glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFBBF24).copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(w * 0.55f, h * 0.75f),
                    radius = w * 0.40f
                ),
                radius = w * 0.40f,
                center = Offset(w * 0.55f, h * 0.75f)
            )

            // Distant mountain layers
            val distMountain = Path().apply {
                moveTo(0f, h * 0.68f)
                cubicTo(w * 0.20f, h * 0.52f, w * 0.40f, h * 0.62f, w * 0.60f, h * 0.50f)
                cubicTo(w * 0.80f, h * 0.42f, w * 0.90f, h * 0.58f, w, h * 0.54f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = distMountain,
                color = Color(0xFF4C0519).copy(alpha = 0.65f)
            )

            // Foreground rugged mountain peak silhouette
            val peakMountain = Path().apply {
                moveTo(0f, h * 0.85f)
                lineTo(w * 0.25f, h * 0.72f)
                lineTo(w * 0.45f, h * 0.80f)
                lineTo(w * 0.72f, h * 0.60f) // Highest ridge where hikers stand
                lineTo(w * 0.88f, h * 0.70f)
                lineTo(w, h * 0.65f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = peakMountain,
                color = Color(0xFF030712)
            )

            // Stylized hikers silhouette helping each other at the summit (w * 0.68 to w * 0.76)
            // Standing hiker reaching down a hand
            val standingHikerX = w * 0.72f
            val standingHikerY = h * 0.60f
            // Head
            drawCircle(color = Color(0xFF030712), radius = 4.5f, center = Offset(standingHikerX, standingHikerY - 24f))
            // Body & Backpack
            drawCircle(color = Color(0xFF030712), radius = 7f, center = Offset(standingHikerX - 3f, standingHikerY - 14f))
            // Reaching Arm
            drawLine(
                color = Color(0xFF030712),
                start = Offset(standingHikerX, standingHikerY - 16f),
                end = Offset(standingHikerX - 16f, standingHikerY - 8f),
                strokeWidth = 3f
            )

            // Second hiker climbing up reaching up
            val climbingHikerX = w * 0.65f
            val climbingHikerY = h * 0.64f
            // Head
            drawCircle(color = Color(0xFF030712), radius = 4f, center = Offset(climbingHikerX, climbingHikerY - 16f))
            // Body
            drawCircle(color = Color(0xFF030712), radius = 6f, center = Offset(climbingHikerX, climbingHikerY - 9f))
            // Arm reaching up to meet
            drawLine(
                color = Color(0xFF030712),
                start = Offset(climbingHikerX, climbingHikerY - 12f),
                end = Offset(standingHikerX - 16f, standingHikerY - 8f),
                strokeWidth = 3f
            )

            // Subtle glowing connection mesh particles between them
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = 2.5f,
                center = Offset(standingHikerX - 16f, standingHikerY - 8f)
            )
        }

        // Overlay text
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "When traditional networks fail, people still matter.",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.90f),
                letterSpacing = 0.2.sp
            )
        }
    }
}

/**
 * Interactive Mesh Network Topology Visual (Reference Image 1, Screen 8 & Reference Image 2, Screen 8).
 * Displays central "YOU (Node Active)", "PEER-1 (Nearby)", "PEER-2 (Relay Ready)" with glowing animated waves.
 */
@Composable
fun RescueMeshNetworkTopologyVisual(
    localNodeId: String = "YOU",
    isOnline: Boolean = true,
    relayingCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meshPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseRadiusScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseRadiusScale"
    )
    val signalOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "signalOffset"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0A1838),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A6E)),
        shadowElevation = 6.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Topology Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Radial dark aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0F316C).copy(alpha = 0.6f), Color.Transparent),
                        center = Offset(w * 0.5f, h * 0.45f),
                        radius = w * 0.45f
                    ),
                    radius = w * 0.45f,
                    center = Offset(w * 0.5f, h * 0.45f)
                )

                val youPos = Offset(w * 0.50f, h * 0.26f)
                val peer1Pos = Offset(w * 0.22f, h * 0.68f)
                val peer2Pos = Offset(w * 0.78f, h * 0.68f)

                // Dotted connection lines
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

                // YOU -> PEER 1 curved link
                val path1 = Path().apply {
                    moveTo(youPos.x, youPos.y)
                    quadraticTo(w * 0.32f, h * 0.42f, peer1Pos.x, peer1Pos.y)
                }
                drawPath(
                    path = path1,
                    color = Color(0xFF06B6D4).copy(alpha = 0.6f),
                    style = Stroke(width = 2.5f, pathEffect = pathEffect)
                )

                // YOU -> PEER 2 curved link
                val path2 = Path().apply {
                    moveTo(youPos.x, youPos.y)
                    quadraticTo(w * 0.68f, h * 0.42f, peer2Pos.x, peer2Pos.y)
                }
                drawPath(
                    path = path2,
                    color = Color(0xFF8B5CF6).copy(alpha = 0.6f),
                    style = Stroke(width = 2.5f, pathEffect = pathEffect)
                )

                // PEER 1 <-> PEER 2 cross link
                val path3 = Path().apply {
                    moveTo(peer1Pos.x, peer1Pos.y)
                    quadraticTo(w * 0.50f, h * 0.78f, peer2Pos.x, peer2Pos.y)
                }
                drawPath(
                    path = path3,
                    color = Color(0xFF38BDF8).copy(alpha = 0.4f),
                    style = Stroke(width = 2f, pathEffect = pathEffect)
                )

                // Animated travelling signal packets along the routes
                // Packet 1: YOU -> PEER 1
                val sig1X = youPos.x + (peer1Pos.x - youPos.x) * signalOffset
                val sig1Y = youPos.y + (peer1Pos.y - youPos.y) * signalOffset
                drawCircle(color = Color(0xFF22D3EE), radius = 5f, center = Offset(sig1X, sig1Y))
                drawCircle(color = Color(0xFF22D3EE).copy(alpha = 0.4f), radius = 10f, center = Offset(sig1X, sig1Y))

                // Packet 2: YOU -> PEER 2
                val sig2X = youPos.x + (peer2Pos.x - youPos.x) * signalOffset
                val sig2Y = youPos.y + (peer2Pos.y - youPos.y) * signalOffset
                drawCircle(color = Color(0xFFA78BFA), radius = 5f, center = Offset(sig2X, sig2Y))
                drawCircle(color = Color(0xFFA78BFA).copy(alpha = 0.4f), radius = 10f, center = Offset(sig2X, sig2Y))

                // Pulsing wave around YOU
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = pulseAlpha * 0.35f),
                    radius = 32f * pulseRadiusScale,
                    center = youPos,
                    style = Stroke(width = 2f)
                )

                // Pulsing wave around PEER 1
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = pulseAlpha * 0.35f),
                    radius = 28f * pulseRadiusScale,
                    center = peer1Pos,
                    style = Stroke(width = 2f)
                )

                // Pulsing wave around PEER 2
                drawCircle(
                    color = Color(0xFF8B5CF6).copy(alpha = pulseAlpha * 0.35f),
                    radius = 28f * pulseRadiusScale,
                    center = peer2Pos,
                    style = Stroke(width = 2f)
                )
            }

            // Node Widgets Overlay

            // YOU Node (Top Center)
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1)))
                        )
                        .border(2.dp, Color(0xFF38BDF8), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "You",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "YOU",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Node Active",
                    fontSize = 9.sp,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // PEER-1 Node (Bottom Left)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF047857)))
                        )
                        .border(2.dp, Color(0xFF34D399), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Peer 1",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "PEER-1",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Nearby",
                    fontSize = 8.5.sp,
                    color = Color(0xFF34D399)
                )
            }

            // Central mesh badge
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LOCAL FIRST",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "CONNECTED TO PEOPLE",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "STRONGER TOGETHER",
                    fontSize = 7.5.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // PEER-2 Node (Bottom Right)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)))
                        )
                        .border(2.dp, Color(0xFFA78BFA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Peer 2",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "PEER-2",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Relay Ready",
                    fontSize = 8.5.sp,
                    color = Color(0xFFA78BFA)
                )
            }
        }
    }
}

/**
 * Footer scenic graphic for Message Queue (Reference Image 1, Screen 10).
 */
@Composable
fun RescueMeshHikersFooter(
    quote: String = "Messages travel farther when people care.",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(18.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sunset gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF312E81),
                        Color(0xFF831843),
                        Color(0xFFF97316),
                        Color(0xFFFBBF24)
                    )
                )
            )

            // Mountain ridge silhouette
            val ridge = Path().apply {
                moveTo(0f, h * 0.78f)
                lineTo(w * 0.28f, h * 0.58f) // Ridge summit where hikers stand
                lineTo(w * 0.55f, h * 0.72f)
                lineTo(w * 0.85f, h * 0.62f)
                lineTo(w, h * 0.70f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = ridge, color = Color(0xFF020617))

            // Two hiker silhouettes standing on ridge summit
            val hx = w * 0.28f
            val hy = h * 0.58f
            drawCircle(color = Color(0xFF020617), radius = 4f, center = Offset(hx, hy - 18f))
            drawCircle(color = Color(0xFF020617), radius = 6f, center = Offset(hx - 2f, hy - 10f))

            drawCircle(color = Color(0xFF020617), radius = 3.5f, center = Offset(hx + 12f, hy - 16f))
            drawCircle(color = Color(0xFF020617), radius = 5.5f, center = Offset(hx + 12f, hy - 9f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Messages travel farther\nwhen people care.",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.End,
                lineHeight = 17.sp
            )
        }
    }
}
