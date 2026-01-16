package com.example.commands;

import com.example.ui.CustomExampleUIPage;
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
 * /example customui - Opens a custom UI page with inline layout
 */
public class CustomUICommand extends AbstractPlayerCommand {
    public CustomUICommand() {
        super("customui", "example.commands.customui.desc");
        requirePermission("example.customui");
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

        // Open the custom UI page
        CustomExampleUIPage page = new CustomExampleUIPage(playerRef);
        player.getPageManager().openCustomPage(ref, store, page);
    }
}
