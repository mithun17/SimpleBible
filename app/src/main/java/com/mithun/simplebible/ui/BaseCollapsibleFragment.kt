package com.mithun.simplebible.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mithun.simplebible.R
import com.mithun.simplebible.utilities.gone
import com.mithun.simplebible.utilities.visible
import kotlin.math.abs

/**
 * Fragments that use a collapsible toolbar should extend this Base class
 */
open class BaseCollapsibleFragment : Fragment() {

    protected lateinit var toolbar: Toolbar
    protected lateinit var toolbarTextView: TextView
    protected lateinit var collapsingToolbar: CollapsingToolbarLayout
    protected lateinit var appBarLayout: AppBarLayout
    protected var toolbarTitle: String = ""
    protected var fabSelection: FloatingActionButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar = view.findViewById(R.id.toolbar)
        toolbarTextView = view.findViewById(R.id.tvToolbar)
        collapsingToolbar = view.findViewById(R.id.ctbAppBar)
        fabSelection = view.findViewById(R.id.fabSelectBook)
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)
        toolbarTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(view.context, R.drawable.ic_arrow_drop_down_24), null)

        toolbarTextView.gone
        appBarLayout = view.findViewById(R.id.collapsible_toolbar)
        appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                    //  Collapsed
                    // set navigation icon colors
                    toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(requireContext(), R.color.primaryText), PorterDuff.Mode.SRC_ATOP)
                    collapsingToolbar.title = null
                    toolbarTextView.visible
                    fabSelection?.gone
                } else {
                    // Expanded
                    toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    toolbarTextView.gone
                    collapsingToolbar.title = toolbarTitle
                    fabSelection?.visible
                }
            }
        )

        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(toolbar)
            toolbarTextView.text = supportActionBar?.title
            supportActionBar?.title = null
        }
    }

    protected fun setTitle(title: String) {
        toolbarTitle = title
        toolbarTextView.text = title
        collapsingToolbar.title = title
    }

    protected fun setSelectionClickListener(callback: () -> Unit) {
        toolbarTextView.setOnClickListener {
            callback.invoke()
        }
    }

    override fun onResume() {
        super.onResume()
        appBarLayout.visible
    }

    override fun onStop() {
        super.onStop()
        appBarLayout.gone
    }
}
