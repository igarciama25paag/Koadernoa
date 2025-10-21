package com.igmata.koadernoa

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.igmata.koadernoa.databinding.ActivityNotepadBinding


class NotepadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotepadBinding
    private lateinit var notesManager: NotesManager
    private var id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notepad)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityNotepadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        notesManager = NotesManager(this)

        id = intent.getSerializableExtra("id") as Int
        binding.title.setText(intent.getSerializableExtra("title") as String)

        setupBackPressedCallBack()
        setupNoteContentEditor()
        setupExitButton()
    }

    private fun setupNoteContentEditor() {
        if(id != -1) binding.noteContentEditor.setText(intent.getSerializableExtra("content") as String)
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
            .setTitle("Notatik atera")
            .setPositiveButton(getString(R.string.dialog_exit)) { dialog, which ->
                if(selectedOption == 0)
                    if(id == -1)
                        notesManager.addNewNote(binding.title.text.toString(), binding.noteContentEditor.text.toString())
                    else
                        notesManager.saveNote(id, binding.title.text.toString(), binding.noteContentEditor.text.toString())
                finish()
            }
            .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(
                arrayOf(getString(R.string.dialog_option_exit_saving), getString(R.string.dialog_option_exit_not_saving)), selectedOption
            ) { dialog, which ->
                selectedOption = which
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}