package com.pqqqqq.directscript.lang.statement.getters.sponge;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(prefix = "@", identifiers = {"online"})
public class OnlineStatement implements IStatement<Boolean> {

    public StatementResult<Boolean> run(Line.LineContainer line) {
        Optional<Player> player = line.getLiteral(0).getPlayer();
        return StatementResult.<Boolean>builder().result(player.isPresent()).literal(player.isPresent()).build();
    }
}
