/*
 * Copyright (c) 2023-2024 Nate Riffe
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
package org.movealong.sly.lang.hfn;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.monad.MonadError;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class CatchError<M extends MonadError<E, ?, M>, E, A> implements HyperFn<M, A, M, A> {
    private final Fn1<? super E, ? extends Monad<A, M>> fn;

    @Override
    public <GB extends Functor<A, M>> GB apply(Functor<A, M> fa) {
        return fa.<MonadError<E, A, M>>coerce().catchError(fn).coerce();
    }

    /**
     * A {@link HyperFn} that implements a standalone version of
     * {@link MonadError#catchError}.
     *
     * @param <M> the {@link Monad} type
     * @param <E> the error type
     * @param <A> the carrier type
     * @param fn  the error catching function
     * @return A <code>HyperFn</code> that encapsulates
     * {@link MonadError#catchError}
     */
    public static <M extends MonadError<E, ?, M>, E, A>
    CatchError<M, E, A> catchError(Fn1<? super E, ? extends Monad<A, M>> fn) {
        return new CatchError<>(fn);
    }
}
