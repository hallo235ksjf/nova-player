package com.ani.novaplayer

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors

class PlayerViewModel(private val context: Context) {

    var controller: MediaController? = null
        private set

    var isPlaying by mutableStateOf(false)
        private set
    var currentTitle by mutableStateOf("")
        private set
    var currentArtist by mutableStateOf("")
        private set
    var currentPositionMs by mutableStateOf(0L)
    var durationMs by mutableStateOf(0L)
        private set
    var isVideoPlaying by mutableStateOf(false)
        private set

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) {
            isPlaying = playing
        }
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            currentTitle = mediaMetadata.title?.toString() ?: ""
            currentArtist = mediaMetadata.artist?.toString() ?: ""
        }
    }

    fun connect(onReady: () -> Unit = {}) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        future.addListener({
            controller = future.get()
            controller?.addListener(playerListener)
            onReady()
        }, MoreExecutors.directExecutor())
    }

    fun playQueue(tracks: List<MediaTrack>, startIndex: Int) {
        val items = tracks.map { track ->
            MediaItem.Builder()
                .setUri(track.uri)
                .setMediaId(track.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .build()
                )
                .build()
        }
        isVideoPlaying = tracks[startIndex].isVideo
        controller?.apply {
            setMediaItems(items, startIndex, 0L)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun next() = controller?.seekToNextMediaItem()
    fun previous() = controller?.seekToPreviousMediaItem()
    fun seekTo(positionMs: Long) { controller?.seekTo(positionMs) }

    fun tickPosition() {
        controller?.let {
            currentPositionMs = it.currentPosition
            durationMs = it.duration.coerceAtLeast(0L)
        }
    }

    fun release() {
        controller?.release()
        controller = null
    }
}
