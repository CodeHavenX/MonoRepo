package com.cramsan.runasimi.mpplib.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.cramsan.runasimi.mpplib.ui.theme.Dimension

@Composable
fun Card(
    cardUiModel: CardUiModel,
    expanded: Boolean? = null,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(expanded ?: false) }

    ElevatedCard(
        modifier = modifier
            .animateContentSize()
            .padding(Dimension.medium)
            .widthIn(max = 400.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimension.x_small,
        ),
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            CardContent(isExpanded, cardUiModel.sentence)
            Divider(
                modifier = Modifier.fillMaxWidth(),
            )
            CardDetails(cardUiModel.components, isExpanded) {
                isExpanded = !isExpanded
            }
        }
    }
}

@Composable
private fun CardContent(
    highlightText: Boolean,
    sentence: List<CardUiModel.Word>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimension.medium),
    ) {
        Text(
            text = buildAnnotatedString {
                sentence.forEach {
                    it.segments.forEach { segment ->
                        val textColor: Color by animateColorAsState(
                            if (highlightText) {
                                segment.color?.toColor() ?: MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            },
                        )
                        withStyle(
                            style = SpanStyle(color = textColor),
                        ) {
                            append(segment.segment)
                        }
                    }
                    append(" ")
                }
            },
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CardDetails(
    components: List<CardUiModel.Component>,
    expanded: Boolean,
    onExpandButtonClicked: () -> Unit = {},
) {
    val expandIcon = if (expanded) {
        Icons.Filled.ExpandLess
    } else {
        Icons.Filled.ExpandMore
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimension.small),
    ) {
        Icon(
            expandIcon,
            contentDescription = null,
            modifier = Modifier.clickable { onExpandButtonClicked() }
                .align(Alignment.BottomEnd),
        )
        // This is to workaround
        // https://stackoverflow.com/questions/67975569/why-cant-i-use-animatedvisibility-in-a-boxscope
        Column {
            AnimatedVisibility(expanded) {
                Column(
                    modifier = Modifier.padding(Dimension.small),
                    verticalArrangement = Arrangement.spacedBy(Dimension.medium),
                ) {
                    components.forEach {
                        Text(
                            buildAnnotatedString {
                                append(it.type)
                                withStyle(
                                    style = SpanStyle(
                                        color = it.color?.toColor() ?: MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                ) {
                                    append("${it.quechua} (${it.meaning})")
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
