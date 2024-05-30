package com.cramsan.runasimi.mpplib.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.cramsan.runasimi.mpplib.ui.MainViewUIModel
import com.cramsan.runasimi.mpplib.ui.screen.pronouns.PronounsContent
import com.cramsan.runasimi.mpplib.ui.screen.trainer.TrainerContent

@Composable
fun MainView(
    mainViewUIModel: MainViewUIModel,
    shuffleCards: () -> Unit = {},
    playAudio: (Int) -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.PRACTICA) }
    var initialPage by rememberSaveable(
        mainViewUIModel.cards,
    ) {
        mutableStateOf(0)
    }

    Scaffold(
        floatingActionButton = { },
        bottomBar = {
            Bottombar(
                selectedTab,
            ) { selectedTab = it }
        },
    ) { innerPadding ->
        Crossfade(
            targetState = selectedTab,
            modifier = Modifier.padding(innerPadding),
        ) {
            when (selectedTab) {
                Tab.PRONOMBRES -> {
                    PronounsContent()
                }
                Tab.PRACTICA -> {
                    TrainerContent(
                        initialPage,
                        mainViewUIModel.cards,
                        mainViewUIModel.isLoading,
                        shuffleCards,
                        playAudio,
                        { initialPage = it },
                    )
                }
                Tab.FRASES_COMUNES -> {
                }
            }
        }
    }
}

@Composable
private fun Bottombar(
    selectedTab: Tab,
    onItemSelected: (Tab) -> Unit,
) {
    NavigationBar {
        Tab.entries.forEach { tab ->
            NavigationBarItem(
                label = { Text(text = tab.name) },
                icon = {
                    Icon(
                        when (tab) {
                            Tab.PRONOMBRES -> Icons.Default.Info
                            Tab.PRACTICA -> Icons.Default.Info
                            Tab.FRASES_COMUNES -> Icons.Default.Info
                        },
                        contentDescription = null,
                    )
                },
                selected = tab == selectedTab,
                alwaysShowLabel = true,
                onClick = { onItemSelected(tab) },
            )
        }
    }
}

private enum class Tab {
    PRONOMBRES,
    PRACTICA,
    FRASES_COMUNES,
}
