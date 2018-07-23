package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_bottom_card.view.*
import org.jetbrains.anko.act
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class BottomCard : Fragment() {

    /** Variables **/
    var index = 0
    var favorites = false
    var distance = ""
    var duration = ""
    private lateinit var place : Place
    lateinit var user: BaseActivity.User
    private var listener: BottomCard.OnFragmentInteractionListener? = null


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments!!.getParcelable("PLACE")
        user = arguments!!.getParcelable("USER")
        distance = arguments!!.getString("DISTANCE")
        duration = arguments!!.getString("DURATION")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bottom_card, container, false)

        // Check if in favorites
        favorites = favoritesContains(act, place.placeID)

        // Update card
        updateCard(view)

        /**====================================================================================================**/
        /** On Click Listeners **/

        // Set on click listener for Favorites -> Add to favorites
        view.bottomcard_layout_favorite.setOnClickListener {
            if(!favorites) { // Not in favorites - add
                toast("Added ${place.name} to your Favorites!")
            } else { // In favorites - remove
                toast("Removed ${place.name} from your Favorites!")
            }

            // Update view and send info to LocationActivity
            favorites = !favorites
            updateFavorites(act, view.bottomcard_text_favorite, view.bottomcard_icon_favorite, favorites)
            listener!!.fragmentFavoritesChanged(favorites)
        }

        // Set on click listener for More Info Button -> adds fragment
        view.bottomcard_button_moreinfo.setOnClickListener {
            listener!!.addMoreInfoCard()
        }

        // Set on click listener for Directions Button -> Google Maps
        view.bottomcard_button_directions.setOnClickListener{
            directionsWarning(place, activity)
        }

        return view
    }

    /**====================================================================================================**/
    /** Life Cycle Methods **/

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
        fun fragmentFavoritesChanged(fave: Boolean)
        fun addMoreInfoCard()
        fun distanceCalled(dist: String, dur: String)
    }

    /** newInstance **/
    companion object {
        fun newInstance(place : Place, user : BaseActivity.User,
                        distance: String = "", duration: String = ""): BottomCard {
            val args = Bundle()

            args.putParcelable("PLACE", place)
            args.putParcelable("USER", user)
            args.putString("DISTANCE", distance)
            args.putString("DURATION", duration)

            val fragment = BottomCard()
            fragment.arguments = args
            return fragment
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // locationUpdates()
    // Updates available card fields before Matrix API call
    private
    fun updateCard(view : View) {
        // Update favorites section
        updateFavorites(act, view.bottomcard_text_favorite, view.bottomcard_icon_favorite, favorites)

        // Update clock section
        updateClock(act, view.bottomcard_text_clock, place.openNow)

        // Update distance and duration sections
        if(distance == "" && distance == "") {
            callMatrixApi(view)
        } else {
            updateDistance(view.bottomcard_text_distance, distance)
            updateDuration(view.bottomcard_text_duration, duration)
        }
    }


    // callMatrixApi()
    // Calls Google Matrix API
    private
    fun callMatrixApi(view: View) {
        doAsync {
            val pair = callDistanceApi(act, user.lat, user.lng, place.placeID)
            distance = pair.first
            duration = pair.second
            uiThread {
                updateDistance(view.bottomcard_text_distance, distance)
                updateDuration(view.bottomcard_text_duration, duration)
                listener!!.distanceCalled(distance, duration)
            }
        }
    }


} /** END CLASS BottomCard.kt **/
