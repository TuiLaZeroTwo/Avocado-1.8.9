/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.ui.client.altmanager.menus

import me.liuli.elixir.account.MinecraftAccount
import net.ccbluex.avocado.lang.translationText
import net.ccbluex.avocado.ui.client.altmanager.GuiAltManager.Companion.login
import net.ccbluex.avocado.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import net.ccbluex.avocado.utils.render.RenderUtils.drawLoadingCircle
import net.ccbluex.avocado.utils.ui.AbstractScreen

class GuiLoginProgress(
    minecraftAccount: MinecraftAccount,
    success: () -> Unit,
    error: (Exception) -> Unit,
    done: () -> Unit
) : AbstractScreen() {

    init {
        login(minecraftAccount, success, error, done)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        assumeNonVolatile {
            drawDefaultBackground()
            drawLoadingCircle(width / 2f, height / 4f + 70)
            drawCenteredString(fontRendererObj, translationText(
                "Loggingintoaccount"), width / 2, height / 2 - 60, 16777215)
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

}