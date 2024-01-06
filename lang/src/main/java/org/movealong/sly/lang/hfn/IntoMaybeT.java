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

import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.maybeT;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class IntoMaybeT<M extends MonadRec<?, M>, A> implements
    HyperFn<M, Maybe<A>, MaybeT<M, ?>, A> {

    public static final IntoMaybeT<?, ?> INSTANCE = new IntoMaybeT<>();

    @Override
    public <GB extends Functor<A, MaybeT<M, ?>>> GB apply(Functor<Maybe<A>, M> fa) {
        return maybeT(fa.coerce()).coerce();
    }

    /**
     * A {@link HyperFn} that constructs {@link MaybeT} from a {@link MonadRec}
     * bearing {@link Maybe}.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <A> the carrier type
     * @return a <code>HyperFn</code> from <code>M</code> {@link Maybe} to
     * {@link MaybeT}.
     * @see RunningMaybeT
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A> IntoMaybeT<M, A> intoMaybeT() {
        return (IntoMaybeT<M, A>) INSTANCE;
    }
}
