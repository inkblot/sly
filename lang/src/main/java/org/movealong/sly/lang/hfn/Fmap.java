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
import com.jnape.palatable.lambda.functor.Functor;
import lombok.AllArgsConstructor;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Upcast.upcast;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class Fmap<F extends Functor<?, F>, A, B> implements HyperFn<F, A, F, B> {
    private final Fn1<? super A, ? extends B> fn;

    @Override
    public <GB extends Functor<B, F>> GB apply(Functor<A, F> fa) {
        return fa.<B>fmap(fn).coerce();
    }

    /**
     * A {@link HyperFn} that implements a standalone version of
     * {@link Functor#fmap}.
     *
     * @param <F> the {@link Functor} type
     * @param <A> the input carrier type
     * @param <B> the output carrier type
     * @param fn  the mapping function
     * @return A <code>HyperFn</code> that encapsulates {@link Functor#fmap}
     */
    public static <F extends Functor<?, F>, A, B>
    Fmap<F, A, B> fmap(Fn1<? super A, ? extends B> fn) {
        return new Fmap<>(fn.fmap(upcast()));
    }
}
