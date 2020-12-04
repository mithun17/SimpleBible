package com.mithun.simplebible.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.repository.BookmarkRepository
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import kotlinx.coroutines.launch

class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val versesRepository: VersesRepository
) : ViewModel() {

    private val _lvBookmarks = MutableLiveData<Resource<List<Bookmark>>>().apply {
        Resource.Loading(null)
    }
    val lvBookmarks: LiveData<Resource<List<Bookmark>>> = _lvBookmarks

    private val _lvBookmarkVerse = MutableLiveData<Resource<VerseEntity>>().apply {
        Resource.Loading(null)
    }
    val lvBookmarkVerse: LiveData<Resource<VerseEntity>> = _lvBookmarkVerse


    fun getAllBookmarks(bibleId: String) {
        _lvBookmarks.value = Resource.Loading(null)
        viewModelScope.launch {
            _lvBookmarks.value = Resource.Success(bookmarkRepository.getAllBookmarks())
        }
    }

    fun getBookmarkVerse(bibleId: String, verseId: String) {
        viewModelScope.launch {
            _lvBookmarkVerse.value = Resource.Success(versesRepository.getVerseById(bibleId, verseId))
        }
    }
}
