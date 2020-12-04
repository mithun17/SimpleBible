package com.mithun.simplebible.data.model

data class Verse(
    val number: Int,
    val text: String,
    var isSelected: Boolean = false,
    var hasNotes: Boolean = false,
    var isBookmarked: Boolean = false,
)
