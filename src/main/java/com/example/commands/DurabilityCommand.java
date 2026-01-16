package com.example.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
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

/**
 * /example durability <value> - Sets the durability of the item in hand
 */
public class DurabilityCommand extends AbstractPlayerCommand {

    @Nonnull
    private final RequiredArg<Double> durabilityArg =
        this.withRequiredArg("value", "example.commands.durability.value", ArgTypes.DOUBLE);

    public DurabilityCommand() {
        super("durability", "example.commands.durability.desc");
        requirePermission("example.durability");
    }

    @Override
    protected void execute(@Nonnull CommandContext context,
                           @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref,
                           @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {

        // Get the durability argument
        double newDurability = durabilityArg.get(context);

        // Get the player and inventory
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            context.sendMessage(Message.translation("Error: Could not access player data."));
            return;
        }

        Inventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInHand();

        // Check if player has an item in hand
        if (itemInHand == null) {
            context.sendMessage(Message.translation("Error: You don't have an item in your hand!"));
            return;
        }

        // Check if the item has durability
        double maxDurability = itemInHand.getMaxDurability();
        if (maxDurability <= 0) {
            context.sendMessage(Message.translation("Error: This item doesn't have durability!"));
            return;
        }

        // Clamp durability to valid range
        if (newDurability < 0) {
            newDurability = 0;
        } else if (newDurability > maxDurability) {
            newDurability = maxDurability;
        }

        // Create new item with updated durability
        ItemStack newItem = itemInHand.withDurability(newDurability);

        // Set the item back in the hotbar
        byte activeSlot = inventory.getActiveHotbarSlot();
        inventory.getHotbar().setItemStackForSlot(activeSlot, newItem);

        // Send updated inventory to client
        player.sendInventory();

        context.sendMessage(Message.translation(
            String.format("Set durability to %.1f / %.1f for '%s'",
                newDurability, maxDurability, itemInHand.getItemId())));
    }
}
