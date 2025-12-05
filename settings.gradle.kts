rootProject.name = "the-game"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("launcher")
include("unity")

include("game:application")
include("game:world-map")
include("game:pawn")
include("game:sandbox")
include("game:core")
include("game:equipment")
include("game:environment")
include("game:scene")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
