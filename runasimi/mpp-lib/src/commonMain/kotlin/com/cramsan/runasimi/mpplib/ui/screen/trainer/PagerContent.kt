package com.cramsan.runasimi.mpplib.ui.screen.trainer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.cramsan.runasimi.mpplib.ui.Card
import com.cramsan.runasimi.mpplib.ui.CardUiModel
import com.cramsan.runasimi.mpplib.ui.theme.Dimension

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrainerContent(
    initialPage: Int,
    cards: List<CardUiModel>,
    isLoading: Boolean,
    shuffleCards: () -> Unit = {},
    playAudio: (Int) -> Unit = {},
    onPageChanged: (Int) -> Unit = {}
) {
    val pageCount = cards.size
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pageCount },
    )

    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChanged(page)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = cards,
            transitionSpec = {
                fadeIn(animationSpec = tween(300, 300)) + slideInVertically(
                    animationSpec = tween(400),
                    initialOffsetY = { fullHeight -> fullHeight }
                ) togetherWith
                    fadeOut(animationSpec = tween(300)) using
                    // Disable clipping since the faded slide-in/out should
                    // be displayed out of bounds.
                    SizeTransform(clip = false)
            }
        ) {
            if (it.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    verticalAlignment = Alignment.Top,
                    beyondBoundsPageCount = 3,
                    modifier = Modifier
                        .fillMaxSize(),
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Card(
                            it[page],
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(bottom = Dimension.large)
                .padding(horizontal = Dimension.small)
                .align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Dimension.small),
        ) {
            SmallFloatingActionButton(
                onClick = {
                    shuffleCards()
                },
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
            }
            FloatingActionButton(
                onClick = { playAudio(pagerState.currentPage) },
            ) {
                AnimatedContent(isLoading) {
                    if (it) {
                        Icon(Icons.Default.Downloading, contentDescription = null)
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                    }
                }
            }
        }
        PageIndicator(cards.size, pagerState.currentPage)
    }
}

@Composable
private fun BoxScope.PageIndicator(
    pageCount: Int,
    currentPage: Int,
) {
    AnimatedContent(
        targetState = pageCount,
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(it) { iteration ->
                val lineWeight = animateFloatAsState(
                    targetValue = if (currentPage == iteration) {
                        1.5f
                    } else {
                        if (iteration < currentPage) {
                            1f
                        } else {
                            1f
                        }
                    },
                    label = "weight",
                    animationSpec = tween(300, easing = EaseInOut)
                )
                val color =
                    if (currentPage == iteration)
                        MaterialTheme.colorScheme.onBackground
                    else
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(horizontal = Dimension.xx_small)
                        .padding(vertical = Dimension.xxx_small)
                        .clip(RoundedCornerShape(Dimension.xxx_small))
                        .background(color)
                        .weight(lineWeight.value)
                        .height(Dimension.xx_small)
                )
            }
        }
    }
}
