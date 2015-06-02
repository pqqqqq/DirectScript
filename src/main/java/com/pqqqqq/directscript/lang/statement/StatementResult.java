package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 */
public class StatementResult<T> {
    private static final StatementResult<Object> SUCCESS = builder().success().build();
    private static final StatementResult<Object> FAILURE = builder().failure().build();

    private final Optional<T> result;
    private final boolean success;

    StatementResult(T result, boolean success) {
        this.result = Optional.fromNullable(result);
        this.success = success;
    }

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static <T> StatementResult<T> success() {
        return (StatementResult<T>) SUCCESS;
    }

    public static <T> StatementResult<T> failure() {
        return (StatementResult<T>) FAILURE;
    }

    public Optional<T> getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public static class Builder<T> {
        @Nullable private T result = null;
        @Nullable private Boolean success = null;

        Builder() {
        }

        public Builder<T> result(T result) {
            this.result = result;
            return this;
        }

        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder<T> success() {
            this.success = true;
            return this;
        }

        public Builder<T> failure() {
            this.success = false;
            return this;
        }

        public StatementResult<T> build() {
            checkNotNull(success, "Success state must be specified");
            return new StatementResult<T>(result, success);
        }
    }
}
