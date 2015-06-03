package com.pqqqqq.directscript.lang.env;

import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.data.variable.IVariableContainer;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;

/**
 * Created by Kevin on 2015-06-02.
 * Represents an environment that branches from a {@link Script}
 */
public interface Environment extends IVariableContainer {

    Script getScript();

    Cause getCause();
}
