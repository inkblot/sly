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
import com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT.identityT;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class MappingIdentityT<M extends MonadRec<?, M>, N extends MonadRec<?, N>> implements
    NaturalTransformation<IdentityT<M, ?>, IdentityT<N, ?>> {

    private final NaturalTransformation<M, N> transformation;

    @Override
    public <A, GA extends Functor<A, IdentityT<N, ?>>> GA apply(Functor<A, IdentityT<M, ?>> fa) {
        return identityT(transformation.apply(fa.<IdentityT<M, A>>coerce().runIdentityT())).coerce();
    }

    /**
     * A {@link NaturalTransformation} that changes the argument {@link MonadRec}
     * of an {@link IdentityT}
     *
     * @param <M>            the input argument {@link MonadRec}
     * @param <N>            the output argument {@link MonadRec}
     * @param transformation a natural transformation from
     *                       <code>&lt;M&gt;</code> to <code>&lt;N&gt;</code>
     * @return a natural transformation of {@link IdentityT}
     */
    public static <M extends MonadRec<?, M>, N extends MonadRec<?, N>> MappingIdentityT<M, N>
    mappingIdentityT(NaturalTransformation<M, N> transformation) {
        return new MappingIdentityT<>(transformation);
    }
}
