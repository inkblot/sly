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

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Try.pureTry;
import static com.jnape.palatable.lambda.io.IO.pureIO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.nt.TransformingMaybe.transformingMaybe;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static org.movealong.sly.matchers.lambda.TryMatcher.successfulTryOf;
import static org.movealong.sly.matchers.lambda.TryMatcher.successfulTryThat;
import static testsupport.matchers.IOMatcher.yieldsValue;

class TransformingMaybeTest {
    @Test
    void transformsJust() {
        assertThat(transformingMaybe(pureIO())
                       .<Integer, MaybeT<IO<?>, Integer>>apply(just(5))
                       .runMaybeT(),
                   yieldsValue(isJustOf(5)));
        assertThat(transformingMaybe(pureTry())
                       .<Integer, MaybeT<Try<?>, Integer>>apply(just(5))
                       .runMaybeT(),
                   successfulTryThat(isJustOf(5)));
    }

    @Test
    void transformsNothing() {
        assertThat(transformingMaybe(pureIO())
                       .<Integer, MaybeT<IO<?>, Integer>>apply(nothing())
                       .runMaybeT(),
                   yieldsValue(equalTo(Maybe.<Integer>nothing())));
        assertThat(transformingMaybe(pureTry())
                       .<Integer, MaybeT<Try<?>, Integer>>apply(nothing())
                       .runMaybeT(),
                   successfulTryOf(Maybe.<Integer>nothing()));
    }
}