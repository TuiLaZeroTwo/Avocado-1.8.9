/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.world

import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module

object FastPlace : Module("FastPlace", Category.WORLD) {
    val speed by int("Speed", 0, 0..4)
    val onlyBlocks by boolean("OnlyBlocks", true)
    val facingBlocks by boolean("OnlyWhenFacingBlocks", true)
}
