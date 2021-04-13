package com.mithun.simplebible.di

import android.content.Context
import com.mithun.simplebible.data.dao.BibleDao
import com.mithun.simplebible.data.dao.BookmarksDao
import com.mithun.simplebible.data.dao.BooksDao
import com.mithun.simplebible.data.dao.ChaptersDao
import com.mithun.simplebible.data.dao.NotesDao
import com.mithun.simplebible.data.dao.VersesEntityDao
import com.mithun.simplebible.data.database.SimpleBibleDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): SimpleBibleDB {
        return SimpleBibleDB.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideBooksDao(database: SimpleBibleDB): BooksDao {
        return database.booksDao()
    }

    @Singleton
    @Provides
    fun provideChaptersDao(database: SimpleBibleDB): ChaptersDao {
        return database.chaptersDao()
    }

    @Singleton
    @Provides
    fun provideVersesDao(database: SimpleBibleDB): VersesEntityDao {
        return database.versesEntityDao()
    }

    @Singleton
    @Provides
    fun provideNotesDao(database: SimpleBibleDB): NotesDao {
        return database.notesDao()
    }

    @Singleton
    @Provides
    fun provideBookmarksDao(database: SimpleBibleDB): BookmarksDao {
        return database.bookmarksDao()
    }

    @Singleton
    @Provides
    fun provideBiblesDao(database: SimpleBibleDB): BibleDao {
        return database.bibleDao()
    }
}
