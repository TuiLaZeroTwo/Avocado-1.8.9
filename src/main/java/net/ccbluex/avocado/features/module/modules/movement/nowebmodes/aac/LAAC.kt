/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.nowebmodes.aac

import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.avocado.utils.extensions.tryJump

object LAAC : NoWebMode("LAAC") {
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb) {
            return
        }

        mc.thePlayer.jumpMovementFactor = if (mc.thePlayer.movementInput.moveStrafe != 0f) 1f else 1.21f

        if (!mc.gameSettings.keyBindSneak.isKeyDown)
            mc.thePlayer.motionY = 0.0

        if (mc.thePlayer.onGround)
            mc.thePlayer.tryJump()
    }
}
