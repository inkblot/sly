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
package org.movealong.sly.model;

import com.jnape.palatable.lambda.adt.product.Product2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.traversable.Traversable;
import lombok.AllArgsConstructor;
import lombok.Value;

import static com.jnape.palatable.lambda.functions.builtin.fn2.$.$;
import static lombok.AccessLevel.PRIVATE;

/**
 * A {@link Functor} that connects a {@link Name} to another value.
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class Named<A> implements
    Functor<A, Named<?>>,
    Traversable<A, Named<?>>,
    Product2<Name, A>,
    WrappedValue<A> {

    Name name;
    A    value;

    @Override
    public <B> Named<B> fmap(Fn1<? super A, ? extends B> fn) {
        return named(name, $(fn, value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <B, App extends Applicative<?, App>, TravB extends Traversable<B, Named<?>>, AppTrav extends Applicative<TravB, App>>
    AppTrav traverse(Fn1<? super A, ? extends Applicative<B, App>> fn, Fn1<? super TravB, ? extends AppTrav> pure) {
        return (AppTrav) $(fn, value).fmap(named(name));
    }

    @Override
    public Name _1() {
        return name;
    }

    @Override
    public A _2() {
        return value;
    }

    public static <A> Named<A> named(Name name, A value) {
        return new Named<>(name, value);
    }

    public static <A> Fn1<A, Named<A>> named(Name name) {
        return a -> named(name, a);
    }
}
