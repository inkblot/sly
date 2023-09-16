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

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import com.jnape.palatable.lambda.monad.transformer.builtin.IterateT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static com.jnape.palatable.lambda.monad.transformer.builtin.IterateT.unfold;
import static lombok.AccessLevel.PRIVATE;
import static org.movealong.sly.lang.fn.builtin.Traverse.traverse;

@AllArgsConstructor(access = PRIVATE)
public final class MappingIterateT<M extends MonadRec<?, M>, N extends MonadRec<?, N>> implements
    NaturalTransformation<IterateT<M, ?>, IterateT<N, ?>> {
    private final NaturalTransformation<M, N> transformation;

    @Override
    public <A, GA extends Functor<A, IterateT<N, ?>>> GA apply(Functor<A, IterateT<M, ?>> fa) {
        MonadRec<Maybe<Tuple2<A, IterateT<M, A>>>, N> nb = transformation.apply(fa.<IterateT<M, A>>coerce().runIterateT());
        return unfold(
            traverse(traverse((IterateT<M, A> it) -> transformation.apply(it.runIterateT()), nb::pure), nb::pure),
            nb).coerce();
    }

    /**
     * A {@link NaturalTransformation} that changes the argument
     * {@link MonadRec} of a {@link IterateT}
     *
     * @param <M>            the input argument {@link MonadRec}
     * @param <N>            the output argument {@link MonadRec}
     * @param transformation a natural transformation from
     *                       <code>&lt;M&gt;</code> to
     *                       <code>&lt;N&gt;</code>
     * @return a natural transformation of {@link IterateT}
     */
    public static <M extends MonadRec<?, M>, N extends MonadRec<?, N>> MappingIterateT<M, N>
    mappingIterateT(NaturalTransformation<M, N> transformation) {
        return new MappingIterateT<>(transformation);
    }
}
