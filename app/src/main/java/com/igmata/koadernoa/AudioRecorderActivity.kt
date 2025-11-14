package com.igmata.koadernoa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igmata.koadernoa.databinding.ActivityAudioRecorderBinding
import com.igmata.koadernoa.util.AudiosManager
import kotlinx.coroutines.*

class AudioRecorderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioRecorderBinding
    private lateinit var timerJob: Job
    private var s = 0
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
        setupStopButton()
        setupPlayButton()
        setupExitButton()
        setupBackPressedCallBack()
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && s < 3600) {
                s++
                val minutes = s / 60
                val seconds = s % 60
                String.format("%02d:%02d", minutes, seconds).also { binding.timer.text = it }
                delay(1000)
            }
            tooLongAudio()
        }
    }

    private fun tooLongAudio() {
        Toast.makeText(this, getString(R.string.audio_time_limit_reached), Toast.LENGTH_LONG).show()
        stopRecording()
    }

    private fun setupStopButton() {
        binding.stopButton.setOnClickListener {
            stopRecording()
        }
    }

    private fun stopRecording() {
        timerJob.cancel()
        audiosManager.stopRecorder()

        binding.stopButton.visibility = View.GONE
        binding.playButton.visibility = View.VISIBLE

        binding.audioVisualizer.visibility = View.VISIBLE
        binding.audioVisualizer.setColor(getColor(R.color.orange))
        binding.audioVisualizer.setDensity(20f)
    }

    private fun setupPlayButton() {
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

    private fun setupBackPressedCallBack() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitDialog()
            }
        })
    }

    private fun setupExitButton() {
        binding.exitButton.setOnClickListener {
            exitDialog()
        }
    }

    private fun exitDialog() {
        var selectedOption = 0
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(getString(R.string.audio_exit_dialog_title))
            .setPositiveButton(getString(R.string.dialog_exit)) { dialog, which ->
                if (selectedOption == 0) {
                    if(binding.title.text.toString().trim().isEmpty()) {
                        Toast.makeText(this, getString(R.string.no_title_message), Toast.LENGTH_LONG).show()
                    } else {
                        stopRecording()
                        audiosManager.saveDefaultAudioFile(binding.title.text.toString())
                        finish()
                    }
                } else {
                    stopRecording()
                    finish()
                }
            }
            .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(
                arrayOf(
                    getString(R.string.dialog_option_exit_saving),
                    getString(R.string.dialog_option_exit_not_saving)
                ), selectedOption
            ) { dialog, which ->
                selectedOption = which
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}