package com.mithun.simplebible.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mithun.simplebible.R
import com.mithun.simplebible.databinding.FragmentImageSelectGalleryBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.ui.adapter.ImageGalleryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageSelectGalleryFragment : BaseFragment() {

    private var _binding: FragmentImageSelectGalleryBinding? = null
    private val binding get() = _binding!!

    private val args: ImageSelectGalleryFragmentArgs by navArgs()
    private val imageGalleryAdapter by lazy {
        ImageGalleryAdapter { imageResourceId ->
            setImageSelection(imageResourceId)
        }
    }

    private fun setImageSelection(imageResourceId: Int) {
        // pass result back
        with(findNavController()) {
            navigate(ImageSelectGalleryFragmentDirections.actionImageEdit(args.verse, args.verseId, imageResourceId))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentImageSelectGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvImages.adapter = imageGalleryAdapter

        // image paths stored in array resource
        val imageArray = requireContext().resources.obtainTypedArray(R.array.images_for_verses)
        val imageList = mutableListOf<Int>()
        for (index in 0 until imageArray.length()) {
            imageList.add(imageArray.getResourceId(index, 0))
        }

        imageGalleryAdapter.submitList(imageList)
        imageArray.recycle()
    }
}
