import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class DependencyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        /*
        project.dependencies.apply {
            add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
            add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
        }
         */

        /*
        // Access the kotlin extension
        val kotlinExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

        // Access a specific sourceSet of the project
        val mainSourceSet = kotlinExtension.sourceSets.getByName("main")

        // Interface with the source set as needed
        println("Source set: ${mainSourceSet.name}")
         */
    }
}