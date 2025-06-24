package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mithun.simplebible.data.repository.BookmarkRepository
import com.mithun.simplebible.data.repository.VersesRepository

class BookmarkViewModelFactory(
    private val bookmarkRepository: BookmarkRepository,
    private val versesRepository: VersesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookmarkViewModel(bookmarkRepository, versesRepository) as T
    }
}
