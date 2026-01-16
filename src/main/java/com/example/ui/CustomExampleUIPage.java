package com.example.ui;

import com.example.ExamplePlugin;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Custom UI page for the Example Plugin using a custom .ui file from the asset pack.
 * Demonstrates creating a custom layout with an image.
 */
public class CustomExampleUIPage extends InteractiveCustomUIPage<CustomExampleUIPage.UIEventData> {

    // Custom .ui file from the plugin's asset pack
    private static final String PAGE_LAYOUT = "Pages/ExamplePlugin_StatusPage.ui";

    public CustomExampleUIPage(@Nonnull PlayerRef playerRef) {
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

        // Get current status
        boolean hasGodmode = plugin.hasGodmode(username);
        boolean hasDamageMeter = plugin.hasDamageMeter(username);
        boolean hasReceivedTools = plugin.hasReceivedTools(username);

        // Load the custom .ui page
        commands.append(PAGE_LAYOUT);

        // Set dynamic text content
        commands.set("#WelcomeText.Text", "Welcome, " + username + "!");

        // Build status text
        String statusText = String.format(
            "Godmode: %s\nDamage Meter: %s\nStarter Tools: %s",
            hasGodmode ? "Enabled" : "Disabled",
            hasDamageMeter ? "Enabled" : "Disabled",
            hasReceivedTools ? "Received" : "Not received"
        );
        commands.set("#StatusText.Text", statusText);
    }

    @Override
    public void handleDataEvent(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull Store<EntityStore> store,
        @Nonnull UIEventData data
    ) {
        // Page uses BackButton which dismisses automatically
    }

    /**
     * Event data received from UI button clicks.
     */
    public static class UIEventData {
        public static final BuilderCodec<UIEventData> CODEC = BuilderCodec
            .builder(UIEventData.class, UIEventData::new)
            .build();
    }
}
