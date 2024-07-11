/*
 * Copyright (c) 2024 Nate Riffe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.movealong.sly.lang.nt;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Try.success;
import static com.jnape.palatable.lambda.adt.Try.trying;
import static com.jnape.palatable.lambda.functions.Fn0.fn0;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn4.IfThenElse.ifThenElse;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static lombok.AccessLevel.PRIVATE;

/**
 * A {@link NaturalTransformation} that runs an {@link IO} safely and
 * concurrently, capturing the result of performing the operation as a
 * {@link Try}. Applying this transformation to an {@link IO} that yields a
 * result will produce a {@link Try} in the success state, and applying it to
 * an {@link IO} that throws an exception will result in a {@link Try} in the
 * failure state. The transformation will run the {@link IO} asynchronously and
 * await its completion before returning with an optional timeout.
 */
@AllArgsConstructor(access = PRIVATE)
public final class ConcurrentlyPerformingIO implements NaturalTransformation<IO<?>, Try<?>> {

    private final Maybe<Executor> executor;
    private final Maybe<Duration> timeout;

    @Override
    public <A, GA extends Functor<A, Try<?>>> GA apply(Functor<A, IO<?>> fa) {
        return success(executor.match(
            fn0(() -> fa.<IO<A>>coerce().unsafePerformAsyncIO()),
            e -> fa.<IO<A>>coerce().unsafePerformAsyncIO(e)))
            .flatMap(future -> trying(() -> timeout.match(
                fn0(future::get),
                to -> future.get(to.toMillis(), MILLISECONDS))))
            .catchError(unwrap(ExecutionException.class).fmap(Try::failure))
            .coerce();
    }

    private Fn1<Throwable, Throwable> unwrap(Class<? extends Throwable> exType) {
        return ifThenElse(exType::isInstance,
                          Throwable::getCause,
                          id());
    }

    /**
     * Constructs a {@link ConcurrentlyPerformingIO} that performs an
     * {@link IO} concurrently, capturing the result of the operation as a
     * {@link Try}. The {@link IO} will run in the forkjoin pool, and will
     * not time out.
     *
     * @return a {@link ConcurrentlyPerformingIO}
     */
    public static ConcurrentlyPerformingIO concurrentlyPerformingIO() {
        return new ConcurrentlyPerformingIO(nothing(), nothing());
    }

    /**
     * Constructs a {@link ConcurrentlyPerformingIO} that performs an {@link IO}
     * concurrently, capturing the result of the operation as a {@link Try}.
     * The {@link IO} will run in the supplied {@link Executor}, and will not
     * time out.
     *
     * @param executor the executor to use
     * @return a {@link ConcurrentlyPerformingIO}
     */
    public static ConcurrentlyPerformingIO concurrentlyPerformingIO(Executor executor) {
        return new ConcurrentlyPerformingIO(just(executor), nothing());
    }

    /**
     * Constructs a {@link ConcurrentlyPerformingIO} that performs an {@link IO}
     * concurrently, capturing the result of the operation as a {@link Try}.
     * The {@link IO} will run in the forkjoin pool, and the invoking thread
     * will time out after the supplied {@link Duration}. In the event that a
     * timeout occurs, the {@link IO} will continue to run asynchronously to
     * completion.
     *
     * @param timeout the timeout
     * @return a {@link ConcurrentlyPerformingIO}
     */
    public static ConcurrentlyPerformingIO concurrentlyPerformingIO(Duration timeout) {
        return new ConcurrentlyPerformingIO(nothing(), just(timeout));
    }

    /**
     * Constructs a {@link ConcurrentlyPerformingIO} that performs an
     * {@link IO} concurrently, capturing the result of the operation as a
     * {@link Try}. The {@link IO} will run in the supplied {@link Executor},
     * and will time out after the supplied {@link Duration}. In the event that
     * a timeout occurs, the {@link IO} will continue to run asynchronously to
     * completion.
     *
     * @param executor the executor to use
     * @param timeout  the timeout
     * @return a {@link ConcurrentlyPerformingIO}
     */
    public static ConcurrentlyPerformingIO concurrentlyPerformingIO(Executor executor, Duration timeout) {
        return new ConcurrentlyPerformingIO(just(executor), just(timeout));
    }
}
