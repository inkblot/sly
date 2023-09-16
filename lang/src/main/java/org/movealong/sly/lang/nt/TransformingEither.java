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
package org.movealong.sly.lang.nt;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.functions.specialized.Pure;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.EitherT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static com.jnape.palatable.lambda.monad.transformer.builtin.EitherT.eitherT;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class TransformingEither<M extends MonadRec<?, M>, L> implements
    NaturalTransformation<Either<L, ?>, EitherT<M, L, ?>> {

    private final Pure<M> pureM;

    @Override
    public <A, GA extends Functor<A, EitherT<M, L, ?>>> GA apply(Functor<A, Either<L, ?>> fa) {
        return eitherT(pureM.<Either<L, A>, MonadRec<Either<L, A>, M>>apply(fa.coerce())).coerce();
    }

    /**
     * A {@link NaturalTransformation} from {@link Either} to {@link EitherT}
     * that introduces an argument monad in pure form.
     *
     * @param <M>   the argument monad
     * @param pureM the pure function for the argument monad
     * @param <L>   the left type
     * @return a natural transformation of {@link Either}
     */
    public static <M extends MonadRec<?, M>, L> TransformingEither<M, L> transformingEither(Pure<M> pureM) {
        return new TransformingEither<>(pureM);
    }
}
