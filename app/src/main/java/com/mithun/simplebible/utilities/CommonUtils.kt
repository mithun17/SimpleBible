package com.mithun.simplebible.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.mithun.simplebible.R

/**
 * Common utility methods
 */
object CommonUtils {

    /**
     * Display share sheet for sharing verse text
     *
     * @param context activity context
     * @param shareText verse text to be shared
     */
    fun showTextShareIntent(context: Context, shareText: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = TEXT_MIME_TYPE
        }
        val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.app_name))
        context.startActivity(shareIntent)
    }

    /**
     * Display share sheet for sharing image
     *
     * @param context activity context
     * @param imageUri uri of the image to be shared
     */
    fun showImageShareIntent(context: Context, imageUri: Uri) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = IMAGE_MIME_TYPE
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.imageVerseShareTitle)))
    }

    /**
     * Copy the passed text into device clipboard
     *
     * @param context activity context
     * @param copyText text to be copied into clipboard
     */
    fun copyToClipboard(context: Context, copyText: String) {
        val clip = ClipData.newPlainText("label", copyText)
        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        clipboard?.setPrimaryClip(clip)
    }

    fun getPxToSp(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.scaledDensity
    }

    fun getSpToPx(context: Context, sp: Int): Float {
        return sp * context.resources.displayMetrics.scaledDensity
    }
}
