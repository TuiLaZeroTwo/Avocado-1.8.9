/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.command.commands

import net.ccbluex.avocado.Avocado.isStarting
import net.ccbluex.avocado.Avocado.moduleManager
import net.ccbluex.avocado.features.command.Command
import net.ccbluex.avocado.features.command.CommandManager
import net.ccbluex.avocado.file.FileManager.accountsConfig
import net.ccbluex.avocado.file.FileManager.clickGuiConfig
import net.ccbluex.avocado.file.FileManager.friendsConfig
import net.ccbluex.avocado.file.FileManager.hudConfig
import net.ccbluex.avocado.file.FileManager.loadConfig
import net.ccbluex.avocado.file.FileManager.modulesConfig
import net.ccbluex.avocado.file.FileManager.valuesConfig
import net.ccbluex.avocado.file.FileManager.xrayConfig
import net.ccbluex.avocado.script.ScriptManager.disableScripts
import net.ccbluex.avocado.script.ScriptManager.reloadScripts
import net.ccbluex.avocado.script.ScriptManager.unloadScripts
import net.ccbluex.avocado.ui.font.Fonts

object ReloadCommand : Command("reload", "configreload") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        isStarting = true

        chat("§c§lReloading commands...")
        CommandManager.registerCommands()

        disableScripts()
        unloadScripts()

        for (module in moduleManager)
            moduleManager.generateCommand(module)

        chat("§c§lReloading scripts...")
        reloadScripts()

        chat("§c§lReloading fonts...")
        Fonts.loadFonts()

        chat("§c§lReloading modules...")
        loadConfig(modulesConfig)


        chat("§c§lReloading values...")
        loadConfig(valuesConfig)

        chat("§c§lReloading accounts...")
        loadConfig(accountsConfig)

        chat("§c§lReloading friends...")
        loadConfig(friendsConfig)

        chat("§c§lReloading xray...")
        loadConfig(xrayConfig)

        chat("§c§lReloading HUD...")
        loadConfig(hudConfig)

        chat("§c§lReloading ClickGUI...")
        loadConfig(clickGuiConfig)

        isStarting = false
        chat("Reloaded.")
    }
}
