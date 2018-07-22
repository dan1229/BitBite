package com.example.daniel.bitbite

import android.app.Fragment
import android.os.Bundle
import android.support.v4.content.ContextCompat.getDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_top_card.view.*
import org.jetbrains.anko.act

class TopCard : Fragment() {

    /** Variables **/
    private lateinit var place : Place


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments.getParcelable("PLACE")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_top_card, container, false)

        // Populate card
        updateCard(view)

        return view
    }

    /**====================================================================================================**/
    /** Mandatory Methods **/

    /** newInstance **/
    companion object {
        fun newInstance(place : Place) : TopCard {
            val args = Bundle()
            args.putParcelable("PLACE", place)
            val fragment = TopCard()
            fragment.arguments = args
            return fragment
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // updateCard()
    // Calls update function for each segment of card
    private
    fun updateCard(view : View) {

        // Update name
        view.topcard_name.text = ellipsizeText(place.name, 25)

        // Update rating
        view.topcard_rating.setImageDrawable(getDrawable(act, place.ratingConversion()))

        // Update description
        view.topcard_description.text = place.fixDescription()

        // Update price
        view.topcard_price.text = place.priceConversion()

        // Update open
        updateOpennow(act, view.topcard_open, place.openNow)

        // Update photo
        updatePhoto(act, place, view.topcard_image)
    }
}
