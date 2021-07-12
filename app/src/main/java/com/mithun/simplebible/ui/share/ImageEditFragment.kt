package com.mithun.simplebible.ui.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentImageEditBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.utilities.CommonUtils
import com.mithun.simplebible.utilities.ExtensionUtils.toRegularText
import com.mithun.simplebible.utilities.ExtensionUtils.toShareText
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.viewmodels.ImageEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImageEditFragment : BaseFragment() {

    private var _binding: FragmentImageEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var verseText: String
    private lateinit var verseId: String

    private val args: ImageEditFragmentArgs by navArgs()
    private val imageEditViewModel: ImageEditViewModel by viewModels()

    @Inject
    lateinit var prefs: Prefs

    private var imageVerseText = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageEditBinding.inflate(inflater, container, false)
        verseText = args.verse.toRegularText()
        verseId = args.verseId
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateMenu(R.menu.menu_save)
        initBackground(args.imageResourceId)
        initVerse()
        initTextColorListener()
    }

    private fun inflateMenu(menu: Int) {
        with(binding.toolbar.root) {
            inflateMenu(menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        copyBitmapAndSaveImage(binding.imageCanvas)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initBackground(imageResourceId: Int) {
        Glide.with(requireContext())
            .load(imageResourceId)
            .into(binding.ivVerseBackgorund)
    }

    private fun initTextColorListener() {
        binding.rgTextColor.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbColorWhite -> {
                    binding.tvVerse.apply {
                        setTextColor(Color.WHITE)
                        setShadowLayer(shadowRadius, shadowDx, shadowDy, Color.BLACK)
                    }
                }
                R.id.rbColorBlack -> {
                    binding.tvVerse.apply {
                        setTextColor(Color.BLACK)
                        setShadowLayer(shadowRadius, shadowDx, shadowDy, Color.WHITE)
                    }
                }
            }
        }
        binding.rbColorWhite.isChecked = true
    }

    private fun initVerse() {
        imageEditViewModel.fetchVerse(verseId, prefs.selectedBibleVersionId)
        lifecycleScope.launchWhenCreated {
            imageEditViewModel.verse.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Loading message
                    }
                    is Resource.Success -> {
                        resource.data?.let {
                            setImageVerse(it)
                            initTextSizeSeekBar()
                            initTextAlignmentListener()
                        }
                    }
                    is Resource.Error -> {
                        // error
                    }
                }
            }
        }
    }

    private fun setImageVerse(it: VerseEntity) {
        imageVerseText = it.toShareText()
        binding.tvVerse.text = imageVerseText
    }

    private fun initTextAlignmentListener() {
        binding.rgTextAlignment.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbLeft -> {
                    binding.tvVerse.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                }
                R.id.rbCenter -> {
                    binding.tvVerse.textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
                R.id.rbRight -> {
                    binding.tvVerse.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                }
            }
            binding.tvVerse.text = imageVerseText
        }
    }

    private fun initTextSizeSeekBar() {
        val minTextSize = 8 // scale independent pixels. Min verse text size
        val step = 2 // incremental steps for increasing text size
        val maxTextSize = CommonUtils.getPxToSp(requireContext(), binding.tvVerse.textSize).toInt()
        binding.sbTextSize.max = (maxTextSize - minTextSize) / step

        binding.sbTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val value = minTextSize + (progress * step)
                binding.tvVerse.textSize = value.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun copyBitmapAndSaveImage(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val bgDrawable = view.background
                if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.TRANSPARENT)
                view.draw(canvas)

                imageEditViewModel.saveImage(verseId, bitmap)
                imageEditViewModel.filePath.collect { status ->
                    when (status.first) {
                        ImageEditViewModel.FileCopyStatus.STARTED -> {
                            // TODO show progress
                        }
                        ImageEditViewModel.FileCopyStatus.SUCCESS -> {
                            val fileUri = status.second
                            if (fileUri.isNotEmpty()) {
                                findNavController().navigate(ImageEditFragmentDirections.actionNavigationImageEditToNavigationImageShare(fileUri))
                            }
                        }
                        ImageEditViewModel.FileCopyStatus.FAIL -> {
                            // TODO show error
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
