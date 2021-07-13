package com.mithun.simplebible.ui.notes

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.data.FullNote
import com.mithun.simplebible.databinding.FragmentAddEditNoteBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.VerseFormatter
import com.mithun.simplebible.utilities.gone
import com.mithun.simplebible.utilities.visible
import com.mithun.simplebible.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddEditNotesFragment : BaseFragment() {

    @Inject
    lateinit var prefs: Prefs

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditNotesFragmentArgs by navArgs()
    private val notesViewModel: NotesViewModel by viewModels()
    private val verseIds: List<String> by lazy { args.verses.map { "${args.chapterId}.$it" } }
    private val noteId: Long by lazy { args.noteId }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    private fun isAdd(): Boolean {
        return args.noteId == 0L
    }

    private fun initUi() {
        binding.tvNoteTitle.text = args.chapterFullName
        binding.etNotesComment.setText(args.comment)

        if (isAdd()) {
            notesViewModel.fetchListOfVerses(prefs.selectedBibleVersionId, verseIds)
        } else {
            notesViewModel.fetchNoteById(noteId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateMenu(R.menu.menu_save)
        subscribeUi()
    }

    private fun inflateMenu(menu: Int) {
        with(binding.toolbar.root) {
            inflateMenu(menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        saveNote()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun saveNote() {
        val comment = binding.etNotesComment.text.toString()
        notesViewModel.saveNote(
            noteId = noteId,
            bibleVersionId = prefs.selectedBibleVersionId,
            chapterId = args.chapterId,
            chapterName = args.chapterFullName,
            verseIds = args.verses.toList(),
            comment
        )
    }

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // observe note loading

                if (isAdd()) {
                    launch {
                        notesViewModel.verses.collect { resource ->
                            when (resource) {
                                is Resource.Success -> loadVersesInNote(resource.data)
                                is Resource.Error -> {
                                    binding.pbSaving.gone
                                }
                                is Resource.Loading -> binding.pbSaving.visible
                            }
                        }
                    }
                } else {
                    launch {
                        notesViewModel.note.collect { resource ->
                            when (resource) {
                                is Resource.Success -> loadNote(resource)
                                is Resource.Error -> {
                                    binding.pbSaving.gone
                                }
                                is Resource.Loading -> binding.pbSaving.visible
                            }
                        }
                    }
                }

                // observe note saving
                launch {
                    notesViewModel.noteSaveState.collect { resource ->
                        val data = resource.data
                        when (resource) {
                            is Resource.Success -> noteSaved()
                            is Resource.Error -> {
                                binding.pbSaving.gone
                            }
                            is Resource.Loading -> showLoading(data)
                        }
                    }
                }
            }
        }
    }

    private fun loadVersesInNote(verseEntities: List<VerseEntity>?) {
        binding.pbSaving.gone
        val verseStringBuilder = SpannableStringBuilder()
        verseEntities?.forEach { entity ->
            verseStringBuilder.append(
                VerseFormatter.formatVerseForDisplay(
                    requireContext(),
                    entity.number.toInt(),
                    entity.text
                )
            )
        }
        binding.tvNoteVerses.text = verseStringBuilder
    }

    private fun showLoading(data: Boolean?) {
        if (data == true) {
            binding.pbSaving.visible
        } else {
            binding.pbSaving.gone
        }
    }

    private fun noteSaved() {
        binding.pbSaving.gone
        findNavController().navigateUp()
    }

    private fun loadNote(resource: Resource<FullNote>) {
        val verseEntities = resource.data?.verses
        loadVersesInNote(verseEntities)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
