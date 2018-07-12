package com.example.daniel.bitbite

import android.app.ActivityOptions
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.util.Pair
import android.view.*
import com.example.daniel.bitbite.R.layout.fragment_results_card
import kotlinx.android.synthetic.main.fragment_results_card.*
import kotlinx.android.synthetic.main.fragment_results_card.view.*
import org.jetbrains.anko.act
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.uiThread
import javax.xml.transform.Result


class ResultsCard : Fragment(){//, GestureDetector.OnGestureListener {

    private lateinit var place : Place
    private lateinit var user : MainActivity.User
//    private lateinit var card : ResultsCard
//    var gDetector: GestureDetectorCompat? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        this.gDetector = GestureDetectorCompat(activity, this)

        place = arguments.getParcelable("place")
        user = arguments.getParcelable("user")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_results_card, container, false)

//        card = this

        // Populate card
        updateCard(view)

        // Set on click listener for card
        view.results_card.setOnClickListener {
            goToLocation()
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragInteraction(uri: Uri)  {

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param place - Place object for fragment
         * @return A new instance of fragment ResultsCard.
         */
        fun newInstance(place : Place, user : MainActivity.User) : ResultsCard {
            val args = Bundle()
            args.putParcelable("place", place)
            args.putParcelable("user", user)
            val fragment = ResultsCard()
            fragment.arguments = args
            return fragment
        }
    }

    /**====================================================================================================**/
    /** Gestures Interface Overrides **/
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        this.gDetector?.onTouchEvent(event)
//        // Be sure to call the superclass implementation
//        return super.onTouchEvent(event)
//    }
//
//    // onFling()
//    // Detects "fling" gesture events
//    override fun onFling(event1: MotionEvent, event2: MotionEvent,
//                         velocityX: Float, velocityY: Float): Boolean {
//
//        Log.d("GESTURE", "fling")
//        getActivity().getFragmentManager().beginTransaction().remove(card).commit()
//        return true
//    }
//
//    override fun onDown(p0: MotionEvent?): Boolean {
//        return false
//    }
//
//    override fun onLongPress(p0: MotionEvent?) {
//        //
//    }
//
//    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
//        return false
//    }
//
//    override fun onShowPress(p0: MotionEvent?) {
//        Log.d("GESTURE", "fling")
//
//    }
//
//    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
//        return false
//    }

    /**====================================================================================================**/
    /** Intent Makers **/

    // goToLocation()
    // Goes to LocationActivity, calls Place Details API
    private
    fun goToLocation() {
        // Create Intent
        val intent = Intent(activity, LocationActivity::class.java)
        intent.putExtra("place", place)
        intent.putExtra("user", user)

        // Check Android version for animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    Pair.create<View, String>(results_card, "top_card"),
                    Pair.create<View, String>(results_image, "top_card_image"),
                    Pair.create<View, String>(results_name, "top_card_name"))
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
    fun updateCard(view : View) {
        view.results_name.text = ellipsizeText(place.name, 25)
        view.results_price.text = place.priceConversion()
        view.results_rating.setImageDrawable(getDrawable(act, place.ratingConversion()))
        view.results_description.text = place.fixDescription()
    }
}
