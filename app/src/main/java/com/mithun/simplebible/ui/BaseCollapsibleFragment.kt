package com.mithun.simplebible.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mithun.simplebible.R
import kotlin.math.abs

/**
 * Fragments that use a collapsible toolbar should extend this Base class
 */
open class BaseCollapsibleFragment : Fragment() {

    protected lateinit var toolbar: Toolbar
    protected lateinit var collapsingToolbar: CollapsingToolbarLayout
    protected lateinit var appBarLayout: AppBarLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar = view.findViewById(R.id.toolbar)
        collapsingToolbar = view.findViewById(R.id.ctbAppBar)
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)

        appBarLayout = view.findViewById(R.id.collapsible_toolbar)
        appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                    //  Collapsed
                    // set navigation icon colors
                    toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(requireContext(), R.color.primaryText), PorterDuff.Mode.SRC_ATOP)
                } else {
                    // Expanded
                    toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                }
            }
        )

        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(toolbar)
        }
    }
}
