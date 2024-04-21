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

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functions.Fn0;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.winterbourne.NaturalTransformation;

import static com.jnape.palatable.lambda.functions.Fn0.fn0;

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
 * @see Starter
 */
public interface Stopper<M extends Monad<?, M>> {

    /**
     * Stops an application component, returning a {@link Unit} in a monadic
     * context. It is expected that this method will be invoked, and the
     * monadic effect of the returned value run at the appropriate time.
     * Failure to do so should be considered a resource leak.
     *
     * @param <MU> the {@link Unit} wrapped in the monad type
     * @return a {@link Unit} in a monadic context
     */
    <MU extends Monad<Unit, M>> MU stop();

    /**
     * Creates a new {@link Stopper} that will stop <code>this</code>
     * and then stop <code>next</code>. It should not normally be necessary
     * for an application to invoke this method. The effect of nesting the
     * lifecycles of two application components together is accomplished by
     * calling the <code>andThen</code> method of {@link Starter}
     *
     * @param next a {@link Stopper} for another application component
     * @return A {@link Stopper} that combines two others
     */
    default Stopper<M> andThen(Stopper<M> next) {
        return stopper(() -> stop().flatMap(fn0(next::stop)));
    }

    /**
     * Transforms the {@link Monad} of the {@link Stopper} through the
     * application of a {@link NaturalTransformation}.
     *
     * @param <N>   the new {@link Monad} type
     * @param trans the {@link NaturalTransformation} to apply
     * @return A {@link Stopper} with parametric type <code>N</code>
     */
    default <N extends Monad<?, N>>
    Stopper<N> mapStopper(NaturalTransformation<M, N> trans) {
        return stopper(() -> trans.apply(stop()));
    }

    /**
     * A convenience method for creating a {@link Stopper} from a lambda
     * expression.
     *
     * @param <M>     the {@link Monad} type
     * @param stopper a {@link Fn0} that implements <code>stop</code>
     * @return A {@link Stopper}
     */
    static <M extends Monad<?, M>>
    Stopper<M> stopper(Fn0<? extends Monad<Unit, M>> stopper) {
        return new Stopper<>() {
            @Override
            public <MU extends Monad<Unit, M>> MU stop() {
                return stopper.apply().coerce();
            }
        };
    }
}
