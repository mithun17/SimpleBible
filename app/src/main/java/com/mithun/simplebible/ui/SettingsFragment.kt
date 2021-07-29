package com.mithun.simplebible.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.mithun.simplebible.R

class SettingsFragment : PreferenceFragmentCompat() {

    private var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        initSettings()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun initSettings() {
        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                getString(R.string.preference_theme_key) -> {
                    // Day or Night mode
                    val nightModeToggle: SwitchPreferenceCompat? = findPreference(getString(R.string.preference_theme_key))
                    nightModeToggle?.let {
                        if (it.isChecked) {
                            (requireActivity() as AppCompatActivity).delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            (requireActivity() as AppCompatActivity).delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                        }
                    }
                }
            }
        }

        val imageSharedPreferences: Preference? = findPreference(getString(R.string.preference_image_share_key))
        imageSharedPreferences?.setOnPreferenceClickListener {
            // do nothing
            true
        }
    }
}
