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
import com.jnape.palatable.winterbourne.NaturalTransformation;
import com.jnape.palatable.winterbourne.StreamT;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * A {@link NaturalTransformation} that alters the argument {@link MonadRec} of
 * {@link StreamT}.
 *
 * @param <M> the argument monad of the input
 * @param <N> the argument monad of the output
 */
@AllArgsConstructor(access = PRIVATE)
public class MappingStreamT<M extends MonadRec<?, M>, N extends MonadRec<?, N>> implements NaturalTransformation<StreamT<M, ?>, StreamT<N, ?>> {

    private final NaturalTransformation<M, N> transformation;

    @Override
    public <A, GA extends Functor<A, StreamT<N, ?>>> GA apply(Functor<A, StreamT<M, ?>> fa) {
        return fa.<StreamT<M, A>>coerce().mapStreamT(transformation).coerce();
    }

    public static <M extends MonadRec<?, M>, N extends MonadRec<?, N>> MappingStreamT<M, N>
    mappingStreamT(NaturalTransformation<M, N> transformation) {
        return new MappingStreamT<>(transformation);
    }

    public static <A, M extends MonadRec<?, M>, N extends MonadRec<?, N>> StreamT<N, A>
    mappingStreamT(NaturalTransformation<M, N> transformation, StreamT<M, A> stream) {
        return mappingStreamT(transformation).apply(stream);
    }
}
