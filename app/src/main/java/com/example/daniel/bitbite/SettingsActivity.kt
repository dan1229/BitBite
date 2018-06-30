package com.example.daniel.bitbite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction().add(android.R.id.content, SettingsFragment()).commit()
        }
    }

    // PreferenceFragment for settings
    class SettingsFragment : PreferenceFragment() {

        private var EMPTY = ""
        private var DEFAULTLOCATION = "DEFAULT"

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)


//            val view = LayoutInflater.from(activity).inflate(R.id.activity_settings, null)
//
//            // Check if default location exists
//            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
//            if (prefs.getString(DEFAULTLOCATION, EMPTY) != EMPTY) { // If default location exists, populate EditText
//                Log.d("LOCATION", "DEFAULT LOCATION EXISTS")
//                DEFAULTLOCATION = prefs.getString(DEFAULTLOCATION, EMPTY)
//
//            }
//            else { // If it doesn't exist, leave blank
//                Log.d("LOCATION", "DEFAULT LOCATION DOES NOT EXIST")
//                settingsEditText
//            }
        }

    }
}