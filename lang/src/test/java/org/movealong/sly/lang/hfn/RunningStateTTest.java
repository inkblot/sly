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
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Empty.empty;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Not.not;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Size.size;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Tail.tail;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Both.both;
import static com.jnape.palatable.lambda.monad.transformer.builtin.StateT.stateT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustThat;
import static org.movealong.sly.model.Stringy.stringify;
import static org.movealong.sly.model.Stringy.stringy;
import static org.movealong.sly.test.lambda.Tuple2Matcher.isTuple2That;

class RunningStateTTest {
    @Test
    void runningStateTMaybeJust() {
        assertThat(RunningStateT
                       .<Maybe<?>, String, Long>runningStateT("junit")
                       .apply(stateT(s -> just(stringy(s))
                           .filter(not(empty()))
                           .fmap(both(size(), sy -> stringify(tail(sy)))))),
                   isJustThat(isTuple2That(equalTo(5L), equalTo("unit"))));
    }

    @Test
    void runningStateTMaybeNothing() {
        assertThat(RunningStateT
                       .<Maybe<?>, String, Long>runningStateT("")
                       .<Maybe<Tuple2<Long, String>>>apply(stateT(s -> just(stringy(s))
                           .filter(not(empty()))
                           .fmap(both(size(), sy -> stringify(tail(sy)))))),
                   equalTo(nothing()));
    }
}