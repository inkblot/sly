/*
 * Copyright (c) 2023 Nate Riffe
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

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.EitherT;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RunningEitherT<M extends MonadRec<?, M>, L, R> implements
    HyperFn<EitherT<M, L, ?>, R, M, Either<L, R>> {

    public static final RunningEitherT<?, ?, ?> INSTANCE = new RunningEitherT<>();

    @Override
    public <GB extends Functor<Either<L, R>, M>> GB apply(Functor<R, EitherT<M, L, ?>> fa) {
        return fa.<EitherT<M, L, R>>coerce().runEitherT().coerce();
    }

    /**
     * A {@link HyperFn} that runs an {@link EitherT}, producing an instance of
     * its argument {@link MonadRec} with an {@link Either} as the carrier.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <L> ths left type of the {@link EitherT}
     * @param <R> the carrier type
     * @return a <code>HyperFn</code> that runs {@link EitherT}
     * @see IntoEitherT
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, L, R> RunningEitherT<M, L, R> runningEitherT() {
        return (RunningEitherT<M, L, R>) INSTANCE;
    }
}
