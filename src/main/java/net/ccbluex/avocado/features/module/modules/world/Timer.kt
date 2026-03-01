/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.world

import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.WorldEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.utils.extensions.isMoving

object Timer : Module("Timer", Category.WORLD, gameDetecting = false) {

    private val mode by choices("Mode", arrayOf("OnMove", "NoMove", "Always"), "OnMove")
    private val speed by float("Speed", 2F, 0.1F..10F)

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        mc.timer.timerSpeed = 1F
    }

    val onUpdate = handler<UpdateEvent> {
        val player = mc.thePlayer ?: return@handler

        if (mode == "Always" || mode == "OnMove" && player.isMoving || mode == "NoMove" && !player.isMoving) {
            mc.timer.timerSpeed = speed
            return@handler
        }

        mc.timer.timerSpeed = 1F
    }

    val onWorld = handler<WorldEvent> {
        if (it.worldClient == null)
            state = false
    }
}
