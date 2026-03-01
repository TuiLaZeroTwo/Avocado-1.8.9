/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.flymodes.other

import net.ccbluex.avocado.event.*
import net.ccbluex.avocado.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.avocado.utils.client.BlinkUtils
import net.ccbluex.avocado.utils.render.RenderUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.*
import org.lwjgl.opengl.GL11
import java.awt.Color

object FlyBlockPacket : FlyMode("FlyBlockPacket") {

    /**
     * @author DeletedUser # BeoPhiMan
     * @reason Fly Block Blink ( Server : heromc.net )
     */

    private var visibleLimit = 4
    private var placedCount = 0
    private var limitBlock = true
    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        val player = mc.thePlayer ?: return
        if (player.isDead) return

        if (event.eventType == EventState.SEND) {
            when (packet) {
                is C03PacketPlayer -> {
                    BlinkUtils.blink(packet, event, true, false)
                }

                is C08PacketPlayerBlockPlacement -> {
                    if (limitBlock) {
                        if (placedCount < visibleLimit) {
                            placedCount++
                        } else {
                            BlinkUtils.blink(packet, event, true, false)
                        }
                    }
                }
            }
        }

        if (event.eventType == EventState.RECEIVE) {
            if (isServerPacket(packet) && !isEntityMovementPacket(packet)) {
                BlinkUtils.blink(packet, event, false, true)
            }
        }
    }
    override fun onMotion(event: MotionEvent) {
        if (event.eventState != EventState.POST) return

        val player = mc.thePlayer ?: return

        if (player.isDead || player.ticksExisted <= 10) {
            BlinkUtils.unblink()
        } else {
            BlinkUtils.syncReceived()
        }
    }
    override fun onRender3D(event: Render3DEvent) {
        val positions = BlinkUtils.positions
        if (positions.isEmpty()) return

        val color = Color(150, 200, 255, 180)

        synchronized(positions) {
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_DEPTH_TEST)

            mc.entityRenderer.disableLightmap()

            GL11.glLineWidth(2.2f)
            GL11.glBegin(GL11.GL_LINE_STRIP)
            RenderUtils.glColor(color)

            val renderPosX = mc.renderManager.renderPosX
            val renderPosY = mc.renderManager.renderPosY
            val renderPosZ = mc.renderManager.renderPosZ

            for (vec in positions) {
                GL11.glVertex3d(
                    vec.xCoord - renderPosX,
                    vec.yCoord - renderPosY,
                    vec.zCoord - renderPosZ
                )
            }

            GL11.glEnd()

            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }
    }
    override fun onEnable() {
        placedCount = 0
        BlinkUtils.unblink()
    }

    override fun onDisable() {
        BlinkUtils.unblink()
    }
    private fun isServerPacket(packet: Any): Boolean {
        return packet.javaClass.simpleName.startsWith("S")
    }

    private fun isEntityMovementPacket(packet: Any): Boolean {
        return when (packet) {
            is S14PacketEntity,
            is S18PacketEntityTeleport,
            is S19PacketEntityHeadLook,
            is S0BPacketAnimation,
            is S0CPacketSpawnPlayer,
            is S1CPacketEntityMetadata -> true
            else -> {
                val name = packet.javaClass.simpleName
                name == "S15PacketEntityRelMove" ||
                        name == "S17PacketEntityLookMove" ||
                        name == "S16PacketEntityLook"
            }
        }
    }
}