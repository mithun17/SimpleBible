package com.mithun.simplebible.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.data.FullNote
import com.mithun.simplebible.databinding.FragmentNotesBinding
import com.mithun.simplebible.ui.adapter.NotesAdapter
import com.mithun.simplebible.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private val notesViewModel: NotesViewModel by viewModels()

    private val notesAdapter by lazy {
        NotesAdapter {
            openNote(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNotes.adapter = notesAdapter
        initOberserveAndSubscribe()
    }

    private fun initOberserveAndSubscribe() {
        lifecycleScope.launchWhenCreated {
            notesViewModel.notes.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        notesAdapter.submitList(resource.data)
                    }
                    is Resource.Error -> {
                    }
                    is Resource.Loading -> {
                    }
                }
            }
        }

        notesViewModel.fetchNotes()
    }

    private fun openNote(note: FullNote) {
    }
}
