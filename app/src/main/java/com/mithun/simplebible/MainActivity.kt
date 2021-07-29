package com.mithun.simplebible

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.gone
import com.mithun.simplebible.utilities.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        navView.setupWithNavController(navController)
        navView.setOnNavigationItemReselectedListener {
            // empty. Prevents recreating fragment when same menu item is clicked multiple times
        }
        initUI(navView)
    }

    private fun initUI(navView: BottomNavigationView) {
        initMode()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_add_edit_note,
                R.id.navigation_filter,
                R.id.navigation_image_select,
                R.id.navigation_image_share,
                R.id.navigation_image_edit,
                R.id.navigation_book_select -> {
                    // hide the bottom nav bar if it hits any of these destinations
                    navView.gone
                }
                else -> {
                    navView.visible
                }
            }
        }
    }

    // initialize with the correct mode
    private fun initMode() {
        delegate.localNightMode = if (prefs.isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    }
}
