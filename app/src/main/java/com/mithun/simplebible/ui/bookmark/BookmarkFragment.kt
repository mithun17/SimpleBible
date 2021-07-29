package com.mithun.simplebible.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentBookmarksBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.ui.adapter.BookmarkAdapter
import com.mithun.simplebible.ui.adapter.BookmarkItem
import com.mithun.simplebible.utilities.CommonUtils
import com.mithun.simplebible.utilities.ExtensionUtils.toCopyText
import com.mithun.simplebible.utilities.ResourcesUtil
import com.mithun.simplebible.utilities.gone
import com.mithun.simplebible.utilities.visible
import com.mithun.simplebible.viewmodels.BookmarkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BookmarkFragment : BaseFragment() {

    @Inject
    lateinit var resourcesUtil: ResourcesUtil

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val bookmarkViewModel: BookmarkViewModel by viewModels()

    private val bookmarkAdapter by lazy {
        BookmarkAdapter { view, bookmark -> showMoreOptions(view, bookmark) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        binding.rvBookmarks.adapter = bookmarkAdapter
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookmarkViewModel.bookmarks.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> binding.pbLoading.visible
                        is Resource.Success -> resource.data?.let { showBookmarks(it) }
                        is Resource.Error -> resource.message?.let { showError(it) }
                        is Resource.Empty -> {
                            // do nothing
                        }
                    }
                }
            }
        }
    }

    private fun showError(errorMessage: String) {
        // error
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        binding.pbLoading.gone
    }

    private fun showBookmarks(bookmarks: List<BookmarkItem>) {
        bookmarkAdapter.setBookmarks(bookmarks)
        bookmarkAdapter.notifyDataSetChanged()
        binding.pbLoading.gone
    }

    // menu options for bookmarks
    private fun showMoreOptions(card: View, bookmark: BookmarkItem) {
        val bookmarksMenu = PopupMenu(requireContext(), card)
        bookmarksMenu.menuInflater.inflate(R.menu.menu_bookmarks_more_options, bookmarksMenu.menu)
        bookmarksMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.shareBookmark -> {
                    // trigger share intent
                    CommonUtils.showTextShareIntent(requireContext(), bookmark.toCopyText(resourcesUtil))
                }
                R.id.copyBookmark -> {
                    // copy to clipboard
                    CommonUtils.copyToClipboard(requireContext(), bookmark.toCopyText(resourcesUtil))
                }
                R.id.deleteBookmark -> {
                    // delete bookmark
                    bookmarkViewModel.deleteBookmark(bookmark)
                }
            }
            true
        }
        bookmarksMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
