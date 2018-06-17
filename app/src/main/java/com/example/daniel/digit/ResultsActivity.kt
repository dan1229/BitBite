package com.example.daniel.digit

import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_results.*

class ResultsActivity : AppCompatActivity() {

    var places = ArrayList<Place>()
    var listSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        toolbar.setTitle("Results")
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get places ArrayList and size from MainActivity
        val bundle = intent.extras
        //places = bundle.get("EXTRA_PLACES_LIST")
        //listSize = bundle.getInt("EXTRA_LIST_SIZE")


        // ArrayList index
        var index = 0

        // Update cards in activity_main.xml
        updateCard(1, index)
        index = nextIndex(index)
        updateCard(2, index)
        index = nextIndex(index)
        updateCard(3, index)
        index = nextIndex(index)

        displayNext3.setOnClickListener{
            if (index > listSize) { // Index in bounds, update cards
                for (i in 1..3) {
                    updateCard(i, index)
                    index = nextIndex(index)
                }
            }
            else { // Index out of bounds, display error message
                testDialog("Out of options. Please go back or search again.")
            }
        }

        // Set on click listeners for cards
    }

    // Calls update function for each segment of card
    fun updateCard(card : Int, index : Int) {
        var i = index
        if(index > listSize) { // Check if index is in bounds
            i = -1
        }
        updateName(card, i)
        updatePrice(card, i)
        updatePhoto(card, i)
        updateRating(card, i)
        updateDescription(card, i)
    }

    // Updates name on the card
    fun updateName(card : Int, index : Int) {
        val textView = getNameView(card)
        if(index >= 0){ // In bounds
            textView.setText(places[index].get_name())
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_name)
        }
    }

    // Updates price on the card
    fun updatePrice(card : Int, index : Int) {
        var textView = getPriceView(card)
        if(index >= 0){ // In bounds
            textView.setText(places[index].get_price())
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_price)
        }
    }


    // Updates phto on the card
    fun updatePhoto(card : Int, index : Int) {
        var view = getImageView(card)
        if(index < 0){ // Out of bounds, default val
            view.setImageResource(R.drawable.main_activity_logo)
        } else{ // In bounds

        }
    }

    // Updates rating on the card
    fun updateRating(card : Int, index : Int) {
        var textView = getRatingView(card)
        if(index >= 0){ // In bounds
            textView.setText(places[index].get_rating())
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_rating)
        }
    }

    // Updates description on the card
    fun updateDescription(card : Int, index : Int) {
        var textView = getDescriptionView(card)
        if(index >= 0){ // In bounds
            textView.setText(places[index].get_description())
        } else{ // Out of bounds, default val
            textView.setText(R.string.default_description)
        }
    }

    // "Iterator" function for index
    private
    fun nextIndex(n : Int) : Int{
        var i = n
        return ++i
    }

    // Test dialog - ya know for testing stuff
    private
    fun testDialog(s : String) {
        // TEST DIALOG
        val builder = AlertDialog.Builder(this@ResultsActivity)
        builder.setMessage(s)
        val dialog: AlertDialog = builder.create()
        dialog.show()
        // TEST DIALOG
    }

    // Gets TextView for name view based on card number
    private
    fun getNameView(card : Int) : TextView {
        var view = findViewById<TextView>(R.id.name1) as TextView
        when(card) {
            1 -> view = findViewById<TextView>(R.id.name1) as TextView
            2 -> view = findViewById<TextView>(R.id.name2) as TextView
            3 -> view = findViewById<TextView>(R.id.name3) as TextView
        }
        return view
    }

    // Gets TextView for price view based on card number
    private
    fun getPriceView(card : Int) : TextView {
        var view = findViewById<TextView>(R.id.price1) as TextView
        when(card) {
            1 -> view = findViewById<TextView>(R.id.price1) as TextView
            2 -> view = findViewById<TextView>(R.id.price2) as TextView
            3 -> view = findViewById<TextView>(R.id.price3) as TextView
        }
        return view
    }

    // Gets ImageView for photo view based on card number
    private
    fun getImageView(card : Int) : ImageView {
        var view = findViewById<ImageView>(R.id.image1) as ImageView
        when(card) {
            1 -> view = findViewById<ImageView>(R.id.image1) as ImageView
            2 -> view = findViewById<ImageView>(R.id.image2) as ImageView
            3 -> view = findViewById<ImageView>(R.id.image3) as ImageView
        }
        return view
    }

    // Gets TextView for rating view based on card number
    private
    fun getRatingView(card : Int) : TextView {
        var view = findViewById<TextView>(R.id.rating1) as TextView
        when(card) {
            1 -> view = findViewById<TextView>(R.id.rating1) as TextView
            2 -> view = findViewById<TextView>(R.id.rating2) as TextView
            3 -> view = findViewById<TextView>(R.id.rating3) as TextView
        }
        return view
    }

    // Gets TextView for descritpion view based on card number
    private
    fun getDescriptionView(card : Int) : TextView {
        var view = findViewById<TextView>(R.id.description1) as TextView
        when(card) {
            1 -> view = findViewById<TextView>(R.id.description1) as TextView
            2 -> view = findViewById<TextView>(R.id.description2) as TextView
            3 -> view = findViewById<TextView>(R.id.description3) as TextView
        }
        return view
    }
}
