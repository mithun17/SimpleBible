package com.mithun.simplebible.data.model

data class Items(
    val text: String,
    val name: String,
    val type: String,
    val attrs: Attrs?,
    val items: List<Items>
)

enum class Type(val value: String) {
    TEXT("text"),
    TAG("tag"),
    ADD("add"),
    WJ("wj")
}
