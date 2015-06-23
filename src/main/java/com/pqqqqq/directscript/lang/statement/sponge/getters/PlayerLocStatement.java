package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that gets the {@link Location} of a player
 */
public class PlayerLocStatement extends Statement<Double[]> {

    public PlayerLocStatement() {
        super(Syntax.builder()
                .identifiers("playerloc")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Player")))
                .build());
    }

    @Override
    public Result<Double[]> run(Context ctx) {
        Optional<Player> playerOptional = ctx.getPlayerOrCauser("Player");
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        Location loc = playerOptional.get().getLocation();
        Double[] result = new Double[]{loc.getX(), loc.getY(), loc.getZ()};
        return Result.<Double[]>builder().success().result(result).literal(result).build();
    }
}
