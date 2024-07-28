package com.example.mixin;

import com.example.special.event.Overworld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(net.minecraft.entity.boss.dragon.EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin{
    @Shadow
    EnderDragonPart head;
    @Shadow
    EnderDragonPart body;
    @Unique
    private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(Text.of("末影龙"), BossBar.Color.PURPLE, BossBar.Style.PROGRESS).setDarkenSky(true);

    @Inject(method = "tickMovement",at = @At("HEAD"))
    protected void InjecttickMovement(CallbackInfo ci){
        //head.playSound(SoundEvents.BLOCK_ANVIL_PLACE,5,1);
        if(head.getWorld().getRegistryKey() != World.END){
            head.owner.setHealth(200);
        }
    }

    @Inject(method = "canUsePortals",at = @At("HEAD"), cancellable = true)
    protected void InjectCanUsePortals(CallbackInfoReturnable<Boolean> cir){
        //head.playSound(SoundEvents.BLOCK_ANVIL_PLACE,5,1);
        cir.setReturnValue(true);
    }

    @ModifyArg(method = "damagePart",at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/phase/Phase;modifyDamageTaken(Lnet/minecraft/entity/damage/DamageSource;F)F"),index = 1)
    protected float InjectDamagePart(float amount){
        if(amount<0.5f)
            return 1f;
        return amount;
    }

    @Inject(method = "damageLivingEntities",at=@At("HEAD"), cancellable = true)
    protected void InjectDamageLivingEntities(List<Entity> entities, CallbackInfo ci){
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity) || (entity == Overworld.Target_Player && ((ServerPlayerEntity) entity).getHealth() <= Overworld.takeHealth)) continue;
            if(head.getWorld().getRegistryKey() == World.END)
                entity.damage(head.owner.getDamageSources().mobAttack(head.owner), 10.0f);
            else
                entity.damage(head.owner.getDamageSources().mobAttack(head.owner), Overworld.chargeDamage);
            head.owner.applyDamageEffects(head.owner, entity);
        }
        ci.cancel();
    }

    @Inject(method = "launchLivingEntities",at=@At("HEAD"), cancellable = true)
    protected void InjectLaunchLivingEntities(List<Entity> entities, CallbackInfo ci){
        double d = (body.getBoundingBox().minX + body.getBoundingBox().maxX) / 2.0;
        double e = (body.getBoundingBox().minZ + body.getBoundingBox().maxZ) / 2.0;
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity)) continue;
            double f = entity.getX() - d;
            double g = entity.getZ() - e;
            double h = Math.max(f * f + g * g, 0.1);
            if (Overworld.Target_Player == null || (entity == Overworld.Target_Player && ((ServerPlayerEntity) entity).getHealth() <= Overworld.takeHealth && !Overworld.pro2)) continue;
            entity.addVelocity(f / h * 4.0, 0.2f, g / h * 4.0);
            if (head.owner.getPhaseManager().getCurrent().isSittingOrHovering() || (entity == Overworld.Target_Player && ((ServerPlayerEntity) entity).getHealth() <= Overworld.takeHealth)) continue;
            if(!Overworld.backEnd)
                entity.damage(head.owner.getDamageSources().mobAttack(head.owner), Overworld.chargeDamage);
            else
                entity.damage(head.owner.getDamageSources().mobAttack(head.owner), 5.0f);
            head.owner.applyDamageEffects(head.owner, entity);
        }
        ci.cancel();
    }
}
