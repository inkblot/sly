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

import static com.jnape.palatable.lambda.monad.transformer.builtin.EitherT.eitherT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class IntoEitherT<M extends MonadRec<?, M>, L, R> implements
    HyperFn<M, Either<L, R>, EitherT<M, L, ?>, R> {

    public static final IntoEitherT<?, ?, ?> INSTANCE = new IntoEitherT<>();

    @Override
    public <GB extends Functor<R, EitherT<M, L, ?>>> GB apply(Functor<Either<L, R>, M> fa) {
        return eitherT(fa.coerce()).coerce();
    }

    /**
     * A {@link HyperFn} that constructs {@link EitherT} from a
     * {@link MonadRec} bearing {@link Either}.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <R> the right type
     * @param <L> the left type
     * @return a <code>HyperFn</code> from <code>M</code> {@link Either} to
     * {@link EitherT}
     * @see RunningEitherT
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, L, R> IntoEitherT<M, L, R> intoEitherT() {
        return (IntoEitherT<M, L, R>) INSTANCE;
    }
}
