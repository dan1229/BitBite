package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_review_card.view.*
import kotlinx.android.synthetic.main.fragment_review_top_card.view.*
import org.jetbrains.anko.act


class ReviewCard : Fragment() {

    /** Variables **/
    var type = 1
    private lateinit var review: Reviews
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        review = arguments!!.getParcelable("REVIEW")
        type = arguments!!.getInt("TYPE")
    }


    /** ON CREATE **/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view: View?

        Log.d("REVIEW", review.author_name)
        Log.d("REVIEW", "type: $type")

        // Make card
        if (type == 1) { // Regular review card
            view = inflater.inflate(R.layout.fragment_review_card, container, false)
            updateCard(view)
        }
        else { // Make top review card
            view = inflater.inflate(R.layout.fragment_review_top_card, container, false)
            view.placeName.text = review.author_name

            // Set on click listener for make review
            view.makereview_card.setOnClickListener {
                listener!!.onFragmentInteraction()
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
        fun onFragmentInteraction()
    }

    /** newInstance **/
    companion object {
        fun newInstance(review: Reviews, type: Int = 1): ReviewCard {
            val args = Bundle()
            args.putParcelable("REVIEW", review)
            args.putInt("TYPE", type)
            val fragment = ReviewCard()
            fragment.arguments = args
            return fragment
        }
    }

    /**====================================================================================================**/
    /** Updater Methods **/

    // updateCard()
    // Calls update function for each segment of card
    private
    fun updateCard(view: View) {
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
    fun updateReviewAuthorPhoto(view: ImageView) {
        val url = review.profile_photo_url
        if (url != "EMPTY") { // If not empty fetch photo
            downloadPhoto(act, view, url)
        } else { // If empty set default photo
            view.setImageDrawable(ContextCompat.getDrawable(
                    act, R.drawable.default_account_icon))
        }
    }


} /** END CLASS ReviewCard.kt **/
