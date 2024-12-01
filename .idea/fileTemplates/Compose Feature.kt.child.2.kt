package ${PACKAGE_NAME}

/**
 * UI state of the ${NAME} feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class ${NAME}UIState(
    val content: ${NAME}UIModel,
    val isLoading: Boolean,
)
