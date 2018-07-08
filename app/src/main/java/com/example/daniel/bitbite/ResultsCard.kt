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
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.daniel.bitbite.R.layout.fragment_results_card
import kotlinx.android.synthetic.main.fragment_results_card.*
import kotlinx.android.synthetic.main.fragment_results_card.view.*
import org.jetbrains.anko.act
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.uiThread


class ResultsCard : Fragment() {
    private lateinit var place : Place
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments.getParcelable("place")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_results_card, container, false)

        // Populate card
        updateCard(view)

        // Set on click listener for card
        view.results_card.setOnClickListener {
            goToLocation()
        }

        return view

    }


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
        // TODO: Rename and change types and number of parameters
        fun newInstance(place : Place) : ResultsCard {
//            this.place = place
//            val args = Bundle()
//            args.putParcelable("PLACE", place)
//            val fragment = ResultsCard()
//            fragment.arguments = args
//            return fragment

            val args = Bundle()
            args.putParcelable("place", place)
            val fragment = ResultsCard()
            fragment.arguments = args
            return fragment
        }
    }

    // goToLocation()
    // Goes to LocationActivity, calls Place Details API
    private
    fun goToLocation() {
        // Create Intent
        val intent = Intent(activity, LocationActivity::class.java)
        intent.putExtra("place", place)

        // Check Android version for animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    Pair.create<View, String>(results_card, "card"),
                    Pair.create<View, String>(results_image, "place_image"),
                    Pair.create<View, String>(results_name, "place_name"))
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

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
