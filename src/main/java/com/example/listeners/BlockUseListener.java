package com.example.listeners;

import com.example.ExamplePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityUseBlockEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

/**
 * Listener for block use events.
 *
 * Currently handles:
 * - Door opening: Gives player a door item reward (once per player)
 */
@SuppressWarnings("removal")
public class BlockUseListener {

    private final ExamplePlugin plugin;

    public BlockUseListener(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register all block use event handlers.
     */
    public void register() {
        plugin.getEventRegistry().registerGlobal(
            LivingEntityUseBlockEvent.class,
            this::onLivingEntityUseBlock
        );
    }

    /**
     * Handles living entity block use events (doors, etc.)
     */
    private void onLivingEntityUseBlock(LivingEntityUseBlockEvent event) {
        String blockType = event.getBlockType();

        // Check if it's a door OPENING (not closing)
        // Block type contains "OpenDoor" when opening, "CloseDoor" when closing
        if (blockType == null || !blockType.toLowerCase().contains("door") || !blockType.contains("Open")) {
            return;
        }

        // Get the entity that used the block
        Ref<EntityStore> entityRef = event.getRef();
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }

        Store<EntityStore> store = entityRef.getStore();
        Player player = store.getComponent(entityRef, Player.getComponentType());

        if (player == null) {
            return;
        }

        String username = player.getPlayerRef().getUsername();

        // Check if player already received the door reward
        if (plugin.hasReceivedDoorReward(username)) {
            return;
        }

        // Mark player as having received the reward
        plugin.markDoorRewardReceived(username);

        // Give the player a door item
        Inventory inventory = player.getInventory();
        ItemStack doorItem = new ItemStack("Furniture_Village_Door", 1);
        inventory.getStorage().addItemStack(doorItem);
        player.sendInventory();

        // Send message to player
        player.sendMessage(Message.translation("You opened a door and received a door item!"));
    }
}
