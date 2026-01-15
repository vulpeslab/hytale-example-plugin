package com.example;

import com.example.commands.ExampleCommand;
import com.example.listeners.BlockPlaceListener;
import com.example.listeners.BlockUseListener;
import com.example.listeners.PlayerDamageListener;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Example plugin demonstrating Hytale plugin development patterns.
 *
 * Features:
 * - Commands: /example info, tools, godmode, damage-meter
 * - Damage system integration (godmode, damage meter)
 * - Block use events (door opening reward)
 */
@SuppressWarnings({"null", "removal"})
public class ExamplePlugin extends JavaPlugin {

    private static ExamplePlugin instance;

    // Player state tracking
    private final Set<String> playersReceivedTools = new HashSet<>();
    private final Set<String> playersReceivedDoorReward = new HashSet<>();
    private final Set<String> playersWithGodmode = new HashSet<>();
    private final Set<String> playersWithDamageMeter = new HashSet<>();

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
        } else {
            playersWithGodmode.add(username);
            return true;
        }
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
        } else {
            playersWithDamageMeter.add(username);
            return true;
        }
    }
}
