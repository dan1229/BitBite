package com.example.daniel.bitbite

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_style_tag.view.*

class StyleTag : Fragment() {

    /** Variables **/
    var style = "Random"
    private var listener: StyleTag.OnFragmentInteractionListener? = null


    /** ON CREATE **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        style = arguments!!.getString("STYLE")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_style_tag, container, false)

        // Update text
        view.styletag_text_style.text = style

        // Set listener for close icon
        view.styletag_icon_close.setOnClickListener {
            listener!!.styleTagTouched(this, style)
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
        fun styleTagTouched(frag : Fragment, string : String)
    }

    /** newInstance **/
    companion object {
        fun newInstance(style : String) : StyleTag {
            val args = Bundle()
            args.putString("STYLE", style)
            val fragment = StyleTag()
            fragment.arguments = args
            return fragment
        }
    }
}
