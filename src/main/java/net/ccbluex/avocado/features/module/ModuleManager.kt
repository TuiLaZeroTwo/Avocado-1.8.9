/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.features.module

import net.ccbluex.avocado.event.KeyEvent
import net.ccbluex.avocado.event.Listenable
import net.ccbluex.avocado.event.handler
import net.ccbluex.avocado.features.command.CommandManager.registerCommand
import net.ccbluex.avocado.features.module.modules.combat.*
import net.ccbluex.avocado.features.module.modules.exploit.*
import net.ccbluex.avocado.features.module.modules.misc.*
import net.ccbluex.avocado.features.module.modules.movement.*
import net.ccbluex.avocado.features.module.modules.player.*
import net.ccbluex.avocado.features.module.modules.render.*
import net.ccbluex.avocado.features.module.modules.world.*
import net.ccbluex.avocado.features.module.modules.world.Timer
import net.ccbluex.avocado.features.module.modules.world.scaffolds.Scaffold
import net.ccbluex.avocado.utils.client.ClientUtils.LOGGER
import java.util.*

private val MODULE_REGISTRY = TreeSet(Comparator.comparing(Module::name))

object ModuleManager : Listenable, Collection<Module> by MODULE_REGISTRY {

    /**
     * Register all modules
     */
    fun registerModules() {
        LOGGER.info("[ModuleManager] Loading modules...")

        // Register modules
        val modules = arrayOf(
            AbortBreaking,
            Aimbot,
            Ambience,
            Animations,
            AntiAFK,
            AntiBlind,
            AntiBot,
            AntiBounce,
            AntiCactus,
            AntiExploit,
            AntiHunger,
            AntiFireball,
            AntiVoid,
            AttackEffects,
            AutoAccount,
            AutoArmor,
            AutoBow,
            AutoBreak,
            AutoClicker,
            AutoDisable,
            AutoFish,
            AutoProjectile,
            AutoPlay,
            AutoLeave,
            AutoPot,
            AutoJump,
            AutoRespawn,
            AutoRod,
            AutoSoup,
            AutoTool,
            AutoWeapon,
            AugustusVelocity,
            AugustusNoslow,
            AvoidHazards,
            Backtrack,
            BedDefender,
            BedGodMode,
            BedPlates,
            BedProtectionESP,
            Blink,
            BlockESP,
            Breadcrumbs,
            BlockOverlay,
            PointerESP,
            ProjectileAimbot,
            BufferSpeed,
            CameraClip,
            CameraView,
            Chams,
            ChestAura,
            ChestStealer,
            CivBreak,
            ClickGUI,
            Clip,
            ComponentOnHover,
            ConsoleSpammer,
            Criticals,
            Damage,
            ESP,
            Eagle,
            FakeLag,
            FastBow,
            FastBreak,
            FastClimb,
            FastPlace,
            FastStairs,
            FastUse,
            FlagCheck,
            Fly,
            ForceUnicodeChat,
            FreeCam,
            Freeze,
            Fucker,
            Fullbright,
            GameDetector,
            Ghost,
            GhostHand,
            GodMode,
            HUD,
            HighJump,
            HitBox,
            IceSpeed,
            Ignite,
            InventoryCleaner,
            InventoryMove,
            ItemESP,
            ItemPhysics,
            ItemTeleport,
            KeepAlive,
            KeepContainer,
            KeepTabList,
            KeyPearl,
            Kick,
            KillAura,
            WaterMark,
            WaterMark2,
            LiquidWalk,
            LongJump,
            Liquids,
            LegitReach,
            MidClick,
            MoreCarry,
            MultiActions,
            NameProtect,
            NameTags,
            NoBob,
            NoClip,
            NoFOV,
            NoFall,
            NoFluid,
            NoFriends,
            NoRotateSet,
            NoHurtCam,
            NoJumpDelay,
            NoPitchLimit,
            NoSlow,
            NoSlowBreak,
            NoSwing,
            NoWeb,
            Nuker,
            PacketDebugger,
            Parkour,
            Phase,
            PingSpoof,
            Plugins,
            Projectiles,
            Reach,
            Refill,
            Regen,
            ResourcePackSpoof,
            ReverseStep,
            Rotations,
            SafeWalk,
            Scaffold,
            ServerCrasher,
            Sneak,
            Spammer,
            Speed,
            Sprint,
            Step,
            StorageESP,
            Strafe,
            SuperKnockback,
            Teleport,
            TeleportHit,
            Teams,
            TimerRange,
            Timer,
            Tracers,
            TrueSight,
            VehicleOneHit,
            Velocity,
            WallClimb,
            XRay,
            Zoot,
            KeepSprint,
            Disabler,
            OverrideRaycast,
            TickBase,
            ForwardTrack,
            FreeLook,
        )

        registerModules(modules = modules)

        LOGGER.info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        MODULE_REGISTRY += module
        generateCommand(module)
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Module) = modules.forEach(this::registerModule)

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        MODULE_REGISTRY.remove(module)
        module.onUnregister()
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        registerCommand(ModuleCommand(module, values))
    }

    /**
     * Get module by [moduleClass]
     */
    operator fun get(moduleClass: Class<out Module>) = MODULE_REGISTRY.find { it.javaClass === moduleClass }

    /**
     * Get module by [moduleName]
     */
    operator fun get(moduleName: String) = MODULE_REGISTRY.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Get modules by [category]
     */
    operator fun get(category: Category) = MODULE_REGISTRY.filter { it.category === category }

    @Deprecated(message = "Only for outdated scripts", replaceWith = ReplaceWith("get(moduleClass)"))
    fun getModule(moduleClass: Class<out Module>) = get(moduleClass)

    @Deprecated(message = "Only for outdated scripts", replaceWith = ReplaceWith("get(moduleName)"))
    fun getModule(moduleName: String) = get(moduleName)

    /**
     * Handle incoming key presses
     */
    private val onKey = handler<KeyEvent> { event ->
        MODULE_REGISTRY.forEach { if (it.keyBind == event.key) it.toggle() }
    }

}
