package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat.getDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_results_card.view.*
import org.jetbrains.anko.act


class ResultsCard : Fragment(){

    var index = 0
    lateinit var place : Place
    private lateinit var user : BaseActivity.User
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments.getParcelable("PLACE")
        user = arguments.getParcelable("USER")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_results_card, container, false)

        // Populate card
        updateCard(view)

        // Update photo
        updatePhoto(view)

        // Set on click listener for card
        view.results_card.setOnClickListener {
            listener!!.resultsCardSelected(place)
        }

        return view
    }

    /**====================================================================================================**/
    /** Mandatory Methods **/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun resultsCardSelected(place: Place)  {}
    }

    companion object {
        fun newInstance(place : Place, user : BaseActivity.User) : ResultsCard {
            val args = Bundle()
            args.putParcelable("PLACE", place)
            args.putParcelable("USER", user)
            val fragment = ResultsCard()
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
        view.results_name.text = ellipsizeText(place.name, 25)
        view.results_price.text = place.priceConversion()
        view.results_rating.setImageDrawable(getDrawable(act, place.ratingConversion()))
        view.results_description.text = place.fixDescription()
    }

    // updatePhoto()
    // Updates photo
    private
    fun updatePhoto(view : View) {
        place.placePhotoCall(act, view.results_image)
    }
}
