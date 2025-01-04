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
package org.movealong.sly.model.test;

import org.junit.jupiter.api.Test;
import org.movealong.sly.model.Failures;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescribeMismatch.describeMismatch;
import static org.movealong.sly.model.Failures.message;
import static org.movealong.sly.model.Label.label;
import static org.movealong.sly.model.test.FailuresAscribedMatcher.isAscribed;
import static org.movealong.sly.model.test.FailuresMessageMatcher.isMessageOf;

class FailuresAscribedMatcherTest {
    @Test
    void positiveMatch() {
        Failures subject = message("a bad happened").ascribe(label("yesterday"));
        assertTrue(isAscribed().matches(subject));
        assertTrue(isAscribed().withAscriptionOf(label("yesterday")).matches(subject));
        assertTrue(isAscribed().withAscribedOf(message("a bad happened")).matches(subject));
    }

    @Test
    void typeMismatch() {
        FailuresAscribedMatcher matcher = isAscribed();

        Failures message = message("a bad happened");
        assertThat(describeMismatch(matcher, message),
                   allOf(containsString("a message failure"),
                         containsString(message.toString())));

        Failures exceptional = Failures.exceptional(new RuntimeException("boom"));
        assertThat(describeMismatch(matcher, exceptional),
                   allOf(containsString("an exceptional failure"),
                         containsString(exceptional.toString())));

        Failures multiple = message("a bad happened").add(message("twice"));
        assertThat(describeMismatch(matcher, multiple),
                   allOf(containsString("a multiple failure"),
                         containsString(multiple.toString())));
    }

    @Test
    void ascriptionMismatch() {
        Failures                subject = message("a bad happened").ascribe(label("yesterday"));
        FailuresAscribedMatcher matcher = isAscribed().withAscriptionOf(label("today"));
        assertFalse(matcher.matches(subject));
        assertThat(describeMismatch(matcher, subject),
                   allOf(containsString("an ascribed failure"),
                         containsString("today"),
                         containsString("yesterday")));
    }

    @Test
    void ascribedMismatch() {
        Failures                subject = message("a bad happened").ascribe(label("yesterday"));
        FailuresAscribedMatcher matcher = isAscribed().withAscribedThat(isMessageOf("terrible things"));
        assertFalse(matcher.matches(subject));
        assertThat(describeMismatch(matcher, subject),
                   allOf(containsString("an ascribed failure"),
                         containsString("a bad happened"),
                         containsString("terrible things")));
    }
}