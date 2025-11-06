package com.igmata.koadernoa.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.igmata.koadernoa.R
import com.igmata.koadernoa.util.AudiosManager
import com.igmata.koadernoa.util.NotesManager

class AudiosAdapter(private val audio: ArrayList<AudiosManager.Audio>, private val listener: OnAudioActionListener) : RecyclerView.Adapter<AudiosAdapter.ViewHolder>() {

    interface OnAudioActionListener {
        fun onAudioDeleted()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.audio_title)
        val duration: TextView = view.findViewById(R.id.audio_duration)
        val play: ImageButton = view.findViewById(R.id.audio_button)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.audio_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val context = holder.itemView.context
        val notesManager = NotesManager(context)

        holder.title.text = audio[position].title
        holder.duration.text = audio[position].duration

        holder.play.setOnClickListener {

        }

        holder.itemView.setOnLongClickListener {
            audioLongClickListener(context, holder, position, notesManager)
        }
    }

    private fun audioLongClickListener(context: Context, holder: AudiosAdapter.ViewHolder, position: Int, notesManager: NotesManager): Boolean {
        return true
    }

    override fun getItemCount(): Int = audio.size
}
