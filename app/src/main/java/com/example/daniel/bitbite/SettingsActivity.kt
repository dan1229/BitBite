package com.example.daniel.bitbite

import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.*
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.settingsContainer, SettingsFragment()).commit()
        }
        //fragmentManager.beginTransaction().add(R.id.settingsContainer, SettingsFragment()).commit()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if(p1 == "default_price"){
            priceBar.progress = p0!!.getInt(p1, 5)
        }
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