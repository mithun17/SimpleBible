package com.mithun.simplebible.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.databinding.FragmentBookmarksBinding
import com.mithun.simplebible.ui.adapter.BookmarkAdapter
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.viewmodels.BookmarkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    private val bookmarkViewModel: BookmarkViewModel by viewModels()

    private val bookmarkAdapter by lazy {
        BookmarkAdapter()
    }

    private val prefs by lazy {
        Prefs(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvBookmarks.adapter = bookmarkAdapter
        initViewModelAndObservers()
    }

    private fun initViewModelAndObservers() {
        bookmarkViewModel.lvBookmarks.observe(
            viewLifecycleOwner,
            { resource ->
                resource.message?.let { errorMessage ->
                    // error
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                }

                resource.data?.let { bookmarks ->
                    bookmarkAdapter.setBookmarks(bookmarks)
                    bookmarkAdapter.notifyDataSetChanged()
                    binding.pbHome.visibility = View.GONE
                } ?: run {
                    binding.pbHome.visibility = View.VISIBLE
                }
            }
        )

        bookmarkViewModel.getAllBookmarks(prefs.selectedBibleId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
