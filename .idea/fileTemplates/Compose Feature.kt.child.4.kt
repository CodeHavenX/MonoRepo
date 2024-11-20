package ${PACKAGE_NAME}

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun ${NAME}ScreenPreview() {
    ${NAME}Content(
        content = ${NAME}UIModel(""),
        loading = false,
    )
}