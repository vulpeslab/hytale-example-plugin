package com.example.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/**
 * Main /example command collection that groups all subcommands.
 * Only shows commands the player has permission to use.
 */
public class ExampleCommand extends AbstractCommandCollection {

    public ExampleCommand() {
        super("example", "example.commands.desc");

        addSubCommand(new InfoCommand());
        addSubCommand(new ToolsCommand());
        addSubCommand(new GodmodeCommand());
        addSubCommand(new DamageMeterCommand());
        addSubCommand(new HudCommand());
        addSubCommand(new DurabilityCommand());
        addSubCommand(new UICommand());
        addSubCommand(new CustomUICommand());
        addSubCommand(new NotificationCommand());
        addSubCommand(new SetHealthCommand());
        addSubCommand(new SetEnergyCommand());
    }
}
