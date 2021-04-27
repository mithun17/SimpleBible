package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mithun.simplebible.databinding.FragmentChapterSelectBinding
import com.mithun.simplebible.ui.adapter.ChapterAdapter
import com.mithun.simplebible.ui.adapter.ChapterItem
import com.mithun.simplebible.viewmodels.SelectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChapterSelectionFragment : Fragment() {

    companion object {
        const val kChapterSelectState = "chapterSelectState"
    }

    private var _binding: FragmentChapterSelectBinding? = null
    private val binding get() = _binding!!

    private val selectionViewModel: SelectionViewModel by viewModels({ requireParentFragment() })

    private val chapterSelectionAdapter by lazy {
        ChapterAdapter { chapterId ->
            selectionViewModel.setSelectedChapterId(chapterId)
            setResult()
        }
    }

    private fun setResult() {
        setFragmentResult(SelectionFragment.kRequestKeyBookSelectFragment, bundleOf(kChapterSelectState to true))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChapterSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bookName = selectionViewModel.selectedBookName
        val bookId = selectionViewModel.selectedBookId
        val chapterCount = selectionViewModel.chapterCount

        binding.rvChapters.adapter = chapterSelectionAdapter

        lifecycleScope.launchWhenCreated {
            combine(bookName, bookId, chapterCount) { bookName, bookId, chapterCount ->
                loadChaptersForBookId(bookId, bookName, chapterCount)
            }.collect()
        }

        lifecycleScope.launch {
            selectionViewModel.chapterNumber.collect { chapterNumber ->
                chapterSelectionAdapter.setSelectedChapterNumber(chapterNumber)
            }
        }
    }

    private fun loadChaptersForBookId(bookId: String, bookName: String, chapterCount: Int) {
        val chapterList = MutableList<ChapterItem>(chapterCount) { ChapterItem(bookId, bookName, it + 1) }
        chapterSelectionAdapter.submitList(chapterList)
    }
}
