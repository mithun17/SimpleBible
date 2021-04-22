package com.mithun.simplebible.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.mithun.simplebible.R

open class BaseFragment : Fragment() {
    protected lateinit var toolbar: Toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setBackgroundColor(ContextCompat.getColor(view.context, R.color.white))
        toolbar.setupWithNavController(navController, appBarConfiguration)

        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(toolbar)
        }
    }
}
