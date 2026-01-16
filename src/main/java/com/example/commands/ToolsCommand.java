package com.example.commands;

import com.example.ExamplePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * /example tools - Gives starter crude tools (once per player)
 */
public class ToolsCommand extends AbstractPlayerCommand {

    public ToolsCommand() {
        super("tools", "example.commands.tools.desc");
        requirePermission("example.tools");
    }

    @Override
    protected void execute(@Nonnull CommandContext context,
                           @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref,
                           @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {

        ExamplePlugin plugin = ExamplePlugin.get();
        String username = playerRef.getUsername();

        // Check if player already received tools
        if (plugin.hasReceivedTools(username)) {
            context.sendMessage(Message.translation("You have already received your starter tools!"));
            return;
        }

        // Get the Player entity and inventory
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            context.sendMessage(Message.translation("Error: Could not access player data."));
            return;
        }

        Inventory inventory = player.getInventory();

        // Create crude starter tools (valid Hytale items)
        List<ItemStack> tools = Arrays.asList(
            new ItemStack("Tool_Pickaxe_Crude", 1),
            new ItemStack("Tool_Hatchet_Crude", 1),
            new ItemStack("Tool_Shovel_Crude", 1),
            new ItemStack("Weapon_Axe_Crude", 1)
        );

        // Add tools to inventory
        inventory.getStorage().addItemStacks(tools);

        // Send updated inventory to client
        player.sendInventory();

        // Mark player as having received tools
        plugin.markToolsReceived(username);

        context.sendMessage(Message.translation("You received your starter crude tools!"));
        context.sendMessage(Message.translation("- Crude Pickaxe"));
        context.sendMessage(Message.translation("- Crude Hatchet"));
        context.sendMessage(Message.translation("- Crude Shovel"));
        context.sendMessage(Message.translation("- Crude Axe"));

        plugin.getLogger().at(Level.INFO).log("Gave starter tools to player: %s", username);
    }
}
