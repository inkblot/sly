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
import com.jnape.palatable.lambda.monad.transformer.builtin.IterateT;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import lombok.NoArgsConstructor;

import static com.jnape.palatable.lambda.monad.transformer.builtin.IterateT.iterateT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class IntoIterateT<M extends MonadRec<?, M>, A> implements
    HyperFn<MaybeT<M, ?>, Tuple2<A, IterateT<M, A>>, IterateT<M, ?>, A> {

    public static final IntoIterateT<?, ?> INSTANCE = new IntoIterateT<>();

    @Override
    public <GB extends Functor<A, IterateT<M, ?>>> GB apply(Functor<Tuple2<A, IterateT<M, A>>, MaybeT<M, ?>> fa) {
        return iterateT(fa.<MaybeT<M, Tuple2<A, IterateT<M, A>>>>coerce().runMaybeT()).coerce();
    }

    /**
     * A {@link HyperFn} that constructs an {@link IntoIterateT} from a
     * {@link MaybeT} that shares the same argument {@link MonadRec} and bears
     * a {@link Tuple2} containing the head and tail of an {@link IterateT}.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <A> the right type
     * @return a <code>HyperFn</code> from {@link MaybeT} <code>M</code> with a
     * carrier consisting of a head and tail {@link Tuple2} to {@link IterateT}
     * tail
     * @see RunningIterateT
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A> IntoIterateT<M, A> intoIterateT() {
        return (IntoIterateT<M, A>) INSTANCE;
    }
}
