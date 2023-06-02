package com.cramsan.ps2link.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.cramsan.ps2link.core.models.CharacterClass
import com.cramsan.ps2link.core.models.Faction
import com.cramsan.ps2link.core.models.MedalType
import com.cramsan.ps2link.core.models.Namespace
import org.jetbrains.compose.resources.painterResource

@Composable
internal expect fun CharacterClass.toPainter(): Painter

@Composable
internal expect fun MedalType?.toPainter(): Painter

@Composable
internal expect fun Faction.toPainter(): Painter

@Composable
internal expect fun Namespace.toPainter(): Painter