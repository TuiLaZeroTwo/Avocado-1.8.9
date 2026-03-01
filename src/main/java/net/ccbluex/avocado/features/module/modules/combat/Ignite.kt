/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.combat

import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.utils.attack.EntityUtils
import net.ccbluex.avocado.utils.block.block
import net.ccbluex.avocado.utils.block.canBeClicked
import net.ccbluex.avocado.utils.block.isReplaceable
import net.ccbluex.avocado.utils.client.PacketUtils.sendPacket
import net.ccbluex.avocado.utils.extensions.onPlayerRightClick
import net.ccbluex.avocado.utils.extensions.sendUseItem
import net.ccbluex.avocado.utils.inventory.InventoryUtils
import net.ccbluex.avocado.utils.inventory.SilentHotbar
import net.ccbluex.avocado.utils.inventory.hotBarSlot
import net.ccbluex.avocado.utils.rotation.RotationUtils
import net.ccbluex.avocado.utils.timing.MSTimer
import net.minecraft.block.BlockAir
import net.minecraft.init.Items
import net.minecraft.item.ItemBucket
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

object Ignite : Module("Ignite", Category.COMBAT) {

    private val lighter by boolean("Lighter", true)
    private val lavaBucket by boolean("Lava", true)

    private val msTimer = MSTimer()

    val onUpdate = handler<UpdateEvent> {
        if (!msTimer.hasTimePassed(500))
            return@handler

        val player = mc.thePlayer ?: return@handler
        val world = mc.theWorld ?: return@handler

        val lighterInHotbar = if (lighter) InventoryUtils.findItem(36, 44, Items.flint_and_steel) else null
        val lavaInHotbar = if (lavaBucket) InventoryUtils.findItem(36, 44, Items.lava_bucket) else null

        val fireInHotbar = lighterInHotbar ?: lavaInHotbar ?: return@handler

        for (entity in world.loadedEntityList) {
            if (EntityUtils.isSelected(entity, true) && !entity.isBurning) {
                val blockPos = entity.position

                if (player.getDistanceSq(blockPos) >= 22.3 || !blockPos.isReplaceable || blockPos.block !is BlockAir)
                    continue

                RotationUtils.resetTicks += 1

                SilentHotbar.selectSlotSilently(this, fireInHotbar, 0, immediate = true, render = false)

                val itemStack = player.hotBarSlot(fireInHotbar).stack

                if (itemStack.item is ItemBucket) {
                    val targetVec = Vec3(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5)
                    val rotation = RotationUtils.toRotation(targetVec)
                    
                    sendPacket(C05PacketPlayerLook(rotation.yaw, rotation.pitch, player.onGround))
                    player.sendUseItem(itemStack)
                } else {
                    for (side in EnumFacing.entries) {
                        val neighbor = blockPos.offset(side)

                        if (!neighbor.canBeClicked())
                            continue

                        val targetVec = Vec3(neighbor.x + 0.5, neighbor.y + 0.5, neighbor.z + 0.5)
                        val rotation = RotationUtils.toRotation(targetVec)

                        sendPacket(C05PacketPlayerLook(rotation.yaw, rotation.pitch, player.onGround))

                        if (player.onPlayerRightClick(neighbor, side.opposite, Vec3(side.directionVec), itemStack)) {
                            player.swingItem()
                            break
                        }
                    }
                }

                SilentHotbar.selectSlotSilently(
                    this,
                    player.inventory.currentItem,
                    immediate = true,
                    render = false,
                    resetManually = true
                )
                sendPacket(C05PacketPlayerLook(player.rotationYaw, player.rotationPitch, player.onGround))
                SilentHotbar.resetSlot(this)

                msTimer.reset()
                break
            }
        }
    }
}
