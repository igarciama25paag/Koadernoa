package com.igmata.koadernoa

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igmata.koadernoa.databinding.ActivityAudioRecorderBinding
import com.igmata.koadernoa.util.AudiosManager
import kotlinx.coroutines.*

class AudioRecorderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioRecorderBinding
    private lateinit var timerJob: Job
    private val audiosManager by lazy { AudiosManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioRecorderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        audiosManager.startRecorder()
        startTimer()
        //createAudioVisualizer()
        createStopButton()
        createPlayButton()
    }

    private fun startTimer() {
        var s = 0
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                s++
                val minutes = s / 60
                val seconds = s % 60
                String.format("%02d:%02d", minutes, seconds).also { binding.timer.text = it }
                delay(1000)
            }
        }
    }

    /*private fun createAudioVisualizer() {
        binding.audioVisualizer.setColor(getColor(R.color.dark_gray))
        binding.audioVisualizer.setDensity(60f)
        //binding.audioVisualizer.setPlayer(mediaPlayer.audioSessionId)
    }*/

    private fun createStopButton() {
        binding.stopButton.setOnClickListener {
            timerJob.cancel()
            audiosManager.stopRecorder()
            binding.stopButton.visibility = View.GONE
            binding.playButton.visibility = View.VISIBLE
            binding.audioVisualizer.visibility = View.VISIBLE
            binding.audioVisualizer.setColor(getColor(R.color.orange))
            binding.audioVisualizer.setDensity(20f)
        }
    }

    private fun createPlayButton() {
        binding.playButton.setOnClickListener {
            if(audiosManager.isPlaying()) {
                binding.playButtonImage.setImageResource(R.drawable.ic_play_audio)
                audiosManager.stopPlayer()
            } else {
                binding.playButtonImage.setImageResource(R.drawable.ic_stop_audio)
                audiosManager.startPlayer()
                binding.audioVisualizer.setPlayer(audiosManager.getMediaPlayer().audioSessionId)
                audiosManager.setOnCompletionListener {
                    binding.playButtonImage.setImageResource(R.drawable.ic_play_audio)
                }
            }
        }
    }
}