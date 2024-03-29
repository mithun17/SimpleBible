package com.mithun.simplebible.ui.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.data.repository.data.FullNote
import com.mithun.simplebible.databinding.ListItemNoteBinding
import com.mithun.simplebible.ui.notes.NotesFragmentDirections
import com.mithun.simplebible.utilities.VerseFormatter

/**
 * Adapter to display all the notes stored in the device
 * @param onNoteMoreOptionsClick click listener for more options
 */
class NotesAdapter(private val onNoteMoreOptionsClick: (View, FullNote) -> Unit) :
    ListAdapter<FullNote, NotesAdapter.ViewHolder>(NoteDiffUtil()) {

    inner class ViewHolder(private val binding: ListItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FullNote) {
            binding.tvNoteTitle.text = item.chapterName
            binding.tvNoteComment.text = item.comment

            val spannableString = SpannableStringBuilder()
            item.verses.forEach {
                // Apply styles for the verses within a note
                spannableString.append(
                    VerseFormatter.formatVerseForDisplay(
                        binding.root.context,
                        it.number.toInt(),
                        it.text
                    )
                )
            }
            binding.tvNoteVerses.text = spannableString
            binding.ivNoteShare.setOnClickListener {
                onNoteMoreOptionsClick(it, item)
            }

            binding.root.setOnClickListener {
                it.findNavController()
                    .navigate(
                        NotesFragmentDirections.actionAddEditNote(
                            item.id,
                            item.bibleId,
                            item.chapterName,
                            item.chapterId,
                            item.verseIds.toIntArray(),
                            item.comment
                        )
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class NoteDiffUtil : DiffUtil.ItemCallback<FullNote>() {
    override fun areItemsTheSame(oldItem: FullNote, newItem: FullNote): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FullNote, newItem: FullNote): Boolean {
        return oldItem == newItem
    }
}
