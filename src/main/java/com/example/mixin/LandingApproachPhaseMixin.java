package com.example.mixin;

import com.example.special.event.Overworld;
import net.minecraft.entity.boss.dragon.phase.LandingApproachPhase;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LandingApproachPhase.class)
public class LandingApproachPhaseMixin {

    @Redirect(method = "updatePath",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTopPosition(Lnet/minecraft/world/Heightmap$Type;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos InjectUpdatePath(World instance, Heightmap.Type type, BlockPos blockPos) {
        if (Overworld.Target_Player != null)
            return Overworld.Target_Player.getBlockPos();
        else {
            for (ServerPlayerEntity player : Overworld.EndPlayer) {
                return player.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.offsetOrigin(new BlockPos(0, 0, 0)));
            }
        }
        return new BlockPos(0,120,0);
    }
}
