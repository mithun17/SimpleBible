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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val versesRepository: VersesRepository
) : ViewModel() {

    private val _lvBookmarks = MutableStateFlow<Resource<List<BookmarkItem>>>(Resource.Loading(emptyList()))
    val lvBookmarks: StateFlow<Resource<List<BookmarkItem>>> = _lvBookmarks

    fun getAllBookmarks() {
        _lvBookmarks.value = Resource.Loading(null)
        viewModelScope.launch {

            val listOfBookmarkItems = mutableListOf<BookmarkItem>()
            val bookmarks = bookmarkRepository.getAllBookmarks()

            bookmarks.forEach { bookmark ->
                val verse = versesRepository.getVerseById(bookmark.bibleId, bookmark.verseId)
                listOfBookmarkItems.add(
                    BookmarkItem(
                        id = bookmark.id,
                        bibleId = bookmark.bibleId,
                        chapterId = bookmark.chapterId,
                        verseId = bookmark.verseId,
                        verse = verse.text,
                        reference = verse.reference
                    )
                )
            }
            _lvBookmarks.value = Resource.Success(listOfBookmarkItems)
        }
    }
}
