package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 */
public class Result<T> {
    private static final Result<Object> SUCCESS = builder().success().build();
    private static final Result<Object> FAILURE = builder().failure().build();

    private final Optional<T> result;
    private final Optional<Literal<T>> literalResult;
    private final boolean success;

    Result(T result, Literal<T> literalResult, boolean success) {
        this.result = Optional.fromNullable(result);
        this.literalResult = Optional.fromNullable(literalResult);
        this.success = success;
    }

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static <T> Result<T> success() {
        return (Result<T>) SUCCESS;
    }

    public static <T> Result<T> failure() {
        return (Result<T>) FAILURE;
    }

    public Optional<T> getResult() {
        return result;
    }

    public Optional<Literal<T>> getLiteralResult() {
        return literalResult;
    }

    public boolean isSuccess() {
        return success;
    }

    public static class Builder<T> {
        @Nullable private T result = null;
        @Nullable private Literal<T> literalResult = null;
        @Nullable private Boolean success = null;

        Builder() {
        }

        public Builder<T> result(T result) {
            this.result = result;
            return this;
        }

        public Builder<T> literal(T literalResult) {
            this.literalResult = Literal.getLiteralBlindly(literalResult);
            return this;
        }

        public Builder<T> literal(Literal<T> literalResult) {
            this.literalResult = literalResult;
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

        public Result<T> build() {
            checkNotNull(success, "Success state must be specified");
            return new Result<T>(result, literalResult, success);
        }
    }
}