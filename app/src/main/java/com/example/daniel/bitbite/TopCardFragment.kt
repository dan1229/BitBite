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
import org.jetbrains.anko.support.v4.act



/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TopCardFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TopCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TopCardFragment : Fragment() {

    var height = 0
    private lateinit var place : Place
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = arguments.getParcelable("place")
        height = arguments.getInt("height")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_top_card, container, false)

        // Populate card
        updateCard(view)

        // Use height passed
        if(height != 0) {
            view.topcard_layout.layoutParams.height = height
            Log.d("HEIGHT", height.toString())
        }

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TopCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(place : Place, height : Int = 0) : TopCardFragment {
            val args = Bundle()
            args.putParcelable("place", place)
            args.putInt("height", height)
            val fragment = TopCardFragment()
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
