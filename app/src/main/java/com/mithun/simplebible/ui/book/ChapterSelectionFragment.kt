package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mithun.simplebible.databinding.FragmentChapterSelectBinding
import com.mithun.simplebible.ui.adapter.ChapterItem
import com.mithun.simplebible.ui.adapter.ChapterSelectAdapter
import com.mithun.simplebible.viewmodels.SelectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChapterSelectionFragment : Fragment() {

    companion object {
        // key set to track the status of chapter selection in view pager
        const val kChapterSelectState = "chapterSelectState"
    }

    private var _binding: FragmentChapterSelectBinding? = null
    private val binding get() = _binding!!

    private val selectionViewModel: SelectionViewModel by viewModels({ requireParentFragment() })

    private val chapterSelectionAdapter by lazy {
        ChapterSelectAdapter { chapterId ->
            selectionViewModel.setSelectedChapterId(chapterId)
            setChapterSelectionResult()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChapterSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvChapters.adapter = chapterSelectionAdapter
        subscribeUi()
    }

    private fun subscribeUi() {
        val bookName = selectionViewModel.selectedBookName
        val bookId = selectionViewModel.selectedBookId
        val chapterCount = selectionViewModel.chapterCount

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    combine(bookName, bookId, chapterCount) { bookName, bookId, chapterCount ->
                        loadChaptersForBookId(bookId, bookName, chapterCount)
                    }.collect()
                }

                launch {
                    selectionViewModel.chapterNumber.collect { chapterNumber ->
                        chapterSelectionAdapter.setSelectedChapterNumber(chapterNumber)
                    }
                }
            }
        }
    }

    private fun loadChaptersForBookId(bookId: String, bookName: String, chapterCount: Int) {
        val chapterList = MutableList(chapterCount) { ChapterItem(bookId, bookName, it + 1) }
        chapterSelectionAdapter.submitList(chapterList)
    }

    // set flag that chapter is selected
    private fun setChapterSelectionResult() {
        setFragmentResult(SelectionFragment.kRequestKeyBookSelect, bundleOf(kChapterSelectState to true))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
