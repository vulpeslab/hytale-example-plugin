package com.example.listeners;

import com.example.ExamplePlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;

import javax.annotation.Nonnull;

/**
 * Damage listener system that handles:
 * - Godmode (prevents all damage for players with godmode enabled)
 * - Damage meter (shows damage dealt to attackers with damage meter enabled)
 * - Damage notifications (shows damage taken to players)
 *
 * Runs in the Filter Damage Group (before damage is applied to health)
 * to allow cancelling damage before it affects the player.
 */
@SuppressWarnings({"null", "removal"})
public class PlayerDamageListener extends DamageEventSystem {

    @Override
    public SystemGroup<EntityStore> getGroup() {
        // Register in the Filter Damage Group to run BEFORE damage is applied
        return DamageModule.get().getFilterDamageGroup();
    }

    @Override
    public Query<EntityStore> getQuery() {
        // Return Query.any() to handle all entities that receive damage
        return Query.any();
    }

    @Override
    public void handle(
        int index,
        @Nonnull ArchetypeChunk<EntityStore> chunk,
        @Nonnull Store<EntityStore> store,
        @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull Damage damage
    ) {
        // Skip if already cancelled
        if (damage.isCancelled()) {
            return;
        }

        ExamplePlugin plugin = ExamplePlugin.get();

        // Get reference to the damaged entity (target)
        Ref<EntityStore> targetRef = chunk.getReferenceTo(index);
        float damageAmount = damage.getAmount();

        // Handle damage meter for attacker (source)
        handleDamageMeter(plugin, store, damage, damageAmount);

        // Handle target player (godmode and damage notifications)
        handleTargetPlayer(plugin, store, targetRef, damage, damageAmount);
    }

    /**
     * Shows damage dealt to attackers who have damage meter enabled.
     */
    private void handleDamageMeter(
        ExamplePlugin plugin,
        Store<EntityStore> store,
        Damage damage,
        float damageAmount
    ) {
        Damage.Source source = damage.getSource();

        if (source instanceof Damage.EntitySource entitySource) {
            Ref<EntityStore> attackerRef = entitySource.getRef();

            if (attackerRef != null && attackerRef.isValid()) {
                Player attackerPlayer = store.getComponent(attackerRef, Player.getComponentType());

                if (attackerPlayer != null) {
                    String attackerUsername = attackerPlayer.getPlayerRef().getUsername();

                    if (plugin.hasDamageMeter(attackerUsername)) {
                        attackerPlayer.sendMessage(Message.translation(
                            String.format("Damage dealt: %.1f", damageAmount)));
                    }
                }
            }
        }
    }

    /**
     * Handles godmode and damage notifications for the target player.
     */
    private void handleTargetPlayer(
        ExamplePlugin plugin,
        Store<EntityStore> store,
        Ref<EntityStore> targetRef,
        Damage damage,
        float damageAmount
    ) {
        // Check if target is a player
        Player targetPlayer = store.getComponent(targetRef, Player.getComponentType());
        if (targetPlayer == null) {
            return;
        }

        String username = targetPlayer.getPlayerRef().getUsername();

        // Check if player has godmode enabled
        if (plugin.hasGodmode(username)) {
            // Cancel all damage for godmode players
            damage.setCancelled(true);
            targetPlayer.sendMessage(Message.translation(
                String.format("Godmode: Blocked %.1f damage!", damageAmount)));
            return;
        }

        // For non-godmode players, show damage notification
        showDamageNotification(store, targetRef, targetPlayer, damageAmount);
    }

    /**
     * Shows a damage notification to the player with current health info.
     */
    private void showDamageNotification(
        Store<EntityStore> store,
        Ref<EntityStore> targetRef,
        Player targetPlayer,
        float damageAmount
    ) {
        EntityStatMap stats = store.getComponent(targetRef, EntityStatMap.getComponentType());
        if (stats == null) {
            return;
        }

        int healthIndex = DefaultEntityStatTypes.getHealth();
        EntityStatValue health = stats.get(healthIndex);
        if (health == null) {
            return;
        }

        float currentHealth = health.get();
        targetPlayer.sendMessage(Message.translation(
            String.format("You took %.1f damage! Health: %.1f -> %.1f",
                damageAmount, currentHealth, currentHealth - damageAmount)));
    }
}
