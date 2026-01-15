package com.example.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;

/**
 * /example info - Shows plugin information
 */
public class InfoCommand extends CommandBase {

    public InfoCommand() {
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
