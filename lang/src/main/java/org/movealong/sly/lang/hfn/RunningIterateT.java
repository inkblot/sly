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

import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.maybeT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RunningIterateT<M extends MonadRec<?, M>, A> implements
    HyperFn<IterateT<M, ?>, A, MaybeT<M, ?>, Tuple2<A, IterateT<M, A>>> {

    public static final RunningIterateT<?, ?> INSTANCE = new RunningIterateT<>();

    @Override
    public <GB extends Functor<Tuple2<A, IterateT<M, A>>, MaybeT<M, ?>>> GB apply(Functor<A, IterateT<M, ?>> fa) {
        return maybeT(fa.<IterateT<M, A>>coerce().runIterateT()).coerce();
    }

    /**
     * A <code>HyperFn</code> that runs an {@link IterateT}, producing an
     * instance of {@link MaybeT} with the same argument {@link MonadRec}
     * bearing a tuple of the head and tail of the iteration.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <A> the carrier type
     * @return an interpreter that runs {@link IterateT}
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A> RunningIterateT<M, A> runningIterateT() {
        return (RunningIterateT<M, A>) INSTANCE;
    }
}
