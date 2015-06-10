package com.pqqqqq.directscript.lang.statement.setters.sponge;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(prefix = "@", identifiers = { "PLAYER" })
public class PlayerStatement implements IStatement {

    public StatementResult run(Line.LineContainer line) {
        Optional<Player> player = line.getPlayerOrCauser(0);
        String message = line.getLiteral(1).getString();

        if (!player.isPresent()) {
            return StatementResult.failure();
        }

        player.get().sendMessage(Texts.of(message));
        return StatementResult.success();
    }
}
