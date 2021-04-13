package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.databinding.FragmentHomeBinding
import com.mithun.simplebible.ui.adapter.BookAdapter
import com.mithun.simplebible.utilities.KJV_BIBLE_ID
import com.mithun.simplebible.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val bookAdapter by lazy {
        BookAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvBooks.adapter = bookAdapter

        initViewModelAndObservers()
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

        homeViewModel.getBooks(KJV_BIBLE_ID)
        homeViewModel.getBibles()
    }
}
