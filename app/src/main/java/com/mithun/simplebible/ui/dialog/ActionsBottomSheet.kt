package com.mithun.simplebible.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mithun.simplebible.databinding.FragmentActionBottomSheetBinding

class ActionsBottomSheet : BottomSheetDialogFragment() {

    interface ActionPickerListener {

        /**
         * Bottom sheet item click callback
         *
         * @param dialogRequestCode request code of the bottom sheet dialog
         * @param actionRequestCode request code if the item selected
         */
        fun onActionClick(dialogRequestCode: Int, actionRequestCode: Int)

        /**
         * invoked when the item picker bottom sheet is dismissed
         *
         * @param dialogRequestCode request code set on the bottom sheet dialog
         */
        fun onDismissDialog(dialogRequestCode: Int) { /* default implementation to make it optional */
        }
    }

    private lateinit var actionPickerListener: ActionPickerListener

    private var _binding: FragmentActionBottomSheetBinding? = null
    private val binding get() = _binding!!

    companion object {

        private const val kActionSheetTitle = "ActionSheetTitle"
        private const val kActionSheetList = "ActionSheetList"

        private const val kActionSheetRequestCode = "ActionSheetRequestCode"

        fun with(fragmentManager: FragmentManager, requestCode: Int, targetFragment: Fragment? = null): Builder {
            return Builder(fragmentManager, requestCode, targetFragment)
        }

        class Builder(
            private val fragmentManager: FragmentManager,
            private val requestCode: Int,
            private val targetFragment: Fragment? = null
        ) {
            private val builderArguments by lazy { Bundle() }

            // add title
            fun title(title: String): Builder {
                builderArguments.putString(kActionSheetTitle, title)
                return this
            }

            // add list of actions
            fun action(list: List<Action>): Builder {
                builderArguments.putParcelableArrayList(kActionSheetList, ArrayList(list))
                return this
            }

            // displays the Bottom sheet dialog
            fun show() {
                getFragmentInstance().show(fragmentManager, getTagValue())
            }

            private fun getFragmentInstance(): ActionsBottomSheet {

                builderArguments.putInt(kActionSheetRequestCode, requestCode)
                val fragment: ActionsBottomSheet = (fragmentManager.findFragmentByTag(getTagValue()) as ActionsBottomSheet?)
                    ?: run {
                        ActionsBottomSheet().apply {
                            arguments = builderArguments
                        }
                    }
                targetFragment?.let { fragment.setTargetFragment(it, requestCode) }
                return fragment
            }

            private fun getTagValue(): String {
                return ActionsBottomSheet::class.java.simpleName + "." + requestCode
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            actionPickerListener = if (targetFragment != null) {
                targetFragment as ActionPickerListener
            } else {
                context as ActionPickerListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling activity/fragment must implement ActionPickerListener interface")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val requestCode = arguments?.getInt(kActionSheetRequestCode)
        if (::actionPickerListener.isInitialized && requestCode != null) {
            actionPickerListener.onDismissDialog(requestCode)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentActionBottomSheetBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // set request code for the bottom sheet
        val requestCode = arguments?.getInt(kActionSheetRequestCode)

        // get the items passed to bottom sheet
        arguments?.getParcelableArrayList<Action>(kActionSheetList)?.let { list ->

            binding.rvItemPicker.layoutManager = LinearLayoutManager(context)
            val adapter = ActionSheetAdapter(
                list,
                object : ActionSheetAdapter.ClickListener {
                    override fun onActionClick(actionCode: Int) {
                        if (::actionPickerListener.isInitialized && requestCode != null) {
                            actionPickerListener.onActionClick(requestCode, actionCode)
                            dismiss()
                        }
                    }
                }
            )
            binding.rvItemPicker.adapter = adapter
        }
        return rootView
    }
}
