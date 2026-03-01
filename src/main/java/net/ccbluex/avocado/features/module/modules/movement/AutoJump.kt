/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement

import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.handler

object AutoJump : Module("AutoJump", Category.MOVEMENT) {
    val onUpdate = handler<UpdateEvent> {
        val player = mc.thePlayer ?: return@handler

        if (!player.onGround) return@handler
        if (player.isInWater || player.isInLava || player.isOnLadder) return@handler

        player.jump()
    }
}