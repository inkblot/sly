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
package org.movealong.sly.app;

import com.jnape.palatable.lambda.functions.Fn0;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.winterbourne.NaturalTransformation;

/**
 * {@link Starter} and {@link Stopper} form a mechanism for controlling an
 * application component that requires set up and tear down. The {@link App}
 * class provides the <code>start</code> method which properly invokes this
 * functionality for components whose lifecycle runs matches the lifecycle of
 * the application. However, it is also possible to use {@link Starter} and
 * {@link Stopper} at any time in an application's life to control the
 * lifecycle of a component, for example to control one that is request-
 * scoped.
 *
 * @param <M> the monad type
 * @see Stopper
 */
public interface Starter<M extends Monad<?, M>> {

    /**
     * Starts an application component, returning a {@link Stopper} in a
     * monadic context. The returned {@link Stopper} will stop the application
     * component when invoked. It is expected that the returned {@link Stopper}
     * will be invoked at the  appropriate time, and failure to do so should be
     * considered a resource leak.
     *
     * @param <MS> the {@link Stopper} wrapped in the monad type
     * @return A stopper for the application component that has been started
     */
    <MS extends Monad<Stopper<M>, M>> MS start();

    /**
     * Creates a new {@link Starter} will compose <code>this</code> and
     * <code>next</code> so that <code>this</code> is started followed by
     * <code>next</code>. The {@link Stopper} that this composed
     * {@link Starter} returns works in the reverse order, stopping the
     * component managed by <code>next</code> first and then the component
     * managed by <code>this</code>. This has the effect of <i>nesting</i>
     * another application component's lifecycle within the boundaries of this
     * one.
     *
     * @param next a {@link Starter} for another application component
     * @return A {@link Starter} that combines two others
     */
    default Starter<M> andThen(Starter<M> next) {
        return starter(() -> start()
            .flatMap(outer -> next
                .start().fmap(inner -> inner.andThen(outer))));
    }

    /**
     * Transforms the {@link Monad} of the {@link Starter} through the
     * application of a {@link NaturalTransformation}.
     *
     * @param <N>   the new {@link Monad} type
     * @param trans the {@link NaturalTransformation} to apply
     * @return A {@link Starter} with parametric type <code>N</code>
     */
    default <N extends Monad<?, N>>
    Starter<N> mapStarter(NaturalTransformation<M, N> trans) {
        return starter(() -> trans
            .apply(start().fmap(stopper -> stopper
                .mapStopper(trans))));
    }

    /**
     * A convenience method for creating a {@link Starter} from a lambda
     * expression.
     *
     * @param <M>     the {@link Monad} type
     * @param starter an {@link Fn0} that implements the <code>start</code>
     *                method
     * @return A {@link Starter}
     */
    static <M extends Monad<?, M>>
    Starter<M> starter(Fn0<? extends Monad<Stopper<M>, M>> starter) {
        return new Starter<>() {
            @Override
            public <MS extends Monad<Stopper<M>, M>> MS start() {
                return starter.apply().coerce();
            }
        };
    }
}
