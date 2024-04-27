/*
 * Copyright (c) 2023-2024 Nate Riffe
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

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.recursion.RecursiveResult;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.monad.MonadRec;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class TrampolineM<M extends MonadRec<?, M>, A, B> implements HyperFn<M, A, M, B> {
    private final Fn1<? super A, ? extends MonadRec<RecursiveResult<A, B>, M>> fn;

    @Override
    public <GB extends Functor<B, M>> GB apply(Functor<A, M> fa) {
        return fa.<MonadRec<A, M>>coerce().trampolineM(fn).coerce();
    }

    /**
     * A {@link HyperFn} that implements a standalone version of
     * {@link MonadRec#trampolineM}.
     *
     * @param <M> the {@link MonadRec} type
     * @param <A> the input carrier type
     * @param <B> the output carrier type
     * @param fn  the trampoline function
     * @return A <code>HyperFn</code> that encapsulates
     * {@link MonadRec#trampolineM}
     */
    public static <M extends MonadRec<?, M>, A, B>
    TrampolineM<M, A, B> trampolineM(Fn1<? super A, ? extends MonadRec<RecursiveResult<A, B>, M>> fn) {
        return new TrampolineM<>(fn);
    }
}
