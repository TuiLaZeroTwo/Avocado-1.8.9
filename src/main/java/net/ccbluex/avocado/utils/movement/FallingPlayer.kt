/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.utils.movement

import net.ccbluex.avocado.utils.client.MinecraftInstance
import net.ccbluex.avocado.utils.extensions.plus
import net.ccbluex.avocado.utils.extensions.toRadians
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK
import net.minecraft.util.Vec3
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FallingPlayer(
    private var x: Double = MinecraftInstance.mc.thePlayer.posX,
    private var y: Double = MinecraftInstance.mc.thePlayer.posY,
    private var z: Double = MinecraftInstance.mc.thePlayer.posZ,
    private var motionX: Double = MinecraftInstance.mc.thePlayer.motionX,
    private var motionY: Double = MinecraftInstance.mc.thePlayer.motionY,
    private var motionZ: Double = MinecraftInstance.mc.thePlayer.motionZ,
    private val yaw: Float = MinecraftInstance.mc.thePlayer.rotationYaw,
    private var strafe: Float = MinecraftInstance.mc.thePlayer.moveStrafing,
    private var forward: Float = MinecraftInstance.mc.thePlayer.moveForward
) : MinecraftInstance {
    constructor(player: EntityPlayerSP, predict: Boolean = false) : this(
        if (predict) player.posX + player.motionX else player.posX,
        if (predict) player.posY + player.motionY else player.posY,
        if (predict) player.posZ + player.motionZ else player.posZ,
        player.motionX,
        player.motionY,
        player.motionZ,
        player.rotationYaw,
        player.moveStrafing,
        player.moveForward
    )

    private fun calculateForTick() {
        strafe *= 0.98f
        forward *= 0.98f

        var v = strafe * strafe + forward * forward
        if (v >= 0.0001f) {
            v = mc.thePlayer.jumpMovementFactor / sqrt(v).coerceAtLeast(1f)

            strafe *= v
            forward *= v

            val f1 = sin(yaw.toRadians())
            val f2 = cos(yaw.toRadians())

            motionX += (strafe * f2 - forward * f1).toDouble()
            motionZ += (forward * f2 + strafe * f1).toDouble()
        }

        motionY -= 0.08
        motionX *= 0.91
        motionY *= 0.9800000190734863
        motionY *= 0.91
        motionZ *= 0.91

        x += motionX
        y += motionY
        z += motionZ
    }

    fun findCollision(ticks: Int): CollisionResult? {
        repeat(ticks) { i ->
            val start = Vec3(x, y, z)
            calculateForTick()
            val end = Vec3(x, y, z)

            for (offset in offsets) {
                rayTrace(start + offset, end)?.let { return CollisionResult(it, i) }
            }
        }
        return null
    }

    private fun rayTrace(start: Vec3, end: Vec3): BlockPos? {
        val result = mc.theWorld.rayTraceBlocks(start, end, true) ?: return null

        return if (result.typeOfHit == BLOCK && result.sideHit == EnumFacing.UP) result.blockPos
        else null
    }

    private val offsets = listOf(
        Vec3(0.0, 0.0, 0.0),
        Vec3(0.3, 0.0, 0.3),
        Vec3(-0.3, 0.0, 0.3),
        Vec3(0.3, 0.0, -0.3),
        Vec3(-0.3, 0.0, -0.3),
        Vec3(0.3, 0.0, 0.15),
        Vec3(-0.3, 0.0, 0.15),
        Vec3(0.15, 0.0, 0.3),
        Vec3(0.15, 0.0, -0.3)
    )

    class CollisionResult(val pos: BlockPos, val tick: Int)
}