rootProject.name = "the-game"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("launcher")
include("unity")

include("game:application")
include("game:core")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
