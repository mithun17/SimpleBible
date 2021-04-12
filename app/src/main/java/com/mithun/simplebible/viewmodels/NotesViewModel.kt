package com.mithun.simplebible.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Note
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.repository.NotesRepository
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.data.repository.data.FullNote
import com.mithun.simplebible.utilities.KJV_BIBLE_ID
import com.mithun.simplebible.utilities.ResourcesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val versesRepository: VersesRepository,
    private val notesRepository: NotesRepository,
    private val resourcesUtil: ResourcesUtil
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _verses =
        MutableStateFlow<Resource<List<VerseEntity>>>(Resource.Loading(emptyList()))
    val verses: StateFlow<Resource<List<VerseEntity>>> = _verses

    private val _noteSaveState = MutableStateFlow<Resource<Boolean>>(Resource.Loading(false))
    val noteSaveState: StateFlow<Resource<Boolean>> = _noteSaveState

    private val _notes = MutableStateFlow<Resource<List<FullNote>>>(Resource.Loading(emptyList()))
    val notes: StateFlow<Resource<List<FullNote>>> = _notes

    private val versesExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _verses.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), emptyList()
        )
    }

    private val notesExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _noteSaveState.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), null
        )
    }

    fun fetchListOfVerses(verseIds: List<String>) {
        viewModelScope.launch(versesExceptionHandler) {
            _verses.value =
                Resource.Success(versesRepository.getVersesById(bibleId = KJV_BIBLE_ID, verseIds))
        }
    }

    fun saveNote(chapterId: String, verseIds: List<Int>, comment: String) {
        _noteSaveState.value = Resource.Loading(true)
        val note = Note(
            bibleId = KJV_BIBLE_ID,
            chapterId = chapterId,
            verses = verseIds,
            comment = comment
        )

        viewModelScope.launch(notesExceptionHandler) {
            notesRepository.saveNote(note)
            _noteSaveState.value = Resource.Success(true)
        }
    }

    fun fetchNotes() {
        viewModelScope.launch {
            _notes.value = Resource.Success(notesRepository.getNotes(KJV_BIBLE_ID))
        }
    }
}
