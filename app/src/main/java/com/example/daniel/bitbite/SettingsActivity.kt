package com.example.daniel.bitbite

import android.os.Bundle
import android.preference.PreferenceFragment
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.appbar_standard.view.*
import org.jetbrains.anko.act


class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Setup Toolbar
        toolbarBuilderUpNavLabl(settings_toolbar.toolbar, "Settings")

        fragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsFragment()).commit()

    }

    // PreferenceFragment for settings
    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)

            findPreference("clear_favorites").setOnPreferenceClickListener {
                dialogConfirmClearFavorites(act)
                true
            }
        }
    }
}