/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.flymodes.hypixel

import net.ccbluex.avocado.event.BlockBBEvent
import net.ccbluex.avocado.event.JumpEvent
import net.ccbluex.avocado.event.PacketEvent
import net.ccbluex.avocado.event.StepEvent
import net.ccbluex.avocado.features.module.modules.movement.Fly.hypixelBoost
import net.ccbluex.avocado.features.module.modules.movement.Fly.hypixelBoostDelay
import net.ccbluex.avocado.features.module.modules.movement.Fly.hypixelBoostTimer
import net.ccbluex.avocado.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.avocado.utils.timing.MSTimer
import net.ccbluex.avocado.utils.timing.TickTimer
import net.minecraft.init.Blocks.air
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB

object Hypixel : FlyMode("Hypixel") {
    private val tickTimer = TickTimer()
    private val msTimer = MSTimer()

    override fun onEnable() {
        msTimer.reset()
        tickTimer.reset()
    }

    override fun onUpdate() {
        mc.timer.timerSpeed =
            if (hypixelBoost && !msTimer.hasTimePassed(hypixelBoostDelay))
                1f + hypixelBoostTimer * (msTimer.hasTimeLeft(hypixelBoostDelay) / hypixelBoostDelay.toFloat())
            else 1f

        tickTimer.update()

        if (tickTimer.hasTimePassed(2)) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ)
            tickTimer.reset()
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer)
            packet.onGround = false
    }

    override fun onBB(event: BlockBBEvent) {
        if (event.block == air && event.y < mc.thePlayer.posY)
            event.boundingBox = AxisAlignedBB.fromBounds(
                event.x.toDouble(),
                event.y.toDouble(),
                event.z.toDouble(),
                event.x + 1.0,
                mc.thePlayer.posY,
                event.z + 1.0
            )
    }

    override fun onJump(event: JumpEvent) {
        event.cancelEvent()
    }

    override fun onStep(event: StepEvent) {
        event.stepHeight = 0f
    }
}
