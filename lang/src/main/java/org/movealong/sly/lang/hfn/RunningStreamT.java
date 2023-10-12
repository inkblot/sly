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
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import com.jnape.palatable.winterbourne.StreamT;
import lombok.NoArgsConstructor;

import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.maybeT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RunningStreamT<M extends MonadRec<?, M>, A> implements
    HyperFn<StreamT<M, ?>, A, MaybeT<M, ?>, Tuple2<Maybe<A>, StreamT<M, A>>> {

    public static final RunningStreamT<?, ?> INSTANCE = new RunningStreamT<>();

    @Override
    public <GB extends Functor<Tuple2<Maybe<A>, StreamT<M, A>>, MaybeT<M, ?>>>
    GB apply(Functor<A, StreamT<M, ?>> fa) {
        return maybeT(fa.<StreamT<M, A>>coerce().runStreamT()).coerce();
    }

    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A>
    RunningStreamT<M, A> runningStreamT() {
        return (RunningStreamT<M, A>) INSTANCE;
    }
}
