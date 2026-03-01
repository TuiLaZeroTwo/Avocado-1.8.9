/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.world

import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.utils.block.block
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks

object AutoBreak : Module("AutoBreak", Category.WORLD, subjective = true, gameDetecting = false) {

    val onUpdate = handler<UpdateEvent> {
        mc.theWorld ?: return@handler

        val target = mc.objectMouseOver?.blockPos ?: return@handler

        mc.gameSettings.keyBindAttack.pressed = target.block != Blocks.air
    }

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindAttack))
            mc.gameSettings.keyBindAttack.pressed = false
    }
}
