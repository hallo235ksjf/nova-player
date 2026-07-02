package com.ani.novaplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ani.novaplayer.MediaScanner
import com.ani.novaplayer.MediaTrack
import com.ani.novaplayer.PlayerViewModel
import com.ani.novaplayer.ui.theme.BgDeep
import com.ani.novaplayer.ui.theme.NeonGreen
import com.ani.novaplayer.ui.theme.TextSecondary

@Composable
fun LibraryScreen(
    hasPermission: Boolean,
    viewModel: PlayerViewModel,
    onOpenPlayer: () -> Unit
) {
    val context = LocalContext.current
    var tab by remember { mutableStateOf(0) }
    var audioTracks by remember { mutableStateOf(listOf<MediaTrack>()) }
    var videoTracks by remember { mutableStateOf(listOf<MediaTrack>()) }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            audioTracks = MediaScanner.scanAudio(context)
            videoTracks = MediaScanner.scanVideo(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgDeep, Color(0xFF161829))))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Nova Player",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.padding(24.dp, 32.dp, 24.dp, 8.dp)
            )

            Row(modifier = Modifier.padding(horizontal = 24.dp)) {
                TabChip("Songs", tab == 0, Icons.Filled.MusicNote) { tab = 0 }
                Spacer(Modifier.width(12.dp))
                TabChip("Videos", tab == 1, Icons.Filled.Movie) { tab = 1 }
            }

            Spacer(Modifier.height(16.dp))

            if (!hasPermission) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Berechtigung wird benötigt, um Medien zu lesen", color = TextSecondary)
                }
            } else {
                val list = if (tab == 0) audioTracks else videoTracks
                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Keine Medien gefunden", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(list) { track ->
                            TrackRow(track) {
                                viewModel.playQueue(list, list.indexOf(track))
                                onOpenPlayer()
                            }
                        }
                        item { Spacer(Modifier.height(90.dp)) }
                    }
                }
            }
        }

        MiniPlayer(
            viewModel = viewModel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            onClick = onOpenPlayer
        )
    }
}

@Composable
private fun TabChip(label: String, selected: Boolean, icon: ImageVector, onClick: () -> Unit) {
    GlassCard(
        cornerRadius = 20,
        modifier = Modifier
            .clickable { onClick() }
            .background(
                if (selected) Brush.horizontalGradient(listOf(NeonGreen.copy(alpha = 0.25f), NeonGreen.copy(alpha = 0.05f)))
                else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent)),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) NeonGreen else TextSecondary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, color = if (selected) Color.White else TextSecondary)
        }
    }
}

@Composable
private fun TrackRow(track: MediaTrack, onClick: () -> Unit) {
    GlassCard(
        cornerRadius = 18,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (track.isVideo) Icons.Filled.Movie else Icons.Filled.MusicNote,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, color = Color.White, maxLines = 1, style = MaterialTheme.typography.titleMedium)
                Text(track.artist, color = TextSecondary, fontSize = 13.sp, maxLines = 1)
            }
        }
    }
}
