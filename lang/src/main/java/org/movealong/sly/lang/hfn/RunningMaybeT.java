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
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RunningMaybeT<M extends MonadRec<?, M>, A> implements
    HyperFn<MaybeT<M, ?>, A, M, Maybe<A>> {

    public static final RunningMaybeT<?, ?> INSTANCE = new RunningMaybeT<>();

    @Override
    public <GB extends Functor<Maybe<A>, M>> GB apply(Functor<A, MaybeT<M, ?>> fa) {
        return fa.<MaybeT<M, A>>coerce().runMaybeT().coerce();
    }

    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A> RunningMaybeT<M, A> runningMaybeT() {
        return (RunningMaybeT<M, A>) INSTANCE;
    }
}
