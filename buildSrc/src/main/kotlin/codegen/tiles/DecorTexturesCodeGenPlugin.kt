package codegen.tiles

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register


class DecorTexturesCodeGenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register the code generation task
        val generateTask = project.tasks.register<GenerateClassesTask>("generateTilesClasses") {
            group = "code generation"
            description = "Generates Java/Kotlin classes before compilation"

            // Set output directory
            outputDir.set(project.layout.buildDirectory.dir("generated/sources/codegen"))
        }
        // Make sure generated sources are added to the source set
        project.plugins.withId("java") {
            val sourceSets = project.extensions.getByType<SourceSetContainer>()
            sourceSets.named("main") {
                java.srcDir(generateTask.map { it.outputDir })
            }
        }

        // Make compilation depend on code generation
        project.tasks.named("classes") {
            dependsOn(generateTask)
        }
    }



}