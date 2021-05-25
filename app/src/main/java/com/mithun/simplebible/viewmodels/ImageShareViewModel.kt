package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.VerseEntity
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
class ImageShareViewModel @Inject constructor(
    private val versesRepository: VersesRepository,
    private val resourcesUtil: ResourcesUtil
) : ViewModel() {

    private val _verse = MutableStateFlow<Resource<VerseEntity>>(Resource.Loading())
    val verse: StateFlow<Resource<VerseEntity>> = _verse

    private val versesExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _verse.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString)
        )
    }

    fun fetchVerse(verseId: String, bibleId: String) {
        viewModelScope.launch(versesExceptionHandler) {
            _verse.value = Resource.Success(versesRepository.getVerseById(bibleId, verseId))
        }
    }
}
