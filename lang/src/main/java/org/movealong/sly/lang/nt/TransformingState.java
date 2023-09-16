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

import com.jnape.palatable.lambda.functions.specialized.Pure;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.State;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.StateT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class TransformingState<M extends MonadRec<?, M>, S> implements
    NaturalTransformation<State<S, ?>, StateT<S, M, ?>> {

    private final Pure<M> pureM;

    @Override
    public <A, GA extends Functor<A, StateT<S, M, ?>>> GA apply(Functor<A, State<S, ?>> fa) {
        return StateT.<S, M, A>stateT(s -> pureM.apply(fa.<State<S, A>>coerce().run(s))).coerce();
    }

    /**
     * A {@link NaturalTransformation} from {@link State} to {@link StateT}
     * that introduces an argument monad in pure form.
     *
     * @param <M>   the argument monad
     * @param <S>   the state type
     * @param pureM the pure function for the argument monad
     * @return a natural transformation of {@link State}
     */
    public static <M extends MonadRec<?, M>, S> TransformingState<M, S> transformingState(Pure<M> pureM) {
        return new TransformingState<>(pureM);
    }
}
