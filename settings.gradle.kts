rootProject.name = "the-game"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("launcher")
//include("unity")

include("game:boot")
include("game:core")
include("game:data")
include("game:scene")
include("game:gui")
include("game:g3d")


include("game:sandbox")


//old modules just for copying existing code
//include("old:application")
//include("old:sandbox")
//include("old:core")
//
////rendering layer should never access to gameplay layer
//include("old:layer:rendering:map")
//include("old:layer:rendering:pawn")
//include("old:layer:rendering:environment")
//include("old:layer:rendering:screen")
//include("old:layer:rendering:equipment")
//include("old:layer:rendering:g2d")
//include("old:layer:rendering:gui")
//
//
//
//include("old:layer:gameplay:world")
//include("old:layer:gameplay:scene")
//include("old:layer:gameplay:state")
//
//include("old:layer:data")



dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
