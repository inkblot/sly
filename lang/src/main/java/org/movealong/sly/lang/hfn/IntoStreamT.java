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

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.specialized.Pure;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import com.jnape.palatable.winterbourne.StreamT;
import lombok.NoArgsConstructor;

import static com.jnape.palatable.winterbourne.StreamT.streamT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class IntoStreamT<M extends MonadRec<?, M>, A> implements
    HyperFn<MaybeT<M, ?>, Tuple2<Maybe<A>, StreamT<M, A>>, StreamT<M, ?>, A> {

    public static final IntoStreamT<?, ?> INSTANCE = new IntoStreamT<>();

    @Override
    public <GB extends Functor<A, StreamT<M, ?>>> GB apply(Functor<Tuple2<Maybe<A>, StreamT<M, A>>, MaybeT<M, ?>> fa) {
        MonadRec<Maybe<Tuple2<Maybe<A>, StreamT<M, A>>>, M> mNode =
            fa.<MaybeT<M, Tuple2<Maybe<A>, StreamT<M, A>>>>coerce().runMaybeT();
        return streamT(() -> mNode, Pure.of(mNode)).coerce();
    }

    /**
     * A {@link HyperFn} that constructs a {@link StreamT} from a
     * {@link MaybeT} that shares the same argument {@link MonadRec} and bears
     * a {@link Tuple2} containing the head and tail of a {@link StreamT}.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <A> the right type
     * @return a <code>HyperFn</code> from {@link MaybeT} <code>M</code> with a
     * carrier consisting of a head and tail {@link Tuple2} to {@link StreamT}
     * tail
     * @see RunningStreamT
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A> IntoStreamT<M, A> intoStreamT() {
        return (IntoStreamT<M, A>) INSTANCE;
    }
}
