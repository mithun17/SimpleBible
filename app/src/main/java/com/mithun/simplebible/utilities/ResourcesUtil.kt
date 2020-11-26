package com.mithun.simplebible.utilities

import android.content.Context

class ResourcesUtil constructor(private val context: Context) {
    fun getString(resourceId: Int): String {
        return context.getString(resourceId)
    }
}
