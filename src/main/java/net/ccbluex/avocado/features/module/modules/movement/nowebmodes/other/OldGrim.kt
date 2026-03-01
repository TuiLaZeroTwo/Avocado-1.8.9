/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement.nowebmodes.other

import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.NoWebMode
import net.ccbluex.avocado.utils.block.BlockUtils
import net.ccbluex.avocado.utils.client.PacketUtils.sendPacket
import net.minecraft.init.Blocks.web
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action
import net.minecraft.util.EnumFacing

object OldGrim : NoWebMode("OldGrim") {
    override fun onUpdate() {
        val searchBlocks = BlockUtils.searchBlocks(2, setOf(web))
        mc.thePlayer.isInWeb = false
        for (block in searchBlocks) {
            val blockpos = block.key
            sendPacket(C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, blockpos, EnumFacing.DOWN))
        }
    }
}
