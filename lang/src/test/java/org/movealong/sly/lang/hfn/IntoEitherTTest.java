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
package org.movealong.sly.lang.hfn;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.monad.transformer.builtin.EitherT.eitherT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.hfn.IntoEitherT.intoEitherT;
import static org.movealong.sly.lang.hfn.RunningEitherT.runningEitherT;

class IntoEitherTTest {
    @Test
    void rightInversion() {
        Identity<Either<Integer, String>>            subject = new Identity<>(right("good"));
        IntoEitherT<Identity<?>, Integer, String>    sut     = intoEitherT();
        RunningEitherT<Identity<?>, Integer, String> invert  = runningEitherT();

        assertEquals(subject, sut.andThen(invert).apply(subject));
        assertEquals(eitherT(subject), invert.andThen(sut).apply(eitherT(subject)));
    }

    @Test
    void leftInversion() {
        Identity<Either<Integer, String>>            subject = new Identity<>(left(7));
        IntoEitherT<Identity<?>, Integer, String>    sut     = intoEitherT();
        RunningEitherT<Identity<?>, Integer, String> invert  = runningEitherT();

        assertEquals(subject, sut.andThen(invert).apply(subject));
        assertEquals(eitherT(subject), invert.andThen(sut).apply(eitherT(subject)));
    }
}