package com.igmata.koadernoa.util

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.core.net.toUri
import com.igmata.koadernoa.AudioRecorderActivity
import java.io.File
import java.io.FileOutputStream

private const val AUDIOS_DIRECTORY_NAME = "audios"
private const val NEW_AUDIO_FILE_NAME = "recording_audio.mp3"

class AudiosManager(
    private val context: Context
) {
    data class Audio(var title: String, val duration: String?, val file: File)
    private val audiosDirectory by lazy { context.getDir(AUDIOS_DIRECTORY_NAME, Context.MODE_PRIVATE) }
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    init {
        ensureFileExistence()
    }

    private fun ensureFileExistence() {
        if(!audiosDirectory.exists()) audiosDirectory.mkdir()
    }

    fun getAudiosArrayList(): ArrayList<Audio>  {
        val files = audiosDirectory.listFiles()
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

    fun goToNewAudioRecorder() = context.startActivity(Intent(context, AudioRecorderActivity::class.java))

    fun newAudioFile(): File {
        return File(NEW_AUDIO_FILE_NAME)
    }

    fun saveNewAudio() {

    }

    /**Recorder and Player**/

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            MediaRecorder(context)
        else MediaRecorder()
    }

    fun startRecorder(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    fun stopRecorder() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }

    fun startPlayer(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    fun stopPlayer() {
        player?.stop()
        player?.release()
        player = null
    }
}