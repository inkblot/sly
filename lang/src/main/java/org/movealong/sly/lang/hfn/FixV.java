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

import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class FixV<A, F extends Functor<?, F>, G extends Functor<?, G>> implements HyperFn<F, A, G, A> {
    private final NaturalTransformation<F, G> transFG;

    @Override
    public <GB extends Functor<A, G>> GB apply(Functor<A, F> fa) {
        return transFG.apply(fa);
    }

    /**
     * Convert a <code>NaturalTransformation</code> to a <code>HyperFn</code>
     * by fixing the value of the {@link NaturalTransformation#apply(Functor)}
     * method's <code>A</code> type parameter at the type level. This enables
     * composition of the <code>NaturalTransformation</code> with
     * <code>HyperFn</code>s.
     */
    public static <A, F extends Functor<?, F>, G extends Functor<?, G>>
    HyperFn<F, A, G, A> fixV(NaturalTransformation<F, G> transFG) {
        return new FixV<>(transFG);
    }
}
