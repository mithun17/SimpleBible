package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.R
import com.mithun.simplebible.databinding.GalleryItemChapterBinding

class ChapterAdapter constructor(private val chapterSelectListener: (String) -> Unit) :
    ListAdapter<ChapterItem, ChapterAdapter.ViewHolder>(ChapterDiffUtil()) {

    private var selectedChapterNumber = 1

    inner class ViewHolder(private val binding: GalleryItemChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChapterItem) {
            binding.tvChapterNumber.text = item.number.toString()

            binding.root.setOnClickListener {
                selectedChapterNumber = item.number
                chapterSelectListener.invoke("${item.bookId}.${item.number}")
            }

            if (item.number == selectedChapterNumber) {
                TextViewCompat.setTextAppearance(binding.tvChapterNumber, R.style.Headline5_SelectedState)
            } else {
                TextViewCompat.setTextAppearance(binding.tvChapterNumber, R.style.Headline5)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterAdapter.ViewHolder {
        return ViewHolder(GalleryItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChapterAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedChapterNumber(chapterId: Int) {
        selectedChapterNumber = chapterId
        notifyDataSetChanged()
    }
}

data class ChapterItem(
    val bookId: String,
    val bookName: String,
    val number: Int
)

private class ChapterDiffUtil : DiffUtil.ItemCallback<ChapterItem>() {
    override fun areItemsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean {
        return oldItem.number == newItem.number
    }

    override fun areContentsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean {
        return oldItem.number == newItem.number
    }
}
