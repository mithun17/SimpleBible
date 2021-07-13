package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mithun.simplebible.R
import com.mithun.simplebible.databinding.FragmentSelectionBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.ui.adapter.BOOKS_PAGE_INDEX
import com.mithun.simplebible.ui.adapter.BookSelectPageAdapter
import com.mithun.simplebible.ui.adapter.CHAPTERS_PAGE_INDEX
import com.mithun.simplebible.ui.adapter.VERSES_PAGE_INDEX
import com.mithun.simplebible.viewmodels.SelectionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectionFragment : BaseFragment() {

    companion object {
        const val kRequestKeyBookSelectFragment = "requestKeyBookSelectFragment"
    }

    private val selectionViewModel: SelectionViewModel by viewModels()

    private var _binding: FragmentSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayout
        val viewPager = binding.vpTabs
        selectionViewModel.chapterCount

        viewPager.adapter = BookSelectPageAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        initFragmentResultListener(viewPager)
    }

    private fun initFragmentResultListener(viewPager: ViewPager2) {
        childFragmentManager.setFragmentResultListener(kRequestKeyBookSelectFragment, this) { _, bundle ->
            with(bundle) {
                when {
                    getBoolean(BooksFragment.kBookSelectState) -> {
                        viewPager.currentItem = CHAPTERS_PAGE_INDEX
                    }
                    getBoolean(ChapterSelectionFragment.kChapterSelectState) -> {
                        viewPager.currentItem = VERSES_PAGE_INDEX
                    }
                    getBoolean(VerseSelectFragment.kVerseSelectState) -> {
                        with(findNavController()) {
                            previousBackStackEntry?.savedStateHandle?.set(VerseSelectFragment.kVerseSelectedNumber, getInt(VerseSelectFragment.kVerseSelectedNumber, -1))
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            BOOKS_PAGE_INDEX -> getString(R.string.title_tab_books)
            CHAPTERS_PAGE_INDEX -> getString(R.string.title_tab_chapters)
            VERSES_PAGE_INDEX -> getString(R.string.title_tab_verses)
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
