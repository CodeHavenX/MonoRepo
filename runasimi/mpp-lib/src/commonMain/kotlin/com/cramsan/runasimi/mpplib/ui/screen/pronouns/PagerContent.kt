package com.cramsan.runasimi.mpplib.ui.screen.pronouns

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.runasimi.mpplib.ModalityTense
import com.cramsan.runasimi.mpplib.PronounFutureSuffix
import com.cramsan.runasimi.mpplib.PronounPastSuffix
import com.cramsan.runasimi.mpplib.PronounPresentSuffix
import com.cramsan.runasimi.mpplib.TimeTense
import com.cramsan.runasimi.mpplib.ui.theme.Dimension

@Composable
fun PronounsContent() {
    Column(
        modifier = Modifier
            .padding(horizontal = Dimension.medium)
            .verticalScroll(rememberScrollState())
    ) {
        SectionHeader("Tiempo Presente")
        PronounPresentSuffix.ÑUQA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PRESENT,
                it.suffix,
            )
        }
        PronounPresentSuffix.QAM.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PRESENT,
                it.suffix,
            )
        }
        PronounPresentSuffix.PAY.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PRESENT,
                it.suffix,
            )
        }
        PronounPresentSuffix.ÑUQANCHIK.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PRESENT,
                it.suffix,
            )
        }
        PronounPresentSuffix.ÑUQAYKU.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PRESENT,
                it.suffix,
            )
        }
        PronounPresentSuffix.QAMKUNA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PRESENT,
                it.suffix,
            )
        }
        PronounPresentSuffix.PAYKUNA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PRESENT,
                it.suffix,
            )
        }
        Spacer(Modifier.height(Dimension.large))
        SectionHeader("Tiempo Pasado")
        PronounPastSuffix.ÑUQA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PAST,
                it.suffix,
            )
        }
        PronounPastSuffix.QAM.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PAST,
                it.suffix,
            )
        }
        PronounPastSuffix.PAY.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PAST,
                it.suffix,
            )
        }
        PronounPastSuffix.ÑUQANCHIK.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PAST,
                it.suffix,
            )
        }
        PronounPastSuffix.ÑUQAYKU.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PAST,
                it.suffix,
            )
        }
        PronounPastSuffix.QAMKUNA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PAST,
                it.suffix,
            )
        }
        PronounPastSuffix.PAYKUNA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.PAST,
                it.suffix,
            )
        }
        Spacer(Modifier.height(Dimension.large))
        SectionHeader("Tiempo Futuro")
        PronounFutureSuffix.ÑUQA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.FUTURE,
                it.suffix,
            )
        }
        PronounFutureSuffix.QAM.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.FUTURE,
                it.suffix,
            )
        }
        PronounFutureSuffix.PAY.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.FUTURE,
                it.suffix,
            )
        }
        PronounFutureSuffix.ÑUQANCHIK.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.FUTURE,
                it.suffix,
            )
        }
        PronounFutureSuffix.ÑUQAYKU.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.FUTURE,
                it.suffix,
            )
        }
        PronounFutureSuffix.QAMKUNA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.FUTURE,
                it.suffix,
            )
        }
        PronounFutureSuffix.PAYKUNA.let {
            PronounLine(
                it.name,
                ModalityTense.SIMPLE,
                TimeTense.FUTURE,
                it.suffix,
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(Dimension.medium),
    )
}

@Composable
private fun PronounLine(
    pronoun: String,
    modalityTense: ModalityTense,
    timeTense: TimeTense,
    suffix: String,
) {
    Row {
        Text(
            pronoun.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = Dimension.x_small),
        )
        Spacer(Modifier.weight(1f))
        if (modalityTense.suffix.isNotBlank()) {
            Text(
                "-${modalityTense.suffix}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.endPadding().padding(vertical = Dimension.x_small)
            )
        }
        if (timeTense.suffix.isNotEmpty()) {
            Text(
                "-${timeTense.suffix.joinToString("/").lowercase()}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.endPadding().padding(vertical = Dimension.x_small)
            )
        }
        Text(
            "-$suffix",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = Dimension.x_small),
        )
    }
}

private fun Modifier.endPadding(): Modifier = padding(end = Dimension.x_small)
