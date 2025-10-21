package com.igmata.koadernoa

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import com.google.android.material.textfield.TextInputEditText
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

        createToolbar()
        updateCardView()
    }

    override fun onRestart() {
        updateCardView()
        super.onRestart()
    }

    private fun createToolbar() {
        binding.newButton.setOnClickListener {
            val editText = EditText(this)
            editText.setPadding(100,50,0,50)
            editText.hint = getString(R.string.new_note_dialog_hint)
            editText.setSingleLine(true)

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.new_note_dialog_question))
                .setView(editText)
                .setPositiveButton(getString(R.string.dialog_create)) { dialog, _ ->
                    val name = editText.text.toString().trim()
                    if (name.isNotEmpty()) notesManager.goToNoteEditor(name, "", -1)
                    else Toast.makeText(this, getString(R.string.note_no_title_message), Toast.LENGTH_LONG).show()
                }
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    fun updateCardView() {
        val array = notesManager.getJsonArray()
        if(array.isEmpty()) {
            binding.noNotesMessage.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else
            binding.noNotesMessage.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            try {
                binding.recyclerView.adapter = NoteAdapter(array)
            } catch (e: Exception) {  }
    }
}