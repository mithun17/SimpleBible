package com.mithun.simplebible.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.data.model.Verse
import com.mithun.simplebible.ui.custom.VerseTextView

class VersesAdapter(private val callback: clickListener) : ListAdapter<Verse, VersesAdapter.ViewHolder>(VersesDiffUtil()) {

    interface clickListener {
        fun onClick()
        fun unClick()
    }

    val listOfSelectedVerses = sortedMapOf<Int, Verse>()

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
                    listOfSelectedVerses[item.number] = item
                } else {
                    view.unselectVerse()
                    listOfSelectedVerses.remove(item.number)
                }

                if (listOfSelectedVerses.isNotEmpty()) {
                    callback.onClick()
                } else {
                    callback.unClick()
                }

                // TODO show action sheet
//                Toast.makeText(view.context, listOfSelectedVerses.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(VerseTextView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedVerses(): Map<Int, Verse> {
        return listOfSelectedVerses
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
