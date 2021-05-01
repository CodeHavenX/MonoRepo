package com.cesarandres.ps2link.fragments.profilepager.profile

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.viewModels
import com.cesarandres.ps2link.base.BaseComposePS2Fragment
import com.cramsan.ps2link.core.models.Namespace
import dagger.hilt.android.AndroidEntryPoint
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject
import kotlin.time.ExperimentalTime

/**
 * Fragment to display the list of locally stored profiles.
 */
@AndroidEntryPoint
class FragmentComposeProfile : BaseComposePS2Fragment<ProfileViewModel>() {

    @Inject
    lateinit var prettyTime: PrettyTime

    override val logTag = "FragmentComposeProfile"
    override val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val characterId = arguments?.getString(CHARACTER_ID_KEY)
        val namespace = arguments?.getSerializable(NAMESPACE_KEY) as Namespace?

        viewModel.setUp(characterId, namespace)
    }

    @OptIn(ExperimentalTime::class)
    @Composable
    override fun CreateComposeContent() {
        val profile = viewModel.profile.collectAsState(null)
        val isLoading = viewModel.isLoading.collectAsState()
        ProfileCompose(
            faction = profile.value?.faction,
            br = profile.value?.battleRank?.toInt(),
            percentToNextBR = profile.value?.percentageToNextBattleRank?.toFloat(),
            certs = profile.value?.certs?.toInt(),
            percentToNextCert = profile.value?.percentageToNextCert?.toFloat(),
            loginStatus = profile.value?.loginStatus,
            lastLogin = profile.value?.lastLogin,
            outfit = profile.value?.outfit,
            server = profile.value?.server?.serverName,
            timePlayed = profile.value?.timePlayed,
            prettyTime = prettyTime,
            eventHandler = viewModel,
            isLoading = isLoading.value
        )
    }

    companion object {

        private const val CHARACTER_ID_KEY = "characterId"
        private const val NAMESPACE_KEY = "namespace"

        fun instance(characterId: String, namespace: Namespace): FragmentComposeProfile {
            val bundle = Bundle().apply {
                putString(CHARACTER_ID_KEY, characterId)
                putSerializable(NAMESPACE_KEY, namespace)
            }
            return FragmentComposeProfile().apply {
                arguments = bundle
            }
        }
    }
}