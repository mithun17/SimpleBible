package com.mithun.simplebible.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.data.FullNote
import com.mithun.simplebible.databinding.FragmentNotesBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.ui.adapter.NotesAdapter
import com.mithun.simplebible.utilities.CommonUtils
import com.mithun.simplebible.utilities.ExtensionUtils.toCopyText
import com.mithun.simplebible.utilities.ResourcesUtil
import com.mithun.simplebible.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : BaseFragment() {

    @Inject
    lateinit var resourcesUtil: ResourcesUtil

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private val notesViewModel: NotesViewModel by viewModels()
    private val notesAdapter by lazy {
        NotesAdapter { card, note ->
            shareNote(card, note)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        binding.rvNotes.adapter = notesAdapter
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notesViewModel.notes.collect { resource ->
                    when (resource) {
                        is Resource.Success -> notesAdapter.submitList(resource.data)
                        is Resource.Error -> {
                        }
                        is Resource.Loading -> {
                        }
                    }
                }
            }
        }
    }

    private fun shareNote(card: View, note: FullNote) {
        val notesMenu = PopupMenu(requireContext(), card)
        notesMenu.menuInflater.inflate(R.menu.menu_notes_more_options, notesMenu.menu)
        notesMenu.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.editNote -> {
                    findNavController().navigate(NotesFragmentDirections.actionAddEditNote(note.id, note.chapterName, note.chapterId, note.verseIds.toIntArray(), note.comment))
                }
                R.id.shareNote -> {
                    // trigger share intent
                    CommonUtils.showTextShareIntent(requireContext(), note.toCopyText(resourcesUtil))
                }
                R.id.copyNote -> {
                    // copy to clipboard
                    CommonUtils.copyToClipboard(requireContext(), note.toCopyText(resourcesUtil))
                }
                R.id.deleteNote -> {
                    notesViewModel.deleteNote(note)
                }
            }
            true
        }
        notesMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
