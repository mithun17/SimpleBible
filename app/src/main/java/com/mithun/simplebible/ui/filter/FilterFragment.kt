package com.mithun.simplebible.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentFilterBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.ui.adapter.BibleFilterAdapter
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.viewmodels.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterFragment : BaseFragment() {

    companion object {
        const val kSelectedBible = "selectedBibleId"
    }

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private val filterViewModel: FilterViewModel by viewModels()

    private val filterAdapter by lazy {
        BibleFilterAdapter { (id, name) ->
            selectBible(id, name)
        }
    }

    private var supportActionBar: ActionBar? = null

    private val prefs by lazy {
        Prefs(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        supportActionBar?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvBibleFilter.adapter = filterAdapter
        initSubscribeAndObserve()
    }

    private fun initSubscribeAndObserve() {

        lifecycleScope.launch {
            filterViewModel.bibles.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val data = resource.data
                        filterAdapter.selectedId = prefs.selectedBibleVersionId
                        filterAdapter.submitList(data)
                    }
                    is Resource.Loading -> {
                    }
                    is Resource.Error -> {
                    }
                }
            }
        }
    }

    private fun selectBible(bibleVersionId: String, bibleVersionName: String) {
        prefs.selectedBibleVersionId = bibleVersionId
        prefs.selectedBibleVersionName = bibleVersionName
        findNavController().previousBackStackEntry?.savedStateHandle?.set(kSelectedBible, bibleVersionId)
        findNavController().popBackStack()
    }
}
