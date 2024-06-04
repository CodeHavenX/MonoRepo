@file:Suppress("Filename")

package com.cramsan.edifikana.client.android.features.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BottomBarDestinationUiModel(
    val route: String,
    @DrawableRes
    val icon: Int,
    @StringRes
    val text: Int,
    val isStartDestination: Boolean = false,
)
