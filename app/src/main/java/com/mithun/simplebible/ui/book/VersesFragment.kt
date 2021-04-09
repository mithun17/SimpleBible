package com.mithun.simplebible.ui.book

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.databinding.FragmentChapterVersesBinding
import com.mithun.simplebible.ui.adapter.VersesAdapter
import com.mithun.simplebible.ui.dialog.Action
import com.mithun.simplebible.ui.dialog.ActionsBottomSheet
import com.mithun.simplebible.utilities.ExtensionUtils.toCopyText
import com.mithun.simplebible.utilities.KJV_BIBLE_ID
import com.mithun.simplebible.utilities.ResourcesUtil
import com.mithun.simplebible.viewmodels.VersesViewModel
import com.mithun.simplebible.viewmodels.VersesViewModelFactory
import kotlinx.coroutines.flow.collect

class VersesFragment : Fragment(), ActionsBottomSheet.ActionPickerListener {

    // Action sheet request codes
    private val kActionRequestCodeShare = 1
    private val kActionRequestCodeCopy = 2
    private val kActionRequestCodeNote = 3
    private val kActionRequestCodeBookmark = 4

    private val resourcesUtil by lazy {
        ResourcesUtil(requireContext())
    }

    private val versesRepository by lazy {
        VersesRepository.getInstance(requireContext())
    }

    private val versesViewModel: VersesViewModel by viewModels {
        VersesViewModelFactory(versesRepository, resourcesUtil)
    }

    private var _binding: FragmentChapterVersesBinding? = null
    private val binding get() = _binding!!

    val args: VersesFragmentArgs by navArgs()

    private val kRequestCodeActionSheet = 1

    private val versesAdapter by lazy {
        VersesAdapter(object : VersesAdapter.clickListener {
            override fun onClick() {
                binding.fabMore.show()
            }

            override fun unClick() {
                binding.fabMore.hide()
            }
        })
    }

    private val chapterId by lazy {
        args.chapterId
    }

    private val chapterName by lazy {
        args.chapterFullName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentChapterVersesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvVerses.adapter = versesAdapter

        binding.ctbAppBar.title = chapterName
        initViewModelAndSetCollectors()
        versesViewModel.getVerses(KJV_BIBLE_ID, chapterId)
    }

    private fun initViewModelAndSetCollectors() {

        // set click listener
        binding.fabMore.setOnClickListener {
            fragmentManager?.let {

                val actionList = mutableListOf(
                    Action(kActionRequestCodeShare, R.drawable.ic_share, getString(R.string.actionSheetShareLabel)),
                    Action(kActionRequestCodeCopy, R.drawable.ic_copy, getString(R.string.actionSheetCopyLabel)),
                    Action(kActionRequestCodeNote, R.drawable.ic_note, getString(R.string.actionSheetNoteLabel))
                )

                if (versesAdapter.listOfSelectedVerses.size == 1) {
                    actionList.add(Action(kActionRequestCodeBookmark, R.drawable.ic_bookmark, getString(R.string.actionSheetBookmarkLabel)))
                }

                ActionsBottomSheet
                    .with(it, kRequestCodeActionSheet, this@VersesFragment)
                    .action(actionList)
                    .show()
            }
        }
        lifecycleScope.launchWhenCreated {
            versesViewModel.verses.collect { resource ->
                resource.message?.let { errorMessage ->
                    // error
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                    binding.pbDialog.visibility = View.GONE
                }
                resource.data?.let { listOfVerses ->
                    // success or error data
                    versesAdapter.submitList(listOfVerses)
                    binding.pbDialog.visibility = View.GONE
                } ?: run {
                    // Loading message
                    binding.pbDialog.visibility = View.VISIBLE
                }
            }

            versesViewModel.bookmarkSaveState.collect { resource ->
                resource.message?.let { errorMessage ->
                    // error
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                    binding.pbDialog.visibility = View.GONE
                }

                resource.data?.let { saveState ->
                    // bookmark saved
                    binding.pbDialog.visibility = View.GONE
                } ?: run {
                    // Loading message
                    binding.pbDialog.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onActionClick(dialogRequestCode: Int, actionRequestCode: Int) {

        when (dialogRequestCode) {
            kRequestCodeActionSheet -> {
                when (actionRequestCode) {
                    kActionRequestCodeShare -> {
                        // TODO Add a deeplink with the content of verse
                        // Create an implicit intent to share text data to other apps
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, versesAdapter.listOfSelectedVerses.toCopyText(chapterName))
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, "Simple Bible")
                        startActivity(shareIntent)
                    }
                    kActionRequestCodeCopy -> {
                        // structure the verse text
                        versesAdapter.listOfSelectedVerses.toCopyText(chapterName)

                        val clip = ClipData.newPlainText("label", versesAdapter.listOfSelectedVerses.toCopyText(chapterName))
                        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
                        clipboard?.setPrimaryClip(clip)
                    }
                    kActionRequestCodeNote -> {
                        Toast.makeText(requireContext(), versesAdapter.listOfSelectedVerses.toString(), Toast.LENGTH_SHORT).show()
                    }
                    kActionRequestCodeBookmark -> {

                        val verseNumber = versesAdapter.listOfSelectedVerses.firstKey()
                        val verseId = "$chapterId.$verseNumber"

                        val bookmark = Bookmark(
                            bibleId = KJV_BIBLE_ID,
                            verse = verseId
                        )

                        versesViewModel.saveBookmark(verseId, bookmark)
                    }
                }
            }
        }
    }
}
