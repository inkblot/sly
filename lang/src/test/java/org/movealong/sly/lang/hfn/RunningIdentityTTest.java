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
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.jnape.palatable.lambda.monad.transformer.builtin.IdentityT.identityT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.hfn.RunningIdentityT.runningIdentityT;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static testsupport.matchers.IOMatcher.yieldsValue;

class RunningIdentityTTest {
    @Test
    void runningWithIO() {
        HyperFn<IdentityT<IO<?>, ?>, String, IO<?>, Identity<String>> sut = runningIdentityT();
        assertThat(sut.apply(identityT(io(new Identity<>("junit")))),
                   yieldsValue(equalTo(new Identity<>("junit"))));
    }

    @Test
    void runningWithPresentMaybe() {
        HyperFn<IdentityT<Maybe<?>, ?>, String, Maybe<?>, Identity<String>> sut = runningIdentityT();
        assertThat(sut.apply(identityT(just(new Identity<>("junit")))),
                   isJustOf(new Identity<>("junit")));
    }

    @Test
    void runningWithAbsentMaybe() {
        HyperFn<IdentityT<Maybe<?>, ?>, String, Maybe<?>, Identity<String>> sut = runningIdentityT();
        assertThat(sut.apply(identityT(nothing())),
                   equalTo(Maybe.<Identity<String>>nothing()));
    }
}