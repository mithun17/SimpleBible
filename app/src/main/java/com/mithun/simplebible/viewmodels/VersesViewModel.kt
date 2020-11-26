package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.model.Verse
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.utilities.ResourcesUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class VersesViewModel(
    private val versesRepository: VersesRepository,
    private val resourcesUtil: ResourcesUtil
) : ViewModel() {


    // backing property so that the activity or fragment has access to only the immutable 'verses' object.
    // this prevents any setting of data directly on a variable in viewModel.
    private val _verses = MutableStateFlow<Resource<List<Verse>>>(Resource.Loading(emptyList()))
    val verses: StateFlow<Resource<List<Verse>>> = _verses

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _verses.value = Resource.Error(throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), emptyList())
    }

    fun getVerses(bibleId: String, chapterId: String) {
        _verses.value = Resource.Loading(null)
        viewModelScope.launch(coroutineExceptionHandler) {
            _verses.value = Resource.Success(versesRepository.getVerses(bibleId, chapterId))
        }
    }
}
