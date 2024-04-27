/*
 * Copyright (c) 2024 Nate Riffe
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
import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.traversable.Traversable;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public class Traverse<A, B, Trav extends Traversable<?, Trav>, TravB extends Traversable<B, Trav>, App extends Applicative<?, App>, AppTrav extends Applicative<TravB, App>>
    implements HyperFn<Trav, A, App, TravB> {

    private final Fn1<? super A, ? extends Applicative<B, App>> fn;
    private final Fn1<? super TravB, ? extends AppTrav>         pure;

    @Override
    public <GB extends Functor<TravB, App>> GB apply(Functor<A, Trav> fa) {
        return fa.<Traversable<A, Trav>>coerce().traverse(fn, pure).coerce();
    }

    /**
     * A {@link HyperFn} that implements a standalone version of
     * {@link Traversable#traverse}.
     *
     * @param <Trav>    the {@link Traversable} type
     * @param <A>       the input carrier type
     * @param <B>       the output carrier type
     * @param <TravB>   the output carrier wrapped in the traversable
     * @param <App>     the {@link Applicative} type
     * @param <AppTrav> the output carrier wrapped in the traversable, then the
     *                  applicative
     * @param fn        the flatmapping function
     * @param pure      a pure function for the applicative type
     * @return A <code>HyperFn</code> that encapsulates
     * {@link Traversable#traverse}
     */
    public static <A, B, Trav extends Traversable<?, Trav>, TravB extends Traversable<B, Trav>, App extends Applicative<?, App>, AppTrav extends Applicative<TravB, App>>
    Traverse<A, B, Trav, TravB, App, AppTrav> traverse(Fn1<? super A, ? extends Applicative<B, App>> fn,
                                                       Fn1<? super TravB, ? extends AppTrav> pure) {
        return new Traverse<>(fn, pure);
    }
}
