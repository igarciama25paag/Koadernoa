package com.igmata.koadernoa.util

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.igmata.koadernoa.NotepadActivity
import java.io.File

private const val NOTES_FILE_NAME = "notes.json"

class NotesManager(
    private val context: Context
) {
    data class Note(var title: String, var content: String?)
    class UnexistingNoteException(message: String) : Exception(message)
    private val notesFile by lazy {
        File(context.filesDir, NOTES_FILE_NAME).also {
            if(!it.exists()) it.writeText("[]")
        }
    }

    private fun interface BetweenLoadAndSave {
        fun inBetween(notes: ArrayList<Note>)
    }

    fun getNotesArray(): Array<Note> {
        val json = notesFile
            .bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(json, Array<Note>::class.java)
    }

    fun getNotesArrayList(): ArrayList<Note> {
        return ArrayList(getNotesArray().toList())
    }

    /**Notes Managing**/

    private fun saveNotes(notes: ArrayList<Note>) {
        notesFile.outputStream().use {
            it.write(Gson().toJson(notes).toByteArray())
        }
    }

    private fun loadAndSave(between: BetweenLoadAndSave) {
        val notes = getNotesArrayList()
        between.inBetween(notes)
        saveNotes(notes)
    }

    fun addNewNote(title: String, content: String) {
        loadAndSave { notes ->
            notes.add(Note(title, content))
        }
    }

    fun addNewNote(note: Note) {
        loadAndSave { notes ->
            notes.add(note)
        }
    }

    fun deleteNote(id: Int) {
        loadAndSave { notes ->
            notes.remove(notes[id])
        }
    }

    fun saveNote(id: Int, title: String, content: String) {
        loadAndSave { notes ->
            notes[id].title = title
            notes[id].content = content
        }
    }

    fun saveNote(id: Int, note: Note) {
        loadAndSave { notes ->
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