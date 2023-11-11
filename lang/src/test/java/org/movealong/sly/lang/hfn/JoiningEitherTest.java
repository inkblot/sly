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
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.monad.transformer.builtin.EitherT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.monad.transformer.builtin.EitherT.eitherT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

class JoiningEitherTest {
    @Test
    void joiningRight() {
        EitherT<Maybe<?>, String, Either<String, Integer>> input = eitherT(just(right(right(1))));
        assertThat(JoiningEither.<Maybe<?>, String, Integer>joiningEither()
                                .<EitherT<Maybe<?>, String, Integer>>apply(input),
                   equalTo(eitherT(just(right(1)))));
    }

    @Test
    void joiningLeft() {
        EitherT<Maybe<?>, String, Either<String, Integer>> input = eitherT(just(right(left("no"))));
        assertThat(JoiningEither.<Maybe<?>, String, Integer>joiningEither()
                                .<EitherT<Maybe<?>, String, Integer>>apply(input),
                   equalTo(eitherT(just(left("no")))));
    }
}