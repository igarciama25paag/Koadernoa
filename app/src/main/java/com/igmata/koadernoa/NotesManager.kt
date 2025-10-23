package com.igmata.koadernoa

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import java.io.File

class NotesManager {
    private val context: Context
    private val fileName: String = "notes.json"

    constructor(context: Context) {
        this.context = context
        var file = File(context.filesDir, fileName)
        if(!file.exists()) file.writeText("[]")
    }

    fun getJsonArray(): Array<Note> {
        val json = context.openFileInput(fileName)
            .bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(json, Array<Note>::class.java)
    }

    fun getJsonArrayList(): ArrayList<Note> {
        val json = context.openFileInput(fileName)
            .bufferedReader()
            .use { it.readText() }
        return ArrayList(Gson().fromJson(json, Array<Note>::class.java).toList())
    }

    private fun saveJson(notes: ArrayList<Note>) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(Gson().toJson(notes).toByteArray())
        }
    }

    fun addNewNote(title: String, content: String) {
        val notes = getJsonArrayList()

        notes.add(Note(title, content))

        saveJson(notes)
    }

    fun deleteNote(id: Int) {
        val notes = getJsonArrayList()

        notes.remove(notes[id])

        saveJson(notes)
    }

    fun saveNote(id: Int, title: String, content: String) {
        val notes = getJsonArrayList()

        notes[id].title = title
        notes[id].content = content

        saveJson(notes)
    }

    fun saveNote(id: Int, note: Note) {
        val notes = getJsonArrayList()

        notes[id] = note

        saveJson(notes)
    }

    fun goToNoteEditor(title: String?, content: String?, id: Int) {
        val intent = Intent(context, NotepadActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        intent.putExtra("id", id)
        context.startActivity(intent)
    }
}