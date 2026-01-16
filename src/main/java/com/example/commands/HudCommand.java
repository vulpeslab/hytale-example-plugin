package com.example.commands;

import com.example.ExamplePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * /example hud - Toggles the ExamplePlugin RGB HUD
 */
@SuppressWarnings("null")
public class HudCommand extends AbstractPlayerCommand {

    public HudCommand() {
        super("hud", "example.commands.hud.desc");
        requirePermission("example.hud");
    }

    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        ExamplePlugin plugin = ExamplePlugin.get();
        boolean enabled = plugin.toggleHud(player, playerRef);

        context.sendMessage(Message.raw("Example HUD " + (enabled ? "enabled" : "disabled") + "."));
        plugin.getLogger().at(Level.INFO).log(
            "Example HUD %s for player: %s",
            enabled ? "enabled" : "disabled",
            playerRef.getUsername()
        );
    }
}
