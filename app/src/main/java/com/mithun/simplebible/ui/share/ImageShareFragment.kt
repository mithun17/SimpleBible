package com.mithun.simplebible.ui.share

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.mithun.simplebible.databinding.FragmentImageShareBinding
import com.mithun.simplebible.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageShareFragment : BaseFragment() {

    private var _binding: FragmentImageShareBinding? = null
    private val binding get() = _binding!!

    val args: ImageShareFragmentArgs by navArgs()

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
        val fileUri = Uri.parse(args.fileUri)
        binding.ivImageVerse.setImageURI(fileUri)
        binding.btnShare.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, "Share image"))
        }
    }
}
