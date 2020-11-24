package com.mithun.simplebible.ui.adapter

import android.R.attr.left
import android.R.attr.right
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.data.model.Verse
import com.mithun.simplebible.ui.custom.VerseTextView


class VersesAdapter : ListAdapter<Verse, VersesAdapter.ViewHolder>(VersesDiffUtil()) {

    class ViewHolder(private val view: VerseTextView) : RecyclerView.ViewHolder(view) {
        fun bind(item: Verse) {
            view.setVerse(item.number, item.text)

            // TODO set click listener
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(VerseTextView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class VersesDiffUtil: DiffUtil.ItemCallback<Verse>() {
    override fun areItemsTheSame(oldItem: Verse, newItem: Verse): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: Verse, newItem: Verse): Boolean {
        return oldItem==newItem
    }
}
