package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.R
import com.mithun.simplebible.data.model.Book
import com.mithun.simplebible.databinding.ListItemBookBinding

class BookAdapter constructor(private val bookSelectListener: (bookName: String, bookId: String, chapterCount: Int) -> Unit) :
    ListAdapter<Book, BookAdapter.ViewHolder>(BookDiffUtil()) {

    private var selectedBookId = ""

    inner class ViewHolder(private val binding: ListItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Book) {
            binding.tvBookName.text = item.name

            binding.root.setOnClickListener {
                val bookName = item.name
                val bookId = item.id
                val chapterCount = item.chapters.last().number.toInt()
                selectedBookId = bookId
                bookSelectListener.invoke(bookName, bookId, chapterCount)
            }

            // set selected item style
            if (item.id == selectedBookId) {
                TextViewCompat.setTextAppearance(binding.tvBookName, R.style.Headline5_SelectedState)
            } else {
                TextViewCompat.setTextAppearance(binding.tvBookName, R.style.Headline5)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedBook(bookId: String) {
        selectedBookId = bookId
        notifyDataSetChanged()
    }
}

private class BookDiffUtil : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return "${oldItem.id}.${oldItem.bibleId}" == "${newItem.id}.${newItem.bibleId}"
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return "${oldItem.id}.${oldItem.bibleId}" == "${newItem.id}.${newItem.bibleId}"
    }
}
