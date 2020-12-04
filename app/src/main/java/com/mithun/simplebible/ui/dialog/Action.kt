package com.mithun.simplebible.ui.dialog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Action(
    val actionCode: Int,
    val actionDrawable: Int,
    val actionText: String
) : Parcelable
