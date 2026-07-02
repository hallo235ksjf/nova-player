package com.ani.novaplayer.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.ani.novaplayer.PlayerViewModel
import com.ani.novaplayer.ui.theme.BgDeep
import com.ani.novaplayer.ui.theme.NeonGreen
import com.ani.novaplayer.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(viewModel: PlayerViewModel, onBack: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.tickPosition()
            delay(500)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgDeep, Color(0xFF181A2E))))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Zurück", tint = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            GlassCard(
                cornerRadius = 28,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (viewModel.isVideoPlaying) {
                    AndroidView(
                        factory = {
                            PlayerView(context).apply {
                                player = viewModel.controller
                                useController = false
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .background(accentGradient(), shape = RoundedCornerShape(28.dp))
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(viewModel.currentTitle, style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text(viewModel.currentArtist, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            Spacer(Modifier.height(16.dp))

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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previous() }) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
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
                IconButton(onClick = { viewModel.next() }) {
                    Icon(Icons.Filled.SkipNext, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
