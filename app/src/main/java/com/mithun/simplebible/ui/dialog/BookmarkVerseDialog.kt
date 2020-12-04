package com.mithun.simplebible.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mithun.simplebible.databinding.DialogBookmarkVerseBinding

class BookmarkVerseDialog : DialogFragment() {

    private var _binding: DialogBookmarkVerseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogBookmarkVerseBinding.inflate(inflater, container, false)
        return binding.root
    }
}
