/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.combat

import net.ccbluex.avocado.event.AttackEvent
import net.ccbluex.avocado.event.PacketEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.utils.client.PacketUtils.sendPacket
import net.ccbluex.avocado.utils.inventory.SilentHotbar
import net.ccbluex.avocado.utils.inventory.attackDamage
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C02PacketUseEntity.Action.ATTACK

object AutoWeapon : Module("AutoWeapon", Category.COMBAT, subjective = true) {

    private val onlySword by boolean("OnlySword", false)

    private val spoof by boolean("SpoofItem", false)
    private val spoofTicks by int("SpoofTicks", 10, 1..20) { spoof }

    private var attackEnemy = false

    override fun onDisable() {
        attackEnemy = false
    }

    val onAttack = handler<AttackEvent> {
        attackEnemy = true
    }

    val onPacket = handler<PacketEvent> { event ->
        val player = mc.thePlayer ?: return@handler

        if (event.packet is C02PacketUseEntity && event.packet.action == ATTACK && attackEnemy) {
            attackEnemy = false

            // Find the best weapon in hotbar (#Kotlin Style)
            val (slot, _) = (0..8)
                .map { it to mc.thePlayer.inventory.getStackInSlot(it) }
                .filter {
                    it.second != null && ((onlySword && it.second.item is ItemSword)
                            || (!onlySword && (it.second.item is ItemSword || it.second.item is ItemTool)))
                }
                .maxByOrNull { it.second.attackDamage } ?: return@handler

            if (slot == mc.thePlayer.inventory.currentItem) // If in hand no need to swap
                return@handler

            // Switch to best weapon
            SilentHotbar.selectSlotSilently(this, slot, spoofTicks, true, !spoof, spoof)

            if (!spoof) {
                player.inventory.currentItem = slot
                SilentHotbar.resetSlot(this)
            }

            // Resend attack packet
            sendPacket(event.packet)
            event.cancelEvent()
        }
    }
}