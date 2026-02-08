To package app for windows run `./gradlew compose`

Tools:
- https://esotericsoftware.com Spine
- IntelliJ Idea
- Unity
- Fruity Loops Studio

Assets:
https://hippogames.itch.io/fantasyheroes4d

# 3D models

### Pawn

__Models__

When adding new modular model for each body type you must upload them to mixamo then export it back with skin.

This must be done because mixamo rename bones to match with animations otherwise animations will not work.

__Animations__

When creating new animation from mixamo you must export animation for each body type.
Upload base model i.e. `PT_Female_Armors_Modular.fbx` or `PT_Male_Armors_Modular.fbx`,
then export model without skin on 60 FPS, convert it using `fbx-conv resources/fbx/WalkingFemale.fbx` and place g3db to
`assets/3d/pawn/animations`.
Animations must contain body type i.e. `IdleMale.g3db`

Then gradle plugin re-generates all necessary classes, so no code edit is needed

Convert all models in folder:
for f in *.fbx; do
~/Downloads/fbx-conv-mac/fbx-conv -f -s 0.01 $f
done
