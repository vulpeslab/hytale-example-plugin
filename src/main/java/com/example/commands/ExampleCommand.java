package com.example.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/**
 * Main /example command collection that groups all subcommands.
 *
 * Available subcommands:
 * - /example info - Shows plugin information
 * - /example tools - Gives starter tools (once per player)
 * - /example godmode - Toggles invincibility (requires permission)
 * - /example damage-meter - Toggles damage display (requires permission)
 * - /example ui - Opens the plugin UI panel
 * - /example customui - Opens a custom UI page with inline layout
 */
public class ExampleCommand extends AbstractCommandCollection {

    public ExampleCommand() {
        super("example", "example.commands.desc");

        // Register all subcommands
        addSubCommand(new InfoCommand());
        addSubCommand(new ToolsCommand());
        addSubCommand(new GodmodeCommand());
        addSubCommand(new DamageMeterCommand());
        addSubCommand(new DurabilityCommand());
        addSubCommand(new UICommand());
        addSubCommand(new CustomUICommand());
    }
}
