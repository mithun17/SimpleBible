package com.mithun.simplebible.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mithun.simplebible.databinding.ItemActionSheetBinding

/**
 * Adapter to display the list of items in the Action sheet
 */
class ActionSheetAdapter(
    private val actionList: List<Action>,
    private val callback: ClickListener
) : RecyclerView.Adapter<ActionSheetAdapter.ViewHolder>() {

    interface ClickListener {
        fun onActionClick(actionCode: Int)
    }

    inner class ViewHolder(private val binding: ItemActionSheetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(action: Action) {
            binding.tvActionItem.text = action.actionText
            binding.tvActionItem.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    binding.root.context,
                    action.actionDrawable
                ),
                null, null, null
            )

            binding.root.setOnClickListener {
                callback.onActionClick(action.actionCode)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemActionSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(actionList[position])
    }

    override fun getItemCount(): Int {
        return actionList.size
    }
}
