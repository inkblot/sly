/*
 * Copyright (c) 2023-2025 Nate Riffe
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
 * A {@link Functor} that connects a {@link Label} to another value.
 */
@Value
@AllArgsConstructor(access = PRIVATE)
public class Labeled<A> implements
    Functor<A, Labeled<?>>,
    Traversable<A, Labeled<?>>,
    Product2<Label, A>,
    WrappedValue<A> {

    Label label;
    A     value;

    @Override
    public <B> Labeled<B> fmap(Fn1<? super A, ? extends B> fn) {
        return labeled(label, $(fn, value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <B, App extends Applicative<?, App>, TravB extends Traversable<B, Labeled<?>>, AppTrav extends Applicative<TravB, App>>
    AppTrav traverse(Fn1<? super A, ? extends Applicative<B, App>> fn, Fn1<? super TravB, ? extends AppTrav> pure) {
        return (AppTrav) $(fn, value).fmap(labeled(label));
    }

    @Override
    public Label _1() {
        return label;
    }

    @Override
    public A _2() {
        return value;
    }

    /**
     * Creates a new {@link Labeled} instance with the given label and value.
     *
     * @param <A>   the type of the value
     * @param label the label
     * @param value the value
     * @return a new {@link Labeled} instance
     */
    public static <A> Labeled<A> labeled(Label label, A value) {
        return new Labeled<>(label, value);
    }

    /**
     * Returns a function that creates a {@link Labeled} instance with the given label
     * and the value provided when the function is applied.
     *
     * @param <A>   the type of the value
     * @param label the label
     * @return a function that creates a {@link Labeled} instance
     */
    public static <A> Fn1<A, Labeled<A>> labeled(Label label) {
        return a -> labeled(label, a);
    }
}
