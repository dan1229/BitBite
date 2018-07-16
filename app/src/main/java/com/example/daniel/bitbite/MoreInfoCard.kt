package com.example.daniel.bitbite

import android.app.ActivityOptions
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_more_info_card.*
import kotlinx.android.synthetic.main.fragment_more_info_card.view.*
import org.jetbrains.anko.act
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MoreInfoCard.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MoreInfoCard.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MoreInfoCard : Fragment() {

    /** Variables **/
    var reviews = ArrayList<Reviews>(5)
    lateinit var detailsResponse : DetailsResponse
    private lateinit var place : Place
    private  var favorites = false
    private var distance = ""
    private var duration = ""
    private var listener: MoreInfoCard.OnFragmentInteractionListener? = null
    var website = ""
    var phone = ""


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments!!.getParcelable("place")
        favorites = arguments!!.getBoolean("fave")
        distance = arguments!!.getString("distance")
        duration = arguments!!.getString("duration")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_more_info_card, container, false)

        // Place Details call and populate card
        doAsync {
            detailsResponse = callDetailsApi(act, place) as DetailsResponse

            uiThread { // Populate Location card
                updateCard(view, detailsResponse)
            }
        }

        /**====================================================================================================**/
        /** On Click Listeners **/

        // Set on click listener for Reviews -> ReviewActivity.kt
        view.moreinfocard_layout_reviews.setOnClickListener{ // Go to ReviewActivity.kt
            if(reviews.isNotEmpty())
                goToReviews()
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
                favorites = true
            } else { // In favorites - remove
                toast("Removed ${place.name} from your Favorites!")
                favorites = false
            }
            // Update view and send info to LocationActivity
            updateFavorites(view)
            listener!!.onFragmentInteraction(favorites)
        }

        // Set on click listener for Directions Button -> Google Maps
        view.moreinfocard_button_directions.setOnClickListener{
            place.openMapsPage(act)
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
        fun onFragmentInteraction(fave: Boolean)
    }

    /** newInstance **/
    companion object {
        fun newInstance(place : Place, favorite : Boolean,
                        distance : String, duration : String) : MoreInfoCard {
            val args = Bundle()
            args.putParcelable("place", place)
            args.putBoolean("fave", favorite)
            args.putString("distance", distance)
            args.putString("duration", duration)
            val fragment = MoreInfoCard()
            fragment.arguments = args
            return fragment
        }
    }

    /**====================================================================================================**/
    /** Intent Makers **/

    // goToReviews()
    // Creates Intent for Reviews.kt and animates transition
    private
    fun goToReviews() {
        // Create Intent
        val intent = Intent(activity, ReviewActivity::class.java)
        intent.putParcelableArrayListExtra("review_list", reviews) // Pass reviews
        intent.putExtra("place_id", place.placeID) // Pass placeID
        intent.putExtra("name", place.name) // Pass name

        // Check Android version for animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation( activity,
                    Pair.create<View, String>(moreinfocard_layout_reviews, "review_card"),
                    Pair.create<View, String>(moreinfocard_reviews_rating, "review_rating"),
                    Pair.create<View, String>(moreinfocard_reviews_text, "review_text"))
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // updateCard()
    // Calls update function for each segment of card
    private
    fun updateCard(view : View, response : DetailsResponse) {
        updateDistance(view, distance, duration)
        updateClock(view, place.openNow)
        updateFavorites(view)

        // Reliant on Place Details API
        updateWebsite(view, response.result.website)
        updatePhone(view, response.result.formatted_phone_number)
        updateAddress(view, response.result.formatted_address)
        updateReviews(view, response.result.reviews)
    }

    // updateDistance()
    // Updates fields related to distance
    private
    fun updateDistance(view : View, distance : String, duration : String) {
        if(distance != "")
            view.moreinfocard_text_distance.text = distance
        if(duration != "")
            view.moreinfocard_text_duration.text = duration
    }

    // updateClock()
    private
    fun updateClock(view : View, bool : Boolean) {
        if(bool){
            view.moreinfocard_text_clock.text = getString(R.string.open)
            view.moreinfocard_text_clock.setTextColor(ContextCompat.getColor(act, R.color.green))
        } else {
            view.moreinfocard_text_clock.text = getString(R.string.closed)
            view.moreinfocard_text_clock.setTextColor(ContextCompat.getColor(act, R.color.red))
        }
    }

    // updateFavorites()
    private
    fun updateFavorites(view : View) {
        val txtView = view.moreinfocard_text_favorites

        if(!favorites) { // Not in favorites
            view.moreinfocard_icon_favorites.setImageDrawable(ContextCompat.getDrawable(
                    act, R.drawable.favorites_icon))
            txtView.text = getString(R.string.default_favorites)
            txtView.setTextColor(ContextCompat.getColor(act, R.color.text_primary))
        } else { // Already in favorites
            view.moreinfocard_icon_favorites.setImageDrawable(ContextCompat.getDrawable(
                    act, R.drawable.favorites_filled_icon))
            txtView.text = getString(R.string.default_already_favorited)
            txtView.setTextColor(ContextCompat.getColor(act, R.color.gold))
        }
    }

    // updateWebsite()
    private
    fun updateWebsite(view : View, input : String) {
        val txtView = view.moreinfocard_text_website
        if(!input.equals(""))
            txtView.text = input
        else
            txtView.text = resources.getString(R.string.default_website)
        website = input
    }

    // updatePhone()
    private
    fun updatePhone(view : View, input : String) {
        val txtView = view.moreinfocard_text_phone
        if(!input.equals(""))
            txtView.text = input
        else
            txtView.text = resources.getString(R.string.default_phone)
        phone = input
    }

    // updateAddress()
    private
    fun updateAddress(view : View, input : String) {
        if(input != "")
            view.moreinfocard_text_address.text = input
    }

    // updateReviews()
    private
    fun updateReviews(view : View, reviews : List<Reviews>) {
        if(!reviews.isEmpty()) { // Reviews array is not empty
            setNonDefaultReview(view, detailsResponse.result.reviews[0])
            copyReviews(detailsResponse.result.reviews)
        }
        else { // Reviews array empty
            setDefaultReview(view)
        }
    }

    /**====================================================================================================**/
    /** Helper Methods **/

    // setNonDefaultReview()
    // Sets non-default review info
    private
    fun setNonDefaultReview(view : View, input : Reviews) {

        var s = """"""" + input.text + """""""
        view.moreinfocard_reviews_text.text = s

        s = "- " + ellipsizeText(input.author_name)
        view.moreinfocard_reviews_author.text = s

        view.moreinfocard_reviews_rating.setImageDrawable(ContextCompat.getDrawable(
                act, reviewRatingConversion(input.rating)))
    }

    // setDefaultReview()
    // Sets default review info
    private
    fun setDefaultReview(view : View) {
        view.moreinfocard_reviews_text.text = resources.getString(R.string.default_review)
        view.moreinfocard_reviews_author.text = resources.getString(R.string.default_review_author)
        view.moreinfocard_reviews_rating.setImageDrawable(ContextCompat.getDrawable(
                act, R.drawable.default_star))
    }

    // copyReviews()
    // Copies reviews array to store locally
    private
    fun copyReviews(input : List<Reviews>?) {
        for(i in 0..(input!!.size - 1))
            reviews.add(input[i])
    }
}
