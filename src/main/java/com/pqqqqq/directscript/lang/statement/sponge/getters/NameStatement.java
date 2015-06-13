package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.sponge.SpongeStatement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-04.
 */
public class NameStatement extends SpongeStatement<String> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"playername"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Player").optional().build()
        };
    }

    @Override
    public Result<String> run(Context ctx) {
        Optional<Player> player = ctx.getPlayerOrCauser(0);
        if (!player.isPresent()) {
            return Result.failure();
        }

        return Result.<String>builder().success().result(player.get().getName()).literal(player.get().getName()).build();
    }
}
