/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.script.remapper.injection.utils

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList

/**
 * A bytecode node util
 *
 * @author CCBlueX
 */
object NodeUtils {

    /**
     * Lazy.
     */
    fun toNodes(vararg nodes : AbstractInsnNode) : InsnList {
        val insnList = InsnList()

        for (node in nodes)
            insnList.add(node)

        return insnList
    }
}