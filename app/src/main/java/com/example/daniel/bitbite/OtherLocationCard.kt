package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_other_location_card.view.*
import org.jetbrains.anko.act
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class OtherLocationCard : Fragment() {

    /** Variables **/
    var dupIndex = 0
    var distance = Pair("", "")
    private lateinit var place : Place
    var user = BaseActivity.User(0.0, 0.0)
    private var listener: OtherLocationCard.OnFragmentInteractionListener? = null

    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments!!.getParcelable("PLACE")
        user = arguments!!.getParcelable("USER")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_other_location_card, container, false)

        // Populate card
        updateOtherLocationCard(view)

        // Set on click listener for card
        view.otherlocation_card.setOnClickListener {
            listener!!.otherLocationFragmentSelected(dupIndex, distance.first, distance.second)
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
        fun otherLocationFragmentSelected(dupIndex: Int, dist: String, dur: String)
    }

    companion object {
        fun newInstance(place: Place, user: BaseActivity.User): OtherLocationCard {

            val args = Bundle()

            args.putParcelable("PLACE", place)
            args.putParcelable("USER", user)

            val fragment = OtherLocationCard()
            fragment.arguments = args
            return fragment
        }
    }

    /**====================================================================================================**/
    /** Upadator Methods **/

    // updateOtherLocationCard()
    // Populates OtherLocationCard
    private
    fun updateOtherLocationCard(view: View) {
        // Update name
        view.otherlocation_text_name.text = ellipsizeText(place.name, 25)

        // Update photo
        updatePhoto(act, place, view.otherlocation_image)

        // Update clock
        updateClock(act, view.otherlocation_text_clock, place.openNow)

        // If distance is empty, call API
        doAsync {
            if(distance.first == "") {
                distance = callDistanceApi(act, user.lat, user.lng, place.placeID)
            }

            uiThread {
                // Update duration
                updateDuration(view.otherlocation_text_duration, distance.second)
            }
        }
    }
}
