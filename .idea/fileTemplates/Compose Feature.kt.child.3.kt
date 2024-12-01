package ${PACKAGE_NAME}

/**
 * ${NAME} UI model. You can use one or multiple models to represent
 * specific sections and component of the UI.
 */
data class ${NAME}UIModel(
    val name: String,
)

/**
 * Converts the given [Any] object to an [${NAME}UIModel] object.
 */
fun Any.to${NAME}UIModel(): ${NAME}UIModel {
    // This is just a placeholder implementation. Replace the Any type and implement the conversion logic.
    return ${NAME}UIModel(
        name = TODO(),
    )
}
