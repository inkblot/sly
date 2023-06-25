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
package org.movealong.sly.lang.lens;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.optics.Lens;
import lombok.NoArgsConstructor;
import org.movealong.sly.lang.lens.shoki.MapLens;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.optics.Lens.simpleLens;
import static com.jnape.palatable.lambda.optics.lenses.MaybeLens.unLiftA;
import static com.jnape.palatable.lambda.optics.lenses.MaybeLens.unLiftB;
import static lombok.AccessLevel.PRIVATE;

/**
 * Lenses that provide a default value to <code>Maybe</code>
 */
@NoArgsConstructor(access = PRIVATE)
public final class DefaultValueLens {

    /**
     * Create a {@link Lens.Simple} with "large" type <code>Maybe&lt;A&gt;</code> and "small" type <code>A</code>.
     * Suitable for composition with other lenses that have <code>Maybe&lt;A&gt;</code> as the "small" type such as
     * {@link MapLens}.
     *
     * @param defaultValue the value to use when the <code>Maybe</code> is nothing
     * @param <A>          the value type
     * @return a <code>Lens</code> that eliminates <code>Maybe</code>
     */
    public static <A> Lens.Simple<Maybe<A>, A> defaultValue(A defaultValue) {
        return simpleLens(m -> m.orElse(defaultValue), (m, a) -> just(a));
    }

    /**
     * Unlifts a {@link Lens} with "large" types <code>S</code> and <code>T</code>, and "small" types
     * <code>Maybe&lt;A&gt;</code> and <code>Maybe&lt;B&gt;</code> to produce a <code>Lens&lt;S, T, A, B&gt;</code>.
     * The operation by supplies a default value of type <code>A</code> when the "small" value that it reads is a
     * nothing.
     *
     * @param lens         the lens to modify
     * @param defaultValue the default value to use
     * @param <S>          the type of the "larger" value for reading
     * @param <T>          the type of the "larger" value for putting
     * @param <A>          the type of the "smaller" value that is read
     * @param <B>          the type of the "smaller" update value
     * @return a lens without <code>Maybe</code> in the <code>A</code> and <code>B</code> types
     */
    public static <S, T, A, B> Lens<S, T, A, B> defaultValue(Lens<S, T, Maybe<A>, Maybe<B>> lens, A defaultValue) {
        return unLiftA(unLiftB(lens), defaultValue);
    }
}
