package com.igmata.koadernoa

import android.graphics.drawable.Icon
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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
import com.igmata.koadernoa.util.NotesManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notesManager: NotesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        notesManager = NotesManager(this)

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

        }
    }

    private fun createPager() {
        binding.pager.adapter = ScreenSlidePagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_notes)
                else -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_audios)
            }
        }.attach()

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        binding.fragmentSubtitle.text = getString(R.string.fragment_notes_title)
                        buildNoteButton()
                    }
                    else -> {
                        binding.fragmentSubtitle.text = getString(R.string.fragment_audios_title)
                        buildAudioButton()
                    }
                }
            }
        })
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> NotesFragment()
            else -> AudiosFragment()
        }
    }
}