package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.databinding.FragmentChapterVersesBinding
import com.mithun.simplebible.ui.adapter.VersesAdapter
import com.mithun.simplebible.utilities.KJV_BIBLE_ID
import com.mithun.simplebible.utilities.ResourcesUtil
import com.mithun.simplebible.viewmodels.VersesViewModel
import com.mithun.simplebible.viewmodels.VersesViewModelFactory
import kotlinx.coroutines.flow.collect

class VersesFragment : Fragment() {


    private val resourcesUtil by lazy {
        ResourcesUtil(requireContext())
    }

    private val versesRepository by lazy {
        VersesRepository.getInstance(requireContext())
    }

    private val versesViewModel: VersesViewModel by viewModels {
        VersesViewModelFactory(versesRepository, resourcesUtil)
    }

    private var _binding: FragmentChapterVersesBinding? = null
    private val binding get() = _binding!!

    val args: VersesFragmentArgs by navArgs()

    private val versesAdapter by lazy {
        VersesAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentChapterVersesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvVerses.adapter = versesAdapter

        val chapterId = args.chapterId
        val chapterName = args.chapterFullName

        binding.ctbAppBar.title = chapterName
        initViewModelAndSetCollectors()
        versesViewModel.getVerses(KJV_BIBLE_ID, chapterId)
    }

    private fun initViewModelAndSetCollectors() {

        lifecycleScope.launchWhenCreated {
            versesViewModel.verses.collect { resource ->
                resource.message?.let { errorMessage ->
                    // error
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                }
                resource.data?.let { listOfVerses ->
                    // success or error data
                    versesAdapter.submitList(listOfVerses)
                    binding.pbDialog.visibility = View.GONE
                } ?: run {
                    // Loading message
                    binding.pbDialog.visibility = View.VISIBLE
                }
            }
        }

    }
}
