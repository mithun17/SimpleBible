package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Bible
import com.mithun.simplebible.data.database.model.Book
import com.mithun.simplebible.data.repository.BibleRepository
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.ResourcesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for HomeFragment.
 * Uses LiveData that is observed by the UI to update its state
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val resourcesUtil: ResourcesUtil,
    private val prefs: Prefs
) : ViewModel() {

    private val _books = MutableStateFlow<Resource<List<Book>>>(Resource.Loading(emptyList()))
    val books: StateFlow<Resource<List<Book>>> = _books

    private val _bible = MutableStateFlow<Resource<Bible>>(Resource.Loading())
    val bible: StateFlow<Resource<Bible>> = _bible

    private val booksExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            _books.value = Resource.Error(
                throwable.message ?: resourcesUtil.getString(R.string.errorGenericString),
                emptyList()
            )
        }

    private val biblesExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            _bible.value = Resource.Error(
                throwable.message ?: resourcesUtil.getString(R.string.errorGenericString)
            )
        }

    init {
        getSelectedBible()
        getBooks(prefs.selectedBibleVersionId)
    }

    private fun getSelectedBible() {
        viewModelScope.launch(biblesExceptionHandler) {
            val bibleBooks = bibleRepository.getPresetBibles()
            _bible.value = Resource.Success(bibleBooks.first { it.id == prefs.selectedBibleVersionId })
        }
    }

    fun setSelectedBible(bibleVersionId: String) {
        prefs.selectedBibleVersionId = bibleVersionId
        getSelectedBible()
        getBooks(bibleVersionId)
    }

    private fun getBooks(bibleId: String) {
        _books.value = Resource.Loading(null)
        viewModelScope.launch(booksExceptionHandler) {
            val bibleBooks = bibleRepository.getBooks(bibleId)
            _books.value = Resource.Success(bibleBooks)
        }
    }
}
