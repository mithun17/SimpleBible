package com.mithun.simplebible.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.mithun.simplebible.R

/**
 * Base fragment for all screens that use a regular toolbar
 */
open class BaseFragment : Fragment() {
    protected lateinit var toolbar: Toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_chapter_verses, R.id.navigation_notes, R.id.navigation_bookmarks, R.id.navigation_settings_fragment))

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
