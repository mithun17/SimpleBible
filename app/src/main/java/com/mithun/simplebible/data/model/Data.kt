package com.mithun.simplebible.data.model

data class Data (

	val id : String,
	val orgId : String,
	val bookId : String,
	val chapterId : String,
	val bibleId : String,
	val reference : String,
	val content : List<Content>,
	val verseCount : Int,
	val next : Next,
	val previous : Previous
)
