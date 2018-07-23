package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_more_info_card.view.*
import org.jetbrains.anko.act
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MoreInfoCard : Fragment() {

    /** Variables **/
    private var listener: MoreInfoCard.OnFragmentInteractionListener? = null
    var reviews = ArrayList<Reviews>(5)
    lateinit var detailsResponse : DetailsResponse
    var user = BaseActivity.User(0.0, 0.0)
    private lateinit var place : Place
    private  var favorites = false
    private var distance = ""
    private var duration = ""
    var address = ""
    var website = ""
    var phone = ""
    var index = 0


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments!!.getParcelable("PLACE")
        favorites = arguments!!.getBoolean("FAVE")
        distance = arguments!!.getString("DISTANCE")
        duration = arguments!!.getString("DURATION")
        user = arguments!!.getParcelable("USER")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_more_info_card, container, false)

        // Place Details call and populate card
        doAsync {
            detailsResponse = callDetailsApi(act, place) as DetailsResponse

            // Store important info
            website = detailsResponse.result.website
            phone = detailsResponse.result.formatted_phone_number
            address = detailsResponse.result.formatted_address

            uiThread { // Populate Location card
                updateMoreInfoCard(view)
            }
        }

        // Setup OtherLocation Fragments
        if(place.duplicates.isNotEmpty()) {
            setupOtherLocation(view)
        }

        /**====================================================================================================**/
        /** On Click Listeners **/

        // Set on click listener for Reviews -> ReviewActivity.kt
        view.moreinfocard_layout_reviews.setOnClickListener{ // Go to ReviewActivity.kt
            if(reviews.isNotEmpty())
                listener!!.onReviewCardInteraction(reviews, place)
        }

        // Set on click listener for Website -> Web Browser
        view.moreinfocard_layout_website.setOnClickListener {
            if(website != "") { // Open website in browser
                val uris = Uri.parse(website)
                val intents = Intent(Intent.ACTION_VIEW, uris)
                val bundle = Bundle()
                bundle.putBoolean("new_window", true)
                intents.putExtras(bundle)
                this.startActivity(intents)
            }
        }

        // Set on click listener for Phone Number -> Dialer
        view.moreinfocard_layout_phone.setOnClickListener {
            if (phone != "") { // Opens dialer with phone number
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel: $phone")
                startActivity(intent)
            }
        }

        // Set on click listener for Address -> Google Maps
        view.moreinfocard_layout_address.setOnClickListener {
            place.openMapsPage(act)
        }

        // Set on click listener for Favorites -> Add to favorites
        view.moreinfocard_layout_favorites.setOnClickListener {
            if(!favorites) { // Not in favorites - add
                toast("Added ${place.name} to your Favorites!")
            } else { // In favorites - remove
                toast("Removed ${place.name} from your Favorites!")
            }

            // Update view and send info to LocationActivity
            favorites = !favorites
            updateFavorites(act, view.moreinfocard_text_favorites, view.moreinfocard_icon_favorites, favorites)
            listener!!.fragmentFavoritesChanged(favorites)
        }

        // Set on click listener for Directions Button -> Google Maps
        view.moreinfocard_button_directions.setOnClickListener{
            directionsWarning(place, activity)
        }

        // Set on click listener for Share Button -> Share Menu
        view.moreinfocard_button_share.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey, look what I found using BitBite (https://BitBite.app) :\n\n" +
                            "${place.name}\n" +
                            detailsResponse.result.website)
            startActivity(shareIntent)
        }

        // Set on click listener for Call Button -> Dialer
        view.moreinfocard_button_call.setOnClickListener {
            if (phone != "") { // Opens dialer with phone number
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel: $phone")
                startActivity(intent)
            }
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
        fun onReviewCardInteraction(reviews: ArrayList<Reviews>, place: Place)
    }

    /** newInstance **/
    companion object {
        fun newInstance(place : Place, favorite : Boolean, distance : String,
                        duration : String, user: BaseActivity.User) : MoreInfoCard {

            val args = Bundle()

            args.putParcelable("PLACE", place)
            args.putBoolean("FAVE", favorite)
            args.putString("DISTANCE", distance)
            args.putString("DURATION", duration)
            args.putParcelable("USER", user)

            val fragment = MoreInfoCard()
            fragment.arguments = args
            return fragment
        }
    }

    /**====================================================================================================**/
    /** Other Locations Fragments Handling **/

    // setupOtherLocation()
    // Sets up OtherLocation section
    private
    fun setupOtherLocation(view: View) {
        // Make scroll view visible
        view.moreinfocard_card_otherlocations.visibility = View.VISIBLE

        // Create fragments and add to layout
        for(i in 0 until place.duplicates.size) {
            val fragment = OtherLocationCard.newInstance(place.duplicates[i], user)
            fragment.dupIndex = i
            fragmentManager.beginTransaction().add(R.id.moreinfocard_otherlocationcard_container, fragment).commit()
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // updateMoreInfoCard()
    // Calls update function for each segment of card
    private
    fun updateMoreInfoCard(view : View) {
        // Update Distance
        updateDistance(view.moreinfocard_text_distance, distance)

        // Update Duration
        updateDuration(view.moreinfocard_text_duration, duration)

        // Update Clock
        updateClock(act, view.moreinfocard_text_clock, place.openNow)

        // Update Website
        updateWebsite(view.moreinfocard_text_website, website)

        // Update Phone
        updatePhone(view.moreinfocard_text_phone, phone)

        // Update Address
        updateAddress(view.moreinfocard_text_address, address)

        // Update Reviews
        updateReviews(view, detailsResponse.result.reviews)

        // Update Favorites
        updateFavorites(act, view.moreinfocard_text_favorites, view.moreinfocard_icon_favorites, favorites)
    }

    // updateReviews()
    private
    fun updateReviews(view : View, reviews : List<Reviews>) {
        if(reviews.isNotEmpty()) { // Reviews array is not empty
            setNonDefaultReview(view, detailsResponse.result.reviews[0])
            copyReviews(detailsResponse.result.reviews)
        }
    }

    /**====================================================================================================**/
    /** Helper Methods **/

    // setNonDefaultReview()
    // Sets non-default review info
    private
    fun setNonDefaultReview(view : View, input : Reviews) {

        // Set Review text
        var s = """"""" + input.text + """""""
        view.moreinfocard_reviews_text.text = s

        // Set Author Name
        s = "— " + ellipsizeText(input.author_name)
        view.moreinfocard_reviews_author.text = s

        view.moreinfocard_reviews_rating.setImageDrawable(ContextCompat.getDrawable(
                act, reviewRatingConversion(input.rating)))
    }

    // copyReviews()
    // Copies reviews array to store locally
    private
    fun copyReviews(input : List<Reviews>?) {
        for(i in 0..(input!!.size - 1))
            reviews.add(input[i])
    }
}
