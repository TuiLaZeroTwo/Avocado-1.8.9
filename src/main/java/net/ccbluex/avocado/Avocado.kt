/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado

import com.formdev.flatlaf.themes.FlatMacLightLaf
import kotlinx.coroutines.launch
import net.ccbluex.avocado.api.ClientUpdate
import net.ccbluex.avocado.api.ClientUpdate.gitInfo
import net.ccbluex.avocado.api.loadSettings
import net.ccbluex.avocado.event.ClientShutdownEvent
import net.ccbluex.avocado.event.EventManager
import net.ccbluex.avocado.event.StartupEvent
import net.ccbluex.avocado.features.command.CommandManager
import net.ccbluex.avocado.features.command.CommandManager.registerCommands
import net.ccbluex.avocado.features.module.ModuleManager
import net.ccbluex.avocado.features.module.ModuleManager.registerModules
import net.ccbluex.avocado.features.special.BungeeCordSpoof
import net.ccbluex.avocado.features.special.ClientFixes
import net.ccbluex.avocado.file.FileManager
import net.ccbluex.avocado.file.FileManager.loadAllConfigs
import net.ccbluex.avocado.file.FileManager.saveAllConfigs
import net.ccbluex.avocado.file.configs.models.ClientConfiguration.updateClientWindow
import net.ccbluex.avocado.lang.LanguageManager.loadLanguages
import net.ccbluex.avocado.script.ScriptManager
import net.ccbluex.avocado.script.ScriptManager.enableScripts
import net.ccbluex.avocado.script.ScriptManager.loadScripts
import net.ccbluex.avocado.script.remapper.Remapper
import net.ccbluex.avocado.script.remapper.Remapper.loadSrg
import net.ccbluex.avocado.tabs.BlocksTab
import net.ccbluex.avocado.tabs.ExploitsTab
import net.ccbluex.avocado.tabs.HeadsTab
import net.ccbluex.avocado.ui.client.altmanager.GuiAltManager.Companion.loadActiveGenerators
import net.ccbluex.avocado.ui.client.clickgui.ClickGui
import net.ccbluex.avocado.ui.client.hud.HUD
import net.ccbluex.avocado.ui.font.Fonts
import net.ccbluex.avocado.utils.client.BlinkUtils
import net.ccbluex.avocado.utils.client.ClassUtils.hasForge
import net.ccbluex.avocado.utils.client.ClientUtils.LOGGER
import net.ccbluex.avocado.utils.client.ClientUtils.disableFastRender
import net.ccbluex.avocado.utils.client.PacketUtils
import net.ccbluex.avocado.utils.inventory.InventoryManager
import net.ccbluex.avocado.utils.inventory.InventoryUtils
import net.ccbluex.avocado.utils.inventory.SilentHotbar
import net.ccbluex.avocado.utils.io.MiscUtils.showErrorPopup
import net.ccbluex.avocado.utils.kotlin.SharedScopes
import net.ccbluex.avocado.utils.movement.BPSUtils
import net.ccbluex.avocado.utils.movement.MovementUtils
import net.ccbluex.avocado.utils.movement.TimerBalanceUtils
import net.ccbluex.avocado.utils.render.MiniMapRegister
import net.ccbluex.avocado.utils.rotation.RotationUtils
import net.ccbluex.avocado.utils.timing.TickedActions
import net.ccbluex.avocado.utils.timing.WaitTickUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import javax.swing.UIManager

object Avocado {

    /**
     * Client Information
     *
     * This has all the basic information.
     */
    const val CLIENT_NAME = "Avocado"
    const val CLIENT_AUTHOR = "CCBlueX, BPM, TLZ"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"
    const val CLIENT_WEBSITE = "avocado.net"
    const val CLIENT_GITHUB = "https://github.com/AvocadoMC/Avocado-1.8.9"

    const val MINECRAFT_VERSION = "1.8.9"

    val clientVersionText = gitInfo["git.build.version"]?.toString() ?: "(Main)"
    val clientVersionNumber = clientVersionText.substring(1).toIntOrNull() ?: 0 // version format: "b<VERSION>" on legacy
    val clientCommit = gitInfo["git.commit.id.abbrev"]?.let { "git-$it" } ?: "(Main)"
    val clientBranch = gitInfo["git.branch"]?.toString() ?: "(Main)"

    /**
     * Defines if the client is in development mode.
     * This will enable update checking on commit time instead of regular legacy versioning.
     */
    const val IN_DEV = false

    val clientTitle = CLIENT_NAME + " " + clientVersionText + " " + clientCommit + " | " + MINECRAFT_VERSION + if (IN_DEV) " " else ""

    var isStarting = true

    // Managers
    val moduleManager = ModuleManager
    val commandManager = CommandManager
    val eventManager = EventManager
    val fileManager = FileManager
    val scriptManager = ScriptManager

    // HUD & ClickGUI
    val hud = HUD

    val clickGui = ClickGui

    /**
     * Start IO tasks
     */
    fun preload(): Future<*> {

        net.ccbluex.avocado.utils.client.javaVersion

        // Change theme of Swing
        UIManager.setLookAndFeel(FlatMacLightLaf())

        val future = CompletableFuture<Unit>()

        SharedScopes.IO.launch {
            try {
                LOGGER.info("Starting preload tasks of $CLIENT_NAME")

                // Download and extract fonts
                Fonts.downloadFonts()

                // Check update
                ClientUpdate.reloadNewestVersion()

                // Load languages
                loadLanguages()

                // Load alt generators
                loadActiveGenerators()

                // Load SRG file
                loadSrg()

                LOGGER.info("Preload tasks of $CLIENT_NAME are completed!")

                future.complete(Unit)
            } catch (e: Exception) {
                future.completeExceptionally(e)
            }
        }

        return future
    }

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        LOGGER.info("Starting $CLIENT_NAME $clientVersionText $clientCommit, by $CLIENT_AUTHOR")

        try {
            // Load client fonts
            Fonts.loadFonts()

            // Register listeners
            RotationUtils
            ClientFixes
            BungeeCordSpoof
            InventoryUtils
            InventoryManager
            MiniMapRegister
            TickedActions
            MovementUtils
            PacketUtils
            TimerBalanceUtils
            BPSUtils
            WaitTickUtils
            SilentHotbar
            BlinkUtils

            // Load settings
            loadSettings(false) {
                LOGGER.info("Successfully loaded ${it.size} settings.")
            }

            // Register commands
            registerCommands()

            // Setup module manager and register modules
            registerModules()

            runCatching {
                // Remapper
                loadSrg()

                if (!Remapper.mappingsLoaded) {
                    error("Failed to load SRG mappings.")
                }

                // ScriptManager
                loadScripts()
                enableScripts()
            }.onFailure {
                LOGGER.error("Failed to load scripts.", it)
            }

            // Load configs
            loadAllConfigs()

            // Update client window
            updateClientWindow()

            // Tabs (Only for Forge!)
            if (hasForge()) {
                BlocksTab()
                ExploitsTab()
                HeadsTab()
            }

            // Disable Optifine FastRender
            disableFastRender()

        } catch (e: Exception) {
            LOGGER.error("Failed to start client: ${e.message}")
            e.showErrorPopup()
        } finally {
            // Set is starting status
            isStarting = false

            EventManager.call(StartupEvent)
            LOGGER.info("Successfully started client")
        }
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        EventManager.call(ClientShutdownEvent)

        // Stop all CoroutineScopes
        SharedScopes.stop()

        // Save all available configs
        saveAllConfigs()
    }

}
