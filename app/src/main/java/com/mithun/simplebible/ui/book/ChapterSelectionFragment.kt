package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.mithun.simplebible.databinding.FragmentChapterSelectBinding
import com.mithun.simplebible.ui.adapter.ChapterAdapter
import com.mithun.simplebible.ui.adapter.ChapterItem

class ChapterSelectionFragment: Fragment() {

    private var _binding: FragmentChapterSelectBinding? = null
    private val binding get() = _binding!!

    val args : ChapterSelectionFragmentArgs by navArgs()

    private val chapterSelectionAdapter by lazy {
        ChapterAdapter()
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
        val bookName = args.bookName
        val bookId = args.bookId
        val chapterCount = args.chapterCount

        binding.tvChaptersTitle.text = bookName
        binding.rvChapters.adapter = chapterSelectionAdapter
        loadChaptersForBookId(bookId, bookName, chapterCount)
    }

    private fun loadChaptersForBookId(bookId: String, bookName: String, chapterCount: Int) {

        val chapterList = MutableList<ChapterItem>(chapterCount) { ChapterItem(bookId, bookName,it+1) }
        chapterSelectionAdapter.submitList(chapterList)
    }
}
