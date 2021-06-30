package com.mithun.simplebible.ui.book

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.mithun.simplebible.R
import com.mithun.simplebible.data.database.model.Bookmark
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.databinding.FragmentChapterVersesBinding
import com.mithun.simplebible.ui.BaseCollapsibleFragment
import com.mithun.simplebible.ui.adapter.VersesAdapter
import com.mithun.simplebible.ui.dialog.Action
import com.mithun.simplebible.ui.dialog.ActionsBottomSheet
import com.mithun.simplebible.utilities.CommonUtils.copyToClipboard
import com.mithun.simplebible.utilities.CommonUtils.showShareIntent
import com.mithun.simplebible.utilities.ExtensionUtils.toCopyText
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.gone
import com.mithun.simplebible.utilities.visible
import com.mithun.simplebible.viewmodels.VersesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VersesFragment : BaseCollapsibleFragment(), ActionsBottomSheet.ActionPickerListener {

    // Action sheet request codes
    private val kActionRequestCodeShare = 1
    private val kActionRequestCodeCopy = 2
    private val kActionRequestCodeNote = 3
    private val kActionRequestCodeBookmark = 4
    private val kActionRequestCodeImage = 5

    @Inject
    lateinit var prefs: Prefs

    private val versesViewModel: VersesViewModel by viewModels()

    private var _binding: FragmentChapterVersesBinding? = null
    private val binding get() = _binding!!
    private var lookupJob: Job? = null

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

    private lateinit var chapterId: String
    private lateinit var chapterName: String

    private lateinit var menu: Menu
    private lateinit var bottomNav: BottomNavigationView

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
        bottomNav = requireActivity().findViewById(R.id.nav_view)
        binding.rvVerses.adapter = versesAdapter

        chapterId = args.chapterId ?: prefs.lastReadChapter
        chapterName = args.chapterFullName ?: ""

        setTitle(chapterName)
        initUI()
        initViewModelAndSetCollectors()
        versesViewModel.getVerses(prefs.selectedBibleVersionId, chapterId)
    }

    private fun initUI() {
        binding.fabSelectBook.setOnClickListener {
            navigateToBookSelection()
        }
        setSelectionClickListener {
            navigateToBookSelection()
        }
        val tv = TypedValue()

        var actionBarHeight = 0f
        if (requireActivity().theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
                .toFloat()
        }

        val parent: ViewGroup = requireActivity().findViewById(R.id.container)
        val transitionDown: Transition = Slide(Gravity.BOTTOM)

        binding.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY) {
                    // Scroll DOWN
                    transitionDown.setDuration(600)
                    transitionDown.addTarget(bottomNav)

                    TransitionManager.beginDelayedTransition(parent, transitionDown)
                    bottomNav.gone
                }
                if (scrollY < oldScrollY) {
                    // Scroll UP
                    transitionDown.setDuration(300)
                    transitionDown.addTarget(bottomNav)

                    TransitionManager.beginDelayedTransition(parent, transitionDown)
                    bottomNav.visible
                }
            }
        )
    }

    private fun navigateToBookSelection() {
        findNavController().navigate(
            VersesFragmentDirections.actionNavigationChapterVersesToNavigationBooks(
                chapterId.split(".").first()
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.menu_overflow, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_version).title = prefs.selectedBibleVersionName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_version -> {
                findNavController().navigate(R.id.action_global_navigation_filter)
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
                    actionList.addAll(
                        listOf(
                            Action(
                                kActionRequestCodeBookmark,
                                R.drawable.ic_bookmark,
                                getString(R.string.actionSheetBookmarkLabel)
                            ),
                            Action(
                                kActionRequestCodeImage,
                                R.drawable.ic_image,
                                getString(R.string.actionSheetImageLabel)
                            )
                        )
                    )
                }

                ActionsBottomSheet
                    .with(it, kRequestCodeActionSheet, this@VersesFragment)
                    .action(actionList)
                    .show()
            }
        }

        lookupJob?.cancel()
        lookupJob = lifecycleScope.launch {
            versesViewModel.verses.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Loading message
                        binding.pbDialog.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        val listOfVerses = resource.data
                        // success or error data
                        listOfVerses?.let { verses ->
                            if (verses.isNotEmpty()) {
                                chapterName = verses.first().reference
                                setTitle(chapterName)
                            }
                        }
                        versesAdapter.submitList(listOfVerses)
                        binding.pbDialog.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        // error
                        val errorMessage = resource.message
                        Snackbar.make(binding.root, errorMessage ?: "", Snackbar.LENGTH_LONG).show()
                        binding.pbDialog.visibility = View.GONE
                    }
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
                            .navigate(VersesFragmentDirections.actionAddEditNote(0L, chapterName, chapterId, verseIds, null))
                    }
                    kActionRequestCodeBookmark -> {

                        val verseNumber = versesAdapter.listOfSelectedVerses.firstKey()
                        val verseId = "$chapterId.$verseNumber"

                        val bookmark = Bookmark(
                            bibleId = prefs.selectedBibleVersionId,
                            chapterId = chapterId,
                            verseId = verseId
                        )

                        versesViewModel.saveBookmark(verseId, bookmark)
                        versesAdapter.setBookmarked(verseNumber)
                    }
                    kActionRequestCodeImage -> {
                        val verseNumber = versesAdapter.listOfSelectedVerses.firstKey()
                        val verseText = versesAdapter.listOfSelectedVerses[verseNumber]
                        val verseId = "$chapterId.$verseNumber"

                        verseText?.let {
                            findNavController().navigate(VersesFragmentDirections.actionImageSelect(verseText, verseId))
                        }
                    }
                }
            }
        }
    }
}
