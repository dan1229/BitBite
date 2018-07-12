package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_top_card.*
import kotlinx.android.synthetic.main.fragment_top_card.view.*
import org.jetbrains.anko.act


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TopCard.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TopCard.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TopCard : Fragment() {

    /** Variables **/
    private lateinit var place : Place
    private var listener: OnFragmentInteractionListener? = null


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments.getParcelable("place")
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
        fun onFragmentInteraction(uri: Uri)
    }

    /** newInstance **/
    companion object {
        fun newInstance(place : Place) : TopCard {
            val args = Bundle()
            args.putParcelable("place", place)
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
        view.topcard_name.text = ellipsizeText(place.name, 25)
        view.topcard_rating.setImageDrawable(getDrawable(act, place.ratingConversion()))
        view.topcard_description.text = place.fixDescription()
        view.topcard_price.text = place.priceConversion()
        updateOpennow(view, place.openNow)
        updatePhoto(view, place)
    }

    // updateOpennow()
    private
    fun updateOpennow(view : View, bool : Boolean) {
        if(bool){
            view.topcard_open.text = getString(R.string.yes)
            view.topcard_open.setTextColor(ContextCompat.getColor(act, R.color.green))
        } else {
            view.topcard_open.text = getString(R.string.no)
            view.topcard_open.setTextColor(ContextCompat.getColor(act, R.color.red))
        }
    }

    // updatePhoto()
    private
    fun updatePhoto(view : View, place : Place) {
            if(place.photoRef != "DEFAULT")
                place.placePhotoCall(act, view.topcard_image) // Fetch image
            else
                topcard_image.setImageDrawable(ContextCompat.getDrawable( // Set default image
                        act, R.drawable.default_place_image))
    }
}
