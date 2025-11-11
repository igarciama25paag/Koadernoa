package com.igmata.koadernoa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igmata.koadernoa.databinding.FragmentAudiosBinding
import com.igmata.koadernoa.util.AudioPlayer
import com.igmata.koadernoa.util.AudiosManager

class AudiosFragment : Fragment(), AudiosAdapter.OnAudioActionListener {

    private lateinit var binding: FragmentAudiosBinding
    private val audiosManager by lazy { AudiosManager(binding.root.context) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudiosBinding.inflate(inflater, container, false)
        createRecycleView()
        return binding.root
    }

    private fun createRecycleView() {
        val arrayList = audiosManager.getAudiosArrayList()
        if(!arrayList.isEmpty())
            binding.recyclerView.adapter = AudiosAdapter(arrayList, this)
        updateRecycleView()
    }

    override fun onAudioDeleted() {
        updateRecycleView()
    }

    fun updateRecycleView() {
        if(audiosManager.getAudiosArrayList().isEmpty()) {
            binding.noAudiosMessage.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.noAudiosMessage.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }
}