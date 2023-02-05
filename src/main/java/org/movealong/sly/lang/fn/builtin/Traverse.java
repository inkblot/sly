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

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.traversable.Traversable;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * <code>Traverse</code> transforms the carrier type <code>A</code> of a <code>Traversable</code> functor
 * <code>Trav</code> into type <code>B</code> wrapped in the <code>Applicative</code> type <code>App</code>,
 * and then inverts the nesting order of two functors <code>App</code> and <code>Trav</code>. The result is
 * a value of type <code>B</code> wrapped in the <code>Traversable</code> functor <code>Trav</code> wrapped
 * in the <code>Applicative</code> functor <code>App</code>.
 *
 * @param <A>       the carrier type of the input functor
 * @param <B>       the output carrier type of the nested functors
 * @param <Trav>    the <code>Traversable</code> type
 * @param <TravA>   type <code>Trav</code> with a carrier type of <code>A</code>
 * @param <TravB>   type <code>Trav</code> with a carrier type of <code>B</code>
 * @param <App>     the <code>Applicative</code> type
 * @param <AppTrav> the <code>Applicative</code> type with a carrier of <code>TravB</code>
 */
@NoArgsConstructor(access = PRIVATE)
public final class Traverse<A, B, Trav extends Traversable<?, Trav>, TravA extends Traversable<A, Trav>, TravB extends Traversable<B, Trav>, App extends Applicative<?, App>, AppTrav extends Applicative<TravB, App>> implements
        Fn3<Fn1<? super A, ? extends Applicative<B, App>>, Fn1<? super TravB, ? extends AppTrav>, TravA, AppTrav> {

    @Override
    public AppTrav checkedApply(Fn1<? super A, ? extends Applicative<B, App>> fn, Fn1<? super TravB, ? extends AppTrav> pure, TravA travA) {
        return travA.traverse(fn, pure);
    }

    public static <A, B, T extends Traversable<?, T>, TravA extends Traversable<A, T>, TravB extends Traversable<B, T>, App extends Applicative<?, App>, AppTrav extends Applicative<TravB, App>>
    Fn3<Fn1<? super A, ? extends Applicative<B, App>>, Fn1<? super TravB, ? extends AppTrav>, TravA, AppTrav> traverse() {
        return new Traverse<>();
    }

    public static <A, B, T extends Traversable<?, T>, TravA extends Traversable<A, T>, TravB extends Traversable<B, T>, App extends Applicative<?, App>, AppTrav extends Applicative<TravB, App>>
    Fn2<Fn1<? super TravB, ? extends AppTrav>, TravA, AppTrav> traverse(Fn1<? super A, ? extends Applicative<B, App>> fn) {
        return Traverse.<A, B, T, TravA, TravB, App, AppTrav>traverse().apply(fn);
    }

    public static <A, B, T extends Traversable<?, T>, TravA extends Traversable<A, T>, TravB extends Traversable<B, T>, App extends Applicative<?, App>, AppTrav extends Applicative<TravB, App>>
    Fn1<TravA, AppTrav> traverse(Fn1<? super A, ? extends Applicative<B, App>> fn, Fn1<? super TravB, ? extends AppTrav> pure) {
        return Traverse.<A, B, T, TravA, TravB, App, AppTrav>traverse(fn).apply(pure);
    }

    public static <A, B, T extends Traversable<?, T>, TravA extends Traversable<A, T>, TravB extends Traversable<B, T>, App extends Applicative<?, App>, AppTrav extends Applicative<TravB, App>>
    AppTrav traverse(Fn1<? super A, ? extends Applicative<B, App>> fn, Fn1<? super TravB, ? extends AppTrav> pure, TravA travA) {
        return Traverse.<A, B, T, TravA, TravB, App, AppTrav>traverse(fn, pure).apply(travA);
    }
}
