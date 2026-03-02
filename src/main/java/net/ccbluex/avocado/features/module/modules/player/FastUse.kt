/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.player

import net.ccbluex.avocado.event.MoveEvent
import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.utils.client.PacketUtils.sendPacket
import net.ccbluex.avocado.utils.inventory.ItemUtils.isConsumingItem
import net.ccbluex.avocado.utils.movement.MovementUtils.serverOnGround
import net.ccbluex.avocado.utils.timing.MSTimer
import net.ccbluex.avocado.event.PacketEvent
import net.minecraft.item.ItemFood
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import kotlin.random.Random

object FastUse : Module("FastUse", Category.PLAYER) {
    private val mode by choices(
        "Mode",
        arrayOf("Instant", "NCP", "AAC", "Custom", "Grim", "1.17Grim"),
        "Instant"
    )

    private val delay by int("CustomDelay", 0, 0..300) { mode == "Custom" }
    private val customSpeed by int("CustomSpeed", 2, 1..35) { mode == "Custom" }
    private val customTimer by float("CustomTimer", 1.1f, 0.5f..2f) { mode == "Custom" }
    private val noMove by boolean("NoMove", false)

    private val msTimer = MSTimer()
    private var usedTimer = false
    private var shouldResetTimer = false

    val onUpdate = handler<UpdateEvent> {
        val thePlayer = mc.thePlayer ?: return@handler

        if (usedTimer || shouldResetTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
            shouldResetTimer = false
        }

        if (!isConsumingItem()) {
            msTimer.reset()
            return@handler
        }

        when (mode.lowercase()) {
            "instant" -> {
                repeat(35) {
                    sendPacket(C03PacketPlayer(serverOnGround))
                }
                mc.playerController.onStoppedUsingItem(thePlayer)
            }

            "ncp" -> if (thePlayer.itemInUseDuration > 14) {
                repeat(20) {
                    sendPacket(C03PacketPlayer(serverOnGround))
                }
                mc.playerController.onStoppedUsingItem(thePlayer)
            }

            "aac" -> {
                mc.timer.timerSpeed = 1.22F
                usedTimer = true
            }

            "custom" -> {
                mc.timer.timerSpeed = customTimer
                usedTimer = true

                if (!msTimer.hasTimePassed(delay))
                    return@handler

                repeat(customSpeed) {
                    sendPacket(C03PacketPlayer(serverOnGround))
                }
                msTimer.reset()
            }

            "1.17grim" -> {
                repeat(5) {
                    sendPacket(
                        C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch,
                            mc.thePlayer.onGround
                        )
                    )
                }
            }

            "grim" -> {
                mc.timer.timerSpeed = 0.49F
                usedTimer = true

                if (mc.thePlayer.itemInUseDuration > 3) {
                    if ((mc.thePlayer.ticksExisted % 3) == 0) {
                        repeat((1..2).random()) {
                            val randomGround = if (serverOnGround) (Math.random() > 0.1) else false
                            sendPacket(C03PacketPlayer(randomGround))
                        }
                    }
                }
            }
        }
    }

    val onMove = handler<MoveEvent> { event ->
        if (mc.thePlayer != null && isConsumingItem() && noMove) {
            event.zero()
        }
    }

    val onPacket = handler<PacketEvent> { event ->
        if (event.eventType.name != "SEND") return@handler

        val packet = event.packet
        val heldItem = mc.thePlayer?.heldItem?.item ?: return@handler

        if (packet is C08PacketPlayerBlockPlacement && isConsumable(heldItem)) {
            mc.timer.timerSpeed = Random.nextDouble(0.95, 1.5).toFloat()
            shouldResetTimer = true
        }
    }

    private fun isConsumable(item: net.minecraft.item.Item?): Boolean {
        if (item == null) return false
        val name = item.unlocalizedName.lowercase()
        return item is ItemFood || name.contains("potion") || name.contains("milk") || name.contains("golden")
    }

    override fun onDisable() {
        if (usedTimer || shouldResetTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
            shouldResetTimer = false
        }
    }
}