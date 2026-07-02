package com.ani.novaplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ani.novaplayer.PlayerViewModel
import com.ani.novaplayer.ui.theme.TextSecondary

@Composable
fun MiniPlayer(viewModel: PlayerViewModel, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (viewModel.currentTitle.isEmpty()) return

    GlassCard(
        cornerRadius = 22,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(viewModel.currentTitle, color = Color.White, maxLines = 1)
                Text(viewModel.currentArtist, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
            }
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    if (viewModel.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
