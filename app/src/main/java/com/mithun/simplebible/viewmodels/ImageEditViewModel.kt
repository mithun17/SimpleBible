package com.mithun.simplebible.viewmodels

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.BibleApplication
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.VerseEntity
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.utilities.ResourcesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ImageEditViewModel @Inject constructor(
    private val versesRepository: VersesRepository,
    private val resourcesUtil: ResourcesUtil,
    application: Application
) : AndroidViewModel(application) {

    private val _verse = MutableStateFlow<Resource<VerseEntity>>(Resource.Loading())
    val verse: StateFlow<Resource<VerseEntity>> = _verse

    private val _filePath = MutableStateFlow(Pair(FileCopyStatus.INITIALISED, ""))
    val filePath: StateFlow<Pair<FileCopyStatus, String>> = _filePath

    private val versesExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        _verse.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString)
        )
    }

    fun fetchVerse(verseId: String, bibleId: String) {
        viewModelScope.launch(versesExceptionHandler) {
            _verse.value = Resource.Success(versesRepository.getVerseById(bibleId, verseId))
        }
    }

    fun saveImage(verseId: String, bitmap: Bitmap) {

        _filePath.value = Pair(FileCopyStatus.STARTED, "")
        with(getApplication<BibleApplication>().applicationContext) {
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let { file ->
                file.mkdirs()
                val fileName = "sb_${
                verseId.replace(" ", "_").replace(":", "_").replace(".", "_")
                }.jpg"

                try {
                    var path: String? = null
                    val fos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val contentValues = ContentValues()
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        val imageUri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        imageUri?.let {
                            path = it.toString()
                            contentResolver.openOutputStream(it)
                        }
                    } else {
                        val imagesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
                        val image = File(imagesDir, fileName)
                        path = image.toURI().toString()
                        FileOutputStream(image)
                    }
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos?.close()
                    path?.let { filePath ->
                        _filePath.value = Pair(FileCopyStatus.SUCCESS, filePath)
                    }
                } catch (e: IOException) {
                    // Log Message
                    _filePath.value = Pair(FileCopyStatus.FAIL, "")
                }
            }
        }
    }

    enum class FileCopyStatus {
        INITIALISED, STARTED, SUCCESS, FAIL
    }
}
