package com.mithun.simplebible.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.databinding.ItemActionSheetBinding

class ActionSheetAdapter(private val actionList: List<Action>, private val callback: ClickListener) : RecyclerView.Adapter<ActionSheetAdapter.ViewHolder>() {

    interface ClickListener {
        fun onActionClick(actionCode: Int)
    }

    inner class ViewHolder(private val binding: ItemActionSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(action: Action) {
            binding.tvActionItem.text=action.actionText
            binding.tvActionItem.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(binding.root.context, action.actionDrawable), null, null, null)

            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "clicked on ${action.actionCode}", Toast.LENGTH_SHORT).show()
                callback.onActionClick(action.actionCode)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemActionSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(actionList[position])
    }

    override fun getItemCount(): Int {
        return actionList.size
    }
}
//
//private class ActionSheetDiffUtil : DiffUtil.ItemCallback<Action>() {
//    override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean {
//        return oldItem.actionCode == newItem.actionCode
//    }
//
//    override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean {
//        return oldItem.actionCode == newItem.actionCode
//    }
//}
