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

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.winterbourne.NaturalTransformation;

/**
 * A <code>HyperFn</code> is an arrow from a {@link Functor} <code>F</code> and
 * carrier <code>A</code> to a {@link Functor} <code>G</code> and carrier
 * <code>B</code>. This is strictly more powerful than a
 * {@link NaturalTransformation}, which has no impact on the carrier type or
 * value of its input.
 * <p>
 * It is similar in nature to a compatible {@link Fn1} with the signature
 * <code>Fn1&lt;Functor&lt;A, F&gt;, Functor&lt;B, G&gt;&gt;</code>.
 * The utility of <code>HyperFn</code> over a compatible {@link Fn1} is
 * contingent on a need to compose them, since the compatible {@link Fn1} form
 * is sufficient for any other purpose.
 * <p>
 * <code>HyperFn</code> is especially useful in cases where the transformation
 * of the {@link Functor} and carrier are linked. When the transformations are
 * not connected, then the operation can be expressed as a composition of an
 * {@link Functor#fmap(Fn1)} operation on the {@link Functor} followed by a
 * {@link NaturalTransformation}, or vice versa.
 *
 * @param <F> the input {@link Functor} type
 * @param <A> the input carrier type
 * @param <G> the output {@link Functor} type
 * @param <B> the output carrier type
 */
public interface HyperFn<F extends Functor<?, F>, A, G extends Functor<?, G>, B> {

    <GB extends Functor<B, G>> GB apply(Functor<A, F> fa);

    /**
     * Left-to-right composition of two compatible <code>HyperFn</code> arrows,
     * yielding a new <code>HyperFn</code>
     *
     * @param hf  the <code>HyperFn</code> to run after this one
     * @param <H> the ultimate output {@link Functor} type
     * @param <C> the ultimate output carrier type
     * @return the composition of the two arrows as a new <code>HyperFn</code>
     */
    default <H extends Functor<?, H>, C> HyperFn<F, A, H, C> andThen(HyperFn<G, B, H, C> hf) {
        return hyperFn(fa -> hf.apply(apply(fa)));
    }

    /**
     * Adapt a compatible {@link Fn1} into a <code>HyperFn</code>
     *
     * @param fn  the function
     * @param <F> the input {@link Functor} type
     * @param <A> the input carrier type
     * @param <G> the output {@link Functor} type
     * @param <B> the output carrier type
     * @return the function adapted as a <code>HyperFn</code>
     */
    static <F extends Functor<?, F>, A, G extends Functor<?, G>, B> HyperFn<F, A, G, B>
    hyperFn(Fn1<Functor<A, F>, ? extends Functor<B, G>> fn) {
        return new HyperFn<>() {
            @Override
            public <GB extends Functor<B, G>> GB apply(Functor<A, F> fa) {
                return fn.apply(fa).coerce();
            }
        };
    }

    /**
     * The identity <code>HyperFn</code>.
     *
     * @param <F> the {@link Functor} type
     * @param <A> the carrier type
     * @return the identity <code>HyperFn</code>
     */
    static <F extends Functor<?, F>, A> HyperFn<F, A, F, A> identity() {
        return hyperFn(Functor::coerce);
    }
}
