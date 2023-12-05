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

import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT;

import static org.movealong.sly.lang.hfn.HyperFn.hyperFn;

public class RunningIdentityT {
    /**
     * An <code>Interpreter</code> that runs an {@link IdentityT}, producing
     * an instance of its argument {@link MonadRec} with an {@link Identity} as
     * the carrier.
     *
     * @param <M> the argument {@link MonadRec}
     * @param <A> the carrier type
     * @return an interpreter that runs {@link IdentityT}
     */
    public static <M extends MonadRec<?, M>, A> HyperFn<IdentityT<M, ?>, A, M, Identity<A>> runningIdentityT() {
        return hyperFn(fa -> fa.<IdentityT<M, A>>coerce().runIdentityT());
    }
}
