package com.cesarandres.ps2link.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast

import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.cesarandres.ps2link.ApplicationPS2Link
import com.cesarandres.ps2link.ApplicationPS2Link.ActivityMode
import com.cesarandres.ps2link.R
import com.cesarandres.ps2link.base.BaseFragment
import com.cesarandres.ps2link.dbg.DBGCensus
import com.cesarandres.ps2link.dbg.DBGCensus.Namespace
import com.cesarandres.ps2link.dbg.DBGCensus.Verb
import com.cesarandres.ps2link.dbg.content.CharacterProfile
import com.cesarandres.ps2link.dbg.content.response.Character_list_response
import com.cesarandres.ps2link.dbg.util.Collections.PS2Collection
import com.cesarandres.ps2link.dbg.util.QueryString
import com.cesarandres.ps2link.dbg.util.QueryString.QueryCommand
import com.cesarandres.ps2link.dbg.util.QueryString.SearchModifier
import com.cesarandres.ps2link.dbg.view.LoadingItemAdapter
import com.cesarandres.ps2link.dbg.view.ProfileItemAdapter
import com.cesarandres.ps2link.module.ButtonSelectSource
import com.cesarandres.ps2link.module.ButtonSelectSource.SourceSelectionChangedListener

import java.util.Locale

/**
 * This fragment will show the user with a field and a button to search for
 * profiles. The only requirement is that the name needs to be at least three
 * characters long.
 */
class FragmentAddProfile : BaseFragment(), SourceSelectionChangedListener {

    private var lastUsedNamespace: Namespace? = null
    private var selectionButton: ButtonSelectSource? = null

    /*
     * (non-Javadoc)
     *
     * @see com.cesarandres.ps2link.base.BaseFragment#onCreateView(android.view.
     * LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_profile, container, false)
        selectionButton = ButtonSelectSource(
            activity!!,
            activity!!.findViewById<View>(R.id.linearLayoutTitle) as ViewGroup
        )
        selectionButton!!.listener = this
        return view
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.cesarandres.ps2link.base.BaseFragment#onActivityCreated(android.os
     * .Bundle)
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.fragmentTitle.text = getString(R.string.title_profiles)
        val buttonCharacters =
            activity!!.findViewById<View>(R.id.imageButtonSearchProfile) as ImageButton
        buttonCharacters.setOnClickListener { downloadProfiles() }
        this.fragmentUpdate.setOnClickListener { downloadProfiles() }

    }

    override fun onResume() {
        super.onResume()
        activityContainer.activityMode = ActivityMode.ACTIVITY_ADD_PROFILE
        this.fragmentUpdate.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val titleLayout = activity!!.findViewById<View>(R.id.linearLayoutTitle) as LinearLayout
        selectionButton!!.removeButtons(activity!!, titleLayout)
    }

    /**
     * This method will retrieve profiles based on the criteria given by the
     * user in the text fields. The user needs to provide a name to start a
     * search. If a value is provided but it is less than three characters long,
     * then the user will see a toast asking to provide more information.
     */
    private fun downloadProfiles() {
        val searchField = view!!.findViewById<View>(R.id.fieldSearchProfile) as EditText
        this.lastUsedNamespace = DBGCensus.currentNamespace
        if (searchField.text.toString().length < 3) {
            Toast.makeText(activity, R.string.text_profile_name_too_short, Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Set the loading adapter
        val listRoot = view!!.findViewById<View>(R.id.listFoundProfiles) as ListView
        listRoot.onItemClickListener = null
        listRoot.adapter = LoadingItemAdapter(this!!.activity!!)

        val url = DBGCensus.generateGameDataRequest(
            Verb.GET,
            PS2Collection.CHARACTER_NAME,
            "",
            QueryString.generateQeuryString()
                .AddComparison(
                    "name.first_lower",
                    SearchModifier.STARTSWITH,
                    searchField.text.toString().toLowerCase(Locale.getDefault())
                )
                .AddCommand(QueryCommand.LIMIT, "25")
                .AddCommand(QueryCommand.JOIN, "character")
        )!!.toString()

        val success = Listener<Character_list_response> { response ->
            try {
                val listRoot = view!!.findViewById<View>(R.id.listFoundProfiles) as ListView
                listRoot.adapter = ProfileItemAdapter(this!!.activity!!,
                    response.character_name_list!!, false)
                listRoot.onItemClickListener =
                    OnItemClickListener { myAdapter, myView, myItemInt, mylng ->
                        mCallbacks.onItemSelected(
                            ApplicationPS2Link.ActivityMode.ACTIVITY_PROFILE.toString(),
                            arrayOf(
                                (myAdapter.getItemAtPosition(myItemInt) as CharacterProfile).characterId,
                                lastUsedNamespace!!.name
                            )
                        )
                    }
            } catch (e: Exception) {
                Toast.makeText(activity, R.string.toast_error_retrieving_data, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val error = ErrorListener {
            val listRoot = view!!.findViewById<View>(R.id.listFoundProfiles) as ListView
            if (listRoot != null) {
                listRoot.adapter = null
            }
            Toast.makeText(activity, R.string.toast_error_retrieving_data, Toast.LENGTH_SHORT)
                .show()
        }

        DBGCensus.sendGsonRequest(url, Character_list_response::class.java, success, error, this)
    }

    override fun onSourceSelectionChanged(selectedNamespace: Namespace) {
        downloadProfiles()
    }
}