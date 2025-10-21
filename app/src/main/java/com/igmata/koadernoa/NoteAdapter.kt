package com.igmata.koadernoa

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val note: Array<Note>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.note_title)
        val description: TextView = view.findViewById(R.id.note_content)
        val background: RelativeLayout = view.findViewById(R.id.note_background)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.note_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val context = (viewHolder.itemView.context as MainActivity)
        val notesManager = NotesManager(context)

        viewHolder.title.text = note[position].title
        viewHolder.description.text = note[position].content

        viewHolder.background.setOnClickListener {
            val note = notesManager.getJsonArrayList()[position]
            context.goToNoteEditor(note.title, note.content, position)
        }

        viewHolder.background.setOnLongClickListener {
            val popupMenu = PopupMenu(context, viewHolder.itemView)
            popupMenu.inflate(R.menu.note_popup_menu)

            // Force icons on menu to appear
            try {
                val fieldMPopup = popupMenu.javaClass.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val menuPopupHelper = fieldMPopup.get(popupMenu)
                val setForceIcons = menuPopupHelper.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                setForceIcons.invoke(menuPopupHelper, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_delete -> {
                        Toast.makeText(context, "'${note[position].title.toString()}' deleted", Toast.LENGTH_LONG).show()
                        notesManager.deleteNote(position)
                        context.updateCardView()
                        true
                    } else -> true
                }
            }
            popupMenu.show()
            true
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = note.size
}