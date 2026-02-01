package org.roldy.core.asset

import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.model.data.ModelData
import com.badlogic.gdx.utils.UBJsonReader

class SyntyModelLoader(resolver: FileHandleResolver, private val reconfigure: ModelData.() -> Unit = {}) :
    G3dModelLoader(UBJsonReader(), resolver) {

    override fun loadModelData(fileHandle: FileHandle, parameters: ModelParameters?): ModelData {
        return super.loadModelData(fileHandle, parameters).apply {
            materials.forEach { mat ->
                mat.textures?.clear()
            }
            reconfigure()
        }
    }
}