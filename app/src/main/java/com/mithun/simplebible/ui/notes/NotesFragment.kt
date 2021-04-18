package com.mithun.simplebible.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.data.FullNote
import com.mithun.simplebible.databinding.FragmentNotesBinding
import com.mithun.simplebible.ui.adapter.NotesAdapter
import com.mithun.simplebible.utilities.CommonUtils
import com.mithun.simplebible.utilities.ExtensionUtils.toCopyText
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.ResourcesUtil
import com.mithun.simplebible.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var resourcesUtil: ResourcesUtil

    private val notesViewModel: NotesViewModel by viewModels()

    private val notesAdapter by lazy {
        NotesAdapter { card, note ->
            shareNote(card, note)
        }
    }

    private val prefs by lazy {
        Prefs(requireContext())
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

        notesViewModel.fetchNotes(prefs.selectedBibleId)
    }

    private fun shareNote(card: View, note: FullNote) {
        val notesMenu = PopupMenu(requireContext(), card)
        notesMenu.menuInflater.inflate(R.menu.menu_notes_more_options, notesMenu.menu)
        notesMenu.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.editNote -> {
                    findNavController().navigate(NotesFragmentDirections.actionAddEditNote(note.chapterName, note.chapterId, note.verseIds.toIntArray(), note.comment))
                }
                R.id.shareNote -> {
                    // trigger share intent
                    CommonUtils.showShareIntent(requireContext(), note.toCopyText(resourcesUtil))
                }
                R.id.copyNote -> {
                    // copy to clipboard
                    CommonUtils.copyToClipboard(requireContext(), note.toCopyText(resourcesUtil))
                }
            }
            true
        }
        notesMenu.show()
    }
}
