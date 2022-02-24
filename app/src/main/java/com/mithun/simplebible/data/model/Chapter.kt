package com.mithun.simplebible.data.model

data class Chapter(
    val id: String,
    val number: String,
    val bookId: String,
    val chapterId: String,
    val bibleId: String,
    val reference: String,
    val content: List<Content>,
    val verseCount: Int,
    val next: Page?,
    val previous: Page?
)
