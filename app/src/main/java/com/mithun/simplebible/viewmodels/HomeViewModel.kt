package com.mithun.simplebible.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.model.Book
import com.mithun.simplebible.data.repository.BibleRepository
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.utilities.ResourcesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for HomeFragment.
 * Uses LiveData that is observed by the UI to update its state
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val resourcesUtil: ResourcesUtil
) : ViewModel() {

    val books = MutableLiveData<Resource<List<Book>>>().apply {
        Resource.Loading(null)
    }

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            books.value = Resource.Error(
                throwable.message ?: resourcesUtil.getString(R.string.errorGenericString),
                emptyList()
            )
        }

    fun getBooks(bibleId: String) {
        books.value = Resource.Loading(null)
        viewModelScope.launch(coroutineExceptionHandler) {
            val bibleBooks = bibleRepository.getBooks(bibleId)
            books.value = Resource.Success(bibleBooks)
        }
    }
}
