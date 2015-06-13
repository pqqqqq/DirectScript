package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.sponge.SpongeStatement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that checks if a player is online
 */
public class OnlineStatement extends SpongeStatement<Boolean> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"online"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Player").build()
        };
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        Optional<Player> player = ctx.getLiteral(0).getPlayer();
        return Result.<Boolean>builder().result(player.isPresent()).literal(player.isPresent()).build();
    }
}
