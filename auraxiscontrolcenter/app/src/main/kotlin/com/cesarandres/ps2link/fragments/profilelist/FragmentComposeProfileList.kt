package com.cesarandres.ps2link.fragments.profilelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cesarandres.ps2link.R
import com.cesarandres.ps2link.base.BaseComposePS2Fragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment to display the list of locally stored profiles.
 */
@AndroidEntryPoint
class FragmentComposeProfileList : BaseComposePS2Fragment<ProfileListViewModel>() {

    override val logTag = "FragmentComposeProfileList"
    override val viewModel: ProfileListViewModel by viewModels()

    @Composable
    override fun CreateComposeContent() {
        val profileList = viewModel.profileList.observeAsState(emptyList())
        ProfileListCompose(
            profileItems = profileList.value,
            eventHandler = viewModel,
        )
    }

    // TODO: Migrate to the new MenuProvider API
    // https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        return view
    }

    // TODO: Migrate to the new MenuProvider API
    // https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
    @Suppress("DEPRECATION")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                val action = FragmentComposeProfileListDirections.actionFragmentProfileListToFragmentAddProfile()
                findNavController().navigate(action)
            }
        }
        return true
    }
}
