package com.mithun.simplebible.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.BookmarkRepository
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.databinding.FragmentBookmarksBinding
import com.mithun.simplebible.ui.adapter.BookmarkAdapter
import com.mithun.simplebible.utilities.KJV_BIBLE_ID
import com.mithun.simplebible.viewmodels.BookmarkViewModel
import com.mithun.simplebible.viewmodels.BookmarkViewModelFactory

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val bookmarkViewModel: BookmarkViewModel by viewModels {
        BookmarkViewModelFactory(BookmarkRepository.getInstance(requireContext()), VersesRepository.getInstance(requireContext()))
    }

    private var bookmarkAdapter = BookmarkAdapter(emptyList())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvBookmarks.adapter = bookmarkAdapter
        binding.rvBookmarks.layoutManager = LinearLayoutManager(requireContext())
        initViewModelAndObservers()
    }

    private fun initViewModelAndObservers() {
        bookmarkViewModel.lvBookmarks.observe(viewLifecycleOwner, { resource ->
            resource.message?.let { errorMessage ->
                // error
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }

            resource.data?.let { bookmarks ->
                bookmarkAdapter = BookmarkAdapter(bookmarks)
                bookmarkAdapter.notifyDataSetChanged()
                binding.pbHome.visibility = View.GONE
            } ?: run {
                binding.pbHome.visibility = View.VISIBLE
            }
        })

        bookmarkViewModel.getAllBookmarks(KJV_BIBLE_ID)
    }
}
