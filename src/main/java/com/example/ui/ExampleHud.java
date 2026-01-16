package com.example.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

/**
 * Custom HUD that shows the ExamplePlugin name in a cycling RGB color.
 */
@SuppressWarnings("null")
public class ExampleHud extends CustomUIHud {

    private static final String HUD_LAYOUT = "Hud/ExamplePlugin_Hud.ui";
    private static final String HUD_TEXT_SELECTOR = "#HudText";
    private static final String HUD_TEXT = "ExamplePlugin";

    private float hue = 0.0f;

    public ExampleHud(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commands) {
        commands.append(HUD_LAYOUT);
        apply(commands);
    }

    /**
     * Update the HUD text color.
     */
    public void tick() {
        UICommandBuilder commands = new UICommandBuilder();
        apply(commands);
        update(false, commands);
    }

    /**
     * Clear the HUD from the client.
     */
    public void clear() {
        update(true, new UICommandBuilder());
    }

    private void apply(UICommandBuilder commands) {
        commands.set(HUD_TEXT_SELECTOR + ".Text", HUD_TEXT);
        commands.set(HUD_TEXT_SELECTOR + ".Style.TextColor", toHexColor(hue));
        hue = (hue + 0.02f) % 1.0f;
    }

    private static String toHexColor(float hue) {
        float h = (hue % 1.0f + 1.0f) % 1.0f;
        float h6 = h * 6.0f;
        int sector = (int) Math.floor(h6);
        float f = h6 - sector;
        float p = 0.0f;
        float q = 1.0f - f;
        float t = f;

        float r;
        float g;
        float b;

        switch (sector) {
            case 0 -> {
                r = 1.0f;
                g = t;
                b = p;
            }
            case 1 -> {
                r = q;
                g = 1.0f;
                b = p;
            }
            case 2 -> {
                r = p;
                g = 1.0f;
                b = t;
            }
            case 3 -> {
                r = p;
                g = q;
                b = 1.0f;
            }
            case 4 -> {
                r = t;
                g = p;
                b = 1.0f;
            }
            default -> {
                r = 1.0f;
                g = p;
                b = q;
            }
        }

        int ri = Math.round(r * 255.0f);
        int gi = Math.round(g * 255.0f);
        int bi = Math.round(b * 255.0f);

        return String.format("#%02x%02x%02x", ri, gi, bi);
    }
}
