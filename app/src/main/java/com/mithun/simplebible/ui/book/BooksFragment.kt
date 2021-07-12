package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.data.model.Book
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentBooksBinding
import com.mithun.simplebible.ui.adapter.BookAdapter
import com.mithun.simplebible.viewmodels.SelectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BooksFragment : Fragment() {

    companion object {
        // key set to track the status of book selection in view pager
        const val kBookSelectState = "bookSelectState"
    }

    private val selectionViewModel: SelectionViewModel by viewModels({ requireParentFragment() })

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private val bookAdapter by lazy {
        BookAdapter { bookName, bookId, _ ->
            selectionViewModel.setSelectedBookId(bookId)
            selectionViewModel.setSelectedBookName(bookName)
            setBookSelectionResult()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        binding.rvBooks.adapter = bookAdapter
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectionViewModel.books.collect { resource ->
                    when (resource) {
                        is Resource.Success -> resource.data?.let { loadBooks(it) }
                        is Resource.Error -> resource.message?.let { showError(it) }
                        is Resource.Loading -> {
                            // Loading
                            binding.pbHome.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun loadBooks(listOfBooks: List<Book>) {
        // success or error data
        bookAdapter.submitList(listOfBooks)
        bookAdapter.setSelectedBook(selectionViewModel.selectedBookId.value)
        binding.pbHome.visibility = View.GONE
    }

    private fun showError(errorMessage: String) {
        // error
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        binding.pbHome.visibility = View.GONE
    }

    // set book selection in fragment bundle
    private fun setBookSelectionResult() {
        setFragmentResult(SelectionFragment.kRequestKeyBookSelectFragment, bundleOf(kBookSelectState to true))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
