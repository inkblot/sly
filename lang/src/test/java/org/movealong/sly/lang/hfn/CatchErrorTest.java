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
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.monad.Monad;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.movealong.sly.lang.hfn.CatchError.catchError;

class CatchErrorTest {
    @Test
    void recoveringLeftEither() {
        Fn1<Integer, Monad<String, Either<Integer, ?>>> fn      = constantly(right("xyzzy"));
        Either<Integer, String>                         subject = left(5);
        CatchError<Either<Integer, ?>, Integer, String> sut     = catchError(fn);

        assertEquals(Either.<Integer, String>right("xyzzy"), sut.apply(subject));
        assertEquals(subject.catchError(fn), sut.apply(subject));
    }

    @Test
    void abandoningLeftEither() {
        Fn1<Integer, Monad<String, Either<Integer, ?>>> fn      = Either::left;
        Either<Integer, String>                         subject = left(5);
        CatchError<Either<Integer, ?>, Integer, String> sut     = catchError(fn);

        assertEquals(Either.<Integer, String>left(5), sut.apply(subject));
        assertEquals(subject.catchError(fn), sut.apply(subject));
    }
}