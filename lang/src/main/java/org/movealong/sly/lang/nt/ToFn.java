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

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class ToFn<A, F extends Functor<?, F>, FA extends Functor<A, F>, G extends Functor<?, G>, GA extends Functor<A, G>>
    implements Fn1<FA, GA> {

    private final NaturalTransformation<F, G> transformation;

    @Override
    public GA checkedApply(FA fa) throws Throwable {
        return transformation.apply(fa);
    }

    /**
     * Convert a {@link NaturalTransformation} into an {@link Fn1} by fixing
     * the carrier to a specific type.
     *
     * @param <A>            the carrier type
     * @param <F>            the input {@link Functor} type
     * @param <FA>           the combined input type
     * @param <G>            the output {@link Functor} type
     * @param <GA>           the combined output type
     * @param transformation the {@link NaturalTransformation} to convert
     * @return an function from <code>FA</code> to <code>GA</code>
     */
    public static <A, F extends Functor<?, F>, FA extends Functor<A, F>, G extends Functor<?, G>, GA extends Functor<A, G>>
    Fn1<FA, GA> toFn(NaturalTransformation<F, G> transformation) {
        return new ToFn<>(transformation);
    }
}
