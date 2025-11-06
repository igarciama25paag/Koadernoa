package com.igmata.koadernoa.util

import android.content.Context
import android.media.MediaMetadataRetriever
import java.io.File


private const val AUDIOS_DIRECTORY = "audios"

class AudiosManager {
    private val context: Context
    data class Audio(var title: String, val duration: String?, val file: File)

    constructor(context: Context) {
        this.context = context
        ensureFileExistence()
    }

    private fun ensureFileExistence() {
        val dir = context.getDir(AUDIOS_DIRECTORY, Context.MODE_PRIVATE)
        if(!dir.exists()) dir.mkdir()
    }

    fun getAudiosArrayList(): ArrayList<Audio>  {
        val files = context.getDir(AUDIOS_DIRECTORY, Context.MODE_PRIVATE).listFiles()
        if(files == null) return ArrayList()
        val audios = ArrayList<Audio>()
        for (file in files) {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(file.path)
            audios.add(Audio(
                file.name,
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION),
                file)
            )
            mmr.release()
        }
        return audios
    }
}