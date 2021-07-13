package com.mithun.simplebible.ui.adapter

import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.data.model.Verse
import com.mithun.simplebible.ui.custom.VerseTextView

class VersesAdapter(private val callback: clickListener) :
    ListAdapter<Verse, VersesAdapter.ViewHolder>(VersesDiffUtil()) {

    interface clickListener {
        fun onClick()
        fun unClick()
    }

    // Map of verse <id, text>
    val listOfSelectedVerses = sortedMapOf<Int, String>()

    inner class ViewHolder(private val view: VerseTextView) : RecyclerView.ViewHolder(view) {
        fun bind(item: Verse) {
            view.setVerse(item.number, item.text, item.isBookmarked, item.hasNotes)

            if (item.number in listOfSelectedVerses) {
                view.selectVerse()
            }

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
                    callback.onClick()
                } else {
                    callback.unClick()
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

    fun setBookmarked(verseNumber: Int) {
        listOfSelectedVerses.remove(verseNumber)
        val index = if (verseNumber> 0) verseNumber - 1 else 0
        getItem(index).isBookmarked = true
        notifyItemChanged(index)
    }

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
