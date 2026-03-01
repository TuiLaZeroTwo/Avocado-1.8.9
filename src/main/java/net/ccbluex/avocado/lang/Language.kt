/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.lang

import net.ccbluex.avocado.file.configs.models.ClientConfiguration.overrideLanguage
import net.ccbluex.avocado.utils.client.ClientUtils.LOGGER
import net.ccbluex.avocado.utils.client.MinecraftInstance
import net.ccbluex.avocado.utils.io.decodeJson

fun translationMenu(key: String, vararg args: Any) = LanguageManager.getTranslation("menu.$key", *args)
fun translationButton(key: String, vararg args: Any) = LanguageManager.getTranslation("button.$key", *args)
fun translationText(key: String, vararg args: Any) = LanguageManager.getTranslation("text.$key", *args)
fun translation(key: String, vararg args: Any) = LanguageManager.getTranslation(key, *args)

object LanguageManager : MinecraftInstance {

    // Current language
    private val language: String
        get() = overrideLanguage.ifBlank { mc.gameSettings.language }

    // Common language
    private const val COMMON_UNDERSTOOD_LANGUAGE = "en_US"

    // List of all languages
    val knownLanguages = arrayOf(
        "en_US"
    )
    private val languageMap = hashMapOf<String, Language>()

    /**
     * Load all languages which are pre-defined in [knownLanguages] and stored in assets.
     * If a language is not found, it will be logged as error.
     *
     * Languages are stored in assets/minecraft/avocado/lang and when loaded will be stored in [languageMap]
     */
    fun loadLanguages() {
        for (language in knownLanguages) {
            runCatching {
                languageMap[language] = javaClass.getResourceAsStream("/assets/minecraft/avocado/lang/$language.json")!!
                    .bufferedReader().use { it.decodeJson() }
            }.onSuccess {
                LOGGER.info("Loaded language $language")
            }.onFailure {
                LOGGER.error("Failed to load language $language", it)
            }
        }
    }

    /**
     * Get translation from language
     */
    fun getTranslation(key: String, vararg args: Any)
        = languageMap[language]?.getTranslation(key, args = args)
        ?: languageMap[COMMON_UNDERSTOOD_LANGUAGE]?.getTranslation(key, args = args)
        ?: key
    
}

class Language(val locale: String, val contributors: List<String>, val translations: Map<String, String>) {

    fun getTranslation(key: String, vararg args: Any) = translations[key]?.format(args = args)

    override fun toString() = locale

}