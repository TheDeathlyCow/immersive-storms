package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.thedeathlycow.immersive.storms.block.RandomBlockDisplayTickCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @WrapOperation(
            method = "randomBlockDisplayTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;randomDisplayTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V"
            )
    )
    private void hookDisplayTickEvent(Block instance, BlockState state, World world, BlockPos pos, Random random, Operation<Void> original) {
        RandomBlockDisplayTickCallback.EVENT.invoker().randomDisplayTick(
                (ClientWorld) (Object) this,
                state,
                pos,
                random
        );
    }
}