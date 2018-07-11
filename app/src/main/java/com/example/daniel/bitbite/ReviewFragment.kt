package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_results_card.view.*
import kotlinx.android.synthetic.main.fragment_review.view.*
import org.jetbrains.anko.act
import org.jetbrains.anko.support.v4.act


class ReviewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var review : Reviews
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        review = arguments!!.getParcelable("review")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_review, container, false)

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
         * @param review Parameter 1.
         * @return A new instance of fragment ReviewFragment.
         */
        fun newInstance(review : Reviews) : ReviewFragment {
            val args = Bundle()
            args.putParcelable("review", review)
            val fragment = ReviewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // updateCard()
    // Calls update function for each segment of card
    private
    fun  updateCard(view : View) {
        val s = "\"${review.text}\""
        view.review_text.text = s
        view.review_author.text = review.author_name
        view.review_rating.setImageDrawable(
                ContextCompat.getDrawable(act, ratingConversion(review.rating)))
        updateReviewAuthorPhoto(view.review_author_photo)
    }

    // updateReviewAuthorPhoto()
    // Updates review author profile picture
    private
    fun updateReviewAuthorPhoto(view : ImageView) {
        val url = review.profile_photo_url
        if(url != "EMPTY"){ // If not empty fetch photo
            downloadPhoto(act, view, url)
        } else { // If empty set default photo
            view.setImageDrawable(ContextCompat.getDrawable(
                    act, R.drawable.default_account_icon))
        }
    }
}