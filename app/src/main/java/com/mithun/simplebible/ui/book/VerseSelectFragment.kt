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
import com.mithun.simplebible.databinding.FragmentVerseSelectBinding
import com.mithun.simplebible.ui.adapter.VerseSelectAdapter
import com.mithun.simplebible.ui.adapter.VersesItem
import com.mithun.simplebible.viewmodels.SelectionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

class VerseSelectFragment : Fragment() {

    companion object {
        const val kVerseSelectState = "verseSelectState"
    }

    private var _binding: FragmentVerseSelectBinding? = null
    private val binding get() = _binding!!

    private val selectionViewModel: SelectionViewModel by viewModels({ requireParentFragment() })

    private val versesSelectionAdapter by lazy {
        VerseSelectAdapter { verseNumber ->
            selectionViewModel.setSelectedVerseNumber(verseNumber)
            setResult()
        }
    }

    private fun setResult() {
        setFragmentResult(SelectionFragment.kRequestKeyBookSelectFragment, bundleOf(kVerseSelectState to true))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentVerseSelectBinding.inflate(inflater, container, false)
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
        val versesCount = selectionViewModel.versesCount
        binding.rvChapters.adapter = versesSelectionAdapter

        lifecycleScope.launchWhenCreated {
            combine(bookName, bookId, versesCount) { bookName, bookId, versesCount ->
                loadVersesForChapterId(bookId, bookName, versesCount)
            }.collect()
        }
    }

    private fun loadVersesForChapterId(bookId: String, bookName: String, verseCount: Int) {
        val chapterList = MutableList(verseCount) { VersesItem(bookId, bookName, it + 1) }
        versesSelectionAdapter.submitList(chapterList)
    }
}
