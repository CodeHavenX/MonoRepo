package com.cesarandres.ps2link.fragments.profilelist

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.cesarandres.ps2link.base.BasePS2ViewModel
import com.cesarandres.ps2link.fragments.OpenProfile
import com.cesarandres.ps2link.fragments.OpenProfileSearch
import com.cramsan.framework.core.DispatcherProvider
import com.cramsan.ps2link.appcore.preferences.PS2Settings
import com.cramsan.ps2link.appcore.repository.PS2LinkRepository
import com.cramsan.ps2link.core.models.Namespace
import javax.inject.Inject

class ProfileListViewModel @Inject constructor(
    application: Application,
    pS2LinkRepository: PS2LinkRepository,
    pS2Settings: PS2Settings,
    dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle,
) : BasePS2ViewModel(
        application,
        pS2LinkRepository,
        pS2Settings,
        dispatcherProvider,
        savedStateHandle
    ),
    ProfileListEventHandler {

    override val logTag: String
        get() = "ProfileListViewModel"

    // State
    private val _profileList = pS2LinkRepository.getAllCharactersAsFlow()
    val profileList = _profileList.asLiveData()

    override fun onSearchProfileClick() {
        events.value = OpenProfileSearch
    }

    override fun onProfileSelected(profileId: String, namespace: Namespace) {
        events.value = OpenProfile(profileId, namespace)
    }
}