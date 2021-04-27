package com.mithun.simplebible.ui.notes

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentAddEditNoteBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.VerseFormatter
import com.mithun.simplebible.utilities.gone
import com.mithun.simplebible.utilities.visible
import com.mithun.simplebible.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditNotesFragment : BaseFragment() {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    val args: AddEditNotesFragmentArgs by navArgs()

    private val notesViewModel: NotesViewModel by viewModels()

    private var supportActionBar: ActionBar? = null

    private val verseIds: List<String> by lazy {
        args.verses.map {
            "${args.chapterId}.$it"
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
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.tvNoteTitle.text = args.chapterFullName
        binding.etNotesComment.setText(args.comment)
        initObserveAndSubscribe()
        notesViewModel.fetchListOfVerses(prefs.selectedBibleVersionId, verseIds)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_save -> {
                saveNote()
            }
            android.R.id.home -> {
                findNavController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        val comment = binding.etNotesComment.text.toString()
        notesViewModel.saveNote(
            bibleVersionId = prefs.selectedBibleVersionId,
            chapterId = args.chapterId,
            chapterName = args.chapterFullName,
            verseIds = args.verses.toList(),
            comment
        )
    }

    private fun initObserveAndSubscribe() {

        lifecycleScope.launchWhenCreated {

            notesViewModel.verses.collect { resource ->
                when (resource) {
                    is Resource.Success -> {

                        binding.pbSaving.gone
                        val verseEntities = resource.data

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
                    is Resource.Error -> {
                        binding.pbSaving.gone
                    }
                    is Resource.Loading -> {
                        binding.pbSaving.visible
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {

            notesViewModel.noteSaveState.collect { resource ->
                val data = resource.data
                when (resource) {
                    is Resource.Success -> {
                        binding.pbSaving.gone
                        requireActivity().onBackPressed()
                    }
                    is Resource.Error -> {
                        binding.pbSaving.gone
                    }
                    is Resource.Loading -> {
                        if (data == true) {
                            binding.pbSaving.visible
                        } else {
                            binding.pbSaving.gone
                        }
                    }
                }
            }
        }
    }
}
