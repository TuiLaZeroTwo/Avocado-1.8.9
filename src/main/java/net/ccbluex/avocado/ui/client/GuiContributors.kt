/*
 * Avocado Hacked Client
 */
package net.ccbluex.avocado.ui.client

import net.ccbluex.avocado.injection.implementations.IMixinGuiSlot
import net.ccbluex.avocado.lang.translationMenu
import net.ccbluex.avocado.ui.font.AWTFontRenderer.Companion.assumeNonVolatile
import net.ccbluex.avocado.ui.font.Fonts
import net.ccbluex.avocado.utils.render.RenderUtils.drawLoadingCircle
import net.ccbluex.avocado.utils.render.RenderUtils.drawRect
import net.ccbluex.avocado.utils.ui.AbstractScreen
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSlot
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11.*
import java.awt.Color

class GuiContributors(private val prevGui: GuiScreen) : AbstractScreen() {

    private lateinit var list: GuiList
    private var credits = emptyList<Credit>()

    override fun initGui() {
        list = GuiList(this)
        list.registerScrollButtons(7, 8)

        +GuiButton(1, width / 2 - 100, height - 30, "Back")

        loadCredits()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        assumeNonVolatile {
            drawBackground(0)

            list.drawScreen(mouseX, mouseY, partialTicks)

            drawRect(width / 4f, 40f, width.toFloat(), height - 40f, Integer.MIN_VALUE)

            if (credits.isNotEmpty()) {
                val credit = credits[list.selectedSlot]

                var y = 50
                val x = width / 4 + 10

                Fonts.fontSemibold40.drawString("@${credit.name}", x.toFloat(), 50f, Color.WHITE.rgb, true)

                Fonts.fontSemibold40.drawString(
                    "${credit.commits} commits",
                    x.toFloat(),
                    70f,
                    Color.LIGHT_GRAY.rgb,
                    true
                )

                for (line in credit.contributions) {
                    y += Fonts.fontSemibold40.fontHeight + 2
                    Fonts.fontSemibold40.drawString(line, x.toFloat(), y.toFloat(), Color.WHITE.rgb, true)
                }
            }

            Fonts.fontSemibold40.drawCenteredString(
                translationMenu("contributors"),
                width / 2F,
                8F,
                0xffffff
            )

            if (credits.isEmpty()) {
                Fonts.fontSemibold40.drawCenteredString("Loading...", width / 8f, height / 2f, Color.WHITE.rgb)
                drawLoadingCircle(width / 8f, height / 2f - 40)
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 1) mc.displayGuiScreen(prevGui)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_ESCAPE -> mc.displayGuiScreen(prevGui)
            Keyboard.KEY_UP -> list.selectedSlot -= 1
            Keyboard.KEY_DOWN -> list.selectedSlot += 1
            Keyboard.KEY_TAB -> list.selectedSlot += if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) -1 else 1
            else -> super.keyTyped(typedChar, keyCode)
        }
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        list.handleMouseInput()
    }

    private fun loadCredits() {
        credits = listOf(
            Credit(
                "1zun4",
                100,
                listOf(
                    "Owner of LiquidBounce",
                    "Main Developer",
                    "Bypass Engineer"
                )
            ),
            Credit(
                "CCBlueX",
                5000,
                listOf(
                    "Creator of LiquidBounce",
                    "Base framework"
                )
            ),
            Credit(
                "BeoPhiMan",
                300,
                listOf(
                    "Combat Improvements",
                    "Bug Fixes"
                )
            ),
            Credit(
                "TLZ",
                200,
                listOf(
                    "Client Optimization",
                    "Testing"
                )
            )
        )
    }

    private inner class GuiList(gui: GuiScreen) :
        GuiSlot(mc, gui.width / 4, gui.height, 40, gui.height - 40, 16) {

        init {
            val mixin = this as IMixinGuiSlot
            mixin.listWidth = gui.width * 3 / 13
            mixin.enableScissor = true
        }

        var selectedSlot = 0
            set(value) {
                if (credits.isNotEmpty())
                    field = (value + credits.size) % credits.size
            }

        override fun getSize() = credits.size

        override fun elementClicked(index: Int, doubleClick: Boolean, mouseX: Int, mouseY: Int) {
            selectedSlot = index
        }

        override fun isSelected(id: Int) = selectedSlot == id

        override fun drawSlot(
            entryID: Int,
            x: Int,
            y: Int,
            height: Int,
            mouseX: Int,
            mouseY: Int
        ) {
            val credit = credits[entryID]
            Fonts.fontSemibold40.drawCenteredString(
                credit.name,
                width / 2F,
                y + 3F,
                Color.WHITE.rgb,
                true
            )
        }

        override fun drawBackground() {}
    }
}

private class Credit(
    val name: String,
    val commits: Int,
    val contributions: List<String>
)