package com.mithun.simplebible.data.model

data class Content (

	val name : String,
	val type : String,
	val attrs : Attrs,
	val items : List<Items>
)
