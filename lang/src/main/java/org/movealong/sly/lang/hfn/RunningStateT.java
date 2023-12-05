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

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.StateT;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class RunningStateT<M extends MonadRec<?, M>, S, A> implements
    HyperFn<StateT<S, M, ?>, A, M, Tuple2<A, S>> {
    private final S s;

    @Override
    public <GB extends Functor<Tuple2<A, S>, M>> GB apply(Functor<A, StateT<S, M, ?>> fa) {
        return fa.<StateT<S, M, A>>coerce().runStateT(s).coerce();
    }

    /**
     * An <code>HyperFn</code> that runs a {@link StateT}, producing an
     * instance of its argument {@link MonadRec} bearing a tuple of final state
     * &lt;S&gt; and the original carrier.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <S> the state type
     * @param <A> the carrier type
     * @param s   the initial state
     * @return a <code>HyperFn</code> that runs {@link StateT}
     */
    public static <M extends MonadRec<?, M>, S, A> RunningStateT<M, S, A> runningStateT(S s) {
        return new RunningStateT<>(s);
    }
}
