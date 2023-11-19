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
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.IterateT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.io.IO.pureIO;
import static com.jnape.palatable.lambda.monad.transformer.builtin.IterateT.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.hfn.RunningMaybeT.runningMaybeT;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustThat;
import static org.movealong.sly.test.lambda.Tuple2Matcher.isTuple2That;
import static testsupport.matchers.IOMatcher.yieldsValue;
import static testsupport.matchers.IterateTMatcher.iterates;

class RunningIterateTTest {
    @Test
    void runningIterateTEmpty() {
        assertThat(RunningIterateT
                       .<IO<?>, Integer>runningIterateT()
                       .andThen(runningMaybeT())
                       .<IO<Maybe<Tuple2<Integer, IterateT<IO<?>, Integer>>>>>apply(empty(pureIO())),
                   yieldsValue(equalTo(nothing())));
    }

    @Test
    void runningIterateTNonEmpty() {
        IterateT<Identity<?>, Integer> input =
            IterateT.of(new Identity<>(1),
                        new Identity<>(1),
                        new Identity<>(2),
                        new Identity<>(3),
                        new Identity<>(5));
        assertThat(RunningIterateT
                       .<Identity<?>, Integer>runningIterateT()
                       .andThen(runningMaybeT())
                       .<Identity<Maybe<Tuple2<Integer, IterateT<Identity<?>, Integer>>>>>apply(input)
                       .runIdentity(),
                   isJustThat(isTuple2That(
                       equalTo(1),
                       iterates(1, 2, 3, 5))));
    }
}