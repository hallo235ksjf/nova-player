package com.ani.novaplayer

import android.content.ContentUris
import android.net.Uri

fun albumArtUri(albumId: Long): Uri =
    ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)
