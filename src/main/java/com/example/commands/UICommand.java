package com.example.commands;

import com.example.ui.ExampleUIPage;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;

/**
 * /example ui - Opens the Example Plugin UI panel
 */
public class UICommand extends AbstractPlayerCommand {

    public UICommand() {
        super("ui", "example.commands.ui.desc");
        requirePermission("example.ui");
    }

    @Override
    protected void execute(@Nonnull CommandContext context,
                           @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref,
                           @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        // Open the Example UI page
        ExampleUIPage page = new ExampleUIPage(playerRef);
        player.getPageManager().openCustomPage(ref, store, page);
    }
}
