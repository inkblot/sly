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

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT;
import com.jnape.palatable.winterbourne.NaturalTransformation;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Size.size;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Eq.eq;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Filter.filter;
import static com.jnape.palatable.lambda.functions.builtin.fn2.GT.gt;
import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.readerT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.nt.RunningReaderT.runningReaderT;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static org.movealong.sly.model.Stringy.stringy;
import static testsupport.matchers.EitherMatcher.isRightThat;

class RunningReaderTTest {
    @Test
    void runningReaderTMaybe() {
        ReaderT<String, Maybe<?>, Integer> input =
            readerT(s -> just(size(filter(eq('i'), stringy(s))).intValue()).filter(gt(0)));
        NaturalTransformation<ReaderT<String, Maybe<?>, ?>, Maybe<?>> sut = runningReaderT("junit");

        assertThat(sut.apply(input),
                   isJustOf(1));
    }

    @Test
    void runningReaderTEither() {
        ReaderT<String, Either<String, ?>, Integer> input =
            readerT(s -> Either.<String, Integer>right(size(filter(eq('i'), stringy(s))).intValue())
                               .filter(gt(0), () -> "No 'i's"));
        NaturalTransformation<ReaderT<String, Either<String, ?>, ?>, Either<String, ?>> sut = runningReaderT("junit");

        assertThat(sut.<Integer, Either<String, Integer>>apply(input),
                   isRightThat(equalTo(1)));
    }
}