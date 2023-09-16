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

import com.jnape.palatable.lambda.functions.specialized.Pure;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class FromPure<F extends Functor<?, F>> implements NaturalTransformation<Identity<?>, F> {
    private final Pure<F> pureF;

    @Override
    public <A, GA extends Functor<A, F>> GA apply(Functor<A, Identity<?>> fa) {
        return pureF.apply(fa.<Identity<A>>coerce().runIdentity());
    }

    /**
     * Constructs a {@link NaturalTransformation} of that transforms
     * {@link Identity} into an arbitrary {@link Functor}, given a {@link Pure}
     * for the functor.
     *
     * @param <F>   the {@link Functor} type
     * @param pureF the pure function
     * @return a transformation of {@link Identity}
     */
    public static <F extends Functor<?, F>> FromPure<F> fromPure(Pure<F> pureF) {
        return new FromPure<>(pureF);
    }
}
