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

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.specialized.Pure;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.maybeT;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class TransformingMaybe<M extends MonadRec<?, M>> implements
    NaturalTransformation<Maybe<?>, MaybeT<M, ?>> {

    private final Pure<M> pureM;

    @Override
    public <A, GA extends Functor<A, MaybeT<M, ?>>> GA apply(Functor<A, Maybe<?>> fa) {
        return maybeT(pureM.<Maybe<A>, MonadRec<Maybe<A>, M>>apply(fa.coerce())).coerce();
    }

    /**
     * A {@link NaturalTransformation} from {@link Maybe} to {@link MaybeT}
     * that introduces an argument monad in pure form.
     *
     * @param <M>   the argument monad
     * @param pureM the pure function for the argument monad
     * @return a natural transformation of {@link Maybe}
     */
    public static <M extends MonadRec<?, M>> NaturalTransformation<Maybe<?>, MaybeT<M, ?>>
    transformingMaybe(Pure<M> pureM) {
        return new TransformingMaybe<>(pureM);
    }
}
