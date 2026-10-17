package com.thedeathlycow.immersive.storms.mixin;

import com.thedeathlycow.immersive.storms.util.AttributeMapHolder;
import com.thedeathlycow.immersive.storms.util.ISLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.class)
public abstract class LevelMixin implements ISLevel {
    @Override
    public AttributeMapHolder immersive_storms$getAttributeMapHolder() {
        return AttributeMapHolder.EMPTY;
    }

    @Mixin(ServerLevel.class)
    public abstract static class ServerLevelMixin extends LevelMixin {
        @Shadow
        public abstract MinecraftServer getServer();

        @Override
        public AttributeMapHolder immersive_storms$getAttributeMapHolder() {
            return (AttributeMapHolder) this.getServer();
        }
    }

    @Mixin(ClientLevel.class)
    public abstract static class ClientLevelMixin extends LevelMixin {
        @Shadow
        @Final
        private ClientPacketListener connection;

        @Override
        public AttributeMapHolder immersive_storms$getAttributeMapHolder() {
            return (AttributeMapHolder) this.connection;
        }
    }
}