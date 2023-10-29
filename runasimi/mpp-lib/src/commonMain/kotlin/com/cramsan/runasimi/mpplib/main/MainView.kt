package com.cramsan.runasimi.mpplib.main

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.cramsan.runasimi.mpplib.ui.Card
import com.cramsan.runasimi.mpplib.ui.CardUiModel
import com.cramsan.runasimi.mpplib.ui.MainViewUIModel
import com.cramsan.runasimi.mpplib.ui.theme.Dimension
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainView(
    mainViewUIModel: MainViewUIModel,
    shuffleCards: () -> Unit = {},
    playAudio: (Int) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val pageCount = mainViewUIModel.cards.size
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pageCount },
    )

    Scaffold(
        floatingActionButton = {
            Column(
                modifier = Modifier.padding(bottom = Dimension.large),
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
                    AnimatedContent(mainViewUIModel.isLoading) {
                        if (it) {
                            Icon(Icons.Default.Downloading, contentDescription = null)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            PagerContent(
                mainViewUIModel.cards,
                pagerState,
            )
        }
    }
    remember(mainViewUIModel.cards) {
        scope.launch {
            pagerState.animateScrollToPage(0)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.PagerContent(
    cards: List<CardUiModel>,
    pagerState: PagerState,
) {
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
    AnimatedContent(
        targetState = cards.size,
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
                    targetValue = if (pagerState.currentPage == iteration) {
                        1.5f
                    } else {
                        if (iteration < pagerState.currentPage) {
                            1f
                        } else {
                            1f
                        }
                    },
                    label = "weight",
                    animationSpec = tween(300, easing = EaseInOut)
                )
                val color =
                    if (pagerState.currentPage == iteration)
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
