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

import com.jnape.palatable.lambda.functions.specialized.Lift;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.MonadT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class Lifting<N extends MonadT<?, ?, ?, N>, M extends MonadRec<?, M>, NM extends MonadT<M, ?, NM, N>>
    implements NaturalTransformation<M, NM> {

    private final Lift<N> liftN;

    @Override
    public <A, GA extends Functor<A, NM>> GA apply(Functor<A, M> fa) {
        return liftN.<A, M, MonadT<M, A, NM, N>>apply(fa.coerce()).coerce();
    }

    /**
     * Constructs a {@link NaturalTransformation} of that lifts <code>M</code>
     * into a {@link MonadT} <code>N</code> using a {@link Lift}.
     *
     * @param <M>   the {@link MonadRec} type
     * @param <N>   the {@link MonadT} type
     * @param <NM>  the {@link MonadT} <code>N</code> parameterized with
     *              <code>M</code>
     * @param liftN a {@link Lift} into <code>N</code>
     * @return a transformation from <code>M</code> to <code>N</code>
     */
    public static <N extends MonadT<?, ?, ?, N>, M extends MonadRec<?, M>, NM extends MonadT<M, ?, NM, N>>
    Lifting<N, M, NM> lifting(Lift<N> liftN) {
        return new Lifting<>(liftN);
    }
}
