package com.example.ui;

import com.example.ExamplePlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Interactive UI page for the Example Plugin.
 * Uses the built-in PluginListPage layout and adapts it for plugin commands.
 */
@SuppressWarnings("null")
public class ExampleUIPage extends InteractiveCustomUIPage<ExampleUIPage.UIEventData> {

    private static final String PAGE_LAYOUT = "Pages/PluginListPage.ui";
    private static final String BUTTON_TEMPLATE = "Pages/PluginListButton.ui";
    private static final String LOGO_UI = "Pages/ExamplePlugin_Logo.ui";

    public ExampleUIPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, UIEventData.CODEC);
    }

    @Override
    public void build(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull UICommandBuilder commands,
        @Nonnull UIEventBuilder events,
        @Nonnull Store<EntityStore> store
    ) {

        ExamplePlugin plugin = ExamplePlugin.get();
        String username = playerRef.getUsername();

        // Load the base page layout
        commands.append(PAGE_LAYOUT);

        // Insert the plugin logo
        commands.insertBefore("#PluginName", LOGO_UI);

        // Set the header info
        commands.set("#PluginName.Text", "Example Plugin");
        commands.set("#PluginIdentifier.Text", "com.example:ExamplePlugin");
        commands.set("#PluginVersion.Text", "1.0.0");

        // Build status description
        boolean hasGodmode = plugin.hasGodmode(username);
        boolean hasDamageMeter = plugin.hasDamageMeter(username);
        boolean hasReceivedTools = plugin.hasReceivedTools(username);

        String statusText = buildStatusText(hasGodmode, hasDamageMeter, hasReceivedTools);
        commands.set("#PluginDescription.Text", statusText);

        // Hide the descriptive only checkbox option
        commands.set("#DescriptiveOnlyOption.Visible", false);

        // Clear the plugin list and add our command buttons
        commands.clear("#PluginList");

        // Add command buttons
        addCommandButton(commands, events, 0, "Show Plugin Info", "info");
        addCommandButton(commands, events, 1, "Get Starter Tools", "tools");
        addCommandButton(commands, events, 2, "Toggle Godmode [" + (hasGodmode ? "ON" : "OFF") + "]", "godmode");
        addCommandButton(commands, events, 3, "Toggle Damage Meter [" + (hasDamageMeter ? "ON" : "OFF") + "]", "damage-meter");
    }

    private void addCommandButton(
        UICommandBuilder commands,
        UIEventBuilder events,
        int index,
        String label,
        String action
    ) {
        String selector = "#PluginList[" + index + "]";
        commands.append("#PluginList", BUTTON_TEMPLATE);
        commands.set(selector + " #Button.Text", label);
        commands.set(selector + " #CheckBox.Visible", false);
        events.addEventBinding(
            CustomUIEventBindingType.Activating,
            selector + " #Button",
            EventData.of("Action", action),
            false
        );
    }

    private String buildStatusText(boolean hasGodmode, boolean hasDamageMeter, boolean hasReceivedTools) {
        StringBuilder sb = new StringBuilder();
        sb.append("Current Status:\n");
        sb.append("- Godmode: ").append(hasGodmode ? "Enabled" : "Disabled").append("\n");
        sb.append("- Damage Meter: ").append(hasDamageMeter ? "Enabled" : "Disabled").append("\n");
        sb.append("- Starter Tools: ").append(hasReceivedTools ? "Received" : "Not received");
        return sb.toString();
    }

    @Override
    public void handleDataEvent(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull Store<EntityStore> store,
        @Nonnull UIEventData data
    ) {

        if (data.action == null) {
            return;
        }

        ExamplePlugin plugin = ExamplePlugin.get();
        String username = playerRef.getUsername();

        switch (data.action) {
            case "info" -> handleInfo();
            case "tools" -> handleTools(ref, store, plugin, username);
            case "godmode" -> handleGodmode(plugin, username);
            case "damage-meter" -> handleDamageMeter(plugin, username);
        }
    }

    private void handleInfo() {
        NotificationUtil.sendNotification(
            playerRef.getPacketHandler(),
            Message.raw("Example Plugin v1.0.0"),
            Message.raw("Made by Vulpeslab"),
            null,
            null,
            NotificationStyle.Default
        );
    }

    private void handleTools(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull Store<EntityStore> store,
        ExamplePlugin plugin,
        String username
    ) {

        if (plugin.hasReceivedTools(username)) {
            NotificationUtil.sendNotification(
                playerRef.getPacketHandler(),
                Message.raw("Already Received"),
                Message.raw("You already have your starter tools!"),
                null,
                null,
                NotificationStyle.Warning
            );
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        Inventory inventory = player.getInventory();

        List<ItemStack> tools = Arrays.asList(
            new ItemStack("Tool_Pickaxe_Crude", 1),
            new ItemStack("Tool_Hatchet_Crude", 1),
            new ItemStack("Tool_Shovel_Crude", 1),
            new ItemStack("Weapon_Axe_Crude", 1)
        );

        inventory.getStorage().addItemStacks(tools);
        player.sendInventory();
        plugin.markToolsReceived(username);

        NotificationUtil.sendNotification(
            playerRef.getPacketHandler(),
            Message.raw("Tools Received!"),
            Message.raw("Check your inventory"),
            null,
            null,
            NotificationStyle.Success
        );

        plugin.getLogger().at(Level.INFO).log("Gave starter tools via UI to player: %s", username);

        // Rebuild UI to update the tools button
        rebuild();
    }

    private void handleGodmode(ExamplePlugin plugin, String username) {
        boolean enabled = plugin.toggleGodmode(username);

        NotificationUtil.sendNotification(
            playerRef.getPacketHandler(),
            Message.raw("Godmode " + (enabled ? "Enabled" : "Disabled")),
            Message.raw(enabled ? "You are now invincible!" : "You can now take damage"),
            null,
            null,
            enabled ? NotificationStyle.Success : NotificationStyle.Default
        );

        plugin.getLogger().at(Level.INFO).log("Godmode %s for player via UI: %s",
            enabled ? "enabled" : "disabled", username);

        // Rebuild UI to update button state
        rebuild();
    }

    private void handleDamageMeter(ExamplePlugin plugin, String username) {
        boolean enabled = plugin.toggleDamageMeter(username);

        NotificationUtil.sendNotification(
            playerRef.getPacketHandler(),
            Message.raw("Damage Meter " + (enabled ? "Enabled" : "Disabled")),
            Message.raw(enabled ? "Damage will be shown" : "Damage display hidden"),
            null,
            null,
            enabled ? NotificationStyle.Success : NotificationStyle.Default
        );

        plugin.getLogger().at(Level.INFO).log("Damage meter %s for player via UI: %s",
            enabled ? "enabled" : "disabled", username);

        // Rebuild UI to update button state
        rebuild();
    }

    /**
     * Event data received from UI button clicks.
     */
    public static class UIEventData {
        public static final BuilderCodec<UIEventData> CODEC = BuilderCodec
            .builder(UIEventData.class, UIEventData::new)
            .append(new KeyedCodec<>("Action", Codec.STRING),
                (data, value) -> data.action = value,
                data -> data.action)
            .add()
            .build();

        public String action;
    }
}
