package com.mithun.simplebible.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.mithun.simplebible.R

object CommonUtils {

    fun showShareIntent(context: Context, shareText: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                shareText
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.app_name))
        context.startActivity(shareIntent)
    }

    fun copyToClipboard(context: Context, copyText: String) {
        val clip = ClipData.newPlainText("label", copyText)
        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        clipboard?.setPrimaryClip(clip)
    }
}
