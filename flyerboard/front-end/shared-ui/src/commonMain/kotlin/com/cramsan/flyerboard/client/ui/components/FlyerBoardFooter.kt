package com.cramsan.flyerboard.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardNavyBackground
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardOnNavyLink
import com.cramsan.flyerboard.client.ui.theme.FlyerBoardOnNavyMuted
import com.cramsan.ui.theme.Padding
import flyerboard_ui.Res
import flyerboard_ui.footer_copyright
import flyerboard_ui.footer_link_contact
import flyerboard_ui.footer_link_privacy
import flyerboard_ui.footer_link_terms
import org.jetbrains.compose.resources.stringResource

/** Dark-navy branded footer shown at the bottom of every FlyerBoard screen. */
@Composable
fun FlyerBoardFooter(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .background(FlyerBoardNavyBackground)
            .padding(horizontal = Padding.MEDIUM, vertical = Padding.SMALL),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            FlyerBoardWordmark()
            Text(
                text = stringResource(Res.string.footer_copyright),
                style = MaterialTheme.typography.bodySmall,
                color = FlyerBoardOnNavyMuted,
            )
        }
        Row {
            TextButton(onClick = {}) {
                Text(
                    text = stringResource(Res.string.footer_link_privacy),
                    color = FlyerBoardOnNavyLink,
                )
            }
            TextButton(onClick = {}) {
                Text(
                    text = stringResource(Res.string.footer_link_terms),
                    color = FlyerBoardOnNavyLink,
                )
            }
            TextButton(onClick = {}) {
                Text(
                    text = stringResource(Res.string.footer_link_contact),
                    color = FlyerBoardOnNavyLink,
                )
            }
        }
    }
}
