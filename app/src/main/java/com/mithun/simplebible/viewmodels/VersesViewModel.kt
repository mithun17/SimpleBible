package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.data.model.Verse
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.utilities.ResourcesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VersesViewModel @Inject constructor(
    private val versesRepository: VersesRepository,
    private val resourcesUtil: ResourcesUtil
) : ViewModel() {

    // backing property so that the activity or fragment has access to only the immutable 'verses' object.
    // this prevents any setting of data directly on a variable in viewModel.
    private val _verses = MutableStateFlow<Resource<List<Verse>>>(Resource.Loading(emptyList()))
    val verses: StateFlow<Resource<List<Verse>>> = _verses

    private val _bookmarkSaveState = MutableStateFlow<Resource<Boolean>>(Resource.Loading(null))
    val bookmarkSaveState: StateFlow<Resource<Boolean>> = _bookmarkSaveState

    private val versesExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _verses.value = Resource.Error(throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), emptyList())
    }

    private val bookmarkExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _bookmarkSaveState.value = Resource.Error(throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), false)
    }

    fun getVerses(bibleId: String, chapterId: String) {
        _verses.value = Resource.Loading(null)
        viewModelScope.launch(versesExceptionHandler) {
            _verses.value = Resource.Success(versesRepository.getVerses(bibleId, chapterId))
        }
    }

    fun saveBookmark(verseId: String, bookmark: Bookmark) {

        _bookmarkSaveState.value = Resource.Loading(null)
        viewModelScope.launch(bookmarkExceptionHandler) {
            _bookmarkSaveState.value = Resource.Success(versesRepository.saveBookmark(verseId, bookmark))
        }
    }
}
