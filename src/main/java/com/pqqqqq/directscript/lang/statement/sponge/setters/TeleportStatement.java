package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

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
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("Location"), ",", Argument.from("Safely")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> player = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        if (!player.isPresent()) {
            return Result.failure();
        }

        Optional<Location> locationOptional = ctx.getLiteral("Location", Location.class).getAs(Location.class);
        if (!locationOptional.isPresent()) {
            return Result.failure();
        }

        boolean safely = ctx.getLiteral("Safely", false).getBoolean();
        if (safely) {
            player.get().setLocationSafely(locationOptional.get());
        } else {
            player.get().setLocation(locationOptional.get());
        }
        return Result.success();
    }
}
