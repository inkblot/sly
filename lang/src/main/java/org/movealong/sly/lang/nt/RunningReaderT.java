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

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class RunningReaderT<R, M extends MonadRec<?, M>> implements NaturalTransformation<ReaderT<R, M, ?>, M> {
    private final R r;

    @Override
    public <A, GA extends Functor<A, M>> GA apply(Functor<A, ReaderT<R, M, ?>> fa) {
        return fa.<ReaderT<R, M, A>>coerce().runReaderT(r).coerce();
    }

    /**
     * A {@link NaturalTransformation} that runs a {@link ReaderT}, yielding
     * the carrier in the argument {@link MonadRec}.
     *
     * @param <R> the reader argument type
     * @param <M> the argument monad type
     * @param r   the reader value
     * @return a transformation of {@link ReaderT}
     */
    public static <R, M extends MonadRec<?, M>> RunningReaderT<R, M> runningReaderT(R r) {
        return new RunningReaderT<>(r);
    }
}
