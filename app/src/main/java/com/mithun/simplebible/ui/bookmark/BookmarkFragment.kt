package com.mithun.simplebible.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentBookmarksBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.ui.adapter.BookmarkAdapter
import com.mithun.simplebible.ui.adapter.BookmarkItem
import com.mithun.simplebible.utilities.CommonUtils
import com.mithun.simplebible.utilities.ExtensionUtils.toCopyText
import com.mithun.simplebible.utilities.Prefs
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

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val bookmarkViewModel: BookmarkViewModel by viewModels()

    private val bookmarkAdapter by lazy {
        BookmarkAdapter { view, bookmark ->
            showMoreOptions(view, bookmark)
        }
    }

    @Inject
    lateinit var resourcesUtil: ResourcesUtil

    private val prefs by lazy {
        Prefs(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvBookmarks.adapter = bookmarkAdapter
        initViewModelAndObservers()
    }

    private fun initViewModelAndObservers() {

        lifecycleScope.launch {
            bookmarkViewModel.bookmarks.collect { resource ->

                when (resource) {
                    is Resource.Loading -> {
                        binding.pbLoading.visible
                    }
                    is Resource.Success -> {
                        resource.data?.let { bookmarks ->
                            bookmarkAdapter.setBookmarks(bookmarks)
                            bookmarkAdapter.notifyDataSetChanged()
                            binding.pbLoading.gone
                        }
                    }
                    is Resource.Error -> {
                        resource.message?.let { errorMessage ->
                            // error
                            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                            binding.pbLoading.gone
                        }
                    }
                }
            }
        }

        bookmarkViewModel.getAllBookmarks()
    }

    private fun showMoreOptions(card: View, bookmark: BookmarkItem) {
        val bookmarksMenu = PopupMenu(requireContext(), card)
        bookmarksMenu.menuInflater.inflate(R.menu.menu_bookmarks_more_options, bookmarksMenu.menu)
        bookmarksMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.shareBookmark -> {
                    // trigger share intent
                    CommonUtils.showShareIntent(requireContext(), bookmark.toCopyText(resourcesUtil))
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
