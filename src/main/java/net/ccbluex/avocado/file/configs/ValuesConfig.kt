/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.file.configs

import com.google.gson.JsonObject
import net.ccbluex.avocado.Avocado
import net.ccbluex.avocado.Avocado.commandManager
import net.ccbluex.avocado.Avocado.moduleManager
import net.ccbluex.avocado.features.special.ClientFixes
import net.ccbluex.avocado.file.FileConfig
import net.ccbluex.avocado.file.FileManager
import net.ccbluex.avocado.file.FileManager.PRETTY_GSON
import net.ccbluex.avocado.file.configs.models.ClientConfiguration
import net.ccbluex.avocado.utils.attack.EntityUtils.Targets
import net.ccbluex.avocado.utils.io.readJson
import java.io.*

class ValuesConfig(file: File) : FileConfig(file) {

    @Throws(IOException::class)
    override fun loadConfig() {
        val json = file.readJson() as? JsonObject ?: return

        val prevVersion = json["ClientVersion"]?.asString ?: "unknown"
        if (prevVersion != Avocado.clientVersionText) {
            FileManager.backupAllConfigs(prevVersion, Avocado.clientVersionText)
        }

        for ((key, value) in json.entrySet()) {
            when {
                key.equals("CommandPrefix", true) -> {
                    commandManager.prefix = value.asString
                }

                key.equals(Targets.name, true) -> {
                    Targets.fromJson(value)
                }

                key.equals(ClientFixes.name, true) -> {
                    ClientFixes.fromJson(value)
                }

                key.equals(ClientConfiguration.name, true) -> {
                    ClientConfiguration.fromJson(value)
                }

                // Compatibility with old versions
                key.equals("background", true) -> {
                    val jsonValue = value as JsonObject
                    if (jsonValue.has("Particles")) ClientConfiguration.particles = jsonValue["Particles"].asBoolean
                }

                else -> {
                    val module = moduleManager[key] ?: continue
                    val jsonModule = value as JsonObject

                    for (moduleValue in module.values) {
                        val element = jsonModule[moduleValue.name]
                        if (element != null) moduleValue.fromJson(element)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun saveConfig() {
        val jsonObject = JsonObject()

        jsonObject.addProperty("CommandPrefix", commandManager.prefix)
        jsonObject.addProperty("ClientVersion", Avocado.clientVersionText)

        jsonObject.add(Targets.name, Targets.toJson())
        jsonObject.add(ClientFixes.name, ClientFixes.toJson())

        jsonObject.add(ClientConfiguration.name, ClientConfiguration.toJson())

        for (module in moduleManager) {
            if (module.values.isEmpty()) continue

            val jsonModule = JsonObject()
            for (value in module.values) {
                jsonModule.add(value.name, value.toJson())
            }
            jsonObject.add(module.name, jsonModule)
        }

        file.writeText(PRETTY_GSON.toJson(jsonObject))
    }
}