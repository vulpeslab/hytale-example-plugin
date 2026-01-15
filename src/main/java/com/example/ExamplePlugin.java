package com.example;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityUseBlockEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@SuppressWarnings({"null", "removal"})
public class ExamplePlugin extends JavaPlugin {

    private static ExamplePlugin instance;

    // Track players who have already received tools (by username)
    private final Set<String> playersReceivedTools = new HashSet<>();

    // Track players who have already received door reward (by username)
    private final Set<String> playersReceivedDoorReward = new HashSet<>();

    // Track players with godmode enabled (by username)
    private final Set<String> playersWithGodmode = new HashSet<>();

    public boolean hasGodmode(String username) {
        return playersWithGodmode.contains(username);
    }

    public boolean toggleGodmode(String username) {
        if (playersWithGodmode.contains(username)) {
            playersWithGodmode.remove(username);
            return false;
        } else {
            playersWithGodmode.add(username);
            return true;
        }
    }

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public static ExamplePlugin get() {
        return instance;
    }

    @Override
    protected void setup() {
        instance = this;

        // Register commands
        getCommandRegistry().registerCommand(new ExampleCommand());

        // Register events
        registerEvents();

        // Register damage listener system
        getEntityStoreRegistry().registerSystem(new PlayerDamageListener());

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

    private void registerEvents() {
        // Listen for living entity block use events (doors, etc.)
        getEventRegistry().registerGlobal(
            LivingEntityUseBlockEvent.class,
            this::onLivingEntityUseBlock
        );
    }

    private void onLivingEntityUseBlock(LivingEntityUseBlockEvent event) {
        // Get block type
        String blockType = event.getBlockType();
        
        // Check if it's a door OPENING (not closing)
        // Block type contains "OpenDoor" when opening, "CloseDoor" when closing
        if (blockType != null && blockType.toLowerCase().contains("door") && blockType.contains("Open")) {
            // Get the entity that used the block
            Ref<EntityStore> entityRef = event.getRef();
            if (entityRef != null && entityRef.isValid()) {
                Store<EntityStore> store = entityRef.getStore();
                Player player = store.getComponent(entityRef, Player.getComponentType());
                
                if (player != null) {
                    String username = player.getPlayerRef().getUsername();
                    
                    // Check if player already received the door reward
                    if (playersReceivedDoorReward.contains(username)) {
                        return;
                    }
                    
                    // Mark player as having received the reward
                    playersReceivedDoorReward.add(username);
                    
                    // Give the player a door item
                    Inventory inventory = player.getInventory();
                    ItemStack doorItem = new ItemStack("Furniture_Village_Door", 1);
                    inventory.getStorage().addItemStack(doorItem);
                    player.sendInventory();
                    
                    // Send message to player
                    player.sendMessage(Message.translation("You opened a door and received a door item!"));
                }
            }
        }
    }

    // Main /example command collection
    class ExampleCommand extends AbstractCommandCollection {

        ExampleCommand() {
            super("example", "example.commands.desc");

            // Add subcommands
            this.addSubCommand(new InfoCommand());
            this.addSubCommand(new ToolsCommand());
            this.addSubCommand(new GodmodeCommand());
        }

        // /example info - shows plugin information
        class InfoCommand extends CommandBase {

            InfoCommand() {
                super("info", "example.commands.info.desc");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                context.sendMessage(Message.translation(""));
                context.sendMessage(Message.translation("========== Example Plugin =========="));
                context.sendMessage(Message.translation("Version: 1.0.0"));
                context.sendMessage(Message.translation("Made by: Vulpeslab"));
                context.sendMessage(Message.translation("GitHub: https://github.com/vulpeslab/hytale-example-plugin"));
                context.sendMessage(Message.translation("====================================="));
            }
        }

        // /example tools - gives stone tools (once per player)
        class ToolsCommand extends AbstractPlayerCommand {

            ToolsCommand() {
                super("tools", "example.commands.tools.desc");
            }

            @Override
            protected void execute(@Nonnull CommandContext context,
                                   @Nonnull Store<EntityStore> store,
                                   @Nonnull Ref<EntityStore> ref,
                                   @Nonnull PlayerRef playerRef,
                                   @Nonnull World world) {

                String username = playerRef.getUsername();

                // Check if player already received tools
                if (playersReceivedTools.contains(username)) {
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
                playersReceivedTools.add(username);

                context.sendMessage(Message.translation("You received your starter crude tools!"));
                context.sendMessage(Message.translation("- Crude Pickaxe"));
                context.sendMessage(Message.translation("- Crude Hatchet"));
                context.sendMessage(Message.translation("- Crude Shovel"));
                context.sendMessage(Message.translation("- Crude Axe"));

                getLogger().at(Level.INFO).log("Gave starter tools to player: %s", username);
            }
        }

        // /example godmode - toggles godmode (requires permission)
        class GodmodeCommand extends AbstractPlayerCommand {

            GodmodeCommand() {
                super("godmode", "example.commands.godmode.desc");
            }

            @Override
            protected void execute(@Nonnull CommandContext context,
                                   @Nonnull Store<EntityStore> store,
                                   @Nonnull Ref<EntityStore> ref,
                                   @Nonnull PlayerRef playerRef,
                                   @Nonnull World world) {

                // Check permission
                if (!context.sender().hasPermission("example.godmode")) {
                    context.sendMessage(Message.translation("You don't have permission to use godmode!"));
                    return;
                }

                String username = playerRef.getUsername();
                boolean enabled = toggleGodmode(username);

                if (enabled) {
                    context.sendMessage(Message.translation("Godmode enabled! You are now invincible."));
                    getLogger().at(Level.INFO).log("Godmode enabled for player: %s", username);
                } else {
                    context.sendMessage(Message.translation("Godmode disabled! You can now take damage."));
                    getLogger().at(Level.INFO).log("Godmode disabled for player: %s", username);
                }
            }
        }
    }

    // Damage listener system that handles godmode (prevents all damage for players with godmode)
    // Runs in the Filter Damage Group (before damage is applied to health)
    class PlayerDamageListener extends DamageEventSystem {

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
        public void handle(int index, ArchetypeChunk<EntityStore> chunk,
                           Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer,
                           Damage damage) {
            // Skip if already cancelled
            if (damage.isCancelled()) {
                return;
            }

            // Get reference to the damaged entity
            Ref<EntityStore> targetRef = chunk.getReferenceTo(index);

            // Check if target is a player
            Player player = store.getComponent(targetRef, Player.getComponentType());
            if (player == null) {
                return;
            }

            String username = player.getPlayerRef().getUsername();
            float damageAmount = damage.getAmount();

            // Check if player has godmode enabled
            if (hasGodmode(username)) {
                // Cancel all damage for godmode players
                damage.setCancelled(true);
                player.sendMessage(Message.translation(
                    String.format("Godmode: Blocked %.1f damage!", damageAmount)));
                return;
            }

            // For non-godmode players, just show damage notification
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
            player.sendMessage(Message.translation(
                String.format("You took %.1f damage! Health: %.1f -> %.1f",
                    damageAmount, currentHealth, currentHealth - damageAmount)));
        }
    }

}
