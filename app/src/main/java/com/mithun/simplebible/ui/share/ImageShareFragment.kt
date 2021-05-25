package com.mithun.simplebible.ui.share

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentImageVerseBinding
import com.mithun.simplebible.ui.BaseFragment
import com.mithun.simplebible.utilities.CommonUtils
import com.mithun.simplebible.utilities.ExtensionUtils.toRegularText
import com.mithun.simplebible.utilities.ExtensionUtils.toShareText
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.viewmodels.ImageShareViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class ImageShareFragment : BaseFragment() {

    private var _binding: FragmentImageVerseBinding? = null
    private val binding get() = _binding!!

    private lateinit var verseText: String
    private lateinit var verseId: String

    val args: ImageShareFragmentArgs by navArgs()

    private val imageShareViewModel: ImageShareViewModel by viewModels()

    @Inject
    lateinit var prefs: Prefs

    private var imageVerseText = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageVerseBinding.inflate(inflater, container, false)

        verseText = args.verse.toRegularText()
        verseId = args.verseId

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGenerate.setOnClickListener {
            saveImage()
        }

        initVerse()
        initTextColorListener()
    }

    private fun initTextColorListener() {
        binding.rgTextColor.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbColorWhite -> {
                    binding.tvVerse.setTextColor(Color.WHITE)
                }
                R.id.rbColorBlack -> {
                    binding.tvVerse.setTextColor(Color.BLACK)
                }
            }
        }
        binding.rbColorWhite.isChecked = true
    }

    private fun initVerse() {
        imageShareViewModel.fetchVerse(verseId, prefs.selectedBibleVersionId)
        lifecycleScope.launchWhenCreated {
            imageShareViewModel.verse.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Loading message
                    }
                    is Resource.Success -> {
                        val verse = resource.data
                        verse?.let {
                            imageVerseText = it.toShareText()
                            binding.tvVerse.text = imageVerseText

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

        val minTextSize = 8 // scale independent pixels
        val maxTextSize = CommonUtils.getPxToSp(requireContext(), binding.tvVerse.textSize).toInt()
        val step = 2
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

    fun saveImage() {

        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let { file ->
            file.mkdirs()
            val fileName = "${verseId.replace(" ", "_").replace(":", "_")}.jpg"

            val bitmap = getBitmapFromView(binding.imageCanvas)
            try {
                val fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = requireContext().contentResolver
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    resolver.openOutputStream(imageUri!!)
                } else {
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString()
                    val image = File(imagesDir, fileName)
                    FileOutputStream(image)
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos?.close()
            } catch (e: IOException) {
                // Log Message
            }
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.TRANSPARENT)
        view.draw(canvas)
        return returnedBitmap
    }
}
