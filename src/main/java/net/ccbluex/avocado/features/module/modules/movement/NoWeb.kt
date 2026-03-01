/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module.modules.movement

import net.ccbluex.avocado.event.UpdateEvent
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.module.Category
import net.ccbluex.avocado.features.module.Module
import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.aac.AAC
import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.aac.LAAC
import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.intave.IntaveNew
import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.intave.IntaveOld
import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.other.None
import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.other.OldGrim
import net.ccbluex.avocado.features.module.modules.movement.nowebmodes.other.Rewi

object NoWeb : Module("NoWeb", Category.MOVEMENT) {

    private val noWebModes = arrayOf(
        // Vanilla
        None,

        // AAC
        AAC, LAAC,

        // Intave
        IntaveOld,
        IntaveNew,

        // Other
        Rewi,
        OldGrim
    )

    private val modes = noWebModes.map { it.modeName }.toTypedArray()

    val mode by choices(
        "Mode", modes, "None"
    )

    val onUpdate = handler<UpdateEvent> {
        modeModule.onUpdate()
    }

    override val tag
        get() = mode

    private val modeModule
        get() = noWebModes.find { it.modeName == mode }!!
}
