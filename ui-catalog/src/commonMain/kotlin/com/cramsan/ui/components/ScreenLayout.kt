package com.cramsan.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.cramsan.framework.core.compose.ifNotTrue
import com.cramsan.framework.core.compose.ifTrue
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size

/**
 * This component represents the layout of an entire screen. It will contain two sections: the content section and the
 * action section.
 */
@Composable
fun ScreenLayout(
    modifier: Modifier = Modifier,
    fixedFooter: Boolean = false,
    maxWith: Dp = Size.COLUMN_MAX_WIDTH,
    topPadding: Dp = Padding.X_LARGE,
    bottomPadding: Dp = Padding.X_LARGE,
    sectionContent: @Composable ColumnScope.(Modifier) -> Unit,
    buttonContent: (@Composable ColumnScope.(Modifier) -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .ifTrue(maxWith != Dp.Unspecified) {
                sizeIn(maxWidth = maxWith)
            }
            .padding(
                top = topPadding,
                bottom = bottomPadding,
            )
            .ifNotTrue(fixedFooter) {
                verticalScroll(rememberScrollState())
            },
        verticalArrangement = Arrangement.spacedBy(Padding.MEDIUM),
    ) {
        ContentSection(
            modifier = Modifier.ifTrue(fixedFooter) {
                weight(1f)
                    .verticalScroll(rememberScrollState())
            },
            content = sectionContent,
        )
        buttonContent?.let {
            ActionSection(buttons = it)
        }
    }
}
