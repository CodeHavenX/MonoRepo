import de.fayard.refreshVersions.core.versionFor

apply(plugin = "io.gitlab.arturbosch.detekt")

val detektFormattingVersion = versionFor("version.io.gitlab.arturbosch.detekt..detekt-formatting")

/**
 * Configure the detekt plugin
 */

dependencies {
    add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:$detektFormattingVersion")
}