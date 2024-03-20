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

import com.jnape.palatable.lambda.functor.Functor;

/**
 * An application
 *
 * @param <A>  the carrier type of the application return
 * @param <F>  the {@link Functor} type of the application return
 * @param <FA> the application return type
 */
@FunctionalInterface
public interface Application<A, F extends Functor<?, F>, FA extends Functor<A, F>> {

    /**
     * Represents the main body of an application.
     *
     * @return the result of running the application
     */
    FA run();

    /**
     * A convenience method for correctly typing a lambda as an
     * {@link Application}.
     *
     * @param <A>         the carrier type of the application return
     * @param <F>         the {@link Functor} type of the application return
     * @param <FA>        the application return type
     * @param application the return value
     * @return <code>application</code> is returned as-is
     */
    static <A, F extends Functor<?, F>, FA extends Functor<A, F>>
    Application<A, F, FA> application(Application<A, F, FA> application) {
        return application;
    }
}
