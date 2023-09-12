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
import com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.readerT;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class MappingReaderT<R, M extends MonadRec<?, M>, N extends MonadRec<?, N>> implements
    NaturalTransformation<ReaderT<R, M, ?>, ReaderT<R, N, ?>> {
    private final NaturalTransformation<M, N> transformation;

    @Override
    public <A, GA extends Functor<A, ReaderT<R, N, ?>>> GA apply(Functor<A, ReaderT<R, M, ?>> fa) {
        return readerT((R r) -> transformation.apply(fa.<ReaderT<R, M, A>>coerce().runReaderT(r))).coerce();
    }

    /**
     * An {@link NaturalTransformation} of {@link ReaderT} that changes
     * the argument {@link MonadRec}.
     *
     * @param <R>            the read type
     * @param <M>            the input argument {@link MonadRec}
     * @param <N>            the output argument {@link MonadRec}
     * @param transformation an interpreter from <code>&lt;M&gt;</code>
     *                       to <code>&lt;N&gt;</code>
     * @return a transformation of {@link ReaderT}
     */
    public static <R, M extends MonadRec<?, M>, N extends MonadRec<?, N>> MappingReaderT<R, M, N>
    mappingReaderT(NaturalTransformation<M, N> transformation) {
        return new MappingReaderT<>(transformation);
    }
}