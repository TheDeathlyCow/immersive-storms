package com.thedeathlycow.immersive.storms;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.thedeathlycow.immersive.storms.client.config.section.AdvancedSettings;
import com.thedeathlycow.immersive.storms.client.util.LevelExtension;
import com.thedeathlycow.immersive.storms.registry.ISAttributeTypes;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import com.thedeathlycow.immersive.storms.util.ISCommandRegistrationCallback;
import dev.yumi.commons.event.EventManager;
import dev.yumi.mc.core.api.ModContainer;
import dev.yumi.mc.core.api.YumiMods;
import dev.yumi.mc.core.api.entrypoint.ModInitializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static net.minecraft.commands.Commands.literal;

public class ImmersiveStorms implements ModInitializer {
    public static final String MOD_NAMESPACE = "immersive-storms";
    public static final String MOD_ID = "immersive_storms";
    public static final EventManager<Identifier> EVENT_MANAGER = new EventManager<>(id("default"), Identifier::parse);
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

    @Override
    public void onInitialize(ModContainer mod) {
        ISParticleTypes.initialize();
        ISSoundEvents.initialize();
        ISAttributeTypes.initialize();
        ISEnvironmentAttributes.initialize();

        ISCommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            var reloadConfig = literal("reload")
                    .requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_OWNER))
                    .executes(ImmersiveStorms::reloadConfig);

            dispatcher.register(literal(MOD_ID).then(reloadConfig));
        });
    }

    public static int reloadConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        if (AdvancedSettings.HANDLER.load()) {
            ((LevelExtension)level).immersiveStorms$rebindAttributeMap(AdvancedSettings.HANDLER.instance());
            source.sendSuccess(() -> Component.literal("Reloaded Immersive Storms config"), true);
        } else {
            source.sendFailure(Component.literal("Failed to reload Immersive Storms config"));
        }

        return Command.SINGLE_SUCCESS;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_NAMESPACE, path);
    }

    public static Path getConfigDir() {
        return YumiMods.get().getConfigDirectory().resolve(MOD_NAMESPACE);
    }
}