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
package org.movealong.sly.test.lambda;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescribeMismatch.describeMismatch;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.test.lambda.Tuple2Matcher.isTuple2That;

class Tuple2MatcherTest {
    @Test
    void goodMatch() {
        Tuple2Matcher<String, Integer> sut = isTuple2That(equalTo("first"), equalTo(1));

        assertThat(descriptionOf(sut), equalTo("Tuple of \"first\" and <1>"));
        assertTrue(sut.matches(tuple("first", 1)));
    }

    @Test
    void leadingMismatch() {
        Tuple2<String, Integer>        item = tuple("first", 1);
        Tuple2Matcher<String, Integer> sut  = isTuple2That(equalTo("second"), equalTo(1));

        assertFalse(sut.matches(item));
        assertThat(describeMismatch(sut, item),
                   equalTo("tuple mismatch: _1: expected \"second\" but was \"first\""));
    }

    @Test
    void trailingMismatch() {
        Tuple2<String, Integer>        item = tuple("first", 1);
        Tuple2Matcher<String, Integer> sut  = isTuple2That(equalTo("first"), equalTo(2));

        assertFalse(sut.matches(item));
        assertThat(describeMismatch(sut, item),
                   equalTo("tuple mismatch: _2: expected <2> but was <1>"));
    }

    @Test
    void fullMismatch() {
        Tuple2<String, Integer>        item = tuple("first", 1);
        Tuple2Matcher<String, Integer> sut  = isTuple2That(equalTo("second"), equalTo(2));

        assertFalse(sut.matches(item));
        assertThat(describeMismatch(sut, item),
                   equalTo("tuple mismatch: _1: expected \"second\" but was \"first\" and _2: expected <2> but was <1>"));
    }
}