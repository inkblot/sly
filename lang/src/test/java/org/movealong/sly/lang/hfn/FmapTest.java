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

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.hfn.Fmap.fmap;

class FmapTest {

    @Test
    void presentMaybeFmap() {
        Maybe<String>                   subject = just("test");
        Fn1<String, Integer>            fn      = String::length;
        Fmap<Maybe<?>, String, Integer> sut     = fmap(fn);

        assertEquals(subject.fmap(fn), sut.<Maybe<Integer>>apply(subject));
    }

    @Test
    void absentMaybeFmap() {
        Maybe<String>                   subject = nothing();
        Fn1<String, Integer>            fn      = String::length;
        Fmap<Maybe<?>, String, Integer> sut     = fmap(fn);

        assertEquals(subject.fmap(fn), sut.<Maybe<Integer>>apply(subject));
    }

    @Test
    void rightEitherFmap() {
        Either<Integer, String>                   subject = right("test");
        Fn1<String, Integer>                      fn      = String::length;
        Fmap<Either<Integer, ?>, String, Integer> sut     = fmap(fn);

        assertEquals(subject.fmap(fn), sut.<Either<Integer, Integer>>apply(subject));
    }

    @Test
    void leftEitherFmap() {
        Either<Integer, String>                   subject = left(-1);
        Fn1<String, Integer>                      fn      = String::length;
        Fmap<Either<Integer, ?>, String, Integer> sut     = fmap(fn);

        assertEquals(subject.fmap(fn), sut.<Either<Integer, Integer>>apply(subject));
    }
}