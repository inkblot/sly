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
package org.movealong.sly.matchers.jdk;

import com.jnape.palatable.shoki.impl.StrictQueue;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
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
import static org.movealong.sly.matchers.jdk.IterableMatcher.iteratesItemsThat;

class IterableMatcherTest {

    @Test
    void sameLength() {
        assertTrue(iteratesItemsThat(equalTo(5)).matches(strictQueue(5)));
        assertFalse(iteratesItemsThat(equalTo(5)).matches(strictQueue(3)));
        assertTrue(iteratesItemsThat(equalTo(5), lessThan(5)).matches(strictQueue(5, 3)));
        assertFalse(iteratesItemsThat(equalTo(5), lessThan(5)).matches(strictQueue(5, 6)));
    }

    @Test
    void mismatchAndSurplus() {
        IterableMatcher<Integer> sut     = iteratesItemsThat(equalTo(5), greaterThan(5));
        StrictQueue<Integer>     subject = strictQueue(6, 7, 8);

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(containsString("mismatches"),
                         containsString("0: " + describeMismatch(equalTo(5), 6)),
                         containsString("surplus"),
                         containsString("2: <8>")));
    }

    @Test
    void mismatchesAndShortage() {
        IterableMatcher<Integer> sut     = iteratesItemsThat(equalTo(5), greaterThan(5));
        StrictQueue<Integer>     subject = strictQueue(6);

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(containsString("mismatches"),
                         containsString("0: " + describeMismatch(equalTo(5), 6)),
                         containsString("missing"),
                         containsString("1: " + descriptionOf(greaterThan(5)))));
    }

    @Test
    void tooShort() {
        IterableMatcher<Integer> sut     = iteratesItemsThat(equalTo(5), lessThan(5));
        StrictQueue<Integer>     subject = strictQueue(5);

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(not(containsString(subject.toString())),
                         containsString("missing elements"),
                         containsString("1: " + descriptionOf(lessThan(5)))));
    }

    @Test
    void tooLong() {
        IterableMatcher<Integer> sut     = iteratesItemsThat(lessThan(5));
        StrictQueue<Integer>     subject = strictQueue(3, 5, 11);

        assertFalse(sut.matches(subject));
        assertThat(describeMismatch(sut, subject),
                   allOf(not(containsString(subject.toString())),
                         containsString("surplus elements"),
                         containsString("1: <5>"),
                         containsString("2: <11>")));
    }

    @Test
    void description() {
        assertThat(descriptionOf(iteratesItemsThat(equalTo(5), lessThan(5))),
                   equalTo("an iterable that:\n" +
                               "\t0: " + descriptionOf(equalTo(5)) + "\n" +
                               "\t1: " + descriptionOf(lessThan(5))));
    }
}