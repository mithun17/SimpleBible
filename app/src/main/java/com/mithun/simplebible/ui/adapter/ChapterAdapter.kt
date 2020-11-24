package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.databinding.GalleryItemChapterBinding
import com.mithun.simplebible.ui.book.ChapterSelectionFragmentDirections

class ChapterAdapter : ListAdapter<ChapterItem, ChapterAdapter.ViewHolder>(ChapterDiffUtil()) {

    class ViewHolder(private val binding: GalleryItemChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChapterItem) {
            binding.tvChapterNumber.text=item.number.toString()

            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "${item.bookId} : ${item.number}", Toast.LENGTH_SHORT).show()

                binding.root.findNavController().navigate(ChapterSelectionFragmentDirections.actionSelectVerse(chapterId = "${item.bookId}.${item.number}", chapterFullName = "${item.bookName} ${item.number}"))

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterAdapter.ViewHolder {
        return ChapterAdapter.ViewHolder(GalleryItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChapterAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
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
