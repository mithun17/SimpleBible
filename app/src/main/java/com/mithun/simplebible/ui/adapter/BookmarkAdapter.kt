package com.mithun.simplebible.ui.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.databinding.ListItemBookmarkBinding
import com.mithun.simplebible.utilities.VerseFormatter

/**
 * Adapter to display the bookmarks
 * @param onMoreOptionsClick click listener for the more options menu
 */
class BookmarkAdapter constructor(private val onMoreOptionsClick: (View, BookmarkItem) -> Unit) :
    RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    private var bookmarkList: List<BookmarkItem> = mutableListOf()

    fun setBookmarks(bookmarks: List<BookmarkItem>) {
        bookmarkList = bookmarks
    }

    inner class ViewHolder(private val binding: ListItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bookmark: BookmarkItem) {
            val spannableString = SpannableStringBuilder()
            // Style the verses with red text when applicable
            spannableString.append(
                VerseFormatter.formatVerseForDisplay(
                    binding.root.context,
                    bookmark.verseId.split(".").last().toInt(),
                    bookmark.verse
                )
            )
            binding.tvBookmarkVerse.text = spannableString
            binding.tvBookmarkTitle.text = bookmark.reference
            binding.ivBookmarkShare.setOnClickListener {
                onMoreOptionsClick.invoke(it, bookmark)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bookmarkList[position])
    }

    override fun getItemCount(): Int = bookmarkList.size
}

class BookmarkItem(
    val id: Long = 0,
    val bibleId: String,
    val chapterId: String,
    val verseId: String,
    val verse: String,
    val reference: String
)
