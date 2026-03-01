/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.command.commands

import net.ccbluex.avocado.event.Listenable
import net.ccbluex.avocado.event.Render2DEvent
import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.command.Command
import net.ccbluex.avocado.utils.extensions.component1
import net.ccbluex.avocado.utils.extensions.component2
import net.ccbluex.avocado.utils.render.RenderUtils.deltaTime
import net.ccbluex.avocado.utils.render.RenderUtils.drawImage
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

object TacoCommand : Command("taco"), Listenable {
    var tacoToggle = false
    private var image = 0
    private var running = 0f
    private val tacoTextures = arrayOf(
        ResourceLocation("avocado/taco/1.png"),
        ResourceLocation("avocado/taco/2.png"),
        ResourceLocation("avocado/taco/3.png"),
        ResourceLocation("avocado/taco/4.png"),
        ResourceLocation("avocado/taco/5.png"),
        ResourceLocation("avocado/taco/6.png"),
        ResourceLocation("avocado/taco/7.png"),
        ResourceLocation("avocado/taco/8.png"),
        ResourceLocation("avocado/taco/9.png"),
        ResourceLocation("avocado/taco/10.png"),
        ResourceLocation("avocado/taco/11.png"),
        ResourceLocation("avocado/taco/12.png")
    )

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        tacoToggle = !tacoToggle
        chat(if (tacoToggle) "§aTACO TACO TACO. :)" else "§cYou made the little taco sad! :(")
    }

    val onRender2D = handler<Render2DEvent> {
        if (!tacoToggle)
            return@handler

        running += 0.15f * deltaTime
        val (width, height) = ScaledResolution(mc)
        drawImage(tacoTextures[image], running.toInt(), height - 60, 64, 32)
        if (width <= running)
            running = -64f
    }

    val onUpdate = handler<UpdateEvent> {
        if (!tacoToggle) {
            image = 0
            return@handler
        }

        image++
        if (image >= tacoTextures.size) image = 0
    }


    override fun tabComplete(args: Array<String>) = listOf("TACO")
}