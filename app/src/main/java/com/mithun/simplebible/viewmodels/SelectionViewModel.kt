package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Book
import com.mithun.simplebible.data.repository.BibleRepository
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.ui.model.Verse
import com.mithun.simplebible.utilities.ExtensionUtils.toVerses
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.ResourcesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val versesRepository: VersesRepository,
    private val resourcesUtil: ResourcesUtil,
    private val prefs: Prefs
) : ViewModel() {

    // verse to be filtered when displaying in the UI
    private val filterVerse = "intro"

    val chapterNumber: MutableStateFlow<Int> = MutableStateFlow(
        prefs.lastReadChapter.split(".").last().toInt()
    )
    var verseNumber = 1

    private val _selectedBookName: MutableStateFlow<String> = MutableStateFlow("")
    val selectedBookName: StateFlow<String> = _selectedBookName

    private val _chapterCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val chapterCount: StateFlow<Int> = _chapterCount

    private val _versesCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val versesCount: StateFlow<Int> = _versesCount

    private val _selectedBookId: MutableStateFlow<String> = MutableStateFlow(
        prefs.lastReadChapter.split(".").first()
    )
    val selectedBookId: StateFlow<String> = _selectedBookId

    private val _selectedChapterId: MutableStateFlow<String> = MutableStateFlow(
        prefs.lastReadChapter
    )
    private val selectedChapterId: StateFlow<String> = _selectedChapterId

    // books returned from repository
    private val _books = MutableStateFlow<Resource<List<Book>>>(Resource.Loading(emptyList()))
    val books: StateFlow<Resource<List<Book>>> = _books

    // verses for a selected chapter
    private val _verses = MutableStateFlow<Resource<List<Verse>>>(Resource.Loading(emptyList()))
    val verses: StateFlow<Resource<List<Verse>>> = _verses

    // error handling the books fetch call
    private val booksExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _books.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), emptyList()
        )
    }

    // error handling verses fetch call
    private val versesExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _verses.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), emptyList()
        )
    }

    init {
        // fetch the verses for the selected chapter
        viewModelScope.launch {
            _selectedChapterId.collect { chapterId ->
                fetchVerses(prefs.selectedBibleVersionId, chapterId)
            }
        }

        // get the list of books for the selected bible version.
        // selected bible version is stored in preference.
        viewModelScope.launch {
            _selectedBookId.collect { bookId ->
                fetchBooks(prefs.selectedBibleVersionId)
            }
        }
    }

    // set Book eg: GEN, EXO, MAT
    fun setSelectedBookId(bookId: String) {
        _selectedBookId.value = bookId
        setSelectedChapterId("$bookId.1") // default to 1st chapter when a book is selected
    }

    // set Book name eg: Genesis, Romans
    fun setSelectedBookName(bookName: String) {
        _selectedBookName.value = bookName
    }

    // set ChapterId eg: GEN.1, MAT.5
    fun setSelectedChapterId(chapterId: String) {
        _selectedChapterId.value = chapterId
        chapterNumber.value = chapterId.split(".").last().toInt()
    }

    // set the number of the verse
    fun setSelectedVerseNumber(verseNumber: Int) {
        this.verseNumber = verseNumber
        prefs.lastReadChapter = selectedChapterId.value
    }

    private fun fetchBooks(bibleId: String) {
        _books.value = Resource.Loading(null)
        viewModelScope.launch(booksExceptionHandler) {
            val bibleBooks = bibleRepository.getBooks(bibleId)
            _chapterCount.value = bibleBooks.first { it.id == selectedBookId.value }.chapters.filter { it.number != filterVerse }
                .count()
            _books.value = Resource.Success(bibleBooks)
        }
    }

    private fun fetchVerses(bibleId: String, chapterId: String) {
        _verses.value = Resource.Loading(null)
        viewModelScope.launch(versesExceptionHandler) {
            with(versesRepository.getAllVersesForChapter(bibleId, chapterId)) {
                _verses.value = Resource.Success(this.toVerses())
                _versesCount.value = this.count()
            }
        }
    }
}
