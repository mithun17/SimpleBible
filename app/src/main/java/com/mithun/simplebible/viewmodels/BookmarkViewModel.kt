package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.data.repository.BookmarkRepository
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.ui.adapter.BookmarkItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val versesRepository: VersesRepository
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<Resource<List<BookmarkItem>>>(Resource.Loading(emptyList()))
    val bookmarks: StateFlow<Resource<List<BookmarkItem>>> = _bookmarks

    init {
        getAllBookmarks()
    }

    fun getAllBookmarks() {
        _bookmarks.value = Resource.Loading(null)
        viewModelScope.launch {

            bookmarkRepository.getAllBookmarks().collect { bookmarks ->
                _bookmarks.value = Resource.Success(
                    bookmarks.map { bookmark ->
                        val verse = versesRepository.getVerseById(bookmark.bibleId, bookmark.verseId)
                        BookmarkItem(
                            id = bookmark.id,
                            bibleId = bookmark.bibleId,
                            chapterId = bookmark.chapterId,
                            verseId = bookmark.verseId,
                            verse = verse.text,
                            reference = verse.reference
                        )
                    }.toList()
                )
            }
        }
    }

    fun deleteBookmark(bookmarkItem: BookmarkItem) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(bookmarkItem)
        }
    }
}
