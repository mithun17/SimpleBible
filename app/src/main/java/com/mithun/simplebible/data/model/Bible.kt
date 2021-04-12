package com.mithun.simplebible.data.model

data class Bible(
    val id: String,
    val dblId: String,
    val abbreviation: String,
    val abbreviationLocal: String,
    val name: String,
    val nameLocal: String,
    val description: String,
    val descriptionLocal: String,
    val type: String
)
