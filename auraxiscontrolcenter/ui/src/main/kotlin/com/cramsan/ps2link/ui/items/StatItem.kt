package com.cramsan.ps2link.ui.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cramsan.ps2link.ui.R
import com.cramsan.ps2link.ui.SlimButton
import com.cramsan.ps2link.ui.theme.PS2Theme

@Composable
fun StatItem(
    modifier: Modifier = Modifier,
    label: String,
    allTime: Float?,
    today: Float?,
    thisWeek: Float?,
    thisMonth: Float?,
) {
    SlimButton(
        modifier = modifier
    ) {
        Column {
            Text(
                text = label.replace("_", " ").capitalize(),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        R.string.text_stat_all,
                        allTime?.toString() ?: stringResource(R.string.text_unknown)
                    ),
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        R.string.text_stat_today,
                        today?.toString() ?: stringResource(R.string.text_unknown)
                    ),
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        R.string.text_stat_week,
                        thisWeek?.toString() ?: stringResource(R.string.text_unknown)
                    ),
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        R.string.text_stat_month,
                        thisMonth?.toString() ?: stringResource(R.string.text_unknown)
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
fun StatItemPreview() {
    PS2Theme {
        StatItem(
            label = "Kills",
            allTime = 10000f,
            today = 19f,
            thisWeek = 200f,
            thisMonth = null,
        )
    }
}