/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module

import net.ccbluex.avocado.Avocado.CLIENT_NAME
import net.minecraft.util.ResourceLocation

enum class Category(val displayName: String) {

    COMBAT("Combat"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    WORLD("World"),
    MISC("Misc"),
    EXPLOIT("Exploit");

    val iconResourceLocation = ResourceLocation("${CLIENT_NAME.lowercase()}/tabgui/${name.lowercase()}.png")

}
