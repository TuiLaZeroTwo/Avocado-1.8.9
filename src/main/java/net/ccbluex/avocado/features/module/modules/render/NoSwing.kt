/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.render

import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module

object NoSwing : Module("NoSwing", Category.RENDER) {
    val serverSide by boolean("ServerSide", true)
}