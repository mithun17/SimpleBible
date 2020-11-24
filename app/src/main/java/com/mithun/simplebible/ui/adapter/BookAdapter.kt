package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.data.model.Book
import com.mithun.simplebible.databinding.ListItemBookBinding
import com.mithun.simplebible.ui.book.HomeFragmentDirections

class BookAdapter : ListAdapter<Book, BookAdapter.ViewHolder>(BookDiffUtil()) {

    class ViewHolder(private val binding: ListItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Book) {
            binding.tvBookName.text=item.name
            binding.root.setOnClickListener {
                val bookName = item.name
                val bookId = item.id
                val chapterCount = item.chapters.last().number.toInt()
                binding.root.findNavController().navigate(HomeFragmentDirections.actionSelectBook(bookName, bookId, chapterCount))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class BookDiffUtil : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.id == newItem.id
    }

}
