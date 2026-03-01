/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.utils.client

import net.minecraft.client.gui.GuiTextField
import org.lwjgl.input.Keyboard

object TabUtils {
    fun tab(vararg textFields: GuiTextField) {
        textFields.forEachIndexed { i, textField ->
            if (textField.isFocused) {
                textField.isFocused = false

                // Cycle to previous textField when holding shift.
                textFields[
                    (i + (if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) -1 else 1) + textFields.size)
                            % textFields.size
                ].isFocused = true

                return
            }
        }

        // Focus first when no text field is focused.
        textFields[0].isFocused = true
    }
}
