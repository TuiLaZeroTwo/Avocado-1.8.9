/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.misc

import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module

object OverrideRaycast : Module("OverrideRaycast", Category.MISC, gameDetecting = false) {
    private val alwaysActive by boolean("AlwaysActive", true)

    fun shouldOverride() = handleEvents() || alwaysActive
}