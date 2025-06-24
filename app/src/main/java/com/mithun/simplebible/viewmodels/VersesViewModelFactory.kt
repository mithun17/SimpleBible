package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.utilities.ResourcesUtil

class VersesViewModelFactory(
    private val versesRepository: VersesRepository,
    private val resourcesUtil: ResourcesUtil
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VersesViewModel(versesRepository, resourcesUtil) as T
    }
}
