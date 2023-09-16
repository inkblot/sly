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
package org.movealong.sly.lang.nt;

import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class RunningIdentityT<M extends MonadRec<?, M>> implements NaturalTransformation<IdentityT<M, ?>, M> {

    public static final RunningIdentityT<?> INSTANCE = new RunningIdentityT<>();

    @Override
    public <A, GA extends Functor<A, M>> GA apply(Functor<A, IdentityT<M, ?>> fa) {
        return fa.<IdentityT<M, A>>coerce().runIdentityT().fmap(Identity::runIdentity).coerce();
    }

    /**
     * A {@link NaturalTransformation} that reduces an {@link IdentityT} to
     * just its argument {@link MonadRec}.
     *
     * @param <M> the argument {@link MonadRec}
     * @return a natural transformation of {@link IdentityT}
     */
    @SuppressWarnings("unchecked")
    public static <M extends MonadRec<?, M>> RunningIdentityT<M> runningIdentityT() {
        return (RunningIdentityT<M>) INSTANCE;
    }
}
