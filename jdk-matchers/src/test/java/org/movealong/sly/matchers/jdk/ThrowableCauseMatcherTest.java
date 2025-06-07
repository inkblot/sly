/*
 * Copyright (c) 2025 Nate Riffe
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

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescribeMismatch.describeMismatch;
import static org.movealong.sly.hamcrest.DescriptionOf.descriptionOf;
import static org.movealong.sly.matchers.jdk.ThrowableCauseMatcher.hasCauseThat;
import static org.movealong.sly.matchers.jdk.ThrowableMessageMatcher.hasMessageOf;

class ThrowableCauseMatcherTest {

    @Test
    void matches() {
        Matcher<Throwable> matcher = hasCauseThat(hasMessageOf("bad happens"));
        RuntimeException   subject = new RuntimeException("outer", new RuntimeException("bad happens"));
        assertTrue(matcher.matches(subject));
    }

    @Test
    void describesWell() {
        assertThat(descriptionOf(hasCauseThat(hasMessageOf("bad happens"))),
                   equalTo("exception with cause exception with message \"bad happens\""));
    }

    @Test
    void describesMismatchWell() {
        Matcher<Throwable> matcher = hasCauseThat(hasMessageOf("bad happens"));
        RuntimeException   subject = new RuntimeException("outer", new RuntimeException("nothing good"));
        assertFalse(matcher.matches(subject));
        assertThat(describeMismatch(matcher, subject),
                   equalTo("cause message was \"nothing good\""));
    }
}