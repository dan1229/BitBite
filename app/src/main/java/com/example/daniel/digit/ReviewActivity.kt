package com.example.daniel.digit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ReviewActivity : AppCompatActivity() {

    var reviews = ArrayList<LocationActivity.Reviews>(5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)


        // Get reviews ArrayList
        // Get bundle
        val bundle = intent.getBundleExtra("myBundle")
        reviews = bundle.getParcelableArrayList<LocationActivity.Reviews>("review_list")
                //as ArrayList<LocationActivity.Reviews>
    }
}
