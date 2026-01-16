package com.example;

import com.example.commands.ExampleCommand;
import com.example.ui.ExampleHud;
import com.example.listeners.BlockPlaceListener;
import com.example.listeners.BlockUseListener;
import com.example.listeners.PlayerDamageListener;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Example plugin demonstrating Hytale plugin development patterns.
 *
 * Features:
 * - Commands: /example info, tools, godmode, damage-meter
 * - Damage system integration (godmode, damage meter)
 * - Block use events (door opening reward)
 */
@SuppressWarnings("null")
public class ExamplePlugin extends JavaPlugin {

    private static ExamplePlugin instance;

    // Player state tracking
    private final Set<String> playersReceivedTools = new HashSet<>();
    private final Set<String> playersReceivedDoorReward = new HashSet<>();
    private final Set<String> playersWithGodmode = new HashSet<>();
    private final Set<String> playersWithDamageMeter = new HashSet<>();
    private final Map<String, ExampleHud> activeHuds = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> hudTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService hudScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "ExamplePlugin-HudUpdater");
        thread.setDaemon(true);
        return thread;
    });

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    /**
     * Get the plugin instance.
     */
    public static ExamplePlugin get() {
        return instance;
    }

    @Override
    protected void setup() {
        instance = this;

        // Register commands
        getCommandRegistry().registerCommand(new ExampleCommand());

        // Register event listeners
        new BlockUseListener(this).register();

        // Register ECS systems
        getEntityStoreRegistry().registerSystem(new PlayerDamageListener());
        getEntityStoreRegistry().registerSystem(new BlockPlaceListener());

        getLogger().at(Level.INFO).log("ExamplePlugin setup complete!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("ExamplePlugin started!");
    }

    @Override
    protected void shutdown() {
        hudTasks.values().forEach(task -> task.cancel(false));
        hudTasks.clear();
        activeHuds.values().forEach(ExampleHud::clear);
        activeHuds.clear();
        hudScheduler.shutdownNow();
        getLogger().at(Level.INFO).log("ExamplePlugin shutting down!");
    }

    // ==================== Tools State ====================

    /**
     * Check if player has already received starter tools.
     */
    public boolean hasReceivedTools(String username) {
        return playersReceivedTools.contains(username);
    }

    /**
     * Mark player as having received starter tools.
     */
    public void markToolsReceived(String username) {
        playersReceivedTools.add(username);
    }

    // ==================== Door Reward State ====================

    /**
     * Check if player has already received the door reward.
     */
    public boolean hasReceivedDoorReward(String username) {
        return playersReceivedDoorReward.contains(username);
    }

    /**
     * Mark player as having received the door reward.
     */
    public void markDoorRewardReceived(String username) {
        playersReceivedDoorReward.add(username);
    }

    // ==================== Godmode State ====================

    /**
     * Check if player has godmode enabled.
     */
    public boolean hasGodmode(String username) {
        return playersWithGodmode.contains(username);
    }

    /**
     * Toggle godmode for a player.
     * @return true if godmode is now enabled, false if disabled
     */
    public boolean toggleGodmode(String username) {
        if (playersWithGodmode.contains(username)) {
            playersWithGodmode.remove(username);
            return false;
        }

        playersWithGodmode.add(username);
        return true;
    }

    // ==================== Damage Meter State ====================

    /**
     * Check if player has damage meter enabled.
     */
    public boolean hasDamageMeter(String username) {
        return playersWithDamageMeter.contains(username);
    }

    /**
     * Toggle damage meter for a player.
     * @return true if damage meter is now enabled, false if disabled
     */
    public boolean toggleDamageMeter(String username) {
        if (playersWithDamageMeter.contains(username)) {
            playersWithDamageMeter.remove(username);
            return false;
        }

        playersWithDamageMeter.add(username);
        return true;
    }

    // ==================== HUD State ====================

    /**
     * Check if player has the Example HUD enabled.
     */
    public boolean hasHud(String username) {
        return activeHuds.containsKey(username);
    }

    /**
     * Toggle the Example HUD for a player.
     * @return true if HUD is now enabled, false if disabled
     */
    public boolean toggleHud(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
        String username = playerRef.getUsername();

        if (activeHuds.containsKey(username)) {
            disableHud(username, player, playerRef);
            return false;
        }

        ExampleHud hud = new ExampleHud(playerRef);
        player.getHudManager().setCustomHud(playerRef, hud);

        ScheduledFuture<?> task = hudScheduler.scheduleAtFixedRate(() -> {
            try {
                hud.tick();
            } catch (Exception e) {
                getLogger().at(Level.WARNING).log("Failed to update Example HUD for %s", username);
            }
        }, 0L, 100L, TimeUnit.MILLISECONDS);

        activeHuds.put(username, hud);
        hudTasks.put(username, task);
        return true;
    }

    private void disableHud(String username, Player player, PlayerRef playerRef) {
        ScheduledFuture<?> task = hudTasks.remove(username);
        if (task != null) {
            task.cancel(false);
        }

        activeHuds.remove(username);
        player.getHudManager().setCustomHud(playerRef, null);
    }
}
