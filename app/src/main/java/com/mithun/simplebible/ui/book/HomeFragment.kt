package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.api.ASV_BIBLE_ID
import com.mithun.simplebible.data.repository.BibleRepository
import com.mithun.simplebible.databinding.FragmentHomeBinding
import com.mithun.simplebible.ui.adapter.BookAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

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
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
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
        init()
    }

    private fun init() {

        val bibleRepository = BibleRepository.getInstance(requireContext())
        binding.pbHome.visibility = View.VISIBLE
        lifecycleScope.launch {

            val books = bibleRepository.getBooks(ASV_BIBLE_ID)
            bookAdapter.submitList(books)
            binding.pbHome.visibility = View.GONE
        }
    }
}
