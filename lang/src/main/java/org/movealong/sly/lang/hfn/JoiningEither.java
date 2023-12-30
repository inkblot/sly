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
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.EitherT;
import lombok.NoArgsConstructor;

import static com.jnape.palatable.lambda.monad.transformer.builtin.EitherT.eitherT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class JoiningEither<M extends MonadRec<?, M>, L, R> implements
    HyperFn<EitherT<M, L, ?>, Either<L, R>, EitherT<M, L, ?>, R> {

    public static final JoiningEither<?, ?, ?> INSTANCE = new JoiningEither<>();

    @Override
    public <GB extends Functor<R, EitherT<M, L, ?>>> GB apply(Functor<Either<L, R>, EitherT<M, L, ?>> fa) {
        return eitherT(fa.<EitherT<M, L, Either<L, R>>>coerce().runEitherT().fmap(Monad::join)).coerce();
    }

    /**
     * A {@link HyperFn} that takes an {@link EitherT} with an {@link Either}
     * in the carrier and, provided they share a common left type, joins the
     * effects of the {@link Either} and {@link EitherT}.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <L> the left type
     * @param <R> the right type
     * @return a <code>HyperFn</code> that transforms {@link EitherT}
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, L, R> JoiningEither<M, L, R> joiningEither() {
        return (JoiningEither<M, L, R>) INSTANCE;
    }
}
