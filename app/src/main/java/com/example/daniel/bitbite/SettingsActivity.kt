package com.example.daniel.bitbite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar_settings)
        toolbar_settings.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fragmentManager.beginTransaction().add(R.id.settingsContainer, SettingsFragment()).commit()
    }

    // PreferenceFragment for settings
    class SettingsFragment : PreferenceFragment() {

        private var EMPTY = ""
        private var DEFAULTLOCATION = "DEFAULT"

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }

    }
}