package com.ani.novaplayer

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

data class MediaTrack(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val isVideo: Boolean,
    val albumId: Long = -1
)

object MediaScanner {

    fun scanAudio(context: Context): List<MediaTrack> {
        val tracks = mutableListOf<MediaTrack>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection, selection, null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())
                tracks.add(
                    MediaTrack(
                        id = id,
                        uri = uri,
                        title = cursor.getString(titleCol) ?: "Unknown",
                        artist = cursor.getString(artistCol) ?: "Unknown Artist",
                        durationMs = cursor.getLong(durCol),
                        isVideo = false,
                        albumId = cursor.getLong(albumCol)
                    )
                )
            }
        }
        return tracks
    }

    fun scanVideo(context: Context): List<MediaTrack> {
        val tracks = mutableListOf<MediaTrack>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION
        )
        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection, null, null,
            "${MediaStore.Video.Media.TITLE} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id.toString())
                tracks.add(
                    MediaTrack(
                        id = id,
                        uri = uri,
                        title = cursor.getString(titleCol) ?: "Unknown",
                        artist = "Video",
                        durationMs = cursor.getLong(durCol),
                        isVideo = true
                    )
                )
            }
        }
        return tracks
    }
}
