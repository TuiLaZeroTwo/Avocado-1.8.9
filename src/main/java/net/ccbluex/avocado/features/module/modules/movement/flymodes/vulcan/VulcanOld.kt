/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.flymodes.vulcan

import net.ccbluex.avocado.features.module.modules.movement.flymodes.FlyMode

object VulcanOld : FlyMode("VulcanOld") {
    override fun onUpdate() {
        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance > 0) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                mc.thePlayer.motionY = -0.1
                mc.thePlayer.jumpMovementFactor = 0.0265f
            } else {
                mc.thePlayer.motionY = -0.16
                mc.thePlayer.jumpMovementFactor = 0.0265f
            }
        }
    }
}
