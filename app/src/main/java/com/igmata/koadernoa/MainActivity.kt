package com.igmata.koadernoa

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        super.onRestart()
        updateCardView()
    }

    private fun createToolbar() {
        binding.newButton.setOnClickListener {
            val editText = EditText(this)
            editText.hint = getString(R.string.new_note_dialog_hint)

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.new_note_dialog_question))
                .setView(editText)
                .setPositiveButton(getString(R.string.dialog_create)) { dialog, _ ->
                    val name = editText.text.toString().trim()
                    if (name.isNotEmpty()) goToNoteEditor(name, "", -1)
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

    fun goToNoteEditor(title: String?, content: String?, id: Int) {
        val intent = Intent(this, NotepadActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}