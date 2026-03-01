/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.longjumpmodes.ncp

import net.ccbluex.avocado.event.MoveEvent
import net.ccbluex.avocado.features.module.modules.movement.LongJump.canBoost
import net.ccbluex.avocado.features.module.modules.movement.LongJump.jumped
import net.ccbluex.avocado.features.module.modules.movement.LongJump.ncpBoost
import net.ccbluex.avocado.features.module.modules.movement.longjumpmodes.LongJumpMode
import net.ccbluex.avocado.utils.extensions.isMoving
import net.ccbluex.avocado.utils.movement.MovementUtils.speed

object NCP : LongJumpMode("NCP") {
    override fun onUpdate() {
        speed *= if (canBoost) ncpBoost else 1f
        canBoost = false
    }

    override fun onMove(event: MoveEvent) {
        if (!mc.thePlayer.isMoving && jumped) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            event.zeroXZ()
        }
    }
}