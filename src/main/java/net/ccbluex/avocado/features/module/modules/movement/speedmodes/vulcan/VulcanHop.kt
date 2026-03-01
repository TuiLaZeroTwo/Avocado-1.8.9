/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.speedmodes.vulcan

import net.ccbluex.avocado.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.avocado.utils.extensions.isInLiquid
import net.ccbluex.avocado.utils.extensions.isMoving
import net.ccbluex.avocado.utils.extensions.tryJump
import net.ccbluex.avocado.utils.movement.MovementUtils.strafe

object VulcanHop : SpeedMode("VulcanHop") {
    override fun onUpdate() {
        val player = mc.thePlayer ?: return
        if (player.isInLiquid || player.isInWeb || player.isOnLadder) return

        if (player.isMoving) {
            if (player.isAirBorne && player.fallDistance > 2) {
                mc.timer.timerSpeed = 1f
                return
            }

            if (player.onGround) {
                player.tryJump()
                if (player.motionY > 0) {
                    mc.timer.timerSpeed = 1.1453f
                }
                strafe(0.4815f)
            } else {
                if (player.motionY < 0) {
                    mc.timer.timerSpeed = 0.9185f
                }
            }
        } else {
            mc.timer.timerSpeed = 1f
        }
    }
}