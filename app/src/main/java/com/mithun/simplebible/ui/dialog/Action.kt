package com.mithun.simplebible.ui.dialog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Class representing an action item in Action/Bottom sheet dialog
 */
@Parcelize
data class Action(
    val actionCode: Int,
    val actionDrawable: Int,
    val actionText: String
) : Parcelable
