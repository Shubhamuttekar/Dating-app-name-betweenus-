package com.example.ui.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightPrimary
import com.example.ui.theme.MutedRose
import com.example.ui.theme.MutedRoseAccent
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftLavenderLight
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TrustOrbVisual(
    modifier: Modifier = Modifier,
    heightDp: Int = 180
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TrustOrbAnimation")

    // Outer rotation angle
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    // Breathing inner core pulse
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CorePulse"
    )

    // Inner rotation counter angle
    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CounterRotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MidnightDark,
                        MidnightPrimary,
                        Color(0xFF23182B)
                    )
                )
            )
            .testTag("trust_orb_canvas_container")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val baseRadius = minOf(centerX, centerY) * 0.45f

            // Ambient background glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SoftLavender.copy(alpha = 0.35f),
                        MutedRose.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = baseRadius * 2.2f
                ),
                center = Offset(centerX, centerY),
                radius = baseRadius * 2.2f
            )

            // Inner core glowing sphere
            val coreRadius = baseRadius * 0.42f * corePulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SoftLavenderLight.copy(alpha = 0.95f),
                        MutedRoseAccent.copy(alpha = 0.7f),
                        SoftLavender.copy(alpha = 0.2f)
                    ),
                    center = Offset(centerX, centerY),
                    radius = coreRadius
                ),
                center = Offset(centerX, centerY),
                radius = coreRadius
            )

            // Geometric Icosahedron / Lattice outer wireframe representation
            val numRings = 4
            val pointsPerRing = 8

            for (r in 1..numRings) {
                val ringRadius = baseRadius * (r / numRings.toFloat())
                val currentAngleOffset = if (r % 2 == 0) rotationAngle else counterRotation
                val path = Path()

                var firstPoint = Offset.Zero
                for (i in 0 until pointsPerRing) {
                    val angleRad = Math.toRadians((i * (360.0 / pointsPerRing) + currentAngleOffset))
                    // Add subtle 3D wobble
                    val zFactor = sin(angleRad * 2 + Math.toRadians(rotationAngle.toDouble())) * 0.15
                    val px = centerX + (ringRadius * (1 + zFactor) * cos(angleRad)).toFloat()
                    val py = centerY + (ringRadius * 0.65f * (1 + zFactor) * sin(angleRad)).toFloat()

                    if (i == 0) {
                        path.moveTo(px, py)
                        firstPoint = Offset(px, py)
                    } else {
                        path.lineTo(px, py)
                    }

                    // Draw node points
                    drawCircle(
                        color = if (i % 2 == 0) MutedRoseAccent.copy(alpha = 0.85f) else SoftLavenderLight.copy(alpha = 0.85f),
                        radius = 2.5.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
                path.lineTo(firstPoint.x, firstPoint.y)

                drawPath(
                    path = path,
                    color = if (r % 2 == 0) MutedRose.copy(alpha = 0.5f) else SoftLavenderLight.copy(alpha = 0.45f),
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }

            // Radial connecting spokes for 3D lattice depth
            for (i in 0 until pointsPerRing) {
                val outerAngleRad = Math.toRadians((i * (360.0 / pointsPerRing) + rotationAngle))
                val innerAngleRad = Math.toRadians((i * (360.0 / pointsPerRing) + counterRotation))

                val outerX = centerX + (baseRadius * cos(outerAngleRad)).toFloat()
                val outerY = centerY + (baseRadius * 0.65f * sin(outerAngleRad)).toFloat()

                val innerX = centerX + (coreRadius * cos(innerAngleRad)).toFloat()
                val innerY = centerY + (coreRadius * 0.65f * sin(innerAngleRad)).toFloat()

                drawLine(
                    color = SoftLavender.copy(alpha = 0.35f),
                    start = Offset(outerX, outerY),
                    end = Offset(innerX, innerY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Floating luminous particles
            val particleCount = 12
            for (p in 0 until particleCount) {
                val pAngle = Math.toRadians((p * (360.0 / particleCount) + rotationAngle * 1.5))
                val pDist = baseRadius * (0.8f + 0.5f * sin(pAngle + p).toFloat())
                val px = centerX + (pDist * cos(pAngle)).toFloat()
                val py = centerY + (pDist * 0.7f * sin(pAngle)).toFloat()

                drawCircle(
                    color = if (p % 3 == 0) MutedRoseAccent.copy(alpha = 0.9f) else SoftLavenderLight.copy(alpha = 0.8f),
                    radius = (1.8f + (p % 3) * 0.8f).dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }
    }
}
