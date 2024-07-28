package com.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.entity.boss.dragon.EnderDragonFight.class)
public class EnderDragonFightMixin {

/*    @Inject(method = "getAliveEndCrystals",at = @At("HEAD"), cancellable = true)
    protected void InjecttickGetAliveEndCrystals(CallbackInfoReturnable<Integer> cir){
        //head.playSound(SoundEvents.BLOCK_ANVIL_PLACE,5,1);
            cir.setReturnValue(0);
    }*/
}
