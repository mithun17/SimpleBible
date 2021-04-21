package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentBooksBinding
import com.mithun.simplebible.ui.adapter.BookAdapter
import com.mithun.simplebible.ui.filter.FilterFragment
import com.mithun.simplebible.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BooksFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private val bookAdapter by lazy {
        BookAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        binding.rvBooks.adapter = bookAdapter

        initUI()
        initViewModelAndObservers()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        binding.collapsibleToolbar.ctbAppBar.title = getString(R.string.booksFragmentTitle)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.navigation_filter)
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(FilterFragment.kSelectedBible)
            ?.observe(viewLifecycleOwner) { version ->
                homeViewModel.setSelectedBible(version)
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>(FilterFragment.kSelectedBible)
            }
    }

    private fun initViewModelAndObservers() {
        lifecycleScope.launchWhenCreated {
            homeViewModel.books.collect { resource ->
                resource.message?.let { errorMessage ->
                    // error
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                }
                resource.data?.let { listOfBooks ->
                    // success or error data
                    bookAdapter.submitList(listOfBooks)
                    binding.pbHome.visibility = View.GONE
                } ?: run {
                    // Loading message
                    binding.pbHome.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            homeViewModel.bible.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        binding.fab.text = resource.data?.abbreviationLocal
                    }
                    is Resource.Error -> {
                        binding.fab.text = ""
                    }
                }
            }
        }
    }
}
