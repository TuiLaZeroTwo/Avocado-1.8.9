/*
 * Avocado Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/AvocadoMC/Avocado-1.8.9/
 */
package net.ccbluex.avocado.injection.forge.mixins.render;

import net.ccbluex.avocado.features.module.modules.render.FreeCam;
import net.ccbluex.avocado.injection.implementations.IMixinEntity;
import net.ccbluex.avocado.utils.client.PacketUtilsKt;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isPlayerSleeping()Z"))
    private boolean injectFreeCam(EntityLivingBase instance) {
        return FreeCam.INSTANCE.renderPlayerFromAllPerspectives(instance);
    }

    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;shouldRender(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;DDD)Z"))
    private boolean injectFreeCamB(RenderManager instance, Entity entity, ICamera camera, double x, double y, double z) {
        if (entity instanceof EntityLivingBase) {
            IMixinEntity iEntity = (IMixinEntity) entity;

            if (iEntity.getTruePos()) {
                PacketUtilsKt.interpolatePosition(iEntity);
            }
        }

        return FreeCam.INSTANCE.handleEvents() || instance.shouldRender(entity, camera, x, y, z);
    }
}
