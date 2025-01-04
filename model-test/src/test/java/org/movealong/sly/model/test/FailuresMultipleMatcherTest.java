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

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.movealong.sly.model.Failures;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.movealong.sly.hamcrest.DescribeMismatch.describeMismatch;
import static org.movealong.sly.matchers.jdk.IterableMatcher.iteratesItemsThat;
import static org.movealong.sly.model.Failures.exceptional;
import static org.movealong.sly.model.Failures.message;
import static org.movealong.sly.model.Label.label;
import static org.movealong.sly.model.test.FailuresMessageMatcher.isMessageOf;
import static org.movealong.sly.model.test.FailuresMultipleMatcher.isMultiple;
import static org.movealong.sly.model.test.FailuresMultipleMatcher.isMultipleThat;

class FailuresMultipleMatcherTest {

    @Test
    void positiveMatch() {
        Failures subject = message("first").add(message("second"));
        Matcher<Failures> matcher = isMultipleThat(iteratesItemsThat(isMessageOf("first"),
                                                                     isMessageOf("second")));
        assertTrue(matcher.matches(subject));
    }

    @Test
    void typeMismatch() {
        Matcher<Failures> matcher = isMultiple();

        Failures message = message("a bad happened");
        assertThat(describeMismatch(matcher, message),
                   allOf(containsString("a message failure"),
                         containsString(message.toString())));

        Failures exceptional = exceptional(new RuntimeException("boom"));
        assertThat(describeMismatch(matcher, exceptional),
                   allOf(containsString("an exceptional failure"),
                         containsString(exceptional.toString())));

        Failures ascribed = message("a bad happened").ascribe(label("yesterday"));
        assertThat(describeMismatch(matcher, ascribed),
                   allOf(containsString("an ascribed failure"),
                         containsString(ascribed.toString())));
    }

    @Test
    void failuresMismatch() {
        Failures alpha   = message("alpha");
        Failures beta    = message("beta");
        Failures subject = alpha.add(beta);
        Matcher<Failures> matcher = isMultipleThat(iteratesItemsThat(isMessageOf("first"),
                                                                     isMessageOf("second")));

        assertThat(describeMismatch(matcher, subject),
                   allOf(containsString("a multiple failure"),
                         containsString("a message failure"),
                         containsString("alpha"),
                         containsString("beta")));
    }
}