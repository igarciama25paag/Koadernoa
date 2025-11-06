package com.igmata.koadernoa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igmata.koadernoa.util.NotesManager
import com.igmata.koadernoa.databinding.FragmentNotesBinding

class NotesFragment : Fragment(), NotesAdapter.OnNoteActionListener {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var notesManager: NotesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        notesManager = NotesManager(binding.root.context)
        createRecycleView()
        return binding.root
    }

    private fun createRecycleView() {
        val array = notesManager.getNotesArrayList()
        if(!array.isEmpty())
            binding.recyclerView.adapter = NotesAdapter(array, this)
        updateRecycleView()
    }

    override fun onNoteDeleted() {
        updateRecycleView()
    }

    fun updateRecycleView() {
        if(notesManager.getNotesArray().isEmpty()) {
            binding.noNotesMessage.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.noNotesMessage.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
}