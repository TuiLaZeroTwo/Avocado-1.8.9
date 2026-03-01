/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.speedmodes.other

import net.ccbluex.avocado.features.module.modules.movement.Speed
import net.ccbluex.avocado.features.module.modules.movement.Speed.customAirStrafe
import net.ccbluex.avocado.features.module.modules.movement.Speed.customAirTimer
import net.ccbluex.avocado.features.module.modules.movement.Speed.customAirTimerTick
import net.ccbluex.avocado.features.module.modules.movement.Speed.customGroundStrafe
import net.ccbluex.avocado.features.module.modules.movement.Speed.customGroundTimer
import net.ccbluex.avocado.features.module.modules.movement.Speed.customY
import net.ccbluex.avocado.features.module.modules.movement.Speed.notOnConsuming
import net.ccbluex.avocado.features.module.modules.movement.Speed.notOnFalling
import net.ccbluex.avocado.features.module.modules.movement.Speed.notOnVoid
import net.ccbluex.avocado.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.avocado.utils.extensions.isMoving
import net.ccbluex.avocado.utils.extensions.stopXZ
import net.ccbluex.avocado.utils.extensions.stopY
import net.ccbluex.avocado.utils.extensions.tryJump
import net.ccbluex.avocado.utils.movement.FallingPlayer
import net.ccbluex.avocado.utils.movement.MovementUtils.strafe
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion

object CustomSpeed : SpeedMode("Custom") {

    override fun onMotion() {
        val player = mc.thePlayer ?: return
        val heldItem = player.heldItem

        val fallingPlayer = FallingPlayer()
        if (notOnVoid && fallingPlayer.findCollision(500) == null
            || notOnFalling && player.fallDistance > 2.5f
            || notOnConsuming && player.isUsingItem
            && (heldItem.item is ItemFood
                    || heldItem.item is ItemPotion
                    || heldItem.item is ItemBucketMilk)
        ) {

            if (player.onGround) player.tryJump()
            mc.timer.timerSpeed = 1f
            return
        }

        if (player.isMoving) {
            if (player.onGround) {
                if (customGroundStrafe > 0) {
                    strafe(customGroundStrafe)
                }

                mc.timer.timerSpeed = customGroundTimer
                player.motionY = customY.toDouble()
            } else {
                if (customAirStrafe > 0) {
                    strafe(customAirStrafe)
                }

                if (player.ticksExisted % customAirTimerTick == 0) {
                    mc.timer.timerSpeed = customAirTimer
                } else {
                    mc.timer.timerSpeed = 1f
                }
            }
        }
    }

    override fun onEnable() {
        val player = mc.thePlayer ?: return

        if (Speed.resetXZ) player.stopXZ()
        if (Speed.resetY) player.stopY()

        super.onEnable()
    }

}