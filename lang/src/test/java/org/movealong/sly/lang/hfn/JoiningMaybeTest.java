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

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT.maybeT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static testsupport.matchers.IOMatcher.yieldsValue;

class JoiningMaybeTest {
    @Test
    void joiningMaybeTJustJust() {
        assertThat(JoiningMaybe
                       .<IO<?>, String>joiningMaybe()
                       .<MaybeT<IO<?>, String>>apply(maybeT(io(just(just("junit")))))
                       .runMaybeT(),
                   yieldsValue(isJustOf("junit")));
    }

    @Test
    void joiningMaybeTJustNothing() {
        assertThat(JoiningMaybe
                       .<IO<?>, String>joiningMaybe()
                       .<MaybeT<IO<?>, String>>apply(maybeT(io(just(nothing()))))
                       .<IO<Maybe<String>>>runMaybeT(),
                   yieldsValue(equalTo(nothing())));
    }

    @Test
    void joiningMaybeTNothing() {
        assertThat(JoiningMaybe
                       .<IO<?>, String>joiningMaybe()
                       .<MaybeT<IO<?>, String>>apply(maybeT(io(nothing())))
                       .<IO<Maybe<String>>>runMaybeT(),
                   yieldsValue(equalTo(nothing())));
    }
}