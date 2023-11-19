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
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;

import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.maybeT;

public class JoiningMaybe<M extends MonadRec<?, M>, A> implements
    HyperFn<MaybeT<M, ?>, Maybe<A>, MaybeT<M, ?>, A> {

    public static final JoiningMaybe<?, ?> INSTANCE = new JoiningMaybe<>();

    @Override
    public <GB extends Functor<A, MaybeT<M, ?>>> GB apply(Functor<Maybe<A>, MaybeT<M, ?>> fa) {
        return maybeT(fa.<MaybeT<M, Maybe<A>>>coerce().runMaybeT().fmap(Monad::join)).coerce();
    }

    /**
     * A {@link HyperFn} that takes a {@link MaybeT} with a {@link Maybe} in
     * the carrier and joins the effects of the <code>Maybe</code> and the
     * <code>MaybeT</code>.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <A> the carrier type
     * @return an interpreter that transforms {@link MaybeT}
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A> JoiningMaybe<M, A> joiningMaybe() {
        return (JoiningMaybe<M, A>) INSTANCE;
    }
}
