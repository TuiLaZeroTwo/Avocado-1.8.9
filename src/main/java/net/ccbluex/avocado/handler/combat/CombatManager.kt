/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.handler.combat

import net.ccbluex.avocado.event.*
import net.ccbluex.avocado.utils.attack.EntityUtils
import net.ccbluex.avocado.utils.client.MinecraftInstance
import net.ccbluex.avocado.utils.movement.MovementUtils
import net.ccbluex.avocado.utils.timing.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object CombatManager : MinecraftInstance, Listenable {
    private val lastAttackTimer = MSTimer()

    var inCombat = false
        private set
    var target: EntityLivingBase? = null
        private set
    private val attackedEntityList = mutableListOf<EntityLivingBase>()
    val focusedPlayerList = mutableListOf<EntityPlayer>()

    val onHitEntityListeners = mutableListOf<(EntityLivingBase) -> Unit>()

    val onUpdate = handler<UpdateEvent> {
        if (mc.thePlayer == null) return@handler
        MovementUtils.updateBlocksPerSecond()

        // bypass java.util.ConcurrentModificationException
        val entitiesToRemove = mutableListOf<EntityLivingBase>()

        attackedEntityList.forEach {
            if (it.isDead) {
                EventManager.call(EntityKilledEvent(it))
                entitiesToRemove.add(it)
            }
        }
        attackedEntityList.removeAll(entitiesToRemove)


        inCombat =  lastAttackTimer.hasTimePassed(500).not()

        if (target != null && !inCombat) {
            if (mc.thePlayer.getDistanceToEntity(target) > 7 || target!!.isDead) {
                target = null
            } else {
                inCombat = true
            }
        }
    }

    val onAttack = handler<AttackEvent> { event ->
        val target = event.targetEntity

        if (target is EntityLivingBase && EntityUtils.isSelected(target, true)) {
            this.target = target
            if (!attackedEntityList.contains(target)) {
                attackedEntityList.add(target)
            }

            onHitEntityListeners.forEach { listener ->
                listener(target)
            }
        }
        lastAttackTimer.reset()
    }


    val onWorld = handler<WorldEvent> {
        inCombat = false
        target = null
        attackedEntityList.clear()
        focusedPlayerList.clear()
    }
    fun setTarget(entity: EntityLivingBase) {
        if (!attackedEntityList.contains(entity)) {
            attackedEntityList.add(entity)
        }
        target = entity
        lastAttackTimer.reset()
    }

    fun isFocusEntity(entity: EntityPlayer): Boolean {
        if (focusedPlayerList.isEmpty()) {
            return true // no need 2 focus
        }

        return focusedPlayerList.contains(entity)
    }

    override fun handleEvents() = true
}