package com.mithun.simplebible.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mithun.simplebible.data.dao.BookmarksDao
import com.mithun.simplebible.data.dao.BooksDao
import com.mithun.simplebible.data.dao.ChaptersDao
import com.mithun.simplebible.data.dao.NotesDao
import com.mithun.simplebible.data.dao.VersesEntityDao
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.data.database.model.FullChapter
import com.mithun.simplebible.data.database.model.Note
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.model.Book
import com.mithun.simplebible.utilities.Converters
import com.mithun.simplebible.utilities.DB_NAME

@Database(entities = [Book::class, FullChapter::class, VerseEntity::class, Bookmark::class, Note::class], version = 1)
@TypeConverters(Converters::class)
abstract class SimpleBibleDB : RoomDatabase() {

    abstract fun booksDao(): BooksDao
    abstract fun chaptersDao(): ChaptersDao
    abstract fun versesEntityDao(): VersesEntityDao
    abstract fun notesDao(): NotesDao
    abstract fun bookmarksDao(): BookmarksDao

    companion object {

        @Volatile
        private var instance: SimpleBibleDB? = null

        fun getInstance(context: Context): SimpleBibleDB {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, SimpleBibleDB::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
