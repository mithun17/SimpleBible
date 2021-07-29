package com.mithun.simplebible.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mithun.simplebible.ui.book.BooksFragment
import com.mithun.simplebible.ui.book.ChapterSelectionFragment
import com.mithun.simplebible.ui.book.VerseSelectFragment

const val BOOKS_PAGE_INDEX = 0
const val CHAPTERS_PAGE_INDEX = 1
const val VERSES_PAGE_INDEX = 2

/**
 * Page Adapter for Book selection View Pager
 */
class BookSelectPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    // mapping of viewPager page indexes to the corresponding Fragments
    private val tabFragments: Map<Int, Fragment> = mapOf(
        BOOKS_PAGE_INDEX to BooksFragment(),
        CHAPTERS_PAGE_INDEX to ChapterSelectionFragment(),
        VERSES_PAGE_INDEX to VerseSelectFragment()
    )

    override fun getItemCount(): Int = tabFragments.size

    override fun createFragment(position: Int): Fragment {
        return tabFragments[position] ?: throw IndexOutOfBoundsException()
    }
}
