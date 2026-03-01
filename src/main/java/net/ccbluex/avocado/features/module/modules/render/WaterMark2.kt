package net.ccbluex.avocado.features.module.modules.render

import net.ccbluex.avocado.event.Render2DEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.ui.font.Fonts
import net.ccbluex.avocado.utils.GlowUtils
import net.ccbluex.avocado.utils.extensions.getPing
import net.ccbluex.avocado.utils.render.RenderUtils.drawRoundedRect
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color

object WaterMark2 : Module("WaterMark2", Category.RENDER) {

    private val bgAlpha = 130
    private val radius = 16f

    private val accent = Color(255, 30, 0)
    private val glow = Color(0, 0, 0, 150)

    val onRender2D = handler<Render2DEvent> {

        val sr = ScaledResolution(mc)

        val username = mc.session.username
        val ping = mc.thePlayer?.getPing() ?: 0
        val fps = Minecraft.getDebugFPS()
        val server = mc.currentServerData?.serverIP ?: "SinglePlayer"

        val text = "Avocado  ·  $username  ·  ${ping}ms to $server  ·  ${fps}fps"

        val font = Fonts.fontSemibold35
        val width = font.getStringWidth(text) + 24f
        val height = 26f

        val x = sr.scaledWidth / 2f - width / 2f
        val y = 8f

        drawBackground(x, y, width, height)

        val textY = y + (height - font.FONT_HEIGHT) / 2f

        val startX = x + 12f

        font.drawString("Avocado", startX, textY, accent.rgb)

        val part1 = "  ·  $username  ·  "
        font.drawString(part1, startX + font.getStringWidth("Avocado"), textY, -1)

        val part2 = "${ping}ms"
        font.drawString(
            part2,
            startX + font.getStringWidth("Avocado$part1"),
            textY,
            accent.rgb
        )

        val rest = " to $server  ·  ${fps}fps"
        font.drawString(
            rest,
            startX + font.getStringWidth("Avocado$part1$part2"),
            textY,
            -1
        )
    }

    private fun drawBackground(x: Float, y: Float, w: Float, h: Float) {

        GlowUtils.drawGlow(
            x,
            y,
            w,
            h,
            12,
            glow
        )
        drawRoundedRect(
            x,
            y,
            x + w,
            y + h,
            Color(0, 0, 0, bgAlpha).rgb,
            radius
        )
    }
}