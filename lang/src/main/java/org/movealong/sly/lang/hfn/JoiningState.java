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

import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.State;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.EitherT;
import com.jnape.palatable.lambda.monad.transformer.builtin.StateT;
import lombok.NoArgsConstructor;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class JoiningState<S, M extends MonadRec<?, M>, A> implements
    HyperFn<StateT<S, M, ?>, State<S, A>, StateT<S, M, ?>, A> {

    public static final JoiningState<?, ?, ?> INSTANCE = new JoiningState<>();

    @Override
    public <GB extends Functor<A, StateT<S, M, ?>>> GB apply(Functor<State<S, A>, StateT<S, M, ?>> fa) {
        return StateT.<S, M, A>stateT(
                         s -> fa.<StateT<S, M, State<S, A>>>coerce()
                                .runStateT(s)
                                .fmap(into(State::run)))
                     .coerce();
    }

    /**
     * A <code>HyperFn</code> that takes an {@link StateT} with an
     * {@link State} in the carrier and, provided they share a common state
     * type, joins the <code>State</code> effectss.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <S> the state type
     * @param <A> the carrier type
     * @return an interpreter that transforms {@link EitherT}
     */
    @SuppressWarnings("unchecked")
    public static <S, M extends MonadRec<?, M>, A> JoiningState<S, M, A> joiningState() {
        return (JoiningState<S, M, A>) INSTANCE;
    }
}
