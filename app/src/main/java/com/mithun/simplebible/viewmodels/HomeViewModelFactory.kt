package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mithun.simplebible.data.repository.BibleRepository
import com.mithun.simplebible.utilities.ResourcesUtil

class HomeViewModelFactory(
    private val bibleRepository: BibleRepository,
    private val resourcesUtil: ResourcesUtil
) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(bibleRepository, resourcesUtil) as T
    }

}
