package com.igmata.koadernoa.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
        val play: CardView = view.findViewById(R.id.audio_button)
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
        val audiosManager = AudiosManager(context)

        holder.title.text = audio[position].title
        val s = audio[position].duration
        holder.duration.text = String.format("%02d:%02d", s/60, s%60)

        holder.play.setOnClickListener {

        }

        holder.itemView.setOnLongClickListener {
            audioLongClickListener(context, holder, position, audiosManager)
        }
    }

    private fun audioLongClickListener(context: Context, holder: ViewHolder, position: Int, audiosManager: AudiosManager): Boolean {
        val popupMenu = PopupMenu(context, holder.itemView)
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
                    Toast.makeText(context, "'${audio[position].title}' ${context.getString(
                        R.string.delete_toast)}", Toast.LENGTH_LONG).show()
                    audiosManager.deleteAudio(audio[position].file)
                    audio.removeAt(position)

                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                    listener.onAudioDeleted()
                    true
                } else -> true
            }
        }
        popupMenu.show()
        return true
    }

    override fun getItemCount(): Int = audio.size
}
