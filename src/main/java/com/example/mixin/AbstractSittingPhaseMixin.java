package com.example.mixin;

import net.minecraft.entity.boss.dragon.phase.AbstractSittingPhase;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AbstractSittingPhase.class)
public class AbstractSittingPhaseMixin {
    @Inject(method = "modifyDamageTaken",at = @At("HEAD"), cancellable = true)
    protected void InjectModifyDamageTaken(DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir){
        if(damageSource.getAttacker() instanceof ServerPlayerEntity) {
            if (damageSource.getAttacker().getWorld().getRegistryKey() != World.END) {
                cir.setReturnValue(10.0f);
            }
        }
    }
}
