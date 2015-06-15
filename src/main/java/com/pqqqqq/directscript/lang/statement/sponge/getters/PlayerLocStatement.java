package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.sponge.SpongeStatement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that gets the {@link Location} of a player
 */
public class PlayerLocStatement extends SpongeStatement<Double[]> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"playerloc"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Player").optional().build()
        };
    }

    @Override
    public Result<Double[]> run(Context ctx) {
        Optional<Player> playerOptional = ctx.getPlayerOrCauser(0);
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        Location loc = playerOptional.get().getLocation();
        Double[] result = new Double[]{loc.getX(), loc.getY(), loc.getZ()};
        return Result.<Double[]>builder().success().result(result).literal(result).build();
    }
}
