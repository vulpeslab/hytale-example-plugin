package com.example.commands;

import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;

/**
 * /example notification - Shows a notification to the player
 */
public class NotificationCommand extends AbstractPlayerCommand {

    public NotificationCommand() {
        super("notification", "example.commands.notification.desc");
        requirePermission("example.notification");
    }

    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {

        // Send a notification to the player
        NotificationUtil.sendNotification(
            playerRef.getPacketHandler(),
            Message.raw("Hello from Example Plugin!"),
            Message.raw("This is a notification"),
            NotificationStyle.Success
        );

        context.sendMessage(Message.raw("Notification sent!"));
    }
}
