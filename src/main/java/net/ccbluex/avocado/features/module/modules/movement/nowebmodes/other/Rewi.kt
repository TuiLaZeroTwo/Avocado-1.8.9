/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.nowebmodes.other

import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.avocado.utils.extensions.tryJump

object Rewi : NoWebMode("Rewi") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) {
            return
        }
        mc.thePlayer.jumpMovementFactor = 0.42f

        if (mc.thePlayer.onGround)
            mc.thePlayer.tryJump()
    }
}
