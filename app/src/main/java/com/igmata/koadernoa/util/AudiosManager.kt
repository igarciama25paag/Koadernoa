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
import java.io.FileInputStream
import java.io.FileOutputStream

private const val AUDIOS_DIRECTORY_NAME = "audios"
private const val DEFAULT_FILE_NAME = "recording_audio.mp3"

class AudiosManager(
    private val context: Context
) {
    data class Audio(var title: String, val duration: Int, val file: File)
    private val audiosDirectory by lazy {
        context.getDir(AUDIOS_DIRECTORY_NAME, Context.MODE_PRIVATE).also {
            if(!it.exists()) it.mkdirs()
        }
    }
    var defaultFile: File = File(context.filesDir, DEFAULT_FILE_NAME)
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    fun getAudiosArrayList(): ArrayList<Audio>  {
        val files = audiosDirectory.listFiles()
        if(files == null) return ArrayList()
        val audios = ArrayList<Audio>()
        for (file in files) {
            val mmr = MediaMetadataRetriever().also {
                it.setDataSource(FileInputStream(file).fd)
            }
            audios.add(Audio(
                file.nameWithoutExtension,
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt() / 1000,
                file)
            )
            mmr.release()
        }
        return audios
    }

    fun goToNewAudioRecorder(audio: Audio?) {
        context.startActivity(Intent(context, AudioRecorderActivity::class.java).also {
            var file = null as File?
            var duration = 0
            if(audio != null) {
                file = audio.file
                duration = audio.duration
            }
            it.putExtra("file", file)
            it.putExtra("duration", duration)
        })
    }

    fun saveDefaultAudioFile(name: String) {
        File(audiosDirectory, "$name.mp3").outputStream().use { out ->
            FileInputStream(defaultFile).copyTo(out)
        }
    }

    fun deleteAudio(audioFile: File) {
        audioFile.delete()
    }

    /**Recorder and Player**/

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            MediaRecorder(context)
        else MediaRecorder()
    }

    fun startRecorder() {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(defaultFile).fd)

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

    fun startPlayer() {
        MediaPlayer.create(context, defaultFile.toUri()).apply {
            player = this
            start()
        }
    }

    fun stopPlayer() {
        player?.stop()
        player?.release()
        player = null
    }

    fun getMediaPlayer() = player!!

    fun isPlaying(): Boolean {
        return if(player == null) false
        else player!!.isPlaying
    }

    fun setOnCompletionListener(ocl: MediaPlayer.OnCompletionListener) {
        player?.setOnCompletionListener(ocl)
    }
}