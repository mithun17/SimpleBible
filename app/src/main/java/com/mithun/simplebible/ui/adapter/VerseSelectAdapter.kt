package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.databinding.GalleryItemChapterBinding

/**
 * Adapter to display the verse numbers for a selected chapter
 * @param onVerseSelect verse selection listener
 */
class VerseSelectAdapter constructor(private val onVerseSelect: (Int) -> Unit) :
    ListAdapter<VersesItem, VerseSelectAdapter.ViewHolder>(VersesItemDiffUtil()) {

    inner class ViewHolder(private val binding: GalleryItemChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VersesItem) {
            binding.tvChapterNumber.text = item.number.toString()
            binding.root.setOnClickListener {
                onVerseSelect.invoke(item.number)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseSelectAdapter.ViewHolder {
        return ViewHolder(GalleryItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VerseSelectAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class VersesItemDiffUtil : DiffUtil.ItemCallback<VersesItem>() {
    override fun areItemsTheSame(oldItem: VersesItem, newItem: VersesItem): Boolean {
        return oldItem.number == newItem.number
    }

    override fun areContentsTheSame(oldItem: VersesItem, newItem: VersesItem): Boolean {
        return oldItem.number == newItem.number
    }
}

/**
 * Data model representing the verse item in the list
 */
data class VersesItem(
    val bookId: String,
    val bookName: String,
    val number: Int
)
