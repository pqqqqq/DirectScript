package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that rotates a player's view
 */
@Statement.Concept
public class RotateStatement extends Statement {

    public RotateStatement() {
        super(Syntax.builder()
                .identifiers("rotate")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Rotation")), Arguments.of(Argument.from("Player"), ",", Argument.from("Rotation")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> player = ctx.getLiteral("Player").getPlayer();
        if (!player.isPresent()) {
            return Result.failure();
        }

        Optional<Vector3d> rotationArray = ctx.getLiteral("Rotation").getVector();
        if (!rotationArray.isPresent()) {
            return Result.failure();
        }

        player.get().setRotation(rotationArray.get());
        return Result.success();
    }
}
