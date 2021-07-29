package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Bible
import com.mithun.simplebible.databinding.ListItemBibleFilterBinding

/**
 * Adapter to display the list of available Bible versions
 * @param onVersionSelect selection listener lambda
 */
class BibleFilterAdapter constructor(private val onVersionSelect: (Pair<String, String>) -> Unit) :
    ListAdapter<Bible, BibleFilterAdapter.ViewHolder>(BiblesDiffUtil()) {

    // keep track of the selected Bible version
    var selectedId = ""

    inner class ViewHolder(private val binding: ListItemBibleFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Bible) {
            binding.tvBibleTitle.text = item.abbreviationLocal
            binding.tvBibleDescription.text = item.nameLocal
            binding.root.setOnClickListener {
                selectedId = item.id
                onVersionSelect.invoke(Pair(item.id, item.abbreviationLocal ?: item.abbreviation))
                notifyDataSetChanged()
            }

            // Apply different style for the selected item
            if (item.id == selectedId) {
                TextViewCompat.setTextAppearance(binding.tvBibleTitle, R.style.Headline6_SelectedState)
            } else {
                TextViewCompat.setTextAppearance(binding.tvBibleTitle, R.style.Headline6)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemBibleFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class BiblesDiffUtil : DiffUtil.ItemCallback<Bible>() {
    override fun areItemsTheSame(oldItem: Bible, newItem: Bible): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Bible, newItem: Bible): Boolean {
        return oldItem.id == newItem.id
    }
}
