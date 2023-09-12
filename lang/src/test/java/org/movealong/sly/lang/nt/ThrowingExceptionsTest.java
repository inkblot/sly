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
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Try.failure;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.io.IO.throwing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.movealong.sly.lang.nt.PerformingIO.performingIO;
import static org.movealong.sly.lang.nt.ThrowingExceptions.throwingExceptions;

class ThrowingExceptionsTest {

    @Test
    void whenItFailsItThrows() {
        Try<String>        subject = failure(new RuntimeException("boom"));
        ThrowingExceptions sut     = throwingExceptions();
        assertThrows(RuntimeException.class,
                     () -> sut.apply(subject),
                     "boom");
    }

    @Test
    void resultsOnSuccess() {
        Try<String>        subject = Try.success("yay");
        ThrowingExceptions sut     = throwingExceptions();
        assertThat(sut.<String, Identity<String>>apply(subject).runIdentity(),
                   equalTo("yay"));
    }

    @Test
    void composed() {
        NaturalTransformation<IO<?>, Identity<?>> sut =
            performingIO().andThen(throwingExceptions());

        assertThat(sut.<String, Identity<String>>apply(io("yay")).runIdentity(),
                   equalTo("yay"));
        assertThrows(RuntimeException.class,
                     () -> sut.apply(throwing(new RuntimeException("boom"))),
                     "boom");
    }
}