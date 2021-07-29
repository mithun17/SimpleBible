package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import dagger.hilt.android.AndroidEntryPoint

/**
 * Parent Fragment that hosts the Book selection ViewPager with 3 child fragments.
 * 1. Select Book, 2. Select Chapter, 3. Select Verse
 */
@AndroidEntryPoint
class SelectionFragment : BaseFragment() {

    companion object {
        const val kRequestKeyBookSelect = "requestKeyBookSelect"
    }

    private var _binding: FragmentSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSelectionBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.vpTabs
        // initialize view pager
        viewPager.adapter = BookSelectPageAdapter(this)
        // set the text for each tab in the view pager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        initFragmentResultListener(viewPager)
        return binding.root
    }

    // Collect the fragment result from each child fragment whenever a book, chapter or verse is selected
    private fun initFragmentResultListener(viewPager: ViewPager2) {
        childFragmentManager.setFragmentResultListener(kRequestKeyBookSelect, this) { _, bundle ->
            with(bundle) {
                when {
                    getBoolean(BooksFragment.kBookSelectState) -> {
                        // move to Chapter selection
                        viewPager.currentItem = CHAPTERS_PAGE_INDEX
                    }
                    getBoolean(ChapterSelectionFragment.kChapterSelectState) -> {
                        // move to Verse selection
                        viewPager.currentItem = VERSES_PAGE_INDEX
                    }
                    getBoolean(VerseSelectFragment.kVerseSelectState) -> {
                        // On verse selection, simply navigate up which lands us in the Verses fragment.
                        // Set the selected verse number in fragment result which will help with scrolling to the correct verse.
                        with(findNavController()) {
                            previousBackStackEntry?.savedStateHandle?.set(VerseSelectFragment.kVerseSelectedNumber, getInt(VerseSelectFragment.kVerseSelectedNumber, -1))
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    // set the title for the tabs in view pager
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
