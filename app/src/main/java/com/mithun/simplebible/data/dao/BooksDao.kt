package com.mithun.simplebible.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mithun.simplebible.data.model.Book

@Dao
interface BooksDao {

    @Query("SELECT * FROM books")
    suspend fun getBooks(): List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)
}
