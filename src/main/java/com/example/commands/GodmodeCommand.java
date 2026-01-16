package com.example.commands;

import com.example.ExamplePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * /example godmode - Toggles godmode (invincibility)
 */
@SuppressWarnings("null")
public class GodmodeCommand extends AbstractPlayerCommand {

    public GodmodeCommand() {
        super("godmode", "example.commands.godmode.desc");
        requirePermission("example.godmode");
    }

    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {

        ExamplePlugin plugin = ExamplePlugin.get();
        String username = playerRef.getUsername();
        boolean enabled = plugin.toggleGodmode(username);

        if (!enabled) {
            context.sendMessage(Message.raw("Godmode disabled! You can now take damage."));
            plugin.getLogger().at(Level.INFO).log("Godmode disabled for player: %s", username);
            return;
        }

        context.sendMessage(Message.raw("Godmode enabled! You are now invincible."));
        plugin.getLogger().at(Level.INFO).log("Godmode enabled for player: %s", username);
    }
}
