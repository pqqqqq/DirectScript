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
@Statement(prefix = "@", identifiers = {"permission"})
public class PermissionStatement implements IStatement<Boolean> {

    public StatementResult<Boolean> run(Line.LineContainer line) {
        Optional<Player> player = line.getPlayerOrCauser(0);
        String permission = line.getLiteral(1).getString();

        if (!player.isPresent()) {
            return StatementResult.failure();
        }

        boolean result = player.get().hasPermission(permission);
        return StatementResult.<Boolean>builder().success().result(result).literal(result).build();
    }
}
