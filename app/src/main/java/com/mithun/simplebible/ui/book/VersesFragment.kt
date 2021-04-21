package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.databinding.FragmentChapterVersesBinding
import com.mithun.simplebible.ui.BaseCollapsibleFragment
import com.mithun.simplebible.ui.adapter.VersesAdapter
import com.mithun.simplebible.ui.dialog.Action
import com.mithun.simplebible.ui.dialog.ActionsBottomSheet
import com.mithun.simplebible.utilities.CommonUtils.copyToClipboard
import com.mithun.simplebible.utilities.CommonUtils.showShareIntent
import com.mithun.simplebible.utilities.ExtensionUtils.toCopyText
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.viewmodels.VersesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class VersesFragment : BaseCollapsibleFragment(), ActionsBottomSheet.ActionPickerListener {

    // Action sheet request codes
    private val kActionRequestCodeShare = 1
    private val kActionRequestCodeCopy = 2
    private val kActionRequestCodeNote = 3
    private val kActionRequestCodeBookmark = 4

    @Inject
    lateinit var prefs: Prefs

    private val versesViewModel: VersesViewModel by viewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
        prefs.lastReadChapter = args.chapterId
        binding.collapsibleToolbar.ctbAppBar.title = chapterName
        initViewModelAndSetCollectors()
        versesViewModel.getVerses(prefs.selectedBibleId, chapterId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_overflow, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_version -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViewModelAndSetCollectors() {

        // set click listener
        binding.fabMore.setOnClickListener {
            fragmentManager?.let {
                val actionList = mutableListOf(
                    Action(
                        kActionRequestCodeShare,
                        R.drawable.ic_share,
                        getString(R.string.actionSheetShareLabel)
                    ),
                    Action(
                        kActionRequestCodeCopy,
                        R.drawable.ic_copy,
                        getString(R.string.actionSheetCopyLabel)
                    ),
                    Action(
                        kActionRequestCodeNote,
                        R.drawable.ic_note,
                        getString(R.string.actionSheetNoteLabel)
                    )
                )

                if (versesAdapter.listOfSelectedVerses.size == 1) {
                    actionList.add(
                        Action(
                            kActionRequestCodeBookmark,
                            R.drawable.ic_bookmark,
                            getString(R.string.actionSheetBookmarkLabel)
                        )
                    )
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
                        // Create an implicit intent to share text data to other apps
                        showShareIntent(requireContext(), versesAdapter.listOfSelectedVerses.toCopyText(chapterName))
                    }
                    kActionRequestCodeCopy -> {
                        // structure the verse text and copy to clipboard
                        copyToClipboard(requireContext(), versesAdapter.listOfSelectedVerses.toCopyText(chapterName))
                    }
                    kActionRequestCodeNote -> {
                        val verseIds = versesAdapter.listOfSelectedVerses.keys.toIntArray()
                        binding.root.findNavController()
                            .navigate(VersesFragmentDirections.actionAddEditNote(chapterName, chapterId, verseIds, null))
                    }
                    kActionRequestCodeBookmark -> {

                        val verseNumber = versesAdapter.listOfSelectedVerses.firstKey()
                        val verseId = "$chapterId.$verseNumber"

                        val bookmark = Bookmark(
                            bibleId = prefs.selectedBibleId,
                            chapterId = chapterId,
                            verse = verseId
                        )

                        versesViewModel.saveBookmark(verseId, bookmark)
                    }
                }
            }
        }
    }
}
