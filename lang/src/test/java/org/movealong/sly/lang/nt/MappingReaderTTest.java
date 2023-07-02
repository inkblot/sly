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
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.EitherT;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Either.left;
import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.io.IO.pureIO;
import static com.jnape.palatable.lambda.monad.transformer.builtin.ReaderT.readerT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.nt.TransformingEither.transformingEither;
import static org.movealong.sly.lang.nt.TransformingMaybe.transformingMaybe;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static testsupport.matchers.EitherMatcher.isLeftOf;
import static testsupport.matchers.EitherMatcher.isRightOf;
import static testsupport.matchers.IOMatcher.yieldsValue;

class MappingReaderTTest {
    @Test
    void mappingReaderTMaybe() {
        ReaderT<String, MaybeT<IO<?>, ?>, String> fn =
            MappingReaderT
                .<String, Maybe<?>, MaybeT<IO<?>, ?>>mappingReaderT(transformingMaybe(pureIO()))
                .apply(readerT(s -> s.contains("n") ? just(s) : nothing()));

        assertThat(fn.<MaybeT<IO<?>, String>>runReaderT("junit")
                     .runMaybeT(),
                   yieldsValue(isJustOf("junit")));
        assertThat(fn.<MaybeT<IO<?>, String>>runReaderT("yes")
                     .<IO<Maybe<String>>>runMaybeT(),
                   yieldsValue(equalTo(nothing())));
    }

    @Test
    void mappingReaderTEither() {
        ReaderT<String, EitherT<IO<?>, Integer, ?>, String> fn =
            MappingReaderT
                .<String, Either<Integer, ?>, EitherT<IO<?>, Integer, ?>>mappingReaderT(transformingEither(pureIO()))
                .apply(readerT(s -> s.contains("n") ? left(s.indexOf('n')) : right(s)));

        assertThat(fn.<EitherT<IO<?>, Integer, String>>runReaderT("junit")
                     .<IO<Either<Integer, String>>>runEitherT(),
                   yieldsValue(isLeftOf(2)));
        assertThat(fn.<EitherT<IO<?>, Integer, String>>runReaderT("yes")
                     .<IO<Either<Integer, String>>>runEitherT(),
                   yieldsValue(isRightOf("yes")));
    }
}