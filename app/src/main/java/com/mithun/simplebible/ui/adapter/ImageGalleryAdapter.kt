package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mithun.simplebible.databinding.ListItemImageBinding

class ImageGalleryAdapter(private val imageSelectListener: (Int) -> Unit) :
    ListAdapter<Int, ImageGalleryAdapter.ViewHolder>(ImagesDiffUtil()) {

    inner class ViewHolder(private val binding: ListItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int) {

            Glide.with(binding.root.context)
                .load(item)
                .centerCrop()
                .thumbnail()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.ivBackground)

            binding.root.setOnClickListener {
                imageSelectListener.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class ImagesDiffUtil : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return oldItem == newItem
    }
}
