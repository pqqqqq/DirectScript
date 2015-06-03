package com.pqqqqq.directscript.lang.statement.statements.sponge;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
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

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        String player = line.getLiteral(scriptInstance, 1).getString();
        String message = line.getLiteral(scriptInstance, 2).getString();

        Optional<Player> serverPlayer = DirectScript.instance().getGame().getServer().getPlayer(player);
        if (!serverPlayer.isPresent()) {
            return StatementResult.failure();
        }

        serverPlayer.get().sendMessage(Texts.of(message));
        return StatementResult.success();
    }
}
