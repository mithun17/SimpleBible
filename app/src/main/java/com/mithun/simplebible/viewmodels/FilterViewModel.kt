package com.mithun.simplebible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Bible
import com.mithun.simplebible.data.repository.BibleRepository
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.ResourcesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val prefs: Prefs,
    private val resourcesUtil: ResourcesUtil
) : ViewModel() {

    private val _bibles = MutableStateFlow<Resource<List<Bible>>>(Resource.Loading(emptyList()))
    val bibles: MutableStateFlow<Resource<List<Bible>>> = _bibles

    private val _bibleSelection = MutableStateFlow("")
    val bibleSelection: MutableStateFlow<String> = _bibleSelection

    private val biblesExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _bibles.value = Resource.Error(
            throwable.message ?: resourcesUtil.getString(R.string.errorGenericString), emptyList()
        )
    }

    init {
        viewModelScope.launch(biblesExceptionHandler) {
            _bibles.value = Resource.Success(bibleRepository.getPresetBibles())
            _bibleSelection.value = prefs.selectedBibleVersionId
        }
    }

    fun saveBibleSelection(bibleId: String) {
        prefs.selectedBibleVersionId = bibleId
    }
}
