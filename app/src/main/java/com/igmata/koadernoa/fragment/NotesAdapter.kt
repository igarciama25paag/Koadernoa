package com.igmata.koadernoa.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.igmata.koadernoa.util.NotesManager
import com.igmata.koadernoa.R

class NotesAdapter(private val note: ArrayList<NotesManager.Note>, private val listener: OnNoteActionListener) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    interface OnNoteActionListener {
        fun onNoteDeleted()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.note_title)
        val description: TextView = view.findViewById(R.id.note_content)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val context = holder.itemView.context
        val notesManager = NotesManager(context)

        holder.title.text = note[position].title
        holder.description.text = note[position].content

        holder.itemView.setOnClickListener {
            notesManager.goToExistingNoteEditor(position)
        }

        holder.itemView.setOnLongClickListener {
            noteLongClickListener(context, holder, position, notesManager)
        }
    }

    private fun noteLongClickListener(context: Context, viewHolder: ViewHolder, position: Int, notesManager: NotesManager): Boolean {
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
                    Toast.makeText(context, "'${note[position].title}' ${context.getString(
                        R.string.delete_toast)}", Toast.LENGTH_LONG).show()
                    notesManager.deleteNote(position)
                    note.removeAt(position)

                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                    listener.onNoteDeleted()
                    true
                } else -> true
            }
        }
        popupMenu.show()
        return true
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = note.size
}