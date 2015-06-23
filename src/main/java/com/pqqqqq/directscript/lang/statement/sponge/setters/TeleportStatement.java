package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that teleports a player to a coordinate location
 */
public class TeleportStatement extends Statement {

    public TeleportStatement() {
        super(Syntax.builder()
                .identifiers("tp")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Coordinates")))
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("Coordinates")))
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("World"), ",", Argument.from("Coordinates")))
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("World"), ",", Argument.from("Coordinates"), ",", Argument.from("Safely")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> player = ctx.getPlayerOrCauser("Player");
        if (!player.isPresent()) {
            return Result.failure();
        }

        Optional<World> world = ctx.getWorldOrCauserWorld("World");
        if (!world.isPresent()) {
            return Result.failure();
        }

        List<LiteralHolder> coordinates = ctx.getLiteral("Coordinates").getArray();
        if (coordinates.size() < 3) { // We need 3 coordinates, it's a 3D game brah
            return Result.failure();
        }

        Location currentLocation = player.get().getLocation();
        double x = coordinates.get(0).getData().or(currentLocation.getX()).getNumber(); // If its null inside the array, we use the player's current coordinate
        double y = coordinates.get(1).getData().or(currentLocation.getY()).getNumber();
        double z = coordinates.get(2).getData().or(currentLocation.getZ()).getNumber();

        Location newLocation = new Location(world.get(), x, y, z);
        boolean safely = ctx.getLiteral("Safely", false).getBoolean();

        if (safely) {
            player.get().setLocationSafely(newLocation);
        } else {
            player.get().setLocation(newLocation);
        }
        return Result.success();
    }
}
