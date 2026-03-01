/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.render

import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.minecraft.client.settings.GameSettings

object KeepTabList : Module("KeepTabList", Category.RENDER, gameDetecting = false) {

    val onUpdate = handler<UpdateEvent> {
        if (mc.thePlayer == null || mc.theWorld == null) return@handler

        mc.gameSettings.keyBindPlayerList.pressed = true
    }

    override fun onDisable() {
        mc.gameSettings.keyBindPlayerList.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindPlayerList)
    }
}