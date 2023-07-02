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
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import lombok.NoArgsConstructor;

import static com.jnape.palatable.lambda.adt.Try.trying;
import static lombok.AccessLevel.PRIVATE;

/**
 * A {@link NaturalTransformation} that runs an {@link IO} safely,
 * capturing the result of performing the operation as a {@link Try}.
 * An <code>IO</code> that yields a result will produce a <code>Try</code>
 * in the success state, and an <code>IO</code> that throws an exception
 * will result in a <code>Try</code> in the failure state.
 */
@NoArgsConstructor(access = PRIVATE)
public final class PerformingIO implements NaturalTransformation<IO<?>, Try<?>> {

    private static final PerformingIO INSTANCE = new PerformingIO();

    @Override
    public <A, GA extends Functor<A, Try<?>>> GA apply(Functor<A, IO<?>> fa) {
        return trying(fa.<IO<A>>coerce()::unsafePerformIO).coerce();
    }

    public static NaturalTransformation<IO<?>, Try<?>> performingIO() {
        return INSTANCE;
    }

    public static <A> Try<A> performingIO(IO<A> io) {
        return performingIO().apply(io);
    }
}
