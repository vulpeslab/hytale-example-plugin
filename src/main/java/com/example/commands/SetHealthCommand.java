package com.example.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;

/**
 * /example sethealth <value> - Sets the player's health
 */
public class SetHealthCommand extends AbstractPlayerCommand {

    @Nonnull
    private final RequiredArg<Double> healthArg =
        this.withRequiredArg("value", "example.commands.sethealth.value", ArgTypes.DOUBLE);

    public SetHealthCommand() {
        super("sethealth", "example.commands.sethealth.desc");
        requirePermission("example.sethealth");
    }

    @Override
    protected void execute(@Nonnull CommandContext context,
                           @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref,
                           @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {

        double newHealth = healthArg.get(context);

        EntityStatMap statMap = store.getComponent(ref,
            EntityStatsModule.get().getEntityStatMapComponentType());

        if (statMap == null) {
            context.sendMessage(Message.raw("Error: Could not access player stats."));
            return;
        }

        int healthIndex = DefaultEntityStatTypes.getHealth();
        if (statMap.get(healthIndex) == null) {
            context.sendMessage(Message.raw("Error: Health stat not found."));
            return;
        }

        float resultHealth = statMap.setStatValue(healthIndex, (float) newHealth);
        context.sendMessage(Message.raw(String.format("Health set to %.1f", resultHealth)));
    }
}
