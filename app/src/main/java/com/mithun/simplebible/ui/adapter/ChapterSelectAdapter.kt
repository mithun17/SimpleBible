package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.R
import com.mithun.simplebible.databinding.GalleryItemChapterBinding

/**
 * Adapter to display all the chapter numbers for a book
 * @param onChapterSelect chapter selection listener
 */
class ChapterSelectAdapter constructor(private val onChapterSelect: (String) -> Unit) :
    ListAdapter<ChapterItem, ChapterSelectAdapter.ViewHolder>(ChapterDiffUtil()) {

    // keep track of selected chapter. default to 1
    private var selectedChapterNumber = 1

    inner class ViewHolder(private val binding: GalleryItemChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChapterItem) {
            binding.tvChapterNumber.text = item.number.toString()
            binding.root.setOnClickListener {
                selectedChapterNumber = item.number
                onChapterSelect.invoke("${item.bookId}.${item.number}")
            }

            if (item.number == selectedChapterNumber) {
                TextViewCompat.setTextAppearance(binding.tvChapterNumber, R.style.Headline5_SelectedState)
            } else {
                TextViewCompat.setTextAppearance(binding.tvChapterNumber, R.style.Headline5)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterSelectAdapter.ViewHolder {
        return ViewHolder(GalleryItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChapterSelectAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedChapterNumber(chapterId: Int) {
        selectedChapterNumber = chapterId
        notifyDataSetChanged()
    }
}

private class ChapterDiffUtil : DiffUtil.ItemCallback<ChapterItem>() {
    override fun areItemsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean {
        return oldItem.number == newItem.number
    }

    override fun areContentsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean {
        return oldItem.number == newItem.number
    }
}

/**
 * Data model representing the Chapter item in the list
 */
data class ChapterItem(
    val bookId: String,
    val bookName: String,
    val number: Int
)
