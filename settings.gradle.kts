rootProject.name = "the-game"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("launcher")

include("game:core")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
