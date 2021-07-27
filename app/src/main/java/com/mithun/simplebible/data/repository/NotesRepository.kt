package com.mithun.simplebible.data.repository

import com.mithun.simplebible.data.api.BibleApi
import com.mithun.simplebible.data.dao.NotesDao
import com.mithun.simplebible.data.dao.VersesEntityDao
import javax.inject.Inject

class NotesRepository @Inject constructor(
    val bibleApi: BibleApi,
    val notesDao: NotesDao,
    val versesEntityDao: VersesEntityDao
) : NotesDao by notesDao {
    // Anemic repo. Delegate work to DAO until this repo has actual stuff to do.
}
