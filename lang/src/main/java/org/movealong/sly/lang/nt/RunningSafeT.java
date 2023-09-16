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
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.SafeT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RunningSafeT<M extends MonadRec<?, M>> implements NaturalTransformation<SafeT<M, ?>, M> {
    @Override
    public <A, GA extends Functor<A, M>> GA apply(Functor<A, SafeT<M, ?>> fa) {
        return fa.<SafeT<M, A>>coerce().runSafeT().coerce();
    }

    /**
     * A {@link NaturalTransformation} that runs a {@link SafeT}, yielding
     * the carrier in the argument {@link MonadRec}.
     *
     * @param <M> the argument {@link MonadRec} type
     * @return a natural transformation of {@link SafeT}
     */
    public static <M extends MonadRec<?, M>> RunningSafeT<M> runningSafeT() {
        return new RunningSafeT<>();
    }
}
