/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.speedmodes.other

import net.ccbluex.avocado.event.MoveEvent
import net.ccbluex.avocado.features.module.modules.movement.Speed
import net.ccbluex.avocado.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.avocado.utils.extensions.isMoving
import net.ccbluex.avocado.utils.movement.MovementUtils.direction
import net.ccbluex.avocado.utils.timing.MSTimer
import kotlin.math.cos
import kotlin.math.sin

object TeleportCubeCraft : SpeedMode("TeleportCubeCraft") {
    private val timer = MSTimer()
    override fun onMove(event: MoveEvent) {
        if (mc.thePlayer.isMoving && mc.thePlayer.onGround && timer.hasTimePassed(300)) {
            val yaw = direction
            val length = Speed.cubecraftPortLength
            event.x = -sin(yaw) * length
            event.z = cos(yaw) * length
            timer.reset()
        }
    }
}