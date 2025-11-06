package com.igmata.koadernoa.util

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.igmata.koadernoa.NotepadActivity
import java.io.File

private const val NOTES_FILE = "notes.json"

class NotesManager {
    private val context: Context
    data class Note(var title: String, var content: String?)
    class UnexistingNoteException(message: String) : Exception(message)

    private fun interface BetweenLoadAndSave {
        fun inBetween(notes: ArrayList<Note>)
    }

    constructor(context: Context) {
        this.context = context
        ensureFileExistence()
    }

    private fun ensureFileExistence() {
        val file = File(context.filesDir, NOTES_FILE)
        if(!file.exists()) file.writeText("[]")
    }

    fun getNotesArray(): Array<Note> {
        ensureFileExistence()
        val json = context.openFileInput(NOTES_FILE)
            .bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(json, Array<Note>::class.java)
    }

    fun getNotesArrayList(): ArrayList<Note> {
        return ArrayList(getNotesArray().toList())
    }

    private fun saveNotes(notes: ArrayList<Note>) {
        ensureFileExistence()
        context.openFileOutput(NOTES_FILE, Context.MODE_PRIVATE).use {
            it.write(Gson().toJson(notes).toByteArray())
        }
    }

    private fun betweenLoadAndSave(between: BetweenLoadAndSave) {
        val notes = getNotesArrayList()
        between.inBetween(notes)
        saveNotes(notes)
    }

    fun addNewNote(title: String, content: String) {
        betweenLoadAndSave { notes ->
            notes.add(Note(title, content))
        }
    }

    fun addNewNote(note: Note) {
        betweenLoadAndSave { notes ->
            notes.add(note)
        }
    }

    fun deleteNote(id: Int) {
        betweenLoadAndSave { notes ->
            notes.remove(notes[id])
        }
    }

    fun saveNote(id: Int, title: String, content: String) {
        betweenLoadAndSave { notes ->
            notes[id].title = title
            notes[id].content = content
        }
    }

    fun saveNote(id: Int, note: Note) {
        betweenLoadAndSave { notes ->
            notes[id] = note
        }
    }

    fun goToNewNoteEditor(title: String?, content: String?) {
        val intent = Intent(context, NotepadActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        intent.putExtra("id", -1)
        context.startActivity(intent)
    }

    fun goToExistingNoteEditor(id: Int) {
        val notes = getNotesArray()
        if(0 <= id && id < notes.size) {
            val intent = Intent(context, NotepadActivity::class.java)
            intent.putExtra("title", notes[id].title)
            intent.putExtra("content", notes[id].content)
            intent.putExtra("id", id)
            context.startActivity(intent)
        } else
            throw UnexistingNoteException("Tried reaching unexisting Note $id")
    }
}