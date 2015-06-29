package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that teleports a player to a coordinate location
 */
public class TeleportStatement extends Statement {

    public TeleportStatement() {
        super(Syntax.builder()
                .identifiers("tp")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Location")))
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("Location")))
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("World"), ",", Argument.from("Location")))
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("World"), ",", Argument.from("Location"), ",", Argument.from("Safely")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> player = ctx.getLiteral("Player").getPlayer();
        if (!player.isPresent()) {
            return Result.failure();
        }

        Optional<World> world = ctx.getLiteral("World").getWorld();
        if (!world.isPresent()) {
            return Result.failure();
        }

        Optional<Vector3d> coordinates = ctx.getLiteral("Location").getVector();
        if (!coordinates.isPresent()) {
            return Result.failure();
        }

        Location newLocation = new Location(world.get(), coordinates.get().getX(), coordinates.get().getY(), coordinates.get().getZ());
        boolean safely = ctx.getLiteral("Safely", false).getBoolean();

        if (safely) {
            player.get().setLocationSafely(newLocation);
        } else {
            player.get().setLocation(newLocation);
        }
        return Result.success();
    }
}
