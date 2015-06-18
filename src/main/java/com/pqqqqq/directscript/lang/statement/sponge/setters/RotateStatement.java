package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.sponge.SpongeStatement;
import org.spongepowered.api.entity.player.Player;

import java.util.List;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that rotates a player's view
 */
@Statement.Concept
public class RotateStatement extends SpongeStatement {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"rotate"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Player").optional().build(),
                Argument.builder().name("Rotation").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> player = ctx.getPlayerOrCauser(0);
        if (!player.isPresent()) {
            return Result.failure();
        }

        List<LiteralHolder> rotationArray = ctx.getLiteral(1).getArray();
        if (rotationArray.size() < 2) { // Even though rotation is 3D, no one really ever uses roll
            return Result.failure();
        }

        Vector3d currentRotation = player.get().getRotation();
        double yaw = rotationArray.get(0).getData().or(currentRotation.getX()).getNumber();
        double pitch = rotationArray.get(1).getData().or(currentRotation.getY()).getNumber();
        double roll = (rotationArray.size() > 2 ? rotationArray.get(2).getData().or(currentRotation.getZ()).getNumber() : currentRotation.getZ()); // Roll isn't needed most of the time

        Vector3d newRotation = new Vector3d(yaw, pitch, roll);
        player.get().setRotation(newRotation);
        return Result.success();
    }
}
