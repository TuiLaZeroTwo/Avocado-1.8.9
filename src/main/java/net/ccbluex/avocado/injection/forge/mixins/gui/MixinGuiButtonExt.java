/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */

package net.ccbluex.avocado.injection.forge.mixins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GuiButtonExt.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButtonExt extends GuiButton {

    public MixinGuiButtonExt(int buttonId, int x, int y, String text) {
        super(buttonId, x, y, text);
    }

    public MixinGuiButtonExt(int buttonId, int x, int y, int widthIn, int heightIn, String text) {
        super(buttonId, x, y, widthIn, heightIn, text);
    }
    @Overwrite
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible)
            return;

        hovered = mouseX >= xPosition && mouseY >= yPosition &&
                mouseX < xPosition + width && mouseY < yPosition + height;

        mc.getTextureManager().bindTexture(buttonTextures);
        drawTexturedModalRect(xPosition, yPosition, 0, 46 + getHoverState(hovered) * 20, width / 2, height);
        drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2,
                46 + getHoverState(hovered) * 20, width / 2, height);

        mouseDragged(mc, mouseX, mouseY);

        int color = 14737632;

        if (!enabled)
            color = 10526880;
        else if (hovered)
            color = 16777120;

        drawCenteredString(mc.fontRendererObj, displayString,
                xPosition + width / 2,
                yPosition + (height - 8) / 2,
                color);
    }
}
