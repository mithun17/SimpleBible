package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.mithun.simplebible.data.repository.BibleRepository
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.databinding.FragmentChapterVersesBinding
import com.mithun.simplebible.ui.adapter.ChapterAdapter
import com.mithun.simplebible.ui.adapter.VersesAdapter
import kotlinx.coroutines.launch

class VersesFragment: Fragment() {
    private var _binding: FragmentChapterVersesBinding? = null
    private val binding get() = _binding!!

    val args : VersesFragmentArgs by navArgs()

    private val versesAdapter by lazy {
        VersesAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentChapterVersesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvVerses.adapter=versesAdapter

        val chapterId = args.chapterId
        val chapterName = args.chapterFullName

        binding.pbDialog.visibility=View.VISIBLE
        val versesRepository = VersesRepository.getInstance(requireContext())
        lifecycleScope.launch {
            val verses = versesRepository.getVerses("de4e12af7f28f599-01", chapterId)

            binding.tvBookName.text = chapterName
            versesAdapter.submitList(verses)
            binding.pbDialog.visibility=View.GONE
        }

    }

    private fun loadChaptersForBookId(bookId: String, chapterCount: Int) {
    }

}
