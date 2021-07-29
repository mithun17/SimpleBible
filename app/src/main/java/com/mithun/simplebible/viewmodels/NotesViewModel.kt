package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Note
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.repository.NotesRepository
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.data.repository.data.FullNote
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
class NotesViewModel @Inject constructor(
    private val versesRepository: VersesRepository,
    private val notesRepository: NotesRepository,
    private val resourcesUtil: ResourcesUtil,
    private val prefs: Prefs
) : ViewModel() {

    private val _verses =
        MutableStateFlow<Resource<List<VerseEntity>>>(Resource.Loading(emptyList()))
    val verses: StateFlow<Resource<List<VerseEntity>>> = _verses

    private val _noteSaveState = MutableStateFlow<Resource<Boolean>>(Resource.Loading(false))
    val noteSaveState: StateFlow<Resource<Boolean>> = _noteSaveState

    private val _notes = MutableStateFlow<Resource<List<FullNote>>>(Resource.Loading(emptyList()))
    val notes: StateFlow<Resource<List<FullNote>>> = _notes

    private val _note = MutableStateFlow<Resource<FullNote>>(Resource.Empty())
    val note: StateFlow<Resource<FullNote>> = _note

    private val versesExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _verses.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), emptyList()
        )
    }

    private val noteSaveExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _noteSaveState.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), null
        )
    }

    private val notesExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _notes.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), null
        )
    }

    private val noteExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _note.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), null
        )
    }

    fun fetchListOfVerses(bibleVersionId: String?, verseIds: List<String>) {
        viewModelScope.launch(versesExceptionHandler) {
            _verses.value =
                Resource.Success(
                    versesRepository.getVersesById(
                        bibleVersionId ?: prefs.selectedBibleVersionId, verseIds
                    )
                )
        }
    }

    fun saveNote(noteId: Long, bibleVersionId: String?, chapterId: String, chapterName: String, verseIds: List<Int>, comment: String) {
        _noteSaveState.value = Resource.Loading(true)
        val note = Note(
            id = if (noteId != 0L) noteId else 0,
            bibleId = bibleVersionId ?: prefs.selectedBibleVersionId,
            chapterId = chapterId,
            chapterName = chapterName,
            verses = verseIds,
            comment = comment
        )

        viewModelScope.launch(noteSaveExceptionHandler) {
            notesRepository.addNote(note)
            _noteSaveState.value = Resource.Success(true)
        }
    }

    init {
        fetchNotes()
    }

    private fun fetchNotes() {
        viewModelScope.launch(notesExceptionHandler) {
            notesRepository.getNotes().collect { notes ->
                _notes.value = Resource.Success(
                    notes.map { note ->
                        val verseIds = note.verses.map { it -> "${note.chapterId}.$it" }.toList()
                        val verses = versesRepository.getVersesById(note.bibleId, verseIds)
                        val fullNote = FullNote(
                            id = note.id,
                            bibleId = note.bibleId,
                            chapterId = note.chapterId,
                            chapterName = note.chapterName,
                            verseIds = note.verses,
                            verses = verses,
                            comment = note.comment,
                            dateAdded = note.dateAdded,
                            dateUpdated = note.dateUpdated
                        )
                        fullNote
                    }.toList()
                )
            }
        }
    }

    fun deleteNote(note: FullNote) {
        viewModelScope.launch {
            notesRepository.deleteNote(note.id)
        }
    }

    fun fetchNoteById(noteId: Long) {
        viewModelScope.launch(noteExceptionHandler) {
            notesRepository.getNoteById(noteId).collect { note ->
                val verseIds = note.verses.map { it -> "${note.chapterId}.$it" }.toList()
                val verses = versesRepository.getVersesById(note.bibleId, verseIds)
                _note.value = Resource.Success(note.toFullNote(verses))
            }
        }
    }

    private fun Note.toFullNote(verses: List<VerseEntity>): FullNote {
        val note = this
        return FullNote(
            id = note.id,
            bibleId = note.bibleId,
            chapterId = note.chapterId,
            chapterName = note.chapterName,
            verseIds = note.verses,
            verses = verses,
            comment = note.comment,
            dateAdded = note.dateAdded,
            dateUpdated = note.dateUpdated
        )
    }
}
