package com.mithun.simplebible.ui.adapter

import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.ui.custom.VerseTextView
import com.mithun.simplebible.ui.model.Verse

/**
 * Adapter to display all the Bible verses for a selected chapter
 * @param onVerseSelect verse selection listener
 */
class VersesAdapter(private val onVerseSelect: ClickListener) :
    ListAdapter<Verse, VersesAdapter.ViewHolder>(VersesDiffUtil()) {

    interface ClickListener {
        fun onSelect()
        fun onUnSelect()
    }

    // Map of verse <id, text>
    val listOfSelectedVerses = sortedMapOf<Int, String>()

    inner class ViewHolder(private val view: VerseTextView) : RecyclerView.ViewHolder(view) {
        fun bind(item: Verse) {
            view.setVerse(item.number, item.text, item.isBookmarked, item.hasNotes)

            if (item.number in listOfSelectedVerses) {
                view.selectVerse()
            }

            // handle the verse selection and un-selection
            view.setOnClickListener {
                item.isSelected = !item.isSelected

                if (item.isSelected) {
                    view.selectVerse()
                    listOfSelectedVerses[item.number] = item.text
                } else {
                    view.unselectVerse()
                    listOfSelectedVerses.remove(item.number)
                }

                if (listOfSelectedVerses.isNotEmpty()) {
                    onVerseSelect.onSelect()
                } else {
                    onVerseSelect.onUnSelect()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(VerseTextView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun clearSelection() {
        listOfSelectedVerses.clear()
        notifyDataSetChanged()
    }

    /**
     * Mark a verse as bookmarked. Display a bookmark icon next to the verse
     * @param verseNumber verse to be bookmarked
     */
    fun setBookmarked(verseNumber: Int) {
        listOfSelectedVerses.remove(verseNumber)
        val index = if (verseNumber> 0) verseNumber - 1 else 0
        getItem(index).isBookmarked = true
        notifyItemChanged(index)
    }

    // scroll to the selected verse number
    fun scrollToVerse(scrollView: NestedScrollView, recyclerView: RecyclerView, selectedVerseNumber: Int) {
        val index = currentList.indexOfFirst { it.number == selectedVerseNumber }
        if (index != -1) {
            recyclerView.post {
                scrollView.smoothScrollTo(0, recyclerView.getChildAt(index).y.toInt())
            }
        }
    }
}

class VersesDiffUtil : DiffUtil.ItemCallback<Verse>() {
    override fun areItemsTheSame(oldItem: Verse, newItem: Verse): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Verse, newItem: Verse): Boolean {
        return oldItem == newItem
    }
}
