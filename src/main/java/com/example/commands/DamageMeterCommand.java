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
 * /example damage-meter - Toggles damage meter display (requires example.damage-meter permission)
 */
public class DamageMeterCommand extends AbstractPlayerCommand {

    private static final String PERMISSION = "example.damage-meter";

    public DamageMeterCommand() {
        super("damage-meter", "example.commands.damage-meter.desc");
    }

    @Override
    protected void execute(@Nonnull CommandContext context,
                           @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref,
                           @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {

        // Check permission
        if (!context.sender().hasPermission(PERMISSION)) {
            context.sendMessage(Message.translation("You don't have permission to use damage meter!"));
            return;
        }

        ExamplePlugin plugin = ExamplePlugin.get();
        String username = playerRef.getUsername();
        boolean enabled = plugin.toggleDamageMeter(username);

        if (enabled) {
            context.sendMessage(Message.translation("Damage meter enabled! You will see damage dealt to enemies."));
            plugin.getLogger().at(Level.INFO).log("Damage meter enabled for player: %s", username);
        } else {
            context.sendMessage(Message.translation("Damage meter disabled."));
            plugin.getLogger().at(Level.INFO).log("Damage meter disabled for player: %s", username);
        }
    }
}
