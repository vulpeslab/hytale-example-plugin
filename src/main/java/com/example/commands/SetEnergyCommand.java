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
 * /example setenergy <value> - Sets the player's energy (stamina)
 */
public class SetEnergyCommand extends AbstractPlayerCommand {

    @Nonnull
    private final RequiredArg<Double> energyArg =
        this.withRequiredArg("value", "example.commands.setenergy.value", ArgTypes.DOUBLE);

    public SetEnergyCommand() {
        super("setenergy", "example.commands.setenergy.desc");
        requirePermission("example.setenergy");
    }

    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {

        double newEnergy = energyArg.get(context);

        EntityStatMap statMap = store.getComponent(ref,
            EntityStatsModule.get().getEntityStatMapComponentType());

        if (statMap == null) {
            context.sendMessage(Message.raw("Error: Could not access player stats."));
            return;
        }

        int staminaIndex = DefaultEntityStatTypes.getStamina();
        if (statMap.get(staminaIndex) == null) {
            context.sendMessage(Message.raw("Error: Energy stat not found."));
            return;
        }

        float resultEnergy = statMap.setStatValue(staminaIndex, (float) newEnergy);
        context.sendMessage(Message.raw(String.format("Energy set to %.1f", resultEnergy)));
    }
}
