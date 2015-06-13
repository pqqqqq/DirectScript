package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 * Represents the immutable result of executing a {@link com.pqqqqq.directscript.lang.reader.Context} by {@link Context#run()}
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

    /**
     * Gets a new {@link com.pqqqqq.directscript.lang.statement.Result.Builder} instance
     *
     * @param <T> the type for the builder/result
     * @return the new builder instance
     */
    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    /**
     * Gets a success result that is cast to a generic type
     *
     * @param <T> the generic type for this result
     * @return the result
     */
    public static <T> Result<T> success() {
        return (Result<T>) SUCCESS;
    }

    /**
     * Gets a failure result that is cast to a generic type
     *
     * @param <T> the generic type for this result
     * @return the result
     */
    public static <T> Result<T> failure() {
        return (Result<T>) FAILURE;
    }

    /**
     * Gets the {@link Optional} result of this {@link Result}
     *
     * @return the result
     */
    public Optional<T> getResult() {
        return result;
    }

    /**
     * Gets the {@link Optional} {@link Literal} result of this {@link Result}
     *
     * @return the literal result
     */
    public Optional<Literal<T>> getLiteralResult() {
        return literalResult;
    }

    /**
     * Gets if this {@link Result} represents a successul result
     *
     * @return true if successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * The builder for building {@link Result}s
     * @param <T> the type to cast the result to
     */
    public static class Builder<T> {
        private T result = null;
        private Literal<T> literalResult = null;
        private Boolean success = null;

        Builder() {
        }

        /**
         * Sets the result of this {@link Result}
         * @param result the new result
         * @return this builder, for fluency
         * @see Result#getResult()
         */
        public Builder<T> result(T result) {
            this.result = result;
            return this;
        }

        /**
         * Sets the {@link Literal} result of this {@link Result}
         * @param literalResult the new result, to be created into a literal
         * @return this builder, for fluency
         * @see Result#getLiteralResult()
         */
        public Builder<T> literal(T literalResult) {
            this.literalResult = Literal.getLiteralBlindly(literalResult);
            return this;
        }

        /**
         * Sets the {@link Literal} result of this {@link Result}
         * @param literalResult the new literal result
         * @return this builder, for fluency
         * @see Result#getLiteralResult()
         */
        public Builder<T> literal(Literal<T> literalResult) {
            this.literalResult = literalResult;
            return this;
        }

        /**
         * Sets the success value of this {@link Result}
         * @param success the success value
         * @return this builder, for fluency
         * @see Result#isSuccess()
         */
        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        /**
         * Sets the success value of this {@link Result} to true
         * @return this builder, for fluency
         * @see Result#isSuccess()
         */
        public Builder<T> success() {
            this.success = true;
            return this;
        }

        /**
         * Sets the success value of this {@link Result} to false
         * @return this builder, for fluency
         * @see Result#isSuccess()
         */
        public Builder<T> failure() {
            this.success = false;
            return this;
        }

        /**
         * Builds the current data into a new {@link Result} instance
         * @return the new result instance
         */
        public Result<T> build() {
            checkNotNull(success, "Success state must be specified");
            return new Result<T>(result, literalResult, success);
        }
    }
}
