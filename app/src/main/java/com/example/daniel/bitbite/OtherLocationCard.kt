package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
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
        updateCard(view)

        // Set on click listener for card
        view.otherlocation_card.setOnClickListener {
            listener!!.otherLocationFragmentSelected(place, distance)
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
        fun otherLocationFragmentSelected(place: Place, distance: Pair<String, String>)
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

    // updateCard()
    // Populates OtherLocationCard
    private
    fun updateCard(view: View) {
        view.otherlocation_text_name.text = ellipsizeText(place.name, 25)
        place.placePhotoCall(act, view.otherlocation_image)
        updateClock(view)
        updateDuration(view)
    }

    // updateClock()
    // Updates clock section
    private
    fun updateClock(view: View) {
        val bool = place.openNow

        if(bool){
            view.otherlocation_text_clock.text = getString(R.string.open)
            view.otherlocation_text_clock.setTextColor(ContextCompat.getColor(act, R.color.green))
        } else {
            view.otherlocation_text_clock.text = getString(R.string.closed)
            view.otherlocation_text_clock.setTextColor(ContextCompat.getColor(act, R.color.red))
        }
    }

    // updateDuration()
    // Gets duration and distance from Matrix API and updates section
    private
    fun updateDuration(view: View) {
        doAsync {
            distance = callDistanceApi(act, user.lat, user.lng, place.placeID)

            uiThread {
                if(distance.second != "")
                    view.otherlocation_text_duration.text = distance.second
            }
        }
    }

}
