/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.render

import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module

object ItemPhysics : Module("ItemPhysics", Category.RENDER) {

    val realistic by boolean("Realistic", false)
    val weight by float("Weight", 0.5F, 0.1F..3F)
    val rotationSpeed by float("RotationSpeed", 1.0F, 0.01F..3F)

}