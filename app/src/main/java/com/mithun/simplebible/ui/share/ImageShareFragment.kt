package com.mithun.simplebible.ui.share

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.mithun.simplebible.R
import com.mithun.simplebible.databinding.FragmentImageShareBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.utilities.CommonUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageShareFragment : BaseFragment() {

    private var _binding: FragmentImageShareBinding? = null
    private val binding get() = _binding!!

    private val args: ImageShareFragmentArgs by navArgs()
    private val fileUri by lazy { Uri.parse(args.fileUri) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateMenu(R.menu.menu_share)
        binding.ivImageVerse.setImageURI(fileUri)
    }

    private fun inflateMenu(menu: Int) {
        with(binding.toolbar.root) {
            inflateMenu(menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_share -> {
                        CommonUtils.showImageShareIntent(requireContext(), fileUri)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
