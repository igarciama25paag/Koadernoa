package com.igmata.koadernoa

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.igmata.koadernoa.databinding.ActivityMainBinding
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

        toolBar()
        cardView()
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
                    if (name.isNotEmpty()) addNewNote(name)
                }
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    fun cardView() {
        val json = assets.open("notes.json")
            .bufferedReader()
            .use { it.readText() }

        if(!json.isEmpty())
            binding.recyclerView.adapter = NoteAdapter(Gson().fromJson(json, Array<Note>::class.java))
        else
            Toast.makeText(this, "No notes file found", Toast.LENGTH_LONG).show()
    }

    fun addNewNote(name: String) {
        val json = assets.open("notes.json")
            .bufferedReader()
            .use { it.readText() }
        val notes = ArrayList(Gson().fromJson(json, Array<Note>::class.java).toList())

        var newID = 1
        for (l in notes) if(l.id >= newID) newID = l.id + 1
        notes.add(Note(newID, name, ""))

        var jsonString = Gson().toJson(notes)

        println(notes)

        openFileOutput("notes.json", Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }
}