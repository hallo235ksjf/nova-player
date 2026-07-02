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
    var currentTrack by mutableStateOf<MediaTrack?>(null)
        private set
    var currentPositionMs by mutableStateOf(0L)
    var durationMs by mutableStateOf(0L)
        private set

    private var queue: List<MediaTrack> = emptyList()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) {
            isPlaying = playing
        }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val id = mediaItem?.mediaId?.toLongOrNull() ?: return
            queue.find { it.id == id }?.let { currentTrack = it }
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
        queue = tracks
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
        currentTrack = tracks[startIndex]
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
