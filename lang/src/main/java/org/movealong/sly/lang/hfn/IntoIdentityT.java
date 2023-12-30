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
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT;

import static com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT.identityT;

public class IntoIdentityT<M extends MonadRec<?, M>, A> implements
    HyperFn<M, Identity<A>, IdentityT<M, ?>, A> {

    public static final IntoIdentityT<?, ?> INSTANCE = new IntoIdentityT<>();

    @Override
    public <GB extends Functor<A, IdentityT<M, ?>>> GB apply(Functor<Identity<A>, M> fa) {
        return identityT(fa.coerce()).coerce();
    }

    /**
     * A {@link HyperFn} that constructs {@link IdentityT} from a
     * {@link MonadRec} bearing {@link Identity}.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <A> the carrier type
     * @return a <code>HyperFn</code> from <code>M</code> {@link Identity} to
     * {@link IdentityT}
     * @see RunningIdentityT
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A> IntoIdentityT<M, A> intoIdentityT() {
        return (IntoIdentityT<M, A>) INSTANCE;
    }
}
