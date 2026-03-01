/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.speedmodes.matrix

import net.ccbluex.avocado.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.avocado.utils.extensions.isInLiquid
import net.ccbluex.avocado.utils.extensions.isMoving
import net.ccbluex.avocado.utils.extensions.tryJump
import net.ccbluex.avocado.utils.movement.MovementUtils.strafe

object OldMatrixHop : SpeedMode("OldMatrixHop") {

    override fun onUpdate() {
        val player = mc.thePlayer ?: return
        if (player.isInLiquid || player.isInWeb || player.isOnLadder) return

        if (player.isMoving) {
            if (player.onGround) {
                player.tryJump()
                player.speedInAir = 0.02098f
                mc.timer.timerSpeed = 1.055f
            } else {
                strafe()
            }
        } else {
            mc.timer.timerSpeed = 1f
        }
    }
}
