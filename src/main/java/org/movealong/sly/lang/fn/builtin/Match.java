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

package org.movealong.sly.lang.fn.builtin;

import com.jnape.palatable.lambda.adt.coproduct.CoProduct2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * Given two <code>Fn1</code>s from types <code>A</code> and <code>B</code>, both returning type <code>R</code>, and
 * a <code>CoProduct2&lt;A, B&gt;</code>, return <code>R</code>. <code>Match</code> is a detached implementation of
 * the {@link CoProduct2#match(Fn1, Fn1)} instance method providing type-safe convergence over the two possible types
 * of a {@link CoProduct2}.
 *
 * @param <A>   the <code>CoProduct2</code>'s first possible type
 * @param <B>   the <code>CoProduct2</code>'s second possible type
 * @param <CP>  the type of the <code>CoProduct2</code> to which the function applies
 * @param <R>   result type
 */
@NoArgsConstructor(access = PRIVATE)
public final class Match<A, B, R, CP extends CoProduct2<A, B, CP>> implements
        Fn3<Fn1<? super A, ? extends R>, Fn1<? super B, ? extends R>, CP, R> {

    public static final Match<?, ?, ?, ?> INSTANCE = new Match<>();

    @Override
    public R checkedApply(Fn1<? super A, ? extends R> aFn, Fn1<? super B, ? extends R> bFn, CP cp) {
        return cp.match(aFn, bFn);
    }

    @SuppressWarnings("unchecked")
    public static <A, B, R, CP extends CoProduct2<A, B, CP>>
    Fn3<Fn1<? super A, ? extends R>, Fn1<? super B, ? extends R>, CP, R> match() {
        return (Fn3<Fn1<? super A, ? extends R>, Fn1<? super B, ? extends R>, CP, R>) INSTANCE;
    }

    public static <A, B, R, CP extends CoProduct2<A, B, CP>>
    Fn2<Fn1<? super B, ? extends R>, CP, R> match(Fn1<? super A, ? extends R> aFn) {
        return Match.<A, B, R, CP>match().apply(aFn);
    }

    public static <A, B, R, CP extends CoProduct2<A, B, CP>>
    Fn1<CP, R> match(Fn1<? super A, ? extends R> aFn, Fn1<? super B, ? extends R> bFn) {
        return Match.<A, B, R, CP>match(aFn).apply(bFn);
    }

    public static <A, B, R, CP extends CoProduct2<A, B, CP>>
    R match(Fn1<? super A, ? extends R> aFn, Fn1<? super B, ? extends R> bFn, CP cp) {
        return Match.<A, B, R, CP>match(aFn, bFn).apply(cp);
    }
}
