package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
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

        // Matrix API call
        if(distance == "" && distance == "")
            callMatrixApi(view)

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
            updateFavorites(view)
            listener!!.fragmentFavoritesChanged(favorites)
        }

        // Set on click listener for More Info Button -> makes views visible
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

    // callMatrixApi()
    // Calls Google Matrix API
    private
    fun callMatrixApi(view: View) {
        doAsync {
            val pair = callDistanceApi(act, user.lat, user.lng, place.placeID)
            distance = pair.first
            duration = pair.second
            listener!!.distanceCalled(distance, duration)

            uiThread { // Populate Location card
                distanceUpdates(view, distance, duration)
            }
        }
    }

    // locationUpdates()
    // Updates available card fields before Matrix API call
    private
    fun updateCard(view : View) {
        updateFavorites(view)
        updateClock(view, place.openNow)
    }

    // distanceUpdates()
    // Updates fields with info. from Matrix API call
    private
    fun distanceUpdates(view : View, distance : String, duration : String) {
        if(distance != "")
            view.bottomcard_text_distance.text = distance
        if(duration != "")
            view.bottomcard_text_duration.text = duration
    }

    // updateClock()
    private
    fun updateClock(view : View, bool : Boolean) {
        if(bool){
            view.bottomcard_text_clock.text = getString(R.string.open)
            view.bottomcard_text_clock.setTextColor(ContextCompat.getColor(act, R.color.green))
        } else {
            view.bottomcard_text_clock.text = getString(R.string.closed)
            view.bottomcard_text_clock.setTextColor(ContextCompat.getColor(act, R.color.red))
        }
    }

    // updateFavorites()
    private
    fun updateFavorites(view : View) {
        val txtView = view.bottomcard_text_favorite

        if(!favorites) { // Not in favorites
            view.bottomcard_icon_favorite.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.favorites_icon))
            txtView.text = getString(R.string.default_favorites)
            txtView.setTextColor(ContextCompat.getColor(act, R.color.text_primary))
        } else { // Already in favorites
            view.bottomcard_icon_favorite.setImageDrawable(ContextCompat.getDrawable(act, R.drawable.favorites_filled_icon))
            txtView.text = getString(R.string.default_already_favorited)
            txtView.setTextColor(ContextCompat.getColor(act, R.color.gold))
        }
    }
}
