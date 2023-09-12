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
package org.movealong.sly.lang.nt;

import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ThrowingExceptions implements NaturalTransformation<Try<?>, Identity<?>> {

    private static final ThrowingExceptions INSTANCE = new ThrowingExceptions();

    @Override
    public <A, GA extends Functor<A, Identity<?>>> GA apply(Functor<A, Try<?>> fa) {
        return new Identity<>(fa.<Try<A>>coerce().orThrow()).coerce();
    }

    /**
     * A {@link NaturalTransformation} of {@link Try} which results in the
     * error mode of {@link Try} being thrown, and the success mode of
     * {@link Try} wrapped in {@link Identity}
     *
     * @return a natural transformation of {@link Try}
     */
    public static ThrowingExceptions throwingExceptions() {
        return INSTANCE;
    }
}
