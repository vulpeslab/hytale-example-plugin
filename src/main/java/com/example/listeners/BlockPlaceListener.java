package com.example.listeners;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;

import javax.annotation.Nonnull;

/**
 * Listener for block placement events.
 * Sends the player a message when they place a block, including block type and coordinates.
 */
@SuppressWarnings("null")
public class BlockPlaceListener extends EntityEventSystem<EntityStore, PlaceBlockEvent> {

    public BlockPlaceListener() {
        super(PlaceBlockEvent.class);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @Override
    public void handle(
        int index,
        @Nonnull ArchetypeChunk<EntityStore> chunk,
        @Nonnull Store<EntityStore> store,
        @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull PlaceBlockEvent event
    ) {

        // Get the entity that placed the block
        Ref<EntityStore> entityRef = chunk.getReferenceTo(index);
        if (entityRef == null || !entityRef.isValid()) {
            return;
        }

        // Check if it's a player
        Player player = store.getComponent(entityRef, Player.getComponentType());
        if (player == null) {
            return;
        }

        // Get block placement details
        Vector3i position = event.getTargetBlock();
        ItemStack itemInHand = event.getItemInHand();

        // Get block type name from the item
        String blockName = "Unknown";
        if (itemInHand != null) {
            blockName = itemInHand.getItemId();
        }

        // Send message to the player
        player.sendMessage(Message.translation(
            String.format("You placed '%s' at (%d, %d, %d)",
                blockName,
                position.x,
                position.y,
                position.z)));
    }
}
