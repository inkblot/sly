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
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.StateT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.pureMaybe;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Tail.tail;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Both.both;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.jnape.palatable.lambda.io.IO.pureIO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.movealong.sly.lang.nt.TransformingState.transformingState;
import static org.movealong.sly.matchers.lambda.JustMatcher.isJustOf;
import static org.movealong.sly.model.Stringy.stringify;
import static org.movealong.sly.model.Stringy.stringy;
import static testsupport.matchers.IOMatcher.yieldsValue;

class TransformingStateTest {
    @Test
    void transformingToIO() {
        TransformingState<IO<?>, String> sut = transformingState(pureIO());
        StateT<String, IO<?>, Integer> subject = sut.apply(state(both(String::length,
                                                                      s -> stringify(tail(stringy(s))))));
        assertThat(subject.runStateT("junit"), yieldsValue(equalTo(tuple(5, "unit"))));
        assertThat(subject.runStateT("unit"), yieldsValue(equalTo(tuple(4, "nit"))));
        assertThat(subject.runStateT("nit"), yieldsValue(equalTo(tuple(3, "it"))));
    }

    @Test
    void transformingToMaybe() {
        TransformingState<Maybe<?>, String> sut = transformingState(pureMaybe());
        StateT<String, Maybe<?>, Integer> subject = sut.apply(state(both(String::length,
                                                                         s -> stringify(tail(stringy(s))))));

        assertThat(subject.runStateT("junit"), isJustOf(tuple(5, "unit")));
        assertThat(subject.runStateT("unit"), isJustOf(tuple(4, "nit")));
        assertThat(subject.runStateT("nit"), isJustOf(tuple(3, "it")));
    }
}