package com.cramsan.minesweepers.common.main

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.cramsan.minesweepers.common.ui.Card
import com.cramsan.minesweepers.common.ui.CardUiModel
import com.cramsan.minesweepers.common.ui.theme.Dimension

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainView(
    cardUiModels: List<CardUiModel>?,
    shuffleCards: () -> Unit = {},
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { shuffleCards() },
                modifier = Modifier.padding(bottom = Dimension.xxx_large)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
            }
        }
    ) { innerPadding ->
        val pageCount = cardUiModels?.size ?: 0
        val pagerState = rememberPagerState(pageCount = { pageCount })
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            AnimatedContent(
                targetState = cardUiModels,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300, 200)) + slideInVertically(
                        animationSpec = tween(400),
                        initialOffsetY = { fullHeight -> fullHeight }
                    ) togetherWith
                            fadeOut(animationSpec = tween(300)) using
                            // Disable clipping since the faded slide-in/out should
                            // be displayed out of bounds.
                            SizeTransform(clip = false)

                }
            ) {
                if (it != null) {
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
                targetState = pageCount,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Row(
                    Modifier
                        .height(50.dp)
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
                            }, label = "weight", animationSpec = tween(300, easing = EaseInOut)
                        )
                        val color =
                            if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        Box(
                            modifier = Modifier
                                .padding(Dimension.xx_small)
                                .clip(RoundedCornerShape(Dimension.xxx_small))
                                .background(color)
                                .weight(lineWeight.value)
                                .height(Dimension.xx_small)
                        )
                    }
                }
            }
        }
    }
}
