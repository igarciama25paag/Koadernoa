package com.igmata.koadernoa

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.igmata.koadernoa.databinding.ActivityMainBinding

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

        toolBar()
        updateCardView()
    }

    fun toolBar() {
        binding.newButton.setOnClickListener {
            val editText = EditText(this)
            editText.hint = getString(R.string.new_note_dialog_hint)

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.new_note_dialog_question))
                .setView(editText)
                .setPositiveButton(getString(R.string.dialog_create)) { dialog, _ ->
                    val name = editText.text.toString().trim()
                    if (name.isNotEmpty()) {
                        notesManager.addNewNote(name)
                        updateCardView()
                    }
                }
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    fun updateCardView() {
        try {
            binding.recyclerView.adapter = NoteAdapter(notesManager.getJsonArray())
        } catch (e: Exception) {  }
    }
}