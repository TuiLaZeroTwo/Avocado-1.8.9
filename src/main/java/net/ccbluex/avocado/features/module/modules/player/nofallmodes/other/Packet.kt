/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.player.nofallmodes.other

import net.ccbluex.avocado.features.module.modules.player.nofallmodes.NoFallMode
import net.ccbluex.avocado.utils.client.PacketUtils.sendPacket
import net.minecraft.network.play.client.C03PacketPlayer

object Packet : NoFallMode("Packet") {
    override fun onUpdate() {
        if (mc.thePlayer.fallDistance > 2f)
            sendPacket(C03PacketPlayer(true))
    }
}