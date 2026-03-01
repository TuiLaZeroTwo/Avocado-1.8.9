/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.injection.implementations

interface IMixinEntity {
	var lerpX: Double
	var lerpY: Double
	var lerpZ: Double
	var trueX: Double
	var trueY: Double
	var trueZ: Double
	var truePos: Boolean
}