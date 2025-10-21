package com.igmata.koadernoa

import android.content.Context
import com.google.gson.Gson

class NotesManager {
    private val context: Context

    constructor(context: Context) {
        this.context = context
    }

    fun getJsonArray(): Array<Note> {
        val json = context.openFileInput("notes.json")
            .bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(json, Array<Note>::class.java)
    }

    fun getJsonArrayList(): ArrayList<Note> {
        val json = context.openFileInput("notes.json")
            .bufferedReader()
            .use { it.readText() }
        return ArrayList(Gson().fromJson(json, Array<Note>::class.java).toList())
    }

    private fun saveJson(notes: ArrayList<Note>) {
        context.openFileOutput("notes.json", Context.MODE_PRIVATE).use {
            it.write(Gson().toJson(notes).toByteArray())
        }
    }

    fun addNewNote(title: String, content: String) {
        val notes = getJsonArrayList()

        notes.add(Note( title, content))

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
}