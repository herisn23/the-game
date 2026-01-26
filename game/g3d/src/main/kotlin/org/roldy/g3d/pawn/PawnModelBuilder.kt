package org.roldy.g3d.pawn

import com.badlogic.gdx.graphics.g3d.Model
import org.roldy.core.disposable.AutoDisposableAdapter
import org.roldy.core.disposable.disposable
import org.roldy.g3d.pawn.utils.copyAnimation

class PawnModelBuilder : AutoDisposableAdapter() {
    data class Data(
        val base: Model,
        val additions: List<Model>
    )

    private val maleModel by disposable {
        Data(
            PawnAssetManager.modelMale.get(),
            listOf(
                PawnAssetManager.modelMaleExt.get(),
                PawnAssetManager.modelMaleExt2.get()
            )
        ).apply(BodyType.Male)
    }

    private val femaleModel by disposable {
        Data(
            PawnAssetManager.modelFemale.get(),
            listOf(
                PawnAssetManager.modelFemaleExt.get(),
                PawnAssetManager.modelFemaleExt2.get()
            )
        ).apply(BodyType.Female)
    }

    private fun Data.apply(bodyType: BodyType) =
        run {
            base.apply {
                additions.forEach {
                    nodes.addAll(it.nodes)
                }

                PawnAnimations[bodyType].all.forEach { anim ->
                    animations.add(copyAnimation(anim.model.get().animations.first(), anim.id))
                }
            }
        }

    operator fun get(bodyType: BodyType) =
        when (bodyType) {
            BodyType.Male -> maleModel
            BodyType.Female -> femaleModel
        }

}