package ${PACKAGE_NAME}

data class ${NAME}UIModel(
    val name: String,
)

fun Any.to${NAME}UIModel(): ${NAME}UIModel {
    return ${NAME}UIModel(
        name = TODO(),
    )
}
