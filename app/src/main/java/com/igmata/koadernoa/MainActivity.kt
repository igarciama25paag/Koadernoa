package com.igmata.koadernoa

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.igmata.koadernoa.databinding.ActivityMainBinding
import com.igmata.koadernoa.fragment.AudiosFragment
import com.igmata.koadernoa.fragment.NotesFragment
import com.igmata.koadernoa.util.AudiosManager
import com.igmata.koadernoa.util.NotesManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val notesManager by lazy { NotesManager(this) }
    private val audiosManager by lazy { AudiosManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buildNoteButton()
        createPager()
    }

    override fun onRestart() {
        createPager()
        super.onRestart()
    }

    private fun buildNoteButton() {
        binding.newButton.setImageResource(R.drawable.ic_new_note)
        binding.newButton.setOnClickListener {
            notesManager.goToNewNoteEditor(getString(R.string.note_default_title), "")
        }
    }

    private fun buildAudioButton() {
        binding.newButton.setImageResource(R.drawable.ic_new_audio)
        binding.newButton.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED)
                audiosManager.goToNewAudioRecorder()
            else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    0
            )
        }
    }

    private fun createPager() {
        binding.pager.adapter = ScreenSlidePagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                1 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_audios)
                else -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_notes)
            }
        }.attach()

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    1 -> {
                        binding.fragmentSubtitle.text = getString(R.string.fragment_audios_title)
                        buildAudioButton()
                    }
                    else -> {
                        binding.fragmentSubtitle.text = getString(R.string.fragment_notes_title)
                        buildNoteButton()
                    }
                }
            }
        })
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            1 -> AudiosFragment()
            else -> NotesFragment()
        }
    }
}