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
package org.movealong.sly.matchers.lambda;

import com.jnape.palatable.lambda.functor.builtin.Identity;
import com.jnape.palatable.winterbourne.StreamT;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.winterbourne.StreamT.streamT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescribeMismatch.describeMismatch;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.matchers.lambda.StreamTMatcher.streamsItemsThat;

class StreamTMatcherTest {

    @Test
    void sameLength() {
        assertTrue(streamsItemsThat(equalTo(5)).matches(streamT(new Identity<>(just(5)))));
        assertFalse(streamsItemsThat(equalTo(5)).matches(streamT(new Identity<>(just(3)))));
        assertTrue(streamsItemsThat(equalTo(5), lessThan(5)).matches(streamT(new Identity<>(just(5)),
                                                                             new Identity<>(just(3)))));
        assertFalse(streamsItemsThat(equalTo(5), lessThan(5)).matches(streamT(new Identity<>(just(5)),
                                                                              new Identity<>(just(6)))));
    }

    @Test
    void mismatchAndSurplus() {
        StreamTMatcher<Integer> sut = streamsItemsThat(equalTo(5), greaterThan(5));
        StreamT<Identity<?>, Integer> subject = streamT(new Identity<>(just(6)),
                                                        new Identity<>(just(7)),
                                                        new Identity<>(just(8)));

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(containsString("mismatches"),
                         containsString("0: " + describeMismatch(equalTo(5), 6)),
                         containsString("surplus"),
                         containsString("2: <8>")));
    }

    @Test
    void mismatchesAndShortage() {
        StreamTMatcher<Integer>       sut     = streamsItemsThat(equalTo(5), greaterThan(5));
        StreamT<Identity<?>, Integer> subject = streamT(new Identity<>(just(6)));

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(containsString("mismatches"),
                         containsString("0: " + describeMismatch(equalTo(5), 6)),
                         containsString("missing"),
                         containsString("1: " + descriptionOf(greaterThan(5)))));
    }

    @Test
    void tooShort() {
        StreamTMatcher<Integer>       sut     = streamsItemsThat(equalTo(5), lessThan(5));
        StreamT<Identity<?>, Integer> subject = streamT(new Identity<>(just(5)));

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(not(containsString(subject.toString())),
                         containsString("missing elements"),
                         containsString("1: " + descriptionOf(lessThan(5)))));
    }

    @Test
    void tooLong() {
        StreamTMatcher<Integer> sut = streamsItemsThat(lessThan(5));
        StreamT<Identity<?>, Integer> subject = streamT(new Identity<>(just(3)),
                                                        new Identity<>(just(5)),
                                                        new Identity<>(just(11)));

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(not(containsString(subject.toString())),
                         containsString("surplus elements"),
                         containsString("1: <5>"),
                         containsString("2: <11>")));
    }

    @Test
    void description() {
        assertThat(descriptionOf(streamsItemsThat(equalTo(5), lessThan(5))),
                   equalTo("a stream that:\n" +
                               "\t0: " + descriptionOf(equalTo(5)) + "\n" +
                               "\t1: " + descriptionOf(lessThan(5))));
    }
}