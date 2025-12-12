rootProject.name = "the-game"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("launcher")
include("unity")

include("game:application")
include("game:sandbox")
include("game:core")

//rendering layer should never access to gameplay layer
include("game:layer:rendering:map")
include("game:layer:rendering:pawn")
include("game:layer:rendering:environment")
include("game:layer:rendering:screen")
include("game:layer:rendering:equipment")
include("game:layer:rendering:g2d")



include("game:layer:gameplay:world")
include("game:layer:gameplay:scene")
include("game:layer:gameplay:state")

include("game:layer:data")



dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
