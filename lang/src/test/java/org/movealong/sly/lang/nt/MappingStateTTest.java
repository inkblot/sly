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
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.MaybeT;
import com.jnape.palatable.lambda.monad.transformer.builtin.StateT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Empty.empty;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Not.not;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Size.size;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Tail.tail;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Both.both;
import static com.jnape.palatable.lambda.io.IO.pureIO;
import static com.jnape.palatable.lambda.monad.transformer.builtin.StateT.stateT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.nt.TransformingMaybe.transformingMaybe;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static org.movealong.sly.model.Stringy.stringify;
import static org.movealong.sly.model.Stringy.stringy;
import static testsupport.matchers.IOMatcher.yieldsValue;

class MappingStateTTest {
    @Test
    void mappingStateTMaybe() {
        StateT<String, Maybe<?>, Long> input = stateT(s -> just(stringy(s))
            .filter(not(empty()))
            .fmap(both(size(), sy -> stringify(tail(sy)))));

        StateT<String, MaybeT<IO<?>, ?>, Long> sut = MappingStateT
            .<String, Maybe<?>, MaybeT<IO<?>, ?>>mappingStateT(transformingMaybe(pureIO()))
            .apply(input);

        assertThat(sut
                       .<MaybeT<IO<?>, Tuple2<Long, String>>>runStateT("junit")
                       .runMaybeT(),
                   yieldsValue(isJustOf(tuple(5L, "unit"))));
        assertThat(sut
                       .<MaybeT<IO<?>, Tuple2<Long, String>>>runStateT("")
                       .<IO<Maybe<Tuple2<Long, String>>>>runMaybeT(),
                   yieldsValue(equalTo(nothing())));
    }
}