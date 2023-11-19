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
import com.jnape.palatable.lambda.monad.transformer.builtin.EitherT;
import com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class JoiningIdentity<M extends MonadRec<?, M>, A> implements
    HyperFn<IdentityT<M, ?>, Identity<A>, IdentityT<M, ?>, A> {

    public static final JoiningIdentity<?, ?> INSTANCE = new JoiningIdentity<>();

    @Override
    public <GB extends Functor<A, IdentityT<M, ?>>> GB apply(Functor<Identity<A>, IdentityT<M, ?>> fa) {
        return fa.fmap(Identity::runIdentity).coerce();
    }

    /**
     * A <code>HyperFn</code> that takes an {@link IdentityT} with an
     * {@link Identity} in the carrier and joins the <code>Identity</code>
     * effects.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <A> the carrier type
     * @return an interpreter that transforms {@link EitherT}
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>, A> JoiningIdentity<M, A> joiningIdentity() {
        return (JoiningIdentity<M, A>) INSTANCE;
    }
}
