package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardAccentGradientEnd
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardAccentGradientStart
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardNavyBackground
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardOnNavy
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardPillShape
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardSelectedTabOverlay
import com.cramsan.ui.theme.Padding
import flyerboard_ui.Res
import flyerboard_ui.app_bar_action_sign_in
import flyerboard_ui.app_bar_action_sign_out
import org.jetbrains.compose.resources.stringResource

/** A tab shown in the center of [FlyerBoardMainTopBar]. */
data class FlyerBoardTopBarTab(val label: String, val selected: Boolean, val onClick: () -> Unit)

/**
 * Dark-navy branded top bar shown on top-level FlyerBoard screens.
 *
 * Displays the "FLYERBOARD" wordmark on the left, a row of navigation [tabs] in the
 * center, a Sign In/Sign Out pill on the right, and a thin gradient accent line below.
 */
@Composable
fun FlyerBoardMainTopBar(
    tabs: List<FlyerBoardTopBarTab>,
    isAuthenticated: Boolean,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .background(FlyerBoardNavyBackground)
                .padding(horizontal = Padding.MEDIUM, vertical = Padding.SMALL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FlyerBoardWordmark()
            Row(
                modifier =
                Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    FlyerBoardMainTopBarTab(tab)
                }
            }
            Button(
                onClick = if (isAuthenticated) onSignOut else onSignIn,
                shape = FlyerBoardPillShape,
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = FlyerBoardOnNavy,
                    contentColor = FlyerBoardNavyBackground,
                ),
            ) {
                val signInLabel = stringResource(Res.string.app_bar_action_sign_in)
                val signOutLabel = stringResource(Res.string.app_bar_action_sign_out)
                Text(if (isAuthenticated) signOutLabel else signInLabel)
            }
        }
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .height(ACCENT_LINE_HEIGHT)
                .background(
                    Brush.horizontalGradient(
                        listOf(FlyerBoardAccentGradientStart, FlyerBoardAccentGradientEnd),
                    ),
                ),
        )
    }
}

@Composable
private fun FlyerBoardMainTopBarTab(
    tab: FlyerBoardTopBarTab,
) {
    val backgroundColor = if (tab.selected) FlyerBoardSelectedTabOverlay else Color.Transparent
    Text(
        text = tab.label,
        color = FlyerBoardOnNavy,
        fontWeight = if (tab.selected) FontWeight.Bold else FontWeight.Normal,
        maxLines = 1,
        softWrap = false,
        modifier =
        Modifier
            .padding(horizontal = Padding.XX_SMALL)
            .clip(FlyerBoardPillShape)
            .background(backgroundColor)
            .clickable(onClick = tab.onClick)
            .padding(horizontal = Padding.MEDIUM, vertical = Padding.X_SMALL),
    )
}

private val ACCENT_LINE_HEIGHT = 2.dp
