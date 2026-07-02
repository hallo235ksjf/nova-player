package com.ani.novaplayer.ui

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.ani.novaplayer.PlayerViewModel
import com.ani.novaplayer.albumArtUri
import com.ani.novaplayer.ui.theme.BgDeep
import com.ani.novaplayer.ui.theme.NeonGreen
import com.ani.novaplayer.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(viewModel: PlayerViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val track = viewModel.currentTrack

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.tickPosition()
            delay(500)
        }
    }

    val infinite = rememberInfiniteTransition(label = "glow")
    val glowPulse by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(2600, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgDeep, Color(0xFF15071F), Color(0xFF181A2E))))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Zurück", tint = Color.White)
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Pulsierender Neon-Glow hinter der Cover-Art
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1f)
                        .scale(glowPulse)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6).copy(alpha = 0.55f),
                                    Color(0xFF6366F1).copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                GlassCard(
                    cornerRadius = 32,
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .aspectRatio(1f)
                ) {
                    if (track?.isVideo == true) {
                        AndroidView(
                            factory = {
                                PlayerView(context).apply {
                                    player = viewModel.controller
                                    useController = false
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (track != null) {
                        var artLoaded by remember(track.id) { mutableStateOf(true) }
                        if (!artLoaded) {
                            Box(
                                Modifier.fillMaxSize().background(accentGradient()),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.MusicNote,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }
                        AsyncImage(
                            model = albumArtUri(track.albumId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(32.dp)),
                            onState = { state ->
                                artLoaded = state is AsyncImagePainter.State.Success
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                track?.title ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(track?.artist ?: "", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            Spacer(Modifier.height(18.dp))

            Slider(
                value = viewModel.currentPositionMs.toFloat().coerceAtLeast(0f),
                onValueChange = { viewModel.seekTo(it.toLong()) },
                valueRange = 0f..(viewModel.durationMs.toFloat().coerceAtLeast(1f)),
                colors = SliderDefaults.colors(
                    thumbColor = NeonGreen,
                    activeTrackColor = NeonGreen,
                    inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(viewModel.currentPositionMs), color = TextSecondary, fontSize = 12.sp)
                Text(formatTime(viewModel.durationMs), color = TextSecondary, fontSize = 12.sp)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previous() }) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .scale(if (viewModel.isPlaying) glowPulse.coerceIn(0.97f, 1.03f) else 1f)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF10B981).copy(alpha = 0.5f), Color.Transparent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { viewModel.togglePlayPause() },
                        modifier = Modifier
                            .size(72.dp)
                            .background(accentGradient(), shape = CircleShape)
                    ) {
                        Icon(
                            if (viewModel.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                IconButton(onClick = { viewModel.next() }) {
                    Icon(Icons.Filled.SkipNext, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
