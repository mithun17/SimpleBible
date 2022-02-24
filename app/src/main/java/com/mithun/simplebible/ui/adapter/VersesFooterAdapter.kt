package com.mithun.simplebible.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.databinding.ItemLayoutVersesFooterBinding
import com.mithun.simplebible.utilities.visible

class VersesFooterAdapter(private val prevChapter: String?, private val nextChapter: String?, private val onChapterSelect: (String) -> Unit) :
    RecyclerView.Adapter<VersesFooterAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLayoutVersesFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(prevChapter: String?, nextChapter: String?) {
            with(binding) {
                prevChapter?.let {
                    tvPrevious.visible
                    tvPrevious.text = prevChapter.replace(".", " ")
                    tvPrevious.setOnClickListener {
                        // navigate to chapter
                        onChapterSelect(prevChapter)
                    }
                }

                nextChapter?.let {
                    tvNext.visible
                    tvNext.text = nextChapter.replace(".", " ")
                    tvNext.setOnClickListener {
                        // navigate to chapter
                        onChapterSelect(nextChapter)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemLayoutVersesFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(prevChapter, nextChapter)
    }

    override fun getItemCount(): Int = 1 // Only one footer
}
