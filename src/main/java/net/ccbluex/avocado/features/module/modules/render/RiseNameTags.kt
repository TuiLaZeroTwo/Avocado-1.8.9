/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.render

import net.ccbluex.avocado.event.Render3DEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.features.module.modules.misc.AntiBot.isBot
import net.ccbluex.avocado.utils.attack.EntityUtils.getHealth
import net.ccbluex.avocado.utils.attack.EntityUtils.isLookingOnEntities
import net.ccbluex.avocado.utils.attack.EntityUtils.isSelected
import net.ccbluex.avocado.utils.client.EntityLookup
import net.ccbluex.avocado.utils.render.RenderUtils
import net.ccbluex.avocado.utils.render.RenderUtils.disableGlCap
import net.ccbluex.avocado.utils.render.RenderUtils.enableGlCap
import net.ccbluex.avocado.utils.render.RenderUtils.resetCaps
import net.ccbluex.avocado.utils.extensions.*
import net.ccbluex.avocado.utils.rotation.RotationUtils.isEntityHeightVisible
import net.ccbluex.avocado.utils.GlowUtils
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11.*
import java.awt.Color

object RiseNameTags : Module("RiseNameTags", Category.RENDER) {

    private val renderSelf by boolean("RenderSelf", false)
    private val bot by boolean("Bots", true)

    private val maxRenderDistance by int("MaxRenderDistance", 50, 1..200)
        .onChanged { value: Int ->
            maxRenderDistanceSq = value.toDouble() * value.toDouble()
        }

    private val onLook by boolean("OnLook", false)
    private val maxAngleDifference by float("MaxAngleDifference", 90f, 5f..90f) { onLook }

    private val thruBlocks by boolean("ThruBlocks", true)

    private val shadowcheck by boolean("ShadowCheck", true)
    private val shadowStrength by int("ShadowStrength", 1, 1..2)
    private var maxRenderDistanceSq: Double =
        maxRenderDistance.toDouble() * maxRenderDistance.toDouble()

    private val entities by EntityLookup<EntityLivingBase>()
        .filter { bot || !isBot(it) }
        .filter { !onLook || isLookingOnEntities(it, maxAngleDifference.toDouble()) }
        .filter { thruBlocks || isEntityHeightVisible(it) }

    val onRender3D = handler<Render3DEvent> {

        if (mc.theWorld == null || mc.thePlayer == null) return@handler

        glPushAttrib(GL_ENABLE_BIT)
        glPushMatrix()

        glDisable(GL_LIGHTING)
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        for (entity in entities) {

            val renderingSelf =
                entity is EntityPlayerSP &&
                        (mc.gameSettings.thirdPersonView != 0 || FreeCam.handleEvents())

            if (!renderingSelf || !renderSelf)
                if (!isSelected(entity, false)) continue

            val distanceSq = mc.thePlayer.getDistanceSqToEntity(entity)

            if (renderingSelf) FreeCam.restoreOriginalPosition()

            if (distanceSq <= maxRenderDistanceSq)
                renderNameTag(entity)

            if (renderingSelf) FreeCam.useModifiedPosition()
        }

        glPopMatrix()
        glPopAttrib()
        glColor4f(1f, 1f, 1f, 1f)
    }

    private fun renderNameTag(entity: EntityLivingBase) {

        val player = mc.thePlayer ?: return
        val renderManager = mc.renderManager
        val font = mc.fontRendererObj

        val name = entity.displayName.unformattedText ?: return
        val healthText = getHealth(entity).toInt().toString()

        glPushMatrix()

        disableGlCap(GL_LIGHTING, GL_DEPTH_TEST)
        enableGlCap(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        val (x, y, z) = entity.interpolatedPosition(entity.lastTickPos) - renderManager.renderPos

        glTranslated(x, y + entity.eyeHeight + 0.55, z)
        glRotatef(-renderManager.playerViewY, 0f, 1f, 0f)
        glRotatef(
            renderManager.playerViewX *
                    if (mc.gameSettings.thirdPersonView == 2) -1f else 1f,
            1f, 0f, 0f
        )

        val distance = player.getDistanceToEntity(entity)
        val scale = ((distance / 4f).coerceAtLeast(1f) / 150f) * 2f
        glScalef(-scale, -scale, scale)

        val nameWidth = font.getStringWidth(name)
        val healthWidth = font.getStringWidth(healthText)
        val maxWidth = maxOf(nameWidth, healthWidth) + 10
        val height = font.FONT_HEIGHT * 2 + 6
        if (shadowcheck) {
            GlowUtils.drawGlow(
                -maxWidth / 2f,
                -height / 2f,
                maxWidth.toFloat(),
                height.toFloat(),
                shadowStrength * 13,
                Color(0, 0, 0, 140)
            )
        }
        glDisable(GL_TEXTURE_2D)
        RenderUtils.drawRoundedRect(
            -maxWidth / 2f,
            -height / 2f,
            maxWidth / 2f,
            height / 2f,
            Color(0, 0, 0, 178).rgb,
            5f
        )
        glEnable(GL_TEXTURE_2D)
        font.drawString(
            name,
            -nameWidth / 2f,
            -height / 2f + 2f,
            Color(103, 216, 230).rgb,
            false
        )
        font.drawString(
            healthText,
            -healthWidth / 2f,
            -height / 2f + font.FONT_HEIGHT + 4f,
            Color.WHITE.rgb,
            false
        )

        resetCaps()
        glPopMatrix()
    }
}