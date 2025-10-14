package com.igmata.koadernoa

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val note: Array<Note>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val description: TextView
        val background: RelativeLayout

        init {
            // Define click listener for the ViewHolder's View
            title = view.findViewById(R.id.note_title)
            description = view.findViewById(R.id.note_content)
            background = view.findViewById(R.id.note_background)
        }
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
        viewHolder.title.text = note[position].title
        viewHolder.description.text = note[position].content
        viewHolder.background.setOnClickListener {
            viewHolder.itemView.context.startActivity(Intent(viewHolder.itemView.context, NotepadActivity::class.java))
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = note.size

}