package com.example.daniel.digit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.*
import com.pavelsikun.seekbarpreference.SeekBarPreference

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, SettingsFragment()).commit()
        }
    }

    // PreferenceFragment for settings
    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }
    }
}
