/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.player

import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.utils.client.PacketUtils.sendPacket
import net.ccbluex.avocado.utils.extensions.isMoving
import net.ccbluex.avocado.utils.movement.MovementUtils.serverOnGround
import net.ccbluex.avocado.utils.timing.MSTimer
import net.minecraft.network.play.client.C00PacketKeepAlive
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.potion.Potion

object Regen : Module("Regen", Category.PLAYER) {

    private val mode by choices(
        "Mode",
        arrayOf(
            "Vanilla",
            "PacketKeepAlive",
            "AAC4NoFire",
            "OldSpartan",
            "NewSpartan",
            "1.17Grim",
            "C04Packet",
            "C05Packet",
        ),
        "Vanilla"
    )
    private val speed by int("Speed", 100, 1..100) { mode == "Vanilla" }

    private val delay by int("Delay", 0, 0..100)
    private val health by int("Health", 18, 0..20)
    private val noAir by boolean("NoAir", true)
    private val potionEffect by boolean("PotionEffect", true)

    private val timer = MSTimer()

    private var resetTimer = false

    val onUpdate = handler<UpdateEvent> {
        if (resetTimer) {
            mc.timer.timerSpeed = 1F
        } else {
            resetTimer = false
        }

        val thePlayer = mc.thePlayer ?: return@handler

        if (
            !mc.playerController.gameIsSurvivalOrAdventure()
            || noAir && !serverOnGround
            || !thePlayer.isEntityAlive
            || thePlayer.health >= health
            || (potionEffect && !thePlayer.isPotionActive(Potion.regeneration))
            || !timer.hasTimePassed(delay)
        ) return@handler

        when (mode.lowercase()) {
            "vanilla" -> {
                repeat(speed) {
                    sendPacket(C03PacketPlayer(serverOnGround))
                }
            }

            "packetkeepalive" -> {
                repeat(speed) {
                    sendPacket(C03PacketPlayer(serverOnGround))
                    sendPacket(C00PacketKeepAlive())
                }
            }

            "aac4nofire" -> {
                if (thePlayer.isBurning && thePlayer.ticksExisted % 10 == 0) {
                    repeat(35) {
                        sendPacket(C03PacketPlayer(true))
                    }
                }
            }

            "oldspartan" -> {
                if (thePlayer.isMoving || !thePlayer.onGround) {
                    return@handler
                }

                repeat(9) {
                    sendPacket(C03PacketPlayer(thePlayer.onGround))
                }
            }

            "newspartan" -> {
                if (!thePlayer.isMoving && serverOnGround) {
                    repeat(9) {
                        sendPacket(C03PacketPlayer(serverOnGround))
                    }

                    mc.timer.timerSpeed = 0.45F
                    resetTimer = true
                }
            }

            "c04packet" -> {
                repeat(speed) {
                    sendPacket(
                        C04PacketPlayerPosition(
                            thePlayer.posX,
                            thePlayer.posY,
                            thePlayer.posZ,
                            serverOnGround
                        )
                    )
                }
            }

            "c05packet" -> {
                repeat(speed) {
                    sendPacket(
                        C05PacketPlayerLook(
                            thePlayer.rotationYaw,
                            thePlayer.rotationPitch,
                            serverOnGround
                        )
                    )
                }
            }

            "1.17grim" -> {
                repeat(speed) {
                    sendPacket(
                        C06PacketPlayerPosLook(
                            thePlayer.posX,
                            thePlayer.posY,
                            thePlayer.posZ,
                            thePlayer.rotationYaw,
                            thePlayer.rotationPitch,
                            serverOnGround
                        )
                    )
                }
            }
        }

        timer.reset()
    }
}
