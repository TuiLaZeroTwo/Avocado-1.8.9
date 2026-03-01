/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.event

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import net.ccbluex.avocado.event.async.launchSequence

interface Listenable {
    fun handleEvents(): Boolean = parent?.handleEvents() ?: true

    val subListeners: Array<Listenable>
        get() = emptyArray()

    val parent: Listenable?
        get() = null
}

inline fun <reified T : Event> Listenable.handler(
    always: Boolean = false,
    priority: Byte = 0,
    noinline action: (T) -> Unit
) {
    EventManager.registerEventHook(T::class.java, EventHook(this, always, priority, action))
}

inline fun <reified T : Event> Listenable.handler(
    dispatcher: CoroutineDispatcher,
    always: Boolean = false,
    priority: Byte = 0,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = handler<T>(always, priority) { launchSequence(dispatcher, always) { action(it) } }
