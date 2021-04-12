package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.databinding.ListItemBookmarkBinding

class BookmarkAdapter :
    RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    private var bookmarkList: List<Bookmark> = mutableListOf()

    fun setBookmarks(bookmarks: List<Bookmark>) {
        bookmarkList = bookmarks
    }

    interface ClickListener {
        fun onItemClick(actionCode: Int)
    }

    inner class ViewHolder(private val binding: ListItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bookmark: Bookmark) {
            binding.tvBookmarkVerse.text = bookmark.verse
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
