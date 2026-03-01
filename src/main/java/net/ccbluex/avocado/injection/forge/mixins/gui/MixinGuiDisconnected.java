/*
 * Avocado Hacked Client
 */
package net.ccbluex.avocado.injection.forge.mixins.gui;

import net.ccbluex.avocado.features.special.AutoReconnect;
import net.ccbluex.avocado.features.special.ClientFixes;
import net.ccbluex.avocado.file.FileManager;
import net.ccbluex.avocado.utils.client.ServerUtils;
import net.ccbluex.avocado.utils.kotlin.RandomUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDisconnected.class)
public abstract class MixinGuiDisconnected extends MixinGuiScreen {

    private GuiButton reconnectButton;
    private GuiButton autoReconnectToggle;
    private GuiButton antiForgeButton;
    private GuiSlider delaySlider;

    private int reconnectTimer = 0;
    private boolean reconnecting = false;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo ci) {

        reconnectTimer = 0;
        reconnecting = false;

        int centerX = width / 2;
        int startY = height / 2 + 60;

        reconnectButton = new GuiButton(1, centerX - 100, startY, 200, 20, "Reconnect");
        buttonList.add(reconnectButton);

        autoReconnectToggle = new GuiButton(2, centerX - 100, startY + 28, 200, 20, "");
        buttonList.add(autoReconnectToggle);

        delaySlider = new GuiSlider(
                3,
                centerX - 97,
                startY + 48,
                196,
                20,
                "Delay: ",
                "ms",
                AutoReconnect.MIN,
                AutoReconnect.MAX,
                AutoReconnect.INSTANCE.getDelay(),
                false,
                true,
                slider -> {
                    AutoReconnect.INSTANCE.setDelay(slider.getValueInt());
                    reconnectTimer = 0;
                    updateTexts();
                }
        );
        buttonList.add(delaySlider);

        buttonList.add(new GuiButton(4, centerX - 100, startY + 72, 98, 20, "Random Name"));

        antiForgeButton = new GuiButton(
                5,
                centerX + 2,
                startY + 72,
                98,
                20,
                ""
        );
        buttonList.add(antiForgeButton);

        updateTexts();
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo ci) {
        switch (button.id) {

            case 1:
                ServerUtils.INSTANCE.connectToLastServer();
                break;

            case 2:
                AutoReconnect.INSTANCE.toggle();
                reconnectTimer = 0;
                reconnecting = false;
                updateTexts();
                break;

            case 4:
                RandomUtils.INSTANCE.randomAccount();
                ServerUtils.INSTANCE.connectToLastServer();
                break;

            case 5:
                ClientFixes.INSTANCE.setFmlFixesEnabled(
                        !ClientFixes.INSTANCE.getFmlFixesEnabled());

                try {
                    FileManager.INSTANCE.getValuesConfig().saveConfig();
                } catch (Exception ignored) {}
                updateTexts();
                break;
        }
    }

    @Override
    public void updateScreen() {

        if (!AutoReconnect.INSTANCE.isEnabled()) {
            reconnectTimer = 0;
            reconnecting = false;
            return;
        }

        reconnectTimer++;

        int delayTicks = AutoReconnect.INSTANCE.getDelay() / 50;

        if (!reconnecting && reconnectTimer >= delayTicks) {
            reconnecting = true;
            ServerUtils.INSTANCE.connectToLastServer();
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo ci) {
        updateTexts();
    }

    private void updateTexts() {

        if (autoReconnectToggle != null) {
            autoReconnectToggle.displayString =
                    "AutoReconnect: " + (AutoReconnect.INSTANCE.isEnabled() ? "§aON" : "§cOFF");
        }

        if (antiForgeButton != null) {
            antiForgeButton.displayString =
                    "AntiForge: " + (ClientFixes.INSTANCE.getFmlFixesEnabled() ? "§aON" : "§cOFF");
        }

        if (delaySlider != null) {
            delaySlider.visible = AutoReconnect.INSTANCE.isEnabled();

            if (AutoReconnect.INSTANCE.isEnabled()) {
                delaySlider.displayString =
                        "Delay: " + (AutoReconnect.INSTANCE.getDelay() / 1000) + "s";
            }
        }

        if (reconnectButton != null) {
            if (!AutoReconnect.INSTANCE.isEnabled()) {
                reconnectButton.displayString = "Reconnect";
                return;
            }

            int seconds = Math.max(
                    (AutoReconnect.INSTANCE.getDelay() / 1000) - (reconnectTimer / 20),
                    0
            );

            reconnectButton.displayString = "Reconnect §7(" + seconds + ")";
        }
    }
}